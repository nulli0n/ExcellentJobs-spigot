package su.nightexpress.excellentjobs.grind.adapter.impl;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

public class CustomFishingAdapter extends AbstractGrindAdapter<Loot, ItemStack> {

    public CustomFishingAdapter(@NotNull String name) {
        super(name);
    }

    @NotNull
    private static BukkitCustomFishingPlugin getAPI() {
        return BukkitCustomFishingPlugin.getInstance();
    }

    @Override
    public boolean canHandle(@NotNull ItemStack itemStack) {
        return getAPI().getItemManager().getCustomFishingItemID(itemStack) != null;
    }

    @Override
    @Nullable
    public Loot getTypeByName(@NotNull String name) {
        return getAPI().getLootManager().getLoot(name).orElse(null);
    }

    @Override
    @Nullable
    public Loot getType(@NotNull ItemStack itemStack) {
        String id = getAPI().getItemManager().getCustomFishingItemID(itemStack);
        if (id == null) return null;

        return getTypeByName(id);
    }

    @Override
    @NotNull
    public String getName(@NotNull Loot loot) {
        return loot.id();
    }

    @Override
    @NotNull
    public String toFullNameOfType(@NotNull Loot loot) {
        return "customfishing:" + super.toFullNameOfType(loot);
    }
}
