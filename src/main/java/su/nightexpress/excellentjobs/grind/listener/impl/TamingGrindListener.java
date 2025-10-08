package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicEntityGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicEntityGrindType;

public class TamingGrindListener extends GrindListener<BasicEntityGrindTable, BasicEntityGrindType> {

    public TamingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicEntityGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        if (!this.grindManager.canGrinding(player)) return;

        LivingEntity entity = event.getEntity();

        this.giveXP(player, (job, table) -> table.getMobReward(entity));
    }
}
