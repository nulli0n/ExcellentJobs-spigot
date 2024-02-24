package su.nightexpress.excellentjobs.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.NumberUtil;

public class BlockEyePos extends BlockPos {

    private final float pitch, yaw;

    public BlockEyePos(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public BlockEyePos(double x, double y, double z, float pitch, float yaw) {
        super(x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @NotNull
    public static BlockEyePos from(@NotNull Location location) {
        return new BlockEyePos(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    @NotNull
    public static BlockEyePos read(@NotNull FileConfig cfg, @NotNull String path) {
        String str = cfg.getString(path, "");
        return deserialize(str);
    }

    @NotNull
    public static BlockEyePos deserialize(@NotNull String str) {
        String[] split = str.split(",");
        if (split.length < 5) return new BlockEyePos(0, 0, 0);

        double x = NumberUtil.getAnyDouble(split[0], 0D);
        double y = NumberUtil.getAnyDouble(split[1], 0D);
        double z = NumberUtil.getAnyDouble(split[2], 0D);
        float pitch = (float) NumberUtil.getAnyDouble(split[3], 0D);
        float yaw = (float) NumberUtil.getAnyDouble(split[4], 0D);

        return new BlockEyePos(x, y, z, pitch, yaw);
    }

    @Override
    @NotNull
    public String serialize() {
        return this.getX() + "," + this.getY() + "," + this.getZ() + "," + this.getPitch() + "," + this.getYaw();
    }

    @Override
    @NotNull
    public Location toLocation(@NotNull World world) {
        return new Location(world, this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    @Override
    @NotNull
    public BlockEyePos copy() {
        return new BlockEyePos(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof BlockEyePos other)) return false;

        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
            return false;
        }
        return Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
    }

    public int hashCode() {
        int hash = 3;
        hash = 19 * hash;
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public String toString() {
        return "BlockPos{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            ", pitch=" + pitch +
            ", yaw=" + yaw +
            '}';
    }
}
