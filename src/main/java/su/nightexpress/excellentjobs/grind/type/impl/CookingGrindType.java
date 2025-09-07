package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.CookingGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;

public class CookingGrindType extends GrindType<CookingGrindTable> {

    public CookingGrindType(@NotNull String id) {
        super(id, CookingGrindTable.class);
    }

    @Override
    public boolean isToolRequired() {
        return false;
    }

    @Override
    @NotNull
    public CookingGrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return CookingGrindTable.read(config, path);
    }

    @Override
    public @NotNull GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        return new CookingGrindTable(SourceTable.fromConverted(convertedEntries, GrindAdapterFamily.ITEM), -80, 50);
    }
}
