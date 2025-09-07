package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.BasicEntityGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;

public class BasicEntityGrindType extends GrindType<BasicEntityGrindTable> {

    public BasicEntityGrindType(@NotNull String id) {
        super(id, BasicEntityGrindTable.class);
    }

    @Override
    public boolean isToolRequired() {
        return false;
    }

    @Override
    @NotNull
    public GrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return BasicEntityGrindTable.read(config, path);
    }

    @Override
    @NotNull
    public GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        return new BasicEntityGrindTable(SourceTable.fromConverted(convertedEntries, GrindAdapterFamily.ENTITY));
    }
}
