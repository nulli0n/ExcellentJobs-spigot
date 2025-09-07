package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicBlockGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicBlockGrindType;

public class BuildingGrindListener extends GrindListener<BasicBlockGrindTable, BasicBlockGrindType> {

    public BuildingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicBlockGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        if (!this.grindManager.canGrinding(player)) return;

        this.giveXP(player, (job, table) -> table.getBlockXP(block));
    }
}
