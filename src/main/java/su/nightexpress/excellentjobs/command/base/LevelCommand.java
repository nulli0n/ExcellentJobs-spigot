package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.command.CommandFlags;
import su.nightexpress.excellentjobs.command.CommandMode;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class LevelCommand extends AbstractCommand<JobsPlugin> {

    public LevelCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"level"}, Perms.COMMAND_LEVEL);
        this.setDescription(Lang.COMMAND_LEVEL_DESC);
        this.setUsage(Lang.COMMAND_LEVEL_USAGE);
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Lists.getEnums(CommandMode.class).stream().map(String::toLowerCase).toList();
        }
        if (arg == 2) {
            return this.plugin.getJobManager().getJobIds();
        }
        if (arg == 3) {
            return Arrays.asList("1", "5", "10");
        }
        if (arg == 4) {
            return Players.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.errorUsage(sender);
            return;
        }

        Job job = plugin.getJobManager().getJobById(result.getArg(2));
        if (job == null) {
            Lang.JOB_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        CommandMode mode = StringUtil.getEnum(result.getArg(1), CommandMode.class).orElse(CommandMode.SET);

        int amount = Math.abs(result.getInt(3, 0));
        if (amount == 0) {
            this.errorNumber(sender, result.getArg(3));
            return;
        }

        this.plugin.getUserManager().getUserDataAndPerformAsync(result.getArg(4, sender.getName()), user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            JobData jobData = user.getData(job);
            jobData.setLevel(mode.modify(jobData.getLevel(), amount));
            jobData.normalize();
            this.plugin.getUserManager().saveAsync(user);

            (switch (mode) {
                case ADD -> Lang.COMMAND_LEVEL_ADD_DONE;
                case REMOVE -> Lang.COMMAND_LEVEL_REMOVE_DONE;
                case SET -> Lang.COMMAND_LEVEL_SET_DONE;
            }).getMessage()
                .replace(jobData.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, amount)
                .send(sender);

            Player target = user.getPlayer();
            if (target != null && !result.hasFlag(CommandFlags.SILENT)) {
                (switch (mode) {
                    case ADD -> Lang.COMMAND_LEVEL_ADD_NOTIFY;
                    case REMOVE -> Lang.COMMAND_LEVEL_REMOVE_NOTIFY;
                    case SET -> Lang.COMMAND_LEVEL_SET_NOTIFY;
                }).getMessage()
                    .replace(jobData.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, amount)
                    .send(target);
            }
        });
    }
}
