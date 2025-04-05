package su.nightexpress.excellentjobs.config;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;

public class Keys {

    public static NamespacedKey ENTITY_TRACKED;
    public static NamespacedKey BREWING_HOLDER;
    public static NamespacedKey LEVEL_FIREWORK;

    public static NamespacedKey wandItem;
    public static NamespacedKey wandZoneId;

    public static void load(@NotNull JobsPlugin plugin) {
        ENTITY_TRACKED = new NamespacedKey(plugin, "entity.tracked");
        BREWING_HOLDER = new NamespacedKey(plugin, "brewing.holder");
        LEVEL_FIREWORK = new NamespacedKey(plugin, "job.firework");

        wandItem = new NamespacedKey(plugin, "wand.item");
        wandZoneId = new NamespacedKey(plugin, "wand.zone_id");
    }
}
