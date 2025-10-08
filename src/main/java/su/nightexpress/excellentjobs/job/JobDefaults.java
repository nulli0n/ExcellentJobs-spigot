package su.nightexpress.excellentjobs.job;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapters;
import su.nightexpress.excellentjobs.grind.table.SourceTable;
import su.nightexpress.excellentjobs.grind.table.impl.*;
import su.nightexpress.excellentjobs.grind.type.GrindTypeId;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.progression.Progression;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bridge.RegistryType;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;

public class JobDefaults {

    @NotNull
    public static List<Job> createDefaultJobs(@NotNull JobsPlugin plugin) {
        List<Job> jobs = new ArrayList<>();

        jobs.add(createMinerJob(plugin));
        jobs.add(createLumberjackJob(plugin));
        jobs.add(createFarmerJob(plugin));
        jobs.add(createFisherJob(plugin));
        jobs.add(createHunterJob(plugin));

        return jobs;
    }

    @NotNull
    private static Job createMinerJob(@NotNull JobsPlugin plugin) {
        Job job = new Job(plugin, "miner");
        job.setIcon(NightItem.fromType(Material.IRON_PICKAXE));
        job.setName("Miner");
        job.setDescription(Lists.newList(
            GRAY.wrap("Dig deep, gather precious resources,"),
            GRAY.wrap("and conquer the underground"),
            GRAY.wrap("for money!"))
        );

        SourceTable blocksTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypeAvg(Material.NETHERRACK, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.SANDSTONE, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.GRANITE, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.ANDESITE, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.DIORITE, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.CALCITE, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.TUFF, GrindAdapters.VANILLA_BLOCK, 2)
            .addTypeAvg(Material.BLACKSTONE, GrindAdapters.VANILLA_BLOCK, 3)
            .addTypeAvg(Material.BASALT, GrindAdapters.VANILLA_BLOCK, 3)
            .addTypeAvg(Material.END_STONE, GrindAdapters.VANILLA_BLOCK, 3)
            .addTypeAvg(Material.COBBLESTONE, GrindAdapters.VANILLA_BLOCK, 4)
            .addTypeAvg(Material.STONE, GrindAdapters.VANILLA_BLOCK, 4)
            .addTypeAvg(Material.DEEPSLATE, GrindAdapters.VANILLA_BLOCK, 5)
            .addTypeAvg(Material.DRIPSTONE_BLOCK, GrindAdapters.VANILLA_BLOCK, 5)
            .addTypesAvg(Tag.TERRACOTTA.getValues(), GrindAdapters.VANILLA_BLOCK, 5)
            .addTypeAvg(Material.PRISMARINE_BRICKS, GrindAdapters.VANILLA_BLOCK, 6)
            .addTypeAvg(Material.ICE, GrindAdapters.VANILLA_BLOCK, 7)
            .addTypeAvg(Material.BLUE_ICE, GrindAdapters.VANILLA_BLOCK, 7)
            .addTypeAvg(Material.FROSTED_ICE, GrindAdapters.VANILLA_BLOCK, 7)
            .build();

        SourceTable mobDropTable = SourceTable.EMPTY;
        SourceTable blockDropTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypeAvg(Material.AMETHYST_BLOCK, GrindAdapters.VANILLA_ITEM, 13)
            .addTypeAvg(Material.OBSIDIAN, GrindAdapters.VANILLA_ITEM, 20)
            .addTypeAvg(Material.RAW_COPPER, GrindAdapters.VANILLA_ITEM, 6)
            .addTypeAvg(Material.COAL, GrindAdapters.VANILLA_ITEM, 7)
            .addTypeAvg(Material.GOLD_NUGGET, GrindAdapters.VANILLA_ITEM, 6)
            .addTypeAvg(Material.IRON_NUGGET, GrindAdapters.VANILLA_ITEM, 5)
            .addTypeAvg(Material.REDSTONE, GrindAdapters.VANILLA_ITEM, 8)
            .addTypeAvg(Material.QUARTZ, GrindAdapters.VANILLA_ITEM, 12)
            .addTypeAvg(Material.LAPIS_LAZULI, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.RAW_IRON, GrindAdapters.VANILLA_ITEM, 20)
            .addTypeAvg(Material.RAW_GOLD, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.DIAMOND, GrindAdapters.VANILLA_ITEM, 65)
            .addTypeAvg(Material.EMERALD, GrindAdapters.VANILLA_ITEM, 80)
            .addTypeAvg(Material.NETHERITE_SCRAP, GrindAdapters.VANILLA_ITEM, 400)
            .build();

        job.addObjective("blocks", GrindTypeId.MINING, new BasicBlockGrindTable(blocksTable));
        job.addObjective("block_drops", GrindTypeId.GATHERING, new GatheringGrindTable(blockDropTable, mobDropTable));
        return job;
    }

    @NotNull
    private static Job createLumberjackJob(@NotNull JobsPlugin plugin) {
        Job job = new Job(plugin, "lumberjack");
        job.setIcon(NightItem.fromType(Material.IRON_AXE));
        job.setDescription(Lists.newList(
            GRAY.wrap("Harvest towering forests and"),
            GRAY.wrap("master the art of woodcutting"),
            GRAY.wrap("for money!"))
        );

        SourceTable.Builder builder = SourceTable.builder().withScale(Progression.DEFAULT_XP_SCALE);

        Tag.LOGS.getValues().forEach(material -> {
            double xp = 32;
            String name = material.name();
            if (name.endsWith("_WOOD") || name.endsWith("_STEM")) xp = 48;
            else if (name.endsWith("_HYPHAE")) xp = 64;

            builder.addTypeAvg(material, GrindAdapters.VANILLA_BLOCK, xp);
        });

        job.addObjective("blocks", GrindTypeId.MINING, new BasicBlockGrindTable(builder.build()));
        return job;
    }

    @NotNull
    private static Job createFarmerJob(@NotNull JobsPlugin plugin) {
        Job job = new Job(plugin, "farmer");
        job.setIcon(NightItem.fromType(Material.IRON_HOE));
        job.setDescription(Lists.newList(
            GRAY.wrap("Tend to fertile lands,"),
            GRAY.wrap("cultivate crops, and flourish"),
            GRAY.wrap("for money!"))
        );

        SourceTable blockDropTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypeAvg(Material.KELP, GrindAdapters.VANILLA_ITEM, 3)
            .addTypeAvg(Material.BAMBOO, GrindAdapters.VANILLA_ITEM, 4)
            .addTypeAvg(Material.SCULK, GrindAdapters.VANILLA_ITEM, 6)
            .addTypeAvg(Material.CACTUS, GrindAdapters.VANILLA_ITEM, 6)
            .addTypeAvg(Material.SUGAR_CANE, GrindAdapters.VANILLA_ITEM, 7)
            .addTypeAvg(Material.TALL_GRASS, GrindAdapters.VANILLA_ITEM, 10)
            .addTypesAvg(Tag.FLOWERS.getValues(), GrindAdapters.VANILLA_ITEM, 10)
            .addTypeAvg(Material.LILY_PAD, GrindAdapters.VANILLA_ITEM, 12)
            .addTypeAvg(Material.WHEAT, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.POTATO, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.CARROT, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.BEETROOT, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.COCOA_BEANS, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.MELON_SLICE, GrindAdapters.VANILLA_ITEM, 4)
            .addTypeAvg(Material.NETHER_WART, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.NETHER_SPROUTS, GrindAdapters.VANILLA_ITEM, 15)
            .addTypeAvg(Material.SWEET_BERRIES, GrindAdapters.VANILLA_ITEM, 16)
            .addTypeAvg(Material.GLOW_BERRIES, GrindAdapters.VANILLA_ITEM, 18)
            .addTypeAvg(Material.CAVE_VINES, GrindAdapters.VANILLA_ITEM, 24)
            .addTypeAvg(Material.BROWN_MUSHROOM, GrindAdapters.VANILLA_ITEM, 24)
            .addTypeAvg(Material.RED_MUSHROOM, GrindAdapters.VANILLA_ITEM, 24)
            .addTypeAvg(Material.AZALEA, GrindAdapters.VANILLA_ITEM, 24)
            .addTypeAvg(Material.PUMPKIN, GrindAdapters.VANILLA_ITEM, 25)
            .addTypeAvg(Material.CRIMSON_FUNGUS, GrindAdapters.VANILLA_ITEM, 28)
            .addTypeAvg(Material.WARPED_FUNGUS, GrindAdapters.VANILLA_ITEM, 28)
            .addTypeAvg(Material.TORCHFLOWER, GrindAdapters.VANILLA_ITEM, 30)
            .addTypeAvg(Material.PITCHER_PLANT, GrindAdapters.VANILLA_ITEM, 30)
            .addTypeAvg(Material.HONEY_BOTTLE, GrindAdapters.VANILLA_ITEM, 75)
            .build();
        job.addObjective("plants", GrindTypeId.GATHERING, new GatheringGrindTable(blockDropTable, SourceTable.EMPTY));

        Set<EntityType> breedable = BukkitThing.getAll(RegistryType.ENTITY_TYPE).stream().filter(type -> {
            Class<?> clazz = type.getEntityClass();
            return clazz != null && Breedable.class.isAssignableFrom(clazz);
        }).collect(Collectors.toSet());

        SourceTable breedTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypesAvg(breedable, GrindAdapters.VANILLA_MOB, 100)
            .build();
        job.addObjective("breeding", GrindTypeId.BREEDING, new BasicEntityGrindTable(breedTable));

        SourceTable shearTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypeAvg(EntityType.SHEEP, GrindAdapters.VANILLA_MOB, 50)
            .addTypeAvg(EntityType.MOOSHROOM, GrindAdapters.VANILLA_MOB, 100)
            .build();
        job.addObjective("shearing", GrindTypeId.SHEARING, new BasicEntityGrindTable(shearTable));

        SourceTable milkTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypeAvg(EntityType.COW, GrindAdapters.VANILLA_MOB, 5)
            .addTypeAvg(EntityType.GOAT, GrindAdapters.VANILLA_MOB, 5)
            .addTypeAvg(EntityType.MOOSHROOM, GrindAdapters.VANILLA_MOB, 10)
            .build();
        job.addObjective("milking", GrindTypeId.MILKING, new BasicEntityGrindTable(milkTable));
        return job;
    }

    @NotNull
    private static Job createFisherJob(@NotNull JobsPlugin plugin) {
        Job job = new Job(plugin, "fisherman");
        job.setIcon(NightItem.fromType(Material.FISHING_ROD));
        job.setDescription(Lists.newList(
            GRAY.wrap("Cast your line into shimmering"),
            GRAY.wrap("waters, reel in bountiful catches,"),
            GRAY.wrap("and excel for money!"))
        );

        SourceTable itemsTable = SourceTable.builder()
            .withScale(Progression.DEFAULT_XP_SCALE)
            .addTypeAvg(Material.COD, GrindAdapters.VANILLA_ITEM, 70)
            .addTypeAvg(Material.SALMON, GrindAdapters.VANILLA_ITEM, 90)
            .addTypeAvg(Material.TROPICAL_FISH, GrindAdapters.VANILLA_ITEM, 120)
            .addTypeAvg(Material.PUFFERFISH, GrindAdapters.VANILLA_ITEM, 150)

            .addTypeAvg(Material.LILY_PAD, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.BOWL, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.LEATHER, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.LEATHER_BOOTS, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.ROTTEN_FLESH, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.STICK, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.STRING, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.POTION, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.BONE, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.BAMBOO, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.INK_SAC, GrindAdapters.VANILLA_ITEM, 50)
            .addTypeAvg(Material.TRIPWIRE_HOOK, GrindAdapters.VANILLA_ITEM, 50)

            .addTypeAvg(Material.BOW, GrindAdapters.VANILLA_ITEM, 100)
            .addTypeAvg(Material.FISHING_ROD, GrindAdapters.VANILLA_ITEM, 100)
            .addTypeAvg(Material.NAME_TAG, GrindAdapters.VANILLA_ITEM, 100)
            .addTypeAvg(Material.NAUTILUS_SHELL, GrindAdapters.VANILLA_ITEM, 100)
            .addTypeAvg(Material.SADDLE, GrindAdapters.VANILLA_ITEM, 100)
            .addTypeAvg(Material.ENCHANTED_BOOK, GrindAdapters.VANILLA_ITEM, 100)
            .build();

        job.addObjective("fishing_items", GrindTypeId.FISHING, new FishingGrindTable(SourceTable.EMPTY, itemsTable));
        return job;
    }

    @NotNull
    private static Job createHunterJob(@NotNull JobsPlugin plugin) {
        Job job = new Job(plugin, "hunter");
        job.setIcon(NightItem.fromType(Material.IRON_SWORD));
        job.setDescription(Lists.newList(
            GRAY.wrap("Brave the wilderness, track elusive"),
            GRAY.wrap("prey, and thrive for money!"))
        );

        SourceTable.Builder builder = SourceTable.builder().withScale(Progression.DEFAULT_XP_SCALE);

        BukkitThing.getAll(RegistryType.ENTITY_TYPE).forEach(entityType -> {
            Class<? extends Entity> mobClass = entityType.getEntityClass();
            if (mobClass == null || !entityType.isSpawnable() || !LivingEntity.class.isAssignableFrom(mobClass)) return;

            double xpAvg = 12;

            if (entityType == EntityType.ENDER_DRAGON) {
                xpAvg = 1000D;
            }
            else if (Animals.class.isAssignableFrom(mobClass)) {
                xpAvg = 10;
            }
            else if (Raider.class.isAssignableFrom(mobClass)) {
                xpAvg = 20D;
            }
            else if (Monster.class.isAssignableFrom(mobClass)) {
                xpAvg = 15D;
            }
            else if (Fish.class.isAssignableFrom(mobClass)) {
                xpAvg = 5;
            }

            builder.addTypeAvg(entityType, GrindAdapters.VANILLA_MOB, xpAvg);
        });

        job.addObjective("mobs", GrindTypeId.KILLING, new KillingGrindTable(builder.build(), -90D));
        return job;
    }
}
