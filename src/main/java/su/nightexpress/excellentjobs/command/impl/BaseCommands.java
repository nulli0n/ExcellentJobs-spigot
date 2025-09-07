package su.nightexpress.excellentjobs.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.ModifyAction;
import su.nightexpress.excellentjobs.command.CommandArguments;
import su.nightexpress.excellentjobs.command.CommandFlags;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.util.Lists;

public class BaseCommands {

    public static final String JOBS_ALIAS = "jobs";

    public static final String MENU_ALIAS       = "menu";
    public static final String LEVELS_ALIAS     = "levels";

    public static void load(@NotNull JobsPlugin plugin, @NotNull HubNodeBuilder builder) {
        builder
            .branch(Commands.literal("reload")
                .description(CoreLang.COMMAND_RELOAD_DESC)
                .permission(Perms.COMMAND_RELOAD)
                .executes((context, arguments) -> {
                    plugin.doReload(context.getSender());
                    return true;
                }))
            .branch(Commands.literal(MENU_ALIAS)
                .playerOnly()
                .description(Lang.COMMAND_MENU_DESC.text())
                .permission(Perms.COMMAND_MENU)
                .executes((context, arguments) -> openMenu(plugin, context))
            )
            .branch(Commands.literal("join")
                .playerOnly()
                .description(Lang.COMMAND_JOIN_DESC.text())
                .permission(Perms.COMMAND_JOIN)
                .withArguments(CommandArguments.forJob(plugin))
                .executes((context, arguments) -> joinJob(plugin, context, arguments))
            )
            .branch(Commands.literal("leave")
                .playerOnly()
                .description(Lang.COMMAND_LEAVE_DESC.text())
                .permission(Perms.COMMAND_LEAVE)
                .withArguments(CommandArguments.forJob(plugin))
                .executes((context, arguments) -> leaveJob(plugin, context, arguments))
            )
            .branch(Commands.literal(LEVELS_ALIAS)
                .playerOnly()
                .description(Lang.COMMAND_LEVELS_DESC.text())
                .permission(Perms.COMMAND_LEVELS)
                .withArguments(CommandArguments.forJob(plugin))
                .executes((context, arguments) -> viewLevels(plugin, context, arguments))
            )
            .branch(Commands.literal("reset")
                .description(Lang.COMMAND_RESET_DESC.text())
                .permission(Perms.COMMAND_RESET)
                .withArguments(
                    CommandArguments.forJob(plugin),
                    Arguments.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_RESET_OTHERS)
                )
                .withFlags(CommandFlags.SILENT)
                .executes((context, arguments) -> resetProgress(plugin, context, arguments))
            )
            .branch(Commands.literal("setstate")
                .description(Lang.COMMAND_SET_STATE_DESC.text())
                .permission(Perms.COMMAND_SET_STATE)
                .withArguments(
                    Arguments.playerName(CommandArguments.PLAYER),
                    CommandArguments.forJob(plugin),
                    CommandArguments.forJobState(plugin)
                )
                .executes((context, arguments) -> setState(plugin, context, arguments))
            )
            .branch(Commands.literal("stats")
                .description(Lang.COMMAND_STATS_DESC.text())
                .permission(Perms.COMMAND_STATS)
                .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_STATS_OTHERS))
                .executes((context, arguments) -> viewStats(plugin, context, arguments))
            )
            .branch(Commands.literal("level")
                .description(Lang.COMMAND_LEVEL_DESC.text())
                .permission(Perms.COMMAND_LEVEL)
                .withArguments(
                    CommandArguments.forAction(plugin),
                    CommandArguments.forJob(plugin),
                    Arguments.integer(CommandArguments.AMOUNT, 1)
                        .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT.text())
                        .suggestions((reader, context) -> Lists.newList("1", "2", "3", "4", "5")),
                    Arguments.playerName(CommandArguments.PLAYER).optional()
                )
                .withFlags(CommandFlags.SILENT)
                .executes((context, arguments) -> manageLevel(plugin, context, arguments))
            )
            .branch(Commands.literal("xp")
                .description(Lang.COMMAND_XP_DESC.text())
                .permission(Perms.COMMAND_XP)
                .withArguments(
                    CommandArguments.forAction(plugin),
                    CommandArguments.forJob(plugin),
                    Arguments.integer(CommandArguments.AMOUNT, 1)
                        .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT.text())
                        .suggestions((reader, context) -> Lists.newList("10", "20", "30", "40", "50")),
                    Arguments.playerName(CommandArguments.PLAYER).optional()
                )
                .withFlags(CommandFlags.SILENT)
                .executes((context, arguments) -> manageXP(plugin, context, arguments))
            );

        if (Config.GENERAL_DEFAULT_MENU_COMMAND_ENABLED.get()) {
            builder.executes((context, arguments) -> openMenu(plugin, context));
        }
    }

    private static boolean joinJob(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();
        plugin.getJobManager().joinJob(player, job);
        return true;
    }

    private static boolean leaveJob(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
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
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();
        if (!job.hasPermission(player)) {
            context.errorPermission();
            return false;
        }

        plugin.getJobManager().openLevelsMenu(player, job);
        return true;
    }

    private static boolean resetProgress(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        String playerName = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());
        boolean isAdmin = arguments.contains(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player target = user.getPlayer();
            plugin.getJobManager().handleJobReset(user, job, target, isAdmin, context.hasFlag(CommandFlags.SILENT));

            if (!context.getSender().getName().equalsIgnoreCase(user.getName())) {
                Lang.COMMAND_RESET_DONE.message().send(context.getSender(), replacer -> replacer
                    .replace(job.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName()));
            }
        });
        return true;
    }

    private static boolean setState(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        JobState state = arguments.get(CommandArguments.STATE, JobState.class);
        String playerName = arguments.getString(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            JobData jobData = user.getData(job);
            jobData.setState(state);
            jobData.update();
            plugin.getUserManager().save(user);

            Lang.COMMAND_SET_STATE_DONE.message().send(context.getSender(),  replacer -> replacer
                .replace(Placeholders.GENERIC_STATE, Lang.JOB_STATE.getLocalized(state))
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(job.replacePlaceholders()));
        });
        return true;
    }

    private static boolean viewStats(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String playerName = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().getUserDataAsync(playerName).thenAccept(user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Lang.COMMAND_STATS_DISPLAY.message().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    user.getDatas().forEach(jobData -> {
                        list.add(jobData.replaceAllPlaceholders().apply(Lang.COMMAND_STATS_ENTRY.text()));
                    });
                }));
        });
        return true;
    }

    private static boolean manageLevel(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ModifyAction mode = arguments.get(CommandArguments.ACTION, ModifyAction.class);
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        int amount = arguments.getInt(CommandArguments.AMOUNT);
        String playerName = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());

        if (amount == 0) return false;

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player target = user.getPlayer();
            boolean silent = context.hasFlag(CommandFlags.SILENT);

            MessageLocale doneMsg;

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

            doneMsg.message().send(context.getSender(), replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount));
        });

        return true;
    }

    private static boolean manageXP(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ModifyAction mode = arguments.get(CommandArguments.ACTION, ModifyAction.class);
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        int amount = arguments.getInt(CommandArguments.AMOUNT);
        String playerName = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());

        if (amount == 0 && mode != ModifyAction.SET) {
            return false;
        }

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player target = user.getPlayer();
            boolean silent = context.hasFlag(CommandFlags.SILENT);

            MessageLocale doneMsg;

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

            doneMsg.message().send(context.getSender(), replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount));
        });
        return true;
    }
}
