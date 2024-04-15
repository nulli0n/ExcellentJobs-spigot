package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.NightMessage;

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

            this.plugin.getJobManager().createSpecialOrder(player, job, true);
            this.runNextTick(player::closeInventory);
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getHandler().getName().equalsIgnoreCase("special_order")) {
                menuItem.getOptions().addVisibilityPolicy(viewer -> Config.SPECIAL_ORDERS_ENABLED.get());

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
        return new MenuOptions(BLACK.enclose("Job Settings: " + BLUE.enclose(JOB_NAME)), 36, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46");
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(NightMessage.asLegacy(LIGHT_GRAY.enclose("↓ Back")));
        });
        list.add(new MenuItem(back).setSlots(31).setPriority(10).setHandler(this.returnHandler));

        ItemStack objectItem = new ItemStack(Material.BOOK);
        ItemUtil.editMeta(objectItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Objectives")));
            meta.setLore(Lists.newList(LIGHT_GRAY.enclose("Click to browse job objectives!")));
        });
        list.add(new MenuItem(objectItem).setPriority(10).setSlots(10).setHandler(this.objectivesHandler));

        ItemStack orderItem = ItemUtil.getSkinHead("900d28ff7b543dd088d004b1b1f95b38d444ea0461ff5ae3c68d76c0c16e2527");
        ItemUtil.editMeta(orderItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Special Order")));
            meta.setLore(Lists.newList(PLACEHOLDER_ORDER));
        });
        list.add(new MenuItem(orderItem).setPriority(10).setSlots(13).setHandler(this.orderHandler));

        ItemStack leaveItem = ItemUtil.getSkinHead("3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025");
        ItemUtil.editMeta(leaveItem, meta -> {
            meta.setDisplayName(LIGHT_RED.enclose(BOLD.enclose("Leave Job")));
            meta.setLore(Lists.newList(
                DARK_GRAY.enclose("Leaving so soon?"),
                " ",
                LIGHT_GRAY.enclose("Please, note: all job progress will"),
                LIGHT_GRAY.enclose("be lost " + LIGHT_RED.enclose("forever") + "!"),
                " ",
                LIGHT_RED.enclose("[▶] " + LIGHT_GRAY.enclose("Click to " + LIGHT_RED.enclose("leave") + LIGHT_GRAY.enclose(".")))
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
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("Special Orders") + " are set of random job objectives"),
            LIGHT_GRAY.enclose("with " + LIGHT_YELLOW.enclose("unqiue rewards") + " for completion in"),
            LIGHT_GRAY.enclose("specified timeframe."),
            "",
            LIGHT_YELLOW.enclose(BOLD.enclose("Cost: ") + LIGHT_RED.enclose(GENERIC_CURRENCY)),
            "",
            LIGHT_YELLOW.enclose("[▶] " + LIGHT_GRAY.enclose("Click to ") + "get order" + LIGHT_GRAY.enclose("."))
        )).read(cfg);

        this.orderCooldownLore = ConfigValue.create("Format.SpecialOrder.Cooldown", Lists.newList(
            DARK_GRAY.enclose("A new order is still preparing."),
            "",
            LIGHT_GRAY.enclose("You've already completed a Special Order recently."),
            "",
            LIGHT_YELLOW.enclose(BOLD.enclose("Cooldown: ") + LIGHT_RED.enclose(GENERIC_TIME))
        )).read(cfg);

        this.orderHaveLore = ConfigValue.create("Format.SpecialOrder.Active", Lists.newList(
            DARK_GRAY.enclose("You have a Special Order."),
            "",
            LIGHT_YELLOW.enclose(BOLD.enclose("Objectives:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(GENERIC_NAME + ": ") + GENERIC_CURRENT + LIGHT_GRAY.enclose("/") + GENERIC_MAX),
            "",
            LIGHT_YELLOW.enclose(BOLD.enclose("Rewards:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(GENERIC_REWARD)),
            "",
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Timeleft: ") + GENERIC_TIME)
        )).read(cfg);

    }
}
