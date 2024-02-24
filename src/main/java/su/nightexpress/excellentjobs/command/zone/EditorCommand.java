package su.nightexpress.excellentjobs.command.zone;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.command.CommandResult;

class EditorCommand extends ZoneSubCommand {

    public EditorCommand(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, zoneManager, new String[]{"editor"});
        this.setDescription(Lang.COMMAND_ZONE_EDITOR_DESC);

        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.zoneManager.openEditor(player);
    }
}
