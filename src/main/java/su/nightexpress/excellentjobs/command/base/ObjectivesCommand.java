package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

import java.util.List;

public class ObjectivesCommand extends AbstractCommand<JobsPlugin> {

    public ObjectivesCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"objectives"}, Perms.COMMAND_OBJECTIVES);
        this.setDescription(Lang.COMMAND_OBJECTIVES_DESC);
        this.setUsage(Lang.COMMAND_OBJECTIVES_USAGE);
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getJobManager().getJobs(player).stream().map(Job::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.errorUsage(sender);
            return;
        }

        Job job = plugin.getJobManager().getJobById(result.getArg(1));
        if (job == null) {
            Lang.JOB_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        Player player = (Player) sender;
        if (!job.hasPermission(player)) {
            this.errorPermission(sender);
            return;
        }

        this.plugin.getJobManager().openObjectivesMenu(player, job);
    }
}
