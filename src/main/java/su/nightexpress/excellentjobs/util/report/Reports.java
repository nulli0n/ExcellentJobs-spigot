package su.nightexpress.excellentjobs.util.report;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.zone.impl.Zone;

public class Reports {

    @NotNull
    public static Report forZone(@NotNull Zone zone) {
        return new Report()
            .add(ReportType.ZONE_SELECTION, () -> {
                if (zone.getCuboid().isEmpty()) return Problem.bad("Invalid cuboid selection.");
                return Problem.good("Selection is valid.");
            })
            .add(ReportType.ZONE_JOB, () -> {
                return zone.getLinkedJob() == null ? Problem.bad("Invalid job.") : Problem.good("Job is valid.");
            })
            ;
    }
}
