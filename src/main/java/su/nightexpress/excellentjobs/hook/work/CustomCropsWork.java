package su.nightexpress.excellentjobs.hook.work;

import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;

public class CustomCropsWork extends Work<CropBreakEvent, CropConfig> implements WorkFormatter<CropConfig> {

    public CustomCropsWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, CropBreakEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<CropConfig> getFormatter() {
        return this;
    }

    @Override
    public boolean handle(@NotNull CropBreakEvent event) {
        if (!(event.entityBreaker() instanceof Player player)) return false;

        CropConfig cropConfig = event.cropConfig();

        this.doObjective(player, cropConfig, 1);
        return true;
    }

    @Override
    @NotNull
    public String getName(@NotNull CropConfig object) {
        return object.id();
    }

    @Override
    @NotNull
    public String getLocalized(@NotNull CropConfig object) {
        return object.id();
    }

    @Override
    @Nullable
    public CropConfig parseObject(@NotNull String name) {
        return Registries.CROP.get(name);
    }
}
