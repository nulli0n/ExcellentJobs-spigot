package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.BasicEntityGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.BasicEntityGrindType;

public class MilkingGrindListener extends GrindListener<BasicEntityGrindTable, BasicEntityGrindType> {

    public MilkingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull BasicEntityGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMilk(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (this.grindManager.canGrinding(player)) return;

        if (event.getItemStack() == null) return;
        if (event.getItemStack().getType() != Material.MILK_BUCKET) return;

        Location eyes = player.getEyeLocation();
        Location location = player.getLocation();
        RayTraceResult result = player.getWorld().rayTraceEntities(eyes, location.getDirection(), 5D, entity -> !(entity instanceof Player));
        if (result == null) return;

        Entity entity = result.getHitEntity();
        if (entity == null) return;

        this.giveXP(player, (job, table) -> table.getMobReward(entity));
    }
}
