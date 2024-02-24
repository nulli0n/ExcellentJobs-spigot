package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.JobObjective;

public abstract class JobObjectiveEvent extends JobDataEvent {

    protected final JobObjective objective;

    public JobObjectiveEvent(@NotNull Player player,
                             @NotNull JobUser user,
                             @NotNull JobData jobData,
                             @NotNull JobObjective objective) {
        super(player, user, jobData);
        this.objective = objective;
    }

    @NotNull
    public final JobObjective getObjective() {
        return objective;
    }
}
