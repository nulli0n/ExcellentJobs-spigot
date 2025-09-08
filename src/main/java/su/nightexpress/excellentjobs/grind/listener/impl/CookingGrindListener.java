package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.CookingGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.CookingGrindType;
import su.nightexpress.excellentjobs.job.workstation.Workstation;
import su.nightexpress.excellentjobs.job.workstation.WorkstationMode;

public class CookingGrindListener extends GrindListener<CookingGrindTable, CookingGrindType> {

    public CookingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull CookingGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrindCooking(BlockCookEvent event) {
        Block block = event.getBlock();
        Workstation workstation = Workstation.getByBlock(block);
        if (workstation == null) return;

        TileState tile = workstation.getBackend();

        Player player = this.plugin.getJobManager().getWorkstationOwner(tile);
        if (player == null) return;

        if (!this.grindManager.canGrinding(player)) return;

        ItemStack ingredient = event.getSource();
        WorkstationMode mode = this.plugin.getJobManager().getWorkstationMode(tile);

        this.giveXP(player, (skill, table) -> table.getIngredientXP(ingredient, mode == WorkstationMode.AUTO));
    }
}
