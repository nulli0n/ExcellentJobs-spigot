package su.nightexpress.excellentjobs.booster.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.booster.BoosterManager;
import su.nightexpress.excellentjobs.booster.config.BoosterConfig;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.command.CommandArguments;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.ArrayList;

public class BoosterCommands {

    private static final String ALIAS_BOOSTS = "boosts";
    private static final String ALIAS_BOOSTER = "booster";

    public static void load(@NotNull JobsPlugin plugin, @NotNull BoosterManager manager, @NotNull HubNodeBuilder builder) {
        builder
            .branch(Commands.literal(ALIAS_BOOSTS)
                .playerOnly()
                .description(Lang.COMMAND_BOOSTS_DESC.text())
                .permission(Perms.COMMAND_BOOSTS)
                .withArguments(CommandArguments.forJob(plugin))
                .executes((context, arguments) -> display(manager, context, arguments))
            )
            .branch(Commands.hub(ALIAS_BOOSTER)
                .description(Lang.COMMAND_BOOSTER_DESC.text())
                .permission(Perms.COMMAND_BOOSTER)
                .branch(Commands.literal("create")
                    .description(Lang.COMMAND_BOOSTER_CREATE_DESC.text())
                    .withArguments(
                        Arguments.decimal(CommandArguments.XP_MULTIPLIER, 0)
                            .localized(Lang.COMMAND_ARGUMENT_NAME_XP_MULTIPLIER.text())
                            .suggestions((reader, context) -> Lists.newList("1.5", "2.0", "2.5", "3.0")),
                        Arguments.decimal(CommandArguments.INCOME_MULTIPLIER, 0)
                            .localized(Lang.COMMAND_ARGUMENT_NAME_PAY_MULTIPLIER.text())
                            .suggestions((reader, context) -> Lists.newList("1.5", "2.0", "2.5", "3.0")),
                        Arguments.decimal(CommandArguments.DURATION, 1)
                            .localized(Lang.COMMAND_ARGUMENT_NAME_DURATION.text())
                            .suggestions((reader, context) -> Lists.newList("3600", "7200", "86400"))
                    )
                    .executes((context, arguments) -> create(manager, context, arguments))
                )
                .branch(Commands.literal("activate")
                    .description(Lang.COMMAND_BOOSTER_ACTIVATE_DESC.text())
                    .withArguments(Arguments.string(CommandArguments.NAME)
                        .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME.text())
                        .suggestions((reader, context) -> new ArrayList<>(BoosterConfig.getBoosterScheduleMap().keySet())))
                    .executes((context, arguments) -> activate(manager, context, arguments))
                )
                .branch(Commands.literal("give")
                    .description(Lang.COMMAND_BOOSTER_CREATE_DESC.text())
                    .withArguments(
                        Arguments.playerName(CommandArguments.PLAYER),
                        CommandArguments.forJob(plugin),
                        Arguments.decimal(CommandArguments.XP_MULTIPLIER, 0)
                            .localized(Lang.COMMAND_ARGUMENT_NAME_XP_MULTIPLIER.text())
                            .suggestions((reader, context) -> Lists.newList("1.5", "2.0", "2.5", "3.0")),
                        Arguments.decimal(CommandArguments.INCOME_MULTIPLIER, 0)
                            .localized(Lang.COMMAND_ARGUMENT_NAME_PAY_MULTIPLIER.text())
                            .suggestions((reader, context) -> Lists.newList("1.5", "2.0", "2.5", "3.0")),
                        Arguments.decimal(CommandArguments.DURATION, 1)
                            .localized(Lang.COMMAND_ARGUMENT_NAME_DURATION.text())
                            .suggestions((reader, context) -> Lists.newList("3600", "7200", "86400"))
                    )
                    .executes((context, arguments) -> give(plugin, manager, context, arguments))
                )
                .branch(Commands.literal("stop")
                    .description(Lang.COMMAND_BOOSTER_REMOVE_DESC.text())
                    .executes((context, arguments) -> stop(manager, context))
                )
                .branch(Commands.literal("remove")
                    .description(Lang.COMMAND_BOOSTER_REMOVE_DESC.text())
                    .withArguments(
                        Arguments.playerName(CommandArguments.PLAYER),
                        CommandArguments.forJob(plugin)
                    )
                    .executes((context, arguments) -> remove(plugin, context, arguments))
                )
            );
    }

    private static boolean display(@NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();

        manager.displayBoosterInfo(player, job);
        return true;
    }

    private static boolean create(@NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double xpMult = arguments.getDouble(CommandArguments.XP_MULTIPLIER);
        double payMult = arguments.getDouble(CommandArguments.INCOME_MULTIPLIER);
        int duration = arguments.getInt(CommandArguments.DURATION);
        Booster booster = Booster.create(xpMult, payMult, duration);

        manager.setGlobalBooster(booster);

        Lang.COMMAND_BOOSTER_CREATE_DONE_GLOBAL.message().send(context.getSender(), replacer -> replacer
            .replace(booster.replacePlaceholers())
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(booster.getExpireDate(), TimeFormatType.LITERAL))
        );

        return true;
    }

    private static boolean give(@NotNull JobsPlugin plugin, @NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        double xpMult = arguments.getDouble(CommandArguments.XP_MULTIPLIER);
        double payMult = arguments.getDouble(CommandArguments.INCOME_MULTIPLIER);
        int duration = arguments.getInt(CommandArguments.DURATION);
        Booster booster = Booster.create(xpMult, payMult, duration);

        String playerName = arguments.getString(CommandArguments.PLAYER);
        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.addBooster(job, booster);
            plugin.getUserManager().save(user);

            Player target = user.getPlayer();
            if (target != null) {
                manager.notifyPersonalBooster(target, job, booster);
            }

            Lang.COMMAND_BOOSTER_CREATE_DONE_PERSONAL.message().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(booster.replacePlaceholers())
                .replace(job.replacePlaceholders())
                .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(booster.getExpireDate(), TimeFormatType.LITERAL))
            );
        });

        return true;
    }

    private static boolean activate(@NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.NAME);
        if (manager.activateBoosterById(name)) {
            Lang.COMMAND_BOOSTER_ACTIVATE_DONE.message().send(context.getSender());
        }
        else {
            Lang.ERROR_INVALID_BOOSTER.message().send(context.getSender());
        }
        return true;
    }

    private static boolean stop(@NotNull BoosterManager manager, @NotNull CommandContext context) {
        if (!manager.hasGlobalBoost()) {
            Lang.COMMAND_BOOSTER_REMOVE_ERROR_NOTHING.message().send(context.getSender());
            return false;
        }

        manager.removeGlobalBooster();
        Lang.COMMAND_BOOSTER_REMOVE_DONE_GLOBAL.message().send(context.getSender());
        return true;
    }

    private static boolean remove(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        String playerName = arguments.getString(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            if (!user.hasBooster(job)) {
                Lang.COMMAND_BOOSTER_REMOVE_ERROR_NOTHING.message().send(context.getSender());
                return;
            }

            user.removeBooster(job);
            plugin.getUserManager().save(user);
            Lang.COMMAND_BOOSTER_REMOVE_DONE_PERSONAL.message().send(context.getSender(), replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName()));
        });
        return true;
    }
}
