package su.nightexpress.excellentjobs.currency.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.api.currency.CurrencyHandler;
import su.nightexpress.excellentjobs.currency.handler.VaultEconomyHandler;
import su.nightexpress.excellentjobs.hook.HookId;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

public class ConfigCurrency implements Currency {

    private final String id;
    private final CurrencyHandler handler;
    private final PlaceholderMap  placeholderMap;

    private String name;
    private String format;

    public ConfigCurrency(@NotNull String id, @NotNull CurrencyHandler handler, @NotNull String name, @NotNull String format) {
        this.id = id.toLowerCase();
        this.handler = handler;
        this.name = name;
        this.format = format;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.CURRENCY_ID, this::getId)
            .add(Placeholders.CURRENCY_NAME, this::getName);
    }

    @NotNull
    public static ConfigCurrency read(@NotNull String id,
                                      @NotNull CurrencyHandler handler,
                                      @NotNull FileConfig config,
                                      @NotNull String path) {

        String name = ConfigValue.create(path + ".Name",
            StringUtil.capitalizeUnderscored(id),
            "Sets currency display name."
        ).read(config);

        String defFormat;
        if (handler instanceof VaultEconomyHandler) {
            defFormat = "$" + Placeholders.GENERIC_AMOUNT;
        }
        else defFormat = Placeholders.GENERIC_AMOUNT + " " + Placeholders.CURRENCY_NAME;

        String format = ConfigValue.create(path + ".Format",
            defFormat,
            "Sets currency format.", "Available placeholders:",
            "- " + Placeholders.GENERIC_AMOUNT,
            "- " + Placeholders.CURRENCY_NAME,
            "This option is useless for " + HookId.COINS_ENGINE + " (it has its own format setting)."
        ).read(config);

        return new ConfigCurrency(id, handler, name, format);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.getName());
        config.set(path + ".Format", this.getFormat());
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
    public CurrencyHandler getHandler() {
        return handler;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(@NotNull String format) {
        this.format = format;
    }
}
