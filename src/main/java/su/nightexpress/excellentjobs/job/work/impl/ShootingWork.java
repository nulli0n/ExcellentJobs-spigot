package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class ShootingWork extends Work<ProjectileLaunchEvent, EntityType> {

    public ShootingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, ProjectileLaunchEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<EntityType> getFormatter() {
        return WorkFormatters.ENITITY_TYPE;
    }

    @Override
    public boolean handle(@NotNull ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();
        if (!(source instanceof Player player)) return false;

        this.doObjective(player, projectile.getType(), 1);
        return true;
    }
}
