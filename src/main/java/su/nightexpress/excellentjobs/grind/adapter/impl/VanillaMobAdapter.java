package su.nightexpress.excellentjobs.grind.adapter.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

public class VanillaMobAdapter extends AbstractGrindAdapter<EntityType, Entity> {

    public VanillaMobAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull Entity entity) {
        return true;
    }

    @Override
    @Nullable
    public EntityType getTypeByName(@NotNull String name) {
        return BukkitThing.getEntityType(name);
    }

    @Override
    @NotNull
    public EntityType getType(@NotNull Entity entity) {
        return entity.getType();
    }

    @Override
    @NotNull
    public String getName(@NotNull EntityType type) {
        return BukkitThing.getAsString(type);
    }
}
