package su.nightexpress.excellentjobs.data.impl;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JobOrderObjective {

    private final String                     objectiveId;
    private final Map<String, JobOrderCount> objectCountMap;

    public JobOrderObjective(@NotNull String objectiveId, @NotNull Map<String, JobOrderCount> objectCountMap) {
        this.objectiveId = objectiveId.toLowerCase();
        this.objectCountMap = new HashMap<>(objectCountMap);
    }

    @NotNull
    public String getObjectiveId() {
        return objectiveId;
    }

    public boolean isCompleted() {
        return this.getObjectCountMap().values().stream().allMatch(JobOrderCount::isCompleted);
    }

    public boolean countObject(@NotNull String id, int amount) {
        JobOrderCount count = this.getObjectCountMap().get(id.toLowerCase());
        if (count == null) return false;

        int required = count.getRequired();
        int progress = Math.min(count.getCurrent() + amount, required);
        count.setCurrent(progress);

        return true;
    }

    @NotNull
    public Map<String, JobOrderCount> getObjectCountMap() {
        return objectCountMap;
    }
}
