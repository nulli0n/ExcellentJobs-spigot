package su.nightexpress.excellentjobs.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.NumberUtil;

public class BlockPos {

    protected final double x,y,z;

    public BlockPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NotNull
    public static BlockPos empty() {
        return new BlockPos(0, 0, 0);
    }

    @NotNull
    public static BlockPos from(@NotNull Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    public static BlockPos read(@NotNull FileConfig cfg, @NotNull String path) {
        String str = cfg.getString(path, "");
        return deserialize(str);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path, this.serialize());
    }

    @NotNull
    public static BlockPos deserialize(@NotNull String str) {
        String[] split = str.split(",");
        if (split.length < 3) return empty();

        double x = NumberUtil.getAnyDouble(split[0], 0D);
        double y = NumberUtil.getAnyDouble(split[1], 0D);
        double z = NumberUtil.getAnyDouble(split[2], 0D);

        return new BlockPos(x, y, z);
    }

    @NotNull
    public String serialize() {
        return this.getX() + "," + this.getY() + "," + this.getZ();
    }

    @NotNull
    public Location toLocation(@NotNull World world) {
        return new Location(world, this.getX(), this.getY(), this.getZ());
    }

    @NotNull
    public Chunk toChunk(@NotNull World world) {
        int chunkX = (int) this.getX() >> 4;
        int chunkZ = (int) this.getZ() >> 4;

        return world.getChunkAt(chunkX, chunkZ);
    }

    public boolean isChunkLoaded(@NotNull World world) {
        int chunkX = (int) this.getX() >> 4;
        int chunkZ = (int) this.getZ() >> 4;

        return world.isChunkLoaded(chunkX, chunkZ);
    }

    @NotNull
    public BlockPos copy() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }

    public boolean isEmpty() {
        return this.getX() == 0D && this.getY() == 0D && this.getZ() == 0D;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof BlockPos other)) return false;

        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
    }

    public int hashCode() {
        int hash = 3;
        hash = 19 * hash;
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        return hash;
    }

    @Override
    public String toString() {
        return "BlockPos{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            '}';
    }
}
