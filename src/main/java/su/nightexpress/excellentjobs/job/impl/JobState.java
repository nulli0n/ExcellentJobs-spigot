package su.nightexpress.excellentjobs.job.impl;

import org.jetbrains.annotations.NotNull;

public enum JobState {

    PRIMARY, SECONDARY, INACTIVE;

    @NotNull
    public JobState getOpposite() {
        if (this == INACTIVE) return this;

        return this == PRIMARY ? SECONDARY : PRIMARY;
    }

    @NotNull
    public static JobState[] actives() {
        return new JobState[]{JobState.PRIMARY, JobState.SECONDARY};
    }
}
