package su.nightexpress.excellentjobs.grind.table;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.wrapper.UniDouble;

public class SourceReward {

    private static final String NUMBER_DELIMITER  = ";";
    private static final String SECTION_DELIMITER = " ";

    public static final double MAX_CHANCE = 100D;

    private final UniDouble xpAmount;
    private final UniDouble moneyAmount;
    private final double chance;

    public SourceReward(@NotNull UniDouble xpAmount, @NotNull UniDouble moneyAmount, double chance) {
        this.xpAmount = xpAmount;
        this.moneyAmount = moneyAmount;
        this.chance = chance;
    }

    @NotNull
    public static SourceReward maxChance(double xp, double money) {
        return new SourceReward(UniDouble.of(xp, xp), UniDouble.of(money, money), MAX_CHANCE);
    }

    @NotNull
    public static SourceReward deserialize(@NotNull String from) {
        String[] sections = from.split(SECTION_DELIMITER);

        String xpRaw = sections[0];
        String moneyRaw = sections.length >= 2 ? sections[1] : "";
        double chance = sections.length >= 3 ? NumberUtil.getDoubleAbs(sections[2]) : MAX_CHANCE;

        UniDouble xpAmount = parseAmount(xpRaw);
        UniDouble moneyAmount = parseAmount(moneyRaw);

        return new SourceReward(xpAmount, moneyAmount, chance);
    }

    @NotNull
    private static UniDouble parseAmount(@NotNull String string) {
        String[] split = string.split(NUMBER_DELIMITER);
        int length = split.length;

        double min = NumberUtil.getDoubleAbs(split[0]);
        double max = length >= 2 ? NumberUtil.getDoubleAbs(split[1]) : min;

        return UniDouble.of(min, max);
    }

    @NotNull
    public String serialize() {
        return this.serialize(this.xpAmount) + SECTION_DELIMITER + this.serialize(this.moneyAmount) + SECTION_DELIMITER + this.chance;
    }

    @NotNull
    private String serialize(@NotNull UniDouble value) {
        return value.getMinValue() + NUMBER_DELIMITER + value.getMaxValue();
    }

    @NotNull
    public GrindReward roll() {
        return new GrindReward(this.rollXP(), this.rollMoney());
    }

    public boolean checkChance() {
        return Rnd.chance(this.chance);
    }

    public double rollXP() {
        return this.xpAmount.roll();
    }

    public double rollMoney() {
        return this.moneyAmount.roll();
    }
}
