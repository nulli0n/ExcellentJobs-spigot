package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.GatheringGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.GatheringGrindType;

public class GatheringGrindListener extends GrindListener<GatheringGrindTable, GatheringGrindType> {

    public GatheringGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull GatheringGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockHarvest(PlayerHarvestBlockEvent event) {
        Player player = event.getPlayer();
        if (!this.grindManager.canGrinding(player)) return;

        event.getItemsHarvested().forEach(itemStack -> {
            this.giveXP(player, (job, table) -> table.getBlockResourceXP(itemStack));
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (!this.grindManager.canGrinding(player)) return;
        if (event.getBlockState() instanceof Container) return; // Do not handle container's drops.

        Block block = event.getBlock();
        if (this.grindManager.isPlayerBlock(block)) return;

        event.getItems().forEach(item -> {
            ItemStack itemStack = item.getItemStack();

            this.giveXP(player, (job, table) -> table.getBlockResourceXP(itemStack));
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDrop(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        Player player = entity.getKiller();
        if (player == null) return;
        if (!this.grindManager.canGrinding(player)) return;
        if (this.grindManager.isSpawnerMob(entity)) return;

        event.getDrops().forEach(itemStack -> {
            this.giveXP(player, (job, table) -> table.getMobResourceXP(itemStack));
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHoneyCollect(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BEEHIVE) return;

        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        Player player = event.getPlayer();
        if (!this.grindManager.canGrinding(player)) return;

        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack == null || itemStack.getType() != Material.GLASS_BOTTLE) return;

        this.plugin.runTask(task -> {
            ItemStack honey = player.getInventory().getItem(slot);
            if (honey == null || honey.getType() != Material.HONEY_BOTTLE) return;

            this.giveXP(player, (job, table) -> table.getBlockResourceXP(honey));
        });
    }
}
