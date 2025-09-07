package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.KillingGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;

public class KillingGrindType extends GrindType<KillingGrindTable> {

    public KillingGrindType(@NotNull String id) {
        super(id, KillingGrindTable.class);
    }

    @Override
    public boolean isToolRequired() {
        return true;
    }

    @Override
    @NotNull
    public KillingGrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return KillingGrindTable.read(config, path);
    }

    @Override
    @NotNull
    public GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        return new KillingGrindTable(SourceTable.fromConverted(convertedEntries, GrindAdapterFamily.ENTITY), -90D);
    }
}
