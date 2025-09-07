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

public class CookingGrindTable implements GrindTable {

    private final SourceTable ingredientsTable;
    private final double      autoSmeltBonus;
    private final double        manualSmeltBonus;

    public CookingGrindTable(@NotNull SourceTable ingredientsTable, double autoSmeltBonus, double manualSmeltBonus) {
        this.ingredientsTable = ingredientsTable;
        this.autoSmeltBonus = autoSmeltBonus;
        this.manualSmeltBonus = manualSmeltBonus;
    }

    @NotNull
    public static CookingGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        SourceTable table = ConfigValue.create(path + ".Ingredients", SourceTable::read, SourceTable.EMPTY).read(config);
        double autoBrewPenalty = ConfigValue.create(path + ".AutomatedSmeltBonus", -80D).read(config);
        double manualBrewBonus = ConfigValue.create(path + ".ManualSmeltBonus", 50D).read(config);

        return new CookingGrindTable(table, autoBrewPenalty, manualBrewBonus);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Ingredients", this.ingredientsTable);
        config.set(path + ".AutomatedSmeltBonus", this.autoSmeltBonus);
        config.set(path + ".ManualSmeltBonus", this.manualSmeltBonus);
    }

    @NotNull
    public GrindReward getIngredientXP(@NotNull ItemStack ingredient, boolean isAutomated) {
        double modeBonus = Calc.toMult(isAutomated ? this.autoSmeltBonus : this.manualSmeltBonus);
        GrindReward xp = this.ingredientsTable.rollForEntityOrDefault(ingredient, GrindAdapterFamily.ITEM);

        return xp.multiply(modeBonus);
    }
}
