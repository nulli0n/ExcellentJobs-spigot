package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class ObjectivesMenu extends LinkedMenu<JobsPlugin, ObjectivesMenu.Data> implements Filled<JobObjective>, ConfigBased {

    private static final String FILE_NAME = "job_obectives.yml";

    private static final String ITEMS  = "%objects%";
    private static final String INCOME = "%reward_currency%";
    private static final String XP     = "%reward_xp%";

    private String       objectiveName;
    private List<String> objectiveLockedLore;
    private List<String> objectiveUnlockedLore;
    private NightItem objectiveLockedIcon;
    private List<String> itemsInfo;
    private String       itemEntry;
    private List<String> incomeRewardInfo;
    private List<String> incomeLimitInfo;
    private List<String> xpRewardInfo;
    private List<String> xpLimitInfo;
    private int[]        objectiveSlots;

    private String workTypeName;
    private List<String> workTypeLore;
    private int[] workTypeSlots;

    public record Data(@NotNull Job job, @NotNull Work<?, ?> work){}

    public ObjectivesMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X6, BLACK.wrap(JOB_NAME + " Objectives"));

        this.load(FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
    }

    public void open(@NotNull Player player, @NotNull Job job, @Nullable Work<?, ?> work) {
        if (work == null) {
            work = job.getObjectiveWorkTypes().getFirst();
        }

        this.open(player, new Data(job, work));
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).job.replacePlaceholders().apply(this.title);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);


    }

    @Override
    protected void onReady(@NotNull MenuViewer menuViewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public  MenuFiller<JobObjective> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player).job;
        Work<?, ?> workType = this.getLink(player).work;
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData jobData = user.getData(job);
        List<? extends Work<?, ?>> workTypes = job.getObjectiveWorkTypes();

        for (int index = 0; index < workTypes.size(); index++) {
            if (index >= this.workTypeSlots.length) break;

            Work<?, ?> work = workTypes.get(index);
            this.addItem(viewer, work.getIcon()
                .setDisplayName(this.workTypeName)
                .setLore(this.workTypeLore)
                .replacement(replacer -> replacer
                    .replace(GENERIC_NAME, work::getDisplayName)
                    .replace(GENERIC_DESCRIPTION, work.getDescription())
                )
                .toMenuItem()
                .setPriority(10)
                .setSlots(this.workTypeSlots[index])
                .setHandler((viewer1, event) -> {
                    this.runNextTick(() -> this.open(player, job, work));
                })
            );
        }

        double xpMultiplier = 1D + JobsAPI.getBoost(player, job, MultiplierType.XP) + jobData.getXPBonus();
        double incomeMultiplier = 1D + jobData.getIncomeBonus();
        double incomeBoost = JobsAPI.getBoost(player, job, MultiplierType.INCOME);

        return MenuFiller.builder(this)
            .setSlots(this.objectiveSlots)
            .setItems(job.getObjectives().stream().filter(objective -> objective.isWork(workType)).sorted(Comparator.comparing(JobObjective::getDisplayName)).toList())
            .setItemCreator(objective -> {
                Work<?, ?> workType2 = objective.getWork();
                boolean isUnlocked = objective.isUnlocked(player, jobData);
                NightItem icon = isUnlocked ? objective.getIcon() : this.objectiveLockedIcon.copy();

                if (workType2 == null) return NightItem.fromType(Material.AIR);

                String name = this.objectiveName.replace(Placeholders.OBJECTIVE_NAME, objective.getDisplayName());

                List<String> itemsInfo;
                if (objective.getItems().size() > 1) {
                    itemsInfo = Replacer.create().replace(GENERIC_ENTRY, list -> {
                        objective.getItems().stream().map(workType2::getObjectLocalizedName).sorted(String::compareTo).forEach(object -> {
                            list.add(this.itemEntry.replace(GENERIC_NAME, object));
                        });
                    }).apply(this.itemsInfo);
                }
                else itemsInfo = Collections.emptyList();

                List<String> xpInfo = Replacer.create()
                    .replace(OBJECTIVE_XP_MIN, NumberUtil.format(objective.getXPReward().getMin() * xpMultiplier))
                    .replace(OBJECTIVE_XP_MAX, NumberUtil.format(objective.getXPReward().getMax() * xpMultiplier))
                    .replace(OBJECTIVE_XP_CHANCE, NumberUtil.format(objective.getXPReward().getChance()))
                    .apply(jobData.isXPLimitReached() ? this.xpLimitInfo : this.xpRewardInfo);

                List<String> incomeInfo = new ArrayList<>();
                objective.getPaymentMap().forEach((currencyId, rewardInfo) -> {
                    Currency currency = EconomyBridge.getCurrency(currencyId);
                    if (currency == null) return;

                    double moneyMultiplier = incomeMultiplier;
                    if (JobUtils.canBeBoosted(currency)) {
                        moneyMultiplier += incomeBoost;
                    }

                    for (String line : (jobData.isPaymentLimitReached(currency) ? this.incomeLimitInfo : this.incomeRewardInfo)) {
                        incomeInfo.add(currency.replacePlaceholders().apply(line)
                            .replace(OBJECTIVE_CURRENCY_MIN, currency.format(rewardInfo.getMin() * moneyMultiplier))
                            .replace(OBJECTIVE_CURRENCY_MAX, currency.format(rewardInfo.getMax() * moneyMultiplier))
                            .replace(OBJECTIVE_CURRENCY_CHANCE, NumberUtil.format(rewardInfo.getChance()))
                        );
                    }
                });

                List<String> objectiveLore = objective.getIcon().getLore();

                List<String> lore = Replacer.create()
                    .replace(OBJECTIVE_LORE, objectiveLore == null ? Collections.emptyList() : objectiveLore)
                    .replace(OBJECTIVE_UNLOCK_LEVEL, NumberUtil.format(objective.getUnlockLevel()))
                    .replace(OBJECTIVE_ACTION_TYPE, workType2.getDisplayName())
                    .replace(ITEMS, itemsInfo)
                    .replace(XP, xpInfo)
                    .replace(INCOME, incomeInfo)
                    .apply(isUnlocked ? this.objectiveUnlockedLore : this.objectiveLockedLore);

                return icon.hideAllComponents().setDisplayName(name).setLore(lore);
            }).build();
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.workTypeName = ConfigValue.create("WorkType.Name", GREEN.wrap(BOLD.wrap(GENERIC_NAME))).read(config);

        this.workTypeLore = ConfigValue.create("WorkType.Description", Lists.newList(
            GENERIC_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GREEN.wrap("[▶]") + LIGHT_GRAY.wrap(" Click to " + GREEN.wrap("toggle") + ".")
        )).read(config);

        this.workTypeSlots = ConfigValue.create("WorkType.Slots", new int[]{3,5,4,2,6}).read(config);


        this.objectiveName = ConfigValue.create("Objective.Name",
            LIGHT_GRAY.wrap(BOLD.wrap(OBJECTIVE_NAME))
        ).read(config);

        this.objectiveLockedLore = ConfigValue.create("Objective.Locked", Lists.newList(
            RED.wrap(BOLD.wrap("UNLOCKED AT LEVEL " + OBJECTIVE_UNLOCK_LEVEL))
        )).read(config);

        this.objectiveUnlockedLore = ConfigValue.create("Objective.Unlocked", Lists.newList(
            DARK_GRAY.wrap(OBJECTIVE_ACTION_TYPE),
            EMPTY_IF_BELOW,
            OBJECTIVE_LORE,
            EMPTY_IF_BELOW,
            ITEMS,
            EMPTY_IF_BELOW,
            XP,
            INCOME
        )).read(config);

        this.objectiveLockedIcon = ConfigValue.create("Objective.LockedIcon",
            NightItem.fromType(Material.BARRIER).hideAllComponents()
        ).read(config);

        this.itemsInfo = ConfigValue.create("Objective.Info.Items", Lists.newList(
            LIGHT_YELLOW.wrap(BOLD.wrap("Including:")),
            GENERIC_ENTRY
        )).read(config);

        this.itemEntry = ConfigValue.create("Objective.Info.ItemEntry",
            LIGHT_YELLOW.wrap("→ " + LIGHT_GRAY.wrap(GENERIC_NAME))
        ).read(config);

        this.incomeRewardInfo = ConfigValue.create("Objective.Reward.Currency.Available", Lists.newList(
            LIGHT_GRAY.wrap("• " + WHITE.wrap(CURRENCY_NAME + ": ") + YELLOW.wrap(OBJECTIVE_CURRENCY_MIN) + " ⬌ " + YELLOW.wrap(OBJECTIVE_CURRENCY_MAX) + " (" + WHITE.wrap(OBJECTIVE_CURRENCY_CHANCE + "%") + ")")
        )).read(config);

        this.xpRewardInfo = ConfigValue.create("Objective.Reward.XP.Available", Lists.newList(
            LIGHT_GRAY.wrap("• " + WHITE.wrap("XP: ") + LIGHT_GREEN.wrap(OBJECTIVE_XP_MIN) + " ⬌ " + LIGHT_GREEN.wrap(OBJECTIVE_XP_MAX) + " (" + WHITE.wrap(OBJECTIVE_XP_CHANCE + "%") + ")")
        )).read(config);

        this.incomeLimitInfo = ConfigValue.create("Objective.Reward.Currency.Limit", Lists.newList(
            LIGHT_GRAY.wrap(STRIKETHROUGH.wrap("• " + WHITE.wrap(CURRENCY_NAME + ": ") + LIGHT_RED.wrap(OBJECTIVE_CURRENCY_MIN) + " ⬌ " + LIGHT_RED.wrap(OBJECTIVE_CURRENCY_MAX)) + " (" + RED.wrap("Daily Limit") + ")")
        )).read(config);

        this.xpLimitInfo = ConfigValue.create("Objective.Reward.XP.Limit", Lists.newList(
            LIGHT_GRAY.wrap(STRIKETHROUGH.wrap("• " + WHITE.wrap("XP: ") + LIGHT_RED.wrap(OBJECTIVE_XP_MIN) + " ⬌ " + LIGHT_RED.wrap(OBJECTIVE_XP_MAX)) + " (" + RED.wrap("Daily Limit") + ")")
        )).read(config);

        this.objectiveSlots = ConfigValue.create("Objective.Slots", IntStream.range(18, 45).toArray()).read(config);


        loader.addDefaultItem(MenuItem.buildNextPage(this, 53).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 45).setPriority(10));
        loader.addDefaultItem(MenuItem.buildReturn(this, 49, (viewer, event) -> {
            Player player = viewer.getPlayer();
            Job job = this.getLink(player).job;

            this.runNextTick(() -> plugin.getJobManager().openJobMenu(viewer.getPlayer(), job));
        }).setPriority(10));
    }
}
