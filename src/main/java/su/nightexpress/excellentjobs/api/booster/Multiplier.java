package su.nightexpress.excellentjobs.api.booster;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.booster.BoosterUtils;

public class Multiplier {

    private double value;

    public Multiplier(double value) {
        this.setValue(value);
    }

    public boolean isValid() {
        return this.value != 0D;
    }

    @NotNull
    public String formattedPercent() {
        return BoosterUtils.formatMultiplier(this.value);
    }

    public double getAsPercent() {
        return BoosterUtils.getAsPercent(this.value);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = Math.abs(value);
    }
}
