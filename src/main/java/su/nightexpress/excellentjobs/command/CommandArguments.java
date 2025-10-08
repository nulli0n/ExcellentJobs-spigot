package su.nightexpress.excellentjobs.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.ModifyAction;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Lists;

import java.util.Optional;

public class CommandArguments {

    public static final String NAME              = "name";
    public static final String PLAYER            = "player";
    public static final String ZONE              = "zone";
    public static final String JOB               = "job";
    public static final String STATE             = "state";
    public static final String XP_MULTIPLIER     = "xp_multiplier";
    public static final String INCOME_MULTIPLIER = "income_multiplier";
    public static final String DURATION          = "duration";
    public static final String PAGE              = "page";
    public static final String ACTION            = "action";
    public static final String AMOUNT            = "amount";

    @NotNull
    public static ArgumentNodeBuilder<Currency> forCurrency(@NotNull JobsPlugin plugin, @NotNull String name) {
        return Commands.argument(name, (contextBuilder, string) -> Optional.ofNullable(EconomyBridge.getCurrency(string)).orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_CURRENCY_ARGUMENT)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_CURRENCY.text())
            .suggestions((reader, context) -> EconomyBridge.getCurrencyIds().stream().toList());
    }

    @NotNull
    public static ArgumentNodeBuilder<Job> forJob(@NotNull JobsPlugin plugin) {
        return Commands.argument(JOB, (context, str) -> Optional.ofNullable(plugin.getJobManager().getJobById(str)).orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_JOB_ARGUMENT)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_JOB.text())
            .suggestions((reader, context) -> plugin.getJobManager().getJobIds());
    }

    @NotNull
    public static ArgumentNodeBuilder<JobState> forJobState(@NotNull JobsPlugin plugin) {
        return Commands.argument(STATE, (context, string) -> Enums.parse(string, JobState.class).orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_JOB_STATE_ARGUMENT)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_JOB_STATE.text())
            .suggestions((reader, context) -> Enums.getNames(JobState.class));
    }

    @NotNull
    public static ArgumentNodeBuilder<ModifyAction> forAction(@NotNull JobsPlugin plugin) {
        return Commands.argument(ACTION, (context, string) -> Enums.parse(string, ModifyAction.class).orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_ACTION_ARGUMENT)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_ACTION.text())
            .suggestions((reader, context) -> Lists.modify(Enums.getNames(ModifyAction.class), String::toLowerCase));
    }
}
