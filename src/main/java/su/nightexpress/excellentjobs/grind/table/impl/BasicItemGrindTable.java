package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;

public class BasicItemGrindTable implements GrindTable {

    private final SourceTable itemsTable;

    public BasicItemGrindTable(@NotNull SourceTable itemsTable) {
        this.itemsTable = itemsTable;
    }

    @NotNull
    public static BasicItemGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        SourceTable itemsTable = ConfigValue.create(path + ".Items", SourceTable::read, SourceTable.EMPTY).read(config);

        return new BasicItemGrindTable(itemsTable);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Items", this.itemsTable);
    }

    @NotNull
    public GrindReward getItemXP(@NotNull ItemStack itemStack, int units) {
        GrindReward reward = new GrindReward();

        // Simulate random xp & money for each crafted unit, as if it was crafted one by one.
        for (int i = 0; i < units; i++) {
            reward.add(this.itemsTable.rollForEntityOrDefault(itemStack, GrindAdapterFamily.ITEM));
        }

        return reward;
    }
}
