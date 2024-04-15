package su.nightexpress.excellentjobs.currency.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.currency.CurrencyHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class ConfigCurrency extends AbstractCurrency {

    private final CurrencyHandler handler;

    public ConfigCurrency(@NotNull String id, @NotNull CurrencyHandler handler, @NotNull String name, @NotNull String format) {
        super(id, name, format);
        this.handler = handler;
    }

    @NotNull
    public static ConfigCurrency read(@NotNull String id, @NotNull CurrencyHandler handler, @NotNull FileConfig config, @NotNull String path) {
        String name = ConfigValue.create(path + ".Name", handler.getDefaultName(),
            "Sets currency display name."
        ).read(config);

        String format = ConfigValue.create(path + ".Format", handler.getDefaultFormat(),
            "Sets currency format.",
            "Available placeholders:",
            "- " + Placeholders.GENERIC_AMOUNT,
            "- " + Placeholders.GENERIC_NAME
        ).read(config);

        return new ConfigCurrency(id, handler, name, format);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.getName());
        config.set(path + ".Format", this.getFormat());
    }

    @NotNull
    @Override
    public CurrencyHandler getHandler() {
        return handler;
    }
}
