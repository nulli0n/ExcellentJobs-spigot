package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.data.impl.JobData;

public class JobLevelDownEvent extends JobDataEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public JobLevelDownEvent(@NotNull Player player, @NotNull JobUser user, @NotNull JobData data) {
        super(player, user, data);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public int getNewLevel() {
        return this.getJobData().getLevel();
    }
}
