package su.nightexpress.excellentjobs.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.UserSettings;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.stats.impl.JobStats;
import su.nightexpress.nightcore.db.AbstractUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JobUser extends AbstractUser {

    private final Map<String, JobData>  dataMap;
    private final Map<String, Booster>  boosterMap;
    private final Map<String, JobStats> statsMap;

    private final UserSettings          settings;

    @NotNull
    public static JobUser create(@NotNull UUID uuid, @NotNull String name) {
        long creationDate = System.currentTimeMillis();

        Map<String, JobData> dataMap = new HashMap<>();
        Map<String, Booster> boosterMap = new HashMap<>();
        Map<String, JobStats> statsMap = new HashMap<>();
        UserSettings settings = new UserSettings();

        return new JobUser(uuid, name, creationDate, creationDate, dataMap, boosterMap, statsMap, settings);
    }

    public JobUser(@NotNull UUID uuid,
                   @NotNull String name,
                   long lastOnline,
                   long dateCreated,
                   @NotNull Map<String, JobData> dataMap,
                   @NotNull Map<String, Booster> boosterMap,
                   @NotNull Map<String, JobStats> statsMap,
                   @NotNull UserSettings settings) {
        super(uuid, name, dateCreated, lastOnline);
        this.dataMap = new HashMap<>(dataMap);
        this.boosterMap = new ConcurrentHashMap<>(boosterMap);
        this.statsMap = new HashMap<>(statsMap);
        this.settings = settings;
    }

    @NotNull
    public UserSettings getSettings() {
        return this.settings;
    }

    public void loadStats(@NotNull Map<String, JobStats> statsMap) {
        this.statsMap.putAll(statsMap);
    }

    public int countTotalEffectiveLevel() {
        return this.getDatas().stream().mapToInt(data -> data.isActive() ? data.getLevel() : 0).sum();
    }

    public int countTotalLevel() {
        return this.getDatas().stream().mapToInt(JobData::getLevel).sum();
    }

    public int countActiveJobs() {
        return (int) this.getDatas().stream().filter(JobData::isActive).count();
    }

    public int countJobs(@NotNull JobState state) {
        return (int) this.getDatas().stream().filter(jobData -> jobData.getState() == state).count();
    }

    public int countSpecialOrders() {
        return (int) this.getDatas().stream().filter(JobData::hasOrder).count();
    }

    public int countActiveSpecialOrders() {
        return (int) this.getDatas().stream().filter(data -> data.hasOrder() && !data.isOrderCompleted() && !data.getOrderData().isExpired()).count();
    }

    @NotNull
    public Map<String, JobData> getDataMap() {
        return this.dataMap;
    }

    @NotNull
    public JobData getData(@NotNull Job job) {
        return this.getDataMap().computeIfAbsent(job.getId(), k -> JobData.create(job));
    }

    @NotNull
    public Collection<JobData> getDatas() {
        return this.getDataMap().values();
    }

    @NotNull
    public Map<String, Booster> getBoosterMap() {
        return this.boosterMap;
    }

    @Nullable
    public Booster getBooster(@NotNull Job job) {
        return this.getBooster(job.getId());
    }

    @Nullable
    public Booster getBooster(@NotNull String jobId) {
        return this.boosterMap.get(jobId.toLowerCase());
    }

    public boolean hasBooster(@NotNull Job job) {
        return this.hasBooster(job.getId());
    }

    public boolean hasBooster(@NotNull String jobId) {
        return this.getBooster(jobId) != null;
    }

    public void addBooster(@NotNull Job job, @NotNull Booster booster) {
        this.addBooster(job.getId(), booster);
    }

    public void addBooster(@NotNull String jobId, @NotNull Booster booster) {
        this.boosterMap.put(jobId.toLowerCase(), booster);
    }

    public void removeBooster(@NotNull Job job) {
        this.removeBooster(job.getId());
    }

    public void removeBooster(@NotNull String jobId) {
        this.boosterMap.remove(jobId.toLowerCase());
    }

    @NotNull
    public Map<String, JobStats> getStatsMap() {
        return this.statsMap;
    }

    @NotNull
    public JobStats getStats(@NotNull Job job) {
        return this.getStats(job.getId());
    }

    @NotNull
    public JobStats getStats(@NotNull String jobId) {
        return this.statsMap.computeIfAbsent(jobId.toLowerCase(), k -> new JobStats());
    }
}
