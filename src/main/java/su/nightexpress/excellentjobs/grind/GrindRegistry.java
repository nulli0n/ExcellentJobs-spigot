package su.nightexpress.excellentjobs.grind;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.type.GrindType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GrindRegistry extends AbstractManager<JobsPlugin> {

    private final Map<String, GrindType<?>> grindTypeByIdMap;

    public GrindRegistry(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.grindTypeByIdMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onShutdown() {
        this.grindTypeByIdMap.clear();
    }

    public <T extends GrindType<?>> void registerGrindType(@NotNull T type) {
        this.grindTypeByIdMap.put(type.getId(), type);
    }

    @Nullable
    public GrindType<?> getTypeById(@NotNull String id) {
        return this.grindTypeByIdMap.get(id.toLowerCase());
    }

    @NotNull
    public Set<GrindType<?>> getTypes() {
        return new HashSet<>(this.grindTypeByIdMap.values());
    }
}
