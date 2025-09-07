package su.nightexpress.excellentjobs.grind.adapter.impl;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

public class MythicMobAdapter extends AbstractGrindAdapter<MythicMob, Entity> {

    public MythicMobAdapter(@NotNull String name) {
        super(name);
    }

    private static MythicBukkit getAPI() {
        return MythicBukkit.inst();
    }

    @Override
    public boolean canHandle(@NotNull Entity entity) {
        return getAPI().getMobManager().isMythicMob(entity);
    }

    @Override
    @Nullable
    public MythicMob getTypeByName(@NotNull String name) {
        return getAPI().getMobManager().getMythicMob(name).orElse(null);
    }

    @Override
    @Nullable
    public MythicMob getType(@NotNull Entity entity) {
        ActiveMob activeMob = getAPI().getMobManager().getMythicMobInstance(entity);
        if (activeMob == null) return null;

        return activeMob.getType();
    }

    @Override
    @NotNull
    public String getName(@NotNull MythicMob type) {
        return type.getInternalName();
    }

    @Override
    @NotNull
    public String toFullNameOfType(@NotNull MythicMob mythicMob) {
        return "mythicmobs:" + super.toFullNameOfType(mythicMob);
    }
}
