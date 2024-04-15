package su.nightexpress.excellentjobs.currency.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.excellentjobs.api.currency.CurrencyHandler;

import java.util.HashSet;
import java.util.Set;

public class CoinsEngineCurrency extends AbstractCurrency implements CurrencyHandler {

    public static final String PREFIX = "coinsengine_";

    private final Currency currency;

    public CoinsEngineCurrency(@NotNull String id, @NotNull String name, @NotNull String format, @NotNull Currency currency) {
        super(id, name, format);
        this.currency = currency;
    }

    @NotNull
    public static CoinsEngineCurrency create(@NotNull Currency currency) {
        String id = PREFIX + currency.getId();
        String name = currency.getName();
        String format = currency.getFormat();

        return new CoinsEngineCurrency(id, name, format, currency);
    }

    @NotNull
    public static Set<CoinsEngineCurrency> getCurrencies() {
        Set<CoinsEngineCurrency> currencies = new HashSet<>();
        CoinsEngineAPI.getCurrencyManager().getCurrencies().forEach(currency -> {
            if (!currency.isVaultEconomy()) {
                currencies.add(create(currency));
            }
        });
        return currencies;
    }

    @Override
    @NotNull
    public CurrencyHandler getHandler() {
        return this;
    }

    @Override
    @NotNull
    public String formatValue(double amount) {
        return this.currency.formatValue(amount);
    }

    @Override
    @NotNull
    public String format(double amount) {
        return this.currency.format(amount);
    }

    @Override
    public double round(double amount) {
        return this.currency.fine(amount);
    }

    @Override
    @NotNull
    public String getDefaultName() {
        return this.currency.getName();
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return CoinsEngineAPI.getBalance(player, this.currency);
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        CoinsEngineAPI.addBalance(player, this.currency, amount);
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        CoinsEngineAPI.removeBalance(player, this.currency, amount);
    }
}
