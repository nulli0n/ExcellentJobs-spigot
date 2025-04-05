package su.nightexpress.excellentjobs.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.data.impl.JobData;

public abstract class JobXPEvent extends JobDataEvent implements Cancellable {

    protected boolean isCancelled;
    protected String source;
    protected int    xp;

    public JobXPEvent(@NotNull Player player, @NotNull JobUser user, @NotNull JobData jobData, int xp) {
        super(player, user, jobData);
        this.setXP(xp);
        this.setSource(source);
    }

    @NotNull
    public static JobXPEvent createEvent(@NotNull Player player, @NotNull JobUser user, @NotNull JobData jobData, int xp) {
        if (xp < 0) {
            return new JobXPLoseEvent(player, user, jobData, xp);
        }
        else {
            return new JobXPGainEvent(player, user, jobData, xp);
        }
    }

    @Override
    public boolean isCancelled() {
        return isCancelled || this.getXP() == 0;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @NotNull
    public final String getSource() {
        return source;
    }

    public final void setSource(@NotNull String source) {
        this.source = source;
    }

    public final int getXP() {
        return xp;
    }

    public final void setXP(int xp) {
        this.xp = Math.abs(xp);
    }
}
