package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicBlockGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.MiningGrindType;
import su.nightexpress.nightcore.util.Lists;

import java.util.Set;

public class MiningGrindListener extends GrindListener<BasicBlockGrindTable, MiningGrindType> {

    private static final Set<Material> TALL_BLOCKS = Lists.newSet(Material.BAMBOO, Material.SUGAR_CANE);

    public MiningGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull MiningGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    private boolean isTallBlock(@NotNull Material material) {
        return TALL_BLOCKS.contains(material);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGrindMining(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!this.grindManager.canGrinding(player)) return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();

        this.giveBlockXP(player, tool, block);
    }

    private void giveBlockXP(@NotNull Player player, @Nullable ItemStack tool, @NotNull Block block) {
        if (this.grindManager.isPlayerBlock(block)) return;

        BlockData blockData = block.getBlockData();
        boolean isTall = this.isTallBlock(block.getType());

        // Do not give XP for ungrowth plants.
        if (!isTall && blockData instanceof Ageable age) {
            if (age.getAge() < age.getMaximumAge()) {
                return;
            }
        }

        this.giveXP(player, tool, (skill, table) -> table.getBlockXP(block));

        if (isTall) {
            Block above = block.getRelative(BlockFace.UP);
            if (above.getType() == block.getType()) {
                this.giveBlockXP(player, tool, above);
            }
        }
    }
}
