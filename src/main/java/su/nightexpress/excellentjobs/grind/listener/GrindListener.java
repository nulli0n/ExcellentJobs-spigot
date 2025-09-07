package su.nightexpress.excellentjobs.grind.listener;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.GrindCalculator;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;

public abstract class GrindListener<E extends GrindTable, T extends GrindType<E>> extends AbstractListener<JobsPlugin> {

    protected final GrindManager grindManager;
    protected final T            grindType;

    public GrindListener(@NotNull JobsPlugin plugin, @NotNull GrindManager grindManager, @NotNull T grindType) {
        super(plugin);
        this.grindManager = grindManager;
        this.grindType = grindType;
    }

    protected void giveXP(@NotNull Player player, @NotNull GrindCalculator<E> calculator) {
        this.giveXP(player, null, calculator);
    }

    protected void giveXP(@NotNull Player player, @Nullable ItemStack tool, @NotNull GrindCalculator<E> calculator) {
        this.grindManager.giveXP(player, tool, this.grindType, calculator);
    }
}
