package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.PDCUtil;

import java.util.UUID;

public class BrewingWork extends Work<BrewEvent, PotionEffectType> {

    public BrewingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, BrewEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<PotionEffectType> getFormatter() {
        return WorkFormatters.EFFECT;
    }

    @Override
    public boolean handle(@NotNull BrewEvent event) {
        BrewerInventory inventory = event.getContents();

        BrewingStand stand = inventory.getHolder();
        if (stand == null) return false;

        String uuidRaw = PDCUtil.getString(stand, Keys.BREWING_HOLDER).orElse(null);
        UUID uuid = uuidRaw == null ? null : UUID.fromString(uuidRaw);
        if (uuid == null) return false;

        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null) return false;

        int[] slots = new int[]{0, 1, 2};

        plugin.runTask(task -> {
            for (int slot : slots) {
                ItemStack item = inventory.getItem(slot);
                if (item == null || item.getType().isAir()) continue;

                ItemMeta meta = item.getItemMeta();
                if (!(meta instanceof PotionMeta potionMeta)) continue;

                if (potionMeta.getBasePotionType() != null) {
                    for (PotionEffect effect : potionMeta.getBasePotionType().getPotionEffects()) {
                        this.doObjective(player, effect.getType(), item.getAmount());
                    }
                }

                potionMeta.getCustomEffects().forEach(effect -> {
                    this.doObjective(player, effect.getType(), item.getAmount());
                });
            }
        });
        return true;
    }
}
