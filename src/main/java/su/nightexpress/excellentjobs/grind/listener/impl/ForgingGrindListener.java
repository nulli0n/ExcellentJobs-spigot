package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicItemGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicItemGrindType;

public class ForgingGrindListener extends GrindListener<BasicItemGrindTable, BasicItemGrindType> {

    public ForgingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicItemGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getView() instanceof AnvilView anvil)) return;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return;

        if (anvil.getRepairCost() <= 0) return;

        ItemStack first = anvil.getItem(0);
        if (first == null || first.getType().isAir()) return;

        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType().isAir()) return;

        if (first.getType() != result.getType() || result.isSimilar(first)) return;

        Player player = (Player) event.getWhoClicked();
        if (!this.grindManager.canGrinding(player)) return;

        ItemStack resultCopy = new ItemStack(result);

        this.plugin.runTask(task -> {
            ItemStack updated = anvil.getItem(2);
            if (updated != null && !updated.getType().isAir()) return;

            this.giveXP(player, (job, table) -> table.getItemXP(resultCopy, 1));
        });
    }
}
