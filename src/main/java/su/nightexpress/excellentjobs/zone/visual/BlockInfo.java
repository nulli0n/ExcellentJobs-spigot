package su.nightexpress.excellentjobs.zone.visual;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public class BlockInfo {

    private final BlockPos blockPos;
    private final BlockData blockData;

    public BlockInfo(@NotNull BlockPos blockPos, @NotNull BlockData blockData) {
        this.blockPos = blockPos;
        this.blockData = blockData;
    }

    @NotNull
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @NotNull
    public BlockData getBlockData() {
        return this.blockData;
    }
}
