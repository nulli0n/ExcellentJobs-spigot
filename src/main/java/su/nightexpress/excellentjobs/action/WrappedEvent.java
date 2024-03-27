package su.nightexpress.excellentjobs.action;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.JobManager;

public class WrappedEvent<E extends Event, O> implements Listener, EventExecutor, ObjectiveProcessor<O> {

    private final JobsPlugin       plugin;
    private final Class<E>         eventClass;
    private final ActionType<E, O> actionType;

    public WrappedEvent(@NotNull JobsPlugin plugin,
                        @NotNull Class<E> eventClass,
                        @NotNull ActionType<E, O> actionType) {
        this.plugin = plugin;
        this.eventClass = eventClass;
        this.actionType = actionType;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event bukkitEvent) {
        if (!this.eventClass.isAssignableFrom(bukkitEvent.getClass())) return;

        E event = this.eventClass.cast(bukkitEvent);
        this.actionType.getEventHelper().handle(this.plugin, event, this);
    }

    @Override
    public void progressObjective(@NotNull Player player, @NotNull O object, int amount, double multiplier) {
        //if (player.getGameMode() == GameMode.CREATIVE) return;
        // Already included in check below
        if (!JobManager.canWorkHere(player)) return;

        this.plugin.getJobManager().doObjective(player, this.actionType, object, amount, multiplier);
    }
}
