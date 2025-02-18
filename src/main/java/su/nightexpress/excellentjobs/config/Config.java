package su.nightexpress.excellentjobs.config;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.action.ActionTypes;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.config.BoosterInfo;
import su.nightexpress.excellentjobs.booster.config.RankBoosterInfo;
import su.nightexpress.excellentjobs.booster.config.TimedBoosterInfo;
import su.nightexpress.excellentjobs.job.impl.OrderReward;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

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

    public static final ConfigValue<Boolean> GENERAL_PROGRESS_BAR_ENABLED = ConfigValue.create("General.ProgressBar.Enabled",
        true,
        "Enables boss bar indicating gained job XP and future income for the latest X seconds.");

    public static final ConfigValue<Integer> GENERAL_PROGRESS_BAR_STAY_TIME = ConfigValue.create("General.ProgressBar.StayTime",
        8,
        "Sets for how long (in seconds) progress bar will stay before reset and disappear.");

    public static final ConfigValue<String> GENERAL_PROGRESS_BAR_TITLE = ConfigValue.create("General.ProgressBar.Title",
        GRAY.enclose(LIGHT_YELLOW.enclose(BOLD.enclose(JOB_NAME + " Job")) + " (Lv. " + WHITE.enclose(JOB_DATA_LEVEL) + ") | " + LIGHT_RED.enclose("+" + GENERIC_XP + " XP") + " | " + LIGHT_GREEN.enclose("+" + GENERIC_INCOME)),
        "Sets title for job progress bar.",
        "You can use 'Job' placeholders (not all of them): " + URL_WIKI_PLACEHOLDERS
    );

    public static final ConfigValue<BarStyle> GENERAL_PROGRESS_BAR_STYLE = ConfigValue.create("General.ProgressBar.Style",
        BarStyle.class, BarStyle.SOLID,
        "Sets style for job progress bar.",
        "Allowed values: " + StringUtil.inlineEnum(BarStyle.class, ", ")
    );

    public static final ConfigValue<String> PLACEHOLDERS_JOBS_DELIMITER = ConfigValue.create("Placeholders.Jobs.Delimiter",
        ", ",
        "Sets delimiter for placeholders listing job name(s)."
    );

    public static final ConfigValue<String> PLACEHOLDERS_JOBS_FALLBACK = ConfigValue.create("Placeholders.Jobs.Fallback",
        GRAY.enclose("<No Jobs>"),
        "Sets fallback text for job listing placeholders if player has no jobs."
    );

    public static final ConfigValue<RankMap<Integer>> JOBS_PRIMARY_AMOUNT = ConfigValue.create("Jobs.Primary_Amount",
        (cfg, path, def) -> RankMap.readInt(cfg, path, -1),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(RankMap.Mode.RANK, Perms.PREFIX_PRIMARY_JOBS, -1, Map.of(
            Placeholders.DEFAULT, -1
        )),
        "Sets how many primary jobs players with certain ranks or permissions can have at the same time.",
        "Use '-1' for unlimited amount."
    );

    public static final ConfigValue<RankMap<Integer>> JOBS_SECONDARY_AMOUNT = ConfigValue.create("Jobs.Secondary_Amount",
        (cfg, path, def) -> RankMap.readInt(cfg, path, -1),
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

    public static final ConfigValue<Boolean> JOBS_FORCE_LEAVE_WHEN_LOST_PERMISSION = ConfigValue.create("Jobs.Leave_When_Lost_Permission",
        true,
        "Sets whether or not players will lost their jobs for which they don't have permission(s) anymore.");

    public static final ConfigValue<Double> JOBS_ENCHANT_MULTIPLIER_BY_LEVEL_COST = ConfigValue.create("Jobs.Details.Enchant.Multiplier_By_Level_Cost",
        1D,
        "Sets amount of percents (%) added to a job's objective XP and payment for each level in enchanting table cost for " + ActionTypes.ITEM_ENCHANT.getName() + " job objectives.",
        "Examples:",
        "==> With 30 level cost, player will gain 30% more XP and payment.",
        "==> With 7 level cost, player will gain 7% more XP and payment."
    );

    public static final ConfigValue<Boolean> LEVELLED_MOBS_KILL_ENTITY_ENABLED = ConfigValue.create("LevelledMobs.Integration.KillEntity.Enabled",
        true,
        "When enabled, multiplies XP and payment amount produced by '" + ActionTypes.ENTITY_KILL.getName() + "' and " + ActionTypes.ENTITY_SHOOT.getName() + "' job objectives when killing mobs with levels from LevelledMobs."
    );

    public static final ConfigValue<Double> LEVELLED_MOBS_KILL_ENTITY_MULTIPLIER = ConfigValue.create("LevelledMobs.Integration.KillEntity.Multiplier",
        1D,
        "Sets percent amount (%) added to a job's objective XP and payment for each mob level.",
        "Examples:",
        "==> With value = 1, a mob with lvl 30 will produce 30% more job XP and payments.",
        "==> With value = 0.5, a mob with lvl 30 will produce 15% more job XP and payments."
    );

    public static final ConfigValue<Boolean> ZONES_ENABLED = ConfigValue.create("Zones.Enabled",
        true,
        "Enables the Zones feature.",
        "When disabled, all zones related commands & features will be unavailable."
    );

    public static final ConfigValue<Boolean> ZONES_STRICT_MODE = ConfigValue.create("Zones.Strict_Mode",
        false,
        "When enabled, players will only get payments & Job XP when working inside Job Zones."
    );

    public static final ConfigValue<Boolean> ZONES_CONTROL_ENTRANCE = ConfigValue.create("Zones.Control_Entrance",
        true,
        "When enabled, prevents players from entering zones that are not available for them.",
        "Examples: When player don't have permission to specific zone; when current server time is not in zone hours, etc.",
        "You can disable this setting if you're experiencing performance issues related to the 'PlayerMoveEvent' from this plugin.",
        "Even if disabled, it still won't allow players to have zone bonuses and the whole ability to work there unless all conditions are met."
    );

    public static final ConfigValue<ItemStack> ZONES_WAND_ITEM = ConfigValue.create("Zones.WandItem",
        JobUtils.getDefaultZoneWand(),
        "Item used to define zone's cuboid."
    );

    public static final ConfigValue<Integer> ZONES_REGENERATION_TASK_INTERVAL = ConfigValue.create("Zones.RegenerationTask.Interval",
        5,
        "Sets how often (in seconds) plugin will attempt to regnerate blocks in job zones."
    );

    public static final ConfigValue<Material> ZONES_HIGHLIGHT_BLOCK_CORNER = ConfigValue.create("Zones.Highlighting.CornerBlock",
        Material.class,
        Material.WHITE_STAINED_GLASS,
        "Block type used for a fake block display entity for zone selection's corners.",
        "[Default is " + BukkitThing.toString(Material.WHITE_STAINED_GLASS) + "]"
    );

    public static final ConfigValue<Material> ZONES_HIGHLIGHT_BLOCK_WIRE = ConfigValue.create("Zones.Highlighting.WireBlock",
        Material.class,
        Material.CHAIN,
        "Block type used for a fake block display entity for zone selection's corners connections.",
        "[Default is " + BukkitThing.toString(Material.CHAIN) + "]"
    );

    public static final ConfigValue<Boolean> SPECIAL_ORDERS_ENABLED = ConfigValue.create("SpecialOrders.Enabled",
        true,
        "Sets whether or not Special Orders feature is enabled.",
        "Special Orders allows players to take daily randomized quests for their jobs.",
        "Players gets better rewards for completing Special Orders.",
        "Read wiki for details: " + URL_WIKI_SPECIAL_ORDERS
    );

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

    public static final ConfigValue<Boolean> ABUSE_TRACK_PLAYER_BLOCKS = ConfigValue.create("Abuse_Protection.Track_Player_Blocks",
        true,
        "Sets whether or not plugin will track player placed blocks. Player placed blocks will give no rewards when mined."
    );

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

    public static final ConfigValue<Set<String>> ABUSE_IGNORE_FERTILIZED = ConfigValue.create("Abuse_Protection.Ignore_Fertilized",
        Lists.newSet(WILDCARD),
        "The following blocks will give no job XP / currency if been fertilized by bone meal.",
        "Add '" + WILDCARD + "' to the list to include all possible blocks.",
        "List of all block names: https://minecraft.wiki/w/Java_Edition_data_values#Blocks"
    ).onRead(set -> Lists.modify(set, String::toLowerCase));

    public static final ConfigValue<Boolean> ABUSE_IGNORE_VEHICLES = ConfigValue.create("Abuse_Protection.Ignore_Vehicles",
        false,
        "When enabled, players will get no XP / money for doing job objectives while in vehicle.",
        "Vehicles are all non-living entities (e.g. minecarts, boats, etc.)"
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
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 25D), 25D),
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
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 25D), 25D)
            ),
            "premium", new RankBoosterInfo("premium", 10, Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 50D), 50D)
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
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 25D), 25D)),
            "xp_money_50", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 50D), 50D)),
            "money_100", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 100D), 0D)),
            "xp_100", new BoosterInfo(Set.of(Placeholders.WILDCARD),
                new BoosterMultiplier(Map.of(CurrencyId.VAULT, 0D), 100D))
        ),
        "List of custom XP / currency boosters to be given via booster commands.",
        "You can create as many boosters as you want.",
        "But keep in mind that only one personal booster per job can be active at the same time.",
        "If player already has a booster for as job, it will be replaced with a new one."
    );

    public static boolean isStatisticEnabled() {
        return STATISTIC_ENABLED.get();
    }

    public static boolean isSpecialOrdersEnabled() {
        return SPECIAL_ORDERS_ENABLED.get();
    }

    @NotNull
    public static Material getHighlightCorner() {
        return ZONES_HIGHLIGHT_BLOCK_CORNER.get();
    }

    @NotNull
    public static Material getHighlightWire() {
        return ZONES_HIGHLIGHT_BLOCK_WIRE.get();
    }
}
