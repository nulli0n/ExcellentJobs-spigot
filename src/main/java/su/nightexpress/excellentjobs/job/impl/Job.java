package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkObjective;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobOrderCount;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobOrderObjective;
import su.nightexpress.excellentjobs.job.reward.JobRewards;
import su.nightexpress.excellentjobs.job.work.WorkRegistry;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static su.nightexpress.excellentjobs.Placeholders.*;

public class Job extends AbstractFileData<JobsPlugin> {

    public static final String CONFIG_NAME = "settings.yml";
    public static final String OBJECTIVES_CONFIG_NAME = "objectives.yml";

    private String       name;
    private List<String> description;
    private boolean   permissionRequired;
    private NightItem icon;
    private JobState  initialState;
    private int          maxLevel;
    private int          maxSecondaryLevel;
    private int          initialXP;
    private double       xpFactor;
    private Modifier     xpMultiplier;
    private Modifier     xpDailyLimits;
    private Modifier      paymentMultiplier;
    private BarColor progressBarColor;

    private boolean                        specialOrdersAllowed;
    private UniInt                         specialOrdersObjectivesAmount;
    private UniInt                         specialOrdersCompleteTime;
    private UniInt                         specialOrdersRewardsAmount;
    private TreeMap<Integer, List<String>> specialOrdersAllowedRewards;
    private Map<String, Double>          specialOrdersCost;

    private final Set<JobState>              allowedStates;
    private final Set<String>                disabledWorlds;
    private final Map<JobState, Integer>     employeesAmount;
    private final Map<Integer, List<String>> levelUpCommands;
    private final JobRewards rewards;
    //private final Map<String, Modifier>      paymentMultiplier;
    private final Map<String, Modifier>      paymentDailyLimits;
    private final Map<String, JobObjective>  objectiveMap;

    public Job(@NotNull JobsPlugin plugin, @NotNull File file, @NotNull String id) {
        super(plugin, file, id);
        this.allowedStates = new HashSet<>();
        this.disabledWorlds = new HashSet<>();
        this.employeesAmount = new ConcurrentHashMap<>();
        this.levelUpCommands = new HashMap<>();
        this.rewards = new JobRewards();
        //this.paymentMultiplier = new HashMap<>();
        this.paymentDailyLimits = new HashMap<>();
        this.objectiveMap = new HashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        if (!ConfigValue.create("Enabled", true).read(config)) return false;

        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId()),
            "Sets display name for the job.",
            Placeholders.URL_WIKI_TEXT
        ).read(config));

        this.setDescription(ConfigValue.create("Description", new ArrayList<>(),
            "Set description for the job.",
            Placeholders.URL_WIKI_TEXT
        ).read(config));

        this.setIcon(ConfigValue.create("Icon", new NightItem(Material.GOLDEN_HOE),
            "Set icon for the job.",
            Placeholders.URL_WIKI_ITEMS
        ).read(config));

        this.setPermissionRequired(ConfigValue.create("Permission_Required", false,
            "When enabled, players must have '" + this.getPermission() + "' permission in order to use this job."
        ).read(config));

        this.setProgressBarColor(ConfigValue.create("ProgressBar.Color",
            BarColor.class, BarColor.GREEN,
            "Sets color for this job progress bar.",
            "Allowed values: " + StringUtil.inlineEnum(BarColor.class, ", ")
        ).read(config));

        this.setInitialState(ConfigValue.create("Initial_State",
            JobState.class, JobState.INACTIVE,
            "Sets initial (start) job state for players that don't have a data for this job.",
            "This includes players joined the server for the first time, and all existent players if the job wasn't present on the server before.",
            "This might be useful if you want to grant players all jobs on first join or to predefine some of them.",
            URL_WIKI_JOB_STATES,
            "[*] This setting bypasses the job limits defined in the config.",
            "[Allowed values: " + StringUtil.inlineEnum(JobState.class, ", ") + "]",
            "[Default is " + JobState.INACTIVE.name() + "]"
        ).read(config));

        this.allowedStates.addAll(ConfigValue.forSet("Allowed_States",
            id -> StringUtil.getEnum(id, JobState.class).orElse(null),
            (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
            Lists.newSet(
                JobState.PRIMARY,
                JobState.SECONDARY,
                JobState.INACTIVE
            ),
            "List of allowed Job States allowed for this job.",
            "Removing " + JobState.INACTIVE.name() + " state will prevent players from leaving this job.",
            URL_WIKI_JOB_STATES,
            "[Allowed values: " + StringUtil.inlineEnum(JobState.class, ", ") + "]"
        ).read(config));

        this.setDisabledWorlds(ConfigValue.create("Disabled_Worlds",
            Lists.newSet("some_world", "another_world123"),
            "The job will have no effect in listed worlds (no XP and Income).",
            URL_WIKI_DISABLED_WORLDS
        ).read(config));

        this.setMaxLevel(ConfigValue.create("Leveling.Max_Level",
            100,
            "Defines max. possible job level if picked as Primary job.",
            URL_WIKI_LEVELING
        ).read(config));

        this.setMaxSecondaryLevel(ConfigValue.create("Leveling.Max_Secondary_Level",
            30,
            "Defines max. possible job level if picked as Secondary job.",
            URL_WIKI_LEVELING
        ).read(config));

        this.setInitialXP(ConfigValue.create("Leveling.XP_Initial",
            904,
            "Defines initial value for the geometric XP progression.",
            URL_WIKI_LEVELING
        ).read(config));

        this.setXPFactor(ConfigValue.create("Leveling.XP_Factor",
            1.09095309,
            "Defines step value for the geometric XP progression.",
            URL_WIKI_LEVELING
        ).read(config));

        this.rewards.load(config, "Leveling.Rewards");

        this.levelUpCommands.putAll(ConfigValue.forMap("Leveling.LevelUp_Commands",
            (key) -> NumberUtil.getInteger(key, 0),
            (cfg, path, key) -> cfg.getStringList(path + "." + key),
            (cfg, path, map) -> map.forEach((lvl, cmds) -> cfg.set(path + "." + lvl, cmds)),
            Map.of(),
            "[ OUTDATED , PLEASE USE LEVEL REWARDS INSTEAD ]"
        ).read(config));

        this.paymentDailyLimits.putAll(ConfigValue.forMapById("Daily_Limits.Currency",
            Modifier::read,
            map -> {
                map.put(CurrencyId.VAULT, Modifier.add(-1, 0, 0));
            },
            "Defines daily Income limits on per currency basis.",
            "You can use the '" + DEFAULT + "' keyword for all currencies that are not listed here.",
            URL_WIKI_DAILY_LIMITS,
            URL_WIKI_MODIFIERS
        ).read(config));

        // TODO Config option to excempt currencies from payment modifiers
        this.paymentMultiplier = ConfigValue.create("Payment_Modifier.Income",
            Modifier::read,
            JobUtils.getDefaultPaymentModifier(),
            "Defines Income bonus for job levels.",
            URL_WIKI_XP_INCOME_BONUS,
            URL_WIKI_MODIFIERS
        ).read(config);

        this.xpMultiplier = ConfigValue.create("Payment_Modifier.XP",
            Modifier::read,
            JobUtils.getDefaultXPModifier(),
            "Defines XP bonus for job levels.",
            URL_WIKI_XP_INCOME_BONUS,
            URL_WIKI_MODIFIERS
        ).read(config);

        this.xpDailyLimits = ConfigValue.create("Daily_Limits.XP",
            Modifier::read,
            Modifier.add(-1, 0, 0),
            "Defines daily XP limit.",
            URL_WIKI_DAILY_LIMITS,
            URL_WIKI_MODIFIERS
        ).read(config);

        if (Config.SPECIAL_ORDERS_ENABLED.get()) {
            this.specialOrdersAllowed = ConfigValue.create("SpecialOrder.Enabled",
                true,
                "Enables Special Orders feature for this job.",
                Placeholders.URL_WIKI_SPECIAL_ORDERS
            ).read(config);

            this.specialOrdersObjectivesAmount = ConfigValue.create("SpecialOrder.Objectives_Amount",
                UniInt::read,
                UniInt.of(1, 2),
                "Sets possible amount of objectives picked for Special Orders of this job."
            ).read(config);

            this.specialOrdersCompleteTime = ConfigValue.create("SpecialOrder.Time_To_Complete",
                UniInt::read,
                UniInt.of(14400, 43200),
                "Sets possible amount of completion time (in seconds) picked for Special Orders of this job."
            ).read(config);

            this.specialOrdersRewardsAmount = ConfigValue.create("SpecialOrder.Rewards_Amount",
                UniInt::read,
                UniInt.of(1, 3),
                "Sets possible amount of rewards picked for Special Orders of this job."
            ).read(config);

            this.specialOrdersAllowedRewards = new TreeMap<>(ConfigValue.forMap("SpecialOrder.Rewards_List",
                NumberUtil::getInteger,
                (cfg, path, def) -> cfg.getStringList(path + "." + def),
                (cfg, path, map) -> map.forEach((level, list) -> cfg.set(path + "." + level, list)),
                Map.of(1, Lists.newList(Placeholders.WILDCARD)),
                "A list of reward names available to use when generating Special Orders depens on job level.",
                "When picking rewards, it will get rewards with the greatest key less than or equal to the job level.",
                "You can create or edit Special Order rewards in config.yml",
                "You can put asterisk '" + Placeholders.WILDCARD + "' to include all possible rewards."
            ).read(config));

            this.specialOrdersCost = new HashMap<>(ConfigValue.forMap("SpecialOrder.Cost",
                CurrencyId::reroute,
                (cfg, path, key) -> cfg.getDouble(path + "." + key),
                (cfg, path, map) -> map.forEach((currencyId, amount) -> cfg.set(path + "." + currencyId, amount)),
                () -> Map.of(
                    CurrencyId.VAULT, 5000D
                ),
                "Sets amount of currency player have to pay to take a Special Order.",
                "Available currencies: " + Placeholders.URL_WIKI_ECONOMY
            ).read(config));
        }

        this.loadObjectives();

        return true;
    }

    public void loadObjectives() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, Config.DIR_JOBS + this.getId(), OBJECTIVES_CONFIG_NAME);
        config.options().setHeader(Lists.newList(
            "=".repeat(50),
            "For a list of available Types and acceptable Objects, please refer to " + Placeholders.URL_WIKI_WORK_TYPES,
            "For a list of available currencies, please refer to " + Placeholders.URL_WIKI_ECONOMY,
            "For a list of available Icon options, please refer to " + Placeholders.URL_WIKI_ITEMS,
            "=".repeat(50)
        ));

        for (String sId : config.getSection("")) {
            JobObjective objective = JobObjective.read(plugin, config, sId, sId);
            if (!this.validateObjective(objective, config)) continue;

            this.objectiveMap.put(objective.getId(), objective);
        }
        config.saveChanges();
    }

    private boolean validateObjective(@NotNull JobObjective objective, @NotNull FileConfig config) {
        String id = objective.getId();
        String fileName = "'" + config.getFile().getPath() + "' -> '" + id + "'";

        var workType = WorkRegistry.getByName(objective.getWorkId());
        if (workType == null) {
            plugin.error("Invalid objective type '" + objective.getWorkId() + "'. Found in " + fileName + ".");
            return false;
        }

        objective.getItems().forEach(objectId -> {
            if (objectId.equalsIgnoreCase(Placeholders.WILDCARD)) return;
            if (workType.parse(objectId) == null) {
                plugin.warn("Unknown object '" + objectId + "'. Found in " + fileName + ".");
            }
        });

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.name);
        config.set("Description", this.description);
        config.set("Icon", this.getIcon());
        config.set("Permission_Required", this.permissionRequired);
        config.set("ProgressBar.Color", this.progressBarColor.name());
        config.set("Initial_State", this.initialState.name());
        config.set("Disabled_Worlds", this.disabledWorlds);
        config.set("Leveling.Max_Level", this.maxLevel);
        config.set("Leveling.Max_Secondary_Level", this.maxSecondaryLevel);
        config.set("Leveling.XP_Initial", this.initialXP);
        config.set("Leveling.XP_Factor", this.xpFactor);
        config.set("Leveling.Rewards", this.rewards);

        config.remove("Leveling.LevelUp_Commands");
        this.getLevelUpCommands().forEach((level, list) -> {
            config.set("Leveling.LevelUp_Commands." + level, list);
        });

        config.remove("Payment_Modifier.Currency");
//        this.getPaymentMultiplier().forEach((id, mod) -> {
//            mod.write(config, "Payment_Modifier.Currency." + id);
//        });

        config.remove("Daily_Limits.Currency");
        this.getDailyPaymentLimits().forEach((id, mod) -> {
            mod.write(config, "Daily_Limits.Currency." + id);
        });

        config.set("Payment_Modifier.Money", this.paymentMultiplier);
        config.set("Payment_Modifier.XP", this.xpMultiplier);
        config.set("Daily_Limits.XP", this.xpDailyLimits);

        if (Config.SPECIAL_ORDERS_ENABLED.get() && this.getSpecialOrdersObjectivesAmount() != null) {
            config.remove("SpecialOrder.Rewards_List");
            config.set("SpecialOrder.Enabled", this.isSpecialOrdersAllowed());
            this.getSpecialOrdersObjectivesAmount().write(config, "SpecialOrder.Objectives_Amount");
            this.getSpecialOrdersCompleteTime().write(config, "SpecialOrder.Time_To_Complete");
            this.getSpecialOrdersRewardsAmount().write(config, "SpecialOrder.Rewards_Amount");
            this.getSpecialOrdersAllowedRewards().forEach((level, list) -> config.set("SpecialOrder.Rewards_List." + level, list));
            this.getSpecialOrdersCost().forEach((currencyId, amount) -> config.set("SpecialOrder.Cost." + currencyId, amount));
        }
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.JOB.replacer(this);
    }

    @NotNull
    public String getAbsolutePath() {
        return this.getFile().getParentFile().getAbsolutePath();
    }

    @NotNull
    public String getInternalPath() {
        return Config.DIR_JOBS + this.getId();
    }

    @NotNull
    public String getPermission() {
        return Perms.PREFIX_JOB + this.getId();
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.isPermissionRequired() || player.hasPermission(this.getPermission());
    }

    public boolean isGoodWorld(@NotNull World world) {
        return this.isGoodWorld(world.getName());
    }

    public boolean isGoodWorld(@NotNull String worldName) {
        return !this.getDisabledWorlds().contains(worldName.toLowerCase());
    }

    public boolean isAllowedState(@NotNull JobState state) {
        return this.allowedStates.contains(state);
    }

    public int getMaxLevel(@NotNull JobState state) {
        if (state == JobState.PRIMARY) {
            return this.getMaxLevel();
        }
        return this.getMaxSecondaryLevel();
    }

    public int getXPToLevel(int level) {
        return (int) (this.initialXP * (Math.pow(this.xpFactor, level)));
    }

    @NotNull
    public List<String> getLevelUpCommands(int level) {
        List<String> commands = new ArrayList<>();
        commands.addAll(this.levelUpCommands.getOrDefault(0, Collections.emptyList()));
        commands.addAll(this.levelUpCommands.getOrDefault(level, Collections.emptyList()));
        return commands;
    }

    public boolean canAffordSpecialOrder(@NotNull Player player) {
        return this.specialOrdersCost.entrySet().stream().allMatch(entry -> {
            Currency currency = EconomyBridge.getCurrency(entry.getKey());
            double amount = entry.getValue();

            return currency == null || currency.getBalance(player) >= amount;
        });
    }

    public void payForSpecialOrder(@NotNull Player player) {
        this.specialOrdersCost.forEach((currencyId, amount) -> {
            Currency currency = EconomyBridge.getCurrency(currencyId);
            if (currency == null) return;

            currency.take(player, amount);
        });
    }

    @Nullable
    public JobOrderData createSpecialOrder(int jobLevel) {
        if (!Config.SPECIAL_ORDERS_ENABLED.get()) return null;

        long duration = this.getSpecialOrdersCompleteTime().roll();
        if (duration <= 0L) {
            return null;
        }

        int objectiveAmount = this.getSpecialOrdersObjectivesAmount().roll();
        if (objectiveAmount <= 0) {
            return null;
        }


        Map<String, JobOrderObjective> objectiveMap = new HashMap<>();
        List<JobObjective> jobObjectives = new ArrayList<>(this.getObjectives());

        while (objectiveAmount > 0 && !jobObjectives.isEmpty()) {
            // Get random job objective.
            JobObjective jobObjective = jobObjectives.remove(Rnd.get(jobObjectives.size()));
            if (!jobObjective.isSpecialOrderAllowed()) continue;

            // Roll amount of objective elements to add in order's objective.
            int objectAmount = jobObjective.getSpecialOrderObjectsAmount().roll();
            if (objectAmount <= 0) continue;

            // Create counters for objects.
            Map<String, JobOrderCount> countMap = new HashMap<>();
            List<String> objects = new ArrayList<>(jobObjective.getItems());
            while (objectAmount > 0 && !objects.isEmpty()) {
                // Get random object from objective.
                String object = objects.remove(Rnd.get(objects.size()));

                // Roll 'required' amount.
                int objectRequired = jobObjective.getSpecialOrderObjectCount().roll();
                if (objectRequired <= 0) continue;

                // Add to order's objective count map.
                countMap.put(object.toLowerCase(), new JobOrderCount(objectRequired));
                objectAmount--;
            }
            if (countMap.isEmpty()) continue;

            // Add order objective to order data.
            JobOrderObjective orderObjective = new JobOrderObjective(jobObjective.getId(), countMap);
            objectiveMap.put(orderObjective.getObjectiveId(), orderObjective);
            objectiveAmount--;
        }

        if (objectiveMap.isEmpty()) {
            return null;
        }


        List<String> rewardNames = new ArrayList<>(this.getSpecialOrdersAllowedRewards(jobLevel));
        if (rewardNames.contains(Placeholders.WILDCARD)) {
            rewardNames = new ArrayList<>(Config.SPECIAL_ORDERS_REWARDS.get().keySet());
        }

        int rewardAmount = this.getSpecialOrdersRewardsAmount().roll();
        List<String> rewards = new ArrayList<>();
        while (rewardAmount > 0 && !rewardNames.isEmpty()) {
            String name = rewardNames.remove(Rnd.get(rewardNames.size()));
            rewards.add(name);
            rewardAmount--;
        }

        long expireDate = System.currentTimeMillis() + duration * 1000L;

        return new JobOrderData(objectiveMap, rewards, false, expireDate);
    }



//    public double getPaymentMultiplier(@NotNull Currency currency, int level) {
//        return this.getPaymentMultiplier(currency.getInternalId(), level);
//    }
//
//    public double getPaymentMultiplier(@NotNull String id, int level) {
//        Modifier scaler = this.getPaymentMultiplier().getOrDefault(id.toLowerCase(), this.getPaymentMultiplier().get(Placeholders.DEFAULT));
//        return scaler == null ? 0D : scaler.getValue(level);
//    }

    public double getPaymentMultiplier(int level) {
        return this.paymentMultiplier.getValue(level);
    }

    public boolean hasDailyPaymentLimit(@NotNull Currency currency, int level) {
        return this.hasDailyPaymentLimit(currency.getInternalId(), level);
    }

    public boolean hasDailyPaymentLimit(@NotNull String id, int level) {
        return this.getDailyPaymentLimit(id, level) > 0D;
    }

    public double getDailyPaymentLimit(@NotNull Currency currency, int level) {
        return this.getDailyPaymentLimit(currency.getInternalId(), level);
    }

    public double getDailyPaymentLimit(@NotNull String id, int level) {
        Modifier scaler = this.getDailyPaymentLimits().getOrDefault(id.toLowerCase(), this.getDailyPaymentLimits().get(Placeholders.DEFAULT));
        return scaler == null ? -1D : scaler.getValue(level);
    }



    public double getXPMultiplier(int level) {
        return this.getXPMultiplier().getValue(level);
    }

    public boolean hasDailyXPLimit(int level) {
        return this.getDailyXPLimit(level) > 0D;
    }

    public double getDailyXPLimit(int level) {
        return this.getDailyXPLimits().getValue(level);
    }


    @NotNull
    public List<? extends Work<?, ?>> getObjectiveWorkTypes() {
        return this.getObjectives().stream().map(JobObjective::getWork).filter(Objects::nonNull).distinct().sorted(Comparator.comparing(Work::getId)).toList();
    }

    @NotNull
    public Set<JobObjective> getObjectives() {
        return new HashSet<>(this.objectiveMap.values());
    }

    @NotNull
    public Set<JobObjective> getObjectives(@NotNull Work<?, ?> type) {
        return this.objectiveMap.values().stream().filter(objective -> objective.isWork(type)).collect(Collectors.toSet());
    }

//    @Deprecated
//    public <O> boolean hasObjective(@NotNull Work<?, O> type, @NotNull O objective) {
//        return this.getObjectiveByObject(type, objective) != null;
//    }

//    @Deprecated
//    public boolean hasObjective(@NotNull Work<?, ?> type, @NotNull String name) {
//        return this.getObjectiveByObject(type, name) != null;
//    }

    @Nullable
    public JobObjective getObjectiveById(@NotNull String id) {
        return this.objectiveMap.get(id.toLowerCase());
    }

//    @Nullable
//    @Deprecated
//    public <O> JobObjective getObjectiveByObject(@NotNull Work<?, O> type, @NotNull O object) {
//        return this.getObjectiveByObject(type, type.getObjectName(object));
//    }

//    @Nullable
//    @Deprecated
//    public JobObjective getObjectiveByObject(@NotNull Work<?, ?> type, @NotNull String name) {
//        return this.getObjectiveMap().values().stream()
//            .filter(objective -> objective.isWork(type) && objective.hasObject(name))
//            .findFirst().orElse(null);
//    }

    @Nullable
    public JobObjective getObjectiveByWork(@NotNull WorkObjective workObjective) {
        return this.objectiveMap.values().stream().filter(objective -> objective.isObjective(workObjective)).findFirst().orElse(null);
    }

    /**
     * This value is calculated by Statistics module.
     * If that module is disabled, this value will stay at 0.
     * @return Total amount of employees for this job.
     */
    public int getEmployees() {
        return this.getEmployeesAmount().values().stream().mapToInt(i -> i).sum();
    }

    public int getEmployeesAmount(@NotNull JobState state) {
        return this.getEmployeesAmount().getOrDefault(state, 0);
    }

    public void setEmployeesAmount(@NotNull JobState state, int amount) {
        this.getEmployeesAmount().put(state, Math.max(0, amount));
    }

    public void addEmployee(@NotNull JobState state, int amount) {
        this.setEmployeesAmount(state, this.getEmployeesAmount(state) + amount);
    }

    public void removeEmployee(@NotNull JobState state, int amount) {
        this.setEmployeesAmount(state, this.getEmployeesAmount(state) - amount);
    }

    @NotNull
    public Map<JobState, Integer> getEmployeesAmount() {
        return this.employeesAmount;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    public void setIcon(@NotNull NightItem icon) {
        this.icon = icon.copy();
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = Math.max(1, Math.abs(maxLevel));
    }

    public int getMaxSecondaryLevel() {
        return maxSecondaryLevel;
    }

    public void setMaxSecondaryLevel(int maxSecondaryLevel) {
        this.maxSecondaryLevel = Math.max(1, maxSecondaryLevel);
    }

    @NotNull
    public JobState getInitialState() {
        return initialState;
    }

    public void setInitialState(@NotNull JobState initialState) {
        this.initialState = initialState;
    }

    @NotNull
    public Set<JobState> getAllowedStates() {
        return allowedStates;
    }

    @NotNull
    public Set<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public void setDisabledWorlds(@NotNull Set<String> disabledWorlds) {
        this.getDisabledWorlds().clear();
        this.getDisabledWorlds().addAll(disabledWorlds.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }

    @NotNull
    public BarColor getProgressBarColor() {
        return progressBarColor;
    }

    public void setProgressBarColor(@NotNull BarColor progressBarColor) {
        this.progressBarColor = progressBarColor;
    }

    public int getInitialXP() {
        return initialXP;
    }

    public void setInitialXP(int initialXP) {
        this.initialXP = Math.max(1, Math.abs(initialXP));
    }

    public double getXPFactor() {
        return xpFactor;
    }

    public void setXPFactor(double xpFactor) {
        this.xpFactor = Math.max(1, xpFactor);
    }

    @NotNull
    public JobRewards getRewards() {
        return this.rewards;
    }

    @NotNull
    public Map<Integer, List<String>> getLevelUpCommands() {
        return levelUpCommands;
    }

//    @NotNull
//    public Map<String, Modifier> getPaymentMultiplier() {
//        return paymentMultiplier;
//    }


    @NotNull
    public Modifier getPaymentMultiplier() {
        return this.paymentMultiplier;
    }

    public void setPaymentMultiplier(@NotNull Modifier paymentMultiplier) {
        this.paymentMultiplier = paymentMultiplier;
    }

    @NotNull
    public Map<String, Modifier> getDailyPaymentLimits() {
        return paymentDailyLimits;
    }

    @NotNull
    public Modifier getXPMultiplier() {
        return xpMultiplier;
    }

    public void setXPMultiplier(@NotNull Modifier xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
    }

    @NotNull
    public Modifier getDailyXPLimits() {
        return xpDailyLimits;
    }

    public void setXPDailyLimits(@NotNull Modifier xpDailyLimits) {
        this.xpDailyLimits = xpDailyLimits;
    }

    @NotNull
    public Map<String, JobObjective> getObjectiveMap() {
        return objectiveMap;
    }

    public boolean isSpecialOrdersAllowed() {
        return this.specialOrdersAllowed;
    }

    public void setSpecialOrdersAllowed(boolean specialOrdersAllowed) {
        this.specialOrdersAllowed = specialOrdersAllowed;
    }

    public UniInt getSpecialOrdersObjectivesAmount() {
        return this.specialOrdersObjectivesAmount;
    }

    public void setSpecialOrdersObjectivesAmount(UniInt specialOrdersObjectivesAmount) {
        this.specialOrdersObjectivesAmount = specialOrdersObjectivesAmount;
    }

    public UniInt getSpecialOrdersCompleteTime() {
        return this.specialOrdersCompleteTime;
    }

    public void setSpecialOrdersCompleteTime(UniInt specialOrdersCompleteTime) {
        this.specialOrdersCompleteTime = specialOrdersCompleteTime;
    }

    public UniInt getSpecialOrdersRewardsAmount() {
        return specialOrdersRewardsAmount;
    }

    public void setSpecialOrdersRewardsAmount(UniInt specialOrdersRewardsAmount) {
        this.specialOrdersRewardsAmount = specialOrdersRewardsAmount;
    }

    public TreeMap<Integer, List<String>> getSpecialOrdersAllowedRewards() {
        return specialOrdersAllowedRewards;
    }

    public void setSpecialOrdersAllowedRewards(TreeMap<Integer, List<String>> specialOrdersAllowedRewards) {
        this.specialOrdersAllowedRewards = specialOrdersAllowedRewards;
    }

    public List<String> getSpecialOrdersAllowedRewards(int level) {
        var entry = this.getSpecialOrdersAllowedRewards().floorEntry(level);
        return entry == null ? new ArrayList<>() : entry.getValue();
    }

    public Map<String, Double> getSpecialOrdersCost() {
        return specialOrdersCost;
    }

    public void setSpecialOrdersCost(Map<String, Double> specialOrdersCost) {
        this.specialOrdersCost = specialOrdersCost;
    }
}
