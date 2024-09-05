package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class JobMenu extends ConfigMenu<JobsPlugin> implements Linked<Job> {

    public static final String FILE_NAME = "job_settings.yml";

    private static final String PLACEHOLDER_ORDER = "%order%";

    private final ItemHandler objectivesHandler;
    private final ItemHandler leaveHandler;
    private final ItemHandler orderHandler;
    private final ItemHandler statsHandler;
    private final ItemHandler returnHandler;
    private final ViewLink<Job> link;

    private List<String> orderAvailableLore;
    private List<String> orderHaveLore;
    private List<String> orderCooldownLore;

    public JobMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobsMenu(viewer.getPlayer()));
        }));

        this.addHandler(this.objectivesHandler = new ItemHandler("objectives", (viewer, event) -> {
            Job job = this.getLink().get(viewer);
            Player player = viewer.getPlayer();

            this.runNextTick(() -> this.plugin.getJobManager().openObjectivesMenu(player, job));
        }));

        this.addHandler(this.leaveHandler = new ItemHandler("leave_job", (viewer, event) -> {
            Job job = this.getLink().get(viewer);
            Player player = viewer.getPlayer();

            this.runNextTick(() -> this.plugin.getJobManager().openLeaveConfirmMenu(player, job));
        }));

        this.addHandler(this.orderHandler = new ItemHandler("special_order", (viewer, event) -> {
            Player player = viewer.getPlayer();
            JobUser user = plugin.getUserManager().getUserData(player);
            Job job = this.getLink().get(player);
            JobData jobData = user.getData(job);

            //if (!event.isShiftClick()) {
                if (jobData.hasOrder() || !jobData.isReadyForNextOrder()) return;
            //}

            this.plugin.getJobManager().createSpecialOrder(player, job, false);
            this.runNextTick(player::closeInventory);
        }));

        this.addHandler(this.statsHandler = new ItemHandler("stats", (viewer, event) -> {
            StatsManager statsManager = plugin.getStatsManager();
            if (statsManager == null) return;

            Player player = viewer.getPlayer();
            Job job = this.getLink().get(player);

            this.runNextTick(() -> statsManager.openStats(player, job));
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            ItemHandler handler = menuItem.getHandler();

            if (handler == this.statsHandler) {
                menuItem.getOptions().setVisibilityPolicy(viewer -> Config.isStatisticEnabled());
            }
            else if (handler == this.orderHandler) {
                menuItem.getOptions().addVisibilityPolicy(viewer -> Config.isSpecialOrdersEnabled());

                menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                    Player player = viewer.getPlayer();
                    JobUser user = plugin.getUserManager().getUserData(player);
                    Job job = this.getLink().get(player);
                    JobData jobData = user.getData(job);
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

                    ItemReplacer.create(item).readMeta().trimmed().hideFlags()
                        .replaceLore(PLACEHOLDER_ORDER, () -> {
                            List<String> lore = new ArrayList<>();
                            for (String line : source) {
                                if (line.contains(GENERIC_CURRENT)) {
                                    orderData.getObjectiveMap().values().forEach(orderObjective -> {
                                        JobObjective jobObjective = job.getObjectiveById(orderObjective.getObjectiveId());
                                        if (jobObjective == null) return;

                                        //loreFinal.add(jobObjective.getDisplayName() + ":");
                                        orderObjective.getObjectCountMap().forEach((object, count) -> {
                                            String oName = jobObjective.getType().getObjectLocalizedName(object);
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
                                        .replace(GENERIC_CURRENCY, job.getSpecialOrdersCost().entrySet().stream().map(entry -> entry.getKey().format(entry.getValue())).collect(Collectors.joining(", ")))
                                        .replace(GENERIC_TIME, TimeUtil.formatDuration(hasActiveOrder ?  orderData.getExpireDate() : jobData.getNextOrderDate()))
                                    );
                                }
                            }
                            return lore;
                        })
                        .writeMeta();
                });
            }
        });
    }

    @Override
    @NotNull
    public ViewLink<Job> getLink() {
        return this.link;
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        Job job = this.getLink().get(viewer);
        options.setTitle(job.replacePlaceholders().apply(options.getTitle()));
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Job Settings: " + BLUE.enclose(JOB_NAME)), MenuSize.CHEST_36);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getLocalizedName());
        });
        list.add(new MenuItem(back).setSlots(31).setPriority(10).setHandler(this.returnHandler));


        ItemStack objectItem = ItemUtil.getSkinHead("5a140a8dfb0f1f5ab979241e58c253a94d65e725f180ab52396e1d8e4c81ce37");
        ItemUtil.editMeta(objectItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Objectives")));
            meta.setLore(Lists.newList(LIGHT_GRAY.enclose("Click to browse job objectives!")));
        });
        list.add(new MenuItem(objectItem).setPriority(10).setSlots(10).setHandler(this.objectivesHandler));



        ItemStack statsItem = ItemUtil.getSkinHead("a8d5cb12219a3f5e9bb68c8914c443c2de160eff00cf3e730fbaccd8db6918fe");
        ItemUtil.editMeta(statsItem, meta -> {
            meta.setDisplayName(LIGHT_CYAN.enclose(BOLD.enclose("Stats")));
            meta.setLore(Lists.newList(LIGHT_GRAY.enclose("Click to view personal job stats!")));
        });
        list.add(new MenuItem(statsItem).setPriority(10).setSlots(12).setHandler(this.statsHandler));



        ItemStack orderItem = ItemUtil.getSkinHead("77b20efd92ffda9e6d7d859b96bb79280e1df5b37bd39a2b5b691da4d85925ef");
        ItemUtil.editMeta(orderItem, meta -> {
            meta.setDisplayName(LIGHT_ORANGE.enclose(BOLD.enclose("Special Order")));
            meta.setLore(Lists.newList(PLACEHOLDER_ORDER));
        });
        list.add(new MenuItem(orderItem).setPriority(10).setSlots(14).setHandler(this.orderHandler));


        ItemStack leaveItem = ItemUtil.getSkinHead("94f90c7bd60bfd0dfc31808d0484d8c2db9959f68df91fbf29423a3da62429a6");
        ItemUtil.editMeta(leaveItem, meta -> {
            meta.setDisplayName(LIGHT_RED.enclose(BOLD.enclose("Leave Job")));
            meta.setLore(Lists.newList(
                DARK_GRAY.enclose("Leaving so soon?"),
                " ",
                LIGHT_GRAY.enclose("Note: all job progress will"),
                LIGHT_GRAY.enclose("be lost " + LIGHT_RED.enclose("forever") + "!"),
                " ",
                LIGHT_RED.enclose("[▶] " + LIGHT_GRAY.enclose("Click to " + LIGHT_RED.enclose("leave") + "."))
            ));
        });
        list.add(new MenuItem(leaveItem).setPriority(10).setSlots(16).setHandler(this.leaveHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {

        this.orderAvailableLore = ConfigValue.create("Format.SpecialOrder.Available", Lists.newList(
            DARK_GRAY.enclose("You don't have Special Orders."),
            "",
            LIGHT_GRAY.enclose(LIGHT_ORANGE.enclose("Special Orders") + " are set of random job objectives"),
            LIGHT_GRAY.enclose("with " + LIGHT_ORANGE.enclose("unqiue rewards") + " for completion in"),
            LIGHT_GRAY.enclose("specified timeframe."),
            "",
            LIGHT_ORANGE.enclose(BOLD.enclose("Cost: ") + LIGHT_RED.enclose(GENERIC_CURRENCY)),
            "",
            LIGHT_ORANGE.enclose("[▶] " + LIGHT_GRAY.enclose("Click to " + LIGHT_ORANGE.enclose("get order") + "."))
        )).read(cfg);

        this.orderCooldownLore = ConfigValue.create("Format.SpecialOrder.Cooldown", Lists.newList(
            DARK_GRAY.enclose("A new order is still preparing."),
            "",
            LIGHT_GRAY.enclose("You've already completed a Special Order recently."),
            "",
            LIGHT_ORANGE.enclose(BOLD.enclose("Cooldown: ") + LIGHT_RED.enclose(GENERIC_TIME))
        )).read(cfg);

        this.orderHaveLore = ConfigValue.create("Format.SpecialOrder.Active", Lists.newList(
            DARK_GRAY.enclose("You have a Special Order."),
            "",
            LIGHT_ORANGE.enclose(BOLD.enclose("Objectives:")),
            LIGHT_ORANGE.enclose("▪ " + LIGHT_GRAY.enclose(GENERIC_NAME + ": ") + GENERIC_CURRENT + LIGHT_GRAY.enclose("/") + GENERIC_MAX),
            "",
            LIGHT_ORANGE.enclose(BOLD.enclose("Rewards:")),
            LIGHT_ORANGE.enclose("▪ " + LIGHT_GRAY.enclose(GENERIC_REWARD)),
            "",
            LIGHT_ORANGE.enclose("▪ " + LIGHT_GRAY.enclose("Timeleft: ") + GENERIC_TIME)
        )).read(cfg);

    }
}
