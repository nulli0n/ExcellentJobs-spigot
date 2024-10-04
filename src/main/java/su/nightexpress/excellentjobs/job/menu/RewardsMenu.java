package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.reward.LevelReward;
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
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class RewardsMenu extends ConfigMenu<JobsPlugin> implements AutoFilled<Integer>, Linked<Job> {

    public static final String FILE_NAME = "job_level_rewards.yml";
    private static final String REWARDS = "%rewards%";

    private final ViewLink<Job> link;
    private final ItemHandler   returnHandler;

    private ItemStack lockedReward;
    private ItemStack emptyReward;
    private ItemStack claimedReward;

    private List<String> rewardFormat;

    private int[] rewardSlots;

    public RewardsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobMenu(viewer.getPlayer(), this.getLink().get(viewer)));
        }));

        this.load();
    }

    public void openAtLevel(@NotNull Player player, @NotNull Job job) {
        JobUser user = plugin.getUserManager().getUserData(player);
        JobData data = user.getData(job);
        int level = data.getLevel();

        int limit = this.rewardSlots.length;
        int page = (int) Math.ceil((double) level / (double) limit);

        MenuViewer viewer = this.getViewerOrCreate(player);
        viewer.setPage(page);
        this.open(player, job);
    }

    @Override
    @NotNull
    public ViewLink<Job> getLink() {
        return this.link;
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Integer> autoFill) {
        Player player = viewer.getPlayer();
        JobUser user = plugin.getUserManager().getUserData(player);
        Job job = this.getLink(player);
        JobData data = user.getData(job);
        JobState state = data.getState();
        int jobLevel = data.getLevel();

        autoFill.setSlots(this.rewardSlots);
        autoFill.setItems(IntStream.range(1, job.getMaxLevel(state) + 1).boxed().toList());
        autoFill.setItemCreator(level -> {
            List<LevelReward> rewards = job.getRewards().getRewards(level);

            ItemStack item;
            if (rewards.isEmpty()) {
                item = new ItemStack(this.emptyReward);
            }
            else {
                if (jobLevel >= level) {
                    item = new ItemStack(this.claimedReward);
                }
                else item = new ItemStack(this.lockedReward);
            }

            List<String> rewardFormats = new ArrayList<>();
            rewards.forEach(reward -> {
                List<String> format = new ArrayList<>(this.rewardFormat);
                format.replaceAll(reward.replacePlaceholders());
                rewardFormats.addAll(format);
            });

            ItemReplacer.create(item).readMeta().trimmed().hideFlags()
                .replace(job.getPlaceholders())
                .replace(GENERIC_LEVEL, NumberUtil.format(level))
                .replace(REWARDS, rewardFormats)
                .writeMeta();

            return item;
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Level Rewards"), MenuSize.CHEST_45);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack prevPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(prevPage).setSlots(36).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(nextPage).setSlots(44).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getLocalizedName());
        });
        list.add(new MenuItem(back).setSlots(40).setPriority(10).setHandler(this.returnHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {
        ItemStack lockedItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemUtil.editMeta(lockedItem, meta -> {
            meta.setDisplayName(GRAY.enclose("Level " + GENERIC_LEVEL) + " " + RED.enclose("[Locked]"));
            meta.setLore(Lists.newList(REWARDS));
        });

        ItemStack claimedItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemUtil.editMeta(claimedItem, meta -> {
            meta.setDisplayName(GRAY.enclose("Level " + GENERIC_LEVEL) + " " + GREEN.enclose("[Completed]"));
            meta.setLore(Lists.newList(REWARDS));
        });

        ItemStack emptyItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemUtil.editMeta(emptyItem, meta -> {
            meta.setDisplayName(GRAY.enclose("Level " + GENERIC_LEVEL) + " " + LIGHT_YELLOW.enclose("[No Rewards]"));
            meta.setLore(Lists.newList(REWARDS));
        });

        this.lockedReward = ConfigValue.create("Reward.Locked", lockedItem).read(cfg);
        this.claimedReward = ConfigValue.create("Reward.Claimed", claimedItem).read(cfg);
        this.emptyReward = ConfigValue.create("Reward.Empty", emptyItem).read(cfg);

        this.rewardFormat = ConfigValue.create("Reward.Format", Lists.newList(
            LIGHT_YELLOW.enclose(REWARD_NAME + ":"),
            LIGHT_GRAY.enclose(REWARD_DESCRIPTION),
            EMPTY_IF_BELOW
        )).read(cfg);

        this.rewardSlots = ConfigValue.create("Reward.Slots", new int[]{
            0,1,10,19,28,29,30,21,12,3,4,5,14,23,32,33,34,25,16,7,8
        }).read(cfg);
    }
}
