package su.nightexpress.excellentjobs.booster;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.booster.config.BoosterInfo;
import su.nightexpress.excellentjobs.booster.config.RankBoosterInfo;
import su.nightexpress.excellentjobs.booster.config.TimedBoosterInfo;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.booster.impl.ExpirableBooster;
import su.nightexpress.excellentjobs.booster.listener.BoosterListenerGeneric;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.TimeUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BoosterManager extends AbstractManager<JobsPlugin> {

    private final Map<String, ExpirableBooster> globalBoosterMap;

    public BoosterManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.globalBoosterMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        Config.BOOSTERS_CUSTOM.get().values().forEach(BoosterInfo::validate);
        Config.BOOSTERS_GLOBAL.get().values().forEach(BoosterInfo::validate);
        Config.BOOSTERS_RANK.get().values().forEach(BoosterInfo::validate);

        this.addListener(new BoosterListenerGeneric(this.plugin));

        this.addTask(this.plugin.createAsyncTask(this::updateGlobal).setSecondsInterval(30));
    }

    @Override
    protected void onShutdown() {
        this.getGlobalBoosterMap().clear();
    }

    @NotNull
    public Map<String, ExpirableBooster> getGlobalBoosterMap() {
        this.globalBoosterMap.values().removeIf(ExpirableBooster::isExpired);
        return globalBoosterMap;
    }

    @Nullable
    public ExpirableBooster getGlobalBooster(@NotNull Job job) {
        return this.getGlobalBoosterMap().get(job.getId());
    }

    @Nullable
    public Booster getRankBooster(@NotNull Player player) {
        return Config.BOOSTERS_RANK.get().values().stream()
            .filter(booster -> Players.getPermissionGroups(player).contains(booster.getRank()))
            .max(Comparator.comparingInt(RankBoosterInfo::getPriority)).map(BoosterInfo::createBooster).orElse(null);
    }

    @NotNull
    public Collection<Booster> getBoosters(@NotNull Player player, @NotNull Job job) {
        Set<Booster> boosters = new HashSet<>();

        JobUser user = plugin.getUserManager().getUserData(player);
        boosters.add(user.getBooster(job));
        boosters.add(this.getGlobalBooster(job));
        boosters.add(this.getRankBooster(player));
        boosters.removeIf(Objects::isNull);

        return boosters;
    }

    public void updateGlobal() {
        TimedBoosterInfo boosterInfo = Config.BOOSTERS_GLOBAL.get().values().stream()
            .filter(TimedBoosterInfo::isReady).findFirst().orElse(null);
        if (boosterInfo == null) return;

        var map = this.getGlobalBoosterMap();
        AtomicBoolean anyNew = new AtomicBoolean(false);

        this.plugin.getJobManager().getJobs().forEach(skill -> {
            if (boosterInfo.isApplicable(skill)) {
                if (!map.containsKey(skill.getId())) anyNew.set(true);

                map.put(skill.getId(), boosterInfo.createBooster());
            }
        });

        if (anyNew.get()) {
            ExpirableBooster booster = boosterInfo.createBooster();
            String jobNames = boosterInfo.getJobs().stream()
                .map(id -> plugin.getJobManager().getJobById(id))
                .filter(Objects::nonNull)
                .map(Job::getName).collect(Collectors.joining(", "));

            Lang.BOOSTER_NOTIFY_GLOBAL.getMessage()
                .replace(Placeholders.JOB_NAME, jobNames)
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(booster.getExpireDate()))
                .replace(Placeholders.XP_BOOST_MODIFIER, 1D + NumberUtil.format(booster.getMultiplier().getXPMultiplier()))
                .replace(Placeholders.XP_BOOST_PERCENT, NumberUtil.format(booster.getMultiplier().getXPPercent()))
                .replace(Placeholders.GENERIC_CURRENCY, list -> {
                    EconomyBridge.getCurrencies().forEach(currency -> {
                        double percent = booster.getMultiplier().getCurrencyPercent(currency);
                        double modifier = booster.getMultiplier().getCurrencyMultiplier(currency);
                        if (percent == 0D || modifier == 0D) return;

                        list.add(currency.replacePlaceholders().apply(Lang.BOOSTER_CURRENCY_INFO.getString())
                            .replace(Placeholders.CURRENCY_BOOST_PERCENT, NumberUtil.format(percent))
                            .replace(Placeholders.CURRENCY_BOOST_MODIFIER, 1D + NumberUtil.format(modifier))
                        );
                    });
                })
                .broadcast();
        }
    }
}
