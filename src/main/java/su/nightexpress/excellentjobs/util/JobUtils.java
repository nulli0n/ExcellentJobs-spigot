package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Pair;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

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
}
