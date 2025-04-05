package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

@SuppressWarnings("UnstableApiUsage")
public class RepairingWork extends Work<InventoryClickEvent, Material> {

    public RepairingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, InventoryClickEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull InventoryClickEvent event) {
        if (!(event.getView() instanceof AnvilView anvil)) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        if (anvil.getRepairCost() <= 0) return false;

        ItemStack first = anvil.getItem(0);
        if (first == null || first.getType().isAir()) return false;

        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        if (first.getType() != result.getType()) return false;

        int damageSource = 0;
        int damageResult = 0;

        if (first.getItemMeta() instanceof Damageable damageable) {
            damageSource = damageable.getDamage();
        }
        if (result.getItemMeta() instanceof Damageable damageable) {
            damageResult = damageable.getDamage();
        }
        if (damageSource == damageResult) return false;

        Player player = (Player) event.getWhoClicked();
        Material material = result.getType();

        plugin.runTask(task -> {
            ItemStack result2 = anvil.getItem(2);
            if (result2 != null && !result2.getType().isAir()) return;

            this.doObjective(player, material, 1);
        });
        return true;
    }
}
