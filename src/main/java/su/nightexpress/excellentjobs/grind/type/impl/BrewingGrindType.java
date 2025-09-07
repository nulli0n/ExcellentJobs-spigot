package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.BrewingGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;

public class BrewingGrindType extends GrindType<BrewingGrindTable> {

    public BrewingGrindType(@NotNull String id) {
        super(id, BrewingGrindTable.class);
    }

    @Override
    @NotNull
    public BrewingGrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return BrewingGrindTable.read(config, path);
    }

    @Override
    @NotNull
    public BrewingGrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        return new BrewingGrindTable(SourceTable.fromConverted(convertedEntries, GrindAdapterFamily.ITEM), 25, -80, 50);
    }

    @Override
    public boolean isToolRequired() {
        return false;
    }
}
