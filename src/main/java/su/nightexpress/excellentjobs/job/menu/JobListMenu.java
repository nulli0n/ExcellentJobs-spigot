package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
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

public class JobListMenu extends ConfigMenu<JobsPlugin> implements AutoFilled<Job> {

    private static final String FILE_NAME = "job_list.yml";

    private static final String PLACEHOLDER_ORDER = "%order%";
    private static final String PLACEHOLDER_STATUS = "%status%";
    private static final String PLACEHOLDER_BOOSTER = "%booster%";
    private static final String PLACEHOLDER_STATE = "%state%";

    private String       jobNameAvailable;
    private List<String> jobLoreAvailable;
    private List<String> jobAvailJoinPrimLore;
    private List<String> jobAvailJoinSeconLore;
    private List<String> jobAvailSettingsLore;
    private List<String> jobAvailLimitLore;
    private List<String> jobSpecOrderLore;
    private List<String> jobBoosterLore;
    private List<String> jobPrimStateLore;
    private List<String> jobSecondStateLore;
    private List<String> jobInactiveStateLore;
    private String       jobNameLockedPerm;
    private List<String> jobLoreLockedPerm;
    private int[]        jobSlots;

    public JobListMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));

        this.load();
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
            JobData jobData = user.getData(job);
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
            else if (this.plugin.getJobManager().canGetMoreJobs(player, JobState.PRIMARY)) {
                status = new ArrayList<>(this.jobAvailJoinPrimLore);
            }
            else if (this.plugin.getJobManager().canGetMoreJobs(player, JobState.SECONDARY)) {
                status = new ArrayList<>(this.jobAvailJoinSeconLore);
            }
            else {
                status = new ArrayList<>(this.jobAvailLimitLore);
            }

            List<String> boosterInfo = new ArrayList<>();
            if (!boosters.isEmpty()) {
                for (String line : this.jobBoosterLore) {
                    if (line.contains(CURRENCY_BOOST_PERCENT) || line.contains(CURRENCY_BOOST_MODIFIER)) {
                        for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
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
                    for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
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

            ItemStack item = job.getIcon();
            ItemReplacer.create(item).trimmed().hideFlags()
                .setDisplayName(name).setLore(loreFinal)
                .replaceLoreExact(PLACEHOLDER_STATE, state)
                .replaceLoreExact(PLACEHOLDER_BOOSTER, boosterInfo)
                .replaceLoreExact(PLACEHOLDER_STATUS, status)
                .replaceLoreExact(PLACEHOLDER_ORDER, order)
                .replace(jobData.getPlaceholders())
                .replace(job.getPlaceholders())
                .writeMeta();

            return item;
        });

        autoFill.setClickAction(job -> (viewer1, event) -> {
            Player player1 = viewer1.getPlayer();

            JobUser user1 = this.plugin.getUserManager().getUserData(player1);
            if (user1.getData(job).getState() != JobState.INACTIVE) {
                this.plugin.getJobManager().openJobMenu(player1, job);
                return;
            }

            if (!job.hasPermission(player1)) {
                Lang.ERROR_NO_PERMISSION.getMessage().send(player1);
                return;
            }

            if (this.plugin.getJobManager().canGetMoreJobs(player1)) {
                this.runNextTick(() -> this.plugin.getJobManager().openJoinConfirmMenu(player1, job));
            }
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose(BOLD.enclose("Jobs")), 27, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack prevPage = ItemUtil.getSkinHead("86971dd881dbaf4fd6bcaa93614493c612f869641ed59d1c9363a3666a5fa6");
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(LIGHT_GRAY.enclose("← Previous Page"));
        });
        list.add(new MenuItem(prevPage).setSlots(18).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead("f32ca66056b72863e98f7f32bd7d94c7a0d796af691c9ac3a9136331352288f9");
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(LIGHT_GRAY.enclose("Next Page →"));
        });
        list.add(new MenuItem(nextPage).setSlots(26).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        ItemStack back = ItemUtil.getSkinHead("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46");
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(LIGHT_RED.enclose("✕ Exit"));
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
            PLACEHOLDER_BOOSTER,
            "",
            PLACEHOLDER_ORDER,
            "",
            PLACEHOLDER_STATUS
        )).read(cfg);

        this.jobAvailJoinPrimLore = ConfigValue.create("Job.Available.Status.Join_Primary", Lists.newList(
            LIGHT_GREEN.enclose("[▶] " + LIGHT_GRAY.enclose("Click to") + " get as primary" + LIGHT_GRAY.enclose("."))
        )).read(cfg);

        this.jobAvailJoinSeconLore = ConfigValue.create("Job.Available.Status.Join_Secondary", Lists.newList(
            LIGHT_GREEN.enclose("[▶] " + LIGHT_GRAY.enclose("Click to") + " get as secondary" + LIGHT_GRAY.enclose("."))
        )).read(cfg);

        this.jobAvailSettingsLore = ConfigValue.create("Job.Available.Status.Settings", Lists.newList(
            LIGHT_YELLOW.enclose("[▶] " + LIGHT_GRAY.enclose("Click to") + " open settings" + LIGHT_GRAY.enclose("."))
        )).read(cfg);

        this.jobAvailLimitLore = ConfigValue.create("Job.Available.Status.Limit", Lists.newList(
            LIGHT_RED.enclose("[❗] " + LIGHT_GRAY.enclose("You can't get") + " more " + LIGHT_GRAY.enclose("jobs."))
        )).read(cfg);

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
