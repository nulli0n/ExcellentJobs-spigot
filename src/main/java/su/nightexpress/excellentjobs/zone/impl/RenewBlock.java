package su.nightexpress.excellentjobs.zone.impl;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;

public class RenewBlock {

    private final BlockData blockData;
    private final long resetTime;

    public RenewBlock(@NotNull BlockData blockData, long resetTime) {
        this.blockData = blockData;
        this.resetTime = resetTime;
    }

    public boolean isReady() {
        return TimeUtil.isPassed(this.resetTime);
    }

    @NotNull
    public BlockData getBlockData() {
        return this.blockData;
    }

    public long getResetTime() {
        return this.resetTime;
    }
}
