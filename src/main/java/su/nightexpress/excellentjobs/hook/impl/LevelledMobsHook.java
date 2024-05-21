package su.nightexpress.excellentjobs.hook.impl;

import me.lokka30.levelledmobs.LevelledMobs;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class LevelledMobsHook {

    public static int getLevel(@NotNull LivingEntity entity) {
        return LevelledMobs.getInstance().levelInterface.getLevelOfMob(entity);
    }
}
