package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicEntityGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicEntityGrindType;

public class ShearingGrindListener extends GrindListener<BasicEntityGrindTable, BasicEntityGrindType> {

    public ShearingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicEntityGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        if (this.grindManager.canGrinding(player)) return;

        Entity entity = event.getEntity();

        this.giveXP(player, (job, table) -> table.getMobReward(entity));
    }
}
