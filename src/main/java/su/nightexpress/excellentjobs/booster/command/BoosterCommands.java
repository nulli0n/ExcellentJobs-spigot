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
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.ArrayList;

public class BoosterCommands {

    private static final String ALIAS_BOOSTS = "boosts";
    private static final String ALIAS_BOOSTER = "booster";

    public static void load(@NotNull JobsPlugin plugin, @NotNull BoosterManager manager) {
        ChainedNode root = plugin.getRootNode();

        root.addChildren(DirectNode.builder(plugin, ALIAS_BOOSTS)
            .playerOnly()
            .description(Lang.COMMAND_BOOSTS_DESC)
            .permission(Perms.COMMAND_BOOSTS)
            .withArgument(CommandArguments.forJob(plugin).required())
            .executes((context, arguments) -> display(manager, context, arguments))
        );

        root.addChildren(ChainedNode.builder(plugin, ALIAS_BOOSTER)
            .description(Lang.COMMAND_BOOSTER_DESC)
            .permission(Perms.COMMAND_BOOSTER)
            .addDirect("create", builder -> builder
                .description(Lang.COMMAND_BOOSTER_CREATE_DESC)
                .withArgument(ArgumentTypes.decimalAbs(CommandArguments.XP_MULTIPLIER)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_XP_MULTIPLIER)
                    .withSamples(tabContext -> Lists.newList("1.5", "2.0", "2.5", "3.0")))
                .withArgument(ArgumentTypes.decimalAbs(CommandArguments.INCOME_MULTIPLIER)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_PAY_MULTIPLIER)
                    .withSamples(tabContext -> Lists.newList("1.5", "2.0", "2.5", "3.0")))
                .withArgument(ArgumentTypes.integerAbs(CommandArguments.DURATION)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_DURATION)
                    .withSamples(tabContext -> Lists.newList("3600", "7200", "86400")))
                .executes((context, arguments) -> create(manager, context, arguments))
            )
            .addDirect("activate", builder -> builder
                .description(Lang.COMMAND_BOOSTER_ACTIVATE_DESC)
                .withArgument(ArgumentTypes.string(CommandArguments.NAME)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
                    .withSamples(tabContext -> new ArrayList<>(BoosterConfig.getBoosterScheduleMap().keySet())))
                .executes((context, arguments) -> activate(manager, context, arguments))
            )
            .addDirect("give", builder -> builder
                .description(Lang.COMMAND_BOOSTER_CREATE_DESC)
                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                .withArgument(CommandArguments.forJob(plugin).required())
                .withArgument(ArgumentTypes.decimalAbs(CommandArguments.XP_MULTIPLIER)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_XP_MULTIPLIER)
                    .withSamples(tabContext -> Lists.newList("1.5", "2.0", "2.5", "3.0")))
                .withArgument(ArgumentTypes.decimalAbs(CommandArguments.INCOME_MULTIPLIER)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_PAY_MULTIPLIER)
                    .withSamples(tabContext -> Lists.newList("1.5", "2.0", "2.5", "3.0")))
                .withArgument(ArgumentTypes.integerAbs(CommandArguments.DURATION)
                    .required()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_DURATION)
                    .withSamples(tabContext -> Lists.newList("3600", "7200", "86400")))
                .executes((context, arguments) -> give(plugin, manager, context, arguments))
            )
            .addDirect("stop", builder -> builder
                .description(Lang.COMMAND_BOOSTER_REMOVE_DESC)
                .executes((context, arguments) -> stop(manager, context))
            )
            .addDirect("remove", builder -> builder
                .description(Lang.COMMAND_BOOSTER_REMOVE_DESC)
                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                .withArgument(CommandArguments.forJob(plugin).required())
                .executes((context, arguments) -> remove(plugin, context, arguments))
            )
        );
    }

    public static void unload(@NotNull JobsPlugin plugin) {
        var root = plugin.getRootNode();

        root.removeChildren(ALIAS_BOOSTS);
        root.removeChildren(ALIAS_BOOSTER);
    }

    private static boolean display(@NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        Player player = context.getPlayerOrThrow();

        manager.displayBoosterInfo(player, job);
        return true;
    }


    private static boolean create(@NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double xpMult = arguments.getDoubleArgument(CommandArguments.XP_MULTIPLIER);
        double payMult = arguments.getDoubleArgument(CommandArguments.INCOME_MULTIPLIER);
        int duration = arguments.getIntArgument(CommandArguments.DURATION);
        Booster booster = Booster.create(xpMult, payMult, duration);

        manager.setGlobalBooster(booster);

        Lang.COMMAND_BOOSTER_CREATE_DONE_GLOBAL.getMessage().send(context.getSender(), replacer -> replacer
            .replace(booster.replacePlaceholers())
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(booster.getExpireDate(), TimeFormatType.LITERAL))
        );

        return true;
    }

    private static boolean give(@NotNull JobsPlugin plugin, @NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        double xpMult = arguments.getDoubleArgument(CommandArguments.XP_MULTIPLIER);
        double payMult = arguments.getDoubleArgument(CommandArguments.INCOME_MULTIPLIER);
        int duration = arguments.getIntArgument(CommandArguments.DURATION);
        Booster booster = Booster.create(xpMult, payMult, duration);

        String playerName = arguments.getStringArgument(CommandArguments.PLAYER);
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

            Lang.COMMAND_BOOSTER_CREATE_DONE_PERSONAL.getMessage().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(booster.replacePlaceholers())
                .replace(job.replacePlaceholders())
                .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(booster.getExpireDate(), TimeFormatType.LITERAL))
            );
        });

        return true;
    }

    private static boolean activate(@NotNull BoosterManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.NAME);
        if (manager.activateBoosterById(name)) {
            context.send(Lang.COMMAND_BOOSTER_ACTIVATE_DONE);
        }
        else {
            context.send(Lang.ERROR_INVALID_BOOSTER);
        }
        return true;
    }

    private static boolean stop(@NotNull BoosterManager manager, @NotNull CommandContext context) {
        if (!manager.hasGlobalBoost()) {
            context.send(Lang.COMMAND_BOOSTER_REMOVE_ERROR_NOTHING.getMessage());
            return false;
        }

        manager.removeGlobalBooster();
        Lang.COMMAND_BOOSTER_REMOVE_DONE_GLOBAL.getMessage().send(context.getSender());
        return true;
    }

    private static boolean remove(@NotNull JobsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.getArgument(CommandArguments.JOB, Job.class);
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            if (!user.hasBooster(job)) {
                context.send(Lang.COMMAND_BOOSTER_REMOVE_ERROR_NOTHING.getMessage());
                return;
            }

            user.removeBooster(job);
            plugin.getUserManager().save(user);
            Lang.COMMAND_BOOSTER_REMOVE_DONE_PERSONAL.getMessage().send(context.getSender(), replacer -> replacer
                .replace(job.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName()));
        });
        return true;
    }
}
