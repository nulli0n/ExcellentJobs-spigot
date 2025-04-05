package su.nightexpress.excellentjobs.booster.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.booster.Multiplier;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.booster.BoosterUtils;
import su.nightexpress.nightcore.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class Booster {

    private final Map<MultiplierType, Multiplier> multiplierMap;

    private long expireDate;

    public Booster(long expireDate) {
        this.multiplierMap = new HashMap<>();
        this.setExpireDate(expireDate);
    }

    @NotNull
    public static Booster create(double paymentMult, double xpMult, int duration) {
        Booster booster = new Booster(TimeUtil.createFutureTimestamp(duration));
        booster.setMultiplier(MultiplierType.INCOME, paymentMult);
        booster.setMultiplier(MultiplierType.XP, xpMult);
        return booster;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholers() {
        return Placeholders.BOOSTER.replacer(this);
    }

    public boolean isValid() {
        return !this.multiplierMap.isEmpty() && this.multiplierMap.values().stream().anyMatch(Multiplier::isValid) && !this.isExpired();
    }

    public boolean isExpired() {
        return TimeUtil.isPassed(this.expireDate);
    }

    public void setMultiplier(@NotNull MultiplierType type, double multiplier) {
        this.multiplierMap.put(type, new Multiplier(multiplier));
    }

    @Nullable
    public Multiplier getMultiplier(@NotNull MultiplierType type) {
        return this.multiplierMap.get(type);
    }

    public boolean hasMultiplier(@NotNull MultiplierType type) {
        Multiplier multiplier = this.getMultiplier(type);
        return multiplier != null && multiplier.isValid();
    }

    @NotNull
    public String formattedPercent(@NotNull MultiplierType type) {
        Multiplier multiplier = this.getMultiplier(type);
        return BoosterUtils.formatMultiplier(multiplier == null ? 1D : multiplier.getValue());
    }

    public double getAsPercent(@NotNull MultiplierType type) {
        Multiplier multiplier = this.getMultiplier(type);
        return BoosterUtils.getAsPercent(multiplier == null ? 1D : multiplier.getValue());
    }

    public double getValue(@NotNull MultiplierType type) {
        Multiplier multiplier = this.getMultiplier(type);
        return multiplier == null ? 0D : multiplier.getValue();
    }

    public long getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }
}
