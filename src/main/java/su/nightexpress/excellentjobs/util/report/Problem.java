package su.nightexpress.excellentjobs.util.report;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;

public class Problem {

    private final ProblemType type;
    private final String text;
    private final String formatted;

    public Problem(@NotNull ProblemType type, @NotNull String text) {
        this.type = type;
        this.text = text;
        this.formatted = format(type, text);
    }

    @NotNull
    public static Problem bad(@NotNull String text) {
        return new Problem(ProblemType.PROBLEM, text);
    }

    @NotNull
    public static Problem warn(@NotNull String text) {
        return new Problem(ProblemType.WARN, text);
    }

    @NotNull
    public static Problem good(@NotNull String text) {
        return new Problem(ProblemType.GOOD, text);
    }

    @NotNull
    public static String format(@NotNull ProblemType type, @NotNull String text) {
        return switch (type) {
            case PROBLEM -> Placeholders.problem(text);
            case WARN -> Placeholders.warn(text);
            case GOOD -> Placeholders.good(text);
        };
    }

    @NotNull
    public ProblemType getType() {
        return type;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public String getFormatted() {
        return formatted;
    }
}
