package su.nightexpress.excellentjobs.grind.provider;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;

public interface GrindListenerProvider<E extends GrindTable, T extends GrindType<E>> {

    @NotNull GrindListener<E, T> provide(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull T grindType);
}
