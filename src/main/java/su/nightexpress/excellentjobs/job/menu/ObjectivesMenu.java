package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.job.work.WorkRegistry;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class ObjectivesMenu extends ConfigMenu<JobsPlugin> implements AutoFilled<JobObjective>, Linked<Job> {

    private static final String FILE_NAME = "job_obectives.yml";

    private static final String PLACEHOLDER_OBJECTS         = "%objects%";
    private static final String PLACEHOLDER_REWARD_CURRENCY = "%reward_currency%";
    private static final String PLACEHOLDER_REWARD_XP       = "%reward_xp%";

    private final ViewLink<Job> link;
    private final ItemHandler returnHandler;

    private String       objName;
    private List<String> lockedLore;
    private List<String> unlockedLore;
    private List<String> objectsLore;
    private List<String> rewardCurrencyAvailLore;
    private List<String> rewardCurrencyLimitLore;
    private List<String> rewardXPAvailLore;
    private List<String> rewardXPLimitLore;
    private int[]        objSlots;

    public ObjectivesMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            Player player = viewer.getPlayer();
            Job job = this.getLink().get(player);
            JobUser user = plugin.getUserManager().getOrFetch(player);
            if (user.getData(job).getState() == JobState.INACTIVE) {
                this.runNextTick(() -> plugin.getJobManager().openPreviewMenu(viewer.getPlayer(), job));
                return;
            }

            this.runNextTick(() -> plugin.getJobManager().openJobMenu(viewer.getPlayer(), this.getLink().get(viewer)));
        }));

        this.load();
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose(JOB_NAME + " Job Objectives"), MenuSize.CHEST_45);
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
        this.objName = ConfigValue.create("Objective.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(OBJECTIVE_NAME))
        ).read(cfg);

        this.lockedLore = ConfigValue.create("Objective.Locked", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " This objective is locked until " + LIGHT_RED.enclose(OBJECTIVE_UNLOCK_LEVEL) + " job level.")
        )).read(cfg);

        this.unlockedLore = ConfigValue.create("Objective.Unlocked", Lists.newList(
            DARK_GRAY.enclose(OBJECTIVE_ACTION_TYPE),
            " ",
            PLACEHOLDER_OBJECTS,
            " ",
            PLACEHOLDER_REWARD_XP,
            PLACEHOLDER_REWARD_CURRENCY
        )).read(cfg);

        this.objectsLore = ConfigValue.create("Objective.Objects", Lists.newList(
            LIGHT_YELLOW.enclose(BOLD.enclose("Objects:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(GENERIC_NAME))
        )).read(cfg);

        this.rewardCurrencyAvailLore = ConfigValue.create("Objective.Reward.Currency.Available", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("✔ ") + CURRENCY_NAME + ": " + LIGHT_GREEN.enclose(OBJECTIVE_CURRENCY_MIN) + " ⬌ " + LIGHT_GREEN.enclose(OBJECTIVE_CURRENCY_MAX) + " (" + WHITE.enclose(OBJECTIVE_CURRENCY_CHANCE + "%") + ")")
        )).read(cfg);

        this.rewardCurrencyLimitLore = ConfigValue.create("Objective.Reward.Currency.Limit", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("✘ ") + CURRENCY_NAME + ": " + LIGHT_RED.enclose(OBJECTIVE_CURRENCY_MIN) + " ⬌ " + LIGHT_RED.enclose(OBJECTIVE_CURRENCY_MAX) + " (" + WHITE.enclose("Daily Limit Reached") + ")")
        )).read(cfg);

        this.rewardXPAvailLore = ConfigValue.create("Objective.Reward.XP.Available", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("✔ ") + "Job XP: " + LIGHT_GREEN.enclose(OBJECTIVE_XP_MIN) + " ⬌ " + LIGHT_GREEN.enclose(OBJECTIVE_XP_MAX) + " (" + WHITE.enclose(OBJECTIVE_XP_CHANCE + "%") + ")")
        )).read(cfg);

        this.rewardXPLimitLore = ConfigValue.create("Objective.Reward.XP.Limit", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("✘ ") + "Job XP: " + LIGHT_RED.enclose(OBJECTIVE_XP_MIN) + " ⬌ " + LIGHT_RED.enclose(OBJECTIVE_XP_MAX) + " (" + WHITE.enclose("Daily Limit Reached") + ")")
        )).read(cfg);

        this.objSlots = ConfigValue.create("Objective.Slots", IntStream.range(0, 36).toArray()).read(cfg);
    }

    @NotNull
    @Override
    public ViewLink<Job> getLink() {
        return link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        Job job = this.getLink().get(viewer);
        options.setTitle(job.replacePlaceholders().apply(options.getTitle()));

        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer menuViewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<JobObjective> autoFill) {
        Player player = viewer.getPlayer();
        Job job = this.getLink().get(player);
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData jobData = user.getData(job);

        int jobLevel = jobData.getLevel();
        double xpGain = 1D + JobsAPI.getBoost(player, job, MultiplierType.XP) + job.getXPMultiplier(jobLevel);
        double incomeGain = 1D + job.getPaymentMultiplier(jobLevel);
        double incomeBoost = JobsAPI.getBoost(player, job, MultiplierType.INCOME);

        autoFill.setSlots(this.objSlots);
        autoFill.setItems(job.getObjectives().stream().sorted(Comparator.comparing(JobObjective::getDisplayName)).toList());
        autoFill.setItemCreator(jobObjective -> {
            String typeId = jobObjective.getWorkId();
            Work<?, ?> workType = WorkRegistry.getByName(typeId);
            boolean isUnlocked = jobObjective.isUnlocked(player, jobData);

            if (workType == null) return new ItemStack(Material.AIR);

            String name = this.objName.replace(Placeholders.OBJECTIVE_NAME, jobObjective.getDisplayName());
            List<String> lore = new ArrayList<>(isUnlocked ? this.unlockedLore : this.lockedLore);
            lore.replaceAll(line -> line
                .replace(Placeholders.OBJECTIVE_UNLOCK_LEVEL, NumberUtil.format(jobObjective.getUnlockLevel()))
                .replace(OBJECTIVE_ACTION_TYPE, workType.getDisplayName())
            );

            List<String> objects = new ArrayList<>();
            for (String line : this.objectsLore) {
                if (line.contains(GENERIC_NAME)) {
                    jobObjective.getObjects().stream().map(workType::getObjectLocalizedName).sorted(String::compareTo).forEach(object -> {
                        objects.add(line.replace(GENERIC_NAME, object));
                    });
                }
                else objects.add(line);
            }

            List<String> rewardXP = new ArrayList<>(jobData.isXPLimitReached() ? this.rewardXPLimitLore : this.rewardXPAvailLore);
            rewardXP.replaceAll(line -> line
                .replace(Placeholders.OBJECTIVE_XP_MIN, NumberUtil.format(jobObjective.getXPReward().getMin() * xpGain))
                .replace(Placeholders.OBJECTIVE_XP_MAX, NumberUtil.format(jobObjective.getXPReward().getMax() * xpGain))
                .replace(Placeholders.OBJECTIVE_XP_CHANCE, NumberUtil.format(jobObjective.getXPReward().getChance()))
            );

            List<String> rewardCurrency = new ArrayList<>();
            jobObjective.getPaymentMap().forEach((currencyId, rewardInfo) -> {
                Currency currency = EconomyBridge.getCurrency(currencyId);
                if (currency == null) return;

                double currencyGain = incomeGain;
                if (JobUtils.canBeBoosted(currency)) {
                    currencyGain += incomeBoost;
                }

                for (String line : (jobData.isPaymentLimitReached(currency) ? this.rewardCurrencyLimitLore : this.rewardCurrencyAvailLore)) {
                    rewardCurrency.add(currency.replacePlaceholders().apply(line)
                        .replace(Placeholders.OBJECTIVE_CURRENCY_MIN, NumberUtil.format(rewardInfo.getMin() * currencyGain))
                        .replace(Placeholders.OBJECTIVE_CURRENCY_MAX, NumberUtil.format(rewardInfo.getMax() * currencyGain))
                        .replace(Placeholders.OBJECTIVE_CURRENCY_CHANCE, NumberUtil.format(rewardInfo.getChance()))
                    );
                }
            });

            ItemStack item = jobObjective.getIcon();
            ItemReplacer.create(item).trimmed().hideFlags()
                .setDisplayName(name)
                .setLore(lore)
                .replace(PLACEHOLDER_OBJECTS, objects)
                .replace(PLACEHOLDER_REWARD_XP, rewardXP)
                .replace(PLACEHOLDER_REWARD_CURRENCY, rewardCurrency)
                .writeMeta();

            return item;
        });
    }
}
