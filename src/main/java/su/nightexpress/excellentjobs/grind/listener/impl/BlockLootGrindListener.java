package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.grind.GrindContext;
import su.nightexpress.excellentjobs.api.grind.GrindProtection;
import su.nightexpress.excellentjobs.api.grind.GrindType;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;

@NullMarked
public class BlockLootGrindListener extends GrindListener<ItemStack> {

    public BlockLootGrindListener(JobsPlugin plugin,
                                  GrindManager manager,
                                  @Nullable GrindProtection protection,
                                  GrindType<ItemStack> type) {
        super(plugin, manager, protection, type);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockHarvest(PlayerHarvestBlockEvent event) {
        Player player = event.getPlayer();
        if (this.protection != null && !this.protection.isGrindAllowed(player)) return;

        event.getItemsHarvested().forEach(itemStack -> {
            this.giveXP(player, itemStack, GrindContext.create());
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        BlockState blockState = event.getBlockState();
        if (blockState instanceof Container) return; // Do not handle container's drops.

        Player player = event.getPlayer();
        Block block = event.getBlock();

        boolean ignore = this.protection().stream().anyMatch(protection -> {
            if (!protection.isGrindAllowed(player)) return true;

            return protection.isArtificalBlock(block) || protection.isUngrowthBlock(blockState);
        });
        if (ignore) return;

        event.getItems().forEach(item -> {
            ItemStack itemStack = item.getItemStack();

            this.giveXP(player, itemStack, GrindContext.create());
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
        if (this.protection != null && !this.protection.isGrindAllowed(player)) return;

        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack.getType() != Material.GLASS_BOTTLE) return;

        this.plugin.runTask(player, () -> {
            ItemStack honey = player.getInventory().getItem(slot);
            if (honey == null || honey.getType() != Material.HONEY_BOTTLE) return;

            this.giveXP(player, honey, GrindContext.create());
        });
    }
}
