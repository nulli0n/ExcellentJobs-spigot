package su.nightexpress.excellentjobs.zone.impl;

import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.util.Cuboid;
import su.nightexpress.excellentjobs.util.pos.BlockPos;

public class Selection {

    //private final Zone zone;

    private BlockPos first;
    private BlockPos second;

    public Selection(/*@NotNull Zone zone*/) {
        //this.zone = zone;
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

//    @NotNull
//    public Zone getZone() {
//        return zone;
//    }

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
