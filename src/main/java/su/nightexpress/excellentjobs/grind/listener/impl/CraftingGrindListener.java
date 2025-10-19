package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicItemGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicItemGrindType;
import su.nightexpress.nightcore.util.Players;

public class CraftingGrindListener extends GrindListener<BasicItemGrindTable, BasicItemGrindType> {

    private static final String DEBUG_MASS_CRAFT = "You crafted x%s of %s.";

    public CraftingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicItemGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrindCrafting(CraftItemEvent event) {
        if (event.getClick() == ClickType.MIDDLE) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir()) return;

        Player player = (Player) event.getWhoClicked();
        if (!this.grindManager.canGrinding(player)) return;

        ItemStack craftedItem = new ItemStack(itemStack);
        int unitSize = craftedItem.getAmount();

        if (event.isShiftClick()) {
            int has = Players.countItem(player, craftedItem);
            this.plugin.getServer().getScheduler().runTask(plugin, () -> {
                int now = Players.countItem(player, craftedItem);
                int crafted = now - has;
                int craftedUnits = crafted / unitSize;

                this.giveXP(player, (skill, table) -> table.getItemXP(craftedItem, craftedUnits));
            });
            return;
        }

        ItemStack cursor = event.getCursor();
        if (!craftedItem.isSimilar(cursor) || cursor.getAmount() + unitSize > cursor.getMaxStackSize()) {
            return;
        }

        this.giveXP(player, (skill, table) -> table.getItemXP(craftedItem, 1));
    }
}
