package su.nightexpress.excellentjobs.stats.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.stats.impl.DayStats;
import su.nightexpress.excellentjobs.stats.impl.JobStats;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class StatsMenu extends LinkedMenu<JobsPlugin, Job> implements ConfigBased {

    private List<StatsEntry> entries;

    private String       entryName;
    private List<String> entryLore;
    private String       currencyEntry;
    private String       objectiveEntry;
    private String       nothingEntry;

    public StatsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X4, BLACK.wrap("[" + JOB_NAME + "] Stats"));
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).replacePlaceholders().apply(super.getTitle(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.displayStats(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void displayStats(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobStats stats = user.getStats(job);

        this.entries.forEach(entry -> {
            int minDays = entry.minDays();
            int maxDays = entry.maxDays();

            DayStats dayStats = minDays < 0 && maxDays < 0 ? stats.getAllTimeStats() : stats.getStatsForDays(minDays, maxDays);

            List<String> currencyAmounts = new ArrayList<>();
            List<String> objectiveAmounts = new ArrayList<>();

            for (Currency currency : EconomyBridge.getCurrencies()) {
                double amount = dayStats.getCurrency(currency);
                if (amount == 0D) continue;

                currencyAmounts.add(currency.replacePlaceholders().apply(this.currencyEntry.replace(GENERIC_AMOUNT, currency.format(amount))));
            }

            if (currencyAmounts.isEmpty()) {
                currencyAmounts.add(this.nothingEntry);
            }

            if (objectiveAmounts.isEmpty()) {
                objectiveAmounts.add(this.nothingEntry);
            }

            NightItem item = entry.item().copy().hideAllComponents()
                .setDisplayName(this.entryName)
                .setLore(this.entryLore)
                .replacement(replacer -> replacer
                    .replace(GENERIC_NAME, entry.name())
                    .replace(GENERIC_CURRENCY, currencyAmounts)
                    .replace(GENERIC_OBJECTIVES, objectiveAmounts)
                );

            viewer.addItem(item.toMenuItem().setSlots(entry.slots()).setPriority(Integer.MAX_VALUE).build());
        });
    }

    private void handleReturn(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        this.runNextTick(() -> plugin.getJobManager().openLevelsMenu(viewer.getPlayer(), this.getLink(viewer)));
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.entries = new ArrayList<>();

        if (!config.contains("Stats.Entries")) {
            StatsEntry entryToday = new StatsEntry("Today", 0, 0, new int[]{10}, NightItem.fromType(Material.PAPER));
            StatsEntry entryWeek = new StatsEntry("Week", 7, -1, new int[]{12}, NightItem.fromType(Material.WRITABLE_BOOK));
            StatsEntry entryMonth = new StatsEntry("Month" , 30, -1, new int[]{14}, NightItem.fromType(Material.COMPASS));
            StatsEntry entryAll = new StatsEntry("All Time", -1, -1, new int[]{16}, NightItem.fromType(Material.RECOVERY_COMPASS));

            config.set("Stats.Entries.today", entryToday);
            config.set("Stats.Entries.week", entryWeek);
            config.set("Stats.Entries.month", entryMonth);
            config.set("Stats.Entries.alltime", entryAll);
        }

        config.getSection("Stats.Entries").forEach(sId -> {
            this.entries.add(StatsEntry.read(config, "Stats.Entries." + sId));
        });

        this.entryName = ConfigValue.create("Stats.Entry.Name",
            SOFT_AQUA.wrap(BOLD.wrap("Stats: ")) + GRAY.wrap(GENERIC_NAME)
        ).read(config);

        this.entryLore = ConfigValue.create("Stats.Entry.Lore", Lists.newList(
            "",
            GRAY.wrap("[" + GREEN.wrap("$") + "]") + " " + GREEN.wrap("Earnings:"),
            GENERIC_CURRENCY,
            "",
            GRAY.wrap("[" + GOLD.wrap("⛏") + "]") + " " + GOLD.wrap("Objectives:"),
            GENERIC_OBJECTIVES
        )).read(config);

        this.currencyEntry = ConfigValue.create("Stats.Currency.Entry",
            GREEN.wrap("● " + GRAY.wrap(CURRENCY_NAME + ": ") + GENERIC_AMOUNT)
        ).read(config);

        this.objectiveEntry = ConfigValue.create("Stats.Objective.Entry",
            GOLD.wrap("● " + GRAY.wrap(OBJECTIVE_NAME + ": ") + GENERIC_AMOUNT)
        ).read(config);

        this.nothingEntry = ConfigValue.create("Stats.Nothing",
            GRAY.wrap(RED.wrap("✘") + " No data collected.")
        ).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.GRAY_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(IntStream.range(0, 27).toArray())
        );

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(IntStream.range(27, 36).toArray())
        );

        loader.addDefaultItem(MenuItem.buildReturn(this, 31, this::handleReturn));
    }

    private record StatsEntry(@NotNull String name, int minDays, int maxDays, int[] slots, @NotNull NightItem item) implements Writeable {

        @NotNull
        public static StatsEntry read(@NotNull FileConfig config, @NotNull String path) {
            int minDays = ConfigValue.create(path + ".MinDays", -1).read(config);
            int maxDays = ConfigValue.create(path + ".MaxDays", -1).read(config);
            int[] slots = ConfigValue.create(path + ".Slot", new int[0]).read(config);
            String name = ConfigValue.create(path + ".Name", minDays + " Days").read(config);
            NightItem item = ConfigValue.create(path + ".Item", NightItem.fromType(Material.AIR)).read(config);

            return new StatsEntry(name, minDays, maxDays, slots, item);
        }

        @Override
        public void write(@NotNull FileConfig config, @NotNull String path) {
            config.set(path + ".MinDays", this.minDays);
            config.set(path + ".MaxDays", this.maxDays);
            config.setIntArray(path + ".Slot", this.slots);
            config.set(path + ".Name", this.name);
            config.set(path + ".Item", this.item);
        }
    }
}
