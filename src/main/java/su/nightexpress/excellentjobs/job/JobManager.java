package su.nightexpress.excellentjobs.job;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.api.event.*;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobOrderCount;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobOrderObjective;
import su.nightexpress.excellentjobs.job.dialog.JobDialogs;
import su.nightexpress.excellentjobs.job.impl.*;
import su.nightexpress.excellentjobs.job.listener.JobExploitListener;
import su.nightexpress.excellentjobs.job.listener.JobGenericListener;
import su.nightexpress.excellentjobs.job.menu.*;
import su.nightexpress.excellentjobs.job.reward.JobRewards;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkObjective;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobCreator;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.ui.UIUtils;
import su.nightexpress.nightcore.ui.menu.confirmation.Confirmation;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobManager extends AbstractManager<JobsPlugin> {

    private final Map<String, Job>                    jobMap;
    private final Map<UUID, Map<String, ProgressBar>> progressBarMap;

    private JobsMenu       jobsMenu;
    private JobMenu        jobMenu;
    private ObjectivesMenu objectivesMenu;
    private RewardsMenu    rewardsMenu;

    public JobManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.jobMap = new HashMap<>();
        this.progressBarMap = new ConcurrentHashMap<>();
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

    public static void devastateEntity(@NotNull Entity entity) {
        PDCUtil.set(entity, Keys.entityTracked, true);
    }

    public static boolean isDevastated(@NotNull Entity entity) {
        return PDCUtil.getBoolean(entity, Keys.entityTracked).isPresent();
    }

    @Override
    protected void onLoad() {
        this.loadJobs();
        this.loadUI();

        this.addListener(new JobGenericListener(this.plugin, this));
        this.addListener(new JobExploitListener(this.plugin));

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

        if (this.jobMenu != null) this.jobMenu.clear();
        if (this.rewardsMenu != null) this.rewardsMenu.clear();
        if (this.objectivesMenu != null) this.objectivesMenu.clear();
        if (this.jobsMenu != null) this.jobsMenu.clear();

        this.jobMap.clear();
        this.progressBarMap.clear();
    }

    private void loadJobs() {
        File dir = new File(this.plugin.getDataFolder() + Config.DIR_JOBS);
        if (!dir.exists()) {
            dir.mkdirs();
            new JobCreator(this.plugin).createDefaultJobs();
        }

        FileUtil.getFolders(plugin.getDataFolder() + Config.DIR_JOBS).forEach(jobDir -> {
            File file = new File(jobDir.getAbsolutePath(), Job.CONFIG_NAME);
            Job job = new Job(plugin, file, jobDir.getName());
            if (job.load()) {
                this.jobMap.put(job.getId(), job);
            }
            else this.plugin.warn("Job not loaded: '" + jobDir.getName() + "'.");
        });
        this.plugin.info("Loaded " + this.jobMap.size() + " jobs.");
    }

    private void loadUI() {
        this.jobsMenu = new JobsMenu(this.plugin);
        this.jobMenu = new JobMenu(this.plugin);
        this.objectivesMenu = new ObjectivesMenu(this.plugin);
        this.rewardsMenu = new RewardsMenu(this.plugin);
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

        //return this.getProgressBarMap(player).computeIfAbsent(job.getId(), k -> new ProgressBar(this.plugin, job, player));
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

    public void openJobMenu(@NotNull Player player, @NotNull Job job) {
        this.jobMenu.open(player, job);
    }

    public void openObjectivesMenu(@NotNull Player player, @NotNull Job job) {
        this.objectivesMenu.open(player, job, null);
    }

    public void openRewardsMenu(@NotNull Player player, @NotNull Job job) {
        this.rewardsMenu.openAtLevel(player, job);
    }

    public void openLeaveConfirmMenu(@NotNull Player player, @NotNull Job job) {
        JobData data = plugin.getUserManager().getOrFetch(player).getData(job);

        if (Version.isAtLeast(Version.MC_1_21_7)) {
            JobDialogs.openLeaveConfirm(this.plugin, player, job);
            return;
        }

        UIUtils.openConfirmation(player, Confirmation.builder()
            .onAccept((viewer, event) -> {
                this.leaveJob(player, job);
                this.plugin.runTask(task -> player.closeInventory());
            })
            .onReturn((viewer, event) -> {
                this.plugin.runTask(task -> this.openJobMenu(viewer.getPlayer(), job));
            })
            .setIcon(job.getIcon().localized(Lang.UI_JOB_LEAVE_INFO).replacement(replacer -> replacer.replace(data.replaceAllPlaceholders())))
            .build());
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

        return user.countJobs(state) > limit;
    }

    public void handleQuit(@NotNull Player player) {
        this.payForJob(player);
        this.getProgressBars(player).forEach(ProgressBar::discard);
        //this.incomeMap.remove(player.getUniqueId());
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
            Lang.JOB_LEAVE_ERROR_NOT_JOINED.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        if (data.isOnCooldown()) {
            Lang.JOB_LEAVE_ERROR_COOLDOWN.getMessage().send(player, replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(data.getCooldown(), TimeFormatType.LITERAL)));
            return false;
        }

        if (!job.isLeaveable()) {
            Lang.JOB_LEAVE_ERROR_NOT_ALLOWED.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        JobLeaveEvent event = new JobLeaveEvent(player, job, data.getState());
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        this.onLeaveJob(player, job, data);
        this.plugin.getUserManager().save(user);

        Lang.JOB_LEAVE_SUCCESS.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
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
            Lang.JOB_JOIN_NOT_JOINABLE.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        for (JobState state : JobState.actives()) {
            if (job.isAllowedState(state) && this.canGetMoreJobs(player, state)) {
                return this.joinJob(player, job, state, false);
            }
        }

        Lang.JOB_JOIN_ERROR_LIMIT_GENERAL.getMessage().send(player);
        return false;
    }

    public boolean joinJob(@NotNull Player player, @NotNull Job job, @NotNull JobState state, boolean forced) {
        if (state == JobState.INACTIVE) return false;

        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        if (data.getState() == state) {
            Lang.JOB_JOIN_ERROR_ALREADY_HIRED.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            return false;
        }

        if (!forced) {
            if (!job.hasPermission(player)) {
                Lang.ERROR_NO_PERMISSION.getMessage().send(player);
                return false;
            }

            if (data.isOnCooldown()) {
                Lang.JOB_JOIN_ERROR_COOLDOWN.getMessage().send(player, replacer -> replacer
                    .replace(job.replacePlaceholders())
                    .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(data.getCooldown(), TimeFormatType.LITERAL)));
                return false;
            }

            if (!this.canGetMoreJobs(player, state)) {
                Lang.JOB_JOIN_ERROR_LIMIT_STATE.getMessage().send(player, replacer -> replacer
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

        (join ? Lang.JOB_JOIN_SUCCESS : Lang.JOB_PRIORITY_CHANGED).getMessage().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));
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
            if (!silent) Lang.JOB_RESET_NOTIFY.getMessage().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));
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
            Lang.JOB_PAYMENT_NOTIFY.getMessage().send(player, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, JobUtils.formatIncome(income))
            );
        }

        income.payAndClear(player);

        return true;
    }

    public boolean createSpecialOrder(@NotNull Player player, @NotNull Job job, boolean force) {
        if (!Config.SPECIAL_ORDERS_ENABLED.get()) {
            Lang.SPECIAL_ORDER_ERROR_DISABLED_SERVER.getMessage().send(player);
            return false;
        }

        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData jobData = user.getData(job);

        if (!force) {
            if (!job.isSpecialOrdersAllowed()) {
                Lang.SPECIAL_ORDER_ERROR_DISABLED_JOB.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
                return false;
            }

            if (jobData.hasOrder() && !jobData.isOrderCompleted()) {
                Lang.SPECIAL_ORDER_ERROR_ALREADY_HAVE.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
                return false;
            }

            int totalOrders = user.countActiveSpecialOrders();
            int maxAmount = Config.SPECIAL_ORDERS_MAX_AMOUNT.get();
            if (maxAmount >= 0 && totalOrders >= maxAmount) {
                Lang.SPECIAL_ORDER_ERROR_MAX_AMOUNT.getMessage().send(player, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(maxAmount)));
                return false;
            }

            if (!jobData.isReadyForNextOrder()) {
                Lang.SPECIAL_ORDER_ERROR_COOLDOWN.getMessage().send(player, replacer -> replacer
                    .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(jobData.getNextOrderDate(), TimeFormatType.LITERAL)));
                return false;
            }


            if (!job.canAffordSpecialOrder(player)) {
                Lang.SPECIAL_ORDER_ERROR_NOT_ENOUGH_FUNDS_INFO.getMessage().send(player, replacer -> replacer
                    .replace(Placeholders.GENERIC_ENTRY, list -> {
                        job.getSpecialOrdersCost().forEach((id, amount) -> {
                            Currency currency = EconomyBridge.getCurrency(id);
                            if (currency == null) return;

                            list.add(currency.replacePlaceholders().apply(Lang.SPECIAL_ORDER_ERROR_NOT_ENOUGH_FUNDS_ENTRY.getString()
                                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                            ));
                        });
                    })
                );
                return false;
            }
        }

        JobOrderData orderData = job.createSpecialOrder(jobData.getLevel());
        if (orderData == null) {
            Lang.SPECIAL_ORDER_ERROR_GENERATION.getMessage().send(player);
            return false;
        }

        if (!force) {
            job.payForSpecialOrder(player);
        }

        long cooldown = Config.SPECIAL_ORDERS_COOLDOWN.get();
        long nextOrderDate;
        if (cooldown < 0) {
            LocalDateTime midnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
            nextOrderDate = TimeUtil.toEpochMillis(midnight);
        }
        else {
            nextOrderDate = System.currentTimeMillis() + cooldown * 1000L;
        }

        jobData.setOrderData(orderData);
        jobData.setNextOrderDate(nextOrderDate);
        this.plugin.getUserManager().save(user);

        Lang.SPECIAL_ORDER_TAKEN_INFO.getMessage().send(player, replacer -> replacer
            .replace(job.replacePlaceholders())
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(orderData.getExpireDate(), TimeFormatType.LITERAL))
            .replace(Placeholders.GENERIC_REWARD, list -> {
                orderData.translateRewards().forEach(reward -> {
                    list.add(Lang.SPECIAL_ORDER_TAKEN_REWARD.getString()
                        .replace(Placeholders.GENERIC_NAME, reward.getName())
                    );
                });
            })
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                orderData.getObjectiveMap().values().forEach(orderObjective -> {
                    JobObjective objective = job.getObjectiveById(orderObjective.getObjectiveId());
                    if (objective == null) return;

                    Work<?, ?> workType = objective.getWork();
                    if (workType == null) return;

                    int totalCount = orderObjective.getObjectCountMap().values().stream().mapToInt(JobOrderCount::getRequired).sum();

                    String details = orderObjective.getObjectCountMap().entrySet().stream().map(entry -> {
                        String objectName = workType.getObjectLocalizedName(entry.getKey());
                        String objectAmount = NumberUtil.format(entry.getValue().getRequired());

                        return Lang.SPECIAL_ORDER_TAKEN_DETAIL.getString()
                            .replace(Placeholders.GENERIC_NAME, objectName)
                            .replace(Placeholders.GENERIC_AMOUNT, objectAmount);
                    }).collect(Collectors.joining(TagWrappers.BR));

                    list.add(Lang.SPECIAL_ORDER_TAKEN_ENTRY.getString()
                        .replace(Placeholders.GENERIC_NAME, objective.getDisplayName())
                        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(totalCount))
                        .replace(Placeholders.GENERIC_ENTRY, details)
                    );
                });
            }));

        return true;
    }

    public void doObjective(@NotNull Player player, @NotNull WorkObjective workObjective) {
        JobUser user = plugin.getUserManager().getOrFetch(player);

        user.getDatas().forEach(jobData -> {
            if (jobData.getState() == JobState.INACTIVE) return;

            Job job = jobData.getJob();
            if (!job.isGoodWorld(player.getWorld())) return;

            JobObjective jobObjective = job.getObjectiveByWork(workObjective);
            if (jobObjective == null || !jobObjective.isUnlocked(player, jobData)) return;

            ProgressBar progressBar = this.getProgressBarOrCreate(player, job);

            this.proceedOrder(player, workObjective, job, jobData, jobObjective);
            this.proceedIncome(player, workObjective, job, jobData, jobObjective, progressBar);
            this.proceedXP(player, workObjective, job, jobData, jobObjective, progressBar);

            if (progressBar != null) progressBar.updateDisplay();
        });
    }

    private void proceedOrder(@NotNull Player player, @NotNull WorkObjective workObjective, @NotNull Job job, @NotNull JobData jobData, @NotNull JobObjective jobObjective) {
        if (!jobData.hasOrder()) return;

        JobOrderData orderData = jobData.getOrderData();

        String objectId = workObjective.getObjectName();
        int amount = workObjective.getAmount();

        if (!orderData.isCompleted()) {
            JobOrderObjective orderObjective = orderData.getObjectiveMap().get(jobObjective.getId());
            if (orderObjective == null) return;

            JobOrderCount count = orderObjective.getCount(objectId);
            if (count == null) return;

            orderObjective.countObject(objectId, amount);

            Lang.SPECIAL_ORDER_PROGRESS.getMessage().send(player, replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.GENERIC_NAME, workObjective.getLocalizedName())
                .replace(Placeholders.GENERIC_CURRENT, NumberUtil.format(count.getCurrent()))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(count.getRequired())));
        }

        if (orderData.isCompleted() && !orderData.isRewarded()) {
            List<OrderReward> rewards = orderData.translateRewards();
            rewards.forEach(reward -> reward.give(player));
            orderData.setRewarded(true);
            Lang.SPECIAL_ORDER_COMPLETED.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
        }
    }

    private void proceedIncome(@NotNull Player player, @NotNull WorkObjective workObjective, @NotNull Job job, @NotNull JobData jobData, @NotNull JobObjective jobObjective,
                               @Nullable ProgressBar progressBar) {

        if (!jobObjective.canPay()) return;

        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobIncome income = jobData.getIncome();

        String objectId = workObjective.getObjectName();
        int jobLevel = jobData.getLevel();
        int amount = workObjective.getAmount();
        double multiplier = workObjective.getMultiplier();
        double boost = JobsAPI.getBoost(player, job, MultiplierType.INCOME);


        jobObjective.getPaymentMap().forEach((currencyId, rewardInfo) -> {
            // Do no process payment for limited currencies.
            if (jobData.isPaymentLimitReached(currencyId)) return;

            Currency currency = EconomyBridge.getCurrency(currencyId);
            if (currency == null) return;

            double payment = rewardInfo.rollAmountNaturally() * amount;
            double paymentMultiplier = 1D;

            if (JobUtils.canBeBoosted(currency)) {
                paymentMultiplier += boost;
            }
            paymentMultiplier += jobData.getIncomeBonus();
            paymentMultiplier += multiplier;

            JobObjectiveIncomeEvent event = new JobObjectiveIncomeEvent(player, user, jobData, jobObjective, workObjective, objectId, currency, payment, paymentMultiplier);
            this.plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            payment = event.getPayment() * event.getPaymentMultiplier();
            if (payment == 0D || Double.isNaN(payment) || Double.isInfinite(payment)) return;

            income.add(currency, payment);
            if (progressBar != null) progressBar.addPayment(currency, payment);

            if (!player.hasPermission(Perms.PREFIX_BYPASS_LIMIT_CURRENCY + job.getId()) && job.hasDailyPaymentLimit(currencyId, jobLevel)) {
                jobData.getLimitData().addCurrency(currencyId, payment);

                if (jobData.isPaymentLimitReached(currencyId)) {
                    Lang.JOB_LIMIT_CURRENCY_NOTIFY.getMessage().send(player, replacer -> replacer
                        .replace(job.replacePlaceholders())
                        .replace(currency.replacePlaceholders()));
                }
            }
        });
    }

    private void proceedXP(@NotNull Player player, @NotNull WorkObjective workObjective, @NotNull Job job, @NotNull JobData jobData, @NotNull JobObjective jobObjective,
                           @Nullable ProgressBar progressBar) {

        if (jobData.isXPLimitReached()) return;

        JobUser user = plugin.getUserManager().getOrFetch(player);
        int jobLevel = jobData.getLevel();
        int amount = workObjective.getAmount();
        double multiplier = workObjective.getMultiplier();
        double boost = JobsAPI.getBoost(player, job, MultiplierType.XP);

        double xpRoll = jobObjective.getXPReward().rollAmountNaturally() * amount;
        double xpMultiplier = 1D;

        xpMultiplier += boost;
        xpMultiplier += jobData.getXPBonus();
        xpMultiplier += multiplier;

        JobObjectiveXPEvent event = new JobObjectiveXPEvent(player, user, jobData, jobObjective, workObjective, xpRoll, xpMultiplier);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        xpRoll = event.getXPAmount() * event.getXPMultiplier();
        if (xpRoll == 0D || Double.isNaN(xpRoll) || Double.isInfinite(xpRoll)) return;

        int xpFinal = (int) xpRoll;

        if (xpFinal < 0) {
            this.removeXP(player, job, Math.abs(xpFinal), true);
        }
        else {
            this.addXP(player, job, xpFinal, true);
        }

        if (progressBar != null) progressBar.addXP(xpFinal);

        if (!player.hasPermission(Perms.PREFIX_BYPASS_LIMIT_XP + job.getId()) && job.hasDailyXPLimit(jobLevel)) {
            jobData.getLimitData().addXP(xpFinal);

            if (jobData.isXPLimitReached()) {
                Lang.JOB_LIMIT_XP_NOTIFY.getMessage().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
            }
        }
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
            Lang.JOB_LEVEL_UP.getMessage().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));

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
            Lang.JOB_LEVEL_DOWN.getMessage().send(player, replacer -> replacer.replace(data.replaceAllPlaceholders()));
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
            Lang.JOB_XP_GAIN.getMessage().send(player, replacer -> replacer
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

        if (!silent && player != null) Lang.JOB_XP_LOSE.getMessage().send(player, replacer -> replacer
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
                job.getLevelUpCommands(level).forEach(command -> {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Placeholders.forPlayer(player).apply(command));
                });
            }

            for (LevelReward levelReward : rewards.getRewards(level, state)) {
                if (!data.isLevelRewardObtained(level) && levelReward.isAvailable(player)) {
                    levelRewards.add(levelReward);
                }
            }

            if (!claimRequired) data.setLevelRewardObtained(level); // Set as claimed if no manual claim required.
        }

        if (levelRewards.isEmpty()) return;

        if (claimRequired) {
            Lang.JOB_REWARDS_NOTIFY.getMessage().send(player, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, () -> String.valueOf(levelRewards.size()))
            );
        }
        else {
            levelRewards.forEach(reward -> reward.run(player));

            Lang.JOB_LEVEL_REWARDS_LIST.getMessage().send(player, replacer -> replacer
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    levelRewards.forEach(reward -> {
                        list.add(reward.replacePlaceholders().apply(Lang.JOB_LEVEL_REWARDS_ENTRY.getString()));
                    });
                }));
        }
    }
}
