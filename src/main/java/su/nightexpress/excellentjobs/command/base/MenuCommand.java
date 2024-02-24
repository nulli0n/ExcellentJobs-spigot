package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

public class MenuCommand extends AbstractCommand<JobsPlugin> {

    public MenuCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"menu"}, Perms.COMMAND_MENU);
        this.setDescription(Lang.COMMAND_MENU_DESC);
        this.setUsage(Lang.COMMAND_MENU_USAGE);
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        plugin.getJobManager().openJobsMenu(player);
    }
}
