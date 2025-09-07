package su.nightexpress.excellentjobs.job.legacy;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;

public record LegacyObjectiveReward(double chance, double min, double max) {

    @NotNull
    public static LegacyObjectiveReward read(@NotNull FileConfig config, @NotNull String path) {
        double chance = config.getDouble(path + ".Chance");
        double min = config.getDouble(path + ".Min");
        double max = config.getDouble(path + ".Max");

        return new LegacyObjectiveReward(chance, min, max);
    }
}
