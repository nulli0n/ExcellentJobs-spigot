package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;

public class JobJoinEvent extends JobEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private JobState state;
    private boolean cancelled;

    public JobJoinEvent(@NotNull Player player, @NotNull Job job, @NotNull JobState state) {
        super(false, player, job);
        this.setState(state);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public JobState getState() {
        return state;
    }

    public void setState(@NotNull JobState state) {
        this.state = state;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
