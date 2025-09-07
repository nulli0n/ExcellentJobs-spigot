package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.GatheringGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;

public class GatheringGrindType extends GrindType<GatheringGrindTable> {

    public GatheringGrindType(@NotNull String id) {
        super(id, GatheringGrindTable.class);
    }

    @Override
    public boolean isToolRequired() {
        return false; // No matter what tool was used to get block/mob drop.
    }

    @Override
    @NotNull
    public GatheringGrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return GatheringGrindTable.read(config, path);
    }

    @Override
    @NotNull
    public GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        return new GatheringGrindTable(SourceTable.fromConverted(convertedEntries, GrindAdapterFamily.ITEM), SourceTable.EMPTY);
    }
}
