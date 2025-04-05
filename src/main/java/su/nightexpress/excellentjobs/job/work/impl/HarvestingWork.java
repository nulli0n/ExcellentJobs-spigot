package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

public class HarvestingWork extends Work<PlayerHarvestBlockEvent, Material> {

    public HarvestingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerHarvestBlockEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull PlayerHarvestBlockEvent event) {
        Block block = event.getHarvestedBlock();
        if (PlayerBlockTracker.isTracked(block)) return false;

        Player player = event.getPlayer();
        event.getItemsHarvested().forEach(itemStack -> {
            this.doObjective(player, itemStack.getType(), itemStack.getAmount());
        });
        return true;
    }
}
