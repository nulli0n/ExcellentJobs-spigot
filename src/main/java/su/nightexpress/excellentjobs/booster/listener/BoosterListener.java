package su.nightexpress.excellentjobs.booster.listener;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.booster.BoosterManager;
import su.nightexpress.nightcore.manager.AbstractListener;

public class BoosterListener extends AbstractListener<JobsPlugin> {

    private final BoosterManager manager;

    public BoosterListener(@NotNull JobsPlugin plugin, @NotNull BoosterManager manager) {
        super(plugin);
        this.manager = manager;
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onBoosterJoin(PlayerJoinEvent event) {
//
//    }
}
