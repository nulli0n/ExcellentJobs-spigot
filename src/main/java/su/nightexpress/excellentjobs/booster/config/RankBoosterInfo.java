package su.nightexpress.excellentjobs.booster.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Set;

public class RankBoosterInfo extends BoosterInfo {

    private final String rank;
    private final int priority;

    public RankBoosterInfo(@NotNull String rank, int priority,
                           @NotNull Set<String> jobs,
                           @NotNull BoosterMultiplier multiplier) {
        super(jobs, multiplier);
        this.rank = rank.toLowerCase();
        this.priority = priority;
    }

    @NotNull
    public static RankBoosterInfo read(@NotNull FileConfig cfg, @NotNull String path, @NotNull String rank) {
        Set<String> jobs = cfg.getStringSet(path + ".Jobs");
        BoosterMultiplier multiplier = BoosterMultiplier.read(cfg, path);
        int priority = cfg.getInt(path + ".Priority");

        return new RankBoosterInfo(rank, priority, jobs, multiplier);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Priority", this.getPriority());
        super.write(cfg, path);
    }

    @NotNull
    public String getRank() {
        return rank;
    }

    public int getPriority() {
        return priority;
    }
}
