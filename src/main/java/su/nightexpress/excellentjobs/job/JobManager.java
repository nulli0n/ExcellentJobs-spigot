package su.nightexpress.excellentjobs.job;

import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.event.*;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.excellentjobs.grind.type.GrindTypeId;
import su.nightexpress.excellentjobs.job.impl.*;
import su.nightexpress.excellentjobs.job.legacy.LegacyJobObjective;
import su.nightexpress.excellentjobs.job.legacy.LegacyObjectiveReward;
import su.nightexpress.excellentjobs.job.listener.JobGenericListener;
import su.nightexpress.excellentjobs.job.listener.JobWorkstationListener;
import su.nightexpress.excellentjobs.job.menu.LevelsMenu;
import su.nightexpress.excellentjobs.job.menu.JobsMenu;
import su.nightexpress.excellentjobs.job.reward.JobRewards;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
import su.nightexpress.excellentjobs.job.workstation.WorkstationMode;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.nightcore.util.wrapper.UniDouble;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobManager extends AbstractManager<JobsPlugin> {

    private final Map<String, Job>                    jobMap;
    private final Map<UUID, Map<String, ProgressBar>> progressBarMap;

    private final NamespacedKey stationOwnerKey;
    private final NamespacedKey stationModeKey;

    private JobsMenu   jobsMenu;
    private LevelsMenu levelsMenu;

    public JobManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.jobMap = new HashMap<>();
        this.progressBarMap = new ConcurrentHashMap<>();

        this.stationOwnerKey = new NamespacedKey(plugin, "workstation.owner_id");
        this.stationModeKey = new NamespacedKey(plugin, "workstation.craft_mode");
    }

    public static boolean canWorkHere(@NotNull Player player) {
        if (Config.GENERAL_DISABLED_WORLDS.get().contains(player.getWorld().getName())) return false;
        if (!checkVehicle(player)) return false;

        return !Config.ABUSE_IGNORE_GAME_MODES.get().contains(player.getGameMode());
    }

    public static boolean checkVehicle(@NotNull Player player) {
        if (!Config.ABUSE_IGNORE_VEHICLES.get()) return true;

        Entity vehicle = player.getVehicle();
        return vehicle == null || vehicle instanceof LivingEntity;
    }

    public static boolean isAbusingPetKilling(@NotNull Player player, @NotNull LivingEntity killedMob) {
        if (!Config.ABUSE_RESTRICT_PET_KILLS.get()) return false;

        EntityDamageEvent damageEvent = killedMob.getLastDamageCause();
        if (damageEvent == null) return false;

        DamageSource source = damageEvent.getDamageSource();
        Entity damager = source.getCausingEntity();
        if (damager == null || damager == player) return false;

        return damager instanceof Tameable tameable && tameable.getOwner() == player;
    }

    @Override
    protected void onLoad() {
        this.loadJobs();
        this.loadUI();

        this.addListener(new JobGenericListener(this.plugin, this));
        this.addListener(new JobWorkstationListener(this.plugin, this));

        // Use 1 second interval for "instant" payments to avoid currency plugin's API usage spam + keep it async.
        int paymentInterval = Config.isInstantPayment() ? 1 : Config.GENERAL_PAYMENT_INTERVAL.get();
        this.addAsyncTask(this::payForJob, paymentInterval);

        if (Config.GENERAL_PROGRESS_BAR_ENABLED.get()) {
            this.addAsyncTask(this::tickProgressBars, 1);
        }
    }

    @Override
    protected void onShutdown() {
        this.payForJob();
        this.progressBarMap.values().forEach(map -> map.values().forEach(ProgressBar::discard));

        if (this.levelsMenu != null) this.levelsMenu.clear();
        if (this.jobsMenu != null) this.jobsMenu.clear();

        this.jobMap.clear();
        this.progressBarMap.clear();
    }

    private void loadJobs() {
        File dir = new File(this.plugin.getDataFolder() + Config.DIR_JOBS);
        if (!dir.exists() && dir.mkdirs()) {
            JobDefaults.createDefaultJobs(this.plugin).forEach(job -> {
                File file = new File(this.getJobsPath(), FileConfig.withExtension(job.getId()));
                FileConfig config = new FileConfig(file);
                config.set("", job);
                config.saveChanges();
            });
        }

        // ----------- UPDATE JOB CONFIGS - START -----------
        List<File> jobFolders = FileUtil.getFolders(this.getJobsPath());
        jobFolders.forEach(jobDir -> {
            File oldFile = new File(jobDir.getAbsolutePath(), "settings.yml");
            if (!oldFile.exists()) return;

            File oldObjectsFile = new File(jobDir.getAbsolutePath(), "objectives.yml");
            if (!oldObjectsFile.exists()) return;

            FileConfig jobConfig = new FileConfig(oldFile);
            FileConfig objectivesConfig = new FileConfig(oldObjectsFile);
            this.updateObjectives(objectivesConfig).forEach((objectiveId, objective) -> {
                jobConfig.set("Objectives." + objectiveId, objective);
            });
            jobConfig.save();

            try {
                File jobFile = new File(this.plugin.getDataFolder() + Config.DIR_JOBS, FileConfig.withExtension(jobDir.getName()));
                File objectBackup = new File(this.plugin.getDataFolder() + Config.DIR_JOBS, jobDir.getName() + "_objectives.yml.backup");
                Files.copy(oldFile.toPath(), jobFile.toPath());
                Files.move(oldObjectsFile.toPath(), objectBackup.toPath());
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        // ----------- UPDATE JOB CONFIGS - END -----------

        for (File file : FileUtil.getConfigFiles(this.getJobsPath())) {
            String name = FileConfig.getName(file);
            this.loadJob(file, Strings.filterForVariable(name));
        }

        this.plugin.info("Loaded " + this.jobMap.size() + " jobs.");
    }

    public void loadJob(@NotNull File file, @NotNull String id) {
        FileConfig config = new FileConfig(file);
        Job job = new Job(this.plugin, id);
        job.loadSettings(config);
        config.saveChanges();

        this.jobMap.put(job.getId(), job);
    }

    private void loadUI() {
        this.jobsMenu = new JobsMenu(this.plugin);
        this.levelsMenu = this.addMenu(new LevelsMenu(this.plugin), Config.DIR_MENU, "job_levels.yml");
    }

    @NotNull
    private Map<String, JobObjective> updateObjectives(@NotNull FileConfig config) {
        Map<String, JobObjective> objectives = new LinkedHashMap<>();

        Map<String, Map<String, SourceReward>> converteds = new LinkedHashMap<>();

        config.getSection("").forEach(sId -> {
            LegacyJobObjective legacyObjective = LegacyJobObjective.read(config, sId, sId);
            String legacyType = legacyObjective.getWorkId();
            String type = GrindTypeId.fromLegacy(legacyType);

            Map<String, LegacyObjectiveReward> payment = legacyObjective.getPaymentMap();
            LegacyObjectiveReward xpLegacy = legacyObjective.getXPReward();
            Set<String> objects = legacyObjective.getItems();

            GrindType<?> grindType = type == null ? null : this.plugin.getGrindRegistry().getTypeById(type);
            if (grindType == null) {
                this.plugin.error("Could not convert objective '" + legacyObjective.getId() + "' with type '" + legacyType + "': No adapter found.");
                return;
            }

            payment.forEach((currencyId, moneyLegacy) -> {
                String newId = type + "~" + currencyId;

                Map<String, SourceReward> convertedEntries = converteds.computeIfAbsent(newId, k -> new LinkedHashMap<>());

                objects.forEach(entryName -> {
                    UniDouble xpConverted = UniDouble.of(xpLegacy.min(), xpLegacy.max());
                    UniDouble moneyConverted = UniDouble.of(moneyLegacy.min(), moneyLegacy.max());
                    SourceReward convertedReward = new SourceReward(xpConverted, moneyConverted, moneyLegacy.chance());

                    convertedEntries.put(entryName, convertedReward);
                });
            });
        });

        converteds.forEach((id, entryMap) -> {
            String[] split = id.split("~");
            String type = split[0];
            String currencyId  = split[1];

            GrindType<?> grindType = this.plugin.getGrindRegistry().getTypeById(type);
            if (grindType == null) return;

            GrindTable grindTable = grindType.convertTable(entryMap);

            JobObjective objective = new JobObjective(currencyId, type, grindTable);
            objectives.put(type + "_" + currencyId, objective);
        });

        return objectives;
    }

    @NotNull
    public String getJobsPath() {
        return this.plugin.getDataFolder() + Config.DIR_JOBS;
    }

    @NotNull
    public Map<String, Job> getJobMap() {
        return jobMap;
    }

    @NotNull
    public Set<Job> getJobs() {
        return new HashSet<>(this.jobMap.values());
    }

    @NotNull
    public List<String> getJobIds() {
        return new ArrayList<>(this.jobMap.keySet());
    }

    @Nullable
    public Job getJobById(@NotNull String id) {
        return this.jobMap.get(id.toLowerCase());
    }

    @NotNull
    public Set<Job> getJobs(@NotNull Player player) {
        return this.getJobs(player, null);
    }

    @NotNull
    public Set<Job> getJobs(@NotNull Player player, @Nullable JobState state) {
        JobUser user = plugin.getUserManager().getOrFetch(player);

        return this.getJobs().stream()
            .filter(job -> state == null || user.getData(job).getState() == state)
            .collect(Collectors.toSet());
    }

    @NotNull
    public Set<Job> getActiveJobs(@NotNull Player player) {
        JobUser user = plugin.getUserManager().getOrFetch(player);

        return this.getJobs().stream()
            .filter(job -> user.getData(job).getState() != JobState.INACTIVE)
            .collect(Collectors.toSet());
    }

    @NotNull
    public JobIncome getIncome(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        return user.getData(job).getIncome();
    }

    @NotNull
    public Set<JobIncome> getIncomes(@NotNull Player player) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        return user.getDatas().stream().map(JobData::getIncome).collect(Collectors.toSet());
    }

    @NotNull
    private Map<String, ProgressBar> getProgressBarMap(@NotNull Player player) {
        return this.progressBarMap.getOrDefault(player.getUniqueId(), Collections.emptyMap());
    }

    @Nullable
    private ProgressBar getProgressBar(@NotNull Player player, @NotNull Job job) {
        return this.getProgressBarMap(player).get(job.getId());
    }

    @Nullable
    public ProgressBar getProgressBarOrCreate(@NotNull Player player, @NotNull Job job) {
        if (!Config.GENERAL_PROGRESS_BAR_ENABLED.get()) return null;

        ProgressBar progressBar = this.getProgressBar(player, job);
        if (progressBar == null) {
            progressBar = new ProgressBar(this.plugin, job, player);
            this.progressBarMap.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(job.getId(), progressBar);
        }
        return progressBar;
    }

    @NotNull
    public Collection<ProgressBar> getProgressBars(@NotNull Player player) {
        return this.getProgressBarMap(player).values();
    }

    public static int getJobsLimit(@NotNull Player player, @NotNull JobState state) {
        if (state == JobState.PRIMARY) return getPrimaryJobsLimit(player);
        if (state == JobState.SECONDARY) return getSeconaryJobsLimit(player);

        return -1;
    }

    public static int getPrimaryJobsLimit(@NotNull Player player) {
        return Config.JOBS_PRIMARY_AMOUNT.get().getGreatestOrNegative(player);
    }

    public static int getSeconaryJobsLimit(@NotNull Player player) {
        return Config.JOBS_SECONDARY_AMOUNT.get().getGreatestOrNegative(player);
    }

    public void openJobsMenu(@NotNull Player player) {
        this.jobsMenu.open(player);
    }

    public void openLevelsMenu(@NotNull Player player, @NotNull Job job) {
        this.levelsMenu.open(player, job);
    }

    public boolean canGetMoreJobs(@NotNull Player player) {
        return Stream.of(JobState.actives()).anyMatch(state -> this.canGetMoreJobs(player, state));
    }

    public boolean canGetMoreJobs(@NotNull Player player, @NotNull JobState state) {
        return this.countAvailableJobs(player, state) != 0;
    }

    public int countAvailableJobs(@NotNull Player player, @NotNull JobState state) {
        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        int limit = getJobsLimit(player, state);
        if (limit < 0) return -1;

        return Math.max(0, limit - user.countJobs(state));
    }

    public boolean isJobLimitExceed(@NotNull Player player, @NotNull JobState state) {
        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        int limit = getJobsLimit(player, state);

        return limit >= 0 && user.countJobs(state) > limit;
    }

    public void handleQuit(@NotNull Player player) {
        this.payForJob(player);
        this.getProgressBars(player).forEach(ProgressBar::discard);
        this.progressBarMap.remove(player.getUniqueId());
    }

    public void handleJoin(@NotNull Player player) {
        this.validateJobs(player);
    }

    public void validateJobs(@NotNull Player player) {
        boolean leaveOnPermissionLost = Config.JOBS_FORCE_LEAVE_WHEN_LOST_PERMISSION.get();

        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        this.getJobs().forEach(job -> {
            JobData data = user.getData(job);
            if (!data.isActive()) return;

            if (leaveOnPermissionLost && (!job.hasPermission(player) || this.isJobLimitExceed(player, data.getState()))) {
                this.onLeaveJob(player, job, data);
                return;
            }

            this.giveRewardsOrNotify(player, job);
        });
        this.plugin.getUserManager().save(user);
    }

    public boolean leaveJob(@NotNull Player player, @NotNull Job job) {
        return this.leaveJob(player, job, false);
    }

    public boolean leaveJob(@NotNull Player player, @NotNull Job job, boolean silent) {
        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        if (!data.isActive()) {
            Lang.JOB_LEAVE_ERROR_NOT_JOINED.message().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        if (data.isOnCooldown()) {
            Lang.JOB_LEAVE_ERROR_COOLDOWN.message().send(player, replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(data.getCooldown(), TimeFormatType.LITERAL)));
            return false;
        }

        if (!job.isLeaveable()) {
            Lang.JOB_LEAVE_ERROR_NOT_ALLOWED.message().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        JobLeaveEvent event = new JobLeaveEvent(player, job, data.getState());
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        this.onLeaveJob(player, job, data);
        this.plugin.getUserManager().save(user);

        Lang.JOB_LEAVE_SUCCESS.message().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
        return true;
    }

    private void onLeaveJob(@NotNull Player player, @NotNull Job job, @NotNull JobData data) {
        this.payForJob(player, job); // Pay what player earned.

        job.removeEmployee(data.getState(), 1);
        job.runLeaveCommands(player);
        data.setState(JobState.INACTIVE);
        if (Config.JOBS_COOLDOWN_ON_LEAVE.get()) {
            data.setCooldown(JobUtils.getJobCooldownTimestamp(player));
        }
        if (Config.JOBS_LEAVE_RESET_PROGRESS.get()) {
            data.reset();
        }
    }

    public boolean joinJob(@NotNull Player player, @NotNull Job job) {
        if (!job.isJoinable()) {
            Lang.JOB_JOIN_NOT_JOINABLE.message().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        for (JobState state : JobState.actives()) {
            if (job.isAllowedState(state) && this.canGetMoreJobs(player, state)) {
                return this.joinOrLeaveJob(player, job, state, false);
            }
        }

        Lang.JOB_JOIN_ERROR_LIMIT_GENERAL.message().send(player);
        return false;
    }

    public boolean joinOrLeaveJob(@NotNull Player player, @NotNull Job job, @NotNull JobState state, boolean forced) {
        if (state == JobState.INACTIVE) {
            this.leaveJob(player, job);
            return false;
        }

        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        if (data.getState() == state) {
            Lang.JOB_JOIN_ERROR_ALREADY_HIRED.message().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        if (!forced) {
            if (!job.hasPermission(player)) {
                CoreLang.ERROR_NO_PERMISSION.message().send(player);
                return false;
            }

            if (data.isOnCooldown()) {
                Lang.JOB_JOIN_ERROR_COOLDOWN.message().send(player, replacer -> replacer
                    .replace(job.replacePlaceholders())
                    .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(data.getCooldown(), TimeFormatType.LITERAL)));
                return false;
            }

            if (!this.canGetMoreJobs(player, state)) {
                Lang.JOB_JOIN_ERROR_LIMIT_STATE.message().send(player, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(getJobsLimit(player, state)))
                    .replace(Placeholders.GENERIC_STATE, Lang.JOB_STATE.getLocalized(state))
                    .replace(job.replacePlaceholders()));
                return false;
            }
        }

        JobJoinEvent event = new JobJoinEvent(player, job, state);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        boolean join = data.getState() == JobState.INACTIVE;

        job.removeEmployee(data.getState(), 1);
        data.setState(event.getState());
        if (Config.JOBS_COOLDOWN_ON_JOIN.get()) {
            data.setCooldown(JobUtils.getJobCooldownTimestamp(player));
        }
        job.addEmployee(event.getState(), 1);

        if (join) job.runJoinCommands(player);

        this.giveRewardsOrNotify(player, job);
        this.plugin.getUserManager().save(user);

        (join ? Lang.JOB_JOIN_SUCCESS : Lang.JOB_PRIORITY_CHANGED).message().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));
        return true;
    }

    public void resetJobProgress(@NotNull Player player, @NotNull Job job) {
        this.resetJobProgress(player, job, false);
    }

    public void resetJobProgress(@NotNull Player player, @NotNull Job job, boolean full) {
        JobUser user = plugin.getUserManager().getOrFetch(player);

        this.handleJobReset(user, job, player, full, false);
    }

    public void handleJobReset(@NotNull JobUser user, @NotNull Job job, @Nullable Player player, boolean full, boolean silent) {
        if (player != null) {
            this.payForJob(player, job);
        }

        JobData data = user.getData(job);
        data.reset(full);
        plugin.getUserManager().save(user);

        if (player != null) {
            if (!silent) Lang.JOB_RESET_NOTIFY.message().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));
        }
    }

    public void tickProgressBars() {
        this.progressBarMap.values().forEach(map -> map.values().removeIf(ProgressBar::checkExpired));
        this.progressBarMap.values().removeIf(Map::isEmpty);
    }

    public void payForJob() {
        this.plugin.getServer().getOnlinePlayers().forEach(this::payForJob);
    }

    /**
     * Get player paid for all their active jobs.
     * @param player Player to paid.
     */
    public void payForJob(@NotNull Player player) {
        this.getJobs().forEach(job -> this.payForJob(player, job));
    }

    public boolean payForJob(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        JobIncome income = data.getIncome();
        if (income.isEmpty()) return false;

        JobPaymentEvent event = new JobPaymentEvent(player, job, income);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        // Send message only for scheduled payments.
        if (!Config.isInstantPayment()) {
            Lang.JOB_PAYMENT_NOTIFY.message().send(player, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, JobUtils.formatIncome(income))
            );
        }

        income.payAndClear(player);

        return true;
    }

    public void addLevel(@NotNull Player player, @NotNull Job job, int amount) {
        this.addLevel(player, job, amount, false);
    }

    public void addLevel(@NotNull Player player, @NotNull Job job, int amount, boolean silent) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        this.handleLevelAdd(user, job, amount, player, silent);
    }

    public void handleLevelAdd(@NotNull JobUser user, @NotNull Job job, int amount, @Nullable Player player, boolean silent) {
        JobData data = user.getData(job);
        if (!data.isMaxLevel()) {
            this.handleLevelSet(user, job, data.getLevel() + amount, player, silent);
        }
    }

    public void handleLevelRemove(@NotNull JobUser user, @NotNull Job job, int amount, @Nullable Player player, boolean silent) {
        JobData data = user.getData(job);
        if (!data.isStartLevel()) {
            this.handleLevelSet(user, job, data.getLevel() - amount, player, silent);
        }
    }

    public void handleLevelSet(@NotNull JobUser user, @NotNull Job job, int amount, @Nullable Player player, boolean silent) {
        JobData data = user.getData(job);
        int oldLevel = data.getLevel();

        data.setLevel(amount);
        data.setXP(0);
        data.update();
        plugin.getUserManager().save(user);

        if (player != null) {
            if (data.getLevel() > oldLevel) {
                this.handleLevelUp(player, job, oldLevel, silent);
                this.giveRewardsOrNotify(player, job);
            }
            else if (data.getLevel() < oldLevel) {
                this.handleLevelDown(player, job, oldLevel, silent);
            }
        }
    }

    private void handleLevelUp(@NotNull Player player, @NotNull Job job, int oldLevel, boolean silent) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);

        JobLevelUpEvent event = new JobLevelUpEvent(player, user, data, oldLevel);
        plugin.getPluginManager().callEvent(event);

        if (!silent) {
            Lang.JOB_LEVEL_UP.message().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));

            if (Config.LEVELING_FIREWORKS.get()) {
                JobUtils.createFirework(player.getWorld(), player.getLocation());
            }
        }
    }

    private void handleLevelDown(@NotNull Player player, @NotNull Job job, int oldLevel, boolean silent) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);

        JobLevelDownEvent event = new JobLevelDownEvent(player, user, data, oldLevel);
        plugin.getPluginManager().callEvent(event);

        if (!silent) {
            Lang.JOB_LEVEL_DOWN.message().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));
        }
    }

    public void addXP(@NotNull Player player, @NotNull Job job, int amount) {
        this.addXP(player, job, amount, false);
    }

    public void addXP(@NotNull Player player, @NotNull Job job, int amount, boolean silent) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        this.handleXPAdd(user, job, amount, silent, player);
    }

    public void handleXPAdd(@NotNull JobUser user, @NotNull Job job, int amount, boolean silent, @Nullable Player player) {
        JobData data = user.getData(job);

        if (player != null && !silent) {
            Lang.JOB_XP_GAIN.message().send(player, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount)));
        }

        this.handleXPSet(user, job, data.getXP() + amount, player);
    }

    public void removeXP(@NotNull Player player, @NotNull Job job, int amount) {
        this.removeXP(player, job, amount, false);
    }

    public void removeXP(@NotNull Player player, @NotNull Job job, int amount, boolean silent) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        this.handleXPRemove(user, job, amount, silent, player);
    }

    public void handleXPRemove(@NotNull JobUser user, @NotNull Job job, int amount, boolean silent, @Nullable Player player) {
        JobData data = user.getData(job);

        if (!silent && player != null) Lang.JOB_XP_LOSE.message().send(player, replacer -> replacer
            .replace(data.replaceAllPlaceholders())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount)));

        this.handleXPSet(user, job, data.getXP() - amount, player);
    }

    public void handleXPSet(@NotNull JobUser user, @NotNull Job job, int amount, /*boolean silent,*/ @Nullable Player player) {
        JobData data = user.getData(job);
        int oldLevel = data.getLevel();

        data.setXP(amount);
        data.update();
        plugin.getUserManager().save(user);

        if (player != null) {
            if (data.getLevel() > oldLevel) {
                this.handleLevelUp(player, job, oldLevel, false);
                this.giveRewardsOrNotify(player, job);
            }
            else if (data.getLevel() < oldLevel) {
                this.handleLevelDown(player, job, oldLevel, false);
            }
        }
    }

    public void giveRewardsOrNotify(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        if (!data.isActive()) return;

        JobState state = data.getState();
        int jobLevel = data.getLevel();
        boolean claimRequired = Config.isRewardClaimRequired();

        List<LevelReward> levelRewards = new ArrayList<>();
        JobRewards rewards = job.getRewards();

        for (int level = JobUtils.START_LEVEL; level < jobLevel + 1; level++) {
            // Old level commands
            if (!data.isLevelRewardObtained(level)) {
                for (LevelReward levelReward : rewards.getRewards(level, state)) {
                    if (levelReward.isAvailable(player)) {
                        levelRewards.add(levelReward);
                    }
                }
            }

            if (!claimRequired) data.setLevelRewardObtained(level); // Set as claimed if no manual claim required.
        }

        if (levelRewards.isEmpty()) return;

        if (claimRequired) {
            Lang.JOB_REWARDS_NOTIFY.message().send(player, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, () -> String.valueOf(levelRewards.size()))
            );
        }
        else {
            levelRewards.forEach(reward -> reward.run(player));

            Lang.JOB_LEVEL_REWARDS_LIST.message().send(player, replacer -> replacer
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    levelRewards.forEach(reward -> {
                        list.add(reward.replacePlaceholders().apply(Lang.JOB_LEVEL_REWARDS_ENTRY.text()));
                    });
                }));
        }
    }

    public void setWorkstationOwnerId(@NotNull TileState station, @NotNull UUID uuid) {
        PDCUtil.set(station, this.stationOwnerKey, uuid);
    }

    @Nullable
    public UUID getWorkstationOwnerId(@NotNull TileState station) {
        return PDCUtil.getUUID(station, this.stationOwnerKey).orElse(null);
    }

    @Nullable
    public Player getWorkstationOwner(@NotNull TileState station) {
        UUID uuid = getWorkstationOwnerId(station);
        return uuid == null ? null : Players.getPlayer(uuid);
    }

    public void setWorkstationMode(@NotNull TileState station, @NotNull WorkstationMode mode) {
        PDCUtil.set(station, this.stationModeKey, mode.getId());
    }

    @Nullable
    public WorkstationMode getWorkstationMode(@NotNull TileState station) {
        return PDCUtil.getInt(station, this.stationModeKey).map(WorkstationMode::byId).orElse(null);
    }
}
