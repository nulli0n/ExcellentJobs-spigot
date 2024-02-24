package su.nightexpress.excellentjobs.stats;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.command.base.TopCommand;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsManager extends AbstractManager<JobsPlugin> {

    private final Map<String, List<TopEntry>> levelTopMap;

    public StatsManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.levelTopMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {

        this.plugin.getBaseCommand().addChildren(new TopCommand(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::updateJobLevelsAndEmployees).setSecondsInterval(Config.STATISTIC_UPDATE_INTERVAL.get()));
    }

    @Override
    protected void onShutdown() {

    }

    @NotNull
    public Map<String, List<TopEntry>> getLevelTopMap() {
        return levelTopMap;
    }

    @NotNull
    public List<TopEntry> getLevelTopEntries(@NotNull Job job) {
        return this.getLevelTopEntries(job.getId());
    }

    @NotNull
    public List<TopEntry> getLevelTopEntries(@NotNull String id) {
        return this.getLevelTopMap().getOrDefault(id.toLowerCase(), Collections.emptyList());
    }

    public void updateJobLevelsAndEmployees() {
        this.updateEmployeesAmount();
        this.updateTopLevelLeaderboard();
    }

    public void updateEmployeesAmount() {
        Map<Job, Map<JobState, Integer>> dataMap = this.plugin.getData().getEmployees();

        dataMap.forEach((job, map) -> {
            map.forEach(job::setEmployeesAmount);
        });
    }

    public void updateTopLevelLeaderboard() {
        Map<String, List<TopEntry>> levelMap = this.getLevelTopMap();
        Map<Job, Map<String, Integer>> dataMap = this.plugin.getData().getLevels();

        levelMap.clear();

        dataMap.forEach((job, userLevelMap) -> {
            AtomicInteger count = new AtomicInteger();
            Lists.sortDescent(userLevelMap).forEach((name, level) -> {
                levelMap.computeIfAbsent(job.getId(), k -> new ArrayList<>()).add(new TopEntry(name, level, count.incrementAndGet()));
            });
        });
    }
}
