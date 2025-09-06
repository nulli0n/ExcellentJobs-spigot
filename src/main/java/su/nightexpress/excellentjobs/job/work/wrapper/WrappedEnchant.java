package su.nightexpress.excellentjobs.job.work.wrapper;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.NumberUtil;

public class WrappedEnchant {

    private static final String DELIMITER = ";";

    private final Enchantment enchantment;
    private final int level;

    public WrappedEnchant(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = Math.max(1, Math.min(level, enchantment.getMaxLevel()));
    }

    @Nullable
    public static WrappedEnchant deserialize(@NotNull String string) {
        String enchantName;
        String numberStr;

        if (string.contains(DELIMITER)) {
            String[] split = string.split(DELIMITER);
            enchantName = split[0];
            numberStr = split.length >= 2 ? split[1] : "1";
        }
        else {
            int lastIndex = string.lastIndexOf(':');
            if (lastIndex != -1) {
                enchantName = string.substring(0, lastIndex);
                numberStr = string.substring(lastIndex + 1);
            }
            else return null;
        }

        Enchantment enchant = BukkitThing.getEnchantment(enchantName);
        if (enchant == null) return null;

        int level = NumberUtil.getIntegerAbs(numberStr);
        return new WrappedEnchant(enchant, level);
    }

    @NotNull
    public String serialize() {
        return BukkitThing.getAsString(this.enchantment) + DELIMITER + this.level;
    }

    @NotNull
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public int getLevel() {
        return this.level;
    }
}
