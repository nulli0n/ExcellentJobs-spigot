package su.nightexpress.excellentjobs.stats;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.command.base.TopCommand;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.stats.impl.DayStats;
import su.nightexpress.excellentjobs.stats.menu.StatsMenu;
import su.nightexpress.excellentjobs.stats.impl.JobStats;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.excellentjobs.stats.listener.StatsListener;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class StatsManager extends AbstractManager<JobsPlugin> {

    private final Map<String, List<TopEntry>> levelTopMap;

    private StatsMenu statsMenu;

    public StatsManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.levelTopMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.getBaseCommand().addChildren(new TopCommand(this.plugin, this));

        this.loadUI();
        this.loadStats();

        this.addListener(new StatsListener(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::updateJobLevelsAndEmployees).setSecondsInterval(Config.STATISTIC_UPDATE_INTERVAL.get()));
    }

    @Override
    protected void onShutdown() {

    }

    private void loadUI() {
        this.statsMenu = new StatsMenu(this.plugin);
    }

    private void loadStats() {
        this.plugin.runTask(task -> this.plugin.getServer().getOnlinePlayers().forEach(this::loadStats));
    }

    private void loadStats(@NotNull Player player) {
        Map<String, JobStats> stats = this.plugin.getData().getStats(player.getUniqueId());
        JobUser user = this.plugin.getUserManager().getUserData(player);
        user.loadStats(stats);
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
        return this.levelTopMap.getOrDefault(id.toLowerCase(), Collections.emptyList());
    }

    public void handleJoin(@NotNull Player player) {
        this.plugin.runTaskAsync(task -> this.loadStats(player));
    }

    public void openStats(@NotNull Player player, @NotNull Job job) {
        this.statsMenu.open(player, job);
    }

    public void addStats(@NotNull Player player, @NotNull Job job, @NotNull Consumer<DayStats> consumer) {
        JobUser user = plugin.getUserManager().getUserData(player);
        JobStats jobStats = user.getStats(job);
        DayStats stats = jobStats.getTodayStats();

        consumer.accept(stats);
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
        Map<Job, Map<String, Integer>> dataMap = this.plugin.getData().getLevels();

        this.levelTopMap.clear();

        dataMap.forEach((job, userLevelMap) -> {
            AtomicInteger count = new AtomicInteger();
            Lists.sortDescent(userLevelMap).forEach((name, level) -> {
                this.levelTopMap.computeIfAbsent(job.getId(), k -> new ArrayList<>()).add(new TopEntry(name, level, count.incrementAndGet()));
            });
        });
    }
}
