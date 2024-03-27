package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

import java.util.List;

public class LeaveCommand extends AbstractCommand<JobsPlugin> {

    public LeaveCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"leave"}, Perms.COMMAND_LEAVE);
        this.setDescription(Lang.COMMAND_LEAVE_DESC);
        this.setUsage(Lang.COMMAND_LEAVE_USAGE);
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            JobUser user = plugin.getUserManager().getUserData(player);
            return user.getDatas().stream().filter(JobData::isActive).map(JobData::getJob).map(Job::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.errorUsage(sender);
            return;
        }

        Job job = this.plugin.getJobManager().getJobById(result.getArg(1));
        if (job == null) {
            Lang.JOB_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        Player player = (Player) sender;

        this.plugin.getJobManager().leaveJob(player, job);
    }
}
