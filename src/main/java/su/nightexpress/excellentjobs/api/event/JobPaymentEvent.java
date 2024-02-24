package su.nightexpress.excellentjobs.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.job.impl.Job;

public class JobPaymentEvent extends JobEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled;
    private Currency currency;
    private double amount;

    public JobPaymentEvent(@NotNull Player player, @NotNull Job job, @NotNull Currency currency, double amount) {
        super(!Bukkit.isPrimaryThread(), player, job);
        this.setAmount(amount);
        this.setCurrency(currency);
    }

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
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
