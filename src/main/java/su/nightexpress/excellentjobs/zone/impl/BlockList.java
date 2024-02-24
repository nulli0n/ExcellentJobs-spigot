package su.nightexpress.excellentjobs.zone.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.util.BlockPos;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.Pair;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.util.*;
import java.util.stream.Collectors;

public class BlockList implements Placeholder {

    private Material fallbackMaterial;
    private int resetTime;
    private boolean dropItems;

    private final String id;
    private final Set<Material>                        materials;
    private final Map<BlockPos, Pair<BlockData, Long>> blocksToReset;
    private final PlaceholderMap placeholderMap;

    public BlockList(@NotNull String id,
                     @NotNull Set<Material> materials,
                     @NotNull Material fallbackMaterial, int resetTime, boolean dropItems) {
        this.id = id.toLowerCase();
        this.materials = new HashSet<>(materials);
        this.setFallbackMaterial(fallbackMaterial);
        this.setResetTime(resetTime);
        this.setDropItems(dropItems);

        this.blocksToReset = new HashMap<>();
        this.placeholderMap = Placeholders.forBlockList(this);
    }

    @NotNull
    public static BlockList read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        Set<Material> materials = config.getStringSet(path + ".Materials").stream()
            .map(BukkitThing::getMaterial).filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Material fallback = BukkitThing.getMaterial(config.getString(path + ".Fallback_Material", Material.STONE.name()));
        if (fallback == null) fallback = Material.STONE;

        int resetTime = config.getInt(path + ".Reset_Time");
        boolean dropItems = config.getBoolean(path + ".Drop_Items");

        return new BlockList(id, materials, fallback, resetTime, dropItems);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Materials", this.getMaterials().stream().map(material -> material.getKey().getKey()).toList());
        config.set(path + ".Fallback_Material", this.getFallbackMaterial().getKey().getKey());
        config.set(path + ".Reset_Time", this.getResetTime());
        config.set(path + ".Drop_Items", this.isDropItems());
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public boolean contains(@NotNull Material material) {
        return this.getMaterials().contains(material);
    }

    public void onBlockBreak(@NotNull Block block) {
        BlockData blockData = block.getBlockData();
        BlockPos pos = BlockPos.from(block.getLocation());
        long resetDate = System.currentTimeMillis() + this.getResetTime() * 1000L;

        this.getBlocksToReset().put(pos, Pair.of(blockData, resetDate));
    }

    public void regenerateBlocks(@NotNull World world) {
        this.regenerateBlocks(world, false);
    }

    public void regenerateBlocks(@NotNull World world, boolean force) {
        //AtomicInteger count = new AtomicInteger();

        this.getBlocksToReset().entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            var pair = entry.getValue();

            if (!force) {
                if (System.currentTimeMillis() < pair.getSecond()) return false;
                if (!pos.isChunkLoaded(world)) return false;
            }

            BlockData blockData = pair.getFirst();
            Location location = pos.toLocation(world);
            world.setBlockData(location, blockData);
            UniParticle.of(Particle.BLOCK_CRACK, blockData).play(LocationUtil.getCenter(location), 0.35, 0.05, 60);

            return true;
        });
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Set<Material> getMaterials() {
        return materials;
    }

    @NotNull
    public Material getFallbackMaterial() {
        return fallbackMaterial;
    }

    public void setFallbackMaterial(@NotNull Material fallbackMaterial) {
        this.fallbackMaterial = fallbackMaterial;
    }

    public int getResetTime() {
        return resetTime;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    @NotNull
    public Map<BlockPos, Pair<BlockData, Long>> getBlocksToReset() {
        return blocksToReset;
    }
}
