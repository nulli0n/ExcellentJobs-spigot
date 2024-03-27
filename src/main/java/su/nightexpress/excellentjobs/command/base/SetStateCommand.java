package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.List;

public class SetStateCommand extends AbstractCommand<JobsPlugin> {

    public SetStateCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"setstate"}, Perms.COMMAND_SET_STATE);
        this.setDescription(Lang.COMMAND_SET_STATE_DESC);
        this.setUsage(Lang.COMMAND_SET_STATE_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Players.playerNames(player);
        }
        if (arg == 2) {
            return plugin.getJobManager().getJobIds();
        }
        if (arg == 3) {
            return Lists.getEnums(JobState.class);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.errorUsage(sender);
            return;
        }

        Job job = this.plugin.getJobManager().getJobById(result.getArg(2));
        if (job == null) {
            Lang.JOB_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        JobState state = StringUtil.getEnum(result.getArg(3), JobState.class).orElse(null);
        if (state == null) {
            Lang.ERROR_INVALID_STATE.getMessage().send(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAndPerformAsync(result.getArg(1), user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            JobData jobData = user.getData(job);
            jobData.setState(state);
            jobData.normalize();
            this.plugin.getUserManager().saveAsync(user);

            Lang.COMMAND_SET_STATE_DONE.getMessage()
                .replace(Placeholders.GENERIC_STATE, plugin.getLangManager().getEnum(state))
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(job.replacePlaceholders())
                .send(sender);
        });
    }
}
