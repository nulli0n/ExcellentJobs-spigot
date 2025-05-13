package su.nightexpress.excellentjobs.util;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.booster.BoosterUtils;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.hook.HookPlugin;
import su.nightexpress.excellentjobs.hook.impl.LevelledMobsHook;
import su.nightexpress.excellentjobs.hook.impl.MythicMobsHook;
import su.nightexpress.excellentjobs.job.impl.JobIncome;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.Map;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobUtils {

    public static final int START_LEVEL = 1;
    public static final int START_XP = 0;

    @NotNull
    public static NightItem getDefaultZoneWand() {
        return NightItem.fromType(Material.BLAZE_ROD)
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("Zone Wand")))
            .setLore(Lists.newList(
                DARK_GRAY.wrap("(Drop to exit selection mode)"),
                "",
                LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("Left-Click to " + LIGHT_YELLOW.wrap("set 1st") + " point."),
                LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("Right-Click to " + LIGHT_YELLOW.wrap("set 2nd") + " point.")
            ));
    }

    @NotNull
    public static Firework createFirework(@NotNull World world, @NotNull Location location) {
        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Type type = Rnd.get(FireworkEffect.Type.values());
        Color color = Color.fromBGR(Rnd.get(256), Rnd.get(256), Rnd.get(256));
        Color fade = Color.fromBGR(Rnd.get(256), Rnd.get(256), Rnd.get(256));
        FireworkEffect effect = FireworkEffect.builder()
            .flicker(Rnd.nextBoolean()).withColor(color).withFade(fade).with(type).trail(Rnd.nextBoolean()).build();

        meta.addEffect(effect);
        meta.setPower(Rnd.get(4));
        firework.setFireworkMeta(meta);
        PDCUtil.set(firework, Keys.levelFirework, true);
        return firework;
    }

    @NotNull
    public static String formatIncome(@NotNull JobIncome income) {
        if (income.isEmpty()) return Lang.OTHER_NO_INCOME.getString();

        return formatIncome(income.getCurrencyMap());
    }

    @NotNull
    public static String formatIncome(@NotNull Map<Currency, Double> map) {
        return map.entrySet().stream().map(entry -> entry.getKey().format(entry.getValue())).collect(Collectors.joining(Lang.OTHER_CURRENCY_DELIMITER.getString()));
    }

    public static int getJobCooldown(@NotNull Player player) {
        return Config.JOBS_COOLDOWN_VALUES.get().getSmallest(player);
    }

    public static long getJobCooldownTimestamp(@NotNull Player player) {
        return TimeUtil.createFutureTimestamp(getJobCooldown(player));
    }

    @NotNull
    public static Modifier getDefaultPaymentModifier() {
        return Modifier.add(0D, 0.01, 1D);
    }

    @NotNull
    public static Modifier getDefaultXPModifier() {
        return Modifier.add(0D, 0.01, 5D);
    }

    public static boolean canBeBoosted(@NotNull Currency currency) {
        return Config.isBoostersEnabled() && BoosterUtils.isBoostable(currency);
    }

    public static boolean hasMythicMobs() {
        return Plugins.isInstalled(HookPlugin.MYTHIC_MOBS);
    }

    public static boolean hasLevelledMobs() {
        return Plugins.isInstalled(HookPlugin.LEVELLED_MOBS);
    }

    public static boolean isVanillaMob(@NotNull Entity entity) {
        if (hasMythicMobs() && MythicMobsHook.isMythicMob(entity)) return false;

        return !(entity instanceof Player);
    }

    public static int getMobLevel(@NotNull LivingEntity entity) {
        return hasLevelledMobs() ? LevelledMobsHook.getLevel(entity) : 0;
    }
}
