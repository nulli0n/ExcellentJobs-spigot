package su.nightexpress.excellentjobs.command.booster;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Players;

import java.util.List;

class ClearSubCommand extends AbstractCommand<JobsPlugin> {

    public ClearSubCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"clear"}, Perms.COMMAND_BOOSTER);
        this.setDescription(Lang.COMMAND_BOOSTER_CLEAR_DESC);
        this.setUsage(Lang.COMMAND_BOOSTER_CLEAR_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return Players.playerNames(player);
        }
        if (arg == 3) {
            return this.plugin.getJobManager().getJobIds();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.errorUsage(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAndPerformAsync(result.getArg(2), user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            if (result.length() >= 4) {
                Job job = this.plugin.getJobManager().getJobById(result.getArg(3));
                if (job == null) {
                    Lang.JOB_ERROR_INVALID.getMessage().send(sender);
                    return;
                }
                user.getBoosterMap().remove(job.getId());

                Lang.COMMAND_BOOSTER_CLEAR_DONE_JOB.getMessage()
                    .replace(job.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }
            else {
                user.getBoosterMap().clear();

                Lang.COMMAND_BOOSTER_CLEAR_DONE_ALL.getMessage()
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }

            this.plugin.getUserManager().saveAsync(user);
        });
    }
}
