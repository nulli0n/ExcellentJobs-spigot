package su.nightexpress.excellentjobs.stats.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.command.CommandArguments;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

public class StatsCommands {

    public static final String TOP_ALIAS = "top";

    public static void load(@NotNull JobsPlugin plugin, @NotNull StatsManager manager, @NotNull HubNodeBuilder builder) {
        builder.branch(Commands.literal(TOP_ALIAS)
            .description(Lang.COMMAND_TOP_DESC.text())
            .permission(Perms.COMMAND_TOP)
            .withArguments(
                CommandArguments.forJob(plugin),
                Arguments.integer(CommandArguments.PAGE, 1)
                    .localized(Lang.COMMAND_ARGUMENT_NAME_PAGE.text())
                    .suggestions((reader, context) -> Lists.newList("1", "2", "3", "4", "5"))
            )
            .executes((context, arguments) -> viewTop(manager, context, arguments))
        );
    }

    private static boolean viewTop(@NotNull StatsManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Job job = arguments.get(CommandArguments.JOB, Job.class);
        int page2 = arguments.getInt(CommandArguments.PAGE, 1);

        int perPage = Config.STATISTIC_ENTRIES_PER_PAGE.get();

        List<TopEntry> full = manager.getLevelTopEntries(job);
        List<List<TopEntry>> split = Lists.split(full, perPage);
        int pages = split.size();
        int page = Math.max(0, Math.min(pages, Math.abs(page2)) - 1);

        List<TopEntry> stats = pages > 0 ? split.get(page) : new ArrayList<>();

        Lang.COMMAND_TOP_LIST.message().send(context.getSender(), replacer -> replacer
            .replace(job.replacePlaceholders())
            .replace(Placeholders.GENERIC_CURRENT, page + 1)
            .replace(Placeholders.GENERIC_MAX, pages)
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                for (TopEntry entry : stats) {
                    list.add(Lang.COMMAND_TOP_ENTRY.text()
                        .replace(Placeholders.GENERIC_POS, NumberUtil.format(entry.getPosition()))
                        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(entry.getValue()))
                        .replace(Placeholders.PLAYER_NAME, entry.getName()));
                }
            }));

        return true;
    }
}
