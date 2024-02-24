package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Players;

import java.util.List;

public class StatsCommand extends AbstractCommand<JobsPlugin> {

    public StatsCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"stats"}, Perms.COMMAND_STATS);
        this.setDescription(Lang.COMMAND_STATS_DESC);
        this.setUsage(Lang.COMMAND_STATS_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(Perms.COMMAND_STATS_OTHERS)) {
            return Players.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() > 1 && !sender.hasPermission(Perms.COMMAND_STATS_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(result.getArg(1, sender.getName())).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            Lang.COMMAND_STATS_DISPLAY.getMessage()
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    user.getDatas().forEach(jobData -> {
                        list.add(jobData.replacePlaceholders().apply(Lang.COMMAND_STATS_ENTRY.getString()));
                    });
                }).send(sender);
        });
    }
}
