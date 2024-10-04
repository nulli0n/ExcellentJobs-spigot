package su.nightexpress.excellentjobs.zone.visual;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.nightcore.util.EntityUtil;

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

    public void removeVisuals(@NotNull Player player) {
        List<Integer> list = this.entityIdMap.remove(player.getUniqueId());
        if (list == null) return;

        this.destroyEntity(player, list);
    }

    public void addVisualBlock(@NotNull Player player, @NotNull Location location, @NotNull BlockData blockData, @NotNull ChatColor color, float size) {
        //List<FakeEntity> entities = this.getEntityMap(player.getUniqueId()).computeIfAbsent(type, k -> new ArrayList<>());
        List<Integer> idList = this.entityIdMap.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

        // To shift scaled down/up displays to the center of a block location.
        float offset = 1f - size;
        // Half-sized (0.5f) block displays got shifted on 1/2 of the size difference, so 0.5f modifier comes from here.
        float shift = 0.5f * offset;

        location.setX(location.getBlockX() + shift);
        location.setY(location.getBlockY() + shift);
        location.setZ(location.getBlockZ() + shift);

        int entityID = this.nextEntityId();

        /*FakeEntity entity = */this.spawnVisualBlock(entityID, player, location, blockData, color, size);
        //entities.add(entity);

        idList.add(entityID);
    }

    protected abstract void spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location, @NotNull BlockData blockData, @NotNull ChatColor color, float size);

    protected abstract void destroyEntity(@NotNull Player player, @NotNull List<Integer> idList);
}
