package su.nightexpress.excellentjobs.currency.handler;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.excellentjobs.api.currency.CurrencyHandler;

public class CoinsEngineHandler implements CurrencyHandler {

    private final Currency currency;

    public CoinsEngineHandler(@NotNull Currency currency) {
        this.currency = currency;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
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
