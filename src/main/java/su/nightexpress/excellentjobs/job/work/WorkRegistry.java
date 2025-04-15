package su.nightexpress.excellentjobs.job.work;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.impl.*;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkRegistry {

    private static final String FILE_NAME = "work_types.yml";
    private static final Map<String, Work<?, ?>> BY_ID = new HashMap<>();

    private static FileConfig config;

    public static void load(@NotNull JobsPlugin plugin) {
        config = FileConfig.loadOrExtract(plugin, FILE_NAME);

        loadDefaults(plugin);

        config.saveChanges();
    }

    public static void clear() {
        BY_ID.clear();
        config = null;
    }

    public static void loadDefaults(@NotNull JobsPlugin plugin) {
        register(new BreedingWork(plugin, WorkId.BREEDING).setDisplayName("Breed").setIcon(NightItem.fromType(Material.TURTLE_EGG)));
        register(new BrewingWork(plugin, WorkId.BREWING).setDisplayName("Brew").setIcon(NightItem.fromType(Material.BREWING_STAND)));
        register(new BuildingWork(plugin, WorkId.BUILDING).setDisplayName("Place").setIcon(NightItem.fromType(Material.GRASS_BLOCK)));
        register(new CollectHoneyWork(plugin, WorkId.COLLECT_HONEY).setDisplayName("Collect Honey").setIcon(NightItem.fromType(Material.HONEY_BOTTLE)));
        register(new CraftingWork(plugin, WorkId.CRAFTING).setDisplayName("Craft").setIcon(NightItem.fromType(Material.CRAFTING_TABLE)));
        register(new DisenchantingWork(plugin, WorkId.DISENCHANTING).setDisplayName("Disenchant").setIcon(NightItem.fromType(Material.GRINDSTONE)));
        register(new DrinkingWork(plugin, WorkId.DRINKING).setDisplayName("Drink").setIcon(NightItem.fromType(Material.POTION)));
        register(new EatingWork(plugin, WorkId.EATING).setDisplayName("Eat").setIcon(NightItem.fromType(Material.APPLE)));
        register(new EnchantingWork(plugin, WorkId.ENCHANTING).setDisplayName("Enchant").setIcon(NightItem.fromType(Material.ENCHANTING_TABLE)));
        register(new FertilizingWork(plugin, WorkId.FERTILIZING).setDisplayName("Fertilize").setIcon(NightItem.fromType(Material.BONE_MEAL)));
        register(new FishingWork(plugin, WorkId.FISHING).setDisplayName("Fish").setIcon(NightItem.fromType(Material.FISHING_ROD)));
        register(new HarvestingWork(plugin, WorkId.HARVESTING).setDisplayName("Harvest").setIcon(NightItem.fromType(Material.COMPOSTER)));
        register(new DoDamageWork(plugin, WorkId.INFLICT_DAMAGE).setDisplayName("Inflict Damage").setIcon(NightItem.fromType(Material.MAGMA_CREAM)));
        register(new TakeDamageWork(plugin, WorkId.RECEIVE_DAMAGE).setDisplayName("Receive Damage").setIcon(NightItem.fromType(Material.SHIELD)));
        register(new KillEntityWork(plugin, WorkId.KILL_ENTITY).setDisplayName("Kill").setIcon(NightItem.fromType(Material.IRON_SWORD)));
        register(new MilkingWork(plugin, WorkId.MILKING).setDisplayName("Milk").setIcon(NightItem.fromType(Material.MILK_BUCKET)));
        register(new MiningWork(plugin, WorkId.MINING).setDisplayName("Mine").setIcon(NightItem.fromType(Material.DIAMOND_PICKAXE)));
        register(new TamingWork(plugin, WorkId.TAMING).setDisplayName("Tame").setIcon(NightItem.fromType(Material.LEAD)));
        register(new TradingWork(plugin, WorkId.TRADING).setDisplayName("Trade").setIcon(NightItem.fromType(Material.EMERALD)));
        register(new RenamingWork(plugin, WorkId.RENAMING).setDisplayName("Rename").setIcon(NightItem.fromType(Material.NAME_TAG)));
        register(new RepairingWork(plugin, WorkId.REPAIRING).setDisplayName("Repair").setIcon(NightItem.fromType(Material.ANVIL)));
        register(new ShearingWork(plugin, WorkId.SHEARING).setDisplayName("Shear").setIcon(NightItem.fromType(Material.SHEARS)));
        register(new ShootingWork(plugin, WorkId.SHOOTING).setDisplayName("Shoot").setIcon(NightItem.fromType(Material.BOW)));
        register(new SmeltingWork(plugin, WorkId.SMELTING).setDisplayName("Smelt").setIcon(NightItem.fromType(Material.FURNACE)));
        register(new EnchantRemoveWork(plugin, WorkId.REMOVE_ENCHANT).setDisplayName("Remove Enchant").setIcon(NightItem.fromType(Material.GRINDSTONE)));
        register(new EnchantObtainWork(plugin, WorkId.GET_ENCHANT).setDisplayName("Get Enchant").setIcon(NightItem.fromType(Material.ENCHANTED_BOOK)));
    }

    private static boolean loadSettings(@NotNull Work<?, ?> work) {
        String path = work.getId();
        if (!ConfigValue.create(path + ".Enabled", true).read(config)) return false;

        work.setDisplayName(ConfigValue.create(path + ".DisplayName", work.getDisplayName()).read(config));
        work.setDescription(ConfigValue.create(path + ".Description", work.getDescription()).read(config));
        work.setIcon(ConfigValue.create(path + ".Icon", work.getIcon()).read(config));
        return true;
    }

    public static <E extends Event, O> boolean register(@NotNull Work<E, O> work) {
        unregister(work);
        if (!loadSettings(work)) return false;

        BY_ID.put(work.getId().toLowerCase(), work);
        work.register();
        return true;
    }

    public static void unregister(@NotNull Work<?, ?> workType) {
        unregister(workType.getId());
    }

    public static void unregister(@NotNull String id) {
        var work = BY_ID.remove(id.toLowerCase());
        if (work == null) return;

        work.unregister();
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
