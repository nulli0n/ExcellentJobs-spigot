package su.nightexpress.excellentjobs.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.Collection;
import java.util.List;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NotNull JobsPlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    static class Expansion extends PlaceholderExpansion {

        private final JobsPlugin plugin;

        public Expansion(@NotNull JobsPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        @NotNull
        public String getAuthor() {
            return this.plugin.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return this.plugin.getName().toLowerCase();
        }

        @Override
        @NotNull
        public String getVersion() {
            return this.plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) return null;

            String key = params.split("_")[0];
            String rest = params.substring(key.length() + 1);

            Job job = plugin.getJobManager().getJobById(key);
            if (job != null) {
                JobUser user = this.plugin.getUserManager().getUserData(player);
                JobData data = user.getData(job);

                if (rest.equalsIgnoreCase("level")) {
                    return NumberUtil.format(data.getLevel());
                }
                if (rest.equalsIgnoreCase("xp")) {
                    return NumberUtil.format(data.getXP());
                }
                if (rest.equalsIgnoreCase("xp_required")) {
                    return NumberUtil.format(data.getMaxXP());
                }
                if (rest.equalsIgnoreCase("xp_to_up")) {
                    return NumberUtil.format(data.getXPToLevelUp());
                }
                if (rest.equalsIgnoreCase("xp_to_down")) {
                    return NumberUtil.format(data.getXPToLevelDown());
                }
                if (rest.equalsIgnoreCase("xp_multiplier")) {
                    return NumberUtil.format(job.getXPMultiplier(data.getLevel()));
                }
                if (rest.equalsIgnoreCase("xp_boost_multiplier")) {
                    Collection<Booster> boosters = plugin.getBoosterManager().getBoosters(player, job);
                    return NumberUtil.format(Booster.getXPBoost(boosters));
                }
                if (rest.equalsIgnoreCase("xp_boost_percent")) {
                    Collection<Booster> boosters = plugin.getBoosterManager().getBoosters(player, job);
                    return NumberUtil.format(Booster.getXPPercent(boosters));
                }
                if (rest.startsWith("currency_multiplier_")) {
                    String curId = rest.substring("currency_multiplier_".length());
                    return NumberUtil.format(job.getPaymentMultiplier(curId, data.getLevel()));
                }
                if (rest.startsWith("currency_boost_multiplier_")) {
                    String curId = rest.substring("currency_boost_multiplier_".length());
                    Collection<Booster> boosters = plugin.getBoosterManager().getBoosters(player, job);
                    return NumberUtil.format(Booster.getCurrencyBoost(curId, boosters));
                }
                if (rest.startsWith("currency_boost_percent_")) {
                    String curId = rest.substring("currency_boost_percent_".length());
                    Collection<Booster> boosters = plugin.getBoosterManager().getBoosters(player, job);
                    return NumberUtil.format(Booster.getCurrencyPercent(curId, boosters));
                }
                if (rest.startsWith("top_level_")) {
                    String[] info = rest.substring("top_level_".length()).split("_");
                    if (info.length < 2) return null;

                    StatsManager statsManager = this.plugin.getStatsManager();
                    if (statsManager == null) return null;

                    int pos = NumberUtil.getInteger(info[0], 0);
                    String type = info[1];

                    List<TopEntry> list = statsManager.getLevelTopEntries(job);
                    if (list.size() < pos) return "-";

                    TopEntry entry = list.get(pos - 1);
                    if (type.equalsIgnoreCase("name")) {
                        return entry.getName();
                    }
                    else if (type.equalsIgnoreCase("value")) {
                        return NumberUtil.format(entry.getValue());
                    }
                }
                if (rest.equalsIgnoreCase("employees_total")) {
                    return NumberUtil.format(job.getEmployees());
                }
                if (rest.equalsIgnoreCase("employees_primary")) {
                    return NumberUtil.format(job.getEmployeesAmount(JobState.PRIMARY));
                }
                if (rest.equalsIgnoreCase("employees_secondary")) {
                    return NumberUtil.format(job.getEmployeesAmount(JobState.SECONDARY));
                }
            }
            return null;
        }
    }
}
