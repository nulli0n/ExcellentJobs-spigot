package su.nightexpress.excellentjobs.config;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;

public class Keys {

    public static NamespacedKey levelFirework;

    public static NamespacedKey wandItem;
    public static NamespacedKey wandZoneId;

    public static void load(@NotNull JobsPlugin plugin) {
        levelFirework = new NamespacedKey(plugin, "job.firework");

        wandItem = new NamespacedKey(plugin, "wand.item");
        wandZoneId = new NamespacedKey(plugin, "wand.zone_id");
    }

    public static void clear() {
        levelFirework = null;

        wandItem = null;
        wandZoneId = null;
    }
}
