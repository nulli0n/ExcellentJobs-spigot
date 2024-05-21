package su.nightexpress.excellentjobs;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.currency.CurrencyManager;
import su.nightexpress.excellentjobs.data.UserManager;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.zone.ZoneManager;

import java.util.Collection;

public class JobsAPI {

    public static final JobsPlugin PLUGIN = JobsPlugin.getPlugin(JobsPlugin.class);

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return PLUGIN.getCurrencyManager();
    }

    @NotNull
    public static JobManager getJobManager() {
        return PLUGIN.getJobManager();
    }

    @Nullable
    public static ZoneManager getZoneManager() {
        return PLUGIN.getZoneManager();
    }

    @NotNull
    public static UserManager getUserManager() {
        return PLUGIN.getUserManager();
    }

    @NotNull
    public static JobUser getUserData(@NotNull Player player) {
        return PLUGIN.getUserManager().getUserData(player);
    }

    @Nullable
    public static Currency getCurrency(@NotNull String id) {
        return PLUGIN.getCurrencyManager().getCurrency(id);
    }

    @Nullable
    public static Job getJobById(@NotNull String id) {
        return PLUGIN.getJobManager().getJobById(id);
    }

    @NotNull
    public static Collection<Job> getJobs() {
        return PLUGIN.getJobManager().getJobs();
    }
}
