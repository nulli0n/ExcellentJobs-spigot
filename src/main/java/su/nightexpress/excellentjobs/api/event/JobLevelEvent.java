package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.user.JobUser;

public abstract class JobLevelEvent extends JobDataEvent {

    protected final int oldLevel;

    public JobLevelEvent(@NotNull Player player, @NotNull JobUser user, @NotNull JobData jobData, int oldLevel) {
        super(player, user, jobData);
        this.oldLevel = oldLevel;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }

    public int getNewLevel() {
        return this.jobData.getLevel();
    }
}
