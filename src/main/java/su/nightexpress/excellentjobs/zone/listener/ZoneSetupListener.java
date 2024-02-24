package su.nightexpress.excellentjobs.zone.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.util.BlockPos;
import su.nightexpress.excellentjobs.util.Cuboid;
import su.nightexpress.excellentjobs.util.Visuals;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.manager.AbstractListener;

import java.util.Map;
import java.util.WeakHashMap;

public class ZoneSetupListener extends AbstractListener<JobsPlugin> {

    private final ZoneManager zoneManager;
    private final Map<Player, BlockPos[]> cuboidMap;

    public ZoneSetupListener(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin);
        this.zoneManager = zoneManager;
        this.cuboidMap = new WeakHashMap<>();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onZoneCuboidSelection(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType().isAir()) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        Zone zone = this.zoneManager.getZoneByWandItem(item);
        if (zone == null) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(Perms.PLUGIN)) return;

        BlockPos pos = BlockPos.from(block.getLocation());
        //Cuboid cuboid = zone.getCuboid();
        //World world = block.getWorld();

        BlockPos[] cuboid = this.cuboidMap.computeIfAbsent(player, k -> new BlockPos[] {BlockPos.empty(), BlockPos.empty()});
        int index = event.getAction() == Action.LEFT_CLICK_BLOCK ? 0 : 1;
        cuboid[index] = pos;

        Visuals.highlightPoints(player, player.getWorld(), cuboid);

        zone.setWorld(block.getWorld());
        zone.setCuboid(new Cuboid(cuboid[0], cuboid[1]));
        zone.save();

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onZoneCuboidDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (this.zoneManager.getZoneByWandItem(item) != null) {
            this.cuboidMap.remove(event.getPlayer());
            event.getItemDrop().remove();
            Visuals.removeVisuals(event.getPlayer());
        }
    }
}
