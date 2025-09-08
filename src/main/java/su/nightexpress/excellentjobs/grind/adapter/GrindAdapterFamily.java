package su.nightexpress.excellentjobs.grind.adapter;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GrindAdapterFamily<O> {

    public static final GrindAdapterFamily<Entity>      ENTITY      = new GrindAdapterFamily<>();
    public static final GrindAdapterFamily<Block>       BLOCK       = new GrindAdapterFamily<>();
    public static final GrindAdapterFamily<BlockState>  BLOCK_STATE = new GrindAdapterFamily<>();
    public static final GrindAdapterFamily<ItemStack>   ITEM        = new GrindAdapterFamily<>();
    public static final GrindAdapterFamily<Enchantment> ENCHANTMENT = new GrindAdapterFamily<>();

    private final Set<GrindAdapter<?, O>> adapterByKey;

    public GrindAdapterFamily() {
        this.adapterByKey = new HashSet<>();
    }

    public <I, E extends GrindAdapter<I, O>> void addAdapter(@NotNull E adapter) {
        this.adapterByKey.add(adapter);
    }

    @NotNull
    public Set<GrindAdapter<?, O>> getAdapters() {
        return this.adapterByKey;
    }

    @NotNull
    public Set<GrindAdapter<?, O>> getAdaptersFor(@NotNull O entity) {
        return this.adapterByKey.stream().filter(adapter -> adapter.canHandle(entity)).collect(Collectors.toSet());
    }

    @Nullable
    public GrindAdapter<?, O> getAdapterFor(@NotNull O entity) {
        return this.getAdaptersFor(entity).stream().findFirst().orElse(null);
    }
}
