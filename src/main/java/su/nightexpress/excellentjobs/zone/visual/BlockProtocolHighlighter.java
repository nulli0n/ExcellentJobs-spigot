package su.nightexpress.excellentjobs.zone.visual;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.nightcore.util.Lists;

import java.util.*;
import java.util.function.Consumer;

public class BlockProtocolHighlighter extends BlockHighlighter {

    private final ProtocolManager manager;

    public BlockProtocolHighlighter(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    protected void spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location, @NotNull BlockData blockData, @NotNull ChatColor color, float size) {
        EntityType type = EntityType.BLOCK_DISPLAY;
        UUID uuid = UUID.randomUUID();
        String entityUID = uuid.toString();

        PacketContainer spawnPacket = this.createSpawnPacket(type, location, entityID, uuid);

        PacketContainer dataPacket = this.createMetadataPacket(entityID, metadata -> {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20 | 0x40)); //invis
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(12, WrappedDataWatcher.Registry.get(Vector3f.class)), new Vector3f(size, size, size)); // scale
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(23, WrappedDataWatcher.Registry.getBlockDataSerializer(false)), WrappedBlockData.createData(blockData)); // slot
        });


        PacketContainer teamPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        teamPacket.getStrings().write(0, entityUID); // Name
        teamPacket.getIntegers().write(0, 0); // Mode. 1 - Remove, 0 - Create
        teamPacket.getSpecificModifier(Collection.class).write(0, Lists.newList(entityUID));

        WrappedTeamParameters parameters = WrappedTeamParameters.newBuilder()
            .displayName(WrappedChatComponent.fromText(entityUID))
            .prefix(WrappedChatComponent.fromText(""))
            .suffix(WrappedChatComponent.fromText(""))
            .color(EnumWrappers.ChatFormatting.fromBukkit(color))
            .nametagVisibility(Team.OptionStatus.ALWAYS.name())
            .collisionRule(Team.OptionStatus.ALWAYS.name())
            .options(0)
            .build();
        teamPacket.getOptionalTeamParameters().write(0, Optional.of(parameters));


        this.manager.sendServerPacket(player, spawnPacket);
        this.manager.sendServerPacket(player, teamPacket);
        this.manager.sendServerPacket(player, dataPacket);

        //return new FakeEntity(entityID, uuid);
    }

    @Override
    protected void destroyEntity(@NotNull Player player, @NotNull List<Integer> idList) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, idList);
        this.manager.sendServerPacket(player, destroyPacket);
    }

    @NotNull
    private PacketContainer createSpawnPacket(@NotNull EntityType entityType, @NotNull Location location, int entityID, @NotNull UUID uuid) {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityID);
        spawnPacket.getUUIDs().write(0, uuid);
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
