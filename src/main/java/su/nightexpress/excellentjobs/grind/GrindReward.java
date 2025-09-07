package su.nightexpress.excellentjobs.grind;

import org.jetbrains.annotations.NotNull;

public class GrindReward {

    private double xp;
    private double money;

    public GrindReward() {
        this(0, 0);
    }

    public GrindReward(double xp, double money) {
        this.xp = xp;
        this.money = money;
    }

    @NotNull
    public GrindReward add(@NotNull GrindReward other) {
        this.xp += other.xp;
        this.money += other.money;
        return this;
    }

    @NotNull
    public GrindReward multiply(double multiplier) {
        this.xp *= multiplier;
        this.money *= multiplier;
        return this;
    }

    public boolean isEmpty() {
        return this.xp <= 0D && this.money <= 0D;
    }

    public double getXP() {
        return this.xp;
    }

    public double getMoney() {
        return this.money;
    }
}
