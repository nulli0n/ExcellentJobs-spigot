package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class EnchantObtainWork extends Work<EnchantItemEvent, Enchantment> {

    public EnchantObtainWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EnchantItemEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Enchantment> getFormatter() {
        return WorkFormatters.ENCHANTMENT;
    }

    @Override
    public boolean handle(@NotNull EnchantItemEvent event) {
        Player player = event.getEnchanter();

        event.getEnchantsToAdd().keySet().forEach(enchantment -> {
            this.doObjective(player, enchantment, 1);
        });
        return true;
    }
}
