package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.Players;

public class TradingWork extends Work<InventoryClickEvent, Material> {

    public TradingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, InventoryClickEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.MERCHANT) return false;

        MerchantInventory merchant = (MerchantInventory) inventory;
        MerchantRecipe recipe = merchant.getSelectedRecipe();
        if (recipe == null) return false;

        Player player = (Player) event.getWhoClicked();
        ItemStack result = recipe.getResult();
        int uses = recipe.getUses();
        int userHas = Players.countItem(player, result);

        plugin.runTask(() -> {
            int uses2 = recipe.getUses();
            if (uses2 <= uses) return;

            int amount = 1;
            if (event.isShiftClick()) {
                int resultSize = result.getAmount();
                int userNow = Players.countItem(player, result);
                int diff = userNow - userHas;
                amount = (int) ((double) diff / (double) resultSize);
            }

            this.doObjective(player, result.getType(), amount);
        });
        return true;
    }
}
