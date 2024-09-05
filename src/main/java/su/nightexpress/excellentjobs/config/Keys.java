package su.nightexpress.excellentjobs.config;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;

public class Keys {

    public static final NamespacedKey ENTITY_TRACKED = new NamespacedKey(JobsAPI.PLUGIN, "entity.tracked");

    public static final NamespacedKey BREWING_HOLDER = new NamespacedKey(JobsAPI.PLUGIN, "brewing.holder");

    public static final NamespacedKey LEVEL_FIREWORK = new NamespacedKey(JobsAPI.PLUGIN, "job.firework");

    public static NamespacedKey wandItem;
    public static NamespacedKey wandZoneId;

    public static void load(@NotNull JobsPlugin plugin) {
        wandItem = new NamespacedKey(plugin, "wand.item");
        wandZoneId = new NamespacedKey(plugin, "wand.zone_id");
    }
}
