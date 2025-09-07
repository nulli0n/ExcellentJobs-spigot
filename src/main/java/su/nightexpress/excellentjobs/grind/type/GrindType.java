package su.nightexpress.excellentjobs.grind.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceReward;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Map;
import java.util.Optional;

public abstract class GrindType<T> {

    protected final String id;
    protected final Class<T> clazz;

    public GrindType(@NotNull String id, @NotNull Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    /**
     * Defines whether a specific skill tool is required to obtain the XP from given sources.
     * @return true if a tool is required, false otherwise.
     */
    public abstract boolean isToolRequired();

    @NotNull
    public abstract GrindTable readTable(@NotNull FileConfig config, @NotNull String path);

    @NotNull
    public abstract GrindTable convertTable(@NotNull Map<String, SourceReward> convertedEntries);

    @NotNull
    public Optional<T> adaptTable(@NotNull GrindTable table) {
        if (this.clazz.isAssignableFrom(table.getClass())) {
            return Optional.of(this.clazz.cast(table));
        }
        return Optional.empty();
    }

    @NotNull
    public String getId() {
        return this.id;
    }
}
