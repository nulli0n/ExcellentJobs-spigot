package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.event.bukkit.PlayerCollectedHoneyEvent;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class CollectHoneyWork extends Work<PlayerCollectedHoneyEvent, Material> {

    public CollectHoneyWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerCollectedHoneyEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull PlayerCollectedHoneyEvent event) {
        Player player = event.getPlayer();
        this.doObjective(player, event.getBlock().getType(), 1);
        return true;
    }
}
