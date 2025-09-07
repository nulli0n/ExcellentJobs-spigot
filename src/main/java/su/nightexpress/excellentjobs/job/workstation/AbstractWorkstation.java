package su.nightexpress.excellentjobs.job.workstation;

import org.bukkit.block.TileState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWorkstation <B extends TileState> implements Workstation {

    protected final WorkstationType type;
    protected final B               backend;

    public AbstractWorkstation(@NotNull WorkstationType type, @NotNull B backend) {
        this.type = type;
        this.backend = backend;
    }

    @Override
    public void update() {
        this.backend.update(true, false);
    }

    @Override
    @NotNull
    public WorkstationType getType() {
        return this.type;
    }

    @Override
    @NotNull
    public B getBackend() {
        return this.backend;
    }
}
