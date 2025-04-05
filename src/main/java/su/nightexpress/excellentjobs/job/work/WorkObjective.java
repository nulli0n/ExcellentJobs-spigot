package su.nightexpress.excellentjobs.job.work;

import org.jetbrains.annotations.NotNull;

public class WorkObjective {

    private final String workId;
    private final String objectName;
    private final String localizedName;

    public WorkObjective(@NotNull String workId, @NotNull String objectName, @NotNull String localizedName) {
        this.workId = workId;
        this.objectName = objectName;
        this.localizedName = localizedName;
    }

    @NotNull
    public String getWorkId() {
        return this.workId;
    }

    @NotNull
    public String getObjectName() {
        return this.objectName;
    }

    @NotNull
    public String getLocalizedName() {
        return this.localizedName;
    }
}
