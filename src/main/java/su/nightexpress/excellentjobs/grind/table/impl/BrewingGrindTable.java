package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.util.Calc;

public class BrewingGrindTable implements GrindTable {

    private final SourceTable ingredientsTable;
    private final double      amountBonus;
    private final double autoBrewPenalty;
    private final double manualBrewBonus;

    public BrewingGrindTable(@NotNull SourceTable ingredientsTable, double amountBonus, double autoBrewPenalty, double manualBrewBonus) {
        this.ingredientsTable = ingredientsTable;
        this.amountBonus = amountBonus;
        this.autoBrewPenalty = autoBrewPenalty;
        this.manualBrewBonus = manualBrewBonus;
    }

    @NotNull
    public static BrewingGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        SourceTable table = ConfigValue.create(path + ".Ingredients", SourceTable::read, SourceTable.EMPTY).read(config);
        double amountBonus = ConfigValue.create(path + ".PerPotionBonus", 25D).read(config);
        double autoBrewPenalty = ConfigValue.create(path + ".AutomatedBrewBonus", -80D).read(config);
        double manualBrewBonus = ConfigValue.create(path + ".ManualBrewBonus", 50D).read(config);

        return new BrewingGrindTable(table, amountBonus, autoBrewPenalty, manualBrewBonus);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Ingredients", this.ingredientsTable);
        config.set(path + ".PerPotionBonus", this.amountBonus);
        config.set(path + ".AutomatedBrewBonus", this.autoBrewPenalty);
        config.set(path + ".ManualBrewBonus", this.manualBrewBonus);
    }

    public GrindReward getIngredientXP(@NotNull ItemStack ingredient, int potionsAmount, boolean isAutomated) {
        double modeBonus = Calc.toMult(isAutomated ? this.autoBrewPenalty : this.manualBrewBonus);
        double potionBonus = Calc.toMult(this.amountBonus * potionsAmount);

        GrindReward reward = this.ingredientsTable.rollForEntityOrDefault(ingredient, GrindAdapterFamily.ITEM);

        return reward.multiply(potionBonus).multiply(modeBonus);
    }
}
