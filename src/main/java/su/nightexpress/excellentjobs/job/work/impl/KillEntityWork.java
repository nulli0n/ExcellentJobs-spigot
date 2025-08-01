package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.excellentjobs.job.work.WorkUtils;
import su.nightexpress.excellentjobs.util.JobUtils;

public class KillEntityWork extends Work<EntityDeathEvent, EntityType> {

    public KillEntityWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EntityDeathEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<EntityType> getFormatter() {
        return WorkFormatters.ENITITY_TYPE;
    }

    @Override
    public boolean handle(@NotNull EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (JobManager.isDevastated(entity)) return false;
        if (!JobUtils.isVanillaMob(entity)) return false;

        Player killer = entity.getKiller();
        if (killer == null) return false;

        if (WorkUtils.isAbusingPetKilling(killer, entity)) return false;

        double multiplier = 0D;

        // LevelledMobs integration.
        if (Config.LEVELLED_MOBS_KILL_ENTITY_ENABLED.get()) {
            int level = JobUtils.getMobLevel(entity);
            double amount = Config.LEVELLED_MOBS_KILL_ENTITY_MULTIPLIER.get();
            multiplier = level * amount;
        }

        this.doObjective(killer, entity.getType(), 1, multiplier);
        return true;
    }
}
