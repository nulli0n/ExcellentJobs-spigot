package su.nightexpress.excellentjobs.job.workstation;

import org.jetbrains.annotations.Nullable;

public enum WorkstationMode {

    MANUAL(0),
    AUTO(1);

    private final int id;

    WorkstationMode(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Nullable
    public static WorkstationMode byId(int id) {
        for (WorkstationMode mode : WorkstationMode.values()) {
            if (mode.id == id) return mode;
        }
        return null;
    }
}
