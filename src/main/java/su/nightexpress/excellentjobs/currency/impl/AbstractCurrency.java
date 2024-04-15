package su.nightexpress.excellentjobs.currency.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

public abstract class AbstractCurrency implements Currency {

    private final String          id;
    private final String          name;
    private final String          format;
    private final PlaceholderMap  placeholderMap;

    public AbstractCurrency(@NotNull String id, @NotNull String name, @NotNull String format) {
        this.id = id.toLowerCase();
        this.name = name;
        this.format = format;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.CURRENCY_ID, this::getId)
            .add(Placeholders.CURRENCY_NAME, this::getName);
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getFormat() {
        return format;
    }
}
