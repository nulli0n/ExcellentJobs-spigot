package su.nightexpress.excellentjobs.job.work;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.impl.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkRegistry {

    private static final Map<String, Work<?, ?>> BY_ID = new HashMap<>();

    //public static final WorkType<EntityBreedEvent, EntityType>             BREEDING       = WorkType.forEntity("breed_entity", WorkHandlers.ENTITY_BREED);
    //public static final WorkType<BrewEvent, PotionEffectType>              BREWING        = WorkType.forEffect("brew_potion", WorkHandlers.POTION_BREW);
    //public static final WorkType<BlockPlaceEvent, Material>                BUILDING       = WorkType.forMaterial("block_place", WorkHandlers.BLOCK_PLACE);
    //public static final WorkType<PlayerCollectedHoneyEvent, Material>      COLLECT_HONEY  = WorkType.forMaterial("collect_honey", WorkHandlers.HONEY_COLLECT);
    //public static final WorkType<CraftItemEvent, Material>                 CRAFTING       = WorkType.forMaterial("craft_item", WorkHandlers.ITEM_CRAFT);
    //public static final WorkType<InventoryClickEvent, Material>            DISENCHANTING  = WorkType.forMaterial("disenchant_item", WorkHandlers.ITEM_DISENCHANT);
    //public static final WorkType<PlayerItemConsumeEvent, PotionEffectType> DRINKING       = WorkType.forEffect("drink_potion", WorkHandlers.POTION_DRINK);
    //public static final WorkType<PlayerItemConsumeEvent, Material>         EATING         = WorkType.forMaterial("consume_item", WorkHandlers.ITEM_CONSUME);
    //public static final WorkType<EnchantItemEvent, Material>               ENCHANTING     = WorkType.forMaterial("enchant_item", WorkHandlers.ITEM_ENCHANT);
    //public static final WorkType<BlockFertilizeEvent, Material>            FERTILIZING    = WorkType.forMaterial("block_fertilize", WorkHandlers.BLOCK_FERTILIZE);
    //public static final WorkType<PlayerFishEvent, Material>                FISHING        = WorkType.forMaterial("fish_item", WorkHandlers.ITEM_FISH);
    //public static final WorkType<PlayerHarvestBlockEvent, Material>        HARVESTING     = WorkType.forMaterial("block_harvest", WorkHandlers.BLOCK_HARVEST);
    //public static final WorkType<EntityDamageEvent, DamageType>            INFLICT_DAMAGE = WorkType.forDamageType("inflict_damage", WorkHandlers.DAMAGE_INFLICT);
    //public static final WorkType<EntityDeathEvent, EntityType>             KILL_ENTITY    = WorkType.forEntity("kill_entity", WorkHandlers.ENTITY_KILL);
    //public static final WorkType<PlayerBucketFillEvent, EntityType>        MILKING        = WorkType.forEntity("milk_entity", WorkHandlers.ENTITY_MILK);
    //public static final WorkType<BlockBreakEvent, Material>                MINING         = WorkType.forMaterial("block_break", WorkHandlers.BLOCK_BREAK);
    //public static final WorkType<EntityTameEvent, EntityType>              TAMING         = WorkType.forEntity("tame_entity", WorkHandlers.ENTITY_TAME);
    //public static final WorkType<InventoryClickEvent, Material>            TRADING        = WorkType.forMaterial("trade_item", WorkHandlers.ITEM_TRADE);
    //public static final WorkType<EntityDamageEvent, DamageType>            RECEIVE_DAMAGE = WorkType.forDamageType("receive_damage", WorkHandlers.DAMAGE_RECEIVE);
    //public static final WorkType<InventoryClickEvent, Material>            RENAMING       = WorkType.forMaterial("rename_item", WorkHandlers.ANVIL_RENAME);
    //public static final WorkType<InventoryClickEvent, Material>            REPAIRING      = WorkType.forMaterial("repair_item", WorkHandlers.ANVIL_REPAIR);
    //public static final WorkType<PlayerShearEntityEvent, EntityType>       SHEARING       = WorkType.forEntity("shear_entity", WorkHandlers.ENTITY_SHEAR);
    //public static final WorkType<ProjectileLaunchEvent, EntityType>        SHOOTING       = WorkType.forEntity("launch_projectile", WorkHandlers.PROJECTILE_LAUNCH);
    //public static final WorkType<FurnaceExtractEvent, Material>            SMELTING       = WorkType.forMaterial("smelt_item", WorkHandlers.ITEM_FURNACE);

    //public static final WorkType<InventoryClickEvent, Enchantment> REMOVE_ENCHANT = WorkType.forEnchantment("remove_enchant", WorkHandlers.ENCHANT_REMOVE);
    //public static final WorkType<EnchantItemEvent, Enchantment>    GET_ENCHANT    = WorkType.forEnchantment("get_enchant", WorkHandlers.ENCHANT_GET);

    public static void load(@NotNull JobsPlugin plugin) {
        loadDefaults(plugin);
        //loadIntegrations(plugin);
        //loadSettings(plugin);
    }

    public static void clear() {
        BY_ID.clear();
    }

    public static void loadDefaults(@NotNull JobsPlugin plugin) {
        register(new BreedingWork(plugin, WorkId.BREEDING));
        register(new BrewingWork(plugin, WorkId.BREWING));
        register(new BuildingWork(plugin, WorkId.BUILDING));
        register(new CollectHoneyWork(plugin, WorkId.COLLECT_HONEY));
        register(new CraftingWork(plugin, WorkId.CRAFTING));
        register(new DisenchantingWork(plugin, WorkId.DISENCHANTING));
        register(new DrinkingWork(plugin, WorkId.DRINKING));
        register(new EatingWork(plugin, WorkId.EATING));
        register(new EnchantingWork(plugin, WorkId.ENCHANTING));
        register(new FertilizingWork(plugin, WorkId.FERTILIZING));
        register(new FishingWork(plugin, WorkId.FISHING));
        register(new HarvestingWork(plugin, WorkId.HARVESTING));
        register(new DoDamageWork(plugin, WorkId.INFLICT_DAMAGE));
        register(new TakeDamageWork(plugin, WorkId.RECEIVE_DAMAGE));
        register(new KillEntityWork(plugin, WorkId.KILL_ENTITY));
        register(new MilkingWork(plugin, WorkId.MILKING));
        register(new MiningWork(plugin, WorkId.MINING));
        register(new TamingWork(plugin, WorkId.TAMING));
        register(new TradingWork(plugin, WorkId.TRADING));
        register(new RenamingWork(plugin, WorkId.RENAMING));
        register(new RepairingWork(plugin, WorkId.REPAIRING));
        register(new ShearingWork(plugin, WorkId.SHEARING));
        register(new ShootingWork(plugin, WorkId.SHOOTING));
        register(new SmeltingWork(plugin, WorkId.SMELTING));
        register(new EnchantRemoveWork(plugin, WorkId.REMOVE_ENCHANT));
        register(new EnchantObtainWork(plugin, WorkId.GET_ENCHANT));

        //register(RENAMING);
        //register(REPAIRING);
        //register(MINING);
        //register(FERTILIZING);
        //register(HARVESTING);
        //register(BUILDING);
        //register(INFLICT_DAMAGE);
        //register(RECEIVE_DAMAGE);
        //register(BREEDING);
        //register(KILL_ENTITY);
        //register(SHEARING);
        //register(TAMING);
        //register(MILKING);
        //register(EATING);
        //register(CRAFTING);
        //register(DISENCHANTING);
        //register(ENCHANTING);
        //register(FISHING);
        //register(SMELTING);
        //register(TRADING);
        //register(BREWING);
        //register(DRINKING);
        //register(SHOOTING);
        //(REMOVE_ENCHANT);
        //register(GET_ENCHANT);
        //register(COLLECT_HONEY);

        // Compatibility for old version configs.
        //register(new WorkType<>("shoot_entity", WorkFormatters.ENITITY_TYPE, WorkHandlers.ENTITY_KILL));
    }

//    public static void loadIntegrations(@NotNull JobsPlugin plugin) {
//        registerExternal(plugin, HookPlugin.MYTHIC_MOBS, MythicMobsHook::register);
//        registerExternal(plugin, HookPlugin.EVEN_MORE_FISH, EvenMoreFishHook::register);
//    }
//
//    private static void loadSettings(@NotNull JobsPlugin plugin) {
//        FileConfig config = plugin.getLang();
//
//        getValues().forEach(workType -> {
//            String path = "Job.Action_Types." + workType.getId();
//            workType.setDisplayName(ConfigValue.create(path + ".DisplayName", workType.getDisplayName()).read(config));
//        });
//    }
//
//    private static void registerExternal(@NotNull JobsPlugin plugin, @NotNull String name, @NotNull Runnable runnable) {
//        if (Plugins.isInstalled(name)) {
//            runnable.run();
//            plugin.info("Found " + name + "! Registering new objective types...");
//        }
//    }

//    @NotNull
//    public static <E extends Event, O> WorkType<E, O> register(@NotNull String id, @NotNull WorkFormatter<O> formatter, @NotNull WorkHandler<E, O> handler) {
//        return register(new WorkType<>(id, formatter, handler));
//    }
//
//    @NotNull
//    public static <E extends Event, O> WorkType<E, O> register(@NotNull WorkType<E, O> workType) {
//        BY_ID.put(workType.getId().toLowerCase(), workType);
//        return workType;
//    }
//
//    public static boolean unregister(@NotNull WorkType<?, ?> workType) {
//        return unregister(workType.getId());
//    }
//
//    public static boolean unregister(@NotNull String id) {
//        return BY_ID.remove(id.toLowerCase()) != null;
//    }
//
//    public static boolean isPresent(@NotNull String name) {
//        return getByName(name) != null;
//    }
//
//    @Nullable
//    public static WorkType<?, ?> getByName(@NotNull String name) {
//        return BY_ID.get(name.toLowerCase());
//    }
//
//    @NotNull
//    public static Set<WorkType<?, ?>> getValues() {
//        return new HashSet<>(BY_ID.values());
//    }

    @NotNull
    public static <E extends Event, O> Work<E, O> register(@NotNull Work<E, O> work) {
        unregister(work);

        BY_ID.put(work.getId().toLowerCase(), work);
        work.register();
        return work;
    }

    public static boolean unregister(@NotNull Work<?, ?> workType) {
        return unregister(workType.getId());
    }

    public static boolean unregister(@NotNull String id) {
        var work = BY_ID.remove(id.toLowerCase());
        if (work == null) return false;

        work.unregister();
        return true;
    }

    public static boolean isPresent(@NotNull String name) {
        return getByName(name) != null;
    }

    @Nullable
    public static Work<?, ?> getByName(@NotNull String name) {
        return BY_ID.get(name.toLowerCase());
    }

    @NotNull
    public static Set<Work<?, ?>> getValues() {
        return new HashSet<>(BY_ID.values());
    }
}
