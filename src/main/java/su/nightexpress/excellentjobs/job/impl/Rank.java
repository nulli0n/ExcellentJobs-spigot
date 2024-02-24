package su.nightexpress.excellentjobs.job.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

@Deprecated
public class Rank implements Placeholder {

    private final String                     id;
    private final String                     name;

    private final PlaceholderMap placeholderMap;

    public Rank(
        @NotNull String id,
        @NotNull String name
        ) {
        this.id = id.toLowerCase();
        this.name = name;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.RANK_ID, this::getId)
            .add(Placeholders.RANK_NAME, this::getName);
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
