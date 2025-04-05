package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.Players;

public class CraftingWork extends Work<CraftItemEvent, Material> {

    public CraftingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, CraftItemEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull CraftItemEvent event) {
        if (event.getClick() == ClickType.MIDDLE) return false;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return false;

        Player player = (Player) event.getWhoClicked();
        ItemStack craft = new ItemStack(item);
        Material type = craft.getType();

        // Идеальный вариант
        // Считаем до, считаем после, разницу записываем в прогресс хД
        boolean numberKey = event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD;

        if (event.isShiftClick() || numberKey) {
            int has = Players.countItem(player, craft);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                int now = Players.countItem(player, craft);
                int crafted = now - has;
                this.doObjective(player, type, crafted);
            });
        }
        else {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir() && (!cursor.isSimilar(craft) || cursor.getAmount() >= cursor.getMaxStackSize())) return false;

            this.doObjective(player, type, 1);
        }
        return true;
    }
}
