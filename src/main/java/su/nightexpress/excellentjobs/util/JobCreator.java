package su.nightexpress.excellentjobs.util;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.impl.ObjectiveReward;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;
import su.nightexpress.excellentjobs.job.work.WorkId;
import su.nightexpress.excellentjobs.job.work.wrapper.WrappedEnchant;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.bridge.RegistryType;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_GRAY;

public class JobCreator {

    private final JobsPlugin plugin;

    public JobCreator(@NotNull JobsPlugin plugin) {
        this.plugin = plugin;
    }

    public void createDefaultJobs() {
        this.createMinerJob();
        this.createBlacksmithJob();
        this.createLumberjackJob();
        this.createFarmerJob();
        this.createFisherJob();
        this.createHunterJob();
        //this.createBuilderJob();

        if (Version.isPaper()) {
            this.createSpellsmithJob();
        }
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
        job.setInitialXP(904);
        job.setXPFactor(1.09095309);
        job.setMaxLevel(100);
        job.setProgressBarColor(BarColor.GREEN);
        //job.setPaymentMultiplier(JobUtils.getDefaultPaymentModifier());
        //job.setXPMultiplier(JobUtils.getDefaultXPModifier());
        job.getDailyPaymentLimits().put(CurrencyId.VAULT, Modifier.add(-1D, 0D, 0D));
        job.setXPDailyLimits(Modifier.add(-1D, 0D, 0D));
        job.save();
    }

    private void createObjectives(@NotNull Job job, @NotNull Consumer<List<JobObjective>> consumer) {
        String jobId = job.getId();

        File file = new File(this.plugin.getDataFolder() + Config.DIR_JOBS + jobId, Job.OBJECTIVES_CONFIG_NAME);
        if (file.exists()) return;

        FileConfig config = new FileConfig(file);

        List<JobObjective> objectives = new ArrayList<>();
        consumer.accept(objectives);

        for (JobObjective objective : objectives) {
            objective.write(config, objective.getId());
        }

        config.saveChanges();
    }

    private void createMinerJob() {
        this.createJob("miner", job -> {
            job.setIcon(NightItem.asCustomHead("1e1d4bc469d29d22a7ef6d21a61b451291f21bf51fd167e7fd07b719512e87a1"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Dig deep, gather precious resources,"),
                LIGHT_GRAY.wrap("and conquer the underground"),
                LIGHT_GRAY.wrap("for money!"))
            );

            this.createObjectives(job, objectives -> {
                objectives.add(forMaterial(WorkId.MINING, Material.STONE, reward(25, 0.5, 1D), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.COBBLESTONE, reward(25, 0.5, 1D), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.MOSSY_COBBLESTONE, reward(50, 0.75, 1D), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.ANDESITE, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.DIORITE, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.GRANITE, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.TUFF, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.AMETHYST_BLOCK, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.CALCITE, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE, reward(75, 1, 3), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.COBBLED_DEEPSLATE, reward(75, 1, 3), reward(3, 6)));
                objectives.add(forMaterial(WorkId.MINING, Material.BASALT, reward(50, 0.75, 1.5), reward(1, 2)));
                objectives.add(forMaterial(WorkId.MINING, Material.NETHERRACK, reward(25, 0.35, 0.75), reward(1, 1)));
                objectives.add(forMaterial(WorkId.MINING, Material.END_STONE, reward(50, 8, 15), reward(20, 30)));

                objectives.add(forMaterial(WorkId.MINING, Material.OBSIDIAN, reward(50, 100), reward(40, 60)));
                objectives.add(forMaterial(WorkId.MINING, Material.ANCIENT_DEBRIS, reward(200, 400), reward(300, 500)));

                objectives.add(forMaterial(WorkId.MINING, Material.CRIMSON_NYLIUM, reward(1, 3), reward(4, 8)));
                objectives.add(forMaterial(WorkId.MINING, Material.WARPED_NYLIUM, reward(1, 3), reward(4, 8)));

                objectives.add(forMaterial(WorkId.MINING, Material.COAL_ORE, reward(8, 12), reward(30, 60)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_COAL_ORE, reward(16, 24), reward(60, 120)));
                objectives.add(forMaterial(WorkId.MINING, Material.REDSTONE_ORE, reward(12, 16), reward(40, 70)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_REDSTONE_ORE, reward(24, 32), reward(80, 140)));
                objectives.add(forMaterial(WorkId.MINING, Material.COPPER_ORE, reward(6, 9), reward(20, 40)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_COPPER_ORE, reward(12, 18), reward(40, 80)));
                objectives.add(forMaterial(WorkId.MINING, Material.IRON_ORE, reward(6, 10), reward(25, 45)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_IRON_ORE, reward(12, 20), reward(50, 90)));
                objectives.add(forMaterial(WorkId.MINING, Material.GOLD_ORE, reward(15, 30), reward(50, 90)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_GOLD_ORE, reward(30, 60), reward(100, 180)));
                objectives.add(forMaterial(WorkId.MINING, Material.LAPIS_ORE, reward(24, 32), reward(80, 140)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_LAPIS_ORE, reward(50, 65), reward(160, 280)));
                objectives.add(forMaterial(WorkId.MINING, Material.DIAMOND_ORE, reward(100, 130), reward(380, 560)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_DIAMOND_ORE, reward(200, 260), reward(600, 900)));
                objectives.add(forMaterial(WorkId.MINING, Material.EMERALD_ORE, reward(150, 250), reward(500, 800)));
                objectives.add(forMaterial(WorkId.MINING, Material.DEEPSLATE_EMERALD_ORE, reward(300, 500), reward(950, 1400)));
                objectives.add(forMaterial(WorkId.MINING, Material.NETHER_QUARTZ_ORE, reward(9, 14), reward(15, 30)));
                objectives.add(forMaterial(WorkId.MINING, Material.NETHER_GOLD_ORE, reward(10, 15), reward(20, 40)));

                objectives.add(forMaterials("terracotta", WorkId.MINING, Tag.TERRACOTTA.getValues(), NightItem.fromType(Material.TERRACOTTA), reward(0.5, 1.5), reward(1, 3), 15));

//        objectives.add(forMaterial(WorkId.SMELTING, Material.RAW_COPPER, reward(0.8, 1.2), reward(2, 5)));
//        objectives.add(forMaterial(WorkId.SMELTING, Material.RAW_GOLD, reward(1.1, 2.2), reward(4, 7)));
//        objectives.add(forMaterial(WorkId.SMELTING, Material.RAW_IRON, reward(0.9, 1.3), reward(3, 6)));
            });
        });
    }

    private void createBlacksmithJob() {
        createJob("blacksmith", job -> {
            job.setIcon(NightItem.asCustomHead("651eb727bd896add55f6d6783cecd793d982500f4d9476143fe08e21ff7e3f5e"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Get materials and craft"),
                LIGHT_GRAY.wrap("your gear for money!"))
            );

            this.createObjectives(job, objectives -> {
                double moneyMin = 3;
                double moneyMax = 7;
                double xpMin = 30;
                double xpMax = 44;

                Map<String, Double> materialMod = new HashMap<>();
                materialMod.put("wooden", 0.5);
                materialMod.put("leather", 1D);
                materialMod.put("stone", 0.7D);
                materialMod.put("iron", 2D);
                materialMod.put("chainmail", 3.5D);
                materialMod.put("golden", 4D);
                materialMod.put("diamond", 6D);
                materialMod.put("netherite", 8D);

                Set<Material> items = new HashSet<>();
                items.addAll(Tag.ITEMS_ENCHANTABLE_ARMOR.getValues());
                items.addAll(Tag.ITEMS_ENCHANTABLE_MINING.getValues());
                items.addAll(Tag.ITEMS_ENCHANTABLE_SWORD.getValues());
                items.addAll(Tag.ITEMS_ENCHANTABLE_BOW.getValues());
                items.addAll(Tag.ITEMS_ENCHANTABLE_CROSSBOW.getValues());
                items.addAll(Tag.ITEMS_ENCHANTABLE_FISHING.getValues());

                items.forEach(material -> {
                    double armorMod = switch (material.getEquipmentSlot()) {
                        case HEAD -> 1.3D;
                        case CHEST -> 2D;
                        case LEGS -> 1.7D;
                        case FEET -> 1.1D;
                        default -> 1D;
                    };

                    String prefix = material.name().split("_")[0].toLowerCase();
                    double mod = materialMod.getOrDefault(prefix, 1D) * armorMod;

                    ObjectiveReward money = reward(moneyMin * mod, moneyMax * mod);
                    ObjectiveReward xp = reward(xpMin * mod, xpMax * mod);

                    objectives.add(forMaterial(WorkId.CRAFTING, material, money, xp));
                    objectives.add(forMaterial(WorkId.REPAIRING, material, money.multiply(0.25), xp.multiply(0.25)));
                });
            });
        });
    }

    private void createLumberjackJob() {
        createJob("lumberjack", job -> {
            job.setIcon(NightItem.asCustomHead("80171f7facc6f7843103b821d9f8a69febe071404325313acbf0b9316a037e06"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Harvest towering forests and"),
                LIGHT_GRAY.wrap("master the art of woodcutting"),
                LIGHT_GRAY.wrap("for money!"))
            );

            this.createObjectives(job, objectives -> {
                Tag.LOGS.getValues().forEach(material -> {
                    objectives.add(forMaterial(WorkId.MINING, material, reward(25, 50), reward(30, 50)));
                });

                Tag.WARPED_STEMS.getValues().forEach(material -> {
                    objectives.add(forMaterial(WorkId.MINING, material, reward(30, 60), reward(40, 60)));
                });

                Tag.CRIMSON_STEMS.getValues().forEach(material -> {
                    objectives.add(forMaterial(WorkId.MINING, material, reward(30, 60), reward(40, 60)));
                });

                Tag.LEAVES.getValues().forEach(material -> {
                    objectives.add(forMaterial(WorkId.MINING, material, reward(25, 0.5, 1), reward(1, 2)));
                });

                objectives.add(forMaterial(WorkId.MINING, Material.MANGROVE_ROOTS, reward(75, 2, 5), reward(2, 4)));
            });
        });
    }

    private void createFarmerJob() {
        createJob("farmer", job -> {
            job.setIcon(NightItem.asCustomHead("9af328c87b068509aca9834eface197705fe5d4f0871731b7b21cd99b9fddc"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Tend to fertile lands,"),
                LIGHT_GRAY.wrap("cultivate crops, and flourish"),
                LIGHT_GRAY.wrap("for money!"))
            );

            this.createObjectives(job, objectives -> {
                objectives.add(forMaterial(WorkId.MINING, Material.WHEAT, reward(2, 4), reward(8, 12)));
                objectives.add(forMaterial(WorkId.MINING, Material.POTATOES, Material.POTATO, reward(2, 4), reward(8, 12)));
                objectives.add(forMaterial(WorkId.MINING, Material.CARROTS, Material.CARROT, reward(2, 4), reward(8, 12)));
                objectives.add(forMaterial(WorkId.MINING, Material.BEETROOTS, Material.BEETROOT, reward(2, 4), reward(8, 12)));
                objectives.add(forMaterial(WorkId.MINING, Material.CACTUS, reward(60, 80), reward(300, 350)));
                objectives.add(forMaterial(WorkId.MINING, Material.SUGAR_CANE, reward(3, 6), reward(20, 30)));
                objectives.add(forMaterial(WorkId.MINING, Material.COCOA, Material.COCOA_BEANS, reward(15, 20), reward(80, 100)));
                objectives.add(forMaterial(WorkId.MINING, Material.MELON, reward(5, 7), reward(35, 45)));
                objectives.add(forMaterial(WorkId.MINING, Material.PUMPKIN, reward(5, 7), reward(35, 45)));
                objectives.add(forMaterial(WorkId.MINING, Material.BROWN_MUSHROOM, reward(50, 70), reward(150, 250)));
                objectives.add(forMaterial(WorkId.MINING, Material.RED_MUSHROOM, reward(50, 70), reward(150, 250)));
                objectives.add(forMaterial(WorkId.MINING, Material.NETHER_WART, reward(1, 3), reward(10, 15)));
                objectives.add(forMaterial(WorkId.MINING, Material.VINE, reward(3, 6), reward(20, 30)));
                objectives.add(forMaterial(WorkId.MINING, Material.BROWN_MUSHROOM_BLOCK, reward(15, 20), reward(45, 60)));
                objectives.add(forMaterial(WorkId.MINING, Material.RED_MUSHROOM_BLOCK, reward(20, 25), reward(50, 65)));
                objectives.add(forMaterial(WorkId.MINING, Material.CHORUS_PLANT, reward(1, 3), reward(1, 1)));
                objectives.add(forMaterial(WorkId.MINING, Material.CHORUS_FLOWER, reward(80, 100), reward(80, 120)));
                objectives.add(forMaterial(WorkId.MINING, Material.BAMBOO, reward(3, 5), reward(15, 20)));

//                objectives.add(forMaterial(WorkId.BUILDING, Material.WHEAT, Material.WHEAT_SEEDS, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.POTATOES, Material.POTATO, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.CARROTS, Material.CARROT, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.BEETROOTS, Material.BEETROOT_SEEDS, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.CACTUS, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.SUGAR_CANE, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.COCOA, Material.COCOA_BEANS, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.MELON_STEM, Material.MELON_SEEDS, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.NETHER_WART, reward(0.0, 0.0), reward(4, 6)));
//                objectives.add(forMaterial(WorkId.BUILDING, Material.VINE, reward(0.0, 0.0), reward(4, 6)));

                objectives.add(forEntity(WorkId.BREEDING, EntityType.CAT, Material.CAT_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.CHICKEN, Material.CHICKEN_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.COW, Material.COW_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.DONKEY, Material.DONKEY_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.FOX, Material.FOX_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.HORSE, Material.HORSE_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.LLAMA, Material.LLAMA_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.MOOSHROOM, Material.MOOSHROOM_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.PIG, Material.PIG_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.RABBIT, Material.RABBIT_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.SHEEP, Material.SHEEP_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.WOLF, Material.WOLF_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.TURTLE, Material.TURTLE_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.DOLPHIN, Material.DOLPHIN_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.PANDA, Material.PANDA_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.GOAT, Material.GOAT_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.BEE, Material.BEE_SPAWN_EGG, reward(4, 8), reward(15, 25)));
                objectives.add(forEntity(WorkId.BREEDING, EntityType.MULE, Material.MULE_SPAWN_EGG, reward(4, 8), reward(15, 25)));

                objectives.add(forEntity(WorkId.SHEARING, EntityType.SHEEP, Material.SHEEP_SPAWN_EGG, reward(3, 5), reward(10, 15)));
                objectives.add(forEntity(WorkId.SHEARING, EntityType.MOOSHROOM, Material.MOOSHROOM_SPAWN_EGG, reward(3, 5), reward(10, 15)));

                objectives.add(forEntity(WorkId.MILKING, EntityType.COW, Material.MOOSHROOM_SPAWN_EGG, reward(3, 5), reward(10, 15)));
                objectives.add(forEntity(WorkId.MILKING, EntityType.MOOSHROOM, Material.MOOSHROOM_SPAWN_EGG, reward(3, 5), reward(10, 15)));
                objectives.add(forEntity(WorkId.MILKING, EntityType.GOAT, Material.MOOSHROOM_SPAWN_EGG, reward(3, 5), reward(10, 15)));

                objectives.add(forEntity(WorkId.TAMING, EntityType.WOLF, Material.WOLF_SPAWN_EGG, reward(10, 15), reward(40, 60)));
                objectives.add(forEntity(WorkId.TAMING, EntityType.HORSE, Material.HORSE_SPAWN_EGG, reward(10, 15), reward(40, 60)));
                objectives.add(forEntity(WorkId.TAMING, EntityType.PARROT, Material.PARROT_SPAWN_EGG, reward(10, 15), reward(40, 60)));
                objectives.add(forEntity(WorkId.TAMING, EntityType.OCELOT, Material.OCELOT_SPAWN_EGG, reward(10, 15), reward(40, 60)));
                objectives.add(forEntity(WorkId.TAMING, EntityType.LLAMA, Material.LLAMA_SPAWN_EGG, reward(10, 15), reward(40, 60)));

                objectives.add(forMaterial(WorkId.HARVESTING, Material.GLOW_BERRIES, reward(25, 35), reward(80, 120)));
                objectives.add(forMaterial(WorkId.HARVESTING, Material.SWEET_BERRIES, reward(25, 35), reward(80, 120)));

                objectives.add(forMaterials("honey", WorkId.COLLECT_HONEY, Lists.newSet(Material.BEEHIVE, Material.BEE_NEST), NightItem.fromType(Material.HONEY_BOTTLE), reward(30, 50), reward(60, 90), 1));
                // TODO objectives.add(forMaterial(WorkId.HARVESTING, Material.HONEYCOMB, reward(0.0, 0.0), reward(0.05, 0.05)));
            });
        });
    }

    private void createFisherJob() {
        createJob("fisher", job -> {
            job.setIcon(NightItem.asCustomHead("12510b301b088638ec5c8747e2d754418cb747a5ce7022c9c712ecbdc5f6f065"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Cast your line into shimmering"),
                LIGHT_GRAY.wrap("waters, reel in bountiful catches,"),
                LIGHT_GRAY.wrap("and excel for money!"))
            );

            this.createObjectives(job, objectives -> {
                objectives.add(forMaterial(WorkId.FISHING, Material.LILY_PAD, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.BOWL, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.LEATHER, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.LEATHER_BOOTS, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.ROTTEN_FLESH, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.STICK, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.STRING, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.POTION, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.BONE, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.BAMBOO, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.INK_SAC, reward(30, 40), reward(100, 200)));
                objectives.add(forMaterial(WorkId.FISHING, Material.TRIPWIRE_HOOK, reward(30, 40), reward(100, 200)));

                objectives.add(forMaterial(WorkId.FISHING, Material.BOW, reward(200, 400), reward(450, 700)));
                objectives.add(forMaterial(WorkId.FISHING, Material.FISHING_ROD, reward(200, 400), reward(450, 700)));
                objectives.add(forMaterial(WorkId.FISHING, Material.NAME_TAG, reward(200, 400), reward(450, 700)));
                objectives.add(forMaterial(WorkId.FISHING, Material.NAUTILUS_SHELL, reward(200, 400), reward(450, 700)));
                objectives.add(forMaterial(WorkId.FISHING, Material.SADDLE, reward(200, 400), reward(450, 700)));
                objectives.add(forMaterial(WorkId.FISHING, Material.ENCHANTED_BOOK, reward(200, 400), reward(450, 700)));

                objectives.add(forMaterial(WorkId.FISHING, Material.COD, reward(35, 80), reward(170, 240)));
                objectives.add(forMaterial(WorkId.FISHING, Material.SALMON, reward(60, 130), reward(460, 730)));
                objectives.add(forMaterial(WorkId.FISHING, Material.TROPICAL_FISH, reward(220, 450), reward(800, 1400)));
                objectives.add(forMaterial(WorkId.FISHING, Material.PUFFERFISH, reward(150, 310), reward(600, 1100)));

                objectives.add(forEntity(WorkId.KILL_ENTITY, EntityType.COD, Material.COD_SPAWN_EGG, reward(80, 120), reward(250, 350)));
                objectives.add(forEntity(WorkId.KILL_ENTITY, EntityType.SALMON, Material.SALMON_SPAWN_EGG, reward(80, 120), reward(250, 350)));
                objectives.add(forEntity(WorkId.KILL_ENTITY, EntityType.TROPICAL_FISH, Material.TROPICAL_FISH_SPAWN_EGG, reward(80, 120), reward(250, 350)));
                objectives.add(forEntity(WorkId.KILL_ENTITY, EntityType.PUFFERFISH, Material.PUFFERFISH_SPAWN_EGG, reward(80, 120), reward(250, 350)));
            });
        });
    }

    private void createHunterJob() {
        createJob("hunter", job -> {
            job.setIcon(NightItem.asCustomHead("5dcf2c198f61df5a6d0ba97dbf90c337995405c17396c016c85f6f3fea52c906"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Brave the wilderness, track elusive"),
                LIGHT_GRAY.wrap("prey, and thrive for money!"))
            );

            this.createObjectives(job, objectives -> {
                BukkitThing.getAll(RegistryType.ENTITY_TYPE).forEach(entityType -> {
                    Class<? extends Entity> clazz = entityType.getEntityClass();
                    if (clazz == null || !entityType.isSpawnable()) return;

                    World world = Bukkit.getWorlds().getFirst();
                    Entity entity = world.createEntity(world.getSpawnLocation(), clazz);
                    if (!(entity instanceof LivingEntity)) return;
                    if (entity instanceof Fish) return;

                    double moneyMod = 1D;
                    double xpMod = 1D;

                    if (entityType == EntityType.ENDER_DRAGON) {
                        moneyMod = 700D;
                        xpMod = 700D;
                    }
                    else if (entity instanceof Animals) {
                        moneyMod = 1.15D;
                        xpMod = 1.15D;
                    }
                    else if (entity instanceof Raider) {
                        moneyMod = 5D;
                        xpMod = 5D;
                    }
                    else if (entity instanceof Monster) {
                        moneyMod = 3D;
                        xpMod = 3D;
                    }

                    double moneyMin = 6 * moneyMod;
                    double moneyMax = 14 * moneyMod;

                    double xpMin = 9 * xpMod;
                    double xpMax = 26 * xpMod;

                    Material material = plugin.getServer().getItemFactory().getSpawnEgg(entityType);
                    if (material == null) return;

                    objectives.add(forEntity(WorkId.KILL_ENTITY, entityType, material, reward(moneyMin, moneyMax), reward(xpMin, xpMax)));
                });
            });
        });
    }

    private void createSpellsmithJob() {
        createJob("spellsmith", job -> {
            job.setIcon(NightItem.asCustomHead("28951399d0ebd0dfe87e50f0d6dee25274d93f1fbb38505ec971b601d1c2cb9"));
            job.setDescription(Lists.newList(
                LIGHT_GRAY.wrap("Get levels and enhance your"),
                LIGHT_GRAY.wrap("gear with enchantments for money!"))
            );

            this.createObjectives(job, objectives -> {
                double moneyMin = 17;
                double moneyMax = 25;
                double xpMin = 80;
                double xpMax = 140;

                Set<Enchantment> enchantments = BukkitThing.getEnchantments();
                double maxWeight = enchantments.stream().map(Enchantment::getWeight).max(Comparator.comparingInt(i -> i)).orElse(0);

                BukkitThing.getEnchantments().forEach(enchantment -> {
                    if (enchantment.isCursed() || enchantment.isTreasure()) return;

                    double weight = enchantment.getWeight();
                    double weightMod = weight / maxWeight;

                    for (int index = 0; index < enchantment.getMaxLevel(); index++) {
                        int level = index + 1;
                        double mod = 1D + (0.5 * level) + weightMod;

                        ObjectiveReward money = reward(moneyMin * mod, moneyMax * mod);
                        ObjectiveReward xp = reward(xpMin * mod, xpMax * mod);

                        objectives.add(forEnchant(WorkId.GET_ENCHANT, enchantment, level, money, xp));
                    }
                });
            });
        });
    }

    @NotNull
    private static JobObjective createEntityObjective(@NotNull String id,
                                                        @NotNull String type,
                                                        @NotNull Set<EntityType> items,
                                                        @NotNull NightItem icon,
                                                        @NotNull ObjectiveReward money,
                                                        @NotNull ObjectiveReward xp,
                                                        int unlockLevel) {
        return createObjective(id, type, WorkFormatters.ENITITY_TYPE, items, icon, money, xp, unlockLevel);
    }

    @NotNull
    private static JobObjective forEntity(@NotNull String type,
                                          @NotNull EntityType object,
                                          @NotNull Material icon,
                                          @NotNull ObjectiveReward money,
                                          @NotNull ObjectiveReward xp) {
        return createEntityObjective(BukkitThing.getValue(object), type, Lists.newSet(object), NightItem.fromType(icon), money, xp, 1);
    }

    @NotNull
    private static JobObjective forMaterials(@NotNull String id,
                                             @NotNull String type,
                                             @NotNull Set<Material> items,
                                             @NotNull NightItem icon,
                                             @NotNull ObjectiveReward money,
                                             @NotNull ObjectiveReward xp,
                                             int unlockLevel) {
        return createObjective(id, type, WorkFormatters.MATERIAL, items, icon, money, xp, unlockLevel);
    }

    @NotNull
    private static JobObjective forMaterial(@NotNull String type, @NotNull Material object, @NotNull Material icon, @NotNull ObjectiveReward money, @NotNull ObjectiveReward xp) {
        return forMaterials(BukkitThing.getValue(object), type, Lists.newSet(object), NightItem.fromType(icon), money, xp, 1);
    }

    @NotNull
    private static JobObjective forMaterial(@NotNull String type, @NotNull Material object, @NotNull ObjectiveReward money, @NotNull ObjectiveReward xp) {
        return forMaterials(BukkitThing.getValue(object), type, Lists.newSet(object), NightItem.fromType(object), money, xp, 1);
    }

    @NotNull
    private static JobObjective forEnchant(@NotNull String type, @NotNull Enchantment enchantment, int level, @NotNull ObjectiveReward money, @NotNull ObjectiveReward xp) {
        String id = BukkitThing.getValue(enchantment) + "_" + NumberUtil.toRoman(level);
        WrappedEnchant wrapped = new WrappedEnchant(enchantment, level);

        return createObjective(id, type, WorkFormatters.WRAPPED_ENCHANTMENT, Lists.newSet(wrapped), NightItem.fromType(Material.ENCHANTED_BOOK), money, xp, 1);
    }

    @NotNull
    private static <O> JobObjective createObjective(@NotNull String id,
                                                    @NotNull String type,
                                                    @NotNull WorkFormatter<O> formatter,
                                                    @NotNull Set<O> items,
                                                    @NotNull NightItem icon,
                                                    @NotNull ObjectiveReward money,
                                                    @NotNull ObjectiveReward xp,
                                                    int unlockLevel) {
        Set<String> objects = items.stream().map(formatter::getName).sorted(String::compareTo).collect(Collectors.toCollection(LinkedHashSet::new));

        return buildObjective(id, type, objects, icon, money, xp, unlockLevel);
    }

    @NotNull
    private static JobObjective buildObjective(@NotNull String idName,
                                                    @NotNull String type,
                                                    @NotNull Set<String> items,
                                                    @NotNull NightItem icon,
                                                    @NotNull ObjectiveReward money,
                                                    @NotNull ObjectiveReward xp,
                                                    int unlockLevel) {
        Map<String, ObjectiveReward> paymentMap = new HashMap<>();
        paymentMap.put(CurrencyId.VAULT, money);

        String id = type + "_" + idName.toLowerCase();
        String display = StringUtil.capitalize(idName.replace("_", " ")); // To prevent toLowerCase call for enchant levels display properly.

        boolean allowForOrder = true;
        UniInt orderObjects = UniInt.of(1, 3);
        UniInt orderCount = UniInt.of(1, 100);

        return new JobObjective(id, type, display, icon, items, paymentMap, xp, unlockLevel, allowForOrder, orderObjects, orderCount);
    }

    @NotNull
    private static ObjectiveReward reward(double min, double max) {
        return reward(100, min, max);
    }

    @NotNull
    private static ObjectiveReward reward(double chance, double min, double max) {
        return new ObjectiveReward(chance, min, max);
    }
}
