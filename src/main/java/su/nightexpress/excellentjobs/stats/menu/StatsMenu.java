package su.nightexpress.excellentjobs.stats.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobObjective;
import su.nightexpress.excellentjobs.stats.impl.DayStats;
import su.nightexpress.excellentjobs.stats.impl.JobStats;
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
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class StatsMenu extends ConfigMenu<JobsPlugin> implements Linked<Job> {

    private static final String FILE_NAME = "job_stats.yml";

    private static final String CURRENCIES = "%currency%";
    private static final String OBJECTIVES = "%objectives%";

    private final ViewLink<Job> link;
    private final ItemHandler returnHandler;

    private List<StatsEntry> entries;

    private String       entryName;
    private List<String> entryLore;
    private String       currencyEntry;
    private String       objectiveEntry;
    private String       nothingEntry;

    public StatsMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobMenu(viewer.getPlayer(), this.getLink().get(viewer)));
        }));

        this.load();
    }

    @NotNull
    @Override
    public ViewLink<Job> getLink() {
        return link;
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.displayStats(viewer, options);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void displayStats(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        Player player = viewer.getPlayer();
        Job job = this.getLink(player);
        JobUser user = plugin.getUserManager().getUserData(player);
        JobStats stats = user.getStats(job);

        options.editTitle(job.replacePlaceholders());

        this.entries.forEach(entry -> {
            ItemStack itemStack = entry.getItemStack();
            int minDays = entry.getMinDays();
            int maxDays = entry.getMaxDays();

            DayStats dayStats = minDays < 0 && maxDays < 0 ? stats.getAllTimeStats() : stats.getStatsForDays(minDays, maxDays);

            List<String> currencyAmounts = new ArrayList<>();
            List<String> objectiveAmounts = new ArrayList<>();

            for (Currency currency : EconomyBridge.getCurrencies()) {
                double amount = dayStats.getCurrency(currency);
                if (amount == 0D) continue;

                currencyAmounts.add(currency.replacePlaceholders().apply(this.currencyEntry.replace(GENERIC_AMOUNT, currency.format(amount))));
            }
            if (currencyAmounts.isEmpty()) currencyAmounts.add(this.nothingEntry);


            for (JobObjective objective : job.getObjectives()) {
                int amount = dayStats.getObjectives(objective);
                if (amount == 0) continue;

                objectiveAmounts.add(this.objectiveEntry.replace(OBJECTIVE_NAME, objective.getDisplayName()).replace(GENERIC_AMOUNT, NumberUtil.format(amount)));
            }
            if (objectiveAmounts.isEmpty()) objectiveAmounts.add(this.nothingEntry);


            ItemReplacer.create(itemStack).hideFlags().trimmed()
                .setDisplayName(this.entryName)
                .setLore(this.entryLore)
                .replace(GENERIC_NAME, entry.getName())
                .replace(CURRENCIES, currencyAmounts)
                .replace(OBJECTIVES, objectiveAmounts)
                .writeMeta();

            this.addWeakItem(player, itemStack, entry.getSlot());
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Job Stats: " + JOB_NAME), MenuSize.CHEST_45);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getLocalizedName());
        });
        list.add(new MenuItem(back).setSlots(40).setPriority(10).setHandler(this.returnHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.entries = new ArrayList<>();

        if (!this.cfg.contains("Stats.Entries")) {
            StatsEntry entryAll = new StatsEntry("All Time", -1, -1, 4, ItemUtil.getSkinHead("a8d5cb12219a3f5e9bb68c8914c443c2de160eff00cf3e730fbaccd8db6918fe"));
            StatsEntry entryToday = new StatsEntry("Today", 0, 0, 19, ItemUtil.getSkinHead("58e4c42c7dc7bfd69902e76f8a2d71a98ff1781ae988ca79d13ad6566d414e49"));
            StatsEntry entryYesterday = new StatsEntry("Yesterday", 1, 1, 21, ItemUtil.getSkinHead("bc72aff8f6c144d799f2d1a1a0c1fefc4f047f2c46abd29268c80ecbb7b2756"));
            StatsEntry entryWeek = new StatsEntry("Week", 7, -1, 23, ItemUtil.getSkinHead("74ab9554d0c2528515a7923c95e685cea44e96664d0638d7e14dfa5ccd776ba5"));
            StatsEntry entryMonth = new StatsEntry("Month" , 30, -1, 25, ItemUtil.getSkinHead("f5be49bbdd1db35def04ad11f06deaaf45c9666c05bc02bc8bf1444e99c7e"));

            entryToday.write(cfg, "Stats.Entries.today");
            entryYesterday.write(cfg, "Stats.Entries.yestertoday");
            entryWeek.write(cfg, "Stats.Entries.week");
            entryMonth.write(cfg, "Stats.Entries.month");
            entryAll.write(cfg, "Stats.Entries.alltime");
        }

        this.cfg.getSection("Stats.Entries").forEach(sId -> {
            this.entries.add(StatsEntry.read(this.cfg, "Stats.Entries." + sId));
        });

        this.entryName = ConfigValue.create("Stats.Entry.Name",
            LIGHT_CYAN.enclose(BOLD.enclose("Stats: ")) + LIGHT_GRAY.enclose(GENERIC_NAME)
        ).read(cfg);

        this.entryLore = ConfigValue.create("Stats.Entry.Lore", Lists.newList(
            " ",
            LIGHT_GRAY.enclose("[" + GREEN.enclose("$") + "]") + " " + GREEN.enclose("Earnings:"),
            CURRENCIES,
            "",
            LIGHT_GRAY.enclose("[" + ORANGE.enclose("⛏") + "]") + " " + ORANGE.enclose("Objectives:"),
            OBJECTIVES
        )).read(cfg);

        this.currencyEntry = ConfigValue.create("Stats.Currency.Entry",
            GREEN.enclose("● " + LIGHT_GRAY.enclose(CURRENCY_NAME + ": ") + GENERIC_AMOUNT)
        ).read(cfg);

        this.objectiveEntry = ConfigValue.create("Stats.Objective.Entry",
            ORANGE.enclose("● " + LIGHT_GRAY.enclose(OBJECTIVE_NAME + ": ") + GENERIC_AMOUNT)
        ).read(cfg);

        this.nothingEntry = ConfigValue.create("Stats.Nothing",
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("✘") + " No data collected.")
        ).read(cfg);
    }

    static class StatsEntry {

        private final int minDays;
        private final int maxDays;
        private final int slot;
        private final String name;
        private final ItemStack itemStack;

        public StatsEntry(@NotNull String name, int minDays, int maxDays, int slot, ItemStack itemStack) {
            this.minDays = minDays;
            this.maxDays = maxDays;
            this.slot = slot;
            this.name = name;
            this.itemStack = itemStack;
        }

        @NotNull
        public static StatsEntry read(@NotNull FileConfig config, @NotNull String path) {
            int minDays = ConfigValue.create(path + ".MinDays", -1).read(config);
            int maxDays = ConfigValue.create(path + ".MaxDays", -1).read(config);
            int slot = ConfigValue.create(path + ".Slot", -1).read(config);
            String name = ConfigValue.create(path + ".Name", minDays + " Days").read(config);
            ItemStack item = ConfigValue.create(path + ".Item", new ItemStack(Material.AIR)).read(config);

            return new StatsEntry(name, minDays, maxDays, slot, item);
        }

        public void write(@NotNull FileConfig config, @NotNull String path) {
            config.set(path + ".MinDays", this.getMinDays());
            config.set(path + ".MaxDays", this.getMaxDays());
            config.set(path + ".Slot", this.getSlot());
            config.set(path + ".Name", this.getName());
            config.setItem(path + ".Item", this.itemStack);
        }

        @NotNull
        public String getName() {
            return name;
        }

        public int getMinDays() {
            return minDays;
        }

        public int getMaxDays() {
            return maxDays;
        }

        public int getSlot() {
            return slot;
        }

        @NotNull
        public ItemStack getItemStack() {
            return new ItemStack(this.itemStack);
        }
    }
}
