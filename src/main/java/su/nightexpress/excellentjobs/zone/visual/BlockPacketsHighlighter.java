package su.nightexpress.excellentjobs.zone.visual;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.nightcore.util.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BlockPacketsHighlighter extends BlockHighlighter {

    private final PlayerManager manager;

    public BlockPacketsHighlighter(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.manager = PacketEvents.getAPI().getPlayerManager();
    }

    @Override
    protected void spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location) {
        EntityType type = Version.isAtLeast(Version.V1_20_R3) ? EntityType.ITEM_DISPLAY : EntityType.SHULKER;
        var spawnPacket = this.createSpawnPacket(type, location, entityID);

        var dataPacket = this.createMetadataPacket(entityID, dataList -> {
            if (type == EntityType.ITEM_DISPLAY) {
                dataList.add(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x40)); // glow
                dataList.add(new EntityData(12, EntityDataTypes.VECTOR3F, new Vector3f(0.99f, 0.99f, 0.99f))); // scale
                dataList.add(new EntityData(23, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(new ItemStack(location.getBlock().getType())))); // slot
                dataList.add(new EntityData(24, EntityDataTypes.BYTE, (byte) 5)); // mode HEAD
            }
            else {
                dataList.add(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20 | 0x40)); // invisible
                dataList.add(new EntityData(5, EntityDataTypes.BOOLEAN, true)); // no gravity
            }
        });

        this.manager.sendPacket(player, spawnPacket);
        this.manager.sendPacket(player, dataPacket);
    }

    @Override
    protected void destroyEntity(@NotNull Player player, @NotNull List<Integer> idList) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(idList.stream().mapToInt(i -> i).toArray());
        this.manager.sendPacket(player, packet);
    }

    @NotNull
    private WrapperPlayServerSpawnEntity createSpawnPacket(@NotNull EntityType type, @NotNull Location location, int entityID) {
        com.github.retrooper.packetevents.protocol.entity.type.EntityType wrappedType = SpigotConversionUtil.fromBukkitEntityType(type);
        com.github.retrooper.packetevents.protocol.world.Location wrappedLocation = SpigotConversionUtil.fromBukkitLocation(location);

        return new WrapperPlayServerSpawnEntity(entityID, UUID.randomUUID(), wrappedType, wrappedLocation, 0f, 0, new Vector3d());
    }

    @NotNull
    private PacketWrapper<?> createMetadataPacket(int entityID, @NotNull Consumer<List<EntityData>> consumer) {
        List<EntityData> dataList = new ArrayList<>();

        consumer.accept(dataList);

        return new WrapperPlayServerEntityMetadata(entityID, dataList);
    }
}
