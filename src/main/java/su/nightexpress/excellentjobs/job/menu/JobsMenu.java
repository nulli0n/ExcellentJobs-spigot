package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobLimitData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobsMenu extends ConfigMenu<JobsPlugin> implements AutoFilled<Job> {

    private static final String FILE_NAME = "job_list.yml";

    private static final String PLACEHOLDER_ORDER = "%order%";
    private static final String PLACEHOLDER_STATUS = "%status%";
    private static final String PLACEHOLDER_BOOSTER = "%booster%";
    private static final String PLACEHOLDER_STATE = "%state%";
    private static final String DAILY_LIMITS = "%daily_limits%";

    private String       jobNameAvailable;
    private List<String> jobLoreAvailable;
    private List<String> jobAvailJoinPrimLore;
    //private List<String> jobAvailJoinSeconLore;
    private List<String> jobAvailSettingsLore;
    //private List<String> jobAvailLimitLore;
    private List<String> jobSpecOrderLore;
    private List<String> jobBoosterLore;
    private List<String> jobPrimStateLore;
    private List<String> jobSecondStateLore;
    private List<String> jobInactiveStateLore;
    private List<String> jobDailyLimits;
    private List<String> jobDailyCurrencyLimit;
    private List<String> jobDailyXPLimit;
    private String       jobNameLockedPerm;
    private List<String> jobLoreLockedPerm;
    private int[]        jobSlots;

    public JobsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));

        plugin.getJobManager().getJobs().forEach(job -> {
            this.addHandler(new ItemHandler("job:" + job.getId(), (viewer, event) -> {
                this.onJobClick(viewer, job);
            }));
        });

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getHandler().getName().startsWith("job:")) {
                String jobId = menuItem.getHandler().getName().substring("job:".length());
                Job job = plugin.getJobManager().getJobById(jobId);
                if (job == null) return;

                menuItem.getOptions().setDisplayModifier((viewer, item) -> {
                    this.replaceJobItem(viewer.getPlayer(), job, item);
                });
            }
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer menuViewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Job> autoFill) {
        Player player = viewer.getPlayer();
        JobUser user = plugin.getUserManager().getUserData(player);

        autoFill.setSlots(this.jobSlots);
        autoFill.setItems(plugin.getJobManager().getJobs().stream().sorted(Comparator.comparing(Job::getName)).toList());
        autoFill.setItemCreator(job -> {
            ItemStack item = job.getIcon();
            this.replaceJobItem(player, job, item);
            return item;
        });

        autoFill.setClickAction(job -> (viewer1, event) -> {
            this.onJobClick(viewer1, job);
        });
    }

    private void onJobClick(MenuViewer viewer, @NotNull Job job) {
        Player player = viewer.getPlayer();

        JobUser user = this.plugin.getUserManager().getUserData(player);
        if (user.getData(job).getState() != JobState.INACTIVE) {
            this.plugin.getJobManager().openJobMenu(player, job);
            return;
        }

        if (!job.hasPermission(player)) {
            Lang.ERROR_NO_PERMISSION.getMessage().send(player);
            return;
        }

        //if (this.plugin.getJobManager().canGetMoreJobs(player1)) {
        this.runNextTick(() -> this.plugin.getJobManager().openPreviewMenu(player, job));
        //}
    }

    private void replaceJobItem(@NotNull Player player, @NotNull Job job, @NotNull ItemStack item) {
        JobUser user = plugin.getUserManager().getUserData(player);
        JobData jobData = user.getData(job);
        JobLimitData limitData = jobData.getLimitData();

        int level = jobData.getLevel();
        Collection<Booster> boosters = plugin.getBoosterManager().getBoosters(player, job);

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

        List<String> status;
        if (jobData.getState() != JobState.INACTIVE) {
            status = new ArrayList<>(this.jobAvailSettingsLore);
        }
//            else if (this.plugin.getJobManager().canGetMoreJobs(player, JobState.PRIMARY) && job.isAllowedState(JobState.PRIMARY)) {
//                status = new ArrayList<>(this.jobAvailJoinPrimLore);
//            }
//            else if (this.plugin.getJobManager().canGetMoreJobs(player, JobState.SECONDARY) && job.isAllowedState(JobState.SECONDARY)) {
//                status = new ArrayList<>(this.jobAvailJoinSeconLore);
//            }
//            else {
//                status = new ArrayList<>(this.jobAvailLimitLore);
//            }
        else {
            status = new ArrayList<>(this.jobAvailJoinPrimLore);
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

        List<String> boosterInfo = new ArrayList<>();
        if (!boosters.isEmpty()) {
            for (String line : this.jobBoosterLore) {
                if (line.contains(CURRENCY_BOOST_PERCENT) || line.contains(CURRENCY_BOOST_MODIFIER)) {
                    for (Currency currency : EconomyBridge.getCurrencies()) {
                        boosterInfo.add(currency.replacePlaceholders().apply(line)
                            .replace(CURRENCY_BOOST_MODIFIER, NumberUtil.format(Booster.getCurrencyBoost(currency, boosters)))
                            .replace(CURRENCY_BOOST_PERCENT, NumberUtil.format(Booster.getCurrencyPercent(currency, boosters)))
                        );
                    }
                }
                else boosterInfo.add(line
                    .replace(XP_BOOST_MODIFIER, NumberUtil.format(Booster.getXPBoost(boosters)))
                    .replace(XP_BOOST_PERCENT, NumberUtil.format(Booster.getXPPercent(boosters)))
                );
            }
        }

        List<String> state = new ArrayList<>();
        if (jobData.getState() == JobState.PRIMARY) {
            state.addAll(this.jobPrimStateLore);
        }
        else if (jobData.getState() == JobState.SECONDARY) {
            state.addAll(this.jobSecondStateLore);
        }
        else state.addAll(this.jobInactiveStateLore);

        List<String> order = new ArrayList<>();
        JobOrderData orderData = jobData.getOrderData();
        if (!orderData.isEmpty() && !orderData.isExpired()) {
            order.addAll(this.jobSpecOrderLore);
            order.replaceAll(line -> line.replace(GENERIC_TIME, TimeUtil.formatDuration(orderData.getExpireDate())));
        }

        List<String> loreFinal = new ArrayList<>();
        for (String line : lore) {
            if (line.contains(CURRENCY_MULTIPLIER)) {
                for (Currency currency : EconomyBridge.getCurrencies()) {
                    double multiplier = job.getPaymentMultiplier(currency, level);
                    if (multiplier == 0D) continue;

                    loreFinal.add(currency.replacePlaceholders().apply(line)
                        .replace(CURRENCY_MULTIPLIER, NumberUtil.format(multiplier * 100D))
                    );
                }
            }
            else {
                loreFinal.add(line
                    .replace(XP_MULTIPLIER, NumberUtil.format(job.getXPMultiplier(level) * 100D))
                );
            }
        }

        ItemReplacer.create(item).trimmed().hideFlags()
            .setDisplayName(name).setLore(loreFinal)
            .replace(DAILY_LIMITS, dailyLimits)
            .replace(PLACEHOLDER_STATE, state)
            .replace(PLACEHOLDER_BOOSTER, boosterInfo)
            .replace(PLACEHOLDER_STATUS, status)
            .replace(PLACEHOLDER_ORDER, order)
            .replace(jobData.replacePlaceholders())
            .replace(job.replacePlaceholders())
            .writeMeta();
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose(BOLD.enclose("Jobs")), MenuSize.CHEST_27);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack prevPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(prevPage).setSlots(18).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(nextPage).setSlots(26).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        ItemStack back = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_CLOSE.getLocalizedName());
        });
        list.add(new MenuItem(back).setSlots(40).setPriority(10).setHandler(ItemHandler.forClose(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.jobNameAvailable = ConfigValue.create("Job.Available.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(JOB_NAME))
        ).read(cfg);

        this.jobLoreAvailable = ConfigValue.create("Job.Available.Lore", Lists.newList(
            PLACEHOLDER_STATE,
            DARK_GRAY.enclose(LIGHT_GREEN.enclose("✔") + " Employees: " + GRAY.enclose(JOB_EMPLOYEES_TOTAL)),
            "",
            JOB_DESCRIPTION,
            "",
            LIGHT_YELLOW.enclose(BOLD.enclose("Your Stats:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("XP: ") + JOB_DATA_XP + LIGHT_GRAY.enclose("/") + JOB_DATA_XP_MAX),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Level: ") + JOB_DATA_LEVEL + LIGHT_GRAY.enclose("/") + JOB_DATA_LEVEL_MAX),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("XP Multiplier: ") + "+" + XP_MULTIPLIER + "%"),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(CURRENCY_NAME + " Multiplier: ") + "+" + CURRENCY_MULTIPLIER + "%"),
            "",
            DAILY_LIMITS,
            "",
            PLACEHOLDER_BOOSTER,
            "",
            PLACEHOLDER_ORDER,
            "",
            PLACEHOLDER_STATUS
        )).read(cfg);

        this.jobDailyLimits = ConfigValue.create("Job.DailyLimits.Header", Lists.newList(
            LIGHT_YELLOW.enclose(BOLD.enclose("Daily Limits:")),
            GENERIC_CURRENCY,
            GENERIC_XP
        )).read(cfg);

        this.jobDailyCurrencyLimit = ConfigValue.create("Job.DailyLimits.Currency", Lists.newList(
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(CURRENCY_NAME + ": ") + GENERIC_CURRENT + LIGHT_GRAY.enclose("/") + GENERIC_TOTAL)
        )).read(cfg);

        this.jobDailyXPLimit = ConfigValue.create("Job.DailyLimits.XP", Lists.newList(
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("XP: ") + GENERIC_CURRENT + LIGHT_GRAY.enclose("/") + GENERIC_TOTAL)
        )).read(cfg);

        this.jobAvailJoinPrimLore = ConfigValue.create("Job.Available.Status.Preview", Lists.newList(
            LIGHT_GREEN.enclose("[▶] " + LIGHT_GRAY.enclose("Click to") + " preview" + LIGHT_GRAY.enclose("."))
        )).read(cfg);

//        this.jobAvailJoinSeconLore = ConfigValue.create("Job.Available.Status.Join_Secondary", Lists.newList(
//            LIGHT_GREEN.enclose("[▶] " + LIGHT_GRAY.enclose("Click to") + " get as secondary" + LIGHT_GRAY.enclose("."))
//        )).read(cfg);

        this.jobAvailSettingsLore = ConfigValue.create("Job.Available.Status.Settings", Lists.newList(
            LIGHT_YELLOW.enclose("[▶] " + LIGHT_GRAY.enclose("Click to") + " open settings" + LIGHT_GRAY.enclose("."))
        )).read(cfg);

//        this.jobAvailLimitLore = ConfigValue.create("Job.Available.Status.Limit", Lists.newList(
//            LIGHT_RED.enclose("[❗] " + LIGHT_GRAY.enclose("You can't get") + " more " + LIGHT_GRAY.enclose("jobs."))
//        )).read(cfg);

        this.jobPrimStateLore = ConfigValue.create("Job.State.Primary", Lists.newList(
            DARK_GRAY.enclose(LIGHT_GREEN.enclose("✔") + " This is your " + GRAY.enclose("Primary") + " job.")
        )).read(cfg);

        this.jobSecondStateLore = ConfigValue.create("Job.State.Secondary", Lists.newList(
            DARK_GRAY.enclose(LIGHT_GREEN.enclose("✔") + " This is your " + GRAY.enclose("Secondary") + " job.")
        )).read(cfg);

        this.jobInactiveStateLore = ConfigValue.create("Job.State.Inactive", Lists.newList(
            DARK_GRAY.enclose(LIGHT_RED.enclose("✘") + " You are " + GRAY.enclose("not an employee") + " of this job.")
        )).read(cfg);

        this.jobBoosterLore = ConfigValue.create("Job.Booster.Info",Lists.newList(
            LIGHT_GREEN.enclose(BOLD.enclose("Boosters:")),
            LIGHT_GREEN.enclose("▪ " + LIGHT_GRAY.enclose("XP Boost: ") + "+" + XP_BOOST_PERCENT + "%"),
            LIGHT_GREEN.enclose("▪ " + LIGHT_GRAY.enclose(CURRENCY_NAME + " Boost: ") + "+" + CURRENCY_BOOST_PERCENT + "%"),
            ""
        )).read(cfg);

        this.jobSpecOrderLore = ConfigValue.create("Job.Available.Order.Info", Lists.newList(
            LIGHT_YELLOW.enclose(BOLD.enclose("Special Order:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Timeleft: ") + GENERIC_TIME)
        )).read(cfg);

        this.jobNameLockedPerm = ConfigValue.create("Job.Locked_Permission.Name",
            LIGHT_RED.enclose("[Locked]") + " " + LIGHT_GRAY.enclose(JOB_NAME)
        ).read(cfg);

        this.jobLoreLockedPerm = ConfigValue.create("Job.Locked_Permission.Lore", Lists.newList(
            LIGHT_GRAY.enclose("You don't have " + LIGHT_RED.enclose("permission") + " for this job.")
        )).read(cfg);

        this.jobSlots = ConfigValue.create("Job.Slots", IntStream.range(10, 17).toArray()).read(cfg);
    }
}
