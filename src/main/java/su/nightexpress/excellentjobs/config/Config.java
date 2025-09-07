package su.nightexpress.excellentjobs.config;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.RankMap;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.rankmap.IntRankMap;

import java.util.Map;
import java.util.Set;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Config {

    public static final String DIR_MENU       = "/menu/";
    public static final String DIR_JOBS       = "/jobs/";
    public static final String DIR_ZONES      = "/zones/";

    public static final ConfigValue<Boolean> FEATURES_BOOSTERS = ConfigValue.create("Features.Boosters",
        true,
        "Controls whether Boosters feature is enabled and available."
    );

    public static final ConfigValue<Set<String>> GENERAL_DISABLED_WORLDS = ConfigValue.create("General.Disabled_Worlds",
        Set.of("my_world", "another_world"),
        "A list of worlds, where jobs will have no effect.");

    public static final ConfigValue<Boolean> GENERAL_DEFAULT_MENU_COMMAND_ENABLED = ConfigValue.create("General.Default_Menu_Command",
        true,
        "Sets whether or not '/jobs menu' command is set as default plugin command instead of '/jobs help' one.");

    public static final ConfigValue<Boolean> GENERAL_PAYMENT_INSTANT = ConfigValue.create("General.Payment.Instant",
        false,
        "Controls whether players get payments for their work (job objectives) instantly without a delay (interval)."
    );

    public static final ConfigValue<Integer> GENERAL_PAYMENT_INTERVAL = ConfigValue.create("General.Payment.Interval",
        900,
        "Sets how often (in seconds) players will get payments for their work.",
        "Players will get instant payment when leaving server.",
        "[*] Useless if 'Instant' is set on 'true'.",
        "[Default is 900 (15 minues)]");

    public static final ConfigValue<Boolean> GENERAL_PROGRESS_BAR_ENABLED = ConfigValue.create("General.ProgressBar.Enabled",
        true,
        "Enables progress bar indicating gained job XP and future income for the latest X seconds."
    );

    public static final ConfigValue<Boolean> GENERAL_PROGRESS_BAR_BOSS_BAR_ENABLED = ConfigValue.create("General.ProgressBar.BossBar.Enabled",
        true,
        "Controls whether job progress bar should display as Boss Bar."
    );

    public static final ConfigValue<Integer> GENERAL_PROGRESS_BAR_STAY_TIME = ConfigValue.create("General.ProgressBar.StayTime",
        8,
        "Sets for how long (in seconds) progress bar will stay before reset and disappear.");

    public static final ConfigValue<String> GENERAL_PROGRESS_BAR_TITLE = ConfigValue.create("General.ProgressBar.Title",
        GRAY.wrap(LIGHT_YELLOW.wrap(BOLD.wrap(JOB_NAME + " Job")) + " (Lv. " + WHITE.wrap(JOB_DATA_LEVEL) + ") | " + LIGHT_RED.wrap("+" + GENERIC_XP + " XP") + " | " + LIGHT_GREEN.wrap("+" + GENERIC_INCOME)),
        "Sets title for job progress bar.",
        "You can use 'Job' placeholders: " + URL_WIKI_PLACEHOLDERS
    );

    public static final ConfigValue<BarStyle> GENERAL_PROGRESS_BAR_STYLE = ConfigValue.create("General.ProgressBar.Style",
        BarStyle.class, BarStyle.SOLID,
        "Sets style for job progress bar.",
        "Allowed values: " + Enums.inline(BarStyle.class, ", ")
    );

    public static final ConfigValue<Boolean> PROGRESS_BAR_ACTION_BAR_ENABLED = ConfigValue.create("General.ProgressBar.ActionBar.Enabled",
        false,
        "Controls whether job progress bar should display in Action Bar."
    );

    public static final ConfigValue<String> PROGRESS_BAR_ACTION_BAR_TEXT = ConfigValue.create("General.ProgressBar.ActionBar.Text",
        GRAY.wrap(LIGHT_YELLOW.wrap(BOLD.wrap(JOB_NAME + " Job")) + " (Lv. " + WHITE.wrap(JOB_DATA_LEVEL) + ") | " + LIGHT_RED.wrap("+" + GENERIC_XP + " XP") + " | " + LIGHT_GREEN.wrap("+" + GENERIC_INCOME)),
        "Sets text for the job progression in action bar.",
        "You can use 'Job' placeholders: " + URL_WIKI_PLACEHOLDERS
    );

    public static final ConfigValue<Boolean> JOBS_COOLDOWN_ON_JOIN = ConfigValue.create("Jobs.Cooldown.OnJoin",
        true,
        "Controls whether the job cooldown is applied when player joins the job.");

    public static final ConfigValue<Boolean> JOBS_COOLDOWN_ON_LEAVE = ConfigValue.create("Jobs.Cooldown.OnLeave",
        true,
        "Controls whether the job cooldown is applied when player quits the job.");

    public static final ConfigValue<IntRankMap> JOBS_COOLDOWN_VALUES = ConfigValue.create("Jobs.Cooldown.Values",
        IntRankMap::read,
        (cfg, path, r) -> r.write(cfg, path),
        () -> IntRankMap.ranked(86400).addValue("vip", 42200).addValue("admin", 0),
        "Sets job cooldown values based on player rank or permissions."
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

    public static final ConfigValue<Boolean> JOBS_REWARDS_CLAIM_REQUIRED = ConfigValue.create("Jobs.Rewards.ClaimRequired",
        false,
        "Controls whether players must manually claim unlocked job rewards in the GUI.",
        "[Default is false]"
    );

    public static final ConfigValue<Boolean> LEVELLED_MOBS_KILL_ENTITY_ENABLED = ConfigValue.create("LevelledMobs.Integration.KillEntity.Enabled",
        true,
        "When enabled, multiplies XP and payment amount produced by  job objective when killing mobs with levels from LevelledMobs."
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

    public static final ConfigValue<NightItem> ZONES_WAND_ITEM = ConfigValue.create("Zones.WandItem",
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
        "[Default is " + BukkitThing.getValue(Material.WHITE_STAINED_GLASS) + "]"
    );

    public static final ConfigValue<Material> ZONES_HIGHLIGHT_BLOCK_WIRE = ConfigValue.create("Zones.Highlighting.WireBlock",
        Material.class,
        Material.CHAIN,
        "Block type used for a fake block display entity for zone selection's corners connections.",
        "[Default is " + BukkitThing.getValue(Material.CHAIN) + "]"
    );

    public static final ConfigValue<Boolean> LEVELING_FIREWORKS = ConfigValue.create("Leveling.Fireworks",
        true,
        "Sets whether or not a random firework will be spawned above the player on job level up.");

    public static final ConfigValue<Boolean> ABUSE_TRACK_PLAYER_BLOCKS = ConfigValue.create("Abuse_Protection.Track_Player_Blocks",
        true,
        "Sets whether or not plugin will track player placed blocks. Player placed blocks will give no rewards when mined."
    );

    public static final ConfigValue<Set<CreatureSpawnEvent.SpawnReason>> ABUSE_IGNORE_SPAWN_REASONS = ConfigValue.forSet("Abuse_Protection.Ignore_SpawnReasons",
        raw -> Enums.get(raw, CreatureSpawnEvent.SpawnReason.class),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Set.of(
            CreatureSpawnEvent.SpawnReason.EGG,
            CreatureSpawnEvent.SpawnReason.SPAWNER,
            CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
            CreatureSpawnEvent.SpawnReason.DISPENSE_EGG,
            CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            CreatureSpawnEvent.SpawnReason.SLIME_SPLIT
        ),
        "Mobs spawned by the following reasons will give no job XP / currency.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html"
    );

    public static final ConfigValue<Set<GameMode>> ABUSE_IGNORE_GAME_MODES = ConfigValue.forSet("Abuse_Protection.Ignore_GameModes",
        raw -> Enums.get(raw, GameMode.class),
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

    public static final ConfigValue<Boolean> ABUSE_RESTRICT_PET_KILLS = ConfigValue.create("Abuse_Protection.Restrict_Pet_Kills",
        false,
        "Controls whether job objectives will produce XP & Income for mobs killed by player's pets."
    );

    public static final ConfigValue<Boolean> STATISTIC_ENABLED = ConfigValue.create("Statistic.Enabled",
        true,
        "Sets whether or not Statistics module is enabled.",
        "When disabled, all job statistics including top stats will be unavailable.");

    public static final ConfigValue<Integer> STATISTIC_UPDATE_INTERVAL = ConfigValue.create("Statistic.Update_Interval", 600,
        "Sets how often (in seconds) statistic will be fetched and updated.");

    public static final ConfigValue<Integer> STATISTIC_ENTRIES_PER_PAGE = ConfigValue.create("Statistic.Entries_Per_Page", 10,
        "Sets how many entries per leaderboard page will be displated.");

    public static boolean isBoostersEnabled() {
        return FEATURES_BOOSTERS.get();
    }

    public static boolean isStatisticEnabled() {
        return STATISTIC_ENABLED.get();
    }

    public static boolean isRewardClaimRequired() {
        return JOBS_REWARDS_CLAIM_REQUIRED.get();
    }

    public static boolean isInstantPayment() {
        return GENERAL_PAYMENT_INSTANT.get();
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
