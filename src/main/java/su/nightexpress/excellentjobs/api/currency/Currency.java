package su.nightexpress.excellentjobs.api.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

public interface Currency extends Placeholder {

    @NotNull
    default String formatValue(double amount) {
        return NumberUtil.format(amount);
    }

    @NotNull
    default String format(double amount) {
        return this.replacePlaceholders().apply(this.getFormat()).replace(Placeholders.GENERIC_AMOUNT, this.formatValue(amount));
    }

    default double round(double amount) {
        return amount;
    }

    @NotNull CurrencyHandler getHandler();

    @NotNull String getId();

    @NotNull String getName();

    void setName(@NotNull String name);

    @NotNull String getFormat();
}
