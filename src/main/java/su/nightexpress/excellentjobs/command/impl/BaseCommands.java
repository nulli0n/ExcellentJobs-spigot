package su.nightexpress.excellentjobs.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.command.CommandArguments;
import su.nightexpress.excellentjobs.command.CommandFlags;
import su.nightexpress.excellentjobs.api.ModifyAction;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.impl.ReloadCommand;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.util.Lists;

public class BaseCommands {

    public static final String JOBS_ALIAS = "jobs";

    public static final String MENU_ALIAS       = "menu";
    public static final String LEVELS_ALIAS     = "levels";
    public static final String OBJECTIVES_ALIAS = "objectives";

    public static void load(@NotNull JobsPlugin plugin) {
        var root = plugin.getRootNode();

        root.addChildren(ReloadCommand.builder(plugin, Perms.COMMAND_RELOAD));

        var menuNode = DirectNode.builder(plugin, MENU_ALIAS)
            .playerOnly()
            .description(Lang.COMMAND_MENU_DESC)
            .permission(Perms.COMMAND_MENU)
            .executes((context, arguments) -> openMenu(plugin, context)).build();

        root.addChildren(menuNode);

        if (Config.GENERAL_DEFAULT_MENU_COMMAND_ENABLED.get()) {
            root.setFallback(menuNode);
        }

        root.addChildren(DirectNode.builder(plugin, "join")
            .playerOnly()
            .description(Lang.COMMAND_JOIN_DESC)
            .permission(Perms.COMMAND_JOIN)
            .withArgument(CommandArguments.forJob(plugin).required())
            .executes((context, arguments) -> joinJob(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "leave")
            .playerOnly()
            .description(Lang.COMMAND_LEAVE_DESC)
            .permission(Perms.COMMAND_LEAVE)
            .withArgument(CommandArguments.forJob(plugin).required())
            .executes((context, arguments) -> leaveJob(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, LEVELS_ALIAS)
            .playerOnly()
            .description(Lang.COMMAND_LEVELS_DESC)
            .permission(Perms.COMMAND_LEVELS)
            .withArgument(CommandArguments.forJob(plugin).required())
            .executes((context, arguments) -> viewLevels(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, OBJECTIVES_ALIAS)
            .playerOnly()
            .description(Lang.COMMAND_OBJECTIVES_DESC)
            .permission(Perms.COMMAND_OBJECTIVES)
            .withArgument(CommandArguments.forJob(plugin).required())
            .executes((context, arguments) -> viewObjectives(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "reset")
            .description(Lang.COMMAND_RESET_DESC)
            .permission(Perms.COMMAND_RESET)
            .withArgument(CommandArguments.forJob(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_RESET_OTHERS))
            .withFlag(CommandFlags.silent().permission(Perms.COMMAND_RESET_OTHERS))
            .executes((context, arguments) -> resetProgress(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "setstate")
            .description(Lang.COMMAND_SET_STATE_DESC)
            .permission(Perms.COMMAND_SET_STATE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.forJob(plugin).required())
            .withArgument(CommandArguments.forJobState(plugin).required())
            .executes((context, arguments) -> setState(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "stats")
            .description(Lang.COMMAND_STATS_DESC)
            .permission(Perms.COMMAND_STATS)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_STATS_OTHERS))
            .executes((context, arguments) -> viewStats(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "level")
            .description(Lang.COMMAND_LEVEL_DESC)
            .permission(Perms.COMMAND_LEVEL)
            .withArgument(CommandArguments.forAction(plugin).required())
            .withArgument(CommandArguments.forJob(plugin).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "2", "3", "4", "5")))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> manageLevel(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "xp")
            .description(Lang.COMMAND_XP_DESC)
            .permission(Perms.COMMAND_XP)
            .withArgument(CommandArguments.forAction(plugin).required())
            .withArgument(CommandArguments.forJob(plugin).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("10", "20", "30", "40", "50")))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> manageXP(plugin, context, arguments))
        );
    }

    private static boolean joinJob(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();
        plugin.getJobManager().joinJob(player, job);
        return true;
    }

    private static boolean leaveJob(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();
        plugin.getJobManager().leaveJob(player, job);
        return true;
    }

    private static boolean openMenu(@NotNull JobsPlugin plugin, @NotNull CommandContext context) {
        Player player = context.getPlayerOrThrow();
        plugin.getJobManager().openJobsMenu(player);
        return true;
    }

    private static boolean viewLevels(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();
        if (!job.hasPermission(player)) {
            context.errorPermission();
            return false;
        }

        plugin.getJobManager().openRewardsMenu(player, job);
        return true;
    }


    private static boolean viewObjectives(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();
        if (!job.hasPermission(player)) {
            context.errorPermission();
            return false;
        }

        plugin.getJobManager().openObjectivesMenu(player, job);
        return true;
    }

    private static boolean resetProgress(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());
        boolean isAdmin = arguments.hasArgument(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player target = user.getPlayer();
            plugin.getJobManager().handleJobReset(user, job, target, isAdmin, arguments.hasFlag(CommandFlags.SILENT));

            if (!context.getSender().getName().equalsIgnoreCase(user.getName())) {
                context.send(Lang.COMMAND_RESET_DONE, replacer -> replacer
                    .replace(job.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName()));
            }
        });
        return true;
    }

    private static boolean setState(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        JobState state = arguments.getArgument(CommandArguments.STATE, JobState.class);
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            JobData jobData = user.getData(job);
            jobData.setState(state);
            jobData.update();
            plugin.getUserManager().save(user);

            context.send(Lang.COMMAND_SET_STATE_DONE, replacer -> replacer
                .replace(Placeholders.GENERIC_STATE, Lang.JOB_STATE.getLocalized(state))
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(job.replacePlaceholders()));
        });
        return true;
    }

    private static boolean viewStats(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().getUserDataAsync(playerName).thenAccept(user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            context.send(Lang.COMMAND_STATS_DISPLAY, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    user.getDatas().forEach(jobData -> {
                        list.add(jobData.replaceAllPlaceholders().apply(Lang.COMMAND_STATS_ENTRY.getString()));
                    });
                }));
        });
        return true;
    }

    private static boolean manageLevel(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ModifyAction mode = arguments.getArgument(CommandArguments.ACTION, ModifyAction.class);
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        if (amount == 0) return false;

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player target = user.getPlayer();
            boolean silent = arguments.hasFlag(CommandFlags.SILENT);

            LangText doneMsg;

            switch (mode) {
                case ADD -> {
                    plugin.getJobManager().handleLevelAdd(user, job, amount, target, silent);
                    doneMsg = Lang.COMMAND_LEVEL_ADD_DONE;
                }
                case REMOVE -> {
                    plugin.getJobManager().handleLevelRemove(user, job, amount, target, silent);
                    doneMsg = Lang.COMMAND_LEVEL_REMOVE_DONE;
                }
                case SET -> {
                    plugin.getJobManager().handleLevelSet(user, job, amount, target, silent);
                    doneMsg = Lang.COMMAND_LEVEL_SET_DONE;
                }
                default -> {
                    return;
                }
            }

            context.send(doneMsg, replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount));
        });

        return true;
    }

    private static boolean manageXP(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ModifyAction mode = arguments.getArgument(CommandArguments.ACTION, ModifyAction.class);
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        if (amount == 0 && mode != ModifyAction.SET) {
            return false;
        }

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player target = user.getPlayer();
            boolean silent = arguments.hasFlag(CommandFlags.SILENT);

            LangText doneMsg;

            switch (mode) {
                case ADD -> {
                    plugin.getJobManager().handleXPAdd(user, job, amount, silent, target);
                    doneMsg = Lang.COMMAND_XP_ADD_DONE;
                }
                case REMOVE -> {
                    plugin.getJobManager().handleXPRemove(user, job, amount, silent, target);
                    doneMsg = Lang.COMMAND_XP_REMOVE_DONE;
                }
                case SET -> {
                    plugin.getJobManager().handleXPSet(user, job, amount, target);
                    doneMsg = Lang.COMMAND_XP_SET_DONE;
                }
                default -> {
                    return;
                }
            }

            context.send(doneMsg, replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount));
        });
        return true;
    }
}
