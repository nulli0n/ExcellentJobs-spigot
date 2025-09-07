package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.KillingGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.KillingGrindType;
import su.nightexpress.excellentjobs.job.JobManager;

public class KillingGrindListener extends GrindListener<KillingGrindTable, KillingGrindType> {

    public KillingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull KillingGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player == null || !this.grindManager.canGrinding(player)) return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean isSpawner = this.grindManager.isSpawnerMob(entity);

        if (JobManager.isAbusingPetKilling(player, entity)) return;
        /*

        // LevelledMobs integration.
        if (Config.LEVELLED_MOBS_KILL_ENTITY_ENABLED.get()) {
            int level = JobUtils.getMobLevel(entity);
            double amount = Config.LEVELLED_MOBS_KILL_ENTITY_MULTIPLIER.get();
            multiplier = level * amount;
        }*/

        this.giveXP(player, tool, (skill, table) -> table.getKillXP(entity, tool, isSpawner));
    }
}
