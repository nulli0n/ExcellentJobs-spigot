package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.BasicItemGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;

public class BasicItemGrindType extends GrindType<BasicItemGrindTable> {

    public BasicItemGrindType(@NotNull String id) {
        super(id, BasicItemGrindTable.class);
    }

    @Override
    public boolean isToolRequired() {
        return false;
    }

    @Override
    @NotNull
    public BasicItemGrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return BasicItemGrindTable.read(config, path);
    }

    @Override
    @NotNull
    public GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        return new BasicItemGrindTable(SourceTable.fromConverted(convertedEntries, GrindAdapterFamily.ITEM));
    }
}
