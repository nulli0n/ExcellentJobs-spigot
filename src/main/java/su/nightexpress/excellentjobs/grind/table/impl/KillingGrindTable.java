package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.util.Calc;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class KillingGrindTable implements GrindTable {

    private final double      spawnerMobBonus;
    private final SourceTable killTable;

    public KillingGrindTable(@NotNull SourceTable killTable, double spawnerMobBonus) {
        this.killTable = killTable;
        this.spawnerMobBonus = spawnerMobBonus;
    }

    @NotNull
    public static KillingGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        double spawnerMobBonus = ConfigValue.create(path + ".SpawnerMobBonus", -90D).read(config);
        SourceTable killsTable = ConfigValue.create(path + ".Mobs", SourceTable::read, SourceTable.EMPTY).read(config);

        return new KillingGrindTable(killsTable, spawnerMobBonus);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".SpawnerMobBonus", this.spawnerMobBonus);
        config.set(path + ".Mobs", this.killTable);
    }

    @NotNull
    public GrindReward applySpawnerMobBonus(@NotNull GrindReward reward) {
        return reward.multiply(Calc.toMult(this.spawnerMobBonus));
    }

    @NotNull
    public GrindReward getKillXP(@NotNull Entity mob, @NotNull ItemStack tool, boolean isSpawner) {
        GrindReward reward = this.killTable.rollForEntityOrDefault(mob, GrindAdapterFamily.ENTITY);

        if (isSpawner) {
            return this.applySpawnerMobBonus(reward);
        }

        return reward;
    }
}
