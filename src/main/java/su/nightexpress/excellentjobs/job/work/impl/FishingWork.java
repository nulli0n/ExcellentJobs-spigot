package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.hook.HookPlugin;
import su.nightexpress.excellentjobs.hook.work.EvenMoreFishWork;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.nightcore.util.Plugins;

public class FishingWork extends Work<PlayerFishEvent, Material> {

    public FishingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerFishEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return false;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item item)) return false;

        Player player = event.getPlayer();
        ItemStack itemStack = item.getItemStack();

        // Do not count EMF fishes.
        if (Plugins.isInstalled(HookPlugin.EVEN_MORE_FISH) && EvenMoreFishWork.isCustomFish(itemStack)) return false;

        this.doObjective(player, itemStack.getType(), itemStack.getAmount());
        return true;
    }
}
