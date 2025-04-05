package su.nightexpress.excellentjobs.util;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.impl.ObjectiveReward;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.excellentjobs.job.work.WorkId;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_GRAY;

public class JobCreator {

    private static final ObjectiveReward MONEY_LOW    = new ObjectiveReward(100D, 0.05, 0.1);
    private static final ObjectiveReward MONEY_MEDIUM = new ObjectiveReward(100D, 0.5, 1.5);
    private static final ObjectiveReward MONEY_HIGH   = new ObjectiveReward(100D, 2.0, 4.0);
    private static final ObjectiveReward MONEY_BEST   = new ObjectiveReward(100D, 5.0, 10.0);

    private static final ObjectiveReward XP_LOW    = new ObjectiveReward(25D, 1, 2);
    private static final ObjectiveReward XP_MEDIUM = new ObjectiveReward(75D, 3, 5);
    private static final ObjectiveReward XP_HIGH   = new ObjectiveReward(100D, 10, 15);
    private static final ObjectiveReward XP_BEST   = new ObjectiveReward(100D, 20, 30);

    private final JobsPlugin plugin;

    public JobCreator(@NotNull JobsPlugin plugin) {
        this.plugin = plugin;
    }

    public void createDefaultJobs() {
        this.createMinerJob();
        this.createDiggerJob();
        this.createLumberjackJob();
        this.createFarmerJob();
        this.createFisherJob();
        this.createHunterJob();
        this.createEnchanterJob();
        this.createBuilderJob();
    }

    private void createJob(@NotNull String id, @NotNull Consumer<Job> consumer) {
        File file = new File(plugin.getDataFolder() + Config.DIR_JOBS + id, Job.CONFIG_NAME);
        if (file.exists()) return;

        FileUtil.create(file);

        Job job = new Job(plugin, file, id);
        consumer.accept(job);
        job.setName(StringUtil.capitalizeUnderscored(id));
        job.setPermissionRequired(false);
        job.setInitialState(JobState.INACTIVE);
        job.setInitialXP(1000);
        job.setXPFactor(1.093);
        job.setMaxLevel(100);
        job.setMaxSecondaryLevel(30);
        job.setProgressBarColor(BarColor.GREEN);
        //job.getPaymentMultiplier().put(CurrencyId.VAULT, Modifier.add(0D, 0.01, 1D));
        job.setPaymentMultiplier(JobUtils.getDefaultPaymentModifier());
        job.setXPMultiplier(JobUtils.getDefaultXPModifier());
        job.getDailyPaymentLimits().put(CurrencyId.VAULT, Modifier.add(-1D, 0D, 0D));
        job.setXPDailyLimits(Modifier.add(-1D, 0D, 0D));
        job.save();
    }

    private void createMinerJob() {
        createJob("miner", job -> {
            job.setIcon(NightItem.asCustomHead("1e1d4bc469d29d22a7ef6d21a61b451291f21bf51fd167e7fd07b719512e87a1"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Dig deep, gather precious resources,"),
                LIGHT_GRAY.wrap("and conquer the underground"),
                LIGHT_GRAY.wrap("for money!"))
            );
        });

        createMinerObjectives();
    }

    private void createDiggerJob() {
        createJob("digger", job -> {
            job.setIcon(NightItem.asCustomHead("9503c00890326605067ff16fffbc2d0502251b3680c1acc1d68cd3d064d0577"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Excavate vast landscapes"),
                LIGHT_GRAY.wrap("and uncover hidden treasures"),
                LIGHT_GRAY.wrap("for money!"))
            );
        });

        createDiggerObjectives();
    }

    private void createLumberjackJob() {
        createJob("lumberjack", job -> {
            job.setIcon(NightItem.asCustomHead("43d09000c308c91a0e7c741efd85e9fd32866b6eb851c95901500b3ece6727de"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Harvest towering forests and"),
                LIGHT_GRAY.wrap("master the art of woodcutting"),
                LIGHT_GRAY.wrap("for money!"))
            );
        });

        createLumberjackObjectives();
    }

    private void createFarmerJob() {
        createJob("farmer", job -> {
            job.setIcon(NightItem.asCustomHead("64352b979a489dfc8ba7b1e1259763843d772f39f1420f8672537aa7522dc1bf"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Tend to fertile lands,"),
                LIGHT_GRAY.wrap("cultivate crops, and flourish"),
                LIGHT_GRAY.wrap("for money!"))
            );
        });

        createFarmerObjectives();
    }

    private void createFisherJob() {
        createJob("fisher", job -> {
            job.setIcon(NightItem.asCustomHead("d804e42ec9b07fce1ce0058b78df5763f6e410d9ce82ef1ebb9597a152b6d4c8"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Cast your line into shimmering"),
                LIGHT_GRAY.wrap("waters, reel in bountiful catches,"),
                LIGHT_GRAY.wrap("and excel for money!"))
            );
        });

        createFisherObjectives();
    }

    private void createHunterJob() {
        createJob("hunter", job -> {
            job.setIcon(NightItem.asCustomHead("5dcf2c198f61df5a6d0ba97dbf90c337995405c17396c016c85f6f3fea52c906"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Brave the wilderness, track elusive"),
                LIGHT_GRAY.wrap("prey, and thrive for money!"))
            );
        });

        createHunterObjectives();
    }

    private void createEnchanterJob() {
        createJob("enchanter", job -> {
            job.setIcon(NightItem.asCustomHead("70fbb9178d9d468e3c9735ee571b7baa54338f7f831f7f4e60ca9c8d14870c7"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Get levels and enhance your"),
                LIGHT_GRAY.wrap("gear with enchantments for money!"))
            );
        });

        createEnchanterObjectives();
    }

    private void createBuilderJob() {
        createJob("builder", job -> {
            job.setIcon(NightItem.asCustomHead("abfaa6bb0194de1b576776a4eabcb46cb1f0a288c678b0ad2500deda118ce958"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Build anything you like"),
                LIGHT_GRAY.wrap("and get paid for it!"))
            );
        });

        createBuilderObjectives();
    }

    private void createMinerObjectives() {
        String jobId = "miner";
        String type = WorkId.MINING;
        List<JobObjective> objectives = new ArrayList<>();

        Set<Material> stoneItems = new HashSet<>(Tag.BASE_STONE_OVERWORLD.getValues());
        stoneItems.add(Material.COBBLESTONE);
        stoneItems.add(Material.MOSSY_COBBLESTONE);

        objectives.add(createMaterialObjective("stones", type, stoneItems,
            new ItemStack(Material.STONE),
            MONEY_LOW.multiply(1.35), XP_LOW, 1)
        );

        objectives.add(createMaterialObjective("stone_bricks", type, Tag.STONE_BRICKS.getValues(),
            new ItemStack(Material.STONE_BRICKS),
            MONEY_MEDIUM, XP_MEDIUM, 15)
        );

        Set<Material> commonOreItems = new HashSet<>();
        commonOreItems.addAll(Tag.COAL_ORES.getValues());
        commonOreItems.addAll(Tag.COPPER_ORES.getValues());
        commonOreItems.addAll(Tag.IRON_ORES.getValues());

        Set<Material> rareOreItems = new HashSet<>();
        rareOreItems.addAll(Tag.GOLD_ORES.getValues());
        rareOreItems.addAll(Tag.REDSTONE_ORES.getValues());
        rareOreItems.addAll(Tag.LAPIS_ORES.getValues());
        rareOreItems.remove(Material.NETHER_GOLD_ORE);

        Set<Material> deepOreItems = new HashSet<>();
        deepOreItems.addAll(Tag.DIAMOND_ORES.getValues());
        deepOreItems.addAll(Tag.EMERALD_ORES.getValues());

        Set<Material> netherOreItems = Lists.newSet(Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE);

        objectives.add(createMaterialObjective("common_ores", type, commonOreItems,
            new ItemStack(Material.COAL_ORE),
            MONEY_MEDIUM, XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("rare_ores", type, rareOreItems,
            new ItemStack(Material.GOLD_ORE),
            MONEY_HIGH, XP_HIGH, 1)
        );

        objectives.add(createMaterialObjective("deepest_ores", type, deepOreItems,
            new ItemStack(Material.DIAMOND_ORE),
            MONEY_BEST, XP_BEST, 1)
        );

        objectives.add(createMaterialObjective("nether_ores", type, netherOreItems,
            new ItemStack(Material.NETHER_QUARTZ_ORE),
            MONEY_HIGH, XP_HIGH, 1)
        );

        objectives.add(createMaterialObjective("terracotta", type, Tag.TERRACOTTA.getValues(),
            new ItemStack(Material.TERRACOTTA),
            MONEY_MEDIUM, XP_MEDIUM, 15)
        );

        objectives.add(createMaterialObjective("nylium", type, Tag.NYLIUM.getValues(),
            new ItemStack(Material.CRIMSON_NYLIUM),
            MONEY_MEDIUM, XP_MEDIUM, 25)
        );

        generateObjectives(jobId, objectives);
    }

    private void createDiggerObjectives() {
        String jobId = "digger";
        String type = WorkId.MINING;
        List<JobObjective> objectives = new ArrayList<>();

        Set<Material> groundItems = new HashSet<>();
        groundItems.addAll(Tag.DIRT.getValues());
        groundItems.addAll(Tag.SAND.getValues());
        groundItems.addAll(Tag.ANIMALS_SPAWNABLE_ON.getValues());
        groundItems.addAll(Tag.AXOLOTLS_SPAWNABLE_ON.getValues());
        groundItems.addAll(Tag.SNOW.getValues());

        Set<Material> groundItems2 = Lists.newSet(
            Material.MYCELIUM,
            Material.GRAVEL, Material.CLAY, Material.SOUL_SAND
        );

        groundItems.removeAll(groundItems2);

        objectives.add(createMaterialObjective("common_ground_blocks", type, groundItems,
            new ItemStack(Material.GRASS_BLOCK),
            MONEY_LOW.multiply(0.7), XP_LOW, 1)
        );

        objectives.add(createMaterialObjective("rare_ground_blocks", type, groundItems2,
            new ItemStack(Material.CLAY),
            MONEY_MEDIUM, XP_MEDIUM, 1)
        );

        generateObjectives(jobId, objectives);
    }

    private void createLumberjackObjectives() {
        String jobId = "lumberjack";
        String type = WorkId.MINING;
        List<JobObjective> objectives = new ArrayList<>();

        objectives.add(createMaterialObjective("acacia", type, Tag.ACACIA_LOGS.getValues(),
            new ItemStack(Material.ACACIA_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("birch", type, Tag.BIRCH_LOGS.getValues(),
            new ItemStack(Material.BIRCH_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("cherry", type, Tag.CHERRY_LOGS.getValues(),
            new ItemStack(Material.CHERRY_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("crimson", type, Tag.CRIMSON_STEMS.getValues(),
            new ItemStack(Material.CRIMSON_STEM),
            MONEY_MEDIUM.multiply(3), XP_MEDIUM.multiply(1.5), 1)
        );

        objectives.add(createMaterialObjective("dark_oak", type, Tag.DARK_OAK_LOGS.getValues(),
            new ItemStack(Material.DARK_OAK_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("jungle", type, Tag.JUNGLE_LOGS.getValues(),
            new ItemStack(Material.JUNGLE_LOG),
            MONEY_MEDIUM.multiply(3), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("mangrove", type, Tag.MANGROVE_LOGS.getValues(),
            new ItemStack(Material.MANGROVE_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("oak", type, Tag.OAK_LOGS.getValues(),
            new ItemStack(Material.OAK_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("spruce", type, Tag.SPRUCE_LOGS.getValues(),
            new ItemStack(Material.SPRUCE_LOG),
            MONEY_MEDIUM.multiply(2), XP_MEDIUM, 1)
        );

        objectives.add(createMaterialObjective("warped", type, Tag.WARPED_STEMS.getValues(),
            new ItemStack(Material.WARPED_STEM),
            MONEY_MEDIUM.multiply(3), XP_MEDIUM.multiply(1.5), 1)
        );

        objectives.add(createMaterialObjective("leaves", type, Tag.LEAVES.getValues(),
            new ItemStack(Material.OAK_LEAVES),
            MONEY_LOW.multiply(1.5), XP_LOW, 1)
        );

        objectives.forEach(jobObjective -> jobObjective.getObjects().removeIf(o -> o.endsWith("_WOOD")));

        generateObjectives(jobId, objectives);
    }

    private void createFarmerObjectives() {
        String jobId = "farmer";
        String blockBreak = WorkId.MINING;
        String blockHarvest = WorkId.HARVESTING;
        String breed = WorkId.BREEDING;
        String milk = WorkId.MILKING;
        List<JobObjective> objectives = new ArrayList<>();

        Set<Material> crops = new HashSet<>(Tag.CROPS.getValues());
        crops.remove(Material.MELON_STEM);
        crops.remove(Material.PUMPKIN_STEM);
        crops.add(Material.MELON);
        crops.add(Material.PUMPKIN);

        objectives.add(createMaterialObjective("honey", WorkId.COLLECT_HONEY, Lists.newSet(Material.BEEHIVE, Material.BEE_NEST),
            new ItemStack(Material.HONEY_BOTTLE),
            MONEY_HIGH.multiply(1.25), XP_HIGH.multiply(0.45), 1));

        objectives.add(createEntityObjective("milks", milk, Lists.newSet(EntityType.COW, EntityType.MOOSHROOM, EntityType.GOAT),
            new ItemStack(Material.MILK_BUCKET),
            MONEY_MEDIUM.multiply(2), XP_HIGH.multiply(0.35), 1));

        objectives.add(createMaterialObjective("crops", blockBreak, crops,
            new ItemStack(Material.WHEAT),
            MONEY_MEDIUM.multiply(2), XP_HIGH.multiply(0.35), 1)
        );

        objectives.add(createMaterialObjective("sugar_cane", blockBreak, Lists.newSet(Material.SUGAR_CANE),
            new ItemStack(Material.SUGAR_CANE),
            MONEY_MEDIUM.multiply(1.5), XP_HIGH.multiply(0.35), 1)
        );

        objectives.add(createMaterialObjective("bamboo", blockBreak, Lists.newSet(Material.BAMBOO),
            new ItemStack(Material.BAMBOO),
            MONEY_MEDIUM.multiply(1.5), XP_HIGH.multiply(0.35), 1)
        );

        objectives.add(createMaterialObjective("berries", blockHarvest, Lists.newSet(Material.GLOW_BERRIES, Material.SWEET_BERRIES),
            new ItemStack(Material.SWEET_BERRIES),
            MONEY_MEDIUM.multiply(2), XP_HIGH.multiply(0.35), 1)
        );

        objectives.add(createMaterialObjective("flowers", blockBreak, Tag.FLOWERS.getValues(),
            new ItemStack(Material.POPPY),
            MONEY_MEDIUM.multiply(0.75), XP_LOW, 1)
        );

        Set<EntityType> breedItems = Lists.newSet(
            EntityType.PIG, EntityType.COW, EntityType.RABBIT, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE,
            EntityType.BEE, EntityType.CAT, EntityType.CHICKEN, EntityType.GOAT, EntityType.LLAMA, EntityType.MOOSHROOM,
            EntityType.PANDA, EntityType.SHEEP, EntityType.WOLF, EntityType.TURTLE
        );

        objectives.add(createEntityObjective("breed_animals", breed, breedItems,
            new ItemStack(Material.PIG_SPAWN_EGG),
            MONEY_BEST.multiply(1.5), XP_MEDIUM.multiply(1.35), 1)
        );

        generateObjectives(jobId, objectives);
    }

    private void createFisherObjectives() {
        String jobId = "fisher";
        String blockBreak = WorkId.FISHING;
        List<JobObjective> objectives = new ArrayList<>();

        objectives.add(createMaterialObjective("craft_fishing_rod", WorkId.CRAFTING,
            Lists.newSet(Material.FISHING_ROD),
            new ItemStack(Material.FISHING_ROD),
            MONEY_MEDIUM.multiply(0.5), XP_LOW, 1)
        );

        Set<Material> fishes = new HashSet<>(Tag.ITEMS_FISHES.getValues());
        fishes.remove(Material.COOKED_COD);
        fishes.remove(Material.COOKED_SALMON);

        objectives.add(createMaterialObjective("fish", blockBreak, fishes,
            new ItemStack(Material.COD),
            MONEY_BEST, XP_MEDIUM.multiply(1.25), 1)
        );

        Set<Material> treasureItems = Lists.newSet(
            Material.BOW, Material.ENCHANTED_BOOK, Material.FISHING_ROD,
            Material.NAME_TAG, Material.NAUTILUS_SHELL, Material.SADDLE);

        objectives.add(createMaterialObjective("treasures", blockBreak, treasureItems,
            new ItemStack(Material.ENCHANTED_BOOK),
            MONEY_BEST, XP_HIGH.multiply(0.7), 1)
        );

        Set<Material> junkItems = Lists.newSet(
            Material.LILY_PAD, Material.BOWL, Material.LEATHER, Material.LEATHER_BOOTS,
            Material.ROTTEN_FLESH, Material.STICK, Material.STRING, Material.POTION,
            Material.BONE, Material.INK_SAC, Material.TRIPWIRE_HOOK
        );

        objectives.add(createMaterialObjective("junk", blockBreak, junkItems,
            new ItemStack(Material.ROTTEN_FLESH),
            MONEY_BEST.multiply(0.85), XP_HIGH.multiply(0.5), 1)
        );

        generateObjectives(jobId, objectives);
    }

    private void createHunterObjectives() {
        String jobId = "hunter";
        String entityKill = WorkId.KILL_ENTITY;
        List<JobObjective> objectives = new ArrayList<>();

        objectives.add(createEntityObjective("raiders", entityKill, Tag.ENTITY_TYPES_RAIDERS.getValues(),
            new ItemStack(Material.CROSSBOW),
            MONEY_BEST.multiply(0.85), XP_HIGH.multiply(0.65), 1)
        );

        objectives.add(createEntityObjective("skeletons", entityKill, Tag.ENTITY_TYPES_SKELETONS.getValues(),
            new ItemStack(Material.BONE),
            MONEY_BEST.multiply(0.75), XP_HIGH.multiply(0.45), 1)
        );

        Set<EntityType> zombieItems = Lists.newSet(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIE_HORSE, EntityType.ZOMBIFIED_PIGLIN,
            EntityType.GIANT, EntityType.HUSK
        );

        objectives.add(createEntityObjective("zombies", entityKill, zombieItems,
            new ItemStack(Material.ZOMBIE_HEAD),
            MONEY_BEST.multiply(0.65), XP_HIGH.multiply(0.45), 1)
        );

        Set<EntityType> hostileItems = Lists.newSet(
            EntityType.BLAZE, EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.SLIME,
            EntityType.ENDERMITE, EntityType.ENDERMAN, EntityType.PIGLIN_BRUTE, EntityType.PIG,
            EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.CAVE_SPIDER, EntityType.SPIDER,
            EntityType.CREEPER, EntityType.DROWNED, EntityType.PHANTOM, EntityType.VEX,
            EntityType.SILVERFISH, EntityType.BREEZE
        );

        objectives.add(createEntityObjective("hostile", entityKill, hostileItems,
            new ItemStack(Material.SKELETON_SKULL),
            MONEY_BEST.multiply(0.75), XP_MEDIUM.multiply(1.5), 1)
        );

        Set<EntityType> animalItems = Lists.newSet(
            EntityType.PIG, EntityType.COW, EntityType.SHEEP, EntityType.GOAT,
            EntityType.CHICKEN, EntityType.HORSE, EntityType.MULE, EntityType.DONKEY,
            EntityType.MOOSHROOM, EntityType.TURTLE, EntityType.CAT,
            EntityType.FOX, EntityType.LLAMA, EntityType.OCELOT, EntityType.PANDA,
            EntityType.PARROT, EntityType.POLAR_BEAR, EntityType.RABBIT, EntityType.TRADER_LLAMA,
            EntityType.WOLF, EntityType.BEE, EntityType.BAT, EntityType.CAMEL, EntityType.FROG, EntityType.ARMADILLO, EntityType.SNIFFER
        );

        objectives.add(createEntityObjective("animals", entityKill, animalItems,
            new ItemStack(Material.CARROT),
            MONEY_HIGH.multiply(1.35), XP_MEDIUM.multiply(1.35), 1)
        );

        Set<EntityType> fishItems = Lists.newSet(
            EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.SQUID,
            EntityType.GLOW_SQUID, EntityType.AXOLOTL, EntityType.TADPOLE
        );

        objectives.add(createEntityObjective("fishes", entityKill, fishItems,
            new ItemStack(Material.COD),
            MONEY_MEDIUM.multiply(1.5), XP_MEDIUM, 1)
        );

        generateObjectives(jobId, objectives);
    }

    private void createEnchanterObjectives() {
        String jobId = "enchanter";
        String itemEnchantType = WorkId.ENCHANTING;
        List<JobObjective> objectives = new ArrayList<>();

        Set<Material> leatherItems = Lists.newSet(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
        );
        objectives.add(createMaterialObjective("leather_armor", itemEnchantType, leatherItems,
            new ItemStack(Material.LEATHER_CHESTPLATE),
            MONEY_BEST.multiply(2), XP_BEST.multiply(0.5), 1)
        );


        Set<Material> goldenItems = Lists.newSet(
            Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE,
            Material.GOLDEN_HOE, Material.GOLDEN_SWORD, Material.GOLDEN_SHOVEL,
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS
        );
        objectives.add(createMaterialObjective("golden_items", itemEnchantType, goldenItems,
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            MONEY_BEST.multiply(2.25), XP_BEST.multiply(0.55), 1)
        );


        Set<Material> chainmailItems = Lists.newSet(
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS
        );
        objectives.add(createMaterialObjective("chainmail_armor", itemEnchantType, chainmailItems,
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            MONEY_BEST.multiply(2.5), XP_BEST.multiply(0.6), 1)
        );


        Set<Material> ironItems = Lists.newSet(
            Material.IRON_AXE, Material.IRON_PICKAXE,
            Material.IRON_HOE, Material.IRON_SWORD, Material.IRON_SHOVEL,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS
        );
        objectives.add(createMaterialObjective("iron_items", itemEnchantType, ironItems,
            new ItemStack(Material.IRON_CHESTPLATE),
            MONEY_BEST.multiply(2.75), XP_BEST.multiply(0.65), 1)
        );


        Set<Material> diamondItems = Lists.newSet(
            Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
            Material.DIAMOND_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_SHOVEL,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
        );
        objectives.add(createMaterialObjective("diamond_items", itemEnchantType, diamondItems,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            MONEY_BEST.multiply(3), XP_BEST.multiply(0.7), 1)
        );


        Set<Material> netherItems = Lists.newSet(
            Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE,
            Material.NETHERITE_HOE, Material.NETHERITE_SWORD, Material.NETHERITE_SHOVEL,
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS
        );
        objectives.add(createMaterialObjective("netherite_items", itemEnchantType, netherItems,
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            MONEY_BEST.multiply(3.25), XP_BEST.multiply(0.75), 1)
        );


        Set<Material> otherItems = Lists.newSet(
            Material.BOW, Material.CROSSBOW, Material.TURTLE_HELMET,
            Material.BOOK, Material.TRIDENT
        );
        objectives.add(createMaterialObjective("other_items", itemEnchantType, otherItems,
            new ItemStack(Material.ENCHANTED_BOOK),
            MONEY_BEST.multiply(2), XP_BEST.multiply(0.5), 1)
        );

        generateObjectives(jobId, objectives);
    }

    private void createBuilderObjectives() {
        String jobId = "builder";
        List<JobObjective> objectives = new ArrayList<>();

        objectives.add(buildObjective("placeable_blocks", WorkId.BUILDING, Lists.newSet(Placeholders.WILDCARD),
            new ItemStack(Material.SCAFFOLDING),
            MONEY_LOW, XP_LOW, 1)
        );

        generateObjectives(jobId, objectives);
    }

    private void generateObjectives(@NotNull String jobId, @NotNull Collection<JobObjective> objectives) {
        File file = new File(this.plugin.getDataFolder() + Config.DIR_JOBS + jobId, Job.OBJECTIVES_CONFIG_NAME);
        if (file.exists()) return;

        FileConfig config = new FileConfig(file);
        for (JobObjective objective : objectives) {
            objective.write(config, objective.getId());
        }
        config.saveChanges();
    }

    @NotNull
    private static JobObjective createEntityObjective(@NotNull String id,
                                                        @NotNull String type,
                                                        @NotNull Set<EntityType> items,
                                                        @NotNull ItemStack icon,
                                                        @NotNull ObjectiveReward money,
                                                        @NotNull ObjectiveReward xp,
                                                        int unlockLevel) {
        return createObjective(id, type, WorkFormatters.ENITITY_TYPE, items, icon, money, xp, unlockLevel);
    }

    @NotNull
    private static JobObjective createMaterialObjective(@NotNull String id,
                                                    @NotNull String type,
                                                    @NotNull Set<Material> items,
                                                    @NotNull ItemStack icon,
                                                    @NotNull ObjectiveReward money,
                                                    @NotNull ObjectiveReward xp,
                                                    int unlockLevel) {
        return createObjective(id, type, WorkFormatters.MATERIAL, items, icon, money, xp, unlockLevel);
    }

    @NotNull
    private static <O> JobObjective createObjective(@NotNull String id,
                                                   @NotNull String type,
                                                   @NotNull WorkFormatter<O> formatter,
                                                   @NotNull Set<O> items,
                                                   @NotNull ItemStack icon,
                                                   @NotNull ObjectiveReward money,
                                                   @NotNull ObjectiveReward xp,
                                                   int unlockLevel) {
        Set<String> objects = items.stream().map(formatter::getName).sorted(String::compareTo).collect(Collectors.toCollection(LinkedHashSet::new));

        return buildObjective(id, type, objects, icon, money, xp, unlockLevel);
    }

    @NotNull
    private static JobObjective buildObjective(@NotNull String id,
                                                    @NotNull String type,
                                                    @NotNull Set<String> items,
                                                    @NotNull ItemStack icon,
                                                    @NotNull ObjectiveReward money,
                                                    @NotNull ObjectiveReward xp,
                                                    int unlockLevel) {
        Map<String, ObjectiveReward> paymentMap = new HashMap<>();
        paymentMap.put(CurrencyId.VAULT, money);

        return new JobObjective(
            id, type, StringUtil.capitalizeUnderscored(id), icon,
            items, paymentMap, xp, unlockLevel,
            true, UniInt.of(1, 3), UniInt.of(1, 100)
        );
    }
}
