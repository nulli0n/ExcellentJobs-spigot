package su.nightexpress.excellentjobs.job.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public record Bonus(@NotNull Modifier primary, @NotNull Modifier secondary) implements Writeable {

    @NotNull
    public static Bonus read(@NotNull FileConfig config, @NotNull String path) {
        Modifier primary = Modifier.read(config, path + ".Primary");
        Modifier second = Modifier.read(config, path + ".Secondary");
        return new Bonus(primary, second);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Primary", this.primary);
        config.set(path + ".Secondary", this.secondary);
    }

    @NotNull
    public Modifier forState(@NotNull JobState state) {
        return switch (state) {
            case PRIMARY -> this.primary;
            case SECONDARY -> this.secondary;
            case INACTIVE -> Modifier.EMPTY;
        };
    }

    public double forStateAndLevel(@NotNull JobState state, int level) {
        return this.forState(state).getValue(level);
    }
}
