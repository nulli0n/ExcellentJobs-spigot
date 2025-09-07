package su.nightexpress.excellentjobs.grind.adapter.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

public class VanillaEnchantmentAdapter extends AbstractGrindAdapter<NamespacedKey, Enchantment> {

    public VanillaEnchantmentAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull Enchantment enchantment) {
        return true;
    }

    @Override
    @Nullable
    public NamespacedKey getTypeByName(@NotNull String name) {
        return BukkitThing.parseKey(name);
    }

    @Override
    @Nullable
    public NamespacedKey getType(@NotNull Enchantment enchantment) {
        return enchantment.getKey();
    }

    @Override
    @NotNull
    public String getName(@NotNull NamespacedKey key) {
        return BukkitThing.getAsString(key);
    }
}
