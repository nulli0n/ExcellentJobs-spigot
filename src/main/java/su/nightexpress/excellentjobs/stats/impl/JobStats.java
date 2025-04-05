package su.nightexpress.excellentjobs.stats.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class JobStats {

    private final Map<String, DayStats> dayStatsMap;

    public JobStats() {
        this(new HashMap<>());
    }

    public JobStats(@NotNull Map<String, DayStats> dayStatsMap) {
        this.dayStatsMap = dayStatsMap;
    }

    @NotNull
    public Map<String, DayStats> getDayStatsMap() {
        return dayStatsMap;
    }

    @NotNull
    public DayStats getTodayStats() {
        return this.getStatsOrCreate(TimeUtil.getCurrentDate());
    }

    @NotNull
    public DayStats getWeeklyStats() {
        return this.getStatsForDays(7);
    }

    @NotNull
    public DayStats getMonthlyStats() {
        return this.getStatsForDays(30);
    }

    @NotNull
    public DayStats getAllTimeStats() {
        return this.getStatsInRange(null, null);
    }

    @NotNull
    public DayStats getStatsForDays(int days) {
        return this.getStatsForDays(days, -1);
    }

    @NotNull
    public DayStats getStatsForDays(int minDays, int maxDays) {
        return this.getStatsInRange(this.adjusted(minDays), this.adjusted(maxDays));
    }

    @Nullable
    private LocalDate adjusted(int days) {
        return days < 0 ? null : TimeUtil.getCurrentDate().minusDays(days);
    }

    @NotNull
    public DayStats getStatsOrCreate(@NotNull LocalDate date) {
        return this.dayStatsMap.computeIfAbsent(date.toString(), k -> new DayStats());
    }

    @Nullable
    public DayStats getStats(@NotNull LocalDate date) {
        return this.dayStatsMap.get(date.toString());
    }

    @NotNull
    public DayStats getStatsInRange(@Nullable LocalDate min, @Nullable LocalDate max) {
        if (min != null && max != null) {
            if (min.isAfter(max)) throw new IllegalStateException("min can not be > max!");
        }

        long minTimestamp = min == null ? -1L : toEpochMillis(min);
        long maxTimestamp = max == null ? -1L : toEpochMillis(max);

        DayStats stats = new DayStats();

        this.dayStatsMap.values().forEach(dayStats -> {
            long timestamp = dayStats.getTimestamp();

            if (minTimestamp > 0 && timestamp < minTimestamp) return;
            if (maxTimestamp > 0 && timestamp > maxTimestamp) return;

            stats.add(dayStats);
        });

        return stats;
    }

    public static long toEpochMillis(@NotNull LocalDate date) {
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        return TimeUtil.toEpochMillis(dateTime);
    }
}
