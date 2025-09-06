package su.nightexpress.excellentjobs.zone.impl;

import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public class Selection {

    private BlockPos first;
    private BlockPos second;

    public Selection() {
    }

    public void clear() {
        this.setFirst(null);
        this.setSecond(null);
    }

    public boolean isIncompleted() {
        return !this.isCompleted();
    }

    public boolean isCompleted() {
        return this.first != null && this.second != null;
    }

    @Nullable
    public BlockPos getFirst() {
        return first;
    }

    public void setFirst(@Nullable BlockPos first) {
        this.first = first;
    }

    @Nullable
    public BlockPos getSecond() {
        return second;
    }

    public void setSecond(@Nullable BlockPos second) {
        this.second = second;
    }

    @Nullable
    public Cuboid toCuboid() {
        return this.isIncompleted() ? null : new Cuboid(this.first, this.second);
    }
}
