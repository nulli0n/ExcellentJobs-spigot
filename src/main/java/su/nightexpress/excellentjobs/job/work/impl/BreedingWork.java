package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class BreedingWork extends Work<EntityBreedEvent, EntityType> {

    public BreedingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EntityBreedEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<EntityType> getFormatter() {
        return WorkFormatters.ENITITY_TYPE;
    }

    @Override
    public boolean handle(@NotNull EntityBreedEvent event) {
        LivingEntity breeder = event.getBreeder();
        if (!(breeder instanceof Player player)) return false;

        this.doObjective(player, event.getEntity().getType(), 1);
        return true;
    }
}
