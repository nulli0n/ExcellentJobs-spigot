package su.nightexpress.excellentjobs.api.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.text.NightMessage;

public interface Currency extends Placeholder {

    @NotNull
    default String formatValue(double amount) {
        return NumberUtil.format(amount);
    }

    @NotNull
    default String format(double amount) {
        return this.replacePlaceholders().apply(this.getFormat()
            .replace(Placeholders.GENERIC_AMOUNT, this.formatValue(amount))
            .replace(Placeholders.GENERIC_NAME, this.getName())
        );
    }

    @NotNull
    default String formatAsLegacy(double amount) {
        return NightMessage.asLegacy(this.format(amount));
    }

    default double round(double amount) {
        return amount;
    }

    @NotNull CurrencyHandler getHandler();

    @NotNull String getId();

    @NotNull String getName();

    @NotNull String getFormat();
}
