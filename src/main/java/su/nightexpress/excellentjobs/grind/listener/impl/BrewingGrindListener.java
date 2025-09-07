package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BrewingGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BrewingGrindType;
import su.nightexpress.excellentjobs.job.workstation.WorkstationMode;

public class BrewingGrindListener extends GrindListener<BrewingGrindTable, BrewingGrindType> {

    public BrewingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BrewingGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrindBrewEnd(BrewEvent event) {
        BrewerInventory brewerInventory = event.getContents();
        BrewingStand stand = brewerInventory.getHolder();
        if (stand == null) return;

        ItemStack ingredient = brewerInventory.getIngredient();
        if (ingredient == null || ingredient.getType().isAir()) return;

        Player player = this.plugin.getJobManager().getWorkstationOwner(stand);
        if (player == null) return;

        if (!this.grindManager.canGrinding(player)) return;

        int potionsAmount = event.getResults().size();
        WorkstationMode mode = this.plugin.getJobManager().getWorkstationMode(stand);

        this.giveXP(player, (skill, table) -> {
            return table.getIngredientXP(ingredient, potionsAmount, mode == WorkstationMode.AUTO);
        });
    }
}
