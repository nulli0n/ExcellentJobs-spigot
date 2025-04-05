package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class BuildingWork extends Work<BlockPlaceEvent, Material> {

    public BuildingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, BlockPlaceEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        this.doObjective(event.getPlayer(), block.getType(), 1);
        return true;
    }
}
