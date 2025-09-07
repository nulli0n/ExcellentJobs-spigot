package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class Multiplier implements Writeable {

    public static final double UNLIMIT_VALUE = -1D;

    public static final Multiplier EMPTY = new Multiplier(0D, 1D, UNLIMIT_VALUE);

    private final double amount;
    private final double interval;
    private final double capacity;

    public Multiplier(double amount, double interval, double capacity) {
        this.amount = amount;
        this.interval = interval;
        this.capacity = capacity;
    }

    @NotNull
    public static Multiplier unlimited(double base, double interval) {
        return limited(base, interval, UNLIMIT_VALUE);
    }

    @NotNull
    public static Multiplier limited(double base, double interval, double capacity) {
        return new Multiplier(base, interval, capacity);
    }

    @NotNull
    public static Multiplier read(@NotNull FileConfig config, @NotNull String path) {
        double base = ConfigValue.create(path + ".Amount", 0D).read(config);
        double interval = ConfigValue.create(path + ".Interval", 1D).read(config);
        double capacity = ConfigValue.create(path + ".Capacity", -1D).read(config);

        return new Multiplier(base, interval, capacity);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Amount", this.amount);
        config.set(path + ".Interval", this.interval);
        config.set(path + ".Capacity", this.capacity);
    }

    public double calculateFor(double value) {
        double interval = this.interval <= 0 ? 1D : this.interval;
        int fractions = (int) Math.floor(value / interval);

        double result = this.amount * fractions;

        return this.capacity < 0 ? result : Math.min(result, this.capacity);
    }

    public double getAmount() {
        return this.amount;
    }
}
