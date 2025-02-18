package su.nightexpress.excellentjobs.hook.impl;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.event.FishingResultEvent;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.action.ActionRegistry;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.action.EventHelper;
import su.nightexpress.excellentjobs.action.ObjectFormatter;

public class CustomFishingHook {

    private static BukkitCustomFishingPlugin getAPI() {
        return BukkitCustomFishingPlugin.getInstance();
    }

    public static void register(@NotNull ActionRegistry registry) {
        registry.registerAction(FishingResultEvent.class, EventPriority.MONITOR, ACTION_TYPE);
    }

    public static final EventHelper<FishingResultEvent, Loot> EVENT_HELPER = (plugin, event, processor) -> {
        if (event.getResult() != FishingResultEvent.Result.SUCCESS) return false;

        Loot loot = event.getLoot();
        Player player = event.getPlayer();

        processor.progressObjective(player, loot, event.getAmount());
        return true;
    };

    public static final ObjectFormatter<Loot> OBJECT_FORMATTER = new ObjectFormatter<>() {

        @NotNull
        @Override
        public String getName(@NotNull Loot loot) {
            return loot.id();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull Loot loot) {
            return loot.nick();
        }

        @Nullable
        @Override
        public Loot parseObject(@NotNull String name) {
            return getAPI().getLootManager().getLoot(name).orElse(null);
        }
    };

    public static final ActionType<FishingResultEvent, Loot> ACTION_TYPE = ActionType.create("custom_fishing", OBJECT_FORMATTER, EVENT_HELPER);
}
