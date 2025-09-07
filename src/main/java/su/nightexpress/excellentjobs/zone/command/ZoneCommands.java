package su.nightexpress.excellentjobs.zone.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.command.CommandArguments;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.command.HubCommand;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;

import java.util.Optional;

public class ZoneCommands {

    public static final String DEF_ROOT_NAME   = "jobzone";
    public static final String DEF_WAND_NAME   = "wand";
    public static final String DEF_CREATE_NAME = "create";

    private static HubCommand command;

    public static void load(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        command = NightCommand.hub(plugin, DEF_ROOT_NAME, builder -> builder
            .description(Lang.COMMAND_ZONE_DESC.text())
            .permission(Perms.COMMAND_ZONE)
            .branch(Commands.literal(DEF_WAND_NAME)
                .playerOnly()
                .description(Lang.COMMAND_ZONE_WAND_DESC.text())
                .permission(Perms.COMMAND_ZONE_WAND)
                .withArguments(zoneArgument(manager))
                .executes((context, arguments) -> giveWand(manager, context, arguments))
            )
            .branch(Commands.literal(DEF_CREATE_NAME)
                .playerOnly()
                .description(Lang.COMMAND_ZONE_CREATE_DESC.text())
                .permission(Perms.COMMAND_ZONE_CREATE)
                .withArguments(Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME.text()))
                .executes((context, arguments) -> createZone(manager, context, arguments))
            )
            .branch(Commands.literal("editor")
                .playerOnly()
                .description(Lang.COMMAND_ZONE_EDITOR_DESC.text())
                .permission(Perms.COMMAND_ZONE_EDITOR)
                .executes((context, arguments) -> openEditor(manager, context))
            )
        );
        command.register();
    }

    public static void unload(@NotNull JobsPlugin plugin) {
        if (command != null) {
            command.unregister();
            command = null;
        }
    }

    @NotNull
    private static ArgumentNodeBuilder<Zone> zoneArgument(@NotNull ZoneManager manager) {
        return Commands.argument(CommandArguments.ZONE, (context, string) -> Optional.ofNullable(manager.getZoneById(string)).orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_ZONE_ARGUMENT)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_ZONE.text())
            .suggestions((reader, context) -> manager.getZoneIds());
    }

    private static boolean giveWand(@NotNull ZoneManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Zone zone = arguments.getOrNull(CommandArguments.ZONE, Zone.class);
        Player player = context.getPlayerOrThrow();
        manager.startSelection(player, zone);
        return true;
    }

    private static boolean createZone(@NotNull ZoneManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        manager.defineZone(player, arguments.getString(CommandArguments.NAME));
        return true;
    }

    private static boolean openEditor(@NotNull ZoneManager manager, @NotNull CommandContext context) {
        Player player = context.getPlayerOrThrow();
        manager.openEditor(player);
        return true;
    }
}
