package su.nightexpress.excellentjobs.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.hook.HookId;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Visuals {

    private static final Class<?>      NMS_ENTITY          = Reflex.getClass("net.minecraft.world.entity", "Entity");
    private static final String        ENTITY_COUNTER_NAME = Version.isAtLeast(Version.V1_19_R3) ? "d" : "c";
    public static final  AtomicInteger ENTITY_COUNTER      = (AtomicInteger) Reflex.getFieldValue(NMS_ENTITY, ENTITY_COUNTER_NAME);

    private static final Map<Player, Set<Integer>> VISUALS_MAP = new WeakHashMap<>();

    public static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    public static void highlightPoints(@NotNull Player player, @NotNull World world, BlockPos[] cuboid) {
        Visuals.removeVisuals(player);

        if (!cuboid[0].isEmpty()) {
            Location min = cuboid[0].toLocation(player.getWorld());
            Visuals.addVisualBlock(player, min);
        }

        if (!cuboid[1].isEmpty()) {
            Location max = cuboid[1].toLocation(player.getWorld());
            Visuals.addVisualBlock(player, max);
        }
    }

    public static void removeVisuals(@NotNull Player player) {
        Set<Integer> list = VISUALS_MAP.remove(player);
        if (list == null) return;

        list.forEach(Visuals::destroyEntity);
    }

    public static void addVisualText(@NotNull Player player, @NotNull String name, @NotNull Location location) {
        if (!Plugins.isLoaded(HookId.PROTOCOL_LIB)) return;

        Set<Integer> list = VISUALS_MAP.computeIfAbsent(player, k -> new HashSet<>());
        Location center = LocationUtil.getCenter(location.clone().add(0, 0.5, 0), false);

        int id = spawnHologram(Lists.newSet(player), center, name);
        list.add(id);
    }

    public static void addVisualBlock(@NotNull Player player, @NotNull Location location) {
        if (!Plugins.isLoaded(HookId.PROTOCOL_LIB)) return;

        Set<Integer> list = VISUALS_MAP.computeIfAbsent(player, k -> new HashSet<>());
        Location clone = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        Location center = LocationUtil.getCenter(clone, false);

        int id = spawnVisualBlock(Lists.newSet(player), center);
        list.add(id);
    }

    public static int spawnHologram(@NotNull Set<Player> players, @NotNull Location location, @NotNull String name) {
        int entityID = nextEntityId();

        PacketContainer spawnPacket = createSpawnPacket(EntityType.ARMOR_STAND, location, entityID);

        players.forEach(player -> {
            Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(NightMessage.asLegacy(name))[0].getHandle());

            PacketContainer dataPacket = createMetadataPacket(entityID, metadata -> {
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20)); //invis
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt); //display name
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true); //custom name visible
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); //no gravity
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker
            });

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnPacket);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, dataPacket);
        });

        return entityID;
    }

    public static int spawnVisualBlock(@NotNull Set<Player> players, @NotNull Location location) {
        int entityID = nextEntityId();

        PacketContainer spawnPacket = createSpawnPacket(EntityType.SHULKER, location, entityID);

        players.forEach(player -> {
            PacketContainer dataPacket = createMetadataPacket(entityID, metadata -> {
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20 | 0x40)); //invis
                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); //no gravity
            });

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnPacket);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, dataPacket);
        });

        return entityID;
    }

    public static void destroyEntity(int... ids) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, IntStream.of(ids).boxed().toList());
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(destroyPacket);
    }

    @NotNull
    private static PacketContainer createSpawnPacket(@NotNull EntityType entityType, @NotNull Location location, int entityID) {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityID);
        spawnPacket.getUUIDs().write(0, UUID.randomUUID());
        spawnPacket.getEntityTypeModifier().write(0, entityType);
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY());
        spawnPacket.getDoubles().write(2, location.getZ());
        return spawnPacket;
    }

    @NotNull
    private static PacketContainer createMetadataPacket(int entityID, @NotNull Consumer<WrappedDataWatcher> consumer) {
        PacketContainer dataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher metadata = new WrappedDataWatcher();

        consumer.accept(metadata);

        List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
        metadata.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
            WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
        });

        dataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        dataPacket.getIntegers().write(0, entityID);

        return dataPacket;
    }
}
