package su.nightexpress.excellentjobs.command.booster;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.base.HelpSubCommand;
import su.nightexpress.nightcore.command.impl.PluginCommand;

public class BoosterCommand extends PluginCommand<JobsPlugin> {

    public BoosterCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"booster"}, Perms.COMMAND_BOOSTER);
        this.setDescription(Lang.COMMAND_BOOSTER_DESC);
        this.setUsage(Lang.COMMAND_BOOSTER_USAGE);

        this.addDefaultCommand(new HelpSubCommand(plugin));
        this.addChildren(new CreateSubCommand(plugin));
        this.addChildren(new ClearSubCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
