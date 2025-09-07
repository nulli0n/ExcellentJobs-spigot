package su.nightexpress.excellentjobs.grind.adapter.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

public class VanillaBlockAdapter extends AbstractGrindAdapter<Material, Block> {

    public VanillaBlockAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull Block block) {
        return true;
    }

    @Override
    @Nullable
    public Material getTypeByName(@NotNull String name) {
        Material material = BukkitThing.getMaterial(name);
        return material != null && material.isBlock() ? material : null;
    }

    @Override
    @Nullable
    public Material getType(@NotNull Block block) {
        return block.getType();
    }

    @Override
    @NotNull
    public String getName(@NotNull Material material) {
        return BukkitThing.getAsString(material);
    }
}
