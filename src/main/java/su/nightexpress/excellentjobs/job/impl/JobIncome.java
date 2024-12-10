package su.nightexpress.excellentjobs.job.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;

import java.util.HashMap;
import java.util.Map;

public class JobIncome {

    private final Job                                      job;
    private final Map<JobObjective, Map<Currency, Double>> paymentMap;

    public JobIncome(@NotNull Job job) {
        this.job = job;
        this.paymentMap = new HashMap<>();
    }

    @NotNull
    public Map<Currency, Double> getSumAndClear() {
        Map<Currency, Double> total = this.getSum();
        this.getPaymentMap().clear();

        return total;
    }

    @NotNull
    public Map<Currency, Double> getSum() {
        Map<Currency, Double> total = new HashMap<>();

        this.getPaymentMap().forEach((objective, map) -> {
            map.forEach((currency, amount) -> {
                total.merge(currency, amount, Double::sum);
            });
        });
        total.values().removeIf(d -> d == 0D);

        return total;
    }

    public void add(@NotNull JobObjective objective, @NotNull Currency currency, double amount) {
        var currencyMap = this.getPaymentMap().computeIfAbsent(objective, k -> new HashMap<>());

        double has = currencyMap.computeIfAbsent(currency, k -> 0D);
        currencyMap.put(currency, has + amount);
    }

    public boolean isZero() {
        if (this.getPaymentMap().isEmpty()) return true;

        return this.getPaymentMap().values().stream().allMatch(map -> map.isEmpty() || map.values().stream().mapToDouble(d -> d).sum() == 0D);
    }

    @NotNull
    public Job getJob() {
        return job;
    }

    @NotNull
    public Map<JobObjective, Map<Currency, Double>> getPaymentMap() {
        return paymentMap;
    }
}
