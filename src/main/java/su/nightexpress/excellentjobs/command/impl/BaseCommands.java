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

    public static void load(@NotNull JobsPlugin plugin) {
        var root = plugin.getRootNode();

        root.addChildren(ReloadCommand.builder(plugin, Perms.COMMAND_RELOAD));

        var menuNode = DirectNode.builder(plugin, "menu")
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

        root.addChildren(DirectNode.builder(plugin, "objectives")
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
            .executes((context, arguments) -> resetStats(plugin, context, arguments))
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
        plugin.getJobManager().openPreviewMenu(player, job);
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

    private static boolean resetStats(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            JobData jobData = user.getData(job);
            jobData.reset();
            if (!playerName.equalsIgnoreCase(user.getName())) {
                jobData.resetAdditional();
            }
            plugin.getUserManager().save(user);

            if (!context.getSender().getName().equalsIgnoreCase(user.getName())) {
                context.send(Lang.COMMAND_RESET_DONE, replacer -> replacer
                    .replace(jobData.replaceAllPlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName()));
            }

            Player target = user.getPlayer();
            if (target != null && !arguments.hasFlag(CommandFlags.SILENT)) {
                Lang.JOB_RESET_NOTIFY.getMessage().send(target, replacer -> replacer.replace(jobData.replaceAllPlaceholders()));
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
            jobData.normalize();
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
            JobData data = user.getData(job);

            if (target != null) {
                int level = data.getLevel();
                int modified = mode.modify(level, amount);
                int add = modified - level;
                plugin.getJobManager().addLevel(target, job, add);
            }
            else {
                data.setLevel(mode.modify(data.getLevel(), amount));
                data.normalize();
            }

            plugin.getUserManager().save(user);

            LangText doneMsg;
            LangText notifyMsg;

            switch (mode) {
                case ADD -> {
                    doneMsg = Lang.COMMAND_LEVEL_ADD_DONE;
                    notifyMsg = Lang.COMMAND_LEVEL_ADD_NOTIFY;
                }
                case REMOVE -> {
                    doneMsg = Lang.COMMAND_LEVEL_REMOVE_DONE;
                    notifyMsg = Lang.COMMAND_LEVEL_REMOVE_NOTIFY;
                }
                case SET -> {
                    doneMsg = Lang.COMMAND_LEVEL_SET_DONE;
                    notifyMsg = Lang.COMMAND_LEVEL_SET_NOTIFY;
                }
                default -> {
                    return;
                }
            }

            context.send(doneMsg, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount));

            if (target != null && !arguments.hasFlag(CommandFlags.SILENT)) {
                notifyMsg.getMessage().send(target, replacer -> replacer
                    .replace(data.replaceAllPlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, amount));
            }
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
            JobData data = user.getData(job);

            if (target != null) {
                int xp = data.getXP();
                int modified = mode.modify(xp, amount);
                int add = modified - xp;
                plugin.getJobManager().addXP(target, job, add);
            }
            else {
                data.setXP(mode.modify(data.getXP(), amount));
                data.normalize();
            }

            plugin.getUserManager().save(user);

            LangText doneMsg;
            LangText notifyMsg;

            switch (mode) {
                case ADD -> {
                    doneMsg = Lang.COMMAND_XP_ADD_DONE;
                    notifyMsg = Lang.COMMAND_XP_ADD_NOTIFY;
                }
                case REMOVE -> {
                    doneMsg = Lang.COMMAND_XP_REMOVE_DONE;
                    notifyMsg = Lang.COMMAND_XP_REMOVE_NOTIFY;
                }
                case SET -> {
                    doneMsg = Lang.COMMAND_XP_SET_DONE;
                    notifyMsg = Lang.COMMAND_XP_SET_NOTIFY;
                }
                default -> {
                    return;
                }
            }

            context.send(doneMsg, replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount));

            if (target != null && !arguments.hasFlag(CommandFlags.SILENT)) {
                notifyMsg.getMessage().send(target, replacer -> replacer
                    .replace(data.replaceAllPlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, amount));
            }
        });
        return true;
    }
}
