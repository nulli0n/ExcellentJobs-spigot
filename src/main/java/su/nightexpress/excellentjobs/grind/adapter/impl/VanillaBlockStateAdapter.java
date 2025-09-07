package su.nightexpress.excellentjobs.grind.adapter.impl;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;
import su.nightexpress.nightcore.util.BukkitThing;

public class VanillaBlockStateAdapter extends AbstractGrindAdapter<Material, BlockState> {

    public VanillaBlockStateAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull BlockState block) {
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
    public Material getType(@NotNull BlockState blockState) {
        return blockState.getType();
    }

    @Override
    @NotNull
    public String getName(@NotNull Material material) {
        return BukkitThing.getAsString(material);
    }
}
