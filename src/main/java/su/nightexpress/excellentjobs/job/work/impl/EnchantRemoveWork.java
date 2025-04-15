package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.excellentjobs.job.work.wrapper.WrappedEnchant;

import java.util.HashMap;

public class EnchantRemoveWork extends Work<InventoryClickEvent, WrappedEnchant> {

    public EnchantRemoveWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, InventoryClickEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<WrappedEnchant> getFormatter() {
        return WorkFormatters.WRAPPED_ENCHANTMENT;
    }

    @Override
    public boolean handle(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.GRINDSTONE) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return false;

        var sourceEnchants = new HashMap<>(source.getEnchantments());
        var resultEnchants = new HashMap<>(result.getEnchantments());
        if (sourceEnchants.size() == resultEnchants.size()) return false;

        sourceEnchants.keySet().removeAll(resultEnchants.keySet());

        Player player = (Player) event.getWhoClicked();
        sourceEnchants.forEach((enchantment, level) -> {
            this.doObjective(player, new WrappedEnchant(enchantment, level), 1);
        });
        return true;
    }
}
