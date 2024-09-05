package su.nightexpress.excellentjobs.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Pair;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobUtils {

    @Nullable
    public static Pair<LocalTime, LocalTime> parseTimes(@NotNull String raw) {
        String[] split = raw.split(" ");
        if (split.length < 2) return null;

        try {
            LocalTime from = LocalTime.parse(split[0], DateTimeFormatter.ISO_LOCAL_TIME).truncatedTo(ChronoUnit.MINUTES);
            LocalTime to = LocalTime.parse(split[1], DateTimeFormatter.ISO_LOCAL_TIME).truncatedTo(ChronoUnit.MINUTES);
            return Pair.of(from, to);
        }
        catch (DateTimeParseException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @NotNull
    public static String serializeTimes(@NotNull Pair<LocalTime, LocalTime> pair) {
        LocalTime from = pair.getFirst();
        LocalTime to = pair.getSecond();

        return from.format(DateTimeFormatter.ISO_LOCAL_TIME) + " " + to.format(DateTimeFormatter.ISO_LOCAL_TIME);
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
