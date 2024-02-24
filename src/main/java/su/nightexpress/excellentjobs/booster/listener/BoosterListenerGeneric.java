package su.nightexpress.excellentjobs.booster.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.nightcore.manager.AbstractListener;

@Deprecated
public class BoosterListenerGeneric extends AbstractListener<JobsPlugin> {

    public BoosterListenerGeneric(@NotNull JobsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoosterJoin(PlayerJoinEvent e) {
        /*if (!Config.BOOSTERS_NOTIFY_ON_JOIN) return;

        plugin.getBoosterManager().notifyBooster(e.getPlayer());*/
    }
}
