package su.nightexpress.excellentjobs.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopCommand extends AbstractCommand<JobsPlugin> {

    private final StatsManager statsManager;

    public TopCommand(@NotNull JobsPlugin plugin, @NotNull StatsManager statsManager) {
        super(plugin, new String[]{"top"}, Perms.COMMAND_TOP);
        this.statsManager = statsManager;

        this.setDescription(Lang.COMMAND_TOP_DESC);
        this.setUsage(Lang.COMMAND_TOP_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.plugin.getJobManager().getJobIds();
        }
        if (arg == 2) {
            return Arrays.asList("1", "2", "3", "4", "5", "10", "20");
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

        int perPage = Config.STATISTIC_ENTRIES_PER_PAGE.get();

        List<TopEntry> full = this.statsManager.getLevelTopEntries(job);
        List<List<TopEntry>> split = Lists.split(full, perPage);
        int pages = split.size();
        int page = Math.max(0, Math.min(pages, Math.abs(result.getInt(2, 1))) - 1);

        List<TopEntry> stats = pages > 0 ? split.get(page) : new ArrayList<>();
        //AtomicInteger pos = new AtomicInteger(1 + perPage * page);

        Lang.COMMAND_TOP_LIST.getMessage()
            .replace(job.replacePlaceholders())
            .replace(Placeholders.GENERIC_CURRENT, page + 1)
            .replace(Placeholders.GENERIC_MAX, pages)
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                for (TopEntry entry : stats) {
                    list.add(Lang.COMMAND_TOP_ENTRY.getString()
                        .replace(Placeholders.GENERIC_POS, NumberUtil.format(entry.getPosition()))
                        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(entry.getValue()))
                        .replace(Placeholders.PLAYER_NAME, entry.getName()));
                }
            })
            .send(sender);
    }
}
