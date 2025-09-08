package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobLimitData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.NormalMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class JobsMenu extends NormalMenu<JobsPlugin> implements Filled<Job>, ConfigBased {

    private static final String FILE_NAME = "job_browse.yml";

    private static final String PLACEHOLDER_STATE  = "%state%";
    private static final String DAILY_LIMITS       = "%daily_limits%";

    private String       jobNameAvailable;
    private List<String> jobLoreAvailable;
    private String       jobNameLockedPerm;
    private List<String> jobLoreLockedPerm;

    private Map<JobState, List<String>> jobStateInfo;

    private List<String> jobDailyLimits;
    private List<String> jobDailyCurrencyLimit;
    private List<String> jobDailyXPLimit;

    private boolean              gridAuto;
    private int[]                gridAutoSlots;
    private int                  gridCustomPages;
    private Map<String, JobSlot> gridCustomSlots;

    public JobsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap(BOLD.wrap("Jobs")));

        this.load(FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        if (!this.gridAuto) {
            viewer.setPages(this.gridCustomPages);
        }

        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer menuViewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public MenuFiller<Job> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();

        return MenuFiller.builder(this)
            .setSlots(this.gridAutoSlots)
            .setItems(plugin.getJobManager().getJobs().stream().sorted(Comparator.comparing(Job::getName)).toList())
            .setItemCreator(job -> this.replaceJobItem(player, job))
            .setItemClick(job -> (viewer1, event) -> this.onJobClick(viewer1, job))
            .build();
    }

    @Override
    public void autoFill(@NotNull MenuViewer viewer) {
        if (this.gridAuto) {
            Filled.super.autoFill(viewer);
            return;
        }

        Player player = viewer.getPlayer();
        int page = viewer.getPage();

        this.plugin.getJobManager().getJobs().forEach(job -> {
            JobSlot slot = this.gridCustomSlots.get(job.getId());
            if (slot == null || slot.page != page) return;

            NightItem item = this.replaceJobItem(player, job);
            MenuItem menuItem = item.toMenuItem().setSlots(slot.slots).setPriority(100).setHandler((viewer1, event) -> this.onJobClick(viewer1, job)).build();

            viewer.addItem(menuItem);
        });
    }

    private void onJobClick(MenuViewer viewer, @NotNull Job job) {
        Player player = viewer.getPlayer();

        if (!job.hasPermission(player)) {
            CoreLang.ERROR_NO_PERMISSION.message().send(player);
            return;
        }

        this.runNextTick(() -> this.plugin.getJobManager().openLevelsMenu(player, job));
    }

    private NightItem replaceJobItem(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData jobData = user.getData(job);
        JobLimitData limitData = jobData.getLimitDataUpdated();
        JobState state = jobData.getState();

        int level = jobData.getLevel();
        double xpBoost = JobsAPI.getBoostPercent(player, job, MultiplierType.XP);
        double payBoost = JobsAPI.getBoostPercent(player, job, MultiplierType.INCOME);

        double xpMod = jobData.getXPBonus() * 100D;
        double payMod = jobData.getIncomeBonus() * 100D;

        double xpGain = xpMod + xpBoost;
        double payGain = payMod + payBoost;

        boolean hasAccess = job.hasPermission(player);
        List<String> lore;
        String name;
        if (hasAccess) {
            name = this.jobNameAvailable;
            lore = new ArrayList<>(this.jobLoreAvailable);
        }
        else {
            name = this.jobNameLockedPerm;
            lore = new ArrayList<>(this.jobLoreLockedPerm);
        }

        List<String> dailyLimits = new ArrayList<>();
        List<String> currencyLimits = new ArrayList<>();
        List<String> xpLimits = new ArrayList<>();
        if (job.hasDailyXPLimit(level)) {
            xpLimits = new ArrayList<>(this.jobDailyXPLimit);
            xpLimits.replaceAll(str -> str
                .replace(GENERIC_CURRENT, NumberUtil.format(limitData.getXPEarned()))
                .replace(GENERIC_TOTAL, NumberUtil.format(job.getDailyXPLimit(level)))
            );
        }
        if (!job.getDailyPaymentLimits().isEmpty()) {
            currencyLimits = new ArrayList<>();
            for (String line : this.jobDailyCurrencyLimit) {
                if (line.contains(CURRENCY_NAME)) {
                    for (Currency currency : EconomyBridge.getCurrencies()) {
                        if (!job.hasDailyPaymentLimit(currency, level)) continue;

                        currencyLimits.add(currency.replacePlaceholders().apply(line)
                            .replace(GENERIC_CURRENT, currency.format(limitData.getCurrencyEarned(currency)))
                            .replace(GENERIC_TOTAL, currency.format(job.getDailyPaymentLimit(currency, level)))
                        );
                    }
                    continue;
                }
                currencyLimits.add(line);
            }
        }
        if (!currencyLimits.isEmpty() || !xpLimits.isEmpty()) {
            dailyLimits = new ArrayList<>(this.jobDailyLimits);
            dailyLimits = Lists.replace(dailyLimits, GENERIC_CURRENCY, currencyLimits);
            dailyLimits = Lists.replace(dailyLimits, GENERIC_XP, xpLimits);
        }

        List<String> stateInfo = this.jobStateInfo.getOrDefault(state, Collections.emptyList());

        List<String> finalDailyLimits = dailyLimits;
        return job.getIcon()
            .hideAllComponents()
            .setDisplayName(name)
            .setLore(lore)
            .replacement(replacer -> replacer
                .replace(GENERIC_XP_BONUS, JobUtils.formatBonus(xpGain))
                .replace(GENERIC_XP_MULTIPLIER, NumberUtil.format(xpMod))
                .replace(GENERIC_XP_BOOST, NumberUtil.format(xpBoost))
                .replace(GENERIC_INCOME_BONUS, JobUtils.formatBonus(payGain))
                .replace(GENERIC_INCOME_MULTIPLIER, NumberUtil.format(payMod))
                .replace(GENERIC_INCOME_BOOST, NumberUtil.format(payBoost))
                .replace(DAILY_LIMITS, finalDailyLimits)
                .replace(PLACEHOLDER_STATE, stateInfo)
                .replace("%order%", Collections.emptyList())
                .replace("%status%", Collections.emptyList())
                .replace(jobData.replacePlaceholders())
                .replace(job.replacePlaceholders())
            );
    }

    private static class JobSlot implements Writeable {

        private final int page;
        private final int[] slots;

        public JobSlot(int page, int[] slots) {
            this.page = page;
            this.slots = slots;
        }

        @NotNull
        public static JobSlot read(@NotNull FileConfig config, @NotNull String path) {
            int page = ConfigValue.create(path + ".Page", 1).read(config);
            int[] slots = ConfigValue.create(path + ".Slots", new int[0]).read(config);

            return new JobSlot(page, slots);
        }

        @Override
        public void write(@NotNull FileConfig config, @NotNull String path) {
            config.set(path + ".Page", this.page);
            config.setIntArray(path + ".Slots", this.slots);
        }
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        int[] defSlots = new int[]{20,21,22,23,24,31,30,32};

        this.gridAuto = ConfigValue.create("Job.Grid.Auto", false).read(config);

        this.gridAutoSlots = ConfigValue.create("Job.Grid.AutoSlots", defSlots).read(config);

        this.gridCustomPages = ConfigValue.create("Job.Grid.CustomPages", 1).read(config);

        this.gridCustomSlots = ConfigValue.forMapById("Job.Grid.CustomSlots",
            JobSlot::read,
            map -> {
                int index = 0;
                for (Job job : plugin.getJobManager().getJobs()) {
                    int slot = index >= defSlots.length ? -1 : defSlots[index++];
                    int page = 1;
                    map.put(job.getId(), new JobSlot(page, new int[]{slot}));
                }
            }
        ).read(config);

        this.jobNameAvailable = ConfigValue.create("Job.Available.Name",
            SOFT_YELLOW.wrap(BOLD.wrap(JOB_NAME))
        ).read(config);

        this.jobLoreAvailable = ConfigValue.create("Job.Available.Lore", Lists.newList(
            PLACEHOLDER_STATE,
            DARK_GRAY.wrap(GREEN.wrap("✔") + " Workers: " + GRAY.wrap(JOB_EMPLOYEES_TOTAL)),
            EMPTY_IF_BELOW,
            JOB_DESCRIPTION,
            EMPTY_IF_ABOVE,
            SOFT_YELLOW.wrap(BOLD.wrap("Your Stats:")),
            SOFT_YELLOW.wrap("▪ " + GRAY.wrap("XP: ") + JOB_DATA_XP + GRAY.wrap("/") + JOB_DATA_XP_MAX),
            SOFT_YELLOW.wrap("▪ " + GRAY.wrap("Level: ") + JOB_DATA_LEVEL + GRAY.wrap("/") + JOB_DATA_LEVEL_MAX),
            SOFT_YELLOW.wrap("▪ " + GRAY.wrap("XP Bonus: ") + GENERIC_XP_BONUS) + " " + GRAY.wrap("(" + GENERIC_XP_MULTIPLIER + " + " + GENERIC_XP_BOOST + ")"),
            SOFT_YELLOW.wrap("▪ " + GRAY.wrap("Income Bonus: ") + GENERIC_INCOME_BONUS) + " " + GRAY.wrap("(" + GENERIC_INCOME_MULTIPLIER + " + " + GENERIC_INCOME_BOOST + ")"),
            EMPTY_IF_BELOW,
            DAILY_LIMITS,
            EMPTY_IF_BELOW,
            SOFT_YELLOW.wrap("→ " + UNDERLINED.wrap("Click to view levels"))
        )).read(config);

        this.jobNameLockedPerm = ConfigValue.create("Job.Locked_Permission.Name",
            RED.wrap("[Locked]") + " " + GRAY.wrap(JOB_NAME)
        ).read(config);

        this.jobLoreLockedPerm = ConfigValue.create("Job.Locked_Permission.Lore", Lists.newList(
            GRAY.wrap("Upgrade your " + RED.wrap("/rank") + " to access this job.")
        )).read(config);

        this.jobDailyLimits = ConfigValue.create("Job.DailyLimits.Header", Lists.newList(
            SOFT_YELLOW.wrap(BOLD.wrap("Daily Limits:")),
            GENERIC_CURRENCY,
            GENERIC_XP
        )).read(config);

        this.jobDailyCurrencyLimit = ConfigValue.create("Job.DailyLimits.Currency", Lists.newList(
            SOFT_YELLOW.wrap("• " + GRAY.wrap(CURRENCY_NAME + ": ") + GENERIC_CURRENT + GRAY.wrap("/") + GENERIC_TOTAL)
        )).read(config);

        this.jobDailyXPLimit = ConfigValue.create("Job.DailyLimits.XP", Lists.newList(
            SOFT_YELLOW.wrap("• " + GRAY.wrap("XP: ") + GENERIC_CURRENT + GRAY.wrap("/") + GENERIC_TOTAL)
        )).read(config);

        this.jobStateInfo = ConfigValue.forMapByEnum("Job.StateInfo", JobState.class,
            (cfg, path, id) -> cfg.getStringList(path),
            map -> {
                map.put(JobState.PRIMARY, Lists.newList(DARK_GRAY.wrap(GREEN.wrap("✔") + " This is your " + GRAY.wrap("Primary") + " job.")));
                map.put(JobState.SECONDARY, Lists.newList(DARK_GRAY.wrap(GREEN.wrap("✔") + " This is your " + GRAY.wrap("Secondary") + " job.")));
                map.put(JobState.INACTIVE, Lists.newList(DARK_GRAY.wrap(RED.wrap("✘") + " You are " + GRAY.wrap("not an employee") + " of this job.")));
            }
        ).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.GRAY_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(IntStream.range(9, 45).toArray())
        );

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44)
        );

        loader.addDefaultItem(MenuItem.buildNextPage(this, 35).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 27).setPriority(10));
    }
}
