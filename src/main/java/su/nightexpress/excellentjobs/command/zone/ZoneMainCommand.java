package su.nightexpress.excellentjobs.command.zone;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.base.HelpSubCommand;
import su.nightexpress.nightcore.command.impl.PluginCommand;

public class ZoneMainCommand extends PluginCommand<JobsPlugin> {

    public ZoneMainCommand(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, new String[]{"zone"}, Perms.COMMAND_ZONE);
        this.setDescription(Lang.COMMAND_ZONE_DESC);
        this.setUsage(Lang.COMMAND_ZONE_USAGE);

        this.addDefaultCommand(new HelpSubCommand(plugin));
        this.addChildren(new EditorCommand(plugin, zoneManager));
        this.addChildren(new WandCommand(plugin, zoneManager));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
