package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.excellentjobs.job.work.wrapper.WrappedEnchant;

public class EnchantObtainWork extends Work<EnchantItemEvent, WrappedEnchant> {

    public EnchantObtainWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EnchantItemEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<WrappedEnchant> getFormatter() {
        return WorkFormatters.WRAPPED_ENCHANTMENT;
    }

    @Override
    public boolean handle(@NotNull EnchantItemEvent event) {
        Player player = event.getEnchanter();

        event.getEnchantsToAdd().forEach((enchantment, level) -> {
            this.doObjective(player, new WrappedEnchant(enchantment, level), 1);
        });
        return true;
    }
}
