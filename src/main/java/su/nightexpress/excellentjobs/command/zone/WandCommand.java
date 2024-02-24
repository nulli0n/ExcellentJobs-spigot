package su.nightexpress.excellentjobs.command.zone;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.command.CommandResult;

import java.util.List;

class WandCommand extends ZoneSubCommand {

    public WandCommand(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, zoneManager, new String[]{"wand"});
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return this.zoneManager.getZoneIds();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.errorUsage(sender);
            return;
        }

        Zone zone = this.zoneManager.getZoneById(result.getArg(2));
        if (zone == null) {

            return;
        }

        Player player = (Player) sender;
        this.zoneManager.giveWand(player, zone);
    }
}
