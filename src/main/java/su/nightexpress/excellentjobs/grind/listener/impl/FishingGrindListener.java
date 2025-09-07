package su.nightexpress.excellentjobs.grind.listener.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.table.impl.FishingGrindTable;
import su.nightexpress.excellentjobs.grind.type.impl.FishingGrindType;

public class FishingGrindListener extends GrindListener<FishingGrindTable, FishingGrindType> {

    public FishingGrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull FishingGrindType grindType) {
        super(plugin, grindManager, grindType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrindFishing(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        Player player = event.getPlayer();
        if (!this.grindManager.canGrinding(player)) return;

        ItemStack tool = player.getInventory().getItem(hand);

        Entity entity = event.getCaught();
        if (entity == null) return;

        this.giveXP(player, tool, (job, table) -> table.getXPForCaught(entity));
    }
}
