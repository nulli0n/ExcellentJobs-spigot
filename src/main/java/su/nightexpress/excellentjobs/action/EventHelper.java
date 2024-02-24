package su.nightexpress.excellentjobs.action;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;

public interface EventHelper<E extends Event, O> {

    boolean handle(@NotNull JobsPlugin plugin, @NotNull E event, @NotNull ObjectiveProcessor<O> processor);
}
