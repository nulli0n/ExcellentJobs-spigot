package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.dialog.JobDialogs;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.ItemClick;
import su.nightexpress.nightcore.ui.menu.item.ItemHandler;
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class LevelsMenu extends LinkedMenu<JobsPlugin, Job> implements ConfigBased, Filled<Integer> {

    private NightItem lockedReward;
    private NightItem claimedReward;
    private NightItem unclaimedReward;
    private NightItem upcomingReward;

    private List<String> rewardFormat;

    private int[] rewardSlots;

    public LevelsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X6, BLACK.wrap("[" + JOB_NAME + "] Progression"));
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).replacePlaceholders().apply(this.title);
    }

    public void openAtLevel(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        int level = data.getLevel();

        int limit = this.rewardSlots.length;
        int page = (int) Math.ceil((double) level / (double) limit);

        this.open(player, job, viewer -> viewer.setPage(page));
    }

    @Override
    @NotNull
    public MenuFiller<Integer> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        JobUser user = plugin.getUserManager().getOrFetch(player);
        Job job = this.getLink(player);
        JobData data = user.getData(job);
        JobState state = data.getState();
        int jobLevel = data.getLevel();
        boolean claimRequired = Config.isRewardClaimRequired();

        return MenuFiller.builder(this)
            .setSlots(this.rewardSlots)
            .setItems(IntStream.range(1, job.getMaxLevel() + 1).boxed().toList())
            .setItemCreator(level -> {
                List<LevelReward> rewards = job.getRewards().getRewards(level);

                NightItem item = this.lockedReward.copy();

                if (data.isActive()) {
                    if (level - jobLevel == 1) {
                        item = this.upcomingReward.copy();
                    }
                    else if (jobLevel >= level) {
                        if (claimRequired && !data.isLevelRewardObtained(level) && !rewards.isEmpty()) {
                            item = this.unclaimedReward.copy();
                        }
                        else {
                            item = this.claimedReward.copy();
                        }
                    }
                }

                List<String> rewardFormats = new ArrayList<>();
                rewards.forEach(reward -> rewardFormats.addAll(Replacer.create().replace(reward.replacePlaceholders()).apply(this.rewardFormat)));
                if (rewards.isEmpty()) {
                    rewardFormats.add(CoreLang.OTHER_NONE.text());
                }

                return item
                    .hideAllComponents()
                    .replacement(replacer -> replacer
                        .replace(job.replacePlaceholders())
                        .replace(GENERIC_LEVEL, NumberUtil.format(level))
                        .replace(GENERIC_REWARDS, rewardFormats)
                    );
            })
            .setItemClick(level -> (viewer1, event) -> {
                if (!data.isActive() || !claimRequired || data.isLevelRewardObtained(level) || jobLevel < level) return;

                List<LevelReward> rewards = job.getRewards().getRewards(level, state);
                rewards.forEach(levelReward -> {
                    if (levelReward.isAvailable(player)) {
                        levelReward.run(player);
                        data.setLevelRewardObtained(level);
                    }
                });
                this.plugin.getUserManager().save(user);

                this.runNextTick(() -> this.flush(viewer));
            })
            .build();
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
                .replace(GENERIC_AMOUNT, () -> String.valueOf(user.countJobs(state))) // Old, compatibility
                .replace(GENERIC_MAX, () -> limit < 0 ? CoreLang.OTHER_INFINITY.text() : String.valueOf(limit)) // Old, compatibility
                .replace(GENERIC_PRIMARY_COUNT, () -> String.valueOf(user.countJobs(JobState.PRIMARY)))
                .replace(GENERIC_SECONDARY_COUNT, () -> String.valueOf(user.countJobs(JobState.SECONDARY)))
                .replace(GENERIC_PRIMARY_LIMIT, () -> primLimit < 0 ? CoreLang.OTHER_INFINITY.text() : String.valueOf(primLimit))
                .replace(GENERIC_SECONDARY_LIMIT, () -> secondLimit < 0 ? CoreLang.OTHER_INFINITY.text() : String.valueOf(secondLimit))
            );
        }
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Deprecated
    private void handleJoin(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        this.plugin.getJobManager().joinJob(player, job);
        this.runNextTick(() -> this.flush(player));
    }

    @Deprecated
    private void handlePriority(@NotNull MenuViewer viewer, @NotNull JobState state) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        this.plugin.getJobManager().joinOrLeaveJob(player, job, state, false);
        this.runNextTick(() -> this.flush(player));
    }

    @Deprecated
    private void handleLeave(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        JobData data = plugin.getUserManager().getOrFetch(player).getData(job);
        if (!data.isActive()) return;

        this.plugin.getJobManager().leaveJob(player, job);
        this.runNextTick(() -> this.flush(player));
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

    private void handleJobs(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        this.runNextTick(() -> plugin.getJobManager().openJobsMenu(viewer.getPlayer()));
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        loader.addDefaultItem(NightItem.fromType(Material.GRAY_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(IntStream.range(9, 45).toArray())
        );

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE).setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53)
        );

        if (Version.isAtLeast(Version.MC_1_21_7)) {
            for (JobState state : JobState.values()) {
                Material material = switch (state) {
                    case INACTIVE -> Material.RED_STAINED_GLASS_PANE;
                    case PRIMARY -> Material.LIME_STAINED_GLASS_PANE;
                    case SECONDARY -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                };

                loader.addDefaultItem(NightItem.fromType(material).setHideTooltip(true)
                    .toMenuItem()
                    .setPriority(1)
                    .setSlots(1, 2, 3, 5, 6, 7, 46, 47, 48, 50, 51, 52)
                    .setHandler(this.createDecorativeHandler(state))
                );
            }
        }

        loader.addDefaultItem(MenuItem.buildNextPage(this, 53).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 45).setPriority(10));
        loader.addDefaultItem(MenuItem.buildReturn(this, 49, this::handleJobs).setPriority(10));

        loader.addDefaultItem(NightItem.fromType(Material.GOLD_INGOT)
            .setDisplayName(GRADIENT.with("#FFE033", "#FFF385").and(BOLD).wrap("Levels & Rewards"))
            .setLore(Lists.newList(
                DARK_GRAY.wrap("(You're here)"),
                "",
                GRAY.wrap("Discover the levels allowing you"),
                GRAY.wrap("to obtain " + YELLOW.wrap("unique rewards") + ".")
            ))
            .setEnchantGlint(true)
            .toMenuItem()
            .setSlots(0)
            .setPriority(10)
        );

        loader.addDefaultItem(NightItem.fromType(Material.WRITABLE_BOOK)
            .setDisplayName(GOLD.wrap(BOLD.wrap("Stats")))
            .setLore(Lists.newList(
                GRAY.wrap("Your personal job stats."),
                "",
                GOLD.wrap("→ " + UNDERLINED.wrap("Click to open"))
            ))
            .toMenuItem()
            .setSlots(8)
            .setPriority(10)
            .setHandler(this.createHandler("stats", this::handleStats, true, viewer -> Config.isStatisticEnabled()))
        );

        loader.addDefaultItem(NightItem.fromType(Material.BARRIER)
            .setDisplayName(RED.and(BOLD).wrap("Stats") + " " + GRAY.wrap("(Locked)"))
            .setLore(Lists.newList(
                GRAY.wrap("Get the job to unlock stats!")
            ))
            .toMenuItem()
            .setSlots(8)
            .setPriority(9)
        );

        if (Version.isAtLeast(Version.MC_1_21_7)) {
            loader.addDefaultItem(NightItem.fromType(Material.LIME_DYE)
                .setDisplayName(SOFT_GREEN.and(BOLD).wrap("Status:") + " " + WHITE.wrap(JOB_DATA_STATE))
                .setLore(Lists.newList(
                    GRAY.wrap("Your job status."),
                    " ",
                    SOFT_GREEN.wrap("→ " + UNDERLINED.wrap("Click to change"))
                ))
                .toMenuItem()
                .setSlots(4)
                .setPriority(10)
                .setHandler(this.createStatusHandler(JobState.PRIMARY))
            );

            loader.addDefaultItem(NightItem.fromType(Material.LIGHT_BLUE_DYE)
                .setDisplayName(SOFT_BLUE.and(BOLD).wrap("Status:") + " " + WHITE.wrap(JOB_DATA_STATE))
                .setLore(Lists.newList(
                    GRAY.wrap("Your job status."),
                    " ",
                    SOFT_BLUE.wrap("→ " + UNDERLINED.wrap("Click to change"))
                ))
                .toMenuItem()
                .setSlots(4)
                .setPriority(10)
                .setHandler(this.createStatusHandler(JobState.SECONDARY))
            );

            loader.addDefaultItem(NightItem.fromType(Material.GRAY_DYE)
                .setDisplayName(RED.and(BOLD).wrap("Status:") + " " + WHITE.wrap(JOB_DATA_STATE))
                .setLore(Lists.newList(
                    GRAY.wrap("Your job status."),
                    " ",
                    RED.wrap("→ " + UNDERLINED.wrap("Click to change"))
                ))
                .toMenuItem()
                .setSlots(4)
                .setPriority(10)
                .setHandler(this.createStatusHandler(JobState.INACTIVE))
            );
        }

        this.lockedReward = ConfigValue.create("Reward.Locked", NightItem.fromType(Material.RED_CONCRETE)
            .setDisplayName(RED.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("Locked"))
            .setLore(Lists.newList(
                "",
                RED.wrap(BOLD.wrap("Reward:")),
                GENERIC_REWARDS
            ))
        ).read(config);

        this.unclaimedReward = ConfigValue.create("Reward.Unclaimed", NightItem.fromType(Material.ORANGE_CONCRETE)
            .setEnchantGlint(true)
            .setDisplayName(SOFT_ORANGE.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + RED.wrap("Unclaimed"))
            .setLore(Lists.newList(
                "",
                SOFT_ORANGE.wrap(BOLD.wrap("Reward:")),
                GENERIC_REWARDS,
                "",
                SOFT_ORANGE.wrap("→ " + UNDERLINED.wrap("Click to claim!"))
            ))
        ).read(config);

        this.claimedReward = ConfigValue.create("Reward.Claimed", NightItem.fromType(Material.LIME_CONCRETE)
            .setDisplayName(GREEN.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("Unlocked"))
            .setLore(Lists.newList(
                "",
                GREEN.wrap(BOLD.wrap("Reward:")),
                GENERIC_REWARDS
            ))
        ).read(config);

        this.upcomingReward = ConfigValue.create("Reward.Upcoming", NightItem.fromType(Material.YELLOW_CONCRETE)
            .setDisplayName(YELLOW.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("In Progress"))
            .setLore(Lists.newList(
                "",
                YELLOW.wrap(BOLD.wrap("Reward:")),
                GENERIC_REWARDS
            ))
        ).read(config);

        this.rewardFormat = ConfigValue.create("Reward.Format", Lists.newList(
            GRAY.wrap("• " + REWARD_NAME)
        )).read(config);

        this.rewardSlots = ConfigValue.create("Reward.Slots", IntStream.range(9, 45).toArray()).read(config);

        // Legacy
        if (Version.isBehind(Version.MC_1_21_7)) {
            loader.addHandler(this.createJoinLeaveHandler(true, Job::isJoinable));
            loader.addHandler(this.createJoinLeaveHandler(false, Job::isLeaveable));
            loader.addHandler(this.createPriorityHandler(JobState.SECONDARY));
            loader.addHandler(this.createPriorityHandler(JobState.PRIMARY));

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
                .setSlots(51)
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
                .setSlots(47)
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
                .setSlots(47)
                .setPriority(10)
                .setHandler(this.createPriorityHandler(JobState.SECONDARY))
            );
        }
    }

    @NotNull
    private ItemHandler createDecorativeHandler(@NotNull JobState state) {
        return new ItemHandler(LowerCase.USER_LOCALE.apply(state.name()) + "_decoration", (viewer, event) -> {}, ItemOptions.builder()
            .setVisibilityPolicy(viewer -> {
                Job job = this.getLink(viewer);
                JobData data = plugin.getUserManager().getOrFetch(viewer.getPlayer()).getData(job);
                return data.getState() == state;
            })
            .build());
    }

    @NotNull
    private ItemHandler createStatusHandler(@NotNull JobState state) {
        return new ItemHandler(LowerCase.USER_LOCALE.apply("status_" + state.name()),
            (viewer, event) -> JobDialogs.openJobStateDialog(plugin, viewer.getPlayer(), this.getLink(viewer)),
            ItemOptions.builder()
                .setVisibilityPolicy(viewer -> {
                    Job job = this.getLink(viewer);
                    JobData data = plugin.getUserManager().getOrFetch(viewer.getPlayer()).getData(job);
                    return data.getState() == state;
                })
                .build());
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
    @Deprecated
    private ItemHandler createJoinLeaveHandler(boolean join, @NotNull Predicate<Job> predicate) {
        String name = join ? "join_job" : "leave_job";
        boolean mustActive = !join;
        ItemClick click = join ? this::handleJoin : this::handleLeave;
        Predicate<JobData> additions = data -> predicate.test(data.getJob());

        return this.createHandler(name, click, mustActive, additions);
    }

    @NotNull
    @Deprecated
    private ItemHandler createPriorityHandler(@NotNull JobState state) {
        String name = "set_" + state.name().toLowerCase();
        return this.createHandler(name, (viewer, event) -> this.handlePriority(viewer, state), true, data -> data.getState() == state.getOpposite() && data.getJob().isAllowedState(state));
    }
}
