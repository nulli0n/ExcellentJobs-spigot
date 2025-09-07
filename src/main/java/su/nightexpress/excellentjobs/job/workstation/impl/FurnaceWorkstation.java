package su.nightexpress.excellentjobs.job.workstation.impl;

import org.bukkit.block.Furnace;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.job.workstation.AbstractWorkstation;
import su.nightexpress.excellentjobs.job.workstation.WorkstationType;

public class FurnaceWorkstation extends AbstractWorkstation<Furnace> {

    public FurnaceWorkstation(@NotNull WorkstationType type, @NotNull Furnace backend) {
        super(type, backend);
    }

    @Override
    public boolean isCrafting() {
        return this.backend.getCookTime() > 0;
    }
}
