package su.nightexpress.excellentjobs.zone.impl;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.BukkitThing;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BlockList implements Writeable {

    private Material fallbackMaterial;
    private int      resetTime;
    private boolean  dropItems;

    private final String        id;
    private final Set<Material> materials;

    public BlockList(@NotNull String id,
                     @NotNull Set<Material> materials,
                     @NotNull Material fallbackMaterial,
                     int resetTime,
                     boolean dropItems) {
        this.id = id.toLowerCase();
        this.materials = new HashSet<>(materials);
        this.setFallbackMaterial(fallbackMaterial);
        this.setResetTime(resetTime);
        this.setDropItems(dropItems);
    }

    @NotNull
    public static BlockList read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        Set<Material> materials = config.getStringSet(path + ".Materials").stream()
            .map(BukkitThing::getMaterial).filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Material fallback = BukkitThing.getMaterial(config.getString(path + ".Fallback_Material", BukkitThing.toString(Material.STONE)));
        if (fallback == null) fallback = Material.STONE;

        int resetTime = config.getInt(path + ".Reset_Time");
        boolean dropItems = config.getBoolean(path + ".Drop_Items");

        return new BlockList(id, materials, fallback, resetTime, dropItems);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Materials", this.materials.stream().map(BukkitThing::toString).toList());
        config.set(path + ".Fallback_Material", BukkitThing.toString(this.fallbackMaterial));
        config.set(path + ".Reset_Time", this.resetTime);
        config.set(path + ".Drop_Items", this.dropItems);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.ZONE_BLOCK_LIST.replacer(this);
    }

    public boolean contains(@NotNull Material material) {
        return this.materials.contains(material);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Set<Material> getMaterials() {
        return this.materials;
    }

    @NotNull
    public Material getFallbackMaterial() {
        return this.fallbackMaterial;
    }

    public void setFallbackMaterial(@NotNull Material fallbackMaterial) {
        this.fallbackMaterial = fallbackMaterial;
    }

    public int getResetTime() {
        return this.resetTime;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public boolean isDropItems() {
        return this.dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }
}
