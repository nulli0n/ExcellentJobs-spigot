package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

@SuppressWarnings("UnstableApiUsage")
public class TakeDamageWork extends Work<EntityDamageEvent, DamageType> {

    public TakeDamageWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EntityDamageEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<DamageType> getFormatter() {
        return WorkFormatters.DAMAGE_TYPE;
    }

    @Override
    public boolean handle(@NotNull EntityDamageEvent event) {
        Entity victim = event.getEntity();
        if (!(victim instanceof Player player)) return false;

        DamageType cause = event.getDamageSource().getDamageType();
        double damage = event.getDamage();
        this.doObjective(player, cause, (int) damage);
        return true;
    }
}
