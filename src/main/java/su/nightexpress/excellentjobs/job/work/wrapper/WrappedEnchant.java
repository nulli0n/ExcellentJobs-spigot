package su.nightexpress.excellentjobs.job.work.wrapper;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.NumberUtil;

public class WrappedEnchant {

    private static final String DELIMITER = ":";

    private final Enchantment enchantment;
    private final int level;

    public WrappedEnchant(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = Math.max(1, Math.min(level, enchantment.getMaxLevel()));
    }

    @Nullable
    public static WrappedEnchant deserialize(@NotNull String string) {
        String[] split = string.split(DELIMITER);
        Enchantment enchant = BukkitThing.getEnchantment(split[0]);
        if (enchant == null) return null;

        int level = split.length >= 2 ? NumberUtil.getIntegerAbs(split[1]) : 1;
        return new WrappedEnchant(enchant, level);
    }

    @NotNull
    public String serialize() {
        return BukkitThing.toString(this.enchantment) + DELIMITER + this.level;
    }

    @NotNull
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public int getLevel() {
        return this.level;
    }
}
