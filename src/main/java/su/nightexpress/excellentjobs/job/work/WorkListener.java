package su.nightexpress.excellentjobs.job.work;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class WorkListener<E extends Event, O> implements Listener, EventExecutor {

    private final Class<E>   eventClass;
    private final Work<E, O> work;

    public WorkListener(@NotNull Class<E> eventClass, @NotNull Work<E, O> work) {
        this.eventClass = eventClass;
        this.work = work;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event bukkitEvent) {
        if (!this.eventClass.isAssignableFrom(bukkitEvent.getClass())) return;

        E event = this.eventClass.cast(bukkitEvent);
        this.work.handle(event);
    }
}
