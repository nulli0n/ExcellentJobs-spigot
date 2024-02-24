package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.job.impl.Job;

public abstract class JobEvent extends Event {

    protected final Player  player;
    protected final Job job;

    public JobEvent(boolean async, @NotNull Player player, @NotNull Job job) {
        super(async);
        this.player = player;
        this.job = job;
    }

    @NotNull
    public final Player getPlayer() {
        return this.player;
    }

    @NotNull
    public final Job getJob() {
        return this.job;
    }
}
