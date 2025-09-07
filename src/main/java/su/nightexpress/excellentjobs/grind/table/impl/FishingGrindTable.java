package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;

public class FishingGrindTable implements GrindTable {

    private final SourceTable entityTable;
    private final SourceTable itemTable;

    public FishingGrindTable(@NotNull SourceTable entityTable, @NotNull SourceTable itemTable) {
        this.entityTable = entityTable;
        this.itemTable = itemTable;
    }

    @NotNull
    public static FishingGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        SourceTable entityTable = ConfigValue.create(path + ".Entities", SourceTable::read, SourceTable.EMPTY).read(config);
        SourceTable itemTable = ConfigValue.create(path + ".Items", SourceTable::read, SourceTable.EMPTY).read(config);

        return new FishingGrindTable(entityTable, itemTable);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Entities", this.entityTable);
        config.set(path + ".Items", this.itemTable);
    }

    @NotNull
    public GrindReward getXPForCaught(@NotNull Entity entity) {
        if (entity instanceof Item item) {
            return this.itemTable.rollForEntityOrDefault(item.getItemStack(), GrindAdapterFamily.ITEM);
        }

        return this.entityTable.rollForEntityOrDefault(entity, GrindAdapterFamily.ENTITY);
    }
}
