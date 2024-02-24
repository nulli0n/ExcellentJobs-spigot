package su.nightexpress.excellentjobs.booster;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.HashMap;
import java.util.Map;

public class BoosterMultiplier {

    private final Map<String, Double> currencyPercent;
    private final double              xpPercent;

    public BoosterMultiplier(@NotNull Map<String, Double> currencyPercent, double xpPercent) {
        this.currencyPercent = currencyPercent;
        this.xpPercent = xpPercent;
    }

    @NotNull
    public static BoosterMultiplier read(@NotNull FileConfig cfg, @NotNull String path) {
        Map<String, Double> currency = new HashMap<>();
        for (String curId : cfg.getSection(path + ".Multiplier.Currency")) {
            double mod = cfg.getDouble(path + ".Multiplier.Currency." + curId, 0D);
            if (mod == 0D) continue;

            currency.put(curId.toLowerCase(), mod);
        }

        double xpModifier = cfg.getDouble(path + ".Multiplier.XP", 1D);
        return new BoosterMultiplier(currency, xpModifier);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        this.getCurrencyPercent().forEach((curId, mod) -> {
            cfg.set(path + ".Multiplier.Currency." + curId, mod);
        });
        cfg.set(path + ".Multiplier.XP", this.getXPPercent());
    }

    public double getCurrencyMultiplier(@NotNull Currency currency) {
        return this.getCurrencyMultiplier(currency.getId());
    }

    public double getCurrencyMultiplier(@NotNull String id) {
        return this.getCurrencyPercent(id) / 100D;
        //return this.getCurrencyPercent().getOrDefault(curId.toLowerCase(), 1D);
    }

    public double getCurrencyPercent(@NotNull Currency currency) {
        return this.getCurrencyPercent(currency.getId());
    }

    public double getCurrencyPercent(@NotNull String id) {
        //return this.getCurrencyMultiplier(curId) * 100D - 100D;
        return this.getCurrencyPercent().getOrDefault(id.toLowerCase(), 0D);
    }

    public double getXPMultiplier() {
        return this.getXPPercent() / 100D;// this.getXPMultiplier() * 100D - 100D;
    }

    @NotNull
    public Map<String, Double> getCurrencyPercent() {
        return currencyPercent;
    }

    public double getXPPercent() {
        return xpPercent;
    }
}
