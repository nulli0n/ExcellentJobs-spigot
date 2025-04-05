package su.nightexpress.excellentjobs.job.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
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

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.excellentjobs.Placeholders.*;

public class PreviewMenu extends ConfigMenu<JobsPlugin> implements Linked<Job> {

    public static final String FILE_NAME = "job_preview.yml";

    private final ViewLink<Job> link;

    private final ItemHandler returnHandler;
    private final ItemHandler joinPrimaryHandler;
    private final ItemHandler joinSecondaryHandler;
    private final ItemHandler objectivesHandler;

    public PreviewMenu(@NotNull JobsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getJobManager().openJobsMenu(viewer.getPlayer()));
        }));

        this.addHandler(this.objectivesHandler = new ItemHandler("objectives", (viewer, event) -> {
            Player player = viewer.getPlayer();
            Job job = this.getLink().get(player);

            this.runNextTick(() -> this.plugin.getJobManager().openObjectivesMenu(player, job));
        }));

        this.addHandler(this.joinPrimaryHandler = new ItemHandler("join_primary", (viewer, event) -> {
            this.onClickJoin(viewer, JobState.PRIMARY);
        }));

        this.addHandler(this.joinSecondaryHandler = new ItemHandler("join_secondary", (viewer, event) -> {
            this.onClickJoin(viewer, JobState.SECONDARY);
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            ItemHandler handler = menuItem.getHandler();
            JobState state;
            if (handler == this.joinPrimaryHandler) state = JobState.PRIMARY;
            else if (handler == this.joinSecondaryHandler) state = JobState.SECONDARY;
            else return;

            menuItem.getOptions().addDisplayModifier((viewer, itemStack) -> {
                Player player = viewer.getPlayer();
                //Job job = this.getLink().get(player);
                JobUser user = plugin.getUserManager().getOrFetch(player);
                int limit = JobManager.getJobsLimit(player, state);

                ItemReplacer.create(itemStack).readMeta()
                    .replace(GENERIC_AMOUNT, String.valueOf(user.countJobs(state)))
                    .replace(GENERIC_MAX, limit < 0 ? Lang.OTHER_INFINITY.getString() : String.valueOf(limit))
                    .writeMeta();
            });
        });
    }

    private void onClickJoin(@NotNull MenuViewer viewer, @NotNull JobState state) {
        Player player = viewer.getPlayer();
        Job job = this.getLink().get(player);

        JobUser user = this.plugin.getUserManager().getOrFetch(player);
        if (user.getData(job).getState() != JobState.INACTIVE) {
            this.runNextTick(player::closeInventory);
            return;
        }

        this.plugin.getJobManager().joinJob(player, job, state, false);
        this.runNextTick(player::closeInventory);
    }

    @NotNull
    @Override
    public ViewLink<Job> getLink() {
        return this.link;
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        options.editTitle(this.getLink().get(viewer).replacePlaceholders());
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Job Preview"), MenuSize.CHEST_36);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack primaryItem = ItemUtil.getSkinHead("71101c498aaac93e76b8f16d29cbfa473ed24d9c6c75563c27eedd49c963e98b");
        ItemUtil.editMeta(primaryItem, meta -> {
            meta.setDisplayName(GREEN.enclose(BOLD.enclose("Get as Primary")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("You have " + GREEN.enclose(GENERIC_AMOUNT) + "/" + GREEN.enclose(GENERIC_MAX) + " primary jobs."),
                "",
                GREEN.enclose("[▶] " + LIGHT_GRAY.enclose("Click to " + GREEN.enclose("get job") + "."))
            ));
        });
        list.add(new MenuItem(primaryItem).setPriority(10).setSlots(11).setHandler(this.joinPrimaryHandler));

        ItemStack secondItem = ItemUtil.getSkinHead("ed78cc391affb80b2b35eb7364ff762d38424c07e724b99396dee921fbbc9cf");
        ItemUtil.editMeta(secondItem, meta -> {
            meta.setDisplayName(RED.enclose(BOLD.enclose("Get as Secondary")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("You have " + RED.enclose(GENERIC_AMOUNT) + "/" + RED.enclose(GENERIC_MAX) + " secondary jobs."),
                "",
                RED.enclose("[▶] " + LIGHT_GRAY.enclose("Click to " + RED.enclose("get job") + "."))
            ));
        });
        list.add(new MenuItem(secondItem).setPriority(10).setSlots(13).setHandler(this.joinSecondaryHandler));

        ItemStack objectItem = ItemUtil.getSkinHead("5a140a8dfb0f1f5ab979241e58c253a94d65e725f180ab52396e1d8e4c81ce37");
        ItemUtil.editMeta(objectItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Objectives")));
            meta.setLore(Lists.newList(LIGHT_GRAY.enclose("Click to browse job objectives!")));
        });
        list.add(new MenuItem(objectItem).setPriority(10).setSlots(15).setHandler(this.objectivesHandler));

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getLocalizedName());
        });
        list.add(new MenuItem(back).setSlots(31).setPriority(10).setHandler(this.returnHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {

    }
}
