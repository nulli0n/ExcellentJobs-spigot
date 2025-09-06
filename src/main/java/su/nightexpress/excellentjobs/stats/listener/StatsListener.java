package su.nightexpress.excellentjobs.stats.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.event.JobObjectiveIncomeEvent;
import su.nightexpress.excellentjobs.api.event.JobPaymentEvent;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobIncome;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.nightcore.manager.AbstractListener;

public class StatsListener extends AbstractListener<JobsPlugin> {

    private final StatsManager statsManager;

    public StatsListener(@NotNull JobsPlugin plugin, @NotNull StatsManager statsManager) {
        super(plugin);
        this.statsManager = statsManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        this.statsManager.handleJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJobPayment(JobPaymentEvent event) {
        Player player = event.getPlayer();
        Job job = event.getJob();
        JobIncome income = event.getIncome();

        this.statsManager.addStats(player, job, stats -> {
            income.getCurrencyMap().forEach(stats::addCurrency);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJobObjective(JobObjectiveIncomeEvent event) {
        Player player = event.getPlayer();
        Job job = event.getJob();

        this.statsManager.addStats(player, job, stats -> {
            stats.addObjective(event.getObjective(), event.getWorkObjective().getObjectName(), 1);
        });
    }
}
