package su.nightexpress.excellentjobs.stats.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.currency.Currency;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DayStats {

    private final Map<String, Double> currencyEarned;
    private final long                timestamp;

    // TODO XP

    public DayStats() {
        this(new HashMap<>(), JobStats.toEpochMillis(LocalDate.now()));
    }

    public DayStats(@NotNull Map<String, Double> currencyEarned, long timestamp) {
        this.currencyEarned = currencyEarned;
        this.timestamp = timestamp;
    }

    public double getCurrency(@NotNull Currency currency) {
        return this.getCurrency(currency.getInternalId());
    }

    public double getCurrency(@NotNull String currencyId) {
        return this.currencyEarned.getOrDefault(currencyId.toLowerCase(), 0D);
    }


    public void add(@NotNull DayStats stats) {
        stats.getCurrencyEarned().forEach(this::addCurrency);
    }

    public void addCurrency(@NotNull Currency currency, double amount) {
        this.addCurrency(currency.getInternalId(), amount);
    }

    public void addCurrency(@NotNull String currencyId, double amount) {
        this.currencyEarned.put(currencyId.toLowerCase(), this.getCurrency(currencyId) + amount);
    }



    @NotNull
    public Map<String, Double> getCurrencyEarned() {
        return currencyEarned;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
