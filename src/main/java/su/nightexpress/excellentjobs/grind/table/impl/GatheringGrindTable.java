package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class GatheringGrindTable implements GrindTable {

    private final SourceTable blockResourcesTable;
    private final SourceTable mobResourcesTable;

    public GatheringGrindTable(@NotNull SourceTable blockResourcesTable, @NotNull SourceTable mobResourcesTable) {
        this.blockResourcesTable = blockResourcesTable;
        this.mobResourcesTable = mobResourcesTable;
    }

    @NotNull
    public static GatheringGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        SourceTable blockResourcesTable = ConfigValue.create(path + ".BlockDrops", SourceTable::read, SourceTable.EMPTY).read(config);
        SourceTable mobResourcesTable = ConfigValue.create(path + ".MobDrops", SourceTable::read, SourceTable.EMPTY).read(config);

        return new GatheringGrindTable(blockResourcesTable, mobResourcesTable);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".BlockDrops", this.blockResourcesTable);
        config.set(path + ".MobDrops", this.mobResourcesTable);
    }

    @NotNull
    public GrindReward getBlockResourceXP(@NotNull ItemStack itemStack) {
        return this.getResourceXP(itemStack, this.blockResourcesTable);
    }

    @NotNull
    public GrindReward getMobResourceXP(@NotNull ItemStack itemStack) {
        return this.getResourceXP(itemStack, this.mobResourcesTable);
    }

    @NotNull
    private GrindReward getResourceXP(@NotNull ItemStack itemStack, @NotNull SourceTable table) {
        int amount = itemStack.getAmount();
        GrindReward reward = table.rollForEntityOrDefault(itemStack, GrindAdapterFamily.ITEM);

        return reward.multiply(amount);
    }
}
