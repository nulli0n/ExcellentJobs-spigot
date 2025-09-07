package su.nightexpress.excellentjobs.grind.adapter.impl;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.EMFAPI;
import com.oheers.fish.fishing.items.Fish;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.grind.adapter.AbstractGrindAdapter;

public class EvenMoreFishAdapter extends AbstractGrindAdapter<Fish, ItemStack> {

    private static final EMFAPI API = EvenMoreFish.getInstance().getApi();
    private static final String DELIMITER = ":";

    public EvenMoreFishAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull ItemStack itemStack) {
        return API.isFish(itemStack);
    }

    @Override
    @Nullable
    public Fish getTypeByName(@NotNull String name) {
        String[] split = name.split(DELIMITER);
        if (split.length < 2) return null;

        return API.getFish(split[0], split[1]);
    }

    @Override
    @Nullable
    public Fish getType(@NotNull ItemStack itemStack) {
        return API.getFish(itemStack);
    }

    @Override
    @NotNull
    public String getName(@NotNull Fish fish) {
        return fish.getRarity().getId() + DELIMITER + fish.getName();
    }

    @Override
    @NotNull
    public String toFullNameOfType(@NotNull Fish fish) {
        return "evenmorefish:" + super.toFullNameOfType(fish);
    }
}
