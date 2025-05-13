package su.nightexpress.excellentjobs.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.ModifyAction;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

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
    public static ArgumentBuilder<Currency> forCurrency(@NotNull JobsPlugin plugin, @NotNull String name) {
        return CommandArgument.builder(name, (str, context) -> EconomyBridge.getCurrency(str))
            .localized(Lang.COMMAND_ARGUMENT_NAME_CURRENCY)
            .customFailure(Lang.ERROR_COMMAND_INVALID_CURRENCY_ARGUMENT)
            .withSamples(tabContext -> EconomyBridge.getCurrencyIds().stream().toList());
    }

    @NotNull
    public static ArgumentBuilder<Job> forJob(@NotNull JobsPlugin plugin) {
        return CommandArgument.builder(JOB, (str, context) -> plugin.getJobManager().getJobById(str))
            .localized(Lang.COMMAND_ARGUMENT_NAME_JOB)
            .customFailure(Lang.ERROR_COMMAND_INVALID_JOB_ARGUMENT)
            .withSamples(tabContext -> plugin.getJobManager().getJobIds());
    }

    @NotNull
    public static ArgumentBuilder<JobState> forJobState(@NotNull JobsPlugin plugin) {
        return CommandArgument.builder(STATE, (string, context) -> StringUtil.getEnum(string, JobState.class).orElse(null))
            .localized(Lang.COMMAND_ARGUMENT_NAME_JOB_STATE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_JOB_STATE_ARGUMENT)
            .withSamples(context -> Lists.getEnums(JobState.class));
    }

    @NotNull
    public static ArgumentBuilder<ModifyAction> forAction(@NotNull JobsPlugin plugin) {
        return CommandArgument.builder(ACTION, (string, context) -> StringUtil.getEnum(string, ModifyAction.class).orElse(null))
            .localized(Lang.COMMAND_ARGUMENT_NAME_ACTION)
            .customFailure(Lang.ERROR_COMMAND_INVALID_ACTION_ARGUMENT)
            .withSamples(context -> Lists.modify(Lists.getEnums(ModifyAction.class), String::toLowerCase));
    }
}
