package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.grind.GrindContext;
import su.nightexpress.excellentjobs.api.grind.GrindProtection;
import su.nightexpress.excellentjobs.api.grind.GrindType;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;

@NullMarked
public class MobLootGrindListener extends GrindListener<ItemStack> {

    public MobLootGrindListener(JobsPlugin plugin,
                                GrindManager manager,
                                @Nullable GrindProtection protection,
                                GrindType<ItemStack> type) {
        super(plugin, manager, protection, type);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDrop(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        Player player = entity.getKiller();
        if (player == null) return;

        if (!this.checkProtection(p -> p.isGrindAllowed(player) && !p.isArtificalMob(entity))) return;

        event.getDrops().forEach(itemStack -> {
            this.giveXP(player, itemStack, GrindContext.create());
        });
    }
}
