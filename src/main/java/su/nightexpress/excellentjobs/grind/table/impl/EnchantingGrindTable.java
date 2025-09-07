package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.util.Calc;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class EnchantingGrindTable implements GrindTable {

    private final SourceTable enchantsTable;
    private final double  levelBonus;

    public EnchantingGrindTable(@NotNull SourceTable enchantsTable, double levelBonus) {
        this.enchantsTable = enchantsTable;
        this.levelBonus = levelBonus;
    }

    @NotNull
    public static EnchantingGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        double levelBonus = ConfigValue.create(path + ".LevelBonus", 25D).read(config);
        SourceTable table = ConfigValue.create(path + ".Enchantments", SourceTable::read, SourceTable.EMPTY).read(config);

        return new EnchantingGrindTable(table, levelBonus);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".LevelBonus", this.levelBonus);
        config.set(path + ".Enchantments", this.enchantsTable);
    }

    @NotNull
    public GrindReward addLevelBonus(GrindReward reward, int level) {
        return reward.multiply(Calc.toMult(this.levelBonus * level));
    }

    @NotNull
    public GrindReward getEnchantXP(@NotNull Enchantment enchantment, int level) {
        GrindReward reward = this.enchantsTable.rollForEntityOrDefault(enchantment, GrindAdapterFamily.ENCHANTMENT);

        return addLevelBonus(reward, level);
    }
}
