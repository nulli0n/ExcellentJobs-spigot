package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.data.impl.JobData;

public class JobXPLoseEvent extends JobXPEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public JobXPLoseEvent(@NotNull Player player, @NotNull JobUser user, @NotNull JobData jobData, int exp) {
        super(player, user, jobData, Math.abs(exp));
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
