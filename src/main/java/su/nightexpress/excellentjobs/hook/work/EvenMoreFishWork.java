package su.nightexpress.excellentjobs.hook.work;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.EMFAPI;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;

public class EvenMoreFishWork extends Work<PlayerFishEvent, Fish> implements WorkFormatter<Fish> {

    private static final EMFAPI API = EvenMoreFish.getInstance().getApi();

    public EvenMoreFishWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, PlayerFishEvent.class, id);
    }

    public static boolean isCustomFish(@NotNull ItemStack itemStack) {
        return API.isFish(itemStack);
    }

    @Override
    @NotNull
    public WorkFormatter<Fish> getFormatter() {
        return this;
    }

    @Override
    public boolean handle(@NotNull PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return false;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item item)) return false;

        Player player = event.getPlayer();
        ItemStack itemStack = item.getItemStack();

        Fish fish = API.getFish(itemStack);
        if (fish == null) return false;

        this.doObjective(player, fish, itemStack.getAmount());
        return true;
    }

    @NotNull
    @Override
    public String getName(@NotNull Fish object) {
        return object.getRarity().getId() + ":" + object.getName();
    }

    @NotNull
    @Override
    public String getLocalized(@NotNull Fish object) {
        return object.getRarity().getDisplayName() + " " + object.getDisplayName();
    }

    @Nullable
    @Override
    public Fish parseObject(@NotNull String name) {
        String[] split = name.split(":");
        if (split.length < 2) return null;

        return API.getFish(split[0], split[1]);//.orElse(null);
    }
}
