package su.nightexpress.excellentjobs;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.booster.BoosterManager;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.user.UserManager;
import su.nightexpress.excellentjobs.zone.ZoneManager;

import java.util.Set;

public class JobsAPI {

    public static JobsPlugin instance;

    static void load(@NotNull JobsPlugin plugin) {
        instance = plugin;
    }

    static void clear() {
        instance = null;
    }

    @NotNull
    public static JobsPlugin getPlugin() {
        return instance;
    }

    @NotNull
    public static JobManager getJobManager() {
        return instance.getJobManager();
    }

    @Nullable
    public static ZoneManager getZoneManager() {
        return instance.getZoneManager();
    }

    @Nullable
    public static BoosterManager getBoosterManager() {
        return instance.getBoosterManager();
    }

    @NotNull
    public static UserManager getUserManager() {
        return instance.getUserManager();
    }

    @NotNull
    public static JobUser getUserData(@NotNull Player player) {
        return getUserManager().getOrFetch(player);
    }

    @Nullable
    public static Job getJobById(@NotNull String id) {
        return getJobManager().getJobById(id);
    }

    @NotNull
    public static Set<Job> getJobs() {
        return getJobManager().getJobs();
    }

    public static double getBoost(@NotNull Player player, @NotNull Job job, @NotNull MultiplierType type) {
        BoosterManager manager = getBoosterManager();
        return manager == null ? 0D : manager.getTotalBoost(player, job, type);
    }

    public static double getBoostPercent(@NotNull Player player, @NotNull Job job, @NotNull MultiplierType type) {
        BoosterManager manager = getBoosterManager();
        return manager == null ? 0D : manager.getTotalBoostPercent(player, job, type);
    }
}
