package su.nightexpress.excellentjobs.grind;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.api.event.JobIncomeEvent;
import su.nightexpress.excellentjobs.api.event.JobXPGainEvent;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterRegistry;
import su.nightexpress.excellentjobs.grind.listener.GrindListener;
import su.nightexpress.excellentjobs.grind.listener.GrindProtectionListener;
import su.nightexpress.excellentjobs.grind.listener.impl.*;
import su.nightexpress.excellentjobs.grind.provider.GrindListenerProvider;
import su.nightexpress.excellentjobs.grind.provider.GrindTypeProvider;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.excellentjobs.grind.type.GrindType;
import su.nightexpress.excellentjobs.grind.type.GrindTypeId;
import su.nightexpress.excellentjobs.grind.type.impl.*;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobIncome;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.impl.ProgressBar;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

public class GrindManager extends AbstractManager<JobsPlugin> {

    private static final String PLAYER_BLOCK_MARKER = "player_block_marker";

    private final GrindRegistry grindRegistry;
    private final NamespacedKey mobSpawnerKey;

    public GrindManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.grindRegistry = new GrindRegistry(plugin);
        this.mobSpawnerKey = new NamespacedKey(plugin, "grind.spawner_mob");
    }

    @Override
    protected void onLoad() {
        this.grindRegistry.setup();

        this.registerDefaults();

        this.addListener(new GrindProtectionListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        this.grindRegistry.shutdown();
    }

    private void registerDefaults() {
        GrindAdapterRegistry.load(this.plugin);

        // Item Related
        this.registerGrindType(GrindTypeId.CRAFTING, BasicItemGrindType::new, CraftingGrindListener::new);
        this.registerGrindType(GrindTypeId.FORGING, BasicItemGrindType::new, ForgingGrindListener::new);
        this.registerGrindType(GrindTypeId.GRINDSTONE, BasicItemGrindType::new, GrindstoneGrindListener::new);

        // Entity Related
        this.registerGrindType(GrindTypeId.KILLING, KillingGrindType::new, KillingGrindListener::new);
        this.registerGrindType(GrindTypeId.BREEDING, BasicEntityGrindType::new, BreedingGrindListener::new);
        this.registerGrindType(GrindTypeId.MILKING, BasicEntityGrindType::new, MilkingGrindListener::new);
        this.registerGrindType(GrindTypeId.SHEARING, BasicEntityGrindType::new, ShearingGrindListener::new);
        this.registerGrindType(GrindTypeId.TAMING, BasicEntityGrindType::new, TamingGrindListener::new);

        // Block Realted
        this.registerGrindType(GrindTypeId.BUILDING, BasicBlockGrindType::new, BuildingGrindListener::new);
        this.registerGrindType(GrindTypeId.FERTILIZING, BasicBlockGrindType::new, FertilizingGrindListener::new);

        // Independent
        this.registerGrindType(GrindTypeId.BREWING, BrewingGrindType::new, BrewingGrindListener::new);
        this.registerGrindType(GrindTypeId.COOKING, CookingGrindType::new, CookingGrindListener::new);
        this.registerGrindType(GrindTypeId.ENCHANTING, EnchantingGrindType::new, EnchantingGrindListener::new);
        this.registerGrindType(GrindTypeId.FISHING, FishingGrindType::new, FishingGrindListener::new);
        this.registerGrindType(GrindTypeId.GATHERING, GatheringGrindType::new, GatheringGrindListener::new);
        this.registerGrindType(GrindTypeId.MINING, MiningGrindType::new, MiningGrindListener::new);
    }

    public <E extends GrindTable, T extends GrindType<E>> void registerGrindType(@NotNull String id,
                                                                                 @NotNull GrindTypeProvider<T> grindProvider,
                                                                                 @NotNull GrindListenerProvider<E, T> listenerProvider) {
        // TODO Check disabled

        T grindType = grindProvider.provide(id);
        GrindListener<E, T> grindListener = listenerProvider.provide(this.plugin, this, grindType);

        this.addListener(grindListener);
        this.grindRegistry.registerGrindType(grindType);
    }

    @NotNull
    public GrindRegistry getGrindRegistry() {
        return this.grindRegistry;
    }



    public <T extends GrindTable, G extends GrindType<T>> void giveXP(@NotNull Player player, @Nullable ItemStack itemStack, @NotNull G grindType, @NotNull GrindCalculator<T> calculator) {
        // TODO Check tool ItemStack tool = itemStack == null ? new ItemStack(Material.AIR) : new ItemStack(itemStack);

        for (Job job : this.plugin.getJobManager().getJobs()) {
            if (!job.isGoodWorld(player.getWorld())) continue;

            JobUser user = plugin.getUserManager().getOrFetch(player);
            JobData jobData = user.getData(job);
            if (!jobData.isActive()) continue;

            int jobLevel = jobData.getLevel();
            ProgressBar progressBar = this.plugin.getJobManager().getProgressBarOrCreate(player, job);

            int xp = 0;
            JobIncome income = this.plugin.getJobManager().getIncome(player, job);

            for (JobObjective objective : job.getObjectiveTables(grindType)) {
                T table = grindType.adaptTable(objective.getGrindTable()).orElse(null);
                if (table == null) continue;

                Currency currency = EconomyBridge.getCurrency(objective.getCurrencyId());
                if (currency == null) continue;

                GrindReward reward = calculator.calculate(job, table);
                if (reward.isEmpty()) continue;

                double jobXP = reward.getXP();
                if (jobXP > 0) {
                    xp += this.proceedXP(player, job, jobXP);
                }

                double jobMoney = reward.getMoney();
                double resultMoney = this.proceedIncome(player, job, currency, jobMoney);
                if (resultMoney > 0) {
                    income.add(currency, resultMoney);
                    if (progressBar != null) progressBar.addPayment(currency, resultMoney);

                    if (!player.hasPermission(Perms.PREFIX_BYPASS_LIMIT_CURRENCY + job.getId()) && job.hasDailyPaymentLimit(currency, jobLevel)) {
                        jobData.getLimitData().addCurrency(currency, resultMoney);

                        if (jobData.isPaymentLimitReached(currency)) {
                            Lang.JOB_LIMIT_CURRENCY_NOTIFY.message().send(player, replacer -> replacer
                                .replace(job.replacePlaceholders())
                                .replace(currency.replacePlaceholders()));
                        }
                    }
                }
            }

            if (xp > 0) {
                this.plugin.getJobManager().addXP(player, job, xp, true);
                if (progressBar != null) progressBar.addXP(xp);

                if (!player.hasPermission(Perms.PREFIX_BYPASS_LIMIT_XP + job.getId()) && job.hasDailyXPLimit(jobLevel)) {
                    jobData.getLimitData().addXP(xp);

                    if (jobData.isXPLimitReached()) {
                        Lang.JOB_LIMIT_XP_NOTIFY.message().send(player, replacer -> replacer.replace(job.replacePlaceholders()));
                    }
                }
            }

            if (progressBar != null && xp > 0 && !income.isEmpty()) {
                progressBar.updateDisplay();
            }
        }
    }

    private int proceedXP(@NotNull Player player, @NotNull Job job, double xpRoll) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData jobData = user.getData(job);

        if (jobData.isXPLimitReached()) return 0;

        double boost = JobsAPI.getBoost(player, job, MultiplierType.XP);
        double xpMultiplier = 1D + boost + jobData.getXPBonus();

        JobXPGainEvent event = new JobXPGainEvent(player, user, jobData, xpRoll, xpMultiplier);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return 0;

        double xpAfter = event.getAmount() * event.getMultiplier();
        return Double.isNaN(xpAfter) || Double.isInfinite(xpAfter) ? 0 : (int) Math.ceil(Math.max(0, xpAfter));
    }

    private double proceedIncome(@NotNull Player player, @NotNull Job job, @NotNull Currency currency, double moneyRoll) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData jobData = user.getData(job);

        if (jobData.isPaymentLimitReached(currency)) return 0;

        double boost = JobUtils.canBeBoosted(currency) ? JobsAPI.getBoost(player, job, MultiplierType.INCOME) : 0D;
        double moneyMultiplier = 1D + boost + jobData.getIncomeBonus();

        JobIncomeEvent event = new JobIncomeEvent(player, user, jobData, currency, moneyRoll, moneyMultiplier);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return 0;

        double moneyAfter = event.getAmount() * event.getMultiplier();
        return (Double.isNaN(moneyAfter) || Double.isInfinite(moneyAfter)) ? 0D : Math.max(0, moneyAfter);
    }

    public boolean canGrinding(@NotNull Player player) {
        return player.getGameMode() != GameMode.CREATIVE && JobManager.canWorkHere(player);
    }

    public boolean isSpawnerMob(@NotNull Entity entity) {
        return PDCUtil.getBoolean(entity, this.mobSpawnerKey).isPresent();
    }

    public void markSpawnerMob(@NotNull Entity entity, boolean flag) {
        PDCUtil.set(entity, this.mobSpawnerKey, flag);
    }

    public boolean isPlayerBlock(@NotNull Block block) {
        return block.hasMetadata(PLAYER_BLOCK_MARKER) || PlayerBlockTracker.isTracked(block);
    }

    public void markPlayerBlock(@NotNull Block block, boolean flag) {
        if (flag) {
            block.setMetadata(PLAYER_BLOCK_MARKER, new FixedMetadataValue(this.plugin, true));
        }
        else {
            block.removeMetadata(PLAYER_BLOCK_MARKER, this.plugin);
        }
    }
}
