package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicEntityGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicEntityGrindType;

public class BreedingGrindListener extends GrindListener<BasicEntityGrindTable, BasicEntityGrindType> {

    public BreedingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicEntityGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        LivingEntity breeder = event.getBreeder();
        if (!(breeder instanceof Player player)) return;
        if (!this.grindManager.canGrinding(player)) return;

        this.giveXP(player, (job, table) -> table.getMobReward(event.getEntity()));
    }
}
