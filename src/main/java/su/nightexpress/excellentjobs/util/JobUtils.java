package su.nightexpress.excellentjobs.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;

import java.time.LocalTime;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobUtils {

    @NotNull
    public static String formatTime(@NotNull LocalTime time) {
        return time.format(Config.GENERAL_TIME_FORMATTER.get());
    }

    @NotNull
    public static ItemStack getDefaultZoneWand() {
        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD);

        ItemUtil.editMeta(itemStack, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Zone Wand")));
            meta.setLore(Lists.newList(
                DARK_GRAY.enclose("(Drop to exit selection mode)"),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Left-Click to " + LIGHT_YELLOW.enclose("set 1st") + " point."),
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Right-Click to " + LIGHT_YELLOW.enclose("set 2nd") + " point.")
            ));
        });

        return itemStack;
    }
}
