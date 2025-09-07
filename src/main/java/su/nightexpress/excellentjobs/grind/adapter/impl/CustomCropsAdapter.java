package su.nightexpress.excellentjobs.grind.adapter.impl;

import net.momirealms.customcrops.api.BukkitCustomCropsAPI;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

import java.util.Optional;

public class CustomCropsAdapter extends AbstractGrindAdapter<CustomCropsBlock, Block> {

    public CustomCropsAdapter(@NotNull String name) {
        super(name);
    }

    @NotNull
    private static CustomCropsAPI getAPI() {
        return BukkitCustomCropsAPI.get();
    }

    @NotNull
    private static Optional<CustomCropsBlockState> getCropState(@NotNull Block block) {
        World world = block.getWorld();
        Pos3 pos = getAPI().adapt(block.getLocation());

        CustomCropsWorld<?> cropsWorld = getAPI().getCustomCropsWorld(world);
        if (cropsWorld == null) return Optional.empty();

        return cropsWorld.getBlockState(pos);
    }

    @Override
    public boolean canHandle(@NotNull Block block) {
        return getCropState(block).isPresent();
    }

    @Override
    @Nullable
    public CustomCropsBlock getTypeByName(@NotNull String name) {
        return Registries.BLOCKS.get(name);
    }

    @Override
    @Nullable
    public CustomCropsBlock getType(@NotNull Block block) {
        return getCropState(block).map(CustomCropsBlockState::type).orElse(null);
    }

    @Override
    @NotNull
    public String getName(@NotNull CustomCropsBlock customCropsBlock) {
        return customCropsBlock.type().asString();
    }

    @Override
    @NotNull
    public String toFullNameOfType(@NotNull CustomCropsBlock customCropsBlock) {
        return "customcrops:" + super.toFullNameOfType(customCropsBlock);
    }
}
