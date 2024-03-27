package su.nightexpress.excellentjobs.config;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.action.ActionTypes;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.config.BoosterInfo;
import su.nightexpress.excellentjobs.booster.config.RankBoosterInfo;
import su.nightexpress.excellentjobs.booster.config.TimedBoosterInfo;
import su.nightexpress.excellentjobs.currency.CurrencyManager;
import su.nightexpress.excellentjobs.job.impl.OrderReward;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.RankMap;
import su.nightexpress.nightcore.util.StringUtil;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class Config {

    public static final String DIR_MENU       = "/menu/";
    public static final String DIR_JOBS       = "/jobs/";
    public static final String DIR_ZONES      = "/zones/";

    public static final ConfigValue<DateTimeFormatter> GENERAL_TIME_FORMATTER = ConfigValue.create("General.Time_Format",
        (cfg, path, def) -> DateTimeFormatter.ofPattern(cfg.getString(path, "HH:mm")),
        (cfg, path, format) -> cfg.set(path, "HH:mm"),
        () -> DateTimeFormatter.ISO_LOCAL_TIME
    );

    public static final ConfigValue<Set<String>> GENERAL_DISABLED_WORLDS = ConfigValue.create("General.Disabled_Worlds",
        Set.of("my_world", "another_world"),
        "A list of worlds, where jobs will have no effect.");

    public static final ConfigValue<Boolean> GENERAL_DEFAULT_MENU_COMMAND_ENABLED = ConfigValue.create("General.Default_Menu_Command",
        true,
        "Sets whether or not '/jobs menu' command is set as default plugin command instead of '/jobs help' one.");

    public static final ConfigValue<Integer> GENERAL_PAYMENT_INTERVAL = ConfigValue.create("General.Payment.Interval",
        900,
        "Sets how often (in seconds) players will get payments for their work.",
        "Players will get instant payment when leaving server.",
        "[Default is 900 (15 minues)]");

    public static final ConfigValue<RankMap<Integer>> JOBS_PRIMARY_AMOUNT = ConfigValue.create("Jobs.Primary_Amount",
        (cfg, path, def) -> RankMap.readInt(cfg, path),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(RankMap.Mode.RANK, Perms.PREFIX_PRIMARY_JOBS, -1, Map.of(
            Placeholders.DEFAULT, -1
        )),
        "Sets how many primary jobs players with certain ranks or permissions can have at the same time.",
        "Use '-1' for unlimited amount."
    );

    public static final ConfigValue<RankMap<Integer>> JOBS_SECONDARY_AMOUNT = ConfigValue.create("Jobs.Secondary_Amount",
        (cfg, path, def) -> RankMap.readInt(cfg, path),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(RankMap.Mode.RANK, Perms.PREFIX_SECONDARY_JOBS, -1, Map.of(
            Placeholders.DEFAULT, -1
        )),
        "Sets how many secondary jobs players with certain ranks or permissions can have at the same time.",
        "Use '-1' for unlimited amount."
    );

    public static final ConfigValue<Boolean> JOBS_DAILY_LIMITS_RESET_MIDNIGHT = ConfigValue.create("Jobs.DailyLimits.Reset_At_Midnight",
        true,
        "Sets whether or not job's daily limits will expire right on midnight.",
        "When disabled, the whole 24 hours must be passed for limit to expire.");

    public static final ConfigValue<Boolean> JOBS_LEAVE_RESET_PROGRESS = ConfigValue.create("Jobs.Leave_Reset_Progress",
        true,
        "Sets whether or not leaving a job will reset all leveling progress for it.");

    public static final ConfigValue<Double> JOBS_ENCHANT_MULTIPLIER_BY_LEVEL_COST = ConfigValue.create("Jobs.Details.Enchant.Multiplier_By_Level_Cost",
        1D,
        "Sets amount of percents (%) added to XP and payment for each level in enchanting table cost for " + ActionTypes.ITEM_ENCHANT.getName() + " job objectives.",
        "Examples:",
        "==> With 30 level cost, player will gain 30% more XP and payment.",
        "==> With 7 level cost, player will gain 7% more XP and payment."
    );

    public static final ConfigValue<Boolean> ZONES_ENABLED = ConfigValue.create("Zones.Enabled",
        true,
        "Enables the Zones feature.",
        "When disabled, all Zones commands & features will be unavailable.");

    public static final ConfigValue<Boolean> ZONES_STRICT_MODE = ConfigValue.create("Zones.Strict_Mode",
        false,
        "When enabled, players will only get payments & Job XP when working inside Job Zones.");

    public static final ConfigValue<Boolean> ZONES_CONTROL_ENTRANCE = ConfigValue.create("Zones.Control_Entrance",
        true,
        "When enabled, prevents players when entering zones currently available for them.",
        "Examples: When player don't have permission to specific zone; when current server time is out of zone open times, etc.",
        "You can disable this setting if you're experiencing performance issues related to 'PlayerMoveEvent' from this plugin.",
        "Even if disabled, it still won't allow players to have zone bonuses and the whole ability to work there unless all conditions are met.");

    public static final ConfigValue<Integer> ZONES_REGENERATION_TASK_INTERVAL = ConfigValue.create("Zones.RegenerationTask.Interval",
        5,
        "Sets how often (in seconds) plugin will attempt to regnerate blocks in job zones.");

    public static final ConfigValue<Boolean> SPECIAL_ORDERS_ENABLED = ConfigValue.create("SpecialOrders.Enabled",
        true,
        "Sets whether or not Special Orders feature is enabled.",
        "Special Orders allows players to take daily randomized quests for their jobs.",
        "Players gets better rewards for completing Special Orders.");

    public static final ConfigValue<Integer> SPECIAL_ORDERS_MAX_AMOUNT = ConfigValue.create("SpecialOrders.Max_Amount",
        3,
        "Sets how many Special Orders player can have at the same time.",
        "Set '-1' for unlimited amount.");

    public static final ConfigValue<Long> SPECIAL_ORDERS_COOLDOWN = ConfigValue.create("SpecialOrders.Cooldown",
        86400L,
        "Sets amount of time (in seconds) that must be passed before player can take new Special Order for a job.",
        "Set '-1' to reset after midnight.");

    public static final ConfigValue<Map<String, OrderReward>> SPECIAL_ORDERS_REWARDS = ConfigValue.forMap("SpecialOrders.Rewards",
        (cfg, path, id) -> OrderReward.read(cfg, path + "." + id, id),
        (cfg, path, map) ->  map.values().forEach(reward -> reward.write(cfg, path + "." + reward.getId())),
        Map.of(
            "money_5000", new OrderReward("money_5000", "$5000", Lists.newList("eco give " + Placeholders.PLAYER_NAME + " 5000")),
            "money_10000", new OrderReward("money_10000", "$10000", Lists.newList("eco give " + Placeholders.PLAYER_NAME + " 10000")),
            "money_1500", new OrderReward("money_15000", "$15000", Lists.newList("eco give " + Placeholders.PLAYER_NAME + " 15000")),
            "item_ingots", new OrderReward("item_ingots", "Irong Ingot (x64), Gold Ingot (x64)", Lists.newList("give " + Placeholders.PLAYER_NAME + " iron_ingot 64", "give " + Placeholders.PLAYER_NAME + " gold_ingot 64"))
        ),
        "Here you can define all possible rewards for Special Orders feature.",
        "In job configuration you can specify which rewards from these will be available to use for that job's orders.",
        "For player name in commands use '" + Placeholders.PLAYER_NAME + "' placeholder.",
        "You can also use " + Plugins.PLACEHOLDER_API + " placeholders in commands."
    );

    public static final ConfigValue<Boolean> LEVELING_FIREWORKS = ConfigValue.create("Leveling.Fireworks", true,
        "Sets whether or not a random firework will be spawned above the player on job level up.");

    public static final ConfigValue<Set<CreatureSpawnEvent.SpawnReason>> ABUSE_IGNORE_SPAWN_REASONS = ConfigValue.forSet("Abuse_Protection.Ignore_SpawnReasons",
        raw -> StringUtil.getEnum(raw, CreatureSpawnEvent.SpawnReason.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Set.of(
            CreatureSpawnEvent.SpawnReason.EGG,
            CreatureSpawnEvent.SpawnReason.SPAWNER,
            CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
            CreatureSpawnEvent.SpawnReason.DISPENSE_EGG,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            CreatureSpawnEvent.SpawnReason.SLIME_SPLIT
        ),
        "Mobs spawned by the following reasons will give no job XP / currency.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html"
    );

    public static final ConfigValue<Set<GameMode>> ABUSE_IGNORE_GAME_MODES = ConfigValue.forSet("Abuse_Protection.Ignore_GameModes",
        raw -> StringUtil.getEnum(raw, GameMode.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Set.of(GameMode.CREATIVE),
        "A list of player GameModes where no job XP / currency will be given.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/GameMode.html"
    );

    public static final ConfigValue<Set<Material>> ABUSE_IGNORE_BLOCK_GENERATION = ConfigValue.forSet("Abuse_Protection.Ignore_Block_Generation",
        raw -> Material.getMaterial(raw.toUpperCase()),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Set.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.OBSIDIAN
        ),
        "The following blocks generated/formed by world mechanics will give no job XP / currency.",
        "Take block names from F3 debug screen wihout the 'minecraft:' prefix."
    );

    public static final ConfigValue<Set<Material>> ABUSE_IGNORE_FERTILIZED = ConfigValue.forSet("Abuse_Protection.Ignore_Fertilized",
        raw -> Material.getMaterial(raw.toUpperCase()),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Set.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS
        ),
        "The following blocks will give no job XP / currency if been fertilized by bone meal.",
        "Take block names from F3 debug screen wihout the 'minecraft:' prefix."
    );

    public static final ConfigValue<Boolean> STATISTIC_ENABLED = ConfigValue.create("Statistic.Enabled",
        true,
        "Sets whether or not Statistics module is enabled.",
        "When disabled, all job statistics including top stats will be unavailable.");

    public static final ConfigValue<Integer> STATISTIC_UPDATE_INTERVAL = ConfigValue.create("Statistic.Update_Interval", 600,
        "Sets how often (in seconds) statistic will be fetched and updated.");

    public static final ConfigValue<Integer> STATISTIC_ENTRIES_PER_PAGE = ConfigValue.create("Statistic.Entries_Per_Page", 10,
        "Sets how many entries per leaderboard page will be displated.");

    public static final ConfigValue<Map<String, TimedBoosterInfo>> BOOSTERS_GLOBAL = ConfigValue.forMap("Boosters.Global",
        (cfg, path, id) -> TimedBoosterInfo.read(cfg, path + "." + id),
        (cfg, path, map) -> map.forEach((id, info) -> info.write(cfg, path + "." + id)),
        Map.of(
            "example", new TimedBoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 25D), 25D),
                Map.of(DayOfWeek.SATURDAY, Set.of(LocalTime.of(16, 0))), 7200)
        ),
        "List of global, automated XP / currency boosters.",
        "You can create as many boosters as you want.",
        "But keep in mind that only one global booster can be active at the same time.",
        "If you have multiple boosters applicable at the same day times, the latest one will override all previous."
    );

    public static final ConfigValue<Map<String, RankBoosterInfo>> BOOSTERS_RANK = ConfigValue.forMap("Boosters.Rank",
        (cfg, path, id) -> RankBoosterInfo.read(cfg, path + "." + id, id),
        (cfg, path, map) -> map.forEach((id, info) -> info.write(cfg, path + "." + id)),
        Map.of(
            "vip", new RankBoosterInfo("vip", 10, Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 25D), 25D)
            ),
            "premium", new RankBoosterInfo("premium", 10, Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 50D), 50D)
            )
        ),
        "List of passive XP / currency boosters based on player permission group(s).",
        "Use the 'Priority' option to define booster's priority to guarantee that players with multiple permission groups will get the best one."
    );

    public static final ConfigValue<Map<String, BoosterInfo>> BOOSTERS_CUSTOM = ConfigValue.forMap("Boosters.Custom",
        (cfg, path, id) -> BoosterInfo.read(cfg, path + "." + id),
        (cfg, path, map) -> map.forEach((id, info) -> info.write(cfg, path + "." + id)),
        Map.of(
            "xp_money_25", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 25D), 25D)),
            "xp_money_50", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 50D), 50D)),
            "money_100", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 100D), 0D)),
            "xp_100", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyManager.ID_MONEY, 0D), 100D))
        ),
        "List of custom XP / currency boosters to be given via booster commands.",
        "You can create as many boosters as you want.",
        "But keep in mind that only one personal booster per job can be active at the same time.",
        "If player already has a booster for as job, it will be replaced with a new one."
    );
}
