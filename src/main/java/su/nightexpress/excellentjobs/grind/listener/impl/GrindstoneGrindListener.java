package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicItemGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicItemGrindType;

public class GrindstoneGrindListener extends GrindListener<BasicItemGrindTable, BasicItemGrindType> {

    public GrindstoneGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicItemGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onXPInventoryHandler(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof GrindstoneInventory inventory)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!this.grindManager.canGrinding(player)) return;

        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return;

        if (source.getEnchantments().size() == result.getEnchantments().size()) return;

        this.giveXP(player, (skill, table) -> table.getItemXP(result, 1));
    }
}
