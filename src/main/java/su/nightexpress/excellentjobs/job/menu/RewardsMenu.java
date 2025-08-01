package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class RewardsMenu extends LinkedMenu<JobsPlugin, Job> implements Filled<Integer>, ConfigBased {

    public static final String FILE_NAME = "job_level_rewards.yml";
    private static final String REWARDS = "%rewards%";

    private NightItem lockedReward;
    private NightItem emptyReward;
    private NightItem claimedReward;
    private NightItem unclaimedReward;
    private NightItem upcomingReward;

    private List<String> rewardFormat;

    private int[] rewardSlots;

    public RewardsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X6, BLACK.wrap("Level Rewards"));

        this.load(FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
    }

    public void openAtLevel(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);
        int level = data.getLevel();

        int limit = this.rewardSlots.length;
        int page = (int) Math.ceil((double) level / (double) limit);

        this.open(player, job, viewer -> {
            viewer.setPage(page);
        });
    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        Job job = this.getLink(viewer);
        JobUser user = plugin.getUserManager().getOrFetch(viewer.getPlayer());
        JobData jobData = user.getData(job);

        item.replacement(replacer -> replacer.replace(jobData.replaceAllPlaceholders()));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

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
        AtomicInteger upcoming = new AtomicInteger(-1);
        
        return MenuFiller.builder(this)
            .setSlots(this.rewardSlots)
            .setItems(IntStream.range(1, job.getMaxLevel() + 1).boxed().toList())
            .setItemCreator(level -> {
                List<LevelReward> rewards = job.getRewards().getRewards(level);

                NightItem item = (rewards.isEmpty() ? this.emptyReward : this.lockedReward).copy();
                boolean hasRewards = rewards.stream().anyMatch(reward -> reward.isAvailable(player) && reward.isGoodState(state));

                if (hasRewards) {
                    if (data.isLevelRewardObtained(level)) {
                        item = this.claimedReward.copy();
                    }
                    else if (jobLevel == level) {
                        item = (claimRequired ? this.unclaimedReward : this.claimedReward).copy();
                    }
                    else if (level > jobLevel && upcoming.get() < 0) {
                        item = this.upcomingReward.copy();
                        upcoming.set(level);
                    }
                }

                List<String> rewardFormats = new ArrayList<>();
                rewards.forEach(reward -> {
                    rewardFormats.addAll(Replacer.create().replace(reward.replacePlaceholders()).apply(this.rewardFormat));
                });

                return item
                    .hideAllComponents()
                    .replacement(replacer -> replacer
                        .replace(job.replacePlaceholders())
                        .replace(GENERIC_LEVEL, NumberUtil.format(level))
                        .replace(REWARDS, rewardFormats)
                    );
            })
            .setItemClick(level -> (viewer1, event) -> {
                if (!data.isActive() || !claimRequired || data.isLevelRewardObtained(level) || jobLevel < level) return;

                List<LevelReward> rewards = job.getRewards().getRewards(level, state);
                rewards.forEach(levelReward -> {
                    if (levelReward.isAvailable(player)) {
                        levelReward.run(player);
                        data.setLevelRewardObtained(level);
                        this.plugin.getUserManager().save(user);
                    }
                });

                this.runNextTick(() -> this.flush(viewer));
            })
            .build();
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        NightItem lockedItem = new NightItem(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName(RED.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("Locked"))
            .setLore(Lists.newList(
                "",
                RED.wrap(BOLD.wrap("Reward:")),
                REWARDS
            ));

        NightItem unclaimedItem = new NightItem(Material.ORANGE_STAINED_GLASS_PANE)
            .setEnchantGlint(true)
            .setDisplayName(SOFT_ORANGE.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + RED.wrap("Unclaimed"))
            .setLore(Lists.newList(
                "",
                SOFT_ORANGE.wrap(BOLD.wrap("Reward:")),
                REWARDS,
                "",
                SOFT_ORANGE.wrap("→ " + UNDERLINED.wrap("Click to claim!"))
            ));

        NightItem claimedItem = new NightItem(Material.LIME_STAINED_GLASS_PANE)
            .setDisplayName(GREEN.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("Unlocked"))
            .setLore(Lists.newList(
                "",
                GREEN.wrap(BOLD.wrap("Reward:")),
                REWARDS
            ));

        NightItem emptyItem = new NightItem(Material.BLACK_STAINED_GLASS_PANE)
            .setDisplayName(GRAY.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("No Rewards"));

        NightItem upcomingItem = new NightItem(Material.YELLOW_STAINED_GLASS_PANE)
            .setDisplayName(YELLOW.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("In Progress"))
            .setLore(Lists.newList(
                "",
                YELLOW.wrap(BOLD.wrap("Reward:")),
                REWARDS
            ));

        this.lockedReward = ConfigValue.create("Reward.Locked", lockedItem).read(config);
        this.unclaimedReward = ConfigValue.create("Reward.Unclaimed", unclaimedItem).read(config);
        this.claimedReward = ConfigValue.create("Reward.Claimed", claimedItem).read(config);
        this.emptyReward = ConfigValue.create("Reward.Empty", emptyItem).read(config);
        this.upcomingReward = ConfigValue.create("Reward.Upcoming", upcomingItem).read(config);

        this.rewardFormat = ConfigValue.create("Reward.Format", Lists.newList(
            GRAY.wrap("• " + REWARD_NAME)
        )).read(config);

        this.rewardSlots = ConfigValue.create("Reward.Slots", new int[]{
            0,9,18,27,28,29,20,11,2,3,4,13,22,31,32,33,24,15,6,7,8,17,26,35,44,53
        }).read(config);

        loader.addDefaultItem(MenuItem.buildNextPage(this, 50).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 48).setPriority(10));
        loader.addDefaultItem(MenuItem.buildReturn(this, 49, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobMenu(viewer.getPlayer(), this.getLink(viewer)));
        }).setPriority(10));
    }
}
