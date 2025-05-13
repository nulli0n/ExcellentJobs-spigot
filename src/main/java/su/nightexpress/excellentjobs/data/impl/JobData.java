package su.nightexpress.excellentjobs.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobIncome;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.util.TimeUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class JobData  {

    private final Job          job;
    private final JobIncome    income;
    private final JobLimitData limitData;

    private final Set<Integer> claimedLevelRewards;

    private JobState state;
    private int      level;
    private int      xp;
    private long     cooldown;

    private JobOrderData orderData;
    private long         nextOrderDate;

    @NotNull
    public static JobData create(@NotNull Job job) {
        JobState state = job.getInitialState();
        JobLimitData limitData = JobLimitData.create(job);
        JobOrderData orderData = JobOrderData.empty();
        long nextOrderDate = 0L;
        long cooldown = 0L;

        Set<Integer> obtainedLevelRewards = new HashSet<>();

        return new JobData(job, state, JobUtils.START_LEVEL, JobUtils.START_XP, cooldown, limitData, orderData, nextOrderDate, obtainedLevelRewards);
    }

    public JobData(@NotNull Job job,
                   @NotNull JobState state,
                   int level,
                   int xp,
                   long cooldown,
                   @NotNull JobLimitData limitData,
                   @NotNull JobOrderData orderData,
                   long nextOrderDate,
                   @NotNull Set<Integer> claimedLevelRewards) {
        this.job = job;
        this.income = new JobIncome();
        this.setState(state);
        this.setCooldown(cooldown);
        this.limitData = limitData;
        this.setOrderData(orderData);
        this.setNextOrderDate(nextOrderDate);
        this.claimedLevelRewards = new HashSet<>(claimedLevelRewards);
        this.setLevel(level);
        this.setXP(xp);

        if (!this.getOrderData().isEmpty()) {
            this.getOrderData().validateObjectives(job);
        }
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.JOB_DATA.replacer(this);
    }

    @NotNull
    public UnaryOperator<String> replaceAllPlaceholders() {
        return str -> this.job.replacePlaceholders().apply(this.replacePlaceholders().apply(str));
    }

    public void reset() {
        this.reset(false);
    }

    public void reset(boolean full) {
        this.income.clear();
        this.setLevel(JobUtils.START_LEVEL);
        this.setXP(JobUtils.START_XP);
        this.setOrderData(null); // Do not reset nextOrderDate and LimitData to prevent abuse

        if (full) {
            this.setCooldown(0L);
            this.claimedLevelRewards.clear();
            this.setNextOrderDate(0L);
            this.limitData.getCurrencyEarned().clear();
            this.limitData.setXPEarned(0);
        }
    }

    public void update() {
        int maxLevel = this.getMaxLevel();
        if (this.level > maxLevel) {
            this.setLevel(maxLevel);
            this.setXP(this.getLevelXP());
        }

        this.checkXPLevelUp();
        this.checkXPLevelDown();
    }

    public boolean isOnCooldown() {
        return !TimeUtil.isPassed(this.cooldown);
    }

    public boolean isPaymentLimitReached(@NotNull Currency currency) {
        return this.isPaymentLimitReached(currency.getInternalId());
    }

    public boolean isPaymentLimitReached(@NotNull String id) {
        this.getLimitData().checkExpiration();

        double limit = this.job.getDailyPaymentLimit(id, this.level);
        return limit > 0 && this.getLimitData().getCurrencyEarned(id) >= limit;
    }

    public boolean isXPLimitReached() {
        this.getLimitData().checkExpiration();

        double limit = this.job.getDailyXPLimit(this.level);
        return limit > 0 && this.getLimitData().getXPEarned() >= limit;
    }

    public boolean isActive() {
        return this.state != JobState.INACTIVE;
    }

    public boolean hasOrder() {
        return !this.getOrderData().isEmpty() && !this.getOrderData().isExpired();
    }

    public boolean isOrderCompleted() {
        return this.getOrderData().isCompleted();
    }

    public boolean isReadyForNextOrder() {
        if (this.orderData.isExpired() || !this.hasOrder() || this.orderData.isCompleted()) {
            return System.currentTimeMillis() >= this.getNextOrderDate();
        }
        return false;
    }

    public void removeXP(int amount) {
        this.setXP(this.xp - Math.abs(amount));
        this.checkXPLevelDown();
    }

    public void addXP(int amount) {
        this.setXP(this.xp + Math.abs(amount));
        this.checkXPLevelUp();
    }

    private void upLevel(int leftover) {
        this.setLevel(this.level + 1);
        this.setXP(leftover);
        this.checkXPLevelUp();
    }

    private void downLevel(int leftover) {
        if (this.isStartLevel()) return;

        this.setLevel(this.level - 1);

        int levelXP = this.getLevelXP();

        this.setXP(levelXP + leftover);
        this.checkXPLevelDown();
    }

    private void checkXPLevelUp() {
        int levelXP = this.getLevelXP();
        if (this.xp < levelXP) return;

        if (this.isMaxLevel()) {
            this.setXP(levelXP);
        }
        else {
            this.upLevel(this.xp - levelXP);
        }
    }

    private void checkXPLevelDown() {
        int levelAntiXP = -this.getLevelXP();
        if (this.xp > levelAntiXP) return;

        if (this.isStartLevel()) {
            this.setXP(levelAntiXP);
        }
        else {
            this.downLevel(this.xp - levelAntiXP);
        }
    }

    @NotNull
    public JobIncome getIncome() {
        return this.income;
    }

    @NotNull
    public Job getJob() {
        return this.job;
    }

    @NotNull
    public JobState getState() {
        return this.state;
    }

    public void setState(@NotNull JobState state) {
        this.state = state;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isStartLevel() {
        return this.level == JobUtils.START_LEVEL;
    }

    public boolean isMaxLevel() {
        return this.level == this.getMaxLevel();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = Math.max(JobUtils.START_LEVEL, Math.min(level, this.getMaxLevel()));
    }

    public int getMaxLevel() {
        return this.job.getMaxLevel(this.state);
    }

    public int getXP() {
        return this.xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public int getLevelXP() {
        return this.job.getXPToLevel(this.level);
    }

    public int getXPToLevelUp() {
        return this.getLevelXP() - this.xp;
    }

    public int getXPToLevelDown() {
        return this.xp + this.getLevelXP();
    }

    @NotNull
    public JobLimitData getLimitData() {
        return limitData;
    }

    @NotNull
    public JobOrderData getOrderData() {
        return orderData;
    }

    public void setOrderData(@Nullable JobOrderData orderData) {
        this.orderData = orderData == null ? JobOrderData.empty() : orderData;
    }

    public long getNextOrderDate() {
        return nextOrderDate;
    }

    public void setNextOrderDate(long nextOrderDate) {
        this.nextOrderDate = nextOrderDate;
    }

    @NotNull
    public Set<Integer> getClaimedLevelRewards() {
        return claimedLevelRewards;
    }

    public boolean isLevelRewardObtained(int level) {
        return this.claimedLevelRewards.contains(level);
    }

    public void setLevelRewardObtained(int level) {
        this.claimedLevelRewards.add(level);
    }
}
