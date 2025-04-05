package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.job.work.WorkObjective;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.user.JobUser;

public class JobObjectiveIncomeEvent extends JobObjectiveEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final WorkObjective workObjective;

    private boolean cancelled;
    private Currency currency;
    private double payment;
    private double paymentMultiplier;

    public JobObjectiveIncomeEvent(@NotNull Player player,
                                   @NotNull JobUser user,
                                   @NotNull JobData jobData,
                                   @NotNull JobObjective objective,
                                   @NotNull WorkObjective workObjective,
                                   @NotNull String objectName,
                                   @NotNull Currency currency,
                                   double payment,
                                   double paymentMultiplier) {
        super(player, user, jobData, objective);
        this.workObjective = workObjective;

        this.setCurrency(currency);
        this.setPayment(payment);
        this.setPaymentMultiplier(paymentMultiplier);
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
    public WorkObjective getWorkObjective() {
        return this.workObjective;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getPaymentMultiplier() {
        return paymentMultiplier;
    }

    public void setPaymentMultiplier(double paymentMultiplier) {
        this.paymentMultiplier = paymentMultiplier;
    }
}
