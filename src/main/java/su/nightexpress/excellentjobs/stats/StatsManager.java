package su.nightexpress.excellentjobs.stats;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.stats.command.StatsCommands;
import su.nightexpress.excellentjobs.stats.impl.DayStats;
import su.nightexpress.excellentjobs.stats.impl.JobStats;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.excellentjobs.stats.listener.StatsListener;
import su.nightexpress.excellentjobs.stats.menu.StatsMenu;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.manager.AbstractManager;

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
        StatsCommands.load(this.plugin, this);

        this.loadUI();
        this.loadStats();

        this.addListener(new StatsListener(this.plugin, this));

        this.addAsyncTask(this::updateJobLevelsAndEmployees, Config.STATISTIC_UPDATE_INTERVAL.get());
    }

    @Override
    protected void onShutdown() {
        StatsCommands.unload(this.plugin);
    }

    private void loadUI() {
        this.statsMenu = new StatsMenu(this.plugin);
    }

    private void loadStats() {
        this.plugin.runTask(() -> this.plugin.getServer().getOnlinePlayers().forEach(this::loadStats));
    }

    private void loadStats(@NotNull Player player) {
        Map<String, JobStats> stats = this.plugin.getDataHandler().getStats(player.getUniqueId());
        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        user.loadStats(stats);
    }

    @NotNull
    public Map<String, List<TopEntry>> getLevelTopMap() {
        return this.levelTopMap;
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
        this.plugin.runTaskAsync(() -> this.loadStats(player));
    }

    public void openStats(@NotNull Player player, @NotNull Job job) {
        this.statsMenu.open(player, job);
    }

    public void addStats(@NotNull Player player, @NotNull Job job, @NotNull Consumer<DayStats> consumer) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobStats jobStats = user.getStats(job);
        DayStats stats = jobStats.getTodayStats();

        consumer.accept(stats);
    }

    public void updateJobLevelsAndEmployees() {
        this.levelTopMap.clear();

        List<JobUser> users = this.plugin.getDataHandler().getUsers();

        this.plugin.getJobManager().getJobs().forEach(job -> {
            for (JobState state : JobState.values()) {
                if (state == JobState.INACTIVE) continue;
                int employess = (int) users.stream().filter(user -> user.getData(job).getState() == state).count();

                job.setEmployeesAmount(state, employess);
            }

            var topList = this.levelTopMap.computeIfAbsent(job.getId(), k -> new ArrayList<>());
            AtomicInteger position = new AtomicInteger();

            users.stream().sorted(Comparator.comparingInt((JobUser user) -> user.getData(job).getLevel()).reversed()).forEach(user -> {
                topList.add(new TopEntry(user.getName(), user.getData(job).getLevel(), position.incrementAndGet()));
            });
        });
    }
}
