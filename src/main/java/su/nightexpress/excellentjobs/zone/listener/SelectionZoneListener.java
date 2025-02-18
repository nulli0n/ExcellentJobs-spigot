package su.nightexpress.excellentjobs.zone.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Selection;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.manager.AbstractListener;

public class SelectionZoneListener extends AbstractListener<JobsPlugin> {

    private final ZoneManager manager;

    public SelectionZoneListener(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        this.manager.exitSelection(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Selection selection = this.manager.getSelection(event.getPlayer());
        if (selection != null) {
            selection.clear();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType().isAir()) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null || !this.manager.isCuboidWand(itemStack)) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Player player = event.getPlayer();
        this.manager.selectPosition(player, itemStack, block.getLocation(), event.getAction());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWandDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (this.manager.isCuboidWand(itemStack)) {
            this.manager.exitSelection(player);

            Zone zone = this.manager.getZoneByWandItem(itemStack);
            if (zone != null) {
                this.manager.openEditor(player, zone);
            }

            itemStack.setAmount(0);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWandMove(InventoryClickEvent event) {
        InventoryType type = event.getInventory().getType();
        if (type == InventoryType.CRAFTING || type == InventoryType.CREATIVE) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null && this.manager.isCuboidWand(itemStack)) {
            event.setCancelled(true);
        }
    }
}
