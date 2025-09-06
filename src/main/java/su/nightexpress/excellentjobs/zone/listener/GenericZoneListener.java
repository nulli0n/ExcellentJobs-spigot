package su.nightexpress.excellentjobs.zone.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
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

public class GenericZoneListener extends AbstractListener<JobsPlugin> {

    private final ZoneManager manager;

    public GenericZoneListener(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        this.manager.getZones(world).forEach(zone -> zone.activate(world));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        this.manager.getZones(world).forEach(zone -> zone.deactivate(world));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneJobIncome(JobObjectiveIncomeEvent event) {
        Player player = event.getPlayer();
        Zone zone = this.manager.getZone(player);
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
        Zone zone = this.manager.getZone(player);
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

        Location from = event.getFrom();
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        Zone zone = this.manager.getZoneByLocation(to);
        if (zone == null) return;

        Player player = event.getPlayer();
        if (!zone.isAvailable(player) && !this.manager.isInZone(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();

        Zone zone = this.manager.getZone(victim);
        if (zone == null) return;

        DamageSource source = event.getDamageSource();
        if (!(source.getCausingEntity() instanceof Player damager)) return;

        if (!zone.isAvailable(damager)) {
            Lang.ZONE_NOT_AVAILABLE.getMessage().send(damager);
            event.setCancelled(true);
            return;
        }

        if (victim instanceof Player) {
            Lang.ZONE_NO_PVP.getMessage().send(damager);
            event.setCancelled(!zone.isPvPAllowed());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneDecorationBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        if (player.hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        event.setCancelled(this.manager.isInZone(player));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        Block block = event.getBlock();
        Zone zone = this.manager.getZone(block);
        if (zone == null) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockProtection(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        Block block = event.getBlock();
        Zone zone = this.manager.getZone(block);
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
        Zone zone = this.manager.getZone(block);
        if (zone == null || !zone.isActive()) return;

        zone.handleBlockBreak(event, block);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectionSignChangeAndBlockUsage(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        if (player.hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;

        Zone zone = this.manager.getZone(player);
        if (zone == null) return;

        if (block.getState() instanceof Sign || zone.isDisabledInteraction(block)) {
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProtectionLuqidFill(PlayerBucketFillEvent event) {
        event.setCancelled(this.checkProtectionLuqid(event));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProtectionLuqidEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(this.checkProtectionLuqid(event));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProtectionLuqidEntity(PlayerBucketEntityEvent event) {
        event.setCancelled(this.checkProtectionLuqid(event));
    }

    private boolean checkProtectionLuqid(PlayerEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return false;

        Zone zone = this.manager.getZone(player);
        return zone != null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> manager.getZone(block) != null);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> manager.getZone(block) != null);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        if (event.getEntity().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;
        if (this.manager.isInZone(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onZoneBlockChange(EntityChangeBlockEvent event) {
        if (event.getEntity().hasPermission(Perms.BYPASS_ZONE_PROTECTION)) return;
        if (this.manager.isInZone(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}
