package su.nightexpress.excellentjobs.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.util.pos.BlockPos;

public class Cuboid {

    private final BlockPos min;
    private final BlockPos max;
    private final BlockPos center;

    public Cuboid(@NotNull BlockPos min, @NotNull BlockPos max) {
        int minX = Math.min(min.getX(), max.getX());
        int minY = Math.min(min.getY(), max.getY());
        int minZ = Math.min(min.getZ(), max.getZ());

        int maxX = Math.max(min.getX(), max.getX());
        int maxY = Math.max(min.getY(), max.getY());
        int maxZ = Math.max(min.getZ(), max.getZ());

        this.min = new BlockPos(minX, minY, minZ);
        this.max = new BlockPos(maxX, maxY, maxZ);

        int cx = (int) (minX + (maxX - minX) / 2D);
        int cy = (int) (minY + (maxY - minY) / 2D);
        int cz = (int) (minZ + (maxZ - minZ) / 2D);

        this.center = new BlockPos(cx, cy, cz);
    }

    public boolean isEmpty() {
        return this.getMin().isEmpty() && this.getMax().isEmpty();
    }

    public boolean contains(@NotNull Location location) {
        return this.contains(BlockPos.from(location));
    }

    public boolean contains(@NotNull BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return x >= min.getX() && x <= max.getX() &&
            y >= min.getY() && y <= max.getY() &&
            z >= min.getZ() && z <= max.getZ();
    }

    public boolean isIntersectingWith(@NotNull Cuboid other) {
        return other.includedIn(this) || this.includedIn(other);
    }

    boolean checkIntersect(float min1, float max1, float min2, float max2) {
        return min1 <= max2 && max1 >= min2;
    }

    public boolean includedIn(@NotNull Cuboid other) {
        if (!this.checkIntersect(this.min.getX(), this.max.getX(), other.getMin().getX(), other.getMax().getX())) return false;
        if (!this.checkIntersect(this.min.getY(), this.max.getY(), other.getMin().getY(), other.getMax().getY())) return false;
        if (!this.checkIntersect(this.min.getZ(), this.max.getZ(), other.getMin().getZ(), other.getMax().getZ())) return false;

        return true;
    }

    @NotNull
    public BlockPos getMin() {
        return this.min;
    }

    @NotNull
    public BlockPos getMax() {
        return this.max;
    }

    @NotNull
    public BlockPos getCenter() {
        return this.center;
    }
}
