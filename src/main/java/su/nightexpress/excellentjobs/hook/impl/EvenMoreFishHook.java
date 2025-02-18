package su.nightexpress.excellentjobs.hook.impl;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.EMFAPI;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.action.ActionRegistry;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.action.EventHelper;
import su.nightexpress.excellentjobs.action.ObjectFormatter;

public class EvenMoreFishHook {

    private static final EMFAPI API = EvenMoreFish.getInstance().getApi();

    public static void register(@NotNull ActionRegistry registry) {
        registry.registerAction(PlayerFishEvent.class, EventPriority.MONITOR, ACTION_TYPE);
    }

    public static boolean isCustomFish(@NotNull ItemStack itemStack) {
        return API.isFish(itemStack);
    }

    public static final EventHelper<PlayerFishEvent, Fish> EVENT_HELPER = (plugin, event, processor) -> {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return false;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item item)) return false;

        Player player = event.getPlayer();
        ItemStack itemStack = item.getItemStack();

        Fish fish = API.getFish(itemStack);
        if (fish == null) return false;

        processor.progressObjective(player, fish, itemStack.getAmount());
        return true;
    };

    public static final ObjectFormatter<Fish> OBJECT_FORMATTER = new ObjectFormatter<>() {

        @NotNull
        @Override
        public String getName(@NotNull Fish object) {
            return object.getRarity().getId() + ":" + object.getName();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull Fish object) {
            return object.getRarity().getDisplayName() + " " + object.getDisplayName();
        }

        @Nullable
        @Override
        public Fish parseObject(@NotNull String name) {
            String[] split = name.split(":");
            if (split.length < 2) return null;

            return API.getFish(split[0], split[1]);//.orElse(null);
        }
    };

    public static final ActionType<PlayerFishEvent, Fish> ACTION_TYPE = ActionType.create("emf_fish_item", OBJECT_FORMATTER, EVENT_HELPER);
}
