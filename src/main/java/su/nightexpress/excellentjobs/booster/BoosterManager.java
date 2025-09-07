package su.nightexpress.excellentjobs.booster;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.booster.config.BoosterConfig;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.booster.impl.BoosterSchedule;
import su.nightexpress.excellentjobs.booster.impl.BoosterType;
import su.nightexpress.excellentjobs.booster.listener.BoosterListener;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.HashSet;
import java.util.Set;

public class BoosterManager extends AbstractManager<JobsPlugin> {

    private static final String FILE_NAME = "boosters.yml";

    private Booster globalBooster;

    public BoosterManager(@NotNull JobsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.loadConfig();

        this.addListener(new BoosterListener(this.plugin, this));

        this.addAsyncTask(this::tickBoosters, BoosterConfig.TICK_INTERVAL.get());
    }

    @Override
    protected void onShutdown() {
        this.globalBooster = null;
    }

    private void loadConfig() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, FILE_NAME);
        config.initializeOptions(BoosterConfig.class);
        config.saveChanges();
    }

    public void tickBoosters() {
        this.tickGlobal();
        this.tickSchedules();
        this.tickPersonal();
    }

    private void tickGlobal() {
        if (this.globalBooster == null) return;
        if (this.globalBooster.isExpired()) {
            Lang.BOOSTER_EXPIRED_GLOBAL.message().broadcast(replacer -> replacer.replace(this.globalBooster.replacePlaceholers()));
            this.globalBooster = null;
        }
    }

    private void tickSchedules() {
        if (this.hasGlobalBoost()) return;

        BoosterSchedule ready = this.getBoosterSchedules().stream().filter(BoosterSchedule::isReady).findFirst().orElse(null);
        if (ready == null) return;

        this.activateBooster(ready, true);
    }

    private void tickPersonal() {
        Players.getOnline().forEach(player -> {
            JobUser user = plugin.getUserManager().getOrFetch(player);

            user.getBoosterMap().forEach((jobId, booster) -> {
                if (!booster.isExpired()) return;

                Job job = plugin.getJobManager().getJobById(jobId);
                if (job != null) {
                    Lang.BOOSTER_EXPIRED_PERSONAL.message().send(player, replacer -> replacer
                        .replace(job.replacePlaceholders())
                        .replace(booster.replacePlaceholers()));
                }

                user.removeBooster(jobId);
            });
        });
    }

    public boolean hasGlobalBoost() {
        return this.globalBooster != null && this.globalBooster.isValid();
    }

    @NotNull
    public Set<BoosterSchedule> getBoosterSchedules() {
        return new HashSet<>(BoosterConfig.getBoosterScheduleMap().values());
    }

    @Nullable
    public BoosterSchedule getBoosterScheduleById(@NotNull String id) {
        return BoosterConfig.getBoosterScheduleMap().get(id.toLowerCase());
    }

    @Nullable
    public Booster getGlobalBooster() {
        return this.globalBooster;
    }

    public double getRankBoost(@NotNull Player player, @NotNull MultiplierType type) {
        return (type == MultiplierType.INCOME ? BoosterConfig.BOOSTERS_BY_RANK_INCOME : BoosterConfig.BOOSTERS_BY_RANK_XP).get().getGreatest(player);
    }

    public double getGlobalBoost(@NotNull MultiplierType type) {
        return this.getBoosterValue(this.globalBooster, type);
    }

    public double getPersonalBoost(@NotNull Player player, @NotNull Job job, @NotNull MultiplierType type) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        return this.getBoosterValue(user.getBooster(job), type);
    }

    public double getTotalBoostPercent(@NotNull Player player, @NotNull Job job, @NotNull MultiplierType multiplierType) {
        double percent = 0D;
        for (BoosterType type : BoosterType.values()) {
            percent += BoosterUtils.getAsPercent(this.getBoosterMultiplier(player, job, type, multiplierType));
        }
        return percent;
    }

    public double getTotalBoost(@NotNull Player player, @NotNull Job job, @NotNull MultiplierType multiplierType) {
        return this.getTotalBoostPercent(player, job, multiplierType) / 100D;
    }

    private double getBoosterValue(@Nullable Booster booster, @NotNull MultiplierType type) {
        return booster == null || !booster.hasMultiplier(type) ? 1D : booster.getValue(type);
    }

    public double getBoosterMultiplier(@NotNull Player player, @NotNull Job job, @NotNull BoosterType type, @NotNull MultiplierType multiplierType) {
        return switch (type) {
            case RANK -> this.getRankBoost(player, multiplierType);
            case GLOBAL -> this.getGlobalBoost(multiplierType);
            case PERSONAL -> this.getPersonalBoost(player, job, multiplierType);
        };
    }

    public long getBoosterExpireDate(@NotNull Player player, @NotNull Job job, @NotNull BoosterType type) {
        return switch (type) {
            case PERSONAL -> {
                JobUser user = plugin.getUserManager().getOrFetch(player);
                Booster booster = user.getBooster(job);
                yield booster == null ? 0L : booster.getExpireDate();
            }
            case GLOBAL -> this.hasGlobalBoost() ? this.globalBooster.getExpireDate() : 0L;
            case RANK -> -1L;
        };
    }

    public boolean hasBoosterMultiplier(@NotNull Player player, @NotNull Job job, @NotNull BoosterType type, @NotNull MultiplierType multiplierType) {
        return this.getBoosterMultiplier(player, job, type, multiplierType) != 1D;
    }

    public boolean activateBoosterById(@NotNull String id) {
        BoosterSchedule schedule = this.getBoosterScheduleById(id);
        if (schedule == null) return false;

        this.activateBooster(schedule, false);
        return true;
    }

    public void activateBooster(@NotNull BoosterSchedule schedule, boolean relative) {
        Booster booster = schedule.createBooster(true);
        if (!booster.isValid()) return;

        this.setGlobalBooster(booster);
    }

    public boolean setGlobalBooster(@NotNull Booster booster) {
        this.globalBooster = booster;
        this.notifyGlobalBooster(booster);
        return true;
    }

    public void removeGlobalBooster() {
        this.globalBooster = null;
    }

    public void notifyGlobalBooster(@NotNull Booster booster) {
        Lang.BOOSTER_ACTIVATED_GLOBAL.message().broadcast(replacer -> replacer
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(booster.getExpireDate(), TimeFormatType.LITERAL))
            .replace(booster.replacePlaceholers())
        );
    }

    public void notifyPersonalBooster(@NotNull Player player, @NotNull Job job, @NotNull Booster booster) {
        Lang.BOOSTER_ACTIVATED_PERSONAL.message().send(player, replacer -> replacer
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(booster.getExpireDate(), TimeFormatType.LITERAL))
            .replace(job.replacePlaceholders())
            .replace(booster.replacePlaceholers())
        );
    }

    public void displayBoosterInfo(@NotNull Player player, @NotNull Job job) {
        double totalXPPercent = this.getTotalBoostPercent(player, job, MultiplierType.XP);
        double totalPayPercent = this.getTotalBoostPercent(player, job, MultiplierType.INCOME);
        if (totalXPPercent == 0D && totalPayPercent == 0D) {
            Lang.BOOSTER_LIST_NOTHING.message().send(player);
            return;
        }

        Lang.BOOSTER_LIST_INFO.message().send(player, replacer -> replacer
            .replace(job.replacePlaceholders())
            .replace(Placeholders.GENERIC_XP_BONUS, JobUtils.formatBonus(totalXPPercent))
            .replace(Placeholders.GENERIC_INCOME_BONUS, JobUtils.formatBonus(totalPayPercent))
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                for (BoosterType type : BoosterType.values()) {
                    double xpMult = this.getBoosterMultiplier(player, job, type, MultiplierType.XP);
                    double payMult = this.getBoosterMultiplier(player, job, type, MultiplierType.INCOME);
                    if (xpMult == 1D && payMult == 1D) continue;

                    list.add(Replacer.create()
                        .replace(Placeholders.GENERIC_TYPE, () -> Lang.BOOSTER_TYPE.getLocalized(type))
                        .replace(Placeholders.GENERIC_XP_BOOST, () -> BoosterUtils.formatMultiplier(xpMult))
                        .replace(Placeholders.GENERIC_INCOME_BOOST, () -> BoosterUtils.formatMultiplier(payMult))
                        .replace(Placeholders.GENERIC_TIME, () -> {
                            long expireDate = this.getBoosterExpireDate(player, job, type);
                            return expireDate < 0L ? CoreLang.OTHER_INFINITY.text() : TimeFormats.formatDuration(expireDate, TimeFormatType.LITERAL);
                        })
                        .apply(Lang.BOOSTER_LIST_ENTRY.text())
                    );
                }
            })
        );
    }
}
