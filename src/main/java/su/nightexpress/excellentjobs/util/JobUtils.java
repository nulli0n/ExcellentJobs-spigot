package su.nightexpress.excellentjobs.util;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.booster.BoosterUtils;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.hook.HookPlugin;
import su.nightexpress.excellentjobs.hook.impl.LevelledMobsHook;
import su.nightexpress.excellentjobs.hook.impl.MythicMobsHook;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobUtils {

    @NotNull
    public static ItemStack getDefaultZoneWand() {
        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD);

        ItemUtil.editMeta(itemStack, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("Zone Wand")));
            meta.setLore(Lists.newList(
                DARK_GRAY.wrap("(Drop to exit selection mode)"),
                "",
                LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("Left-Click to " + LIGHT_YELLOW.wrap("set 1st") + " point."),
                LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("Right-Click to " + LIGHT_YELLOW.wrap("set 2nd") + " point.")
            ));
        });

        return itemStack;
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
