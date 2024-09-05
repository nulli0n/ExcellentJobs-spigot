package su.nightexpress.excellentjobs.zone.visual;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.util.Cuboid;
import su.nightexpress.excellentjobs.util.pos.BlockPos;
import su.nightexpress.excellentjobs.zone.impl.Selection;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.LocationUtil;

import java.util.*;

public abstract class BlockHighlighter {

    protected final JobsPlugin plugin;
    private final Map<UUID, List<Integer>> entityIdMap;

    public BlockHighlighter(@NotNull JobsPlugin plugin) {
        this.plugin = plugin;
        this.entityIdMap = new HashMap<>();
    }

    protected int nextEntityId() {
        return EntityUtil.nextEntityId();
    }

    public void highlightPoints(@NotNull Player player, @NotNull Selection selection) {
        highlightPoints(player, selection.getFirst(), selection.getSecond());
    }

    public void highlightPoints(@NotNull Player player, @NotNull Cuboid cuboid) {
        highlightPoints(player, cuboid.getMin(), cuboid.getMax());
    }

    private void highlightPoints(@NotNull Player player, @Nullable BlockPos min, @Nullable BlockPos max) {
        this.removeVisuals(player);

        World world = player.getWorld();

        for (BlockPos blockPos : new BlockPos[]{min, max}) {
            if (blockPos == null || blockPos.isEmpty()) continue;

            Location location = blockPos.toLocation(world);
            this.addVisualBlock(player, location);
        }
    }

    public void removeVisuals(@NotNull Player player) {
        List<Integer> list = this.entityIdMap.remove(player.getUniqueId());
        if (list == null) return;

        this.destroyEntity(player, list);
    }

    public void addVisualBlock(@NotNull Player player, @NotNull Location location) {
        List<Integer> idList = this.entityIdMap.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

        Location center = LocationUtil.setCenter3D(location);
        int entityID = this.nextEntityId();

        this.spawnVisualBlock(entityID, player, center);
        idList.add(entityID);
    }

    protected abstract void spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location);

    protected abstract void destroyEntity(@NotNull Player player, @NotNull List<Integer> idList);
}
