package su.nightexpress.excellentjobs.grind.table.impl;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.table.SourceTable;

public class BasicBlockGrindTable implements GrindTable {

    private final SourceTable blocksTable;

    public BasicBlockGrindTable(@NotNull SourceTable blocksTable) {
        this.blocksTable = blocksTable;
    }

    @NotNull
    public static BasicBlockGrindTable read(@NotNull FileConfig config, @NotNull String path) {
        SourceTable blocks = ConfigValue.create(path + ".Blocks", SourceTable::read, SourceTable.EMPTY).read(config);

        return new BasicBlockGrindTable(blocks);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Blocks", this.blocksTable);
    }

    @NotNull
    public GrindReward getBlockXP(@NotNull Block block) {
        return this.blocksTable.rollForEntityOrDefault(block, GrindAdapterFamily.BLOCK);
    }

    @NotNull
    public GrindReward getBlockXP(@NotNull BlockState block) {
        return this.blocksTable.rollForEntityOrDefault(block, GrindAdapterFamily.BLOCK_STATE);
    }
}
