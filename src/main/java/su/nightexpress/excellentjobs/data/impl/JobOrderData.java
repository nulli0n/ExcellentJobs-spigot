package su.nightexpress.excellentjobs.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.impl.OrderReward;

import java.util.*;

public class JobOrderData {

    private final Map<String, JobOrderObjective> objectiveMap;

    private List<String> rewards;
    private boolean      rewarded;
    private long         expireDate;

    @NotNull
    public static JobOrderData empty() {
        return new JobOrderData(new HashMap<>(), new ArrayList<>(), true, 0L);
    }

    public JobOrderData(@NotNull Map<String, JobOrderObjective> objectiveMap,
                        @NotNull List<String> rewards,
                        boolean rewarded,
                        long expireDate) {
        this.objectiveMap = new HashMap<>(objectiveMap);
        this.setRewards(rewards);
        this.setRewarded(rewarded);
        this.setExpireDate(expireDate);
    }

    public void validateObjectives(@NotNull Job job) {
        this.getObjectiveMap().keySet().removeIf(id -> job.getObjectiveById(id) == null);
    }

    @NotNull
    public List<OrderReward> translateRewards() {
        var rewardMap = Config.SPECIAL_ORDERS_REWARDS.get();

        return this.getRewards().stream().map(rewardMap::get).filter(Objects::nonNull).toList();
    }

    public boolean isEmpty() {
        return this.getObjectiveMap().isEmpty() || this.getExpireDate() == 0L;
    }

    public boolean isExpired() {
        return this.getExpireDate() >= 0L && System.currentTimeMillis() > this.getExpireDate();
    }

    public boolean isCompleted() {
        return !this.isEmpty() && this.getObjectiveMap().values().stream().allMatch(JobOrderObjective::isCompleted);
    }

    public boolean countObjective(@NotNull JobObjective objective, @NotNull String object, int amount) {
        JobOrderObjective orderObjective = this.getObjectiveMap().get(objective.getId());
        if (orderObjective == null) return false;

        return orderObjective.countObject(object, amount);
    }

    @NotNull
    public Map<String, JobOrderObjective> getObjectiveMap() {
        return objectiveMap;
    }

    @NotNull
    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(@NotNull List<String> rewards) {
        this.rewards = rewards;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }
}
