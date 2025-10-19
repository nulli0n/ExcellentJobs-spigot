package su.nightexpress.excellentjobs.job.workstation.impl;

import org.bukkit.block.BrewingStand;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.job.workstation.AbstractWorkstation;
import su.nightexpress.excellentjobs.job.workstation.WorkstationType;

public class BrewingWorkstation extends AbstractWorkstation<BrewingStand> {

    public BrewingWorkstation(@NotNull BrewingStand backend) {
        super(WorkstationType.BREWING_STAND, backend);
    }

    @Override
    public boolean isCrafting() {
        return this.backend.getBrewingTime() > 0;
    }
}
