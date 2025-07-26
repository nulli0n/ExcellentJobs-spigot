package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

import java.util.Set;

public class MiningWork extends Work<BlockBreakEvent, Material> {

    private static final Set<Material> NO_AGE_CHECK = Lists.newSet(
        Material.SUGAR_CANE,
        Material.BAMBOO,
        Material.CACTUS
    );

    public MiningWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, BlockBreakEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getBlockData() instanceof Ageable ageable) {
            if (!NO_AGE_CHECK.contains(block.getType())) {
                if (ageable.getAge() < ageable.getMaximumAge()) return false;
            }
        }

        if (PlayerBlockTracker.isTracked(block)) {
            return false;
        }

        Player player = event.getPlayer();
        this.doObjective(player, block.getType(), 1);
        return true;
    }
}
