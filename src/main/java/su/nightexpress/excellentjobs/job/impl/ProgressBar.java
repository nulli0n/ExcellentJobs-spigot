package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.nightcore.util.text.NightMessage;

public class ProgressBar {

    private final JobsPlugin plugin;
    private final Job        job;
    private final Player     player;
    private final JobIncome  income;
    private final BossBar    bossBar;
    private final Replacer replacer;
    private final boolean useBossBar;

    private int xp;
    private long expireDate;

    public ProgressBar(@NotNull JobsPlugin plugin, @NotNull Job job, @NotNull Player player) {
        this.plugin = plugin;
        this.job = job;
        this.player = player;
        this.income = new JobIncome();
        this.useBossBar = Config.GENERAL_PROGRESS_BAR_BOSS_BAR_ENABLED.get();
        this.bossBar = plugin.getServer().createBossBar("", job.getProgressBarColor(), Config.GENERAL_PROGRESS_BAR_STYLE.get());
        this.bossBar.setVisible(false);
        if (this.useBossBar) {
            this.bossBar.addPlayer(player);
        }

        this.replacer = new Replacer()
            .replacePlaceholderAPI(this.player)
            .replace(Placeholders.JOB_NAME, this.job::getName)
            .replace(Placeholders.GENERIC_XP, () -> NumberUtil.format(this.xp))
            .replace(Placeholders.GENERIC_INCOME, () -> JobUtils.formatIncome(this.income));

        this.setXP(0);
        this.updateDisplayTime();
    }

    public boolean checkExpired() {
        if (this.isExpired()) {
            this.discard();
            return true;
        }
        return false;
    }

    public void discard() {
        this.bossBar.removeAll();
        this.bossBar.setVisible(false);
        this.income.clear();
    }

    public void updateDisplay() {
        if (this.xp == 0 && this.income.isEmpty()) return;

        JobData data = this.plugin.getUserManager().getOrFetch(player).getData(job);

        if (this.useBossBar) {
            int dataXP = Math.max(0, data.getXP());
            int maxXP = Math.max(1, data.getLevelXP());

            double percent = (double) dataXP / (double) maxXP;
            if (percent < 0) percent = 0D;
            else if (percent > 1) percent = 1D;

            String title = data.replaceAllPlaceholders().apply(Config.GENERAL_PROGRESS_BAR_TITLE.get());
            this.bossBar.setTitle(NightMessage.asLegacy(this.replacer.apply(title)));
            this.bossBar.setProgress(percent);
            this.bossBar.setVisible(true);
        }

        if (Config.PROGRESS_BAR_ACTION_BAR_ENABLED.get()) {
            String text = this.replacer.apply(data.replaceAllPlaceholders().apply(Config.PROGRESS_BAR_ACTION_BAR_TEXT.get()));
            Players.sendActionBarText(this.player, text);
        }

        this.updateDisplayTime();
    }

    public void updateDisplayTime() {
        this.expireDate = TimeUtil.createFutureTimestamp(Config.GENERAL_PROGRESS_BAR_STAY_TIME.get());
    }

    public boolean isExpired() {
        return TimeUtil.isPassed(this.expireDate);
    }

    public void addXP(int amount) {
        this.setXP(this.getXP() + amount);
    }

    public void addPayment(@NotNull Currency currency, double amount) {
        this.income.add(currency, amount);
    }

    @NotNull
    public JobIncome getIncome() {
        return this.income;
    }

    public int getXP() {
        return this.xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public long getExpireDate() {
        return this.expireDate;
    }
}
