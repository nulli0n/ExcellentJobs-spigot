package su.nightexpress.excellentjobs.zone.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.command.CommandArguments;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;

public class ZoneCommands {

    public static final String DEF_ROOT_NAME   = "jobzone";
    public static final String DEF_WAND_NAME   = "wand";
    public static final String DEF_CREATE_NAME = "create";

    private static ServerCommand command;

    public static void load(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        command = RootCommand.chained(plugin, DEF_ROOT_NAME, builder -> builder
            .description(Lang.COMMAND_ZONE_DESC)
            .permission(Perms.COMMAND_ZONE)
            .addDirect(DEF_WAND_NAME, child -> child
                .playerOnly()
                .description(Lang.COMMAND_ZONE_WAND_DESC)
                .permission(Perms.COMMAND_ZONE_WAND)
                .withArgument(zoneArgument(manager))
                .executes((context, arguments) -> giveWand(manager, context, arguments))
            )
            .addDirect(DEF_CREATE_NAME, child -> child
                .playerOnly()
                .description(Lang.COMMAND_ZONE_CREATE_DESC)
                .permission(Perms.COMMAND_ZONE_CREATE)
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(Lang.COMMAND_ARGUMENT_NAME_NAME).required())
                .executes((context, arguments) -> createZone(manager, context, arguments))
            )
            .addDirect("editor", child -> child
                .playerOnly()
                .description(Lang.COMMAND_ZONE_EDITOR_DESC)
                .permission(Perms.COMMAND_ZONE_EDITOR)
                .executes((context, arguments) -> openEditor(manager, context))
            )
        );

        plugin.getCommandManager().registerCommand(command);
    }

    public static void unload(@NotNull JobsPlugin plugin) {
        if (command != null) {
            plugin.getCommandManager().unregisterCommand(command);
            command = null;
        }
    }

    @NotNull
    private static ArgumentBuilder<Zone> zoneArgument(@NotNull ZoneManager manager) {
        return CommandArgument.builder(CommandArguments.ZONE, (string, context) -> manager.getZoneById(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_ZONE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_ZONE_ARGUMENT)
            .withSamples(context -> manager.getZoneIds());
    }

    private static boolean giveWand(@NotNull ZoneManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Zone zone = null;
        if (arguments.hasArgument(CommandArguments.ZONE)) {
            zone = arguments.getArgument(CommandArguments.ZONE, Zone.class);
        }

        Player player = context.getPlayerOrThrow();
        manager.startSelection(player, zone);
        return true;
    }

    private static boolean createZone(@NotNull ZoneManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        manager.defineZone(player, arguments.getStringArgument(CommandArguments.NAME));
        return true;
    }

    private static boolean openEditor(@NotNull ZoneManager manager, @NotNull CommandContext context) {
        Player player = context.getPlayerOrThrow();
        manager.openEditor(player);
        return true;
    }
}
