package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Hours {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    private final LocalTime from;
    private final LocalTime to;

    public Hours(@NotNull LocalTime from, @NotNull LocalTime to) {
        this.from = from;
        this.to = to;
    }

    @Nullable
    public static Hours parse(@NotNull String str) {
        String[] split = str.split(" ");
        if (split.length < 2) return null;

        try {
            LocalTime from = LocalTime.parse(split[0], FORMATTER).truncatedTo(ChronoUnit.MINUTES);
            LocalTime to = LocalTime.parse(split[1], FORMATTER).truncatedTo(ChronoUnit.MINUTES);
            return new Hours(from, to);
        }
        catch (DateTimeParseException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @NotNull
    public String serialize() {
        return this.from.format(FORMATTER) + " " + this.to.format(FORMATTER);
    }

    @NotNull
    public String format() {
        return this.from.truncatedTo(ChronoUnit.MINUTES).format(FORMATTER) + " - " + this.to.truncatedTo(ChronoUnit.MINUTES).format(FORMATTER);
    }

    public boolean isAvailable() {
        LocalTime now = LocalTime.now();
        return now.isAfter(this.from) && now.isBefore(this.to);
    }

    @NotNull
    public LocalTime getFrom() {
        return this.from;
    }

    @NotNull
    public LocalTime getTo() {
        return this.to;
    }
}
