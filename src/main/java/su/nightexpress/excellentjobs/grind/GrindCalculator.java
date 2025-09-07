package su.nightexpress.excellentjobs.grind;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.job.impl.Job;

public interface GrindCalculator<T extends GrindTable> {

    @NotNull GrindReward calculate(@NotNull Job job, @NotNull T table);
}
