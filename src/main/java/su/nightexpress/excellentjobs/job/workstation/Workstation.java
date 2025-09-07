package su.nightexpress.excellentjobs.job.workstation;

import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.job.workstation.impl.BrewingWorkstation;
import su.nightexpress.excellentjobs.job.workstation.impl.CampfireWorkstation;
import su.nightexpress.excellentjobs.job.workstation.impl.FurnaceWorkstation;

public interface Workstation {

    @Nullable
    static Workstation getByBlock(@NotNull Block block) {
        return getByState(block.getState());
    }

    @Nullable
    static Workstation getByInventory(@NotNull Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof BlockState state)) return null;

        return getByState(state);
    }

    @Nullable
    private static Workstation getByState(@NotNull BlockState state) {
        return switch (state) {
            case BrewingStand brewingStand -> new BrewingWorkstation(brewingStand);
            case BlastFurnace blastFurnace -> new FurnaceWorkstation(WorkstationType.BLAST_FURNACE, blastFurnace);
            case Smoker smoker -> new FurnaceWorkstation(WorkstationType.SMOKER, smoker);
            case Furnace furnace -> new FurnaceWorkstation(WorkstationType.FURNACE, furnace);
            case Campfire campfire -> new CampfireWorkstation(campfire);
            default -> null;
        };
    }

    @NotNull WorkstationType getType();

    @NotNull TileState getBackend();

    void update();

    boolean isCrafting();

    /*int getRemainingTime();

    void setRemainingTime(int remainingTime);*/
}
