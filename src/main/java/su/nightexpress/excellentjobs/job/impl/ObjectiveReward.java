package su.nightexpress.excellentjobs.job.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.random.Rnd;

public class ObjectiveReward {

    public static final ObjectiveReward EMPTY = new ObjectiveReward(0, 0, 0);

    private final double chance;
    private final double min;
    private final double max;

    public ObjectiveReward(double chance, double min, double max) {
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    @NotNull
    public static ObjectiveReward read(@NotNull FileConfig config, @NotNull String path) {
        double chance = config.getDouble(path + ".Chance");
        double min = config.getDouble(path + ".Min");
        double max = config.getDouble(path + ".Max");

        return new ObjectiveReward(chance, min, max);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Chance", this.getChance());
        config.set(path + ".Min", this.getMin());
        config.set(path + ".Max", this.getMax());
    }

    @NotNull
    public ObjectiveReward multiply(double modifier) {
        return new ObjectiveReward(this.getChance(), NumberUtil.round(this.getMin() * modifier), NumberUtil.round(this.getMax() * modifier));
    }

    public boolean isEmpty() {
        return this.getChance() <= 0D || (this.getMin() == 0 && this.getMax() == 0);
    }

    public boolean checkChance() {
        return Rnd.chance(this.getChance());
    }

    public double rollAmountNaturally() {
        if (this.isEmpty() || !this.checkChance()) return 0D;

        return this.rollAmount();
    }

    public double rollAmount() {
        return Rnd.getDouble(this.getMin(), this.getMax());
    }

    public double getChance() {
        return chance;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
