package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class MilkingWork extends Work<PlayerBucketFillEvent, EntityType> {

    public MilkingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerBucketFillEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<EntityType> getFormatter() {
        return WorkFormatters.ENITITY_TYPE;
    }

    @Override
    public boolean handle(@NotNull PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (event.getItemStack() == null) return false;
        if (event.getItemStack().getType() != Material.MILK_BUCKET) return false;

        Location eyes = player.getEyeLocation();
        Location location = player.getLocation();
        RayTraceResult result = player.getWorld().rayTraceEntities(eyes, location.getDirection(), 5D, entity -> !(entity instanceof Player));
        if (result == null) return false;

        Entity entity = result.getHitEntity();
        if (entity == null) return false;

        this.doObjective(player, entity.getType(), 1);
        return true;
    }
}
