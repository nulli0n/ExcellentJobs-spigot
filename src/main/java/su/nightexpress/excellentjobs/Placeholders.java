package su.nightexpress.excellentjobs;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.util.report.ReportType;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.time.LocalTime;
import java.util.function.Function;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Placeholders extends su.nightexpress.nightcore.util.Placeholders {

    public static final String URL_WIKI                = "https://nightexpress.gitbook.io/excellentjobs/";
    public static final String URL_WIKI_PLACEHOLDERS   = URL_WIKI + "utility/placeholders";
    public static final String URL_WIKI_ACTION_TYPES   = URL_WIKI + "features/job-action-types";
    public static final String URL_WIKI_CURRENCY       = URL_WIKI + "features/multi-currency";
    public static final String URL_WIKI_SPECIAL_ORDERS = URL_WIKI + "features/special-orders";
    public static final String URL_WIKI_ZONES          = URL_WIKI + "features/zones";

    private static final String PREFIX_PROBLEM = RED.enclose("✘ ");
    private static final String PREFIX_GOOD    = GREEN.enclose("✔ ");
    private static final String PREFIX_WARN    = ORANGE.enclose("❗ ");

    public static final String GENERIC_AMOUNT   = "%amount%";
    public static final String GENERIC_NAME     = "%name%";
    public static final String GENERIC_BALANCE = "%balance%";
    public static final String GENERIC_XP      = "%exp%";
    public static final String GENERIC_TIME    = "%time%";
    public static final String GENERIC_CURRENCY = "%currency%";
    public static final String GENERIC_POS      = "%pos%";
    public static final String GENERIC_CURRENT  = "%current%";
    public static final String GENERIC_MIN      = "%min%";
    public static final String GENERIC_MAX      = "%max%";
    public static final String GENERIC_STATE    = "%state%";
    public static final String GENERIC_ENTRY    = "%entry%";
    public static final String GENERIC_TYPE     = "%type%";
    public static final String GENERIC_TOTAL    = "%total%";
    public static final String GENERIC_REWARD   = "%reward%";
    public static final String GENERIC_INCOME   = "%income%";
    public static final String GENERIC_LEVEL    = "%level%";

    public static final String MODIFIER_BASE      = "%modifier_base%";
    public static final String MODIFIER_PER_LEVEL = "%modifier_per_level%";
    public static final String MODIFIER_STEP      = "%modifier_step%";
    public static final String MODIFIER_ACTION    = "%modifier_action%";

    public static final String XP_MULTIPLIER           = "%xp_multiplier%";
    public static final String XP_BOOST_MODIFIER       = "%xp_boost_modifier%";
    public static final String XP_BOOST_PERCENT        = "%xp_boost_percent%";
    public static final String CURRENCY_MULTIPLIER     = "%currency_multiplier%";
    public static final String CURRENCY_BOOST_PERCENT  = "%currency_boost_percent%";
    public static final String CURRENCY_BOOST_MODIFIER = "%currency_boost_modifier%";

    public static final String CURRENCY_ID = "%currency_id%";
    public static final String CURRENCY_NAME = "%currency_name%";

    public static final String JOB_DATA_STATE      = "%job_state%";
    public static final String JOB_DATA_XP         = "%job_xp%";
    public static final String JOB_DATA_XP_MAX     = "%job_xp_max%";
    public static final String JOB_DATA_XP_TO_UP   = "%job_xp_to_up%";
    public static final String JOB_DATA_XP_TO_DOWN = "%job_xp_to_down%";
    public static final String JOB_DATA_RANK       = "%job_rank%";
    public static final String JOB_DATA_LEVEL      = "%job_level%";
    public static final String JOB_DATA_LEVEL_MAX  = "%job_level_max%";

    public static final String JOB_ID                  = "%job_id%";
    public static final String JOB_NAME                = "%job_name%";
    public static final String JOB_DESCRIPTION         = "%job_description%";
    public static final String JOB_PERMISSION_REQUIRED = "%job_permission_required%";
    public static final String JOB_PERMISSION_NODE     = "%job_permission_node%";
    public static final String JOB_MAX_LEVEL           = "%job_max_level%";
    public static final String JOB_MAX_SECONDARY_LEVEL = "%job_max_secondary_level%";
    public static final String JOB_EMPLOYEES_TOTAL     = "%job_employees_total%";
    public static final String JOB_EMPLOYEES_PRIMARY   = "%job_employees_primary%";
    public static final String JOB_EMPLOYEES_SECONDARY = "%job_employees_secondary%";

    public static final String ZONE_ID                          = "%zone_id%";
    public static final String ZONE_NAME                        = "%zone_name%";
    public static final String ZONE_DESCRIPTION                 = "%zone_description%";
    public static final String ZONE_JOB_ID                      = "%zone_job_id%";
    public static final String ZONE_JOB_NAME                    = "%zone_job_name%";
    public static final String ZONE_JOB_MIN_LEVEL               = "%zone_job_min_level%";
    public static final String ZONE_JOB_MAX_LEVEL               = "%zone_job_max_level%";
    public static final String ZONE_CLOSE_TIME                  = "%zone_close_time%";
    public static final String ZONE_OPEN_TIME                   = "%zone_open_time%";
    public static final String ZONE_DISABLED_BLOCK_INTERACTIONS = "%zone_disabled_block_interactions%";
    public static final String ZONE_PERMISSION                  = "%zone_permission%";
    public static final String ZONE_PERMISSION_REQUIRED         = "%zone_permission_required%";
    public static final String ZONE_PVP_ALLOWED                 = "%zone_pvp_allowed%";

    public static final String                       ZONE_REPORT  = "%zone_report%";
    public static final Function<ReportType, String> ZONE_INSPECT = type -> "%zone_inspect_" + type.name().toLowerCase() + "%";

    public static final String BLOCK_LIST_ID                = "%blocklist_id%";
    public static final String BLOCK_LIST_MATERIALS         = "%blocklist_materials%";
    public static final String BLOCK_LIST_FALLBACK_MATERIAL = "%blocklist_fallback_material%";
    public static final String BLOCK_LIST_RESET_TIME        = "%blocklist_reset_time%";
    public static final String BLOCK_LIST_DROP_ITEMS        = "%blocklist_drop_items%";

    public static final String BOOSTER_TIME_LEFT         = "%booster_timeleft%";

    public static final String OBJECTIVE_ACTION_TYPE     = "%objective_action_type%";
    public static final String OBJECTIVE_NAME            = "%objective_name%";
    public static final String OBJECTIVE_CURRENCY_MIN    = "%objective_currency_min%";
    public static final String OBJECTIVE_CURRENCY_MAX    = "%objective_currency_max%";
    public static final String OBJECTIVE_CURRENCY_CHANCE = "%objective_currency_chance%";
    public static final String OBJECTIVE_XP_MIN          = "%objective_xp_min%";
    public static final String OBJECTIVE_XP_MAX          = "%objective_xp_max%";
    public static final String OBJECTIVE_XP_CHANCE       = "%objective_xp_chance%";
    public static final String OBJECTIVE_UNLOCK_LEVEL    = "%objective_unlock_level%";

    @NotNull
    public static PlaceholderMap forZoneAll(@NotNull Zone zone) {
        return PlaceholderMap.fusion(zone.getPlaceholders(), forZoneEditor(zone));
    }

    @NotNull
    public static PlaceholderMap forZone(@NotNull Zone zone) {
        return new PlaceholderMap()
            .add(ZONE_ID, zone::getId)
            .add(ZONE_NAME, zone::getName)
            .add(ZONE_DESCRIPTION, () -> String.join("\n", zone.getDescription()))
            .add(ZONE_PVP_ALLOWED, () -> Lang.getYesOrNo(zone.isPvPAllowed()))
            .add(ZONE_JOB_ID, () -> zone.getLinkedJob() == null ? "null" : zone.getLinkedJob().getId())
            .add(ZONE_JOB_NAME, () -> zone.getLinkedJob() == null ? "null" : zone.getLinkedJob().getName())
            .add(ZONE_JOB_MIN_LEVEL, () -> NumberUtil.format(zone.getMinJobLevel()))
            .add(ZONE_JOB_MAX_LEVEL, () -> NumberUtil.format(zone.getMaxJobLevel()))
            .add(ZONE_CLOSE_TIME, () -> {
                var times = zone.getCurrentOpenTimes();
                if (times == null) return "-";

                return times.getSecond().format(Config.GENERAL_TIME_FORMATTER.get());
            })
            .add(ZONE_OPEN_TIME, () -> {
                LocalTime time = zone.getNearestOpenTime();
                return time == null ? "-" : time.format(Config.GENERAL_TIME_FORMATTER.get());
            })
            .add(ZONE_DISABLED_BLOCK_INTERACTIONS, () -> {
                return String.join("\n", Lists.modify(zone.getDisabledInteractions(), type -> good(LangAssets.get(type))));
            })
            ;
    }

    @NotNull
    public static PlaceholderMap forZoneEditor(@NotNull Zone zone) {
        PlaceholderMap placeholders = new PlaceholderMap()
            .add(ZONE_REPORT, () -> String.join("\n", zone.getReport().getFullReport()))
            .add(ZONE_PERMISSION, zone::getPermission)
            .add(ZONE_PERMISSION_REQUIRED, Lang.getYesOrNo(zone.isPermissionRequired()));

        zone.getReport().getKnownReports().forEach(type -> {
            placeholders.add(ZONE_INSPECT.apply(type), () -> String.valueOf(zone.getReport().getProblem(type)));
        });

        return placeholders;
    }

    @NotNull
    public static PlaceholderMap forModifier(@NotNull Modifier modifier) {
        return new PlaceholderMap()
            .add(MODIFIER_BASE, () -> NumberUtil.format(modifier.getBase()))
            .add(MODIFIER_PER_LEVEL, () -> NumberUtil.format(modifier.getPerLevel()))
            .add(MODIFIER_STEP, () -> NumberUtil.format(modifier.getStep()))
            .add(MODIFIER_ACTION, () -> StringUtil.capitalizeFully(modifier.getAction().name()));
    }

    @NotNull
    public static PlaceholderMap forBlockList(@NotNull BlockList blockList) {
        return new PlaceholderMap()
            .add(BLOCK_LIST_ID, blockList::getId)
            .add(BLOCK_LIST_MATERIALS, () -> {
                return String.join("\n", blockList.getMaterials().stream().map(mat -> good(LangAssets.get(mat))).toList());
            })
            .add(BLOCK_LIST_FALLBACK_MATERIAL, () -> good(LangAssets.get(blockList.getFallbackMaterial())))
            .add(BLOCK_LIST_RESET_TIME, () -> good(TimeUtil.formatTime(blockList.getResetTime() * 1000L)))
            .add(BLOCK_LIST_DROP_ITEMS, () -> {
                String yesNo = Lang.getYesOrNo(blockList.isDropItems());
                return blockList.isDropItems() ? good(yesNo) : problem(yesNo);
            });
    }

    @NotNull
    public static String problem(@NotNull String text) {
        return PREFIX_PROBLEM + GRAY.enclose(text);
    }

    @NotNull
    public static String good(@NotNull String text) {
        return PREFIX_GOOD + GRAY.enclose(text);
    }

    @NotNull
    public static String warn(@NotNull String text) {
        return PREFIX_WARN + GRAY.enclose(text);
    }
}
