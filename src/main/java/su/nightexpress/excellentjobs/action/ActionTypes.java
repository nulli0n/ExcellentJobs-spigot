package su.nightexpress.excellentjobs.action;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import su.nightexpress.excellentjobs.api.event.bukkit.PlayerCollectedHoneyEvent;

public class ActionTypes {

    public static final ActionType<InventoryClickEvent, Material> ANVIL_RENAME = ActionType.create(
        "rename_item", ObjectFormatters.MATERIAL, EventHelpers.ANVIL_RENAME
    );

    public static final ActionType<InventoryClickEvent, Material> ANVIL_REPAIR = ActionType.create(
        "repair_item", ObjectFormatters.MATERIAL, EventHelpers.ANVIL_REPAIR
    );

    public static final ActionType<BlockBreakEvent, Material> BLOCK_BREAK = ActionType.create(
        "block_break", ObjectFormatters.MATERIAL, EventHelpers.BLOCK_BREAK
    );

    public static final ActionType<BlockFertilizeEvent, Material> BLOCK_FERTILIZE = ActionType.create(
        "block_fertilize", ObjectFormatters.MATERIAL, EventHelpers.BLOCK_FERTILIZE
    );

    public static final ActionType<PlayerHarvestBlockEvent, Material> BLOCK_HARVEST = ActionType.create(
        "block_harvest", ObjectFormatters.MATERIAL, EventHelpers.BLOCK_HARVEST
    );

    public static final ActionType<BlockPlaceEvent, Material> BLOCK_PLACE = ActionType.create(
        "block_place", ObjectFormatters.MATERIAL, EventHelpers.BLOCK_PLACE
    );

    public static final ActionType<EntityDamageByEntityEvent, EntityDamageEvent.DamageCause> DAMAGE_INFLICT = ActionType.create(
        "inflict_damage", ObjectFormatters.DAMAGE_CAUSE, EventHelpers.DAMAGE_INFLICT
    );

    public static final ActionType<EntityDamageEvent, EntityDamageEvent.DamageCause> DAMAGE_RECEIVE = ActionType.create(
        "receive_damage", ObjectFormatters.DAMAGE_CAUSE, EventHelpers.DAMAGE_RECEIVE
    );

    public static final ActionType<EntityBreedEvent, EntityType> ENTITY_BREED = ActionType.create(
        "breed_entity", ObjectFormatters.ENITITY_TYPE, EventHelpers.ENTITY_BREED
    );

    public static final ActionType<EntityDeathEvent, EntityType> ENTITY_KILL = ActionType.create(
        "kill_entity", ObjectFormatters.ENITITY_TYPE, EventHelpers.ENTITY_KILL
    );

    public static final ActionType<EntityDeathEvent, EntityType> ENTITY_SHOOT = ActionType.create(
        "shoot_entity", ObjectFormatters.ENITITY_TYPE, EventHelpers.ENTITY_SHOOT
    );

    public static final ActionType<PlayerShearEntityEvent, EntityType> ENTITY_SHEAR = ActionType.create(
        "shear_entity", ObjectFormatters.ENITITY_TYPE, EventHelpers.ENTITY_SHEAR
    );

    public static final ActionType<EntityTameEvent, EntityType> ENTITY_TAME = ActionType.create(
        "tame_entity", ObjectFormatters.ENITITY_TYPE, EventHelpers.ENTITY_TAME
    );

    public static final ActionType<PlayerBucketFillEvent, EntityType> ENTITY_MILK = ActionType.create(
        "milk_entity", ObjectFormatters.ENITITY_TYPE, EventHelpers.ENTITY_MILK
    );

    public static final ActionType<PlayerItemConsumeEvent, Material> ITEM_CONSUME = ActionType.create(
        "consume_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_CONSUME
    );

    public static final ActionType<CraftItemEvent, Material> ITEM_CRAFT = ActionType.create(
        "craft_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_CRAFT
    );

    public static final ActionType<InventoryClickEvent, Material> ITEM_DISENCHANT = ActionType.create(
        "disenchant_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_DISENCHANT
    );

    public static final ActionType<EnchantItemEvent, Material> ITEM_ENCHANT = ActionType.create(
        "enchant_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_ENCHANT
    );

    public static final ActionType<PlayerFishEvent, Material> ITEM_FISH = ActionType.create(
        "fish_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_FISH
    );

    public static final ActionType<FurnaceExtractEvent, Material> ITEM_FURNACE = ActionType.create(
        "smelt_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_FURNACE
    );

    public static final ActionType<InventoryClickEvent, Material> ITEM_TRADE = ActionType.create(
        "trade_item", ObjectFormatters.MATERIAL, EventHelpers.ITEM_TRADE
    );

    public static final ActionType<BrewEvent, PotionEffectType> POTION_BREW = ActionType.create(
        "brew_potion", ObjectFormatters.POTION_TYPE, EventHelpers.POTION_BREW
    );

    public static final ActionType<PlayerItemConsumeEvent, PotionEffectType> POTION_DRINK = ActionType.create(
        "drink_potion", ObjectFormatters.POTION_TYPE, EventHelpers.POTION_DRINK
    );

    public static final ActionType<ProjectileLaunchEvent, EntityType> PROJECTILE_LAUNCH = ActionType.create(
        "launch_projectile", ObjectFormatters.ENITITY_TYPE, EventHelpers.PROJECTILE_LAUNCH
    );

    public static final ActionType<InventoryClickEvent, Enchantment> ENCHANT_REMOVE = ActionType.create(
        "remove_enchant", ObjectFormatters.ENCHANTMENT, EventHelpers.ENCHANT_REMOVE
    );

    public static final ActionType<EnchantItemEvent, Enchantment> ENCHANT_GET = ActionType.create(
        "get_enchant", ObjectFormatters.ENCHANTMENT, EventHelpers.ENCHANT_GET
    );

    public static final ActionType<PlayerCollectedHoneyEvent, Material> HONEY_COLLECT = ActionType.create(
        "collect_honey", ObjectFormatters.MATERIAL, EventHelpers.HONEY_COLLECT
    );
}
