package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgressBar {

    private final JobsPlugin            plugin;
    private final Job                   job;
    private final Player                player;
    private final Map<Currency, Double> moneyMap;
    private final BossBar               bossBar;

    private int xp;
    private long expireDate;

    public ProgressBar(@NotNull JobsPlugin plugin, @NotNull Job job, @NotNull Player player) {
        this.plugin = plugin;
        this.job = job;
        this.player = player;
        this.moneyMap = new HashMap<>();
        this.bossBar = plugin.getServer().createBossBar("", job.getProgressBarColor(), Config.GENERAL_PROGRESS_BAR_STYLE.get());
        this.bossBar.setVisible(false);
        this.bossBar.addPlayer(player);

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
        this.moneyMap.clear();
    }

    public void updateDisplay() {
        if (this.xp == 0 && this.moneyMap.isEmpty()) return;

        String xp = NumberUtil.format(this.getXP());
        String money = moneyMap.entrySet().stream().map(e -> e.getKey().format(e.getValue())).collect(Collectors.joining(", "));

        JobData data = this.plugin.getUserManager().getUserData(player).getData(job);
        int dataXP = Math.max(0, data.getXP());
        int maxXP = Math.max(1, data.getMaxXP());

        double percent = (double) dataXP / (double) maxXP;
        if (percent < 0) percent = 0D;
        else if (percent > 1) percent = 1D;

        String title = data.replacePlaceholders().apply(Config.GENERAL_PROGRESS_BAR_TITLE.get());

        this.bossBar.setTitle(NightMessage.asLegacy(title
            .replace(Placeholders.JOB_NAME, job.getName())
            .replace(Placeholders.GENERIC_XP, xp)
            .replace(Placeholders.GENERIC_INCOME, money)
        ));
        this.bossBar.setProgress(percent);
        this.bossBar.setVisible(true);

        this.updateDisplayTime();
    }

    public void updateDisplayTime() {
        this.expireDate = System.currentTimeMillis() + Config.GENERAL_PROGRESS_BAR_STAY_TIME.get() * 1000L;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expireDate;
    }

    public void addXP(int amount) {
        this.setXP(this.getXP() + amount);
    }

    public void addPayment(@NotNull Currency currency, double amount) {
        double has = this.moneyMap.computeIfAbsent(currency, k -> 0D);
        this.moneyMap.put(currency, has + amount);
    }

    public int getXP() {
        return xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    @NotNull
    public Map<Currency, Double> getMoneyMap() {
        return moneyMap;
    }

    public long getExpireDate() {
        return expireDate;
    }
}
