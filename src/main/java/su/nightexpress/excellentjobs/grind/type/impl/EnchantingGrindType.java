package su.nightexpress.excellentjobs.grind.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.EnchantingGrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnchantingGrindType extends GrindType<EnchantingGrindTable> {

    public EnchantingGrindType(@NotNull String id) {
        super(id, EnchantingGrindTable.class);
    }

    @Override
    public boolean isToolRequired() {
        return false;
    }

    @Override
    @NotNull
    public EnchantingGrindTable readTable(@NotNull FileConfig config, @NotNull String path) {
        return EnchantingGrindTable.read(config, path);
    }

    @Override
    @NotNull
    public GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries) {
        Map<String, SourceReward> fixed = new LinkedHashMap<>();
        convertedEntries.forEach((id, reward) -> {
            fixed.put(id.split(";")[0], reward); // Cut levels from entry names since it's controlled by table setting now.
        });

        return new EnchantingGrindTable(SourceTable.fromConverted(fixed, GrindAdapterFamily.ENCHANTMENT), 25D);
    }
}
