package su.nightexpress.excellentjobs.job.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.event.bukkit.PlayerCollectedHoneyEvent;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.PDCUtil;

public class JobGenericListener extends AbstractListener<JobsPlugin> {

    private final JobManager jobManager;

    public JobGenericListener(@NotNull JobsPlugin plugin, @NotNull JobManager jobManager) {
        super(plugin);
        this.jobManager = jobManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        this.jobManager.handleQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSkillFireworkDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Firework firework)) return;
        if (PDCUtil.getBoolean(firework, Keys.LEVEL_FIREWORK).orElse(false)) {
            event.setCancelled(true);
        }
    }

    // TODO Player harvest honeycomb event (shears check + beehive block state)

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHoneyCollect(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BEEHIVE) return;

        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack == null || itemStack.getType() != Material.GLASS_BOTTLE) return;

        this.plugin.runTask(task -> {
            ItemStack honey = player.getInventory().getItem(slot);
            if (honey == null || honey.getType() != Material.HONEY_BOTTLE) return;

            PlayerCollectedHoneyEvent honeyEvent = new PlayerCollectedHoneyEvent(player, block);
            this.plugin.getPluginManager().callEvent(honeyEvent);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewingClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory inventory)) return;

        BrewingStand stand = inventory.getHolder();
        if (stand == null) return;

        PDCUtil.set(stand, Keys.BREWING_HOLDER, event.getWhoClicked().getUniqueId().toString());
        stand.update();
    }
}
