package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.currency.handler.VaultEconomyHandler;
import su.nightexpress.excellentjobs.data.impl.JobOrderCount;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobOrderObjective;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static su.nightexpress.excellentjobs.Placeholders.PLAYER_NAME;

public class Job extends AbstractFileData<JobsPlugin> implements Placeholder {

    public static final String CONFIG_NAME = "settings.yml";
    public static final String OBJECTIVES_CONFIG_NAME = "objectives.yml";

    private String       name;
    private List<String> description;
    private boolean      permissionRequired;
    private ItemStack    icon;
    private JobState     initialState;
    private int          maxLevel;
    private int          maxSecondaryLevel;
    private int          initialXP;
    private double       xpFactor;
    private Modifier     xpMultiplier;
    private Modifier     xpDailyLimits;

    private boolean                        specialOrdersAllowed;
    private UniInt                         specialOrdersObjectivesAmount;
    private UniInt                         specialOrdersCompleteTime;
    private UniInt                         specialOrdersRewardsAmount;
    private TreeMap<Integer, List<String>> specialOrdersAllowedRewards;
    private Map<Currency, Double>          specialOrdersCost;

    private final Set<String> disabledWorlds;
    private final Map<JobState, Integer> employeesAmount;
    private final TreeMap<Integer, Integer>  xpTable;
    private final Map<Integer, List<String>> levelUpCommands;
    private final Map<String, Modifier>      paymentMultiplier;
    private final Map<String, Modifier>      paymentDailyLimits;
    private final Map<String, JobObjective>  objectiveMap;
    private final PlaceholderMap             placeholderMap;

    public Job(@NotNull JobsPlugin plugin, @NotNull File file, @NotNull String id) {
        super(plugin, file, id);
        this.disabledWorlds = new HashSet<>();
        this.employeesAmount = new ConcurrentHashMap<>();
        this.xpTable = new TreeMap<>();
        this.levelUpCommands = new HashMap<>();
        this.paymentMultiplier = new HashMap<>();
        this.paymentDailyLimits = new HashMap<>();
        this.objectiveMap = new HashMap<>();
        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.JOB_ID, this::getId)
            .add(Placeholders.JOB_NAME, this::getName)
            .add(Placeholders.JOB_DESCRIPTION, () -> String.join("\n", this.getDescription()))
            .add(Placeholders.JOB_PERMISSION_REQUIRED, () -> CoreLang.getYesOrNo(this.isPermissionRequired()))
            .add(Placeholders.JOB_PERMISSION_NODE, this::getPermission)
            .add(Placeholders.JOB_MAX_LEVEL, () -> NumberUtil.format(this.getMaxLevel()))
            .add(Placeholders.JOB_MAX_SECONDARY_LEVEL, () -> NumberUtil.format(this.getMaxSecondaryLevel()))
            .add(Placeholders.JOB_EMPLOYEES_TOTAL, () -> NumberUtil.format(this.getEmployees()))
            .add(Placeholders.JOB_EMPLOYEES_PRIMARY, () -> NumberUtil.format(this.getEmployeesAmount(JobState.PRIMARY)))
            .add(Placeholders.JOB_EMPLOYEES_SECONDARY, () -> NumberUtil.format(this.getEmployeesAmount(JobState.SECONDARY)));
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        if (!ConfigValue.create("Enabled", true).read(config)) return false;

        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId()),
            "Job display name.",
            Placeholders.WIKI_TEXT_URL
        ).read(config));

        this.setDescription(ConfigValue.create("Description", new ArrayList<>(),
            "Job description.",
            Placeholders.WIKI_TEXT_URL
        ).read(config));

        this.setIcon(ConfigValue.create("Icon", new ItemStack(Material.GOLDEN_HOE),
            "Job icon.",
            Placeholders.WIKI_TEXT_URL
        ).read(config));

        this.setPermissionRequired(ConfigValue.create("Permission_Required", false,
            "When enabled, players must have '" + this.getPermission() + "' permission in order to use this job."
        ).read(config));

        this.setInitialState(ConfigValue.create("Initial_State",
            JobState.class, JobState.INACTIVE,
            "Sets initial (start) job state for new players and new jobs.",
            "This means that, when a new player joins the server, OR when there is a new job created for existent users,",
            "it will be assigned to a player with specified state.",
            "This might be useful if you want to grant players all jobs on first join or to predefine some of them.",
            "Also, keep in mind that this setting bypasses the max jobs values defined in the config.yml!",
            "[Allowed values: " + StringUtil.inlineEnum(JobState.class, ", ") + "]",
            "[Default is " + JobState.INACTIVE.name() + "]"
        ).read(config));

        this.setDisabledWorlds(ConfigValue.create("Disabled_Worlds",
            Set.of("some_world"),
            "A list of worlds where this job will have no effect (no XP, no payments)."
        ).read(config));

        this.setMaxLevel(ConfigValue.create("Leveling.Max_Level", 100,
            "Max. possible job level."
        ).read(config));

        this.setMaxSecondaryLevel(ConfigValue.create("Leveling.Max_Secondary_Level", 30,
            "Max. possible job level when job is set as 'Secondary' for a player."
        ).read(config));

        this.setInitialXP(ConfigValue.create("Leveling.XP_Initial", 100,
            "Sets start amount of XP required for the next level."
        ).read(config));

        this.setXPFactor(ConfigValue.create("Leveling.XP_Factor", 1.093,
            "Sets XP multiplier to calculate XP amount required for next level.",
            "The formula is: <xp_required> = <previous_xp_required> * <xp_factor>"
        ).read(config));

        for (int level = 1; level < (this.getMaxLevel() + 1); level++) {
            int xpPrevious = this.xpTable.getOrDefault(level - 1, this.getInitialXP());
            int xpToLevel = level == 1 ? this.getInitialXP() : (int) (xpPrevious * this.getXPFactor());
            this.xpTable.put(level, xpToLevel);
        }

        this.levelUpCommands.putAll(ConfigValue.forMap("Leveling.LevelUp_Commands",
            (key) -> NumberUtil.getInteger(key, 0),
            (cfg, path, key) -> cfg.getStringList(path + "." + key),
            (cfg, path, map) -> map.forEach((lvl, cmds) -> cfg.set(path + "." + lvl, cmds)),
            Map.of(
                0, Lists.newList("eco give " + PLAYER_NAME + " 250", "feed " + PLAYER_NAME)
            ),
            "A list of commands to execute when player reaches certain level(s).",
            "Key = Level reached.",
            "Use '0' as a level to run command(s) on every level up."
        ).read(config));

        this.paymentMultiplier.putAll(ConfigValue.forMap("Payment_Modifier.Currency",
            String::toLowerCase,
            (cfg, path, key) -> Modifier.read(cfg, path + "." + key),
            (cfg, path, map) -> map.forEach((id, mod) -> mod.write(cfg, path + "." + id)),
            () -> Map.of(
                VaultEconomyHandler.ID, Modifier.add(1.00, 0.01, 1)
            ),
            "Sets payment multipliers for each currency adjustable by player's job level.",
            "You can use '" + Placeholders.DEFAULT + "' keyword for all currencies not included here."
        ).read(config));

        this.paymentDailyLimits.putAll(ConfigValue.forMap("Daily_Limits.Currency",
            String::toLowerCase,
            (cfg, path, key) -> Modifier.read(cfg, path + "." + key),
            (cfg, path, map) -> map.forEach((id, mod) -> mod.write(cfg, path + "." + id)),
            () -> Map.of(
                VaultEconomyHandler.ID, Modifier.add(-1, 0, 0)
            ),
            "Sets payment daily limits for each currency adjustable by player's job level.",
            "You can use '" + Placeholders.DEFAULT + "' keyword for all currencies not included here."
        ).read(config));

        this.xpMultiplier = Modifier.read(config, "Payment_Modifier.XP",
            Modifier.add(1.00, 0.01, 5),
            "Sets job's objective XP multiplier adjustable by player's job level."
        );

        this.xpDailyLimits = Modifier.read(config, "Daily_Limits.XP",
            Modifier.add(-1, 0, 0),
            "Sets job's objective XP daily limit adjustable by player's job level."
        );

        if (Config.SPECIAL_ORDERS_ENABLED.get()) {
            this.specialOrdersAllowed = ConfigValue.create("SpecialOrder.Enabled",
                true,
                "Enables Special Orders feature for this job.",
                Placeholders.URL_WIKI_SPECIAL_ORDERS
            ).read(config);

            this.specialOrdersObjectivesAmount = ConfigValue.create("SpecialOrder.Objectives_Amount",
                (cfg2, path2, def) -> UniInt.read(cfg2, path2),
                (cfg2, path2, obj) -> obj.write(cfg2, path2),
                () -> UniInt.of(1, 2),
                "Sets possible amount of objectives picked for Special Orders of this job."
            ).read(config);

            this.specialOrdersCompleteTime = ConfigValue.create("SpecialOrder.Time_To_Complete",
                (cfg, path, def) -> UniInt.read(cfg, path),
                (cfg, path, obj) -> obj.write(cfg, path),
                () -> UniInt.of(14400, 43200),
                "Sets possible amount of completion time (in seconds) picked for Special Orders of this job."
            ).read(config);

            this.specialOrdersRewardsAmount = ConfigValue.create("SpecialOrder.Rewards_Amount",
                (cfg, path, def) -> UniInt.read(cfg, path),
                (cfg, path, obj) -> obj.write(cfg, path),
                () -> UniInt.of(1, 3),
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
                id -> plugin.getCurrencyManager().getCurrency(id),
                (cfg, path, key) -> cfg.getDouble(path + "." + key),
                (cfg, path, map) -> map.forEach((currency, amount) -> cfg.set(path + "." + currency.getId(), amount)),
                () -> Map.of(
                    plugin.getCurrencyManager().getCurrencyOrAny(VaultEconomyHandler.ID), 5000D
                ),
                "Sets amount of currency player have to pay to take a Special Order.",
                "Available currencies: " + Placeholders.URL_WIKI_CURRENCY
            ).read(config));
        }

        this.loadObjectives();

        return true;
    }

    public void loadObjectives() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, Config.DIR_JOBS + this.getId(), OBJECTIVES_CONFIG_NAME);
        config.options().setHeader(Lists.newList(
            "=".repeat(50),
            "For a list of available Types and acceptable Objects, please refer to " + Placeholders.URL_WIKI_ACTION_TYPES,
            "For a list of available currencies, please refer to " + Placeholders.URL_WIKI_CURRENCY,
            "For a list of available Icon options, please refer to " + Placeholders.WIKI_ITEMS_URL,
            "=".repeat(50)
        ));

        for (String sId : config.getSection("")) {
            JobObjective objective = JobObjective.read(plugin, config, sId, sId);
            if (objective == null) {
                this.plugin.warn("Could not load '" + sId + "' objective in '" + getId() + "' job! File: " + config.getFile().getName());
                continue;
            }
            this.getObjectiveMap().put(objective.getId(), objective);
        }
        config.saveChanges();
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.getName());
        config.set("Description", this.getDescription());
        config.setItem("Icon", this.getIcon());
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("Initial_State", this.getInitialState().name());
        config.set("Disabled_Worlds", this.getDisabledWorlds());
        config.set("Leveling.Max_Level", this.getMaxLevel());
        config.set("Leveling.Max_Secondary_Level", this.getMaxSecondaryLevel());
        config.set("Leveling.XP_Initial", this.getInitialXP());
        config.set("Leveling.XP_Factor", this.getXPFactor());
        config.remove("Leveling.LevelUp_Commands");
        this.getLevelUpCommands().forEach((level, list) -> {
            config.set("Leveling.LevelUp_Commands." + level, list);
        });
        config.remove("Payment_Modifier.Currency");
        this.getPaymentMultiplier().forEach((id, mod) -> {
            mod.write(config, "Payment_Modifier.Currency." + id);
        });
        config.remove("Daily_Limits.Currency");
        this.getDailyPaymentLimits().forEach((id, mod) -> {
            mod.write(config, "Daily_Limits.Currency." + id);
        });

        this.getXPMultiplier().write(config, "Payment_Modifier.XP");
        this.getDailyXPLimits().write(config, "Daily_Limits.XP");

        if (Config.SPECIAL_ORDERS_ENABLED.get() && this.getSpecialOrdersObjectivesAmount() != null) {
            config.remove("SpecialOrder.Rewards_List");
            config.set("SpecialOrder.Enabled", this.isSpecialOrdersAllowed());
            this.getSpecialOrdersObjectivesAmount().write(config, "SpecialOrder.Objectives_Amount");
            this.getSpecialOrdersCompleteTime().write(config, "SpecialOrder.Time_To_Complete");
            this.getSpecialOrdersRewardsAmount().write(config, "SpecialOrder.Rewards_Amount");
            this.getSpecialOrdersAllowedRewards().forEach((level, list) -> config.set("SpecialOrder.Rewards_List." + level, list));
            this.getSpecialOrdersCost().forEach((currency, amount) -> config.set("SpecialOrder.Cost." + currency.getId(), amount));
        }
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
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
        return player.hasPermission(this.getPermission());
    }

    public boolean isGoodWorld(@NotNull World world) {
        return this.isGoodWorld(world.getName());
    }

    public boolean isGoodWorld(@NotNull String worldName) {
        return !this.getDisabledWorlds().contains(worldName.toLowerCase());
    }

    public int getMaxLevel(@NotNull JobState state) {
        if (state == JobState.PRIMARY) {
            return this.getMaxLevel();
        }
        return this.getMaxSecondaryLevel();
    }

    public int getXPToLevel(int level) {
        Map.Entry<Integer, Integer> entry = this.getXPTable().floorEntry(level);
        return entry != null ? entry.getValue() : this.getInitialXP();
    }

    @NotNull
    public List<String> getLevelUpCommands(int level) {
        List<String> commands = new ArrayList<>();
        commands.addAll(this.levelUpCommands.getOrDefault(0, Collections.emptyList()));
        commands.addAll(this.levelUpCommands.getOrDefault(level, Collections.emptyList()));
        return commands;
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
            List<String> objects = new ArrayList<>(jobObjective.getObjects());
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



    public double getPaymentMultiplier(@NotNull Currency currency, int level) {
        return this.getPaymentMultiplier(currency.getId(), level);
    }

    public double getPaymentMultiplier(@NotNull String id, int level) {
        Modifier scaler = this.getPaymentMultiplier().getOrDefault(id.toLowerCase(), this.getPaymentMultiplier().get(Placeholders.DEFAULT));
        return scaler == null ? 0D : scaler.getValue(level);
    }

    public boolean hasDailyPaymentLimit(@NotNull Currency currency, int level) {
        return this.hasDailyPaymentLimit(currency.getId(), level);
    }

    public boolean hasDailyPaymentLimit(@NotNull String id, int level) {
        return this.getDailyPaymentLimit(id, level) > 0D;
    }

    public double getDailyPaymentLimit(@NotNull Currency currency, int level) {
        return this.getDailyPaymentLimit(currency.getId(), level);
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
    public Collection<JobObjective> getObjectives() {
        return this.getObjectiveMap().values();
    }

    @NotNull
    public Collection<JobObjective> getObjectives(@NotNull ActionType<?, ?> type) {
        return this.getObjectives().stream().filter(objective -> objective.getType() == type).collect(Collectors.toSet());
    }

    public <O> boolean hasObjective(@NotNull ActionType<?, O> type, @NotNull O objective) {
        return this.getObjectiveByObject(type, objective) != null;
    }

    public boolean hasObjective(@NotNull ActionType<?, ?> type, @NotNull String name) {
        return this.getObjectiveByObject(type, name) != null;
    }

    @Nullable
    public JobObjective getObjectiveById(@NotNull String id) {
        return this.getObjectiveMap().get(id.toLowerCase());
    }

    @Nullable
    public <O> JobObjective getObjectiveByObject(@NotNull ActionType<?, O> type, @NotNull O object) {
        return this.getObjectiveByObject(type, type.getObjectName(object));
    }

    @Nullable
    public JobObjective getObjectiveByObject(@NotNull ActionType<?, ?> type, @NotNull String name) {
        return this.getObjectiveMap().values().stream()
            .filter(objective -> objective.getType() == type && objective.hasObject(name))
            .findFirst().orElse(null);
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
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
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
    public Set<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public void setDisabledWorlds(@NotNull Set<String> disabledWorlds) {
        this.getDisabledWorlds().clear();
        this.getDisabledWorlds().addAll(disabledWorlds.stream().map(String::toLowerCase).collect(Collectors.toSet()));
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
    public TreeMap<Integer, Integer> getXPTable() {
        return xpTable;
    }

    @NotNull
    public Map<Integer, List<String>> getLevelUpCommands() {
        return levelUpCommands;
    }

    @NotNull
    public Map<String, Modifier> getPaymentMultiplier() {
        return paymentMultiplier;
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

    public Map<Currency, Double> getSpecialOrdersCost() {
        return specialOrdersCost;
    }

    public void setSpecialOrdersCost(Map<Currency, Double> specialOrdersCost) {
        this.specialOrdersCost = specialOrdersCost;
    }
}
