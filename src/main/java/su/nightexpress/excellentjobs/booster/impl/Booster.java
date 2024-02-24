package su.nightexpress.excellentjobs.booster.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.config.BoosterInfo;

import java.util.Collection;

public class Booster {

    private final BoosterMultiplier multiplier;

    public Booster(@NotNull BoosterInfo parent) {
        this(parent.getMultiplier());
    }

    public Booster(@NotNull BoosterMultiplier multiplier) {
        this.multiplier = multiplier;
    }

    @NotNull
    public BoosterMultiplier getMultiplier() {
        return this.multiplier;
    }

    public static double getCurrencyBoost(@NotNull Currency currency, @NotNull Collection<Booster> boosters) {
        return getCurrencyBoost(currency.getId(), boosters);
    }

    public static double getCurrencyBoost(@NotNull String id, @NotNull Collection<Booster> boosters) {
        return 1D + getCurrencyPlainBoost(id, boosters);
    }

    public static double getCurrencyPlainBoost(@NotNull Currency currency, @NotNull Collection<Booster> boosters) {
        return getCurrencyPlainBoost(currency.getId(), boosters);
    }

    public static double getCurrencyPlainBoost(@NotNull String id, @NotNull Collection<Booster> boosters) {
        return boosters.stream().mapToDouble(b -> b.getMultiplier().getCurrencyMultiplier(id)).sum();
    }

    public static double getXPBoost(@NotNull Collection<Booster> boosters) {
        return 1D + getPlainXPBoost(boosters);
    }

    public static double getPlainXPBoost(@NotNull Collection<Booster> boosters) {
        return boosters.stream().mapToDouble(b -> b.getMultiplier().getXPMultiplier()).sum();
    }

    public static double getCurrencyPercent(@NotNull Currency currency, @NotNull Collection<Booster> boosters) {
        return getCurrencyPercent(currency.getId(), boosters);
    }

    public static double getCurrencyPercent(@NotNull String id, @NotNull Collection<Booster> boosters) {
        return boosters.stream().mapToDouble(b -> b.getMultiplier().getCurrencyPercent(id)).sum();
    }

    public static double getXPPercent(@NotNull Collection<Booster> boosters) {
        return boosters.stream().mapToDouble(b -> b.getMultiplier().getXPPercent()).sum();
    }
}
