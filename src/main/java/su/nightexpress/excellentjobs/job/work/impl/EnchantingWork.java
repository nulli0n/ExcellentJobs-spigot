package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class EnchantingWork extends Work<EnchantItemEvent, Material> {

    public EnchantingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, EnchantItemEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();

        double modifier = (Config.JOBS_ENCHANT_MULTIPLIER_BY_LEVEL_COST.get() * event.getExpLevelCost() / 100D);

        this.doObjective(player, item.getType(), 1, modifier);
        return true;
    }
}
