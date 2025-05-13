package su.nightexpress.excellentjobs.job.work;

import org.jetbrains.annotations.NotNull;

public class WorkObjective {

    private final String workId;
    private final String objectName;
    private final String localizedName;
    private final int amount;
    private final double multiplier;

    public WorkObjective(@NotNull String workId, @NotNull String objectName, @NotNull String localizedName, int amount, double multiplier) {
        this.workId = workId;
        this.objectName = objectName;
        this.localizedName = localizedName;
        this.amount = amount;
        this.multiplier = multiplier;
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

    public int getAmount() {
        return this.amount;
    }

    public double getMultiplier() {
        return this.multiplier;
    }
}
