package su.nightexpress.excellentjobs.zone.visual;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.nightcore.util.Lists;

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
    protected void spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location, @NotNull BlockData blockData, @NotNull ChatColor color, float size) {
        EntityType type = EntityType.BLOCK_DISPLAY;// Version.isAtLeast(Version.V1_20_R3) ? EntityType.BLOCK_DISPLAY : EntityType.SHULKER;

        UUID uuid = UUID.randomUUID();
        String entityUID = uuid.toString();
        WrappedBlockState state = WrappedBlockState.getByString(blockData.getAsString());

        var spawnPacket = this.createSpawnPacket(type, location, entityID, uuid);

        var dataPacket = this.createMetadataPacket(entityID, dataList -> {
//            if (type == EntityType.BLOCK_DISPLAY) {
                dataList.add(new EntityData(0, EntityDataTypes.BYTE, (byte) (0x20 | 0x40))); // glow
                dataList.add(new EntityData(12, EntityDataTypes.VECTOR3F, new Vector3f(size, size, size))); // scale
                dataList.add(new EntityData(23, EntityDataTypes.BLOCK_STATE, state.getGlobalId())); // block ID
//            }
//            else {
//                dataList.add(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20 | 0x40)); // invisible
//                dataList.add(new EntityData(5, EntityDataTypes.BOOLEAN, true)); // no gravity
//            }

        });

        WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.text(entityUID),
            Component.text(""),
            Component.text(""),
            WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
            WrapperPlayServerTeams.CollisionRule.ALWAYS,
            NamedTextColor.NAMES.valueOr(color.name().toLowerCase(), NamedTextColor.WHITE),
            WrapperPlayServerTeams.OptionData.NONE
        );
        var teamPacket = new WrapperPlayServerTeams(entityUID, WrapperPlayServerTeams.TeamMode.CREATE, info, Lists.newList(entityUID));

        this.manager.sendPacket(player, spawnPacket);
        this.manager.sendPacket(player, teamPacket);
        this.manager.sendPacket(player, dataPacket);
    }

    @Override
    protected void destroyEntity(@NotNull Player player, @NotNull List<Integer> idList) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(idList.stream().mapToInt(i -> i).toArray());
        this.manager.sendPacket(player, packet);
    }

    @NotNull
    private WrapperPlayServerSpawnEntity createSpawnPacket(@NotNull EntityType type, @NotNull Location location, int entityID, @NotNull UUID uuid) {
        com.github.retrooper.packetevents.protocol.entity.type.EntityType wrappedType = SpigotConversionUtil.fromBukkitEntityType(type);
        com.github.retrooper.packetevents.protocol.world.Location wrappedLocation = SpigotConversionUtil.fromBukkitLocation(location);

        return new WrapperPlayServerSpawnEntity(entityID, uuid, wrappedType, wrappedLocation, 0f, 0, new Vector3d());
    }

    @NotNull
    private PacketWrapper<?> createMetadataPacket(int entityID, @NotNull Consumer<List<EntityData>> consumer) {
        List<EntityData> dataList = new ArrayList<>();

        consumer.accept(dataList);

        return new WrapperPlayServerEntityMetadata(entityID, dataList);
    }
}
