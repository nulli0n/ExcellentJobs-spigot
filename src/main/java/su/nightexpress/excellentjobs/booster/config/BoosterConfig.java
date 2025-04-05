package su.nightexpress.excellentjobs.booster.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.booster.BoosterUtils;
import su.nightexpress.excellentjobs.booster.impl.BoosterSchedule;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.rankmap.DoubleRankMap;
import su.nightexpress.nightcore.util.rankmap.RankMap;

import java.util.Map;
import java.util.Set;

import static su.nightexpress.excellentjobs.Placeholders.*;

public class BoosterConfig {

    public static final ConfigValue<Integer> TICK_INTERVAL = ConfigValue.create("Settings.Tick_Interval",
        1,
        "Sets booster update interval.",
        "[*] Do not change unless you understand what you're doing.",
        "[Asynchronous]",
        "[Default is 1]");

    public static final ConfigValue<Set<String>> EXCLUSIVE_CURRENCIES = ConfigValue.create("Settings.Exclusives.Currency",
        Lists.newSet(CurrencyId.forCoinsEngine("super_coins"), "some_currency"),
        "Boosters will have no effect on listed currencies.",
        URL_ECO_BRIDGE
    ).onRead(set -> Lists.modify(set, String::toLowerCase));

    public static final ConfigValue<Map<String, BoosterSchedule>> BOOSTERS_BY_SCHEDULE = ConfigValue.forMapById("Boosters.BySchedule",
        BoosterSchedule::read,
        map -> map.putAll(BoosterUtils.getDefaultBoosterSchedules()),
        "Global job boosters that activates at specific day times.",
        "You can define as many as you want."
    );

    public static final ConfigValue<RankMap<Double>> BOOSTERS_BY_RANK_INCOME = ConfigValue.create("Boosters.ByRank.Income",
        DoubleRankMap::read,
        (config, path, map) -> map.write(config, path),
        () -> DoubleRankMap.permissioned(Perms.PREFIX + "rankbooster.income.", 1D).addValue("vip", 1.5D).addValue("pro", 2D),
        "Persistent job income boosters applied to players based on their rank or permissions."
    );

    public static final ConfigValue<RankMap<Double>> BOOSTERS_BY_RANK_XP = ConfigValue.create("Boosters.ByRank.XP",
        DoubleRankMap::read,
        (config, path, map) -> map.write(config, path),
        () -> DoubleRankMap.permissioned(Perms.PREFIX + "rankbooster.xp.", 1D).addValue("vip", 1.5D).addValue("pro", 2D),
        "Persistent job XP boosters applied to players based on their rank or permissions."
    );

    @NotNull
    public static Map<String, BoosterSchedule> getBoosterScheduleMap() {
        return BOOSTERS_BY_SCHEDULE.get();
    }
}
