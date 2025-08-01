package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.ItemClick;
import su.nightexpress.nightcore.ui.menu.item.ItemHandler;
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class JobMenu extends LinkedMenu<JobsPlugin, Job> implements ConfigBased {

    public static final String FILE_NAME = "job_settings.yml";

    private static final String PLACEHOLDER_ORDER = "%order%";

    private List<String> orderAvailableLore;
    private List<String> orderHaveLore;
    private List<String> orderCooldownLore;

    public JobMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap(JOB_NAME + " Job"));

        this.load(FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).replacePlaceholders().apply(this.title);
    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        if (menuItem.getHandler() != null) {
            Player player = viewer.getPlayer();
            Job job = this.getLink(player);
            JobUser user = plugin.getUserManager().getOrFetch(viewer.getPlayer());
            JobData data = user.getData(job);
            JobState state = data.getState();

            int limit = JobManager.getJobsLimit(player, state);
            int primLimit = JobManager.getJobsLimit(player, JobState.PRIMARY);
            int secondLimit = JobManager.getJobsLimit(player, JobState.SECONDARY);

            item.replacement(replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(PLACEHOLDER_ORDER, list -> this.replaceOrder(job, data, list))
                .replace(GENERIC_AMOUNT, () -> String.valueOf(user.countJobs(state))) // Old, compatibility
                .replace(GENERIC_MAX, () -> limit < 0 ? Lang.OTHER_INFINITY.getString() : String.valueOf(limit)) // Old, compatibility
                .replace(GENERIC_PRIMARY_COUNT, () -> String.valueOf(user.countJobs(JobState.PRIMARY)))
                .replace(GENERIC_SECONDARY_COUNT, () -> String.valueOf(user.countJobs(JobState.SECONDARY)))
                .replace(GENERIC_PRIMARY_LIMIT, () -> primLimit < 0 ? Lang.OTHER_INFINITY.getString() : String.valueOf(primLimit))
                .replace(GENERIC_SECONDARY_LIMIT, () -> secondLimit < 0 ? Lang.OTHER_INFINITY.getString() : String.valueOf(secondLimit))
            );
        }
    }

    private void replaceOrder(@NotNull Job job, @NotNull JobData jobData, @NotNull List<String> lore) {
        JobOrderData orderData = jobData.getOrderData();
        boolean hasActiveOrder = jobData.hasOrder() && !orderData.isCompleted();

        List<String> source;
        if (jobData.hasOrder() && !orderData.isCompleted()) {
            source = this.orderHaveLore;
        }
        else if (!jobData.isReadyForNextOrder()) {
            source = this.orderCooldownLore;
        }
        else source = this.orderAvailableLore;

        for (String line : source) {
            if (line.contains(GENERIC_CURRENT)) {
                orderData.getObjectiveMap().values().forEach(orderObjective -> {
                    JobObjective jobObjective = job.getObjectiveById(orderObjective.getObjectiveId());
                    if (jobObjective == null) return;

                    Work<?, ?> workType = jobObjective.getWork();
                    if (workType == null) return;

                    orderObjective.getObjectCountMap().forEach((object, count) -> {
                        String oName = workType.getObjectLocalizedName(object);
                        lore.add(line
                            .replace(GENERIC_NAME, oName)
                            .replace(GENERIC_CURRENT, NumberUtil.format(count.getCurrent()))
                            .replace(GENERIC_MAX, NumberUtil.format(count.getRequired()))
                        );
                    });
                });
            }
            else if (line.contains(GENERIC_REWARD)) {
                orderData.translateRewards().forEach(orderReward -> {
                    lore.add(line.replace(GENERIC_REWARD, orderReward.getName()));
                });
            }
            else {
                lore.add(line
                    .replace(GENERIC_CURRENCY, job.getSpecialOrdersCost().entrySet().stream()
                        .map(entry -> {
                            Currency currency = EconomyBridge.getCurrency(entry.getKey());
                            if (currency == null) return null;

                            return currency.format(entry.getValue());
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", ")))
                    .replace(GENERIC_TIME, TimeFormats.formatDuration(hasActiveOrder ? orderData.getExpireDate() : jobData.getNextOrderDate(), TimeFormatType.LITERAL))
                );
            }
        }
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void handleJoin(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        this.plugin.getJobManager().joinJob(player, job);
        this.runNextTick(() -> this.flush(player));
    }

    private void handlePriority(@NotNull MenuViewer viewer, @NotNull JobState state) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        this.plugin.getJobManager().joinJob(player, job, state, false);
        this.runNextTick(() -> this.flush(player));
    }

    private void handleLeave(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        JobData data = plugin.getUserManager().getOrFetch(player).getData(job);
        if (!data.isActive()) return;

        this.runNextTick(() -> this.plugin.getJobManager().openLeaveConfirmMenu(player, job));
    }

    private void handleObjectives(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);

        this.runNextTick(() -> this.plugin.getJobManager().openObjectivesMenu(player, job));
    }

    private void handleLevels(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);

        this.runNextTick(() -> this.plugin.getJobManager().openRewardsMenu(player, job));
    }

    private void handleMissions(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        if (!data.isActive() || !data.isReadyForNextOrder()) return;

        this.plugin.getJobManager().createSpecialOrder(player, job, false);
        this.runNextTick(player::closeInventory);
    }

    private void handleStats(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        StatsManager statsManager = plugin.getStatsManager();
        if (statsManager == null) return;

        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        JobData data = plugin.getUserManager().getOrFetch(player).getData(job);
        if (!data.isActive()) return;

        this.runNextTick(() -> statsManager.openStats(player, job));
    }

    private void handleReturn(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        this.runNextTick(() -> plugin.getJobManager().openJobsMenu(viewer.getPlayer()));
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig cfg, @NotNull MenuLoader loader) {
        this.orderAvailableLore = ConfigValue.create("Format.SpecialOrder.Available", Lists.newList(
            DARK_GRAY.wrap("You don't have Special Orders."),
            "",
            GRAY.wrap(SOFT_ORANGE.wrap("Special Orders") + " are set of random job objectives"),
            GRAY.wrap("with " + SOFT_ORANGE.wrap("unqiue rewards") + " for completion in"),
            GRAY.wrap("specified timeframe."),
            "",
            SOFT_ORANGE.wrap(BOLD.wrap("Cost: ") + SOFT_RED.wrap(GENERIC_CURRENCY)),
            "",
            SOFT_ORANGE.wrap("→ " + UNDERLINED.wrap("Click to get order"))
        )).read(cfg);

        this.orderCooldownLore = ConfigValue.create("Format.SpecialOrder.Cooldown", Lists.newList(
            DARK_GRAY.wrap("A new order is still preparing."),
            "",
            GRAY.wrap("You've already completed a Special Order recently."),
            "",
            SOFT_ORANGE.wrap(BOLD.wrap("Cooldown: ") + SOFT_RED.wrap(GENERIC_TIME))
        )).read(cfg);

        this.orderHaveLore = ConfigValue.create("Format.SpecialOrder.Active", Lists.newList(
            DARK_GRAY.wrap("You have a Special Order."),
            "",
            SOFT_ORANGE.wrap(BOLD.wrap("Objectives:")),
            SOFT_ORANGE.wrap("▪ " + GRAY.wrap(GENERIC_NAME + ": ") + GENERIC_CURRENT + GRAY.wrap("/") + GENERIC_MAX),
            "",
            SOFT_ORANGE.wrap(BOLD.wrap("Rewards:")),
            SOFT_ORANGE.wrap("▪ " + GRAY.wrap(GENERIC_REWARD)),
            "",
            SOFT_ORANGE.wrap("▪ " + GRAY.wrap("Timeleft: ") + GENERIC_TIME)
        )).read(cfg);

        loader.addDefaultItem(MenuItem.buildReturn(this, 40, this::handleReturn).setPriority(10));

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE).setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44)
        );

        loader.addDefaultItem(NightItem.fromType(Material.COMPASS)
            .setDisplayName(SOFT_AQUA.wrap(BOLD.wrap("Objectives")))
            .setLore(Lists.newList(
                GRAY.wrap("Discover the tasks allowing you"),
                GRAY.wrap("to earn " + SOFT_AQUA.wrap("money") + " and " + SOFT_AQUA.wrap("experience") + "."),
                "",
                SOFT_AQUA.wrap("→ " + UNDERLINED.wrap("Click to open"))
            ))
            .toMenuItem()
            .setSlots(19)
            .setPriority(10)
            .setHandler(new ItemHandler("objectives", this::handleObjectives))
        );

        loader.addDefaultItem(NightItem.fromType(Material.EXPERIENCE_BOTTLE)
            .setDisplayName(SOFT_YELLOW.wrap(BOLD.wrap("Levels")))
            .setLore(Lists.newList(
                GRAY.wrap(SOFT_YELLOW.wrap("• ") + "Level: " + SOFT_YELLOW.wrap(JOB_DATA_LEVEL) + " (" + JOB_DATA_XP + "/" + JOB_DATA_XP_MAX + " XP)"),
                "",
                GRAY.wrap("Discover the levels allowing you"),
                GRAY.wrap("to obtain " + SOFT_YELLOW.wrap("unique rewards") + "."),
                "",
                SOFT_YELLOW.wrap("→ " + UNDERLINED.wrap("Click to open"))
            ))
            .toMenuItem()
            .setSlots(21)
            .setPriority(10)
            .setHandler(new ItemHandler("rewards", this::handleLevels))
        );

        loader.addDefaultItem(NightItem.fromType(Material.CHEST)
            .setDisplayName(SOFT_ORANGE.wrap(BOLD.wrap("Special Order")))
            .setLore(Lists.newList(PLACEHOLDER_ORDER))
            .toMenuItem()
            .setSlots(23)
            .setPriority(10)
            .setHandler(this.createHandler("special_order", this::handleMissions, true, viewer -> Config.isSpecialOrdersEnabled()))
        );

        loader.addDefaultItem(NightItem.fromType(Material.CHEST)
            .setDisplayName(SOFT_ORANGE.wrap(BOLD.wrap("Special Order")) + " " + RED.wrap("(Locked)"))
            .setLore(Lists.newList(
                GRAY.wrap("Orders will become available once"),
                GRAY.wrap("join the job.")
            ))
            .toMenuItem()
            .setSlots(23)
            .setPriority(9)
        );

        loader.addDefaultItem(NightItem.fromType(Material.KNOWLEDGE_BOOK)
            .setDisplayName(SOFT_GREEN.wrap(BOLD.wrap("Stats")))
            .setLore(Lists.newList(
                GRAY.wrap("Your personal job stats."),
                "",
                SOFT_GREEN.wrap("→ " + UNDERLINED.wrap("Click to open"))
            ))
            .toMenuItem()
            .setSlots(25)
            .setPriority(10)
            .setHandler(this.createHandler("stats", this::handleStats, true, viewer -> Config.isStatisticEnabled()))
        );

        loader.addDefaultItem(NightItem.fromType(Material.KNOWLEDGE_BOOK)
            .setDisplayName(SOFT_GREEN.wrap(BOLD.wrap("Stats")) + " " + RED.wrap("(Locked)"))
            .setLore(Lists.newList(
                GRAY.wrap("Stats will become available once"),
                GRAY.wrap("join the job.")
            ))
            .toMenuItem()
            .setSlots(25)
            .setPriority(9)
        );

        loader.addDefaultItem(NightItem.fromType(Material.LIME_DYE)
            .setDisplayName(SOFT_GREEN.wrap(BOLD.wrap("Join Job")))
            .setLore(Lists.newList(
                GRAY.wrap("Are you intertesed in"),
                GRAY.wrap("getting the " + WHITE.wrap(JOB_NAME) + " job?"),
                " ",
                SOFT_GREEN.and(BOLD).wrap("YOUR LIMITS"),
                GRAY.wrap("Primary Jobs: " + SOFT_GREEN.wrap(GENERIC_PRIMARY_COUNT) + "/" + SOFT_GREEN.wrap(GENERIC_PRIMARY_LIMIT)),
                GRAY.wrap("Secondary Jobs: " + SOFT_GREEN.wrap(GENERIC_SECONDARY_COUNT) + "/" + SOFT_GREEN.wrap(GENERIC_SECONDARY_LIMIT)),
                " ",
                SOFT_GREEN.wrap("→ " + UNDERLINED.wrap("Click to join"))
            ))
            .toMenuItem()
            .setSlots(4)
            .setPriority(10)
            .setHandler(this.createJoinLeaveHandler(true, Job::isJoinable))
        );

        loader.addDefaultItem(NightItem.fromType(Material.BARRIER)
            .setDisplayName(SOFT_RED.wrap(BOLD.wrap("Leave")))
            .setLore(Lists.newList(
                GRAY.wrap("Leave the job losing all"),
                GRAY.wrap("the XP and levels."),
                " ",
                SOFT_RED.wrap("→ " + UNDERLINED.wrap("Click to continue"))
            ))
            .toMenuItem()
            .setSlots(44)
            .setPriority(10)
            .setHandler(this.createJoinLeaveHandler(false, Job::isLeaveable))
        );

        loader.addDefaultItem(NightItem.fromType(Material.STRUCTURE_VOID)
            .setDisplayName(SOFT_BLUE.wrap(BOLD.wrap("Set Primary")))
            .setLore(Lists.newList(
                GRAY.wrap("Sets this job as " + SOFT_BLUE.wrap("primary") + " to"),
                GRAY.wrap("gain more " + SOFT_GREEN.wrap("XP") + ", " + SOFT_GREEN.wrap("money") + " and " + SOFT_GREEN.wrap("rewards") + "."),
                " ",
                GRAY.wrap("You have " + SOFT_BLUE.wrap(GENERIC_PRIMARY_COUNT) + "/" + SOFT_BLUE.wrap(GENERIC_PRIMARY_LIMIT) + " primary jobs."),
                " ",
                SOFT_BLUE.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
            ))
            .toMenuItem()
            .setSlots(36)
            .setPriority(10)
            .setHandler(this.createPriorityHandler(JobState.PRIMARY))
        );

        loader.addDefaultItem(NightItem.fromType(Material.STRUCTURE_VOID)
            .setDisplayName(SOFT_BLUE.wrap(BOLD.wrap("Set Secondary")))
            .setLore(Lists.newList(
                GRAY.wrap("Sets this job as " + SOFT_BLUE.wrap("secondary") + " to"),
                GRAY.wrap("unlock slot for a primary job at"),
                GRAY.wrap("a cost of " + SOFT_RED.wrap("less XP") + ", " + SOFT_RED.wrap("money") + " and " + SOFT_RED.wrap("rewards") + "."),
                " ",
                GRAY.wrap("You have " + SOFT_BLUE.wrap(GENERIC_SECONDARY_COUNT) + "/" + SOFT_BLUE.wrap(GENERIC_SECONDARY_LIMIT) + " secondary jobs."),
                " ",
                SOFT_BLUE.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
            ))
            .toMenuItem()
            .setSlots(36)
            .setPriority(10)
            .setHandler(this.createPriorityHandler(JobState.SECONDARY))
        );
    }

    @NotNull
    private ItemHandler createHandler(@NotNull String name, @NotNull ItemClick click, boolean mustActive, @Nullable Predicate<JobData> additions) {
        return new ItemHandler(name, click, ItemOptions.builder().setVisibilityPolicy(viewer -> {
            Job job = this.getLink(viewer);
            JobData data = plugin.getUserManager().getOrFetch(viewer.getPlayer()).getData(job);
            return data.isActive() == mustActive && (additions == null || additions.test(data));
        }).build());
    }

    @NotNull
    private ItemHandler createJoinLeaveHandler(boolean join, @NotNull Predicate<Job> predicate) {
        String name = join ? "join_job" : "leave_job";
        boolean mustActive = !join;
        ItemClick click = join ? this::handleJoin : this::handleLeave;
        Predicate<JobData> additions = data -> predicate.test(data.getJob());

        return this.createHandler(name, click, mustActive, additions);
    }

    @NotNull
    private ItemHandler createPriorityHandler(@NotNull JobState state) {
        String name = "set_" + state.name().toLowerCase();
        return this.createHandler(name, (viewer, event) -> this.handlePriority(viewer, state), true, data -> data.getState() == state.getOpposite() && data.getJob().isAllowedState(state));
    }
}
