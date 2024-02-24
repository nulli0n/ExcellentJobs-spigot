package su.nightexpress.excellentjobs.zone.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.api.event.JobObjectiveIncomeEvent;
import su.nightexpress.excellentjobs.api.event.JobObjectiveXPEvent;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.manager.AbstractListener;

public class ZoneGenericListener extends AbstractListener<JobsPlugin> {

    private final ZoneManager zoneManager;

    public ZoneGenericListener(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin);
        this.zoneManager = zoneManager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneJobIncome(JobObjectiveIncomeEvent event) {
        Player player = event.getPlayer();
        Zone zone = this.zoneManager.getZone(player);
        if (zone == null && Config.ZONES_STRICT_MODE.get()) {
            event.setCancelled(true);
            return;
        }

        if (zone == null) return;

        if (!zone.isAvailable(player)) {
            Lang.ZONE_NOT_AVAILABLE.getMessage().send(player);
            event.setCancelled(true);
            return;
        }

        Currency currency = event.getCurrency();
        Modifier modifier = zone.getPaymentModifier(currency);
        if (modifier == null) return;

        int jobLevel = event.getJobData().getLevel();
        double multiplier = modifier.getValue(jobLevel);

        event.setPaymentMultiplier(event.getPaymentMultiplier() + multiplier);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneJobIncome(JobObjectiveXPEvent event) {
        Player player = event.getPlayer();
        Zone zone = this.zoneManager.getZone(player);
        if ((zone == null && Config.ZONES_STRICT_MODE.get()) || (zone != null && !zone.isAvailable(player))) {
            event.setCancelled(true);
            return;
        }
        if (zone == null) return;

        Modifier modifier = zone.getXPModifier();
        int jobLevel = event.getJobData().getLevel();
        double multiplier = modifier.getValue(jobLevel);

        event.setXPMultiplier(event.getXPMultiplier() + multiplier);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneEntrance(PlayerMoveEvent event) {
        if (!Config.ZONES_CONTROL_ENTRANCE.get()) return;

        Location to = event.getTo();
        if (to == null) return;

        Location from = event.getFrom();
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        Zone zone = this.zoneManager.getZoneByLocation(to);
        if (zone == null) return;

        Player player = event.getPlayer();
        if (!zone.isAvailable(player) && !this.zoneManager.isInZone(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneEntityDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        Zone zone = this.zoneManager.getZone(victim);
        if (zone == null) return;

        Player damager;
        if (attacker instanceof Player player) {
            damager = player;
        }
        else if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            damager = player;
        }
        else return;

        if (!zone.isAvailable(damager)) {
            Lang.ZONE_NOT_AVAILABLE.getMessage().send(attacker);
            event.setCancelled(true);
            return;
        }

        if (victim instanceof Player) {
            Lang.ZONE_NO_PVP.getMessage().send(attacker);
            event.setCancelled(!zone.isPvPAllowed());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneDecorationBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        if (player.hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        event.setCancelled(this.zoneManager.isInZone(player));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        Block block = event.getBlock();
        Zone zone = this.zoneManager.getZone(block);
        if (zone == null) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockProtection(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        Block block = event.getBlock();
        Zone zone = this.zoneManager.getZone(block);
        if (zone == null) return;

        Player player = event.getPlayer();
        BlockList blockList = zone.getBlockList(block);
        if (blockList == null) {
            event.setCancelled(true);
            return;
        }
        if (!zone.isAvailable(player)) {
            Lang.ZONE_NOT_AVAILABLE.getMessage().send(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onZoneBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Zone zone = this.zoneManager.getZone(block);
        if (zone == null) return;

        //Player player = event.getPlayer();
        World world = block.getWorld();
        BlockList blockList = zone.getBlockList(block);
        if (blockList != null/* && zone.isAvailable(player)*/) {
            if (!blockList.isDropItems()) {
                event.setExpToDrop(0);
                event.setDropItems(false);
            }
            blockList.onBlockBreak(block);
            this.plugin.runTask(task -> world.setBlockData(block.getLocation(), blockList.getFallbackMaterial().createBlockData()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockExplode1(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> zoneManager.getZone(block) != null);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockExplode2(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> zoneManager.getZone(block) != null);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockForm(EntityBlockFormEvent event) {
        if (event.getEntity().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;
        if (this.zoneManager.isInZone(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockChange(EntityChangeBlockEvent event) {
        if (event.getEntity().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;
        if (this.zoneManager.isInZone(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}
