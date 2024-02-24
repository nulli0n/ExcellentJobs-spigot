package su.nightexpress.excellentjobs;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;

import java.util.Collection;

public class JobsAPI {

    public static final JobsPlugin PLUGIN = JobsPlugin.getPlugin(JobsPlugin.class);

    @NotNull
    public static JobUser getUserData(@NotNull Player player) {
        return PLUGIN.getUserManager().getUserData(player);
    }

    @Nullable
    public static Currency getCurrency(@NotNull String id) {
        return PLUGIN.getCurrencyManager().getCurrency(id);
    }

    @NotNull
    public static JobManager getJobManager() {
        return PLUGIN.getJobManager();
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
