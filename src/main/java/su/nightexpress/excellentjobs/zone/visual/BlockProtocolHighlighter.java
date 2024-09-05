package su.nightexpress.excellentjobs.zone.visual;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class BlockProtocolHighlighter extends BlockHighlighter {

    private final ProtocolManager manager;

    public BlockProtocolHighlighter(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    protected void spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location) {
        EntityType type = EntityType.SHULKER;
        PacketContainer spawnPacket = this.createSpawnPacket(type, location, entityID);

        PacketContainer dataPacket = this.createMetadataPacket(entityID, metadata -> {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20 | 0x40)); //invis
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); //no gravity
        });

        this.manager.sendServerPacket(player, spawnPacket);
        this.manager.sendServerPacket(player, dataPacket);
    }

    @Override
    protected void destroyEntity(@NotNull Player player, @NotNull List<Integer> idList) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, idList);
        this.manager.sendServerPacket(player, destroyPacket);
    }

    @NotNull
    private PacketContainer createSpawnPacket(@NotNull EntityType entityType, @NotNull Location location, int entityID) {
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
    private PacketContainer createMetadataPacket(int entityID, @NotNull Consumer<WrappedDataWatcher> consumer) {
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
