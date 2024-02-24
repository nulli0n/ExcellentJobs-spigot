package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.JobObjective;

public class JobObjectiveXPEvent extends JobObjectiveEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ActionType<?, ?> actionType;
    private final Object           object;

    private boolean cancelled;
    private double xpAmount;
    private double xpMultiplier;

    public JobObjectiveXPEvent(@NotNull Player player,
                                   @NotNull JobUser user,
                                   @NotNull JobData jobData,
                                   @NotNull JobObjective objective,
                                   @NotNull ActionType<?, ?> actionType,
                                   @NotNull Object object,
                                   double xpAmount,
                                   double xpMultiplier) {
        super(player, user, jobData, objective);
        this.actionType = actionType;
        this.object = object;

        this.setXPAmount(xpAmount);
        this.setXPMultiplier(xpMultiplier);
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    public final ActionType<?, ?> getActionType() {
        return actionType;
    }

    @NotNull
    public final Object getObject() {
        return object;
    }

    public double getXPAmount() {
        return xpAmount;
    }

    public void setXPAmount(double xpAmount) {
        this.xpAmount = xpAmount;
    }

    public double getXPMultiplier() {
        return xpMultiplier;
    }

    public void setXPMultiplier(double xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
    }
}
