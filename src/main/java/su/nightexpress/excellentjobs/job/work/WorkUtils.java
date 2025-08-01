package su.nightexpress.excellentjobs.job.work;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.config.Config;

public class WorkUtils {

    // TODO Support for pet plugins?

    public static boolean isAbusingPetKilling(@NotNull Player player, @NotNull LivingEntity killedMob) {
        if (!Config.ABUSE_RESTRICT_PET_KILLS.get()) return false;

        EntityDamageEvent damageEvent = killedMob.getLastDamageCause();
        if (damageEvent == null) return false;

        DamageSource source = damageEvent.getDamageSource();
        Entity damager = source.getCausingEntity();
        if (damager == null || damager == player) return false;

        return damager instanceof Tameable tameable && tameable.getOwner() == player;
    }
}
