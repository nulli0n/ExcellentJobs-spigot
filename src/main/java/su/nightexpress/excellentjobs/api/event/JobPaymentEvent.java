package su.nightexpress.excellentjobs.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobIncome;

public class JobPaymentEvent extends JobEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final JobIncome income;

    private boolean cancelled;

    public JobPaymentEvent(@NotNull Player player, @NotNull Job job, @NotNull JobIncome income) {
        super(!Bukkit.isPrimaryThread(), player, job);
        this.income = income;
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

    @NotNull
    public JobIncome getIncome() {
        return this.income;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
