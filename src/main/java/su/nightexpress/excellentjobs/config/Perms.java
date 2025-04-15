package su.nightexpress.excellentjobs.config;

import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class Perms {

    public static final String PREFIX                       = "excellentjobs.";
    public static final String PREFIX_COMMAND               = PREFIX + "command.";
    public static final String PREFIX_BYPASS                = PREFIX + "bypass.";
    public static final String PREFIX_ZONE                  = PREFIX + "zone.";
    public static final String PREFIX_JOB                   = PREFIX + "job.";
    public static final String PREFIX_PRIMARY_JOBS          = PREFIX + "jobs.primary.";
    public static final String PREFIX_SECONDARY_JOBS        = PREFIX + "jobs.secondary.";
    public static final String PREFIX_BYPASS_LIMIT_XP       = PREFIX_BYPASS + "job.limit.xp.";
    public static final String PREFIX_BYPASS_LIMIT_CURRENCY = PREFIX_BYPASS + "job.limit.currency.";

    public static final UniPermission PLUGIN  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);
    public static final UniPermission JOB     = new UniPermission(PREFIX_JOB + Placeholders.WILDCARD);
    public static final UniPermission ZONE    = new UniPermission(PREFIX_ZONE + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_BOOSTER      = new UniPermission(PREFIX_COMMAND + "booster");
    public static final UniPermission COMMAND_BOOSTS       = new UniPermission(PREFIX_COMMAND + "boosts");
    public static final UniPermission COMMAND_STATS        = new UniPermission(PREFIX_COMMAND + "stats");
    public static final UniPermission COMMAND_STATS_OTHERS = new UniPermission(PREFIX_COMMAND + "stats.others");
    public static final UniPermission COMMAND_JOIN         = new UniPermission(PREFIX_COMMAND + "join");
    public static final UniPermission COMMAND_LEAVE        = new UniPermission(PREFIX_COMMAND + "leave");
    public static final UniPermission COMMAND_MENU         = new UniPermission(PREFIX_COMMAND + "menu");
    public static final UniPermission COMMAND_XP           = new UniPermission(PREFIX_COMMAND + "xp");
    public static final UniPermission COMMAND_LEVEL        = new UniPermission(PREFIX_COMMAND + "level");
    public static final UniPermission COMMAND_OBJECTIVES   = new UniPermission(PREFIX_COMMAND + "objectives");
    public static final UniPermission COMMAND_RESET        = new UniPermission(PREFIX_COMMAND + "reset");
    public static final UniPermission COMMAND_RESET_OTHERS = new UniPermission(PREFIX_COMMAND + "reset.others");
    public static final UniPermission COMMAND_SET_STATE    = new UniPermission(PREFIX_COMMAND + "setstate");
    public static final UniPermission COMMAND_TOP          = new UniPermission(PREFIX_COMMAND + "top");
    public static final UniPermission COMMAND_RELOAD       = new UniPermission(PREFIX_COMMAND + "reload");
    public static final UniPermission COMMAND_ZONE         = new UniPermission(PREFIX_COMMAND + "zone");
    public static final UniPermission COMMAND_ZONE_CREATE  = new UniPermission(PREFIX_COMMAND + "zone.create");
    public static final UniPermission COMMAND_ZONE_WAND    = new UniPermission(PREFIX_COMMAND + "zone.wand");
    public static final UniPermission COMMAND_ZONE_EDITOR  = new UniPermission(PREFIX_COMMAND + "zone.editor");

    public static final UniPermission BYPASS_ZONE_ACCESS            = new UniPermission(PREFIX_BYPASS + "zone.access");
    public static final UniPermission BYPASS_ZONE_PROTECTION        = new UniPermission(PREFIX_BYPASS + "zone.protection");
    public static final UniPermission BYPASS_OBJECTIVE_UNLOCK_LEVEL = new UniPermission(PREFIX_BYPASS + "objective.unlock.level");
    public static final UniPermission BYPASS_JOB_LIMIT_XP           = new UniPermission(PREFIX_BYPASS_LIMIT_XP + Placeholders.WILDCARD);
    public static final UniPermission BYPASS_JOB_LIMIT_CURRENCY     = new UniPermission(PREFIX_BYPASS_LIMIT_CURRENCY + Placeholders.WILDCARD);

    static {
        PLUGIN.addChildren(COMMAND, BYPASS, JOB, ZONE);

        COMMAND.addChildren(
            COMMAND_RELOAD,
            COMMAND_JOIN,
            COMMAND_LEAVE,
            COMMAND_MENU,
            COMMAND_OBJECTIVES,
            COMMAND_LEVEL,
            COMMAND_XP,
            COMMAND_RESET,
            COMMAND_RESET_OTHERS,
            COMMAND_SET_STATE,
            COMMAND_BOOSTER,
            COMMAND_BOOSTS,
            COMMAND_STATS,
            COMMAND_STATS_OTHERS,
            COMMAND_ZONE,
            COMMAND_ZONE_CREATE,
            COMMAND_ZONE_EDITOR,
            COMMAND_ZONE_WAND
        );

        BYPASS.addChildren(
            BYPASS_ZONE_ACCESS,
            BYPASS_ZONE_PROTECTION,
            BYPASS_JOB_LIMIT_CURRENCY,
            BYPASS_JOB_LIMIT_XP,
            BYPASS_OBJECTIVE_UNLOCK_LEVEL
        );
    }
}
