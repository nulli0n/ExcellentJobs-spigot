package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.user.JobUser;

public class JobIncomeEvent extends JobDataEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled;
    private Currency currency;
    private double amount;
    private double multiplier;

    public JobIncomeEvent(@NotNull Player player, @NotNull JobUser user, @NotNull JobData jobData, @NotNull Currency currency, double amount, double multiplier) {
        super(player, user, jobData);

        this.setCurrency(currency);
        this.setAmount(amount);
        this.setMultiplier(multiplier);
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
    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
