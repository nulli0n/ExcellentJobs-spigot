package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class TamingWork extends Work<EntityTameEvent, EntityType> {

    public TamingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EntityTameEvent.class, id);
    }

    @Override
    public @NotNull WorkFormatter<EntityType> getFormatter() {
        return WorkFormatters.ENITITY_TYPE;
    }

    @Override
    public boolean handle(@NotNull EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        LivingEntity entity = event.getEntity();

        this.doObjective(player, entity.getType(), 1);
        return true;
    }
}
