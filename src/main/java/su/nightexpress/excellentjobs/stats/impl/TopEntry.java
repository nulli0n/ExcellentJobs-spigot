package su.nightexpress.excellentjobs.stats.impl;

import org.jetbrains.annotations.NotNull;

public class TopEntry {

    private final int    position;
    private final String name;
    private final int    value;

    public TopEntry(@NotNull String name, int value, int position) {
        this.name = name;
        this.value = value;
        this.position = position;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }
}
