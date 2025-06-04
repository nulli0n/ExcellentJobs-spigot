package su.nightexpress.excellentjobs.hook.work;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.hook.impl.MythicMobsHook;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;

public class MythicMobsWork extends Work<MythicMobDeathEvent, MythicMob> implements WorkFormatter<MythicMob> {

    public MythicMobsWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, MythicMobDeathEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<MythicMob> getFormatter() {
        return this;
    }

    @Override
    public boolean handle(@NotNull MythicMobDeathEvent event) {
        LivingEntity killer = event.getKiller();
        if (!(killer instanceof Player player)) return false;
        if (JobManager.isDevastated(event.getEntity())) return false;

        this.doObjective(player, event.getMobType(), 1);
        return true;
    }

    @NotNull
    @Override
    public String getName(@NotNull MythicMob object) {
        return object.getInternalName();
    }

    @NotNull
    @Override
    public String getLocalized(@NotNull MythicMob object) {
        return object.getDisplayName() == null ? object.getEntityTypeString() : object.getDisplayName().get();
    }

    @Nullable
    @Override
    public MythicMob parseObject(@NotNull String name) {
        return MythicMobsHook.getMobConfig(name);
    }
}
