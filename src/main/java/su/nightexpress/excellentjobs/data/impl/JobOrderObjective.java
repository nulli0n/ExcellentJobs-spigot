package su.nightexpress.excellentjobs.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.Placeholders;

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
        return this.objectCountMap.values().stream().allMatch(JobOrderCount::isCompleted);
    }

    @Nullable
    public JobOrderCount getCount(@NotNull String objectId) {
        return this.objectCountMap.getOrDefault(objectId.toLowerCase(), this.objectCountMap.get(Placeholders.WILDCARD));
    }

    public boolean countObject(@NotNull String id, int amount) {
        JobOrderCount count = this.getCount(id);
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
