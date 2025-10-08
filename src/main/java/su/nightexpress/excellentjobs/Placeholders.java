package su.nightexpress.excellentjobs;

import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderList;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Placeholders extends su.nightexpress.nightcore.util.Placeholders {

    public static final String URL_WIKI                     = "https://nightexpressdev.com/excellentjobs/";
    public static final String URL_WIKI_PLACEHOLDERS        = URL_WIKI + "placeholders";
    public static final String URL_WIKI_ECONOMY             = URL_WIKI + "hooks/eco-currencies";
    public static final String URL_WIKI_SPECIAL_ORDERS      = URL_WIKI + "jobs/special-orders";
    public static final String URL_WIKI_LEVELING            = URL_WIKI + "jobs/leveling";
    public static final String URL_WIKI_LEVEL_REWARDS       = URL_WIKI + "jobs/level-rewards";
    public static final String URL_WIKI_DAILY_LIMITS        = URL_WIKI + "jobs/daily-limits";
    public static final String URL_WIKI_XP_INCOME_BONUS     = URL_WIKI + "jobs/xp-income-bonus";
    public static final String URL_WIKI_DISABLED_WORLDS     = URL_WIKI + "jobs/disabled-worlds";
    public static final String URL_WIKI_JOB_PRIORITIES      = URL_WIKI + "jobs/priorities";
    public static final String URL_WIKI_JOB_PRIORITY_LIMITS = URL_WIKI + "jobs/priority-limits";
    public static final String URL_WIKI_JOB_AUTO_JOIN       = URL_WIKI + "jobs/auto-join";
    public static final String URL_WIKI_LEAVE_JOIN_COMMANDS = URL_WIKI + "jobs/leave-join-commands";


    public static final String URL_WIKI_ZONES           = URL_WIKI + "zones/overview";
    public static final String URL_WIKI_MODIFIERS       = URL_WIKI + "modifiers";

    public static final String URL_ECO_BRIDGE = "https://nightexpressdev.com/economy-bridge/currencies/";

    public static final String GENERIC_AMOUNT      = "%amount%";
    public static final String GENERIC_NAME        = "%name%";
    public static final String GENERIC_DESCRIPTION = "%description%";
    public static final String GENERIC_BALANCE     = "%balance%";
    public static final String GENERIC_XP          = "%exp%";
    public static final String GENERIC_TIME        = "%time%";
    public static final String GENERIC_CURRENCY    = "%currency%";
    public static final String GENERIC_POS         = "%pos%";
    public static final String GENERIC_CURRENT     = "%current%";
    public static final String GENERIC_MIN         = "%min%";
    public static final String GENERIC_MAX         = "%max%";
    public static final String GENERIC_STATE       = "%state%";
    public static final String GENERIC_ENTRY       = "%entry%";
    public static final String GENERIC_TYPE        = "%type%";
    public static final String GENERIC_TOTAL       = "%total%";
    public static final String GENERIC_REWARD      = "%reward%";
    public static final String GENERIC_REWARDS      = "%rewards%";
    public static final String GENERIC_INCOME      = "%income%";
    public static final String GENERIC_LEVEL       = "%level%";
    public static final String GENERIC_OBJECTIVES = "%objectives%";

    public static final String GENERIC_XP_BONUS          = "%xp_bonus%";
    public static final String GENERIC_XP_BOOST          = "%xp_boost%";
    public static final String GENERIC_XP_MULTIPLIER     = "%xp_multiplier%";
    public static final String GENERIC_INCOME_BONUS      = "%income_bonus%";
    public static final String GENERIC_INCOME_BOOST      = "%income_boost%";
    public static final String GENERIC_INCOME_MULTIPLIER = "%income_multiplier%";

    public static final String GENERIC_PRIMARY_COUNT = "%primary_count%";
    public static final String GENERIC_PRIMARY_LIMIT = "%primary_limit%";
    public static final String GENERIC_SECONDARY_COUNT = "%secondary_count%";
    public static final String GENERIC_SECONDARY_LIMIT = "%secondary_limit%";

    public static final String MODIFIER_BASE      = "%modifier_base%";
    public static final String MODIFIER_PER_LEVEL = "%modifier_per_level%";
    public static final String MODIFIER_STEP      = "%modifier_step%";
    public static final String MODIFIER_ACTION    = "%modifier_action%";

    public static final String BOOSTER_XP_MODIFIER     = "%booster_xp_modifier%";
    public static final String BOOSTER_XP_PERCENT      = "%booster_xp_percent%";
    public static final String BOOSTER_INCOME_PERCENT  = "%booster_income_percent%";
    public static final String BOOSTER_INCOME_MODIFIER = "%booster_income_modifier%";

    public static final String CURRENCY_ID   = "%currency_id%";
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
    public static final String JOB_EMPLOYEES_TOTAL     = "%job_employees_total%";
    public static final String JOB_EMPLOYEES_PRIMARY   = "%job_employees_primary%";
    public static final String JOB_EMPLOYEES_SECONDARY = "%job_employees_secondary%";

    public static final String ZONE_ID                          = "%zone_id%";
    public static final String ZONE_NAME                        = "%zone_name%";
    public static final String ZONE_DESCRIPTION                 = "%zone_description%";
    public static final String ZONE_JOB_IDS                     = "%zone_job_ids%";
    public static final String ZONE_JOB_NAMES                   = "%zone_job_names%";
    public static final String ZONE_JOB_MIN_LEVEL               = "%zone_job_min_level%";
    public static final String ZONE_JOB_MAX_LEVEL               = "%zone_job_max_level%";
    //public static final String ZONE_CLOSE_TIME                  = "%zone_close_time%";
    //public static final String ZONE_OPEN_TIME                   = "%zone_open_time%";
    public static final String ZONE_HOURS_ENABLED               = "%zone_hours_enabled%";
    public static final String ZONE_DISABLED_BLOCK_INTERACTIONS = "%zone_disabled_block_interactions%";
    public static final String ZONE_PERMISSION                  = "%zone_permission%";
    public static final String ZONE_PERMISSION_REQUIRED         = "%zone_permission_required%";
    public static final String ZONE_PVP_ALLOWED                 = "%zone_pvp_allowed%";

    public static final String ZONE_INSPECT_SELECTION = "%zone_inspect_zone_selection%";

    public static final String BLOCK_LIST_ID                = "%blocklist_id%";
    public static final String BLOCK_LIST_MATERIALS         = "%blocklist_materials%";
    public static final String BLOCK_LIST_FALLBACK_MATERIAL = "%blocklist_fallback_material%";
    public static final String BLOCK_LIST_RESET_TIME        = "%blocklist_reset_time%";
    public static final String BLOCK_LIST_DROP_ITEMS        = "%blocklist_drop_items%";

    public static final String BOOSTER_TIME_LEFT = "%booster_timeleft%";

    public static final String OBJECTIVE_ACTION_TYPE     = "%objective_action_type%";
    public static final String OBJECTIVE_NAME            = "%objective_name%";
    public static final String OBJECTIVE_LORE            = "%objective_lore%";
    public static final String OBJECTIVE_CURRENCY_MIN    = "%objective_currency_min%";
    public static final String OBJECTIVE_CURRENCY_MAX    = "%objective_currency_max%";
    public static final String OBJECTIVE_CURRENCY_CHANCE = "%objective_currency_chance%";
    public static final String OBJECTIVE_XP_MIN          = "%objective_xp_min%";
    public static final String OBJECTIVE_XP_MAX          = "%objective_xp_max%";
    public static final String OBJECTIVE_XP_CHANCE       = "%objective_xp_chance%";
    public static final String OBJECTIVE_UNLOCK_LEVEL    = "%objective_unlock_level%";

    public static final String                   REWARD_NAME         = "%reward_name%";
    public static final String                   REWARD_DESCRIPTION  = "%reward_description%";
    public static final String                   REWARD_LEVELS        = "%reward_levels%";
    public static final String                   REWARD_REPEATABLE   = "%reward_repeatable%";
    public static final String                   REWARD_REQUIREMENT  = "%reward_requirement%";
    public static final Function<String, String> REWARD_MODIFIER     = id -> "%mod_" + id + "%";
    public static final Function<String, String> REWARD_MODIFIER_RAW = id -> "%rawmod_" + id + "%";

    public static final PlaceholderList<Currency> CURRENCY = new PlaceholderList<Currency>()
        .add(CURRENCY_ID, Currency::getInternalId)
        .add(CURRENCY_NAME, Currency::getName);

    public static final PlaceholderList<Modifier> MODIFIER = PlaceholderList.create(list -> list
        .add(MODIFIER_BASE, modifier -> NumberUtil.format(modifier.getBase()))
        .add(MODIFIER_PER_LEVEL, modifier -> NumberUtil.format(modifier.getPerLevel()))
        .add(MODIFIER_STEP, modifier -> NumberUtil.format(modifier.getStep()))
        .add(MODIFIER_ACTION, modifier -> StringUtil.capitalizeFully(modifier.getAction().name()))
    );

    public static final PlaceholderList<Booster> BOOSTER = PlaceholderList.create(list -> list
        .add(BOOSTER_XP_MODIFIER, booster -> NumberUtil.format(booster.getValue(MultiplierType.XP)))
        .add(BOOSTER_XP_PERCENT, booster -> booster.formattedPercent(MultiplierType.XP))
        .add(BOOSTER_INCOME_MODIFIER, booster -> NumberUtil.format(booster.getValue(MultiplierType.INCOME)))
        .add(BOOSTER_INCOME_PERCENT, booster -> booster.formattedPercent(MultiplierType.INCOME))
    );

    public static final PlaceholderList<Job> JOB = PlaceholderList.create(list -> list
        .add(JOB_ID, Job::getId)
        .add(JOB_NAME, Job::getName)
        .add(JOB_DESCRIPTION, job -> String.join("\n", job.getDescription()))
        .add(JOB_PERMISSION_REQUIRED, job -> CoreLang.STATE_YES_NO.get(job.isPermissionRequired()))
        .add(JOB_PERMISSION_NODE, Job::getPermission)
        .add(JOB_MAX_LEVEL, job -> NumberUtil.format(job.getMaxLevel()))
        .add("%job_max_secondary_level%", job -> NumberUtil.format(job.getMaxLevel()))
        .add(JOB_EMPLOYEES_TOTAL, job -> NumberUtil.format(job.getEmployees()))
        .add(JOB_EMPLOYEES_PRIMARY, job -> NumberUtil.format(job.getEmployeesAmount(JobState.PRIMARY)))
        .add(JOB_EMPLOYEES_SECONDARY, job -> NumberUtil.format(job.getEmployeesAmount(JobState.SECONDARY)))
    );

    public static final PlaceholderList<JobData> JOB_DATA = PlaceholderList.create(list -> list
        .add(JOB_DATA_STATE, jobData -> Lang.JOB_STATE.getLocalized(jobData.getState()))
        .add(JOB_DATA_LEVEL, jobData -> NumberUtil.format(jobData.getLevel()))
        .add(JOB_DATA_LEVEL_MAX, jobData -> NumberUtil.format(jobData.getMaxLevel()))
        .add(JOB_DATA_XP, jobData -> NumberUtil.format(jobData.getXP()))
        .add(JOB_DATA_XP_MAX, jobData -> NumberUtil.format(jobData.getLevelXP()))
        .add(JOB_DATA_XP_TO_UP, jobData -> NumberUtil.format(jobData.getXPToLevelUp()))
        .add(JOB_DATA_XP_TO_DOWN, jobData -> NumberUtil.format(jobData.getXPToLevelDown()))
    );

    public static final PlaceholderList<LevelReward> LEVEL_REWARD = PlaceholderList.create(list -> list
        .add(REWARD_NAME, LevelReward::getName)
        .add(REWARD_DESCRIPTION, reward -> String.join("\n", reward.getDescription()))
        .add(REWARD_REQUIREMENT, reward -> String.join("\n", reward.getRequirementText()))
        .add(REWARD_LEVELS, reward -> Arrays.toString(reward.getLevels()))
        .add(REWARD_REPEATABLE, reward -> CoreLang.STATE_YES_NO.get(reward.isRepeatable()))
    );

    public static final PlaceholderList<Zone> ZONE = PlaceholderList.create(list -> list
            .add(ZONE_ID, Zone::getId)
            .add(ZONE_NAME, Zone::getName)
            .add(ZONE_DESCRIPTION, zone -> String.join("\n", zone.getDescription()))
            .add(ZONE_PVP_ALLOWED, zone -> CoreLang.STATE_YES_NO.get(zone.isPvPAllowed()))
            .add(ZONE_JOB_IDS, zone -> String.join("\n", zone.getLinkedJobs()))
            .add(ZONE_JOB_NAMES, zone -> zone.getLinkedJobs().stream().map(id -> {
                Job job = JobsAPI.getJobById(id);
                return job == null ? CoreLang.badEntry(id) : CoreLang.goodEntry(job.getName());
            }).collect(Collectors.joining("\n")))
            .add(ZONE_JOB_MIN_LEVEL, zone -> NumberUtil.format(zone.getMinJobLevel()))
            .add(ZONE_JOB_MAX_LEVEL, zone -> NumberUtil.format(zone.getMaxJobLevel()))
            .add(ZONE_HOURS_ENABLED, zone -> CoreLang.STATE_ENABLED_DISALBED.get(zone.isHoursEnabled()))
//        .add(ZONE_CLOSE_TIME, zone -> {
//            LocalTime time = zone.getNearestCloseTime();
//            return time == null ? "-" : JobUtils.formatTime(time);
//        })
//        .add(ZONE_OPEN_TIME, zone -> {
//            LocalTime time = zone.getNearestOpenTime();
//            return time == null ? "-" : JobUtils.formatTime(time);
//        })
            .add(ZONE_DISABLED_BLOCK_INTERACTIONS, zone -> {
                return String.join("\n", Lists.modify(zone.getDisabledInteractions(), type -> CoreLang.goodEntry(LangUtil.getSerializedName(type))));
            })
    );

    public static final PlaceholderList<Zone> ZONE_EDITOR = PlaceholderList.create(list -> list
        .add(ZONE)
        .add(ZONE_PERMISSION, Zone::getPermission)
        .add(ZONE_PERMISSION_REQUIRED, zone -> CoreLang.STATE_YES_NO.get(zone.isPermissionRequired()))
        .add(ZONE_INSPECT_SELECTION, zone -> {
            if (zone.getCuboid().isEmpty()) return CoreLang.badEntry("Invalid cuboid selection.");
            return CoreLang.goodEntry("Selection is valid.");
        })
    );

    public static final PlaceholderList<BlockList> ZONE_BLOCK_LIST = PlaceholderList.create(list -> list
        .add(BLOCK_LIST_ID, BlockList::getId)
        .add(BLOCK_LIST_MATERIALS, blockList -> {
            return String.join("\n", blockList.getMaterials().stream().map(mat -> CoreLang.goodEntry(LangUtil.getSerializedName(mat))).toList());
        })
        .add(BLOCK_LIST_FALLBACK_MATERIAL, blockList -> CoreLang.goodEntry(LangUtil.getSerializedName(blockList.getFallbackMaterial())))
        .add(BLOCK_LIST_RESET_TIME, blockList -> CoreLang.goodEntry(TimeFormats.toLiteral(blockList.getResetTime() * 1000L)))
        .add(BLOCK_LIST_DROP_ITEMS, blockList -> {
            String yesNo = CoreLang.STATE_YES_NO.get(blockList.isDropItems());
            return blockList.isDropItems() ? CoreLang.goodEntry(yesNo) : CoreLang.badEntry(yesNo);
        })
    );
}
