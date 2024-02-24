package su.nightexpress.excellentjobs.booster.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Placeholders;

import java.util.HashSet;
import java.util.Set;

public class BoosterInfo {

    private final Set<String>       jobs;
    private final BoosterMultiplier multiplier;

    public BoosterInfo(@NotNull Set<String> jobs, @NotNull BoosterMultiplier multiplier) {
        this.jobs = new HashSet<>(jobs);
        this.multiplier = multiplier;
    }

    public void validate() {
        if (this.getJobs().contains(Placeholders.WILDCARD)) {
            this.getJobs().clear();
            this.getJobs().addAll(JobsAPI.getJobManager().getJobMap().keySet());
        }
    }

    @NotNull
    public static BoosterInfo read(@NotNull FileConfig cfg, @NotNull String path) {
        Set<String> jobs = cfg.getStringSet(path + ".Jobs");
        BoosterMultiplier multiplier = BoosterMultiplier.read(cfg, path);

        return new BoosterInfo(jobs, multiplier);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Jobs", this.getJobs());
        this.getMultiplier().write(cfg, path);
    }

    @NotNull
    public Booster createBooster() {
        return new Booster(this);
    }

    public boolean isApplicable(@NotNull Job job) {
        return this.isApplicable(job.getId());
    }

    public boolean isApplicable(@NotNull String skillId) {
        return this.getJobs().contains(skillId);
    }

    @NotNull
    public Set<String> getJobs() {
        return jobs;
    }

    @NotNull
    public BoosterMultiplier getMultiplier() {
        return multiplier;
    }
}
