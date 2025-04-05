package su.nightexpress.excellentjobs.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;
import su.nightexpress.nightcore.command.experimental.flag.FlagTypes;

public class CommandFlags {

    public static final String SILENT = "s";

    @NotNull
    public static SimpleFlagBuilder silent() {
        return FlagTypes.simple(SILENT);
    }
}
