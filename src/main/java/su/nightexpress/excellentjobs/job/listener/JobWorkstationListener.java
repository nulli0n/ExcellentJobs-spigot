package su.nightexpress.excellentjobs.job.listener;

import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.workstation.Workstation;
import su.nightexpress.excellentjobs.job.workstation.WorkstationMode;
import su.nightexpress.nightcore.manager.AbstractListener;

import java.util.function.Consumer;

public class JobWorkstationListener extends AbstractListener<JobsPlugin> {

    private final JobManager manager;

    public JobWorkstationListener(@NotNull JobsPlugin plugin, @NotNull JobManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Workstation workstation = Workstation.getByBlock(block);
        if (workstation == null) return;

        Player player = event.getPlayer();

        this.modifyStandNextTick(workstation, station -> this.manager.setWorkstationOwnerId(station, player.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationAutomaticInventoryMoveEvent(InventoryMoveItemEvent event) {
        Inventory from = event.getSource();
        if (from.getType() != InventoryType.HOPPER) return;

        Inventory to = event.getDestination();
        Workstation workstation = Workstation.getByInventory(to);
        if (workstation == null) return;

        this.modifyStandNextTick(workstation, station -> this.manager.setWorkstationMode(station, WorkstationMode.AUTO));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationMenuClick(InventoryClickEvent event) {
        this.handleWorkstationInteraction(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationMenuDrag(InventoryDragEvent event) {
        this.handleWorkstationInteraction(event);
    }

    private void handleWorkstationInteraction(@NotNull InventoryInteractEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Workstation workstation = Workstation.getByInventory(event.getInventory());
        if (workstation == null || workstation.isCrafting()) return;

        this.modifyStandNextTick(workstation, station -> {
            this.manager.setWorkstationOwnerId(station, player.getUniqueId());
            this.manager.setWorkstationMode(station, WorkstationMode.MANUAL);
        });
    }

    private void modifyStandNextTick(@NotNull Workstation workstation, @NotNull Consumer<TileState> consumer) {
        this.plugin.runTask(() -> {
            if (!(workstation.getBackend().getLocation().getBlock().getState() instanceof TileState tickedStation)) return;

            consumer.accept(tickedStation);
            tickedStation.update(true, false);
        });
    }
}
