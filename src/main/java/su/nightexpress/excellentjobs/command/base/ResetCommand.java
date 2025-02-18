package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.command.CommandFlags;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Players;

import java.util.List;

public class ResetCommand extends AbstractCommand<JobsPlugin> {

    public ResetCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"reset"}, Perms.COMMAND_RESET);
        this.setDescription(Lang.COMMAND_RESET_DESC);
        this.setUsage(Lang.COMMAND_RESET_USAGE);
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.plugin.getJobManager().getJobIds();
        }
        if (arg == 2 && player.hasPermission(Perms.COMMAND_RESET_OTHERS)) {
            return Players.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.errorUsage(sender);
            return;
        }
        if (result.length() >= 3 && !sender.hasPermission(Perms.COMMAND_RESET_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        Job job = plugin.getJobManager().getJobById(result.getArg(1));
        if (job == null) {
            Lang.JOB_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAndPerformAsync(result.getArg(2), user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            JobData jobData = user.getData(job);
            jobData.reset();
            this.plugin.getUserManager().saveAsync(user);

            if (!sender.getName().equalsIgnoreCase(user.getName())) {
                Lang.COMMAND_RESET_DONE.getMessage()
                    .replace(jobData.replaceAllPlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }

            Player target = user.getPlayer();
            if (target != null && !result.hasFlag(CommandFlags.SILENT)) {
                Lang.JOB_RESET_NOTIFY.getMessage().replace(jobData.replaceAllPlaceholders()).send(target);
            }
        });
    }
}
