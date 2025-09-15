package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.excellentjobs.job.reward.JobRewards;
import su.nightexpress.excellentjobs.progression.Progression;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static su.nightexpress.excellentjobs.Placeholders.*;

public class Job implements Writeable {

    public static final String CONFIG_NAME = "settings.yml";
    public static final String OBJECTIVES_CONFIG_NAME = "objectives.yml";

    private final JobsPlugin plugin;
    private final String id;

    private String       name;
    private List<String> description;
    private boolean      permissionRequired;
    private NightItem    icon;
    private JobState     initialState;
    private int          maxLevel;
    private int          initialXP;
    private double       xpFactor;
    private BarColor     progressBarColor;

    private List<String> joinCommands = new ArrayList<>();
    private List<String> leaveCommands = new ArrayList<>();

    private Bonus xpBonus = JobUtils.getDefaultXPBonus();
    private Bonus incomeBonus = JobUtils.getDefaultIncomeBonus();

    private Modifier     xpDailyLimits;
    private JobRewards rewards = JobRewards.getDefault();

    private final Set<JobState>              allowedStates;
    private final Set<String>                disabledWorlds;
    private final Map<JobState, Integer>     employeesAmount;
    private final Map<String, JobObjective> objectiveById;
    private final Map<String, Modifier>           paymentDailyLimits;

    public Job(@NotNull JobsPlugin plugin, @NotNull String id) {
        this.plugin = plugin;
        this.id = id;
        this.allowedStates = new HashSet<>();
        this.disabledWorlds = new HashSet<>();
        this.employeesAmount = new ConcurrentHashMap<>();
        this.paymentDailyLimits = new HashMap<>();

        this.objectiveById = new HashMap<>();

        this.setName(StringUtil.capitalizeUnderscored(id));
        this.setDescription(new ArrayList<>());
        this.setIcon(NightItem.fromType(Material.GOLDEN_HOE));
        this.setPermissionRequired(false);
        this.setInitialState(JobState.INACTIVE);
        this.setInitialXP(Progression.INITIAL_XP);
        this.setXPFactor(Progression.XP_FACTOR);
        this.setMaxLevel(Progression.DEFAULT_MAX_JOB_LEVEL);
        this.setProgressBarColor(BarColor.GREEN);
        this.paymentDailyLimits.put(CurrencyId.VAULT, Modifier.add(-1D, 0D, 0D));
        this.setXPDailyLimits(Modifier.add(-1D, 0D, 0D));
    }

    public void loadSettings(@NotNull FileConfig config) {
        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.id),
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
            "Allowed values: " + Enums.inline(BarColor.class)
        ).read(config));

        this.setInitialState(ConfigValue.create("Initial_State",
            JobState.class, JobState.INACTIVE,
            "Assigns the job with specified state for new players joined for the first time.",
            URL_WIKI_JOB_AUTO_JOIN,
            "[Allowed values: " + Enums.inline(JobState.class) + "]",
            "[Default is " + JobState.INACTIVE.name() + "]"
        ).read(config));

        this.allowedStates.addAll(ConfigValue.forSet("Allowed_States",
            id -> Enums.get(id, JobState.class),
            (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
            Lists.newSet(
                JobState.PRIMARY,
                JobState.SECONDARY,
                JobState.INACTIVE
            ),
            "Controls which states (priorities) are allowed for this job.",
            URL_WIKI_JOB_PRIORITY_LIMITS,
            "[Allowed values: " + Enums.inline(JobState.class) + "]"
        ).read(config));

        this.setDisabledWorlds(ConfigValue.create("Disabled_Worlds",
            Lists.newSet("some_world", "another_world123"),
            "The job will have no effect in listed worlds (no XP and Income).",
            URL_WIKI_DISABLED_WORLDS
        ).read(config));

        this.joinCommands = ConfigValue.create("General.JoinCommands", this.joinCommands, URL_WIKI_LEAVE_JOIN_COMMANDS).read(config);
        this.leaveCommands = ConfigValue.create("General.LeaveCommands",this.leaveCommands, URL_WIKI_LEAVE_JOIN_COMMANDS).read(config);

        this.setMaxLevel(ConfigValue.create("Leveling.Max_Level",
            100,
            "Defines max. possible job level if picked as Primary job.",
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

        this.rewards = ConfigValue.create("Leveling.Rewards", JobRewards::read, this.rewards,
            "Leveling rewards.",
            Placeholders.URL_WIKI_LEVEL_REWARDS
        ).read(config);

        this.paymentDailyLimits.putAll(ConfigValue.forMapById("Daily_Limits.Currency",
            Modifier::read,
            map -> map.put(CurrencyId.VAULT, Modifier.add(-1, 0, 0)),
            "Defines daily Income limits on per currency basis.",
            "You can use the '" + DEFAULT + "' keyword for all currencies that are not listed here.",
            URL_WIKI_DAILY_LIMITS,
            URL_WIKI_MODIFIERS
        ).read(config));

        if (config.contains("Payment_Modifier")) {
            Modifier income = ConfigValue.create("Payment_Modifier.Income", Modifier::read, JobUtils.getDefaultPaymentModifier()).read(config);
            Modifier xp = ConfigValue.create("Payment_Modifier.XP", Modifier::read, JobUtils.getDefaultXPModifier()).read(config);

            Modifier incomeSecond = Modifier.add(-0.6D, 0D, 1D);
            Modifier xpSecond = Modifier.add(-0.3D, 0D, 1D);

            config.set("Bonus.XP", new Bonus(xp, xpSecond));
            config.set("Bonus.Income", new Bonus(income, incomeSecond));
            config.remove("Payment_Modifier");
        }

        // TODO Config option to excempt currencies from payment bonus

        this.setXPBonus(ConfigValue.create("Bonus.XP", Bonus::read, this.xpBonus,
            "Sets XP bonus based on player's job state and level.",
            URL_WIKI_XP_INCOME_BONUS,
            URL_WIKI_MODIFIERS
        ).read(config));

        this.setIncomeBonus(ConfigValue.create("Bonus.Income", Bonus::read, this.incomeBonus,
            "Sets Income bonus based on player's job state and level.",
            URL_WIKI_XP_INCOME_BONUS,
            URL_WIKI_MODIFIERS
        ).read(config));

        this.xpDailyLimits = ConfigValue.create("Daily_Limits.XP",
            Modifier::read,
            Modifier.add(-1, 0, 0),
            "Defines daily XP limit.",
            URL_WIKI_DAILY_LIMITS,
            URL_WIKI_MODIFIERS
        ).read(config);

        this.loadGrindTables(config, "Objectives");
    }

    public void loadGrindTables(@NotNull FileConfig config, @NotNull String path) {
        config.getSection(path).forEach(sId -> {
            String objPath = path + "." + sId;
            String type = ConfigValue.create(objPath + ".Type", "null").read(config);
            GrindType<?> grindType = this.plugin.getGrindRegistry().getTypeById(type);
            if (grindType == null) {
                this.plugin.error("Unknown work type '" + type + "'. Found in '" + config.getFile().getPath() + "'.");
                return;
            }

            String currencyId = ConfigValue.create(objPath + ".Currency", CurrencyId.VAULT).read(config);
            GrindTable table = grindType.readTable(config, objPath + ".SourceTable");

            JobObjective objective = new JobObjective(currencyId, type, table);
            this.objectiveById.put(LowerCase.INTERNAL.apply(sId), objective);
        });
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.name);
        config.set(path + ".Description", this.description);
        config.set(path + ".Icon", this.getIcon());
        config.set(path + ".Permission_Required", this.permissionRequired);
        config.set(path + ".ProgressBar.Color", this.progressBarColor.name());
        config.set(path + ".Initial_State", this.initialState.name());
        config.set(path + ".Disabled_Worlds", this.disabledWorlds);
        config.set(path + ".General.JoinCommands", this.joinCommands);
        config.set(path + ".General.LeaveCommands", this.leaveCommands);
        config.set(path + ".Leveling.Max_Level", this.maxLevel);
        config.set(path + ".Leveling.XP_Initial", this.initialXP);
        config.set(path + ".Leveling.XP_Factor", this.xpFactor);
        config.set(path + ".Leveling.Rewards", this.rewards);

        config.remove(path + ".Daily_Limits.Currency");
        this.getDailyPaymentLimits().forEach((id, mod) -> mod.write(config, "Daily_Limits.Currency." + id));

        config.set(path + ".Bonus.XP", this.xpBonus);
        config.set(path + ".Bonus.Income", this.incomeBonus);
        config.set(path + ".Daily_Limits.XP", this.xpDailyLimits);

        config.remove(path + ".Objectives");
        this.objectiveById.forEach((id, objective) -> config.set(path + ".Objectives." + id, objective));
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.JOB.replacer(this);
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

    public boolean isJoinable() {
        return Stream.of(JobState.actives()).anyMatch(this::isAllowedState);
    }

    public boolean isLeaveable() {
        return this.isAllowedState(JobState.INACTIVE);
    }

    public int getXPToLevel(int level) {
        return (int) (this.initialXP * (Math.pow(this.xpFactor, level)));
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

    @NotNull
    public Bonus getXPBonus() {
        return this.xpBonus;
    }

    public void setXPBonus(@NotNull Bonus xpBonus) {
        this.xpBonus = xpBonus;
    }

    @NotNull
    public Bonus getIncomeBonus() {
        return this.incomeBonus;
    }

    public void setIncomeBonus(@NotNull Bonus incomeBonus) {
        this.incomeBonus = incomeBonus;
    }

    public boolean hasDailyXPLimit(int level) {
        return this.getDailyXPLimit(level) > 0D;
    }

    public double getDailyXPLimit(int level) {
        return this.getDailyXPLimits().getValue(level);
    }

    @NotNull
    public Map<String, JobObjective> getObjectivesById() {
        return this.objectiveById;
    }

    @NotNull
    public Set<JobObjective> getObjectiveTables(@NotNull GrindType<?> grindType) {
        return this.objectiveById.values().stream()
            .filter(objective -> objective.getGrindTypeId().equalsIgnoreCase(grindType.getId()))
            .collect(Collectors.toSet());
    }

    public void addObjective(@NotNull String id, @NotNull String grindTypeId, @NotNull GrindTable table) {
        this.addObjective(id, JobObjective.forVault(grindTypeId, table));
    }

    public void addObjective(@NotNull String id, @NotNull JobObjective objective) {
        this.objectiveById.put(LowerCase.INTERNAL.apply(id), objective);
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

    public void runJoinCommands(@NotNull Player player) {
        this.runCommands(player, this.joinCommands);
    }

    public void runLeaveCommands(@NotNull Player player) {
        this.runCommands(player, this.leaveCommands);
    }

    private void runCommands(@NotNull Player player, @NotNull List<String> commands) {
        Players.dispatchCommands(player, Lists.modify(commands, s -> this.replacePlaceholders().apply(s)));
    }

    @NotNull
    public Map<JobState, Integer> getEmployeesAmount() {
        return this.employeesAmount;
    }

    @NotNull
    public String getId() {
        return this.id;
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
    public List<String> getJoinCommands() {
        return this.joinCommands;
    }

    @NotNull
    public List<String> getLeaveCommands() {
        return this.leaveCommands;
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
    public Map<String, Modifier> getDailyPaymentLimits() {
        return paymentDailyLimits;
    }

    @NotNull
    public Modifier getDailyXPLimits() {
        return xpDailyLimits;
    }

    public void setXPDailyLimits(@NotNull Modifier xpDailyLimits) {
        this.xpDailyLimits = xpDailyLimits;
    }
}
