package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsAPI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobIncome {

    private final Map<Currency, Double> currencyMap;

    public JobIncome() {
        this.currencyMap = new ConcurrentHashMap<>();
    }

    public void payAndClear(@NotNull Player player, @NotNull Job job) {
        this.pay(player, job);
        this.clear();
    }

    public void pay(@NotNull Player player, @NotNull Job job) {
        this.currencyMap.forEach((currency, amount) -> {
            if (currency.getInternalId().equals("vault") && !job.getDebitAccount().isEmpty()) {
                if (EconomyBridge.getEconomyBalance(job.getDebitAccountUUID()) < amount) {
                    // Don't pay the worker if the account has insufficient balance?
                    JobsAPI.getPlugin().getSLF4JLogger().info("Failed to payout ${} for player {} due to insufficient balance in debit account", amount, player.getName());
                    return;
                }
                currency.take(job.getDebitAccountUUID(), amount);
            }
            currency.give(player, amount);
        });
    }

    public void clear() {
        this.currencyMap.clear();
    }

    public void set(@NotNull Currency currency, double amount) {
        if (amount <= 0) {
            this.remove(currency);
            return;
        }

        this.currencyMap.put(currency, amount);
    }

    public void add(@NotNull Currency currency, double amount) {
        if (amount == 0) return;

        double has = this.currencyMap.getOrDefault(currency, 0D);

        this.set(currency, has + amount);
    }

    public void reduce(@NotNull Currency currency, double amount) {
        this.add(currency, -amount);
    }

    public void remove(@NotNull Currency currency) {
        this.currencyMap.remove(currency);
    }

    public boolean isEmpty() {
        return this.currencyMap.isEmpty() || this.currencyMap.values().stream().mapToDouble(amount -> amount).sum() == 0D;
    }

    @NotNull
    public Map<Currency, Double> getCurrencyMap() {
        return this.currencyMap;
    }
}
