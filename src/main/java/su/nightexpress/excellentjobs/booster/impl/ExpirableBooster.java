package su.nightexpress.excellentjobs.booster.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.config.TimedBoosterInfo;

public class ExpirableBooster extends Booster {

    private long expireDate;

    public ExpirableBooster(@NotNull TimedBoosterInfo parent) {
        this(parent.getMultiplier(), parent.getDuration());
    }

    public ExpirableBooster(@NotNull BoosterMultiplier multiplier, int duration) {
        this(multiplier, System.currentTimeMillis() + duration * 1000L + 100L);
    }

    public ExpirableBooster(@NotNull BoosterMultiplier multiplier, long expireDate) {
        super(multiplier);
        this.setExpireDate(expireDate);
    }

    public boolean isExpired() {
        return this.expireDate > 0L && System.currentTimeMillis() > this.expireDate;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }
}
