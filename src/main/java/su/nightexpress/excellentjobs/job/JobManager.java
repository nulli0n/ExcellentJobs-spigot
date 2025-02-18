package su.nightexpress.excellentjobs.job;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.api.event.*;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.*;
import su.nightexpress.excellentjobs.job.impl.*;
import su.nightexpress.excellentjobs.job.listener.JobExploitListener;
import su.nightexpress.excellentjobs.job.listener.JobGenericListener;
import su.nightexpress.excellentjobs.job.menu.*;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
import su.nightexpress.excellentjobs.job.util.JobCreator;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.economybridge.api.Currency;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JobManager extends AbstractManager<JobsPlugin> {

    private final Map<String, Job>                    jobMap;
    private final Map<UUID, Map<String, JobIncome>>   incomeMap;
    private final Map<UUID, Map<String, ProgressBar>> progressBarMap;

    private JobsMenu jobsMenu;
    private JobMenu      jobMenu;
    private PreviewMenu  previewMenu;
    private JobResetMenu   jobResetMenu;
    private ObjectivesMenu objectivesMenu;
    private RewardsMenu    rewardsMenu;
    private JobJoinConfirmMenu  joinConfirmMenu;
    private JobLeaveConfirmMenu leaveConfirmMenu;

    public JobManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.jobMap = new HashMap<>();
        this.incomeMap = new HashMap<>();
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
        PDCUtil.set(entity, Keys.ENTITY_TRACKED, true);
    }

    public static boolean isDevastated(@NotNull Entity entity) {
        return PDCUtil.getBoolean(entity, Keys.ENTITY_TRACKED).isPresent();
    }

    @Override
    protected void onLoad() {
        JobCreator.createDefaultJobs();

        FileUtil.getFolders(plugin.getDataFolder() + Config.DIR_JOBS).forEach(jobDir -> {
            File file = new File(jobDir.getAbsolutePath(), Job.CONFIG_NAME);
            Job job = new Job(plugin, file, jobDir.getName());
            if (job.load()) {
                this.jobMap.put(job.getId(), job);
            }
            else this.plugin.warn("Job not loaded: '" + jobDir.getName() + "'.");
        });
        this.plugin.info("Loaded " + this.jobMap.size() + " jobs.");

        this.jobsMenu = new JobsMenu(this.plugin);
        this.jobMenu = new JobMenu(this.plugin);
        this.previewMenu = new PreviewMenu(this.plugin);
        this.jobResetMenu = new JobResetMenu(this.plugin);
        this.joinConfirmMenu = new JobJoinConfirmMenu(this.plugin);
        this.leaveConfirmMenu = new JobLeaveConfirmMenu(this.plugin);
        this.objectivesMenu = new ObjectivesMenu(this.plugin);
        this.rewardsMenu = new RewardsMenu(this.plugin);

        this.addListener(new JobGenericListener(this.plugin, this));
        this.addListener(new JobExploitListener(this.plugin));

        this.addTask(this.plugin.createAsyncTask(this::payForJob).setSecondsInterval(Config.GENERAL_PAYMENT_INTERVAL.get()));
        if (Config.GENERAL_PROGRESS_BAR_ENABLED.get()) {
            this.addTask(this.plugin.createAsyncTask(this::tickProgressBars).setSecondsInterval(1));
        }
    }

    @Override
    protected void onShutdown() {
        this.payForJob();
        this.progressBarMap.values().forEach(map -> map.values().forEach(ProgressBar::discard));

        if (this.jobMenu != null) this.jobMenu.clear();
        if (this.previewMenu != null) this.previewMenu.clear();
        if (this.rewardsMenu != null) this.rewardsMenu.clear();
        if (this.objectivesMenu != null) this.objectivesMenu.clear();
        if (this.jobsMenu != null) this.jobsMenu.clear();
        if (this.jobResetMenu != null) this.jobResetMenu.clear();
        if (this.joinConfirmMenu != null) this.joinConfirmMenu.clear();
        if (this.leaveConfirmMenu != null) this.leaveConfirmMenu.clear();

        this.jobMap.clear();
        this.incomeMap.clear();
        this.progressBarMap.clear();
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
        JobUser user = plugin.getUserManager().getUserData(player);

        return this.getJobs().stream()
            .filter(job -> state == null || user.getData(job).getState() == state)
            .collect(Collectors.toSet());
    }

    @NotNull
    public Set<Job> getActiveJobs(@NotNull Player player) {
        JobUser user = plugin.getUserManager().getUserData(player);

        return this.getJobs().stream()
            .filter(job -> user.getData(job).getState() != JobState.INACTIVE)
            .collect(Collectors.toSet());
    }

    @NotNull
    public <O> Set<Job> getJobsByObjective(@NotNull ActionType<?, O> type, @NotNull O objective) {
        return this.getJobs().stream().filter(job -> job.hasObjective(type, objective)).collect(Collectors.toCollection(HashSet::new));
    }

    @NotNull
    public Map<UUID, Map<String, JobIncome>> getIncomeMap() {
        return incomeMap;
    }

    @NotNull
    private Map<String, JobIncome> getIncomeMap(@NotNull Player player) {
        return this.incomeMap.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
    }

    @NotNull
    public JobIncome getIncome(@NotNull Player player, @NotNull Job job) {
        return this.getIncomeMap(player).computeIfAbsent(job.getId(), k -> new JobIncome(job));
    }

    @NotNull
    public Collection<JobIncome> getIncomes(@NotNull Player player) {
        return this.getIncomeMap(player).values();
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

    public void openPreviewMenu(@NotNull Player player, @NotNull Job job) {
        this.previewMenu.open(player, job);
    }

    public void openObjectivesMenu(@NotNull Player player, @NotNull Job job) {
        this.objectivesMenu.open(player, job);
    }

    public void openRewardsMenu(@NotNull Player player, @NotNull Job job) {
        this.rewardsMenu.openAtLevel(player, job);
    }

    public void openJoinConfirmMenu(@NotNull Player player, @NotNull Job job) {
        this.joinConfirmMenu.open(player, job);
    }

    public void openLeaveConfirmMenu(@NotNull Player player, @NotNull Job job) {
        this.leaveConfirmMenu.open(player, job);
    }

    public void openResetMenu(@NotNull Player player, @NotNull Job job) {
        JobUser user = this.plugin.getUserManager().getUserData(player);
        this.jobResetMenu.open(player, user.getData(job));
    }

    public boolean canGetMoreJobs(@NotNull Player player) {
        //return this.canGetMoreJobs(player, JobState.PRIMARY) || this.canGetMoreJobs(player, JobState.SECONDARY);

        return this.countJoinableJobs(player) != 0;
    }

    public boolean canGetMoreJobs(@NotNull Player player, @NotNull JobState state) {
//        JobUser user = this.plugin.getUserManager().getUserData(player);
//        int limit = getJobsLimit(player, state);
//        return limit < 0 || user.countJobs(state) < limit;

        return this.countJoinableJobs(player, state) != 0;
    }

    public int countJoinableJobs(@NotNull Player player) {
        return this.countJoinableJobs(player, JobState.PRIMARY) + this.countJoinableJobs(player, JobState.SECONDARY);
    }

    public int countJoinableJobs(@NotNull Player player, @NotNull JobState state) {
        JobUser user = this.plugin.getUserManager().getUserData(player);
        int limit = getJobsLimit(player, state);
        if (limit < 0) return -1;

        return user.countJobs(state) - limit;
    }

    public void handleQuit(@NotNull Player player) {
        this.payForJob(player);
        this.getProgressBars(player).forEach(ProgressBar::discard);
        this.incomeMap.remove(player.getUniqueId());
        this.progressBarMap.remove(player.getUniqueId());
    }

    public void displayJobProgress(@NotNull Player player, @NotNull Job job) {

    }

    public void handleJoin(@NotNull Player player) {
        this.validateJobs(player);
    }

    public void validateJobs(@NotNull Player player) {
        if (!Config.JOBS_FORCE_LEAVE_WHEN_LOST_PERMISSION.get()) return;

        JobUser user = this.plugin.getUserManager().getUserData(player);
        this.getJobs().forEach(job -> {
            JobData data = user.getData(job);
            if (data.isActive() && !job.hasPermission(player)) {
                this.onLeaveJob(job, data);
            }
        });
        this.plugin.getUserManager().scheduleSave(user);
    }

    public boolean leaveJob(@NotNull Player player, @NotNull Job job) {
        return this.leaveJob(player, job, false);
    }

    public boolean leaveJob(@NotNull Player player, @NotNull Job job, boolean silent) {
        JobUser user = this.plugin.getUserManager().getUserData(player);
        JobData data = user.getData(job);
        if (!data.isActive()) {
            Lang.JOB_LEAVE_ERROR_NOT_JOINED.getMessage().replace(job.replacePlaceholders()).send(player);
            return false;
        }

        if (!job.isAllowedState(JobState.INACTIVE)) {
            Lang.JOB_LEAVE_ERROR_NOT_ALLOWED.getMessage().replace(job.replacePlaceholders()).send(player);
            return false;
        }

        JobLeaveEvent event = new JobLeaveEvent(player, job, data.getState());
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        this.onLeaveJob(job, data);
        this.plugin.getUserManager().scheduleSave(user);

        Lang.JOB_LEAVE_SUCCESS.getMessage().replace(job.replacePlaceholders()).send(player);
        return true;
    }

    private void onLeaveJob(@NotNull Job job, @NotNull JobData data) {
        job.removeEmployee(data.getState(), 1);
        data.setState(JobState.INACTIVE);
        if (Config.JOBS_LEAVE_RESET_PROGRESS.get()) {
            data.reset();
        }
    }

    @Deprecated
    public boolean joinJob(@NotNull Player player, @NotNull Job job, boolean forced) {
        if (forced || (this.canGetMoreJobs(player, JobState.PRIMARY) && job.isAllowedState(JobState.PRIMARY))) {
            return this.joinJob(player, job, JobState.PRIMARY, forced);
        }
        if (this.canGetMoreJobs(player, JobState.SECONDARY) && job.isAllowedState(JobState.SECONDARY)) {
            return this.joinJob(player, job, JobState.SECONDARY, false);
        }

        Lang.JOB_JOIN_ERROR_LIMIT_GENERAL.getMessage().replace(job.replacePlaceholders()).send(player);
        return false;
    }

    public boolean joinJob(@NotNull Player player, @NotNull Job job, @NotNull JobState state, boolean forced) {
        if (state == JobState.INACTIVE) return false;

        JobUser user = this.plugin.getUserManager().getUserData(player);
        JobData data = user.getData(job);
        if (data.getState() == state) {
            Lang.JOB_JOIN_ERROR_ALREADY_HIRED.getMessage().replace(job.replacePlaceholders()).send(player);
            return false;
        }

        if (!forced) {
            if (!job.hasPermission(player)) {
                Lang.ERROR_NO_PERMISSION.getMessage().send(player);
                return false;
            }

            if (!this.canGetMoreJobs(player, state)) {
                Lang.JOB_JOIN_ERROR_LIMIT_STATE.getMessage()
                    .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(getJobsLimit(player, state)))
                    .replace(Placeholders.GENERIC_STATE, Lang.JOB_STATE.getLocalized(state))
                    .replace(job.replacePlaceholders())
                    .send(player);
                return false;
            }
        }

        JobJoinEvent event = new JobJoinEvent(player, job, state);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        data.setState(event.getState());
        job.addEmployee(event.getState(), 1);
        this.plugin.getUserManager().scheduleSave(user);

        Lang.JOB_JOIN_SUCCESS.getMessage().replace(job.replacePlaceholders()).send(player);
        return true;
    }

    public void tickProgressBars() {
        this.progressBarMap.values().forEach(map -> map.values().removeIf(ProgressBar::checkExpired));
        this.progressBarMap.values().removeIf(Map::isEmpty);
    }

    public void payForJob() {
        this.plugin.getServer().getOnlinePlayers().forEach(this::payForJob);
    }

    public void payForJob(@NotNull Player player) {
        Map<Job, Map<Currency, Double>> perJob = new HashMap<>();

        // Get payment sum of all objectives for each job.
        this.getIncomes(player).forEach(jobIncome -> {
            Job job = jobIncome.getJob();

            JobPrePaymentEvent event = new JobPrePaymentEvent(player, job, jobIncome);
            this.plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            perJob.put(job, jobIncome.getSumAndClear());
        });
        perJob.values().removeIf(Map::isEmpty); // Clear empty job payments


        // Calculate total payment from all jobs.
        Map<Currency, Double> total = new HashMap<>();
        perJob.forEach((job, map) -> map.forEach((currency, amount) -> {

            JobPaymentEvent event = new JobPaymentEvent(player, job, currency, amount);
            this.plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            total.merge(event.getCurrency(), event.getAmount(), Double::sum);
        }));
        if (total.isEmpty()) return;


        // Pay the worker.
        total.forEach((currency, amount) -> {
            currency.give(player, amount);
        });

        String totalPay = total.entrySet().stream().map(entry -> {
            Currency currency = entry.getKey();
            String amount = currency.format(entry.getValue());
            return Lang.JOB_PAYMENT_RECEIPT_ENTRY_CURRENCY.getString().replace(Placeholders.GENERIC_AMOUNT, amount);
        }).collect(Collectors.joining(", "));

        Lang.JOB_PAYMENT_RECEIPT.getMessage()
            .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(Config.GENERAL_PAYMENT_INTERVAL.get() * 1000L + 100L))
            .replace(Placeholders.GENERIC_TOTAL, totalPay)
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                perJob.forEach((job, moneyMap) -> {
                    String currencies = moneyMap.entrySet().stream()
                        .map(entry -> {
                            Currency currency = entry.getKey();
                            String amount = currency.format(entry.getValue());
                            return Lang.JOB_PAYMENT_RECEIPT_ENTRY_CURRENCY.getString().replace(Placeholders.GENERIC_AMOUNT, amount);
                        })
                        .collect(Collectors.joining(Placeholders.TAG_LINE_BREAK));

                    list.add(job.replacePlaceholders().apply(Lang.JOB_PAYMENT_RECEIPT_ENTRY_JOB.getString()
                        .replace(Placeholders.GENERIC_CURRENCY, currencies)
                    ));
                });
            })
            .send(player);
    }

    public boolean createSpecialOrder(@NotNull Player player, @NotNull Job job, boolean force) {
        if (!Config.SPECIAL_ORDERS_ENABLED.get()) {
            Lang.SPECIAL_ORDER_ERROR_DISABLED_SERVER.getMessage().send(player);
            return false;
        }

        JobUser user = plugin.getUserManager().getUserData(player);
        JobData jobData = user.getData(job);

        if (!force) {
            if (!job.isSpecialOrdersAllowed()) {
                Lang.SPECIAL_ORDER_ERROR_DISABLED_JOB.getMessage().replace(job.replacePlaceholders()).send(player);
                return false;
            }

            if (jobData.hasOrder() && !jobData.isOrderCompleted()) {
                Lang.SPECIAL_ORDER_ERROR_ALREADY_HAVE.getMessage().replace(job.replacePlaceholders()).send(player);
                return false;
            }

            int totalOrders = user.countActiveSpecialOrders();
            int maxAmount = Config.SPECIAL_ORDERS_MAX_AMOUNT.get();
            if (maxAmount >= 0 && totalOrders >= maxAmount) {
                Lang.SPECIAL_ORDER_ERROR_MAX_AMOUNT.getMessage()
                    .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(maxAmount))
                    .send(player);
                return false;
            }

            if (!jobData.isReadyForNextOrder()) {
                Lang.SPECIAL_ORDER_ERROR_COOLDOWN.getMessage()
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(jobData.getNextOrderDate()))
                    .send(player);
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
        this.plugin.getUserManager().scheduleSave(user);

        Lang.SPECIAL_ORDER_TAKEN_INFO.getMessage()
            .replace(job.replacePlaceholders())
            .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(orderData.getExpireDate()))
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

                    int totalCount = orderObjective.getObjectCountMap().values().stream().mapToInt(JobOrderCount::getRequired).sum();

                    String details = orderObjective.getObjectCountMap().entrySet().stream().map(entry -> {
                        String objectName = objective.getType().getObjectLocalizedName(entry.getKey());
                        String objectAmount = NumberUtil.format(entry.getValue().getRequired());

                        return Lang.SPECIAL_ORDER_TAKEN_DETAIL.getString()
                            .replace(Placeholders.GENERIC_NAME, objectName)
                            .replace(Placeholders.GENERIC_AMOUNT, objectAmount);
                    }).collect(Collectors.joining(Placeholders.TAG_LINE_BREAK));

                    list.add(Lang.SPECIAL_ORDER_TAKEN_ENTRY.getString()
                        .replace(Placeholders.GENERIC_NAME, objective.getDisplayName())
                        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(totalCount))
                        .replace(Placeholders.GENERIC_ENTRY, details)
                    );
                });
            }).send(player);

        return true;
    }

    public <O> void doObjective(@NotNull Player player, @NotNull ActionType<?, O> type, @NotNull O object, int amount) {
        this.doObjective(player, type, object, amount, 0D);
    }

    public <O> void doObjective(@NotNull Player player, @NotNull ActionType<?, O> type, @NotNull O object, int amount, double multiplier) {
        JobUser user = plugin.getUserManager().getUserData(player);

        user.getDatas().forEach(jobData -> {
            if (jobData.getState() == JobState.INACTIVE) return;

            Job job = jobData.getJob();
            if (!job.isGoodWorld(player.getWorld())) return;

            JobObjective jobObjective = job.getObjectiveByObject(type, object);
            if (jobObjective == null || !jobObjective.isUnlocked(player, jobData)) return;

            String objectId = type.getObjectName(object);

            JobOrderData orderData = jobData.getOrderData();
            Label_Order:
            if (jobData.hasOrder()) {
                if (!orderData.isCompleted()) {
                    JobOrderObjective orderObjective = orderData.getObjectiveMap().get(jobObjective.getId());
                    if (orderObjective == null) break Label_Order;

                    JobOrderCount count = orderObjective.getCount(objectId);
                    if (count == null) break Label_Order;

                    orderObjective.countObject(objectId, amount);

                    Lang.SPECIAL_ORDER_PROGRESS.getMessage()
                        .replace(job.replacePlaceholders())
                        .replace(Placeholders.GENERIC_NAME, type.getObjectLocalizedName(object))
                        .replace(Placeholders.GENERIC_CURRENT, NumberUtil.format(count.getCurrent()))
                        .replace(Placeholders.GENERIC_MAX, NumberUtil.format(count.getRequired()))
                        .send(player);
                }
                if (orderData.isCompleted() && !orderData.isRewarded()) {
                    List<OrderReward> rewards = orderData.translateRewards();
                    rewards.forEach(reward -> reward.give(player));
                    orderData.setRewarded(true);
                    Lang.SPECIAL_ORDER_COMPLETED.getMessage().replace(job.replacePlaceholders()).send(player);
                }
            }

            if (!jobObjective.canPay()) return;

            int jobLevel = jobData.getLevel();
            Collection<Booster> boosters = this.plugin.getBoosterManager().getBoosters(player, job);
            ProgressBar progressBar = this.getProgressBarOrCreate(player, job);

            JobIncome income = this.getIncome(player, job);
            jobObjective.getPaymentMap().forEach((currencyId, rewardInfo) -> {
                // Do no process payment for limited currencies.
                if (jobData.isPaymentLimitReached(currencyId)) return;

                Currency currency = EconomyBridge.getCurrency(currencyId);
                if (currency == null) return;

                double payment = rewardInfo.rollAmountNaturally() * amount;
                double paymentMultiplier = 1D;

                paymentMultiplier += Booster.getCurrencyPlainBoost(currencyId, boosters);
                paymentMultiplier += job.getPaymentMultiplier(currencyId, jobLevel);
                paymentMultiplier += multiplier;

                JobObjectiveIncomeEvent event = new JobObjectiveIncomeEvent(
                    player, user, jobData, jobObjective, type, object, objectId, currency, payment, paymentMultiplier
                );
                this.plugin.getPluginManager().callEvent(event);
                if (event.isCancelled()) return;

                payment = event.getPayment() * event.getPaymentMultiplier();
                if (payment == 0D || Double.isNaN(payment) || Double.isInfinite(payment)) return;

                income.add(jobObjective, currency, payment);
                if (progressBar != null) progressBar.addPayment(currency, payment);

                if (!player.hasPermission(Perms.PREFIX_BYPASS_LIMIT_CURRENCY + job.getId()) && job.hasDailyPaymentLimit(currencyId, jobLevel)) {
                    jobData.getLimitData().addCurrency(currencyId, payment);

                    if (jobData.isPaymentLimitReached(currencyId)) {
                        Lang.JOB_LIMIT_CURRENCY_NOTIFY.getMessage()
                            .replace(job.replacePlaceholders())
                            .replace(currency.replacePlaceholders())
                            .send(player);
                    }
                }
            });



            XP:
            if (!jobData.isXPLimitReached()) {
                double xpRoll = jobObjective.getXPReward().rollAmountNaturally() * amount;
                double xpMultiplier = 1D;

                xpMultiplier += Booster.getPlainXPBoost(boosters);
                xpMultiplier += job.getXPMultiplier(jobLevel);
                xpMultiplier += multiplier;

                JobObjectiveXPEvent event = new JobObjectiveXPEvent(
                    player, user, jobData, jobObjective, type, object, xpRoll, xpMultiplier
                );
                this.plugin.getPluginManager().callEvent(event);
                if (event.isCancelled()) break XP;

                xpRoll = event.getXPAmount() * event.getXPMultiplier();
                if (xpRoll == 0D || Double.isNaN(xpRoll) || Double.isInfinite(xpRoll)) break XP;

                if (this.addXP(player, job, /*type.getObjectLocalizedName(object),*/ xpRoll, false)) {
                    if (progressBar != null) progressBar.addXP((int) xpRoll);

                    if (!player.hasPermission(Perms.PREFIX_BYPASS_LIMIT_XP + job.getId()) && job.hasDailyXPLimit(jobLevel)) {
                        jobData.getLimitData().addXP((int) xpRoll);

                        if (jobData.isXPLimitReached()) {
                            Lang.JOB_LIMIT_XP_NOTIFY.getMessage().replace(job.replacePlaceholders()).send(player);
                        }
                    }
                }
            }

            if (progressBar != null) progressBar.updateDisplay();
        });
    }

    public void addLevel(@NotNull Player player, @NotNull Job job, int amount) {
        JobUser user = plugin.getUserManager().getUserData(player);
        JobData jobData = user.getData(job);
        boolean isMinus = amount < 0;

        for (int count = 0; count < Math.abs(amount); count++) {
            int exp = isMinus ? -jobData.getXPToLevelDown() : jobData.getXPToLevelUp();
            this.addXP(player, job, exp, false);
        }
    }

    public boolean addXP(@NotNull Player player, @NotNull Job job, double amount) {
        return this.addXP(player, job, amount, true);
    }

    public boolean addXP(@NotNull Player player, @NotNull Job job, double amount, boolean notify) {
        if (amount == 0D) return false;

        JobUser user = plugin.getUserManager().getUserData(player);
        JobData jobData = user.getData(job);

        boolean isLose = amount < 0;
        int xp = (int) Math.floor(amount);

        JobXPEvent xpEvent = JobXPEvent.createEvent(player, user, jobData, xp);
        plugin.getPluginManager().callEvent(xpEvent);
        if (xpEvent.isCancelled()) return false;

        xp = xpEvent.getXP();

        int levelHas = jobData.getLevel();
        if (isLose) {
            jobData.removeXP(xp);
        }
        else {
            jobData.addXP(xp);
        }

        // Send exp gain/lose message.
        if (notify) {
            (isLose ? Lang.JOB_XP_LOSE : Lang.JOB_XP_GAIN).getMessage()
                .replace(jobData.replaceAllPlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .send(player);
        }

        this.plugin.getUserManager().scheduleSave(user);

        // Call events for level up/down.
        if (levelHas > jobData.getLevel()) {
            JobLevelDownEvent event = new JobLevelDownEvent(player, user, jobData);
            plugin.getPluginManager().callEvent(event);

            Lang.JOB_LEVEL_DOWN.getMessage().replace(jobData.replaceAllPlaceholders()).send(player);
        }
        else if (levelHas < jobData.getLevel()) {
            JobLevelUpEvent event = new JobLevelUpEvent(player, user, jobData);
            plugin.getPluginManager().callEvent(event);

            // TODO force permission
            this.triggerLevelRewards(player, job, jobData.getLevel(), false);

            Lang.JOB_LEVEL_UP.getMessage().replace(jobData.replaceAllPlaceholders()).send(player);

            if (Config.LEVELING_FIREWORKS.get()) {
                this.createFirework(player.getWorld(), player.getLocation());
            }
        }
        return true;
    }

    public boolean addXPNaturally(@NotNull Player player, @NotNull Job job, double amount) {
        Collection<Booster> boosters = this.plugin.getBoosterManager().getBoosters(player, job);
        double xpMultiplier = 1D + Booster.getPlainXPBoost(boosters);

        amount *= xpMultiplier;

        return this.addXP(player, job, amount);
    }

    public void triggerLevelRewards(@NotNull Player player, @NotNull Job job, int level, boolean force) {
        JobUser user = this.plugin.getUserManager().getUserData(player);
        JobData jobData = user.getData(job);

        if (force || !jobData.isLevelRewardObtained(level)) {
            job.getLevelUpCommands(level).forEach(command -> {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Placeholders.forPlayer(player).apply(command));
            });
        }

        List<LevelReward> rewards = job.getRewards().getRewards(level);
        rewards.removeIf(reward -> !force && (jobData.isLevelRewardObtained(level) || !reward.isAvailable(player, level)));

        //rewards.removeIf(reward -> !force && jobData.isLevelRewardObtained(reward.getLevel()) && !reward.isRepeatable());

        if (!rewards.isEmpty()) {
            rewards.forEach(reward -> reward.run(player));

            Lang.JOB_LEVEL_REWARDS_LIST.getMessage()
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    rewards.forEach(reward -> {
                        list.add(reward.replacePlaceholders().apply(Lang.JOB_LEVEL_REWARDS_ENTRY.getString()));
                    });
                })
                .send(player);
        }

        jobData.setLevelRewardObtained(level);
    }

    @NotNull
    private Firework createFirework(@NotNull World world, @NotNull Location location) {
        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Type type = Rnd.get(FireworkEffect.Type.values());
        Color color = Color.fromBGR(Rnd.get(256), Rnd.get(256), Rnd.get(256));
        Color fade = Color.fromBGR(Rnd.get(256), Rnd.get(256), Rnd.get(256));
        FireworkEffect effect = FireworkEffect.builder()
            .flicker(Rnd.nextBoolean()).withColor(color).withFade(fade).with(type).trail(Rnd.nextBoolean()).build();

        meta.addEffect(effect);
        meta.setPower(Rnd.get(4));
        firework.setFireworkMeta(meta);
        PDCUtil.set(firework, Keys.LEVEL_FIREWORK, true);
        return firework;
    }
}
