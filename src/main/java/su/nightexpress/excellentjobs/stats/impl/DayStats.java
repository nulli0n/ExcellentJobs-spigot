package su.nightexpress.excellentjobs.stats.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.job.impl.JobObjective;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DayStats {

    private final Map<String, Double>               currencyEarned;
    private final Map<String, Map<String, Integer>> objectivesCompleted;
    private final long timestamp;

    private int ordersCompleted;

    // TODO XP

    public DayStats() {
        this(new HashMap<>(), new HashMap<>(), JobStats.toEpochMillis(LocalDate.now()), 0);
    }

    public DayStats(@NotNull Map<String, Double> currencyEarned,
                    @NotNull Map<String, Map<String, Integer>> objectivesCompleted,
                    long timestamp,
                    int ordersCompleted) {
        this.currencyEarned = currencyEarned;
        this.objectivesCompleted = objectivesCompleted;
        this.timestamp = timestamp;
        this.ordersCompleted = ordersCompleted;
    }

    public double getCurrency(@NotNull Currency currency) {
        return this.getCurrency(currency.getInternalId());
    }

    public double getCurrency(@NotNull String currencyId) {
        return this.currencyEarned.getOrDefault(currencyId.toLowerCase(), 0D);
    }

    public int getObjectives(@NotNull JobObjective objective) {
        return this.getObjectives(objective.getId());
    }

    public int getObjectives(@NotNull String objectiveId) {
        Map<String, Integer> map = this.objectivesCompleted.computeIfAbsent(objectiveId.toLowerCase(), k -> new HashMap<>());
        return map.values().stream().mapToInt(i -> i).sum();
    }

    public int getObjectives(@NotNull JobObjective objective, @NotNull String objectName) {
        return this.getObjectives(objective.getId(), objectName);
    }

    public int getObjectives(@NotNull String objectiveId, @NotNull String objectName) {
        var map = this.objectivesCompleted.computeIfAbsent(objectiveId.toLowerCase(), k -> new HashMap<>());
        return map.getOrDefault(objectName.toLowerCase(), 0);
    }

    public void add(@NotNull DayStats stats) {
        stats.getCurrencyEarned().forEach(this::addCurrency);
        stats.getObjectivesCompleted().forEach((objectiveId, map) -> map.forEach((objectName, amount) -> this.addObjective(objectiveId, objectName, amount)));
        stats.addOrders(stats.getOrdersCompleted());
    }

    public void addCurrency(@NotNull Currency currency, double amount) {
        this.addCurrency(currency.getInternalId(), amount);
    }

    public void addCurrency(@NotNull String currencyId, double amount) {
        this.currencyEarned.put(currencyId.toLowerCase(), this.getCurrency(currencyId) + amount);
    }


    public void addObjective(@NotNull JobObjective objective, @NotNull String objectName, int amount) {
        this.addObjective(objective.getId(), objectName, amount);
    }

    public void addObjective(@NotNull String objectiveId, @NotNull String objectName, int amount) {
        objectiveId = objectiveId.toLowerCase();
        objectName = objectName.toLowerCase();

        var map = this.objectivesCompleted.computeIfAbsent(objectiveId, k -> new HashMap<>());
        int current = map.getOrDefault(objectName, 0);
        map.put(objectName, current + amount);
    }

    public void addOrders(int amount) {
        this.setOrdersCompleted(this.ordersCompleted + Math.abs(amount));
    }

    @NotNull
    public Map<String, Double> getCurrencyEarned() {
        return currencyEarned;
    }

    @NotNull
    public Map<String, Map<String, Integer>> getObjectivesCompleted() {
        return objectivesCompleted;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrdersCompleted() {
        return ordersCompleted;
    }

    public void setOrdersCompleted(int ordersCompleted) {
        this.ordersCompleted = ordersCompleted;
    }
}
