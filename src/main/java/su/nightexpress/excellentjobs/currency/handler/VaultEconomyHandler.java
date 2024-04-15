package su.nightexpress.excellentjobs.currency.handler;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.currency.CurrencyHandler;
import su.nightexpress.nightcore.integration.VaultHook;

public class VaultEconomyHandler implements CurrencyHandler {

    public static final String ID = "money";

    @Override
    @NotNull
    public String getDefaultName() {
        return "Money";
    }

    @Override
    @NotNull
    public String getDefaultFormat() {
        return "$" + Placeholders.GENERIC_AMOUNT;
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return VaultHook.getBalance(player);
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        VaultHook.addMoney(player, amount);
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        VaultHook.takeMoney(player, amount);
    }
}
