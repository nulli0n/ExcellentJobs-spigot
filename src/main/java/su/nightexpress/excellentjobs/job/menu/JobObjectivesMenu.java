package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
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
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobObjectivesMenu extends ConfigMenu<JobsPlugin> implements AutoFilled<JobObjective>, Linked<Job> {

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

    public JobObjectivesMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobMenu(viewer.getPlayer(), this.getLink().get(viewer)));
        }));

        this.load();
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose(JOB_NAME + " Job Objectives"), 45, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack prevPage = ItemUtil.getSkinHead("86971dd881dbaf4fd6bcaa93614493c612f869641ed59d1c9363a3666a5fa6");
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(NightMessage.asLegacy(LIGHT_GRAY.enclose("← Previous Page")));
        });
        list.add(new MenuItem(prevPage).setSlots(36).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead("f32ca66056b72863e98f7f32bd7d94c7a0d796af691c9ac3a9136331352288f9");
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(NightMessage.asLegacy(LIGHT_GRAY.enclose("Next Page →")));
        });
        list.add(new MenuItem(nextPage).setSlots(44).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        ItemStack back = ItemUtil.getSkinHead("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46");
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(NightMessage.asLegacy(LIGHT_GRAY.enclose("↓ Back")));
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
        JobUser user = plugin.getUserManager().getUserData(player);
        JobData jobData = user.getData(job);

        autoFill.setSlots(this.objSlots);
        autoFill.setItems(job.getObjectives().stream().sorted(Comparator.comparing(JobObjective::getDisplayName)).toList());
        autoFill.setItemCreator(jobObjective -> {
            ActionType<?, ?> type = jobObjective.getType();
            boolean isUnlocked = jobObjective.isUnlocked(player, jobData);

            String name = this.objName.replace(Placeholders.OBJECTIVE_NAME, jobObjective.getDisplayName());
            List<String> lore = new ArrayList<>(isUnlocked ? this.unlockedLore : this.lockedLore);
            lore.replaceAll(line -> line
                .replace(Placeholders.OBJECTIVE_UNLOCK_LEVEL, NumberUtil.format(jobObjective.getUnlockLevel()))
                .replace(OBJECTIVE_ACTION_TYPE, jobObjective.getType().getDisplayName())
            );

            List<String> objects = new ArrayList<>();
            for (String line : this.objectsLore) {
                if (line.contains(GENERIC_NAME)) {
                    jobObjective.getObjects().stream().map(type::getObjectLocalizedName).sorted(String::compareTo).forEach(object -> {
                        objects.add(line.replace(GENERIC_NAME, object));
                    });
                }
                else objects.add(line);
            }

            List<String> rewardXP = new ArrayList<>(jobData.isXPLimitReached() ? this.rewardXPLimitLore : this.rewardXPAvailLore);
            rewardXP.replaceAll(line -> line
                .replace(Placeholders.OBJECTIVE_XP_MIN, NumberUtil.format(jobObjective.getXPReward().getMin()))
                .replace(Placeholders.OBJECTIVE_XP_MAX, NumberUtil.format(jobObjective.getXPReward().getMax()))
                .replace(Placeholders.OBJECTIVE_XP_CHANCE, NumberUtil.format(jobObjective.getXPReward().getChance()))
            );

            List<String> rewardCurrency = new ArrayList<>();
            jobObjective.getPaymentMap().forEach((currency, rewardInfo) -> {
                for (String line : (jobData.isPaymentLimitReached(currency) ? this.rewardCurrencyLimitLore : this.rewardCurrencyAvailLore)) {
                    rewardCurrency.add(currency.replacePlaceholders().apply(line)
                        .replace(Placeholders.OBJECTIVE_CURRENCY_MIN, NumberUtil.format(rewardInfo.getMin()))
                        .replace(Placeholders.OBJECTIVE_CURRENCY_MAX, NumberUtil.format(rewardInfo.getMax()))
                        .replace(Placeholders.OBJECTIVE_CURRENCY_CHANCE, NumberUtil.format(rewardInfo.getChance()))
                    );
                }
            });

            ItemStack item = jobObjective.getIcon();
            ItemReplacer.create(item).trimmed().hideFlags()
                .setDisplayName(name)
                .setLore(lore)
                .replaceLoreExact(PLACEHOLDER_OBJECTS, objects)
                .replaceLoreExact(PLACEHOLDER_REWARD_XP, rewardXP)
                .replaceLoreExact(PLACEHOLDER_REWARD_CURRENCY, rewardCurrency)
                .writeMeta();

            return item;
        });
    }
}
