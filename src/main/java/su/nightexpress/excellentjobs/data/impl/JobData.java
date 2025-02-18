package su.nightexpress.excellentjobs.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class JobData  {

    private final Job          job;
    private final JobLimitData limitData;

    private JobOrderData orderData;
    private long         nextOrderDate;

    private JobState state;
    private int      level;
    private int      xp;

    private final Set<Integer>   obtainedLevelRewards;

    @NotNull
    public static JobData create(@NotNull Job job) {
        JobLimitData limitData = JobLimitData.create(job);

        JobOrderData orderData = JobOrderData.empty();
        long nextOrderDate = 0L;

        int level = 0;
        int xp = 0;
        JobState state = job.getInitialState();

        Set<Integer> obtainedLevelRewards = new HashSet<>();

        return new JobData(job, state, level, xp, limitData, orderData, nextOrderDate, obtainedLevelRewards);
    }

    public JobData(@NotNull Job job,
                   @NotNull JobState state,
                   int level,
                   int xp,
                   @NotNull JobLimitData limitData,
                   @NotNull JobOrderData orderData,
                   long nextOrderDate,
                   @NotNull Set<Integer> obtainedLevelRewards) {
        this.job = job;
        this.setState(state);
        this.limitData = limitData;
        this.setOrderData(orderData);
        this.setNextOrderDate(nextOrderDate);
        this.obtainedLevelRewards = new HashSet<>(obtainedLevelRewards);
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
        this.setLevel(0);
        this.setXP(0);
        this.setOrderData(null);
        // Do not reset nextOrderDate and LimitData to prevent abuse

        this.update();
    }


    public void update() {
        boolean isMaxLevel = this.getLevel() >= this.getMaxLevel();
        if (this.getXP() >= this.getMaxXP() && (!isMaxLevel)) {
            this.upLevel(this.getXP() - this.getMaxXP());
        }

        if (this.getXP() <= -(this.getMaxXP()) && (this.getLevel() > 1)) {
            this.downLevel(this.getXP() + (this.getMaxXP()));
        }
    }

    public void normalize() {
        if (this.getLevel() > this.getMaxLevel()) {
            this.setLevel(this.getMaxLevel());
        }

        boolean isMaxLevel = this.getLevel() >= this.getMaxLevel();
        if (this.getXP() >= this.getMaxXP()) {
            if ((!isMaxLevel)) {
                this.upLevel(this.getXP() - this.getMaxXP());
            }
            else this.setXP(this.getMaxXP());
        }

        boolean isFirstLevel = this.getLevel() == 1;
        if (this.getXP() <= -(this.getMaxXP())) {
            if ((!isFirstLevel)) {
                this.downLevel(this.getXP() + (this.getMaxXP()));
            }
            else this.setXP(-this.getMaxXP());
        }
    }

    public boolean isPaymentLimitReached(@NotNull Currency currency) {
        return this.isPaymentLimitReached(currency.getInternalId());
    }

    public boolean isPaymentLimitReached(@NotNull String id) {
        this.getLimitData().checkExpiration();

        double limit = this.getJob().getDailyPaymentLimit(id, this.getLevel());
        return limit > 0 && this.getLimitData().getCurrencyEarned(id) >= limit;
    }

    public boolean isXPLimitReached() {
        this.getLimitData().checkExpiration();

        double limit = this.getJob().getDailyXPLimit(this.getLevel());
        return limit > 0 && this.getLimitData().getXPEarned() >= limit;
    }

    public boolean isActive() {
        return this.getState() != JobState.INACTIVE;
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
        amount = Math.abs(amount);

        int toDown = this.getXPToLevelDown();
        if (amount >= toDown) {
            boolean isFirstLevel = this.getLevel() == 1;

            if (isFirstLevel) {
                this.setXP(-this.getMaxXP());
            }
            else {
                this.downLevel(amount - toDown);
            }
            return;
        }

        this.setXP(this.getXP() - amount);
    }

    public void addXP(int amount) {
        amount = Math.abs(amount);

        int toUp = this.getXPToLevelUp();
        if (amount >= toUp) {
            boolean isMaxLevel = this.getLevel() >= this.getMaxLevel();

            if (isMaxLevel) {
                this.setXP(this.getMaxXP());
            }
            else {
                this.upLevel(amount - toUp);
            }
            return;
        }

        this.setXP(this.getXP() + amount);
    }

    public void upLevel(int expLeft) {
        this.setLevel(this.getLevel() + 1);

        int expReq = this.getMaxXP();
        if (expReq <= 0) expReq = this.getJob().getInitialXP();

        this.setXP(expLeft);

        if (this.getXP() >= expReq) {
            if (this.getLevel() >= this.getMaxLevel()) {
                this.addXP(1);
            }
            else {
                this.upLevel(expLeft - expReq);
            }
        }
    }

    public void downLevel(int expLeft) {
        if (this.getLevel() == 1) return;
        this.setLevel(this.getLevel() - 1);

        int expMax = this.getMaxXP();
        if (expMax <= 0) expMax = this.getJob().getInitialXP();

        this.setXP(-Math.abs(expLeft));

        int expDown = -(expMax);
        if (this.getXP() <= expDown) {
            if (this.getLevel() == 1) {
                this.setXP(-this.getXPToLevelDown());
            }
            else {
                this.downLevel((this.getXP() - expDown));
            }
        }
    }

    @NotNull
    public Job getJob() {
        return this.job;
    }

    @NotNull
    public JobState getState() {
        return state;
    }

    public void setState(@NotNull JobState state) {
        if (this.getState() == JobState.INACTIVE && this.getLevel() == 0) {
            this.setLevel(1);
        }
        this.state = state;
    }

    public int getLevel() {
        return this.level;
    }


    public void setLevel(int level) {
        this.level = Math.max(0, level);
    }

    public int getMaxLevel() {
        return this.getJob().getMaxLevel(this.getState());
    }

    public int getXP() {
        return this.xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public int getMaxXP() {
        return this.getJob().getXPToLevel(this.getLevel());
    }

    public int getXPToLevelUp() {
        return this.getMaxXP() - this.getXP();
    }

    public int getXPToLevelDown() {
        return this.getXP() + this.getMaxXP();
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
    public Set<Integer> getObtainedLevelRewards() {
        return obtainedLevelRewards;
    }

    public boolean isLevelRewardObtained(int level) {
        return this.obtainedLevelRewards.contains(level);
    }

    public void setLevelRewardObtained(int level) {
        this.obtainedLevelRewards.add(level);
    }
}
