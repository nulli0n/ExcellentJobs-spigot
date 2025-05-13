package su.nightexpress.excellentjobs.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.stats.impl.TopEntry;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

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
            return this.plugin.getDescription().getAuthors().getFirst();
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

            JobUser user = this.plugin.getUserManager().getOrFetch(player);

            if (params.equalsIgnoreCase("total_level")) {
                return String.valueOf(user.countTotalLevel());
            }
            if (params.equalsIgnoreCase("jobs_primary")) {
                return listJobs(user, JobState.PRIMARY);
            }
            if (params.equalsIgnoreCase("jobs_secondary")) {
                return listJobs(user, JobState.SECONDARY);
            }
            if (params.equalsIgnoreCase("jobs_joined")) {
                return NumberUtil.format(user.countActiveJobs());
            }
            if (params.equalsIgnoreCase("jobs_max_joinable")) {
                int count = plugin.getJobManager().countJoinableJobs(player);
                return count < 0 ? Lang.OTHER_INFINITY.getString() : NumberUtil.format(count);
            }

            String key = params.split("_")[0];
            String rest = params.substring(key.length() + 1);

            Job job = plugin.getJobManager().getJobById(key);
            if (job != null) {
                JobData data = user.getData(job);

                if (rest.equalsIgnoreCase("level")) {
                    return NumberUtil.format(data.getLevel());
                }
                if (rest.equalsIgnoreCase("xp")) {
                    return NumberUtil.format(data.getXP());
                }
                if (rest.equalsIgnoreCase("xp_required")) {
                    return NumberUtil.format(data.getLevelXP());
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
                    return NumberUtil.format(JobsAPI.getBoost(player, job, MultiplierType.XP));
                }
                if (rest.equalsIgnoreCase("xp_boost_percent")) {
                    return NumberUtil.format(JobsAPI.getBoostPercent(player, job, MultiplierType.XP));
                }
                if (rest.equalsIgnoreCase("income_multiplier") || rest.equalsIgnoreCase("payment_multiplier")) {
                    return NumberUtil.format(job.getPaymentMultiplier(data.getLevel()));
                }
                if (rest.equalsIgnoreCase("income_boost_multiplier")) {
                    return NumberUtil.format(JobsAPI.getBoost(player, job, MultiplierType.INCOME));
                }
                if (rest.equalsIgnoreCase("income_boost_percent")) {
                    return NumberUtil.format(JobsAPI.getBoostPercent(player, job, MultiplierType.INCOME));
                }
                if (rest.equalsIgnoreCase("income")) {
                    return JobUtils.formatIncome(data.getIncome());
                }

                if (rest.startsWith("top_level_")) {
                    String[] info = rest.substring("top_level_".length()).split("_");
                    if (info.length < 2) return null;

                    StatsManager statsManager = this.plugin.getStatsManager();
                    if (statsManager == null) return null;

                    int pos = NumberUtil.getIntegerAbs(info[0], 0);
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
            // TODO Stats
            return null;
        }

        @NotNull
        private static String listJobs(@NotNull JobUser user, @NotNull JobState state) {
            String delimiter = Lang.OTHER_JOB_DELIMITER.getString();
            List<String> jobNames = user.getDatas().stream()
                .filter(data -> data.getState() == state)
                .map(data -> data.getJob().getName())
                .sorted(String::compareTo)
                .toList();

            return NightMessage.asLegacy(jobNames.isEmpty() ? Lang.OTHER_NO_JOBS.getString() : String.join(delimiter, jobNames));
        }
    }
}
