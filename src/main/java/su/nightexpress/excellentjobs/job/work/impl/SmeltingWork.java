package su.nightexpress.excellentjobs.job.work.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkFormatter;
import su.nightexpress.excellentjobs.job.work.WorkFormatters;

public class SmeltingWork extends Work<FurnaceExtractEvent, Material> {

    public SmeltingWork(@NotNull JobsPlugin plugin, @NotNull String id) {
        super(plugin, FurnaceExtractEvent.class, id);
    }

    @Override
    @NotNull
    public WorkFormatter<Material> getFormatter() {
        return WorkFormatters.MATERIAL;
    }

    @Override
    public boolean handle(@NotNull FurnaceExtractEvent event) {
        Player player = event.getPlayer();

        Material material = event.getItemType();
        int amount = event.getItemAmount();

        this.doObjective(player, material, amount);
        return true;
    }
}
