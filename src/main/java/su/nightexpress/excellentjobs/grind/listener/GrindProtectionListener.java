package su.nightexpress.excellentjobs.grind.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

import java.util.Set;

public class GrindProtectionListener extends AbstractListener<JobsPlugin> {

    private final GrindManager manager;

    public GrindProtectionListener(@NotNull JobsPlugin plugin, @NotNull GrindManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrindProtectionEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if (Config.ABUSE_IGNORE_SPAWN_REASONS.get().contains(reason)) {
            this.manager.markSpawnerMob(entity, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGlitchEntityTransform(EntityTransformEvent event) {
        if (this.manager.isSpawnerMob(event.getEntity())) {
            event.getTransformedEntities().forEach(entity -> this.manager.markSpawnerMob(entity, true));
            this.manager.markSpawnerMob(event.getTransformedEntity(), true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGlitchBlockFertilize(BlockFertilizeEvent event) {
        Block block = event.getBlock();
        Set<String> badBlocks = Config.ABUSE_IGNORE_FERTILIZED.get();

        boolean isBadBlock = badBlocks.contains(Placeholders.WILDCARD) || badBlocks.contains(BukkitThing.getValue(block.getType()));

        event.getBlocks().forEach(blockState -> {
            if (isBadBlock) {
                PlayerBlockTracker.trackForce(blockState.getBlock());
            }
            else {
                PlayerBlockTracker.unTrack(blockState.getBlock());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGlitchBlockGeneration(BlockFormEvent event) {
        if (Config.ABUSE_IGNORE_BLOCK_GENERATION.get().contains(event.getNewState().getType())) {
            this.plugin.runTask(() -> PlayerBlockTracker.trackForce(event.getBlock()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // An extra marker that a block was placed by a player for the #isPlayerBlock method,
        // that will handle both, BlockBreak and BlockDrop phases.
        if (PlayerBlockTracker.isTracked(block) && event.isDropItems()) {
            this.manager.markPlayerBlock(block, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDrop(BlockDropItemEvent event) {
        Block block = event.getBlock();
        this.manager.markPlayerBlock(block, false);
    }
}
