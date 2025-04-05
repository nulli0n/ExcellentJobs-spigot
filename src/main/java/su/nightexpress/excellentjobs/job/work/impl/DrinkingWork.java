package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

import java.util.HashSet;
import java.util.Set;

public class DrinkingWork extends Work<PlayerItemConsumeEvent, PotionEffectType> {

    public DrinkingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerItemConsumeEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<PotionEffectType> getFormatter() {
        return WorkFormatters.EFFECT;
    }

    @Override
    public boolean handle(@NotNull PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!(item.getItemMeta() instanceof PotionMeta potionMeta)) return false;

        Set<PotionEffectType> types = new HashSet<>();
        if (potionMeta.getBasePotionType() != null) {
            potionMeta.getBasePotionType().getPotionEffects().forEach(e -> types.add(e.getType()));
        }

        potionMeta.getCustomEffects().forEach(effect -> types.add(effect.getType()));

        types.forEach(effectType -> {
            this.doObjective(player, effectType, 1);
        });
        return true;
    }
}
