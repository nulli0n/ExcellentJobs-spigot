package su.nightexpress.excellentjobs.action;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.RayTraceResult;
import su.nightexpress.excellentjobs.api.event.bukkit.PlayerCollectedHoneyEvent;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.hook.HookId;
import su.nightexpress.excellentjobs.hook.impl.EvenMoreFishHook;
import su.nightexpress.excellentjobs.hook.impl.LevelledMobsHook;
import su.nightexpress.excellentjobs.hook.impl.MythicMobsHook;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EventHelpers {

    public static final EventHelper<InventoryClickEvent, Material> ANVIL_RENAME  = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.ANVIL) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        AnvilInventory anvil = (AnvilInventory) inventory;
        if (anvil.getRepairCost() <= 0) return false;

        ItemStack first = anvil.getItem(0);
        if (first == null || first.getType().isAir()) return false;

        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        String renameText = anvil.getRenameText();
        if (renameText == null) return false;

        String nameSource = Colorizer.restrip(ItemUtil.getItemName(first));
        String nameResult = Colorizer.restrip(renameText);
        if (nameSource.equalsIgnoreCase(nameResult)) return false;

        Player player = (Player) event.getWhoClicked();
        plugin.runTask(task -> {
            ItemStack result2 = anvil.getItem(2);
            if (result2 != null && !result2.getType().isAir()) return;

            processor.progressObjective(player, result.getType(), result.getAmount());
        });
        return true;
    };

    public static final EventHelper<InventoryClickEvent, Material> ANVIL_REPAIR  = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.ANVIL) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        AnvilInventory anvil = (AnvilInventory) inventory;
        if (anvil.getRepairCost() <= 0) return false;

        ItemStack first = anvil.getItem(0);
        if (first == null || first.getType().isAir()) return false;

        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        if (first.getType() != result.getType()) return false;

        int damageSource = 0;
        int damageResult = 0;

        if (first.getItemMeta() instanceof Damageable damageable) {
            damageSource = damageable.getDamage();
        }
        if (result.getItemMeta() instanceof Damageable damageable) {
            damageResult = damageable.getDamage();
        }
        if (damageSource == damageResult) return false;

        Player player = (Player) event.getWhoClicked();
        plugin.runTask(task -> {
            ItemStack result2 = anvil.getItem(2);
            if (result2 != null && !result2.getType().isAir()) return;

            processor.progressObjective(player, result.getType(), 1);
        });
        return true;
    };

    public static final EventHelper<BlockBreakEvent, Material> BLOCK_BREAK = (plugin, event, processor) -> {
        Block block = event.getBlock();
        Material material = block.getType();

        if (block.getBlockData() instanceof Ageable ageable) {
            if (material != Material.SUGAR_CANE && material != Material.BAMBOO) {
                if (ageable.getAge() < ageable.getMaximumAge()) return false;
            }
        }

        if (PlayerBlockTracker.isTracked(block)) {
            // Dont need this, bc block tracker untrack plants when grow naturally.
            //if (!(block.getBlockData() instanceof Ageable ageable)) return false;
            //if (ageable.getAge() < ageable.getMaximumAge()) return false;
            return false;
        }

        Player player = event.getPlayer();
        processor.progressObjective(player, block.getType(), 1);
        return true;
    };

    public static final EventHelper<BlockFertilizeEvent, Material> BLOCK_FERTILIZE = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        if (player == null) return false;

        processor.progressObjective(player, event.getBlock().getType(), 1);

        event.getBlocks().forEach(blockState -> {
            processor.progressObjective(player, blockState.getType(), 1);
        });
        return true;
    };

    public static final EventHelper<PlayerHarvestBlockEvent, Material> BLOCK_HARVEST = (plugin, event, processor) -> {
        Block block = event.getHarvestedBlock();
        if (PlayerBlockTracker.isTracked(block)) return false;

        Player player = event.getPlayer();
        event.getItemsHarvested().forEach(itemStack -> {
            processor.progressObjective(player, itemStack.getType(), itemStack.getAmount());
        });
        return true;
    };

    public static final EventHelper<BlockPlaceEvent, Material> BLOCK_PLACE = (plugin, event, processor) -> {
        Block block = event.getBlockPlaced();

        processor.progressObjective(event.getPlayer(), block.getType(), 1);
        return false;
    };

    public static final EventHelper<EntityDamageByEntityEvent, EntityDamageEvent.DamageCause> DAMAGE_INFLICT = (plugin, event, processor) -> {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) return false;

        EntityDamageEvent.DamageCause cause = event.getCause();
        double damage = event.getDamage();
        processor.progressObjective(player, cause, (int) damage);
        return true;
    };

    public static final EventHelper<EntityDamageEvent, EntityDamageEvent.DamageCause> DAMAGE_RECEIVE = (plugin, event, processor) -> {
        Entity victim = event.getEntity();
        if (!(victim instanceof Player player)) return false;

        EntityDamageEvent.DamageCause cause = event.getCause();
        double damage = event.getDamage();
        processor.progressObjective(player, cause, (int) damage);
        return true;
    };

    public static final EventHelper<EntityBreedEvent, EntityType> ENTITY_BREED = (plugin, event, processor) -> {
        LivingEntity breeder = event.getBreeder();
        if (!(breeder instanceof Player player)) return false;

        processor.progressObjective(player, event.getEntity().getType(), 1);
        return true;
    };

    public static final EventHelper<EntityDeathEvent, EntityType> ENTITY_KILL = (plugin, event, processor) -> {
        LivingEntity entity = event.getEntity();
        if (JobManager.isDevastated(entity)) return false;

        Player killer = entity.getKiller();
        if (killer == null) return false;

        double multiplier = 0D;

        // Do not count MythicMobs here.
        if (Plugins.isInstalled(HookId.MYTHIC_MOBS) && MythicMobsHook.isMythicMob(entity)) return false;

        // LevelledMobs integration.
        if (Plugins.isInstalled(HookId.LEVELLED_MOBS) && Config.LEVELLED_MOBS_KILL_ENTITY_ENABLED.get()) {
            int level = LevelledMobsHook.getLevel(entity);
            double amount = Config.LEVELLED_MOBS_KILL_ENTITY_MULTIPLIER.get();
            multiplier = level * amount;
        }

        processor.progressObjective(killer, entity.getType(), 1, multiplier);
        return true;
    };

    public static final EventHelper<EntityDeathEvent, EntityType> ENTITY_SHOOT = (plugin, event, processor) -> {
        LivingEntity entity = event.getEntity();
        if (JobManager.isDevastated(entity)) return false;
        //if (entity.getVehicle() instanceof Minecart || entity.getVehicle() instanceof Boat) return false;
        //if (entity.getLastDamageCause() != null && entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CRAMMING) return false;

        Player killer = entity.getKiller();
        if (killer == null) return false;

        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent ede)) return false;
        if (!(ede.getDamager() instanceof Projectile)) return false;

        double multiplier = 0D;

        // Do not count MythicMobs here.
        if (Plugins.isInstalled(HookId.MYTHIC_MOBS) && MythicMobsHook.isMythicMob(entity)) return false;

        // LevelledMobs integration.
        if (Plugins.isInstalled(HookId.LEVELLED_MOBS) && Config.LEVELLED_MOBS_KILL_ENTITY_ENABLED.get()) {
            int level = LevelledMobsHook.getLevel(entity);
            double amount = Config.LEVELLED_MOBS_KILL_ENTITY_MULTIPLIER.get();
            multiplier = level * amount;
        }

        processor.progressObjective(killer, entity.getType(), 1, multiplier);
        return true;
    };

    public static final EventHelper<PlayerShearEntityEvent, EntityType> ENTITY_SHEAR = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();

        processor.progressObjective(player, entity.getType(), 1);
        return true;
    };

    public static final EventHelper<EntityTameEvent, EntityType> ENTITY_TAME = (plugin, event, processor) -> {
        Player player = (Player) event.getOwner();
        LivingEntity entity = event.getEntity();

        processor.progressObjective(player, entity.getType(), 1);
        return true;
    };

    public static final EventHelper<PlayerBucketFillEvent, EntityType> ENTITY_MILK = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        if (event.getItemStack() == null) return false;
        if (event.getItemStack().getType() != Material.MILK_BUCKET) return false;

        Location eyes = player.getEyeLocation();
        Location location = player.getLocation();
        RayTraceResult result = player.getWorld().rayTraceEntities(eyes, location.getDirection(), 5D, entity -> !(entity instanceof Player));
        if (result == null) return false;

        Entity entity = result.getHitEntity();
        if (entity == null) return false;

        processor.progressObjective(player, entity.getType(), 1);
        return true;
    };

    public static final EventHelper<PlayerItemConsumeEvent, Material> ITEM_CONSUME = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        processor.progressObjective(player, item.getType(), 1);
        return true;
    };

    public static final EventHelper<CraftItemEvent, Material> ITEM_CRAFT = (plugin, event, processor) -> {
        if (event.getClick() == ClickType.MIDDLE) return false;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return false;

        Player player = (Player) event.getWhoClicked();
        ItemStack craft = new ItemStack(item);
        Material type = craft.getType();

        // Идеальный вариант
        // Считаем до, считаем после, разницу записываем в прогресс хД
        boolean numberKey = event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD;

        if (event.isShiftClick() || numberKey) {
            int has = Players.countItem(player, craft);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                int now = Players.countItem(player, craft);
                int crafted = now - has;
                processor.progressObjective(player, type, crafted);
            });
        }
        else {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir() && (!cursor.isSimilar(craft) || cursor.getAmount() >= cursor.getMaxStackSize()))
                return false;

            processor.progressObjective(player, type, 1);
        }
        return true;
    };

    public static final EventHelper<InventoryClickEvent, Material> ITEM_DISENCHANT = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.GRINDSTONE) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return false;

        if (source.getEnchantments().size() == result.getEnchantments().size()) return false;

        Player player = (Player) event.getWhoClicked();
        processor.progressObjective(player, result.getType(), 1);
        return true;
    };

    public static final EventHelper<EnchantItemEvent, Material> ITEM_ENCHANT = (plugin, event, processor) -> {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();

        double modifier = (Config.JOBS_ENCHANT_MULTIPLIER_BY_LEVEL_COST.get() * event.getExpLevelCost() / 100D);

        processor.progressObjective(player, item.getType(), 1, modifier);
        return true;
    };

    public static final EventHelper<PlayerFishEvent, Material> ITEM_FISH = (plugin, event, processor) -> {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return false;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item item)) return false;

        Player player = event.getPlayer();
        ItemStack itemStack = item.getItemStack();

        // Do not count EMF fishes.
        if (Plugins.isInstalled(HookId.EVEN_MORE_FISH) && EvenMoreFishHook.isCustomFish(itemStack)) return false;

        processor.progressObjective(player, itemStack.getType(), itemStack.getAmount());
        return true;
    };

    public static final EventHelper<FurnaceExtractEvent, Material> ITEM_FURNACE = (plugin, event, processor) -> {
        Player player = event.getPlayer();

        Material material = event.getItemType();
        int amount = event.getItemAmount();

        processor.progressObjective(player, material, amount);
        return true;
    };

    public static final EventHelper<InventoryClickEvent, Material> ITEM_TRADE = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.MERCHANT) return false;

        MerchantInventory merchant = (MerchantInventory) inventory;
        MerchantRecipe recipe = merchant.getSelectedRecipe();
        if (recipe == null) return false;

        Player player = (Player) event.getWhoClicked();
        ItemStack result = recipe.getResult();
        int uses = recipe.getUses();
        int userHas = Players.countItem(player, result);

        plugin.runTask(task -> {
            int uses2 = recipe.getUses();
            if (uses2 <= uses) return;

            int amount = 1;
            if (event.isShiftClick()) {
                int resultSize = result.getAmount();
                int userNow = Players.countItem(player, result);
                int diff = userNow - userHas;
                amount = (int) ((double) diff / (double) resultSize);
            }

            processor.progressObjective(player, result.getType(), amount);
        });
        return true;
    };

    public static final EventHelper<BrewEvent, PotionEffectType> POTION_BREW = (plugin, event, processor) -> {
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

                PotionType potionType;
                if (Version.isAtLeast(Version.V1_20_R2)) {
                    for (PotionEffect effect : potionMeta.getBasePotionType().getPotionEffects()) {
                        processor.progressObjective(player, effect.getType(), item.getAmount());
                    }
                }
                else {
                    potionType = potionMeta.getBasePotionData().getType();
                    if (potionType.getEffectType() != null) {
                        processor.progressObjective(player, potionType.getEffectType(), item.getAmount());
                    }
                }

                potionMeta.getCustomEffects().forEach(effect -> {
                    processor.progressObjective(player, effect.getType(), item.getAmount());
                });
            }
        });
        return true;
    };

    public static final EventHelper<PlayerItemConsumeEvent, PotionEffectType> POTION_DRINK = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!(item.getItemMeta() instanceof PotionMeta potionMeta)) return false;

        Set<PotionEffectType> types = new HashSet<>();
        if (Version.isAtLeast(Version.V1_20_R2)) {
            if (potionMeta.getBasePotionType() != null) {
                potionMeta.getBasePotionType().getPotionEffects().forEach(e -> types.add(e.getType()));
            }
        }
        else {
            PotionType potionType = potionMeta.getBasePotionData().getType();
            if (potionType.getEffectType() != null) {
                types.add(potionType.getEffectType());
            }
        }
        potionMeta.getCustomEffects().forEach(e -> types.add(e.getType()));

        types.forEach(effectType -> {
            processor.progressObjective(player, effectType, 1);
        });
        return true;
    };

    public static final EventHelper<ProjectileLaunchEvent, EntityType> PROJECTILE_LAUNCH = (plugin, event, processor) -> {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();
        if (!(source instanceof Player player)) return false;

        processor.progressObjective(player, projectile.getType(), 1);
        return true;
    };

    public static final EventHelper<InventoryClickEvent, Enchantment> ENCHANT_REMOVE = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.GRINDSTONE) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return false;

        var sourceEnchants = new HashSet<>(source.getEnchantments().keySet());
        var resultEnchants = new HashSet<>(result.getEnchantments().keySet());
        if (sourceEnchants.size() == resultEnchants.size()) return false;

        sourceEnchants.removeAll(resultEnchants);

        Player player = (Player) event.getWhoClicked();
        sourceEnchants.forEach(enchantment -> {
            processor.progressObjective(player, enchantment, 1);
        });
        return true;
    };

    public static final EventHelper<EnchantItemEvent, Enchantment> ENCHANT_GET = (plugin, event, processor) -> {
        Player player = event.getEnchanter();

        event.getEnchantsToAdd().keySet().forEach(enchantment -> {
            processor.progressObjective(player, enchantment, 1);
        });
        return true;
    };

    public static final EventHelper<PlayerCollectedHoneyEvent, Material> HONEY_COLLECT = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        processor.progressObjective(player, event.getBlock().getType(), 1);
        return true;
    };
}
