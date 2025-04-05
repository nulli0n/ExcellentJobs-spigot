package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class FertilizingWork extends Work<BlockFertilizeEvent, Material> {

    public FertilizingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, BlockFertilizeEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull BlockFertilizeEvent event) {
        Player player = event.getPlayer();
        if (player == null) return false;

        this.doObjective(player, event.getBlock().getType(), 1);

        event.getBlocks().forEach(blockState -> {
            this.doObjective(player, blockState.getType(), 1);
        });
        return true;
    }
}
