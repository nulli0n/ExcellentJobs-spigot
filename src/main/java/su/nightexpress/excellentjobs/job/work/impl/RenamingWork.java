package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

@SuppressWarnings("UnstableApiUsage")
public class RenamingWork extends Work<InventoryClickEvent, Material> {

    public RenamingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
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

        String renameText = anvil.getRenameText();
        if (renameText == null) return false;

        String nameSource = NightMessage.stripTags(ItemUtil.getSerializedName(first));
        String nameResult = NightMessage.stripTags(renameText);
        if (nameSource.equalsIgnoreCase(nameResult)) return false;

        Player player = (Player) event.getWhoClicked();
        plugin.runTask(() -> {
            ItemStack result2 = anvil.getItem(2);
            if (result2 != null && !result2.getType().isAir()) return;

            this.doObjective(player, result.getType(), result.getAmount());
        });
        return true;
    }
}
