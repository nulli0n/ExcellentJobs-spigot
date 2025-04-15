package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
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
import java.util.stream.Collectors;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

@SuppressWarnings("UnstableApiUsage")
public class JobMenu extends LinkedMenu<JobsPlugin, Job> implements ConfigBased {

    public static final String FILE_NAME = "job_settings.yml";

    private static final String PLACEHOLDER_ORDER = "%order%";

    private List<String> orderAvailableLore;
    private List<String> orderHaveLore;
    private List<String> orderCooldownLore;

    public JobMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Job Settings: " + BLUE.wrap(JOB_NAME)));

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
            Job job = this.getLink(viewer);
            JobUser user = plugin.getUserManager().getOrFetch(viewer.getPlayer());
            JobData jobData = user.getData(job);

            item.replacement(replacer -> replacer.replace(jobData.replaceAllPlaceholders()));
        }
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void loadConfiguration(@NotNull FileConfig cfg, @NotNull MenuLoader loader) {
        this.orderAvailableLore = ConfigValue.create("Format.SpecialOrder.Available", Lists.newList(
            DARK_GRAY.wrap("You don't have Special Orders."),
            "",
            LIGHT_GRAY.wrap(LIGHT_ORANGE.wrap("Special Orders") + " are set of random job objectives"),
            LIGHT_GRAY.wrap("with " + LIGHT_ORANGE.wrap("unqiue rewards") + " for completion in"),
            LIGHT_GRAY.wrap("specified timeframe."),
            "",
            LIGHT_ORANGE.wrap(BOLD.wrap("Cost: ") + LIGHT_RED.wrap(GENERIC_CURRENCY)),
            "",
            LIGHT_ORANGE.wrap("[▶] " + LIGHT_GRAY.wrap("Click to " + LIGHT_ORANGE.wrap("get order") + "."))
        )).read(cfg);

        this.orderCooldownLore = ConfigValue.create("Format.SpecialOrder.Cooldown", Lists.newList(
            DARK_GRAY.wrap("A new order is still preparing."),
            "",
            LIGHT_GRAY.wrap("You've already completed a Special Order recently."),
            "",
            LIGHT_ORANGE.wrap(BOLD.wrap("Cooldown: ") + LIGHT_RED.wrap(GENERIC_TIME))
        )).read(cfg);

        this.orderHaveLore = ConfigValue.create("Format.SpecialOrder.Active", Lists.newList(
            DARK_GRAY.wrap("You have a Special Order."),
            "",
            LIGHT_ORANGE.wrap(BOLD.wrap("Objectives:")),
            LIGHT_ORANGE.wrap("▪ " + LIGHT_GRAY.wrap(GENERIC_NAME + ": ") + GENERIC_CURRENT + LIGHT_GRAY.wrap("/") + GENERIC_MAX),
            "",
            LIGHT_ORANGE.wrap(BOLD.wrap("Rewards:")),
            LIGHT_ORANGE.wrap("▪ " + LIGHT_GRAY.wrap(GENERIC_REWARD)),
            "",
            LIGHT_ORANGE.wrap("▪ " + LIGHT_GRAY.wrap("Timeleft: ") + GENERIC_TIME)
        )).read(cfg);

        loader.addDefaultItem(MenuItem.buildReturn(this, 40, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobsMenu(viewer.getPlayer()));
        }).setPriority(10));

        loader.addDefaultItem(NightItem.asCustomHead("5a140a8dfb0f1f5ab979241e58c253a94d65e725f180ab52396e1d8e4c81ce37")
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("Objectives")))
            .setLore(Lists.newList(
                LIGHT_GRAY.wrap("Discover the tasks allowing you"),
                LIGHT_GRAY.wrap("to earn " + LIGHT_YELLOW.wrap("money") + " and " + LIGHT_YELLOW.wrap("experience") + ".")
            ))
            .toMenuItem()
            .setSlots(10)
            .setPriority(10)
            .setHandler(new ItemHandler("objectives", (viewer, event) -> {
                Player player = viewer.getPlayer();
                Job job = this.getLink(player);

                this.runNextTick(() -> this.plugin.getJobManager().openObjectivesMenu(player, job));
            }))
        );

        loader.addDefaultItem(NightItem.asCustomHead("c8c758ab08cbe59730972c9c2941f95475804858ce4b0a2b49f5b5c5027d66c")
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("Level Rewards")))
            .setLore(Lists.newList(
                LIGHT_GRAY.wrap("Discover the levels allowing you"),
                LIGHT_GRAY.wrap("to obtain " + LIGHT_YELLOW.wrap("unique rewards") + "."),
                "",
                LIGHT_GRAY.wrap("• " + WHITE.wrap("Current Level: ") + LIGHT_YELLOW.wrap(JOB_DATA_LEVEL) + " (" + JOB_DATA_XP + "/" + JOB_DATA_XP_MAX + ")")
            ))
            .toMenuItem()
            .setSlots(20)
            .setPriority(10)
            .setHandler(new ItemHandler("rewards", (viewer, event) -> {
                Player player = viewer.getPlayer();
                Job job = this.getLink(player);

                this.runNextTick(() -> this.plugin.getJobManager().openRewardsMenu(player, job));
            }))
        );

        loader.addDefaultItem(NightItem.asCustomHead("a8d5cb12219a3f5e9bb68c8914c443c2de160eff00cf3e730fbaccd8db6918fe")
            .setDisplayName(LIGHT_CYAN.wrap(BOLD.wrap("Stats")))
            .setLore(Lists.newList(LIGHT_GRAY.wrap("Click to view personal job stats!")))
            .toMenuItem()
            .setSlots(24)
            .setPriority(10)
            .setHandler(new ItemHandler("stats", (viewer, event) -> {
                StatsManager statsManager = plugin.getStatsManager();
                if (statsManager == null) return;

                Player player = viewer.getPlayer();
                Job job = this.getLink(player);

                this.runNextTick(() -> statsManager.openStats(player, job));
            }, ItemOptions.builder()
                .setVisibilityPolicy(viewer -> Config.isStatisticEnabled())
                .build()))
        );

        loader.addDefaultItem(NightItem.asCustomHead("77b20efd92ffda9e6d7d859b96bb79280e1df5b37bd39a2b5b691da4d85925ef")
            .setDisplayName(LIGHT_ORANGE.wrap(BOLD.wrap("Special Order")))
            .setLore(Lists.newList(PLACEHOLDER_ORDER))
            .toMenuItem()
            .setSlots(16)
            .setPriority(10)
            .setHandler(new ItemHandler("special_order", (viewer, event) -> {
                Player player = viewer.getPlayer();
                Job job = this.getLink(player);
                JobUser user = plugin.getUserManager().getOrFetch(player);
                JobData jobData = user.getData(job);

                if (!jobData.isReadyForNextOrder()) return;

                this.plugin.getJobManager().createSpecialOrder(player, job, false);
                this.runNextTick(player::closeInventory);
            }, ItemOptions.builder()
                .setVisibilityPolicy(viewer -> Config.isSpecialOrdersEnabled())
                .setDisplayModifier((viewer, item) -> {
                    Player player = viewer.getPlayer();
                    Job job = this.getLink(player);
                    JobUser user = plugin.getUserManager().getOrFetch(player);
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

                    item.replacement(replacer -> replacer
                        .replace(PLACEHOLDER_ORDER, lore -> {
                            for (String line : source) {
                                if (line.contains(GENERIC_CURRENT)) {
                                    orderData.getObjectiveMap().values().forEach(orderObjective -> {
                                        JobObjective jobObjective = job.getObjectiveById(orderObjective.getObjectiveId());
                                        if (jobObjective == null) return;

                                        Work<?, ?> workType = jobObjective.getWork();
                                        if (workType == null) return;

                                        //loreFinal.add(jobObjective.getDisplayName() + ":");
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
                                        .replace(GENERIC_TIME, TimeFormats.formatDuration(hasActiveOrder ?  orderData.getExpireDate() : jobData.getNextOrderDate(), TimeFormatType.LITERAL))
                                    );
                                }
                            }
                        }));
                }).build()))
        );

        loader.addDefaultItem(NightItem.asCustomHead("94f90c7bd60bfd0dfc31808d0484d8c2db9959f68df91fbf29423a3da62429a6")
            .setDisplayName(LIGHT_RED.wrap(BOLD.wrap("Leave Job")))
            .setLore(Lists.newList(
                DARK_GRAY.wrap("Leaving so soon?"),
                " ",
                LIGHT_GRAY.wrap("Note: all job progress will"),
                LIGHT_GRAY.wrap("be lost " + LIGHT_RED.wrap("forever") + "!"),
                " ",
                LIGHT_RED.wrap("[▶] " + LIGHT_GRAY.wrap("Click to " + LIGHT_RED.wrap("leave") + "."))
            ))
            .toMenuItem()
            .setSlots(13)
            .setPriority(10)
            .setHandler(new ItemHandler("leave_job", (viewer, event) -> {
                Player player = viewer.getPlayer();
                Job job = this.getLink(player);

                this.runNextTick(() -> this.plugin.getJobManager().openLeaveConfirmMenu(player, job));
            }))
        );
    }
}
