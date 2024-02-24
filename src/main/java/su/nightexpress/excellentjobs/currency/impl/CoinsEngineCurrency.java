package su.nightexpress.excellentjobs.currency.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.currency.handler.CoinsEngineHandler;

public class CoinsEngineCurrency extends ConfigCurrency {

    public CoinsEngineCurrency(@NotNull String id, @NotNull CoinsEngineHandler handler,
                               @NotNull String name, @NotNull String format) {
        super(id, handler, name, format);
    }

    @Override
    @NotNull
    public CoinsEngineHandler getHandler() {
        return (CoinsEngineHandler) super.getHandler();
    }

    @Override
    @NotNull
    public String formatValue(double amount) {
        return this.getHandler().getCurrency().formatValue(amount);
    }

    @Override
    @NotNull
    public String format(double amount) {
        return this.getHandler().getCurrency().format(amount);
    }

    @Override
    public double round(double amount) {
        return this.getHandler().getCurrency().fine(amount);
    }
}
