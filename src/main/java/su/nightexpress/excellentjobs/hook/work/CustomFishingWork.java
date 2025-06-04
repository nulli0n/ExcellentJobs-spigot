package su.nightexpress.excellentjobs.hook.work;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.event.FishingResultEvent;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;

public class CustomFishingWork extends Work<FishingResultEvent, Loot> implements WorkFormatter<Loot> {

    public CustomFishingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, FishingResultEvent.class, id);
    }

    public static BukkitCustomFishingPlugin getAPI() {
        return BukkitCustomFishingPlugin.getInstance();
    }

    @NotNull
    @Override
    public String getName(@NotNull Loot loot) {
        return loot.id();
    }

    @NotNull
    @Override
    public String getLocalized(@NotNull Loot loot) {
        return loot.nick();
    }

    @Nullable
    @Override
    public Loot parseObject(@NotNull String name) {
        return getAPI().getLootManager().getRegisteredLoots().stream().filter(loot -> loot.id().equalsIgnoreCase(name)).findFirst().orElse(null);
        //return getAPI().getLootManager().getLoot(name).orElse(null);
    }

    @Override
    public boolean handle(@NotNull FishingResultEvent event) {
        if (event.getResult() != FishingResultEvent.Result.SUCCESS) return false;

        Loot loot = event.getLoot();
        Player player = event.getPlayer();

        this.doObjective(player, loot, event.getAmount(), 0D);
        return true;
    }

    @Override
    @NotNull
    public WorkFormatter<Loot> getFormatter() {
        return this;
    }
}
