package su.nightexpress.excellentjobs.booster.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.impl.ExpirableBooster;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TimedBoosterInfo extends BoosterInfo {

    private final Map<DayOfWeek, Set<LocalTime>> startTimes;
    private final int duration;

    public TimedBoosterInfo(@NotNull Set<String> jobs,
                            @NotNull BoosterMultiplier multiplier,
                            @NotNull Map<DayOfWeek, Set<LocalTime>> startTimes,
                            int duration) {
        super(jobs, multiplier);
        this.startTimes = startTimes;
        this.duration = duration;
    }

    @NotNull
    public static TimedBoosterInfo read(@NotNull FileConfig cfg, @NotNull String path) {
        Set<String> jobs = cfg.getStringSet(path + ".Jobs");
        BoosterMultiplier multiplier = BoosterMultiplier.read(cfg, path);

        Map<DayOfWeek, Set<LocalTime>> startTimes = new HashMap<>();
        for (String dayName : cfg.getSection(path + ".Start_Times")) {
            DayOfWeek day = StringUtil.getEnum(dayName, DayOfWeek.class).orElse(null);
            if (day == null) continue;

            Set<String> timesRaw = cfg.getStringSet(path + ".Start_Times." + dayName);
            Set<LocalTime> times = timesRaw.stream().map(raw -> {
                try {
                    return LocalTime.parse(raw, DateTimeFormatter.ISO_LOCAL_TIME).truncatedTo(ChronoUnit.MINUTES);
                }
                catch (DateTimeParseException ignored) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toSet());

            startTimes.put(day, times);
        }

        int duration = cfg.getInt(path + ".Duration");

        return new TimedBoosterInfo(jobs, multiplier, startTimes, duration);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Jobs", this.getJobs());
        this.getStartTimes().forEach((day, times) -> {
            cfg.set(path + ".Start_Times." + day.name(), times.stream().map(time -> time.format(DateTimeFormatter.ISO_LOCAL_TIME)).toList());
        });
        cfg.set(path + ".Duration", this.getDuration());
        this.getMultiplier().write(cfg, path);
    }

    @Override
    @NotNull
    public ExpirableBooster createBooster() {
        return new ExpirableBooster(this);
    }

    public boolean isReady() {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        Set<LocalTime> times = this.getStartTimes().get(day);
        if (times == null || times.isEmpty()) return false;

        LocalTime timeNow = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        return times.stream().anyMatch(stored -> stored.equals(timeNow));
    }

    @NotNull
    public Map<DayOfWeek, Set<LocalTime>> getStartTimes() {
        return startTimes;
    }

    public int getDuration() {
        return duration;
    }
}
