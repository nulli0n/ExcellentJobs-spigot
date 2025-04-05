package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class ShearingWork extends Work<PlayerShearEntityEvent, EntityType> {

    public ShearingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerShearEntityEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<EntityType> getFormatter() {
        return WorkFormatters.ENITITY_TYPE;
    }

    @Override
    public boolean handle(@NotNull PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();

        this.doObjective(player, entity.getType(), 1);
        return true;
    }
}
