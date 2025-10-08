package su.nightexpress.excellentjobs.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JobLimitData {

    private final String              jobId;
    private final Map<String, Double> currencyEarned;

    private double xpEarned;
    private long   expireDate;

    @NotNull
    public static JobLimitData create(@NotNull Job job) {
        return new JobLimitData(job.getId(), new HashMap<>(), 0, System.currentTimeMillis());
    }

    public JobLimitData(@NotNull String jobId, @NotNull Map<String, Double> currencyEarned, double xpEarned, long expireDate) {
        this.jobId = jobId.toLowerCase();
        this.currencyEarned = currencyEarned;
        this.xpEarned = xpEarned;
        this.expireDate = expireDate;
    }

    public void checkExpiration() {
        if (System.currentTimeMillis() < this.getExpireDate()) return;

        if (Config.JOBS_DAILY_LIMITS_RESET_MIDNIGHT.get()) {
            LocalDateTime midnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
            this.expireDate = TimeUtil.toEpochMillis(midnight);
        }
        else {
            this.expireDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        }

        this.getCurrencyEarned().clear();
        this.setXPEarned(0D);
    }

    public void addCurrency(@NotNull Currency currency, double amount) {
        this.addCurrency(currency.getInternalId(), amount);
    }

    public void addCurrency(@NotNull String id, double amount) {
        if (amount <= 0D) return;

        this.getCurrencyEarned().put(id.toLowerCase(), this.getCurrencyEarned(id) + amount);
    }

    public void addXP(int amount) {
        if (amount <= 0) return;

        this.setXPEarned(this.getXPEarned() + amount);
    }

    public double getCurrencyEarned(@NotNull Currency currency) {
        return this.getCurrencyEarned(currency.getInternalId());
    }

    public double getCurrencyEarned(@NotNull String id) {
        return this.getCurrencyEarned().getOrDefault(id.toLowerCase(), 0D);
    }

    @NotNull
    public String getJobId() {
        return jobId;
    }

    public Map<String, Double> getCurrencyEarned() {
        return currencyEarned;
    }

    public double getXPEarned() {
        return xpEarned;
    }

    public void setXPEarned(double xpEarned) {
        this.xpEarned = xpEarned;
    }

    public long getExpireDate() {
        return expireDate;
    }
}
