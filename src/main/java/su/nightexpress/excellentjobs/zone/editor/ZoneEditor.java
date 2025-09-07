package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.ui.dialog.Dialog;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;

@SuppressWarnings("UnstableApiUsage")
public class ZoneEditor extends LinkedMenu<JobsPlugin, Zone> {

    private static final String SKIN_LETTER       = "8ff88b122ff92513c6a27b7f67cb3fea97439e078821d6861b74332a2396";
    private static final String SKIN_BOUNDS       = "ad62c8d3f5a7dfcca6e7bfd02df0ea358e51621453e05a21cd8a1ea9816ec6b8";
    private static final String SKIN_BOOKS        = "67b0f5a72193e770086d0a4a86ed1225199fa7b82716dab362cfa99065649c07";
    private static final String SKIN_JOB          = "5a140a8dfb0f1f5ab979241e58c253a94d65e725f180ab52396e1d8e4c81ce37";
    private static final String SKIN_TOGGLE_RED   = "455665e311a71239e75fc4c1822005a692d0fe699c17fff60b6f4b1a0543475b";
    private static final String SKIN_TOGGLE_GREEN = "86f1c9ecbcd49842dcbe9a3d85abba6479ea46bdd424dbd9d0ef54c28bf502d7";
    private static final String SKIN_DISABLED     = "7df0ee9d25b41cb645dd2fe5c7746cbb8a1d37fd3e01e25e013242f9d03a30d6";
    private static final String SKIN_ORE          = "632ccf7814539a61f8bfc15bcf111a39ad8ae163c36e44b6379415556475d72a";
    private static final String SKIN_GOLD         = "c8c758ab08cbe59730972c9c2941f95475804858ce4b0a2b49f5b5c5027d66c";
    private static final String SKIN_WORKBENCH    = "e3c81adc6c06d95c65b6c1089755a04d7ebc414f51ba66d14d0c4c1d71520df6";

    private final ZoneManager manager;

    public ZoneEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin, MenuType.GENERIC_9X6, Lang.EDITOR_TITLE_ZONE_SETTINGS.text());
        this.manager = manager;

        this.addItem(MenuItem.buildReturn(this, 49, (viewer, event) -> {
            this.runNextTick(() -> this.manager.openEditor(viewer.getPlayer()));
        }));

        this.addItem(ItemUtil.getSkinHead(SKIN_BOUNDS), Lang.EDITOR_ZONE_SELECTION, 4, (viewer, event, zone) -> {
            this.manager.startSelection(viewer.getPlayer(), zone);
            this.runNextTick(() -> viewer.getPlayer().closeInventory());
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_LETTER), Lang.EDITOR_ZONE_NAME, 20, (viewer, event, zone) -> {
            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_NAME.text(), input -> {
                zone.setName(input.getTextRaw());
                zone.save();
                return true;
            }));
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_BOOKS), Lang.EDITOR_ZONE_DESCRIPTION, 21, (viewer, event, zone) -> {
            if (event.isRightClick()) {
                zone.getDescription().clear();
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.handleInput(Dialog.builder(viewer, Lang.EDITOR_ZONE_ENTER_DESCRIPTION.text(), input -> {
                    zone.getDescription().add(input.getTextRaw());
                    zone.save();
                    return true;
                }));
            }
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_JOB), Lang.EDITOR_ZONE_LINKED_JOBS, 22, (viewer, event, zone) -> {
            if (event.isRightClick()) {
                zone.getLinkedJobs().clear();
                zone.save();
                return;
            }

            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_ZONE_ENTER_JOB_ID.text(), input -> {
                Job job = this.plugin.getJobManager().getJobById(input.getTextRaw());
                if (job != null) {
                    zone.getLinkedJobs().add(job.getId());
                    zone.save();
                }
                return true;
            }).setSuggestions(plugin.getJobManager().getJobIds(), true));
        });

        this.addItem(Material.PLAYER_HEAD, Lang.EDITOR_ZONE_JOB_LEVEL, 23, (viewer, event, zone) -> {
            if (event.getClick() == ClickType.DROP) {
                zone.setMinJobLevel(-1);
                zone.setMaxJobLevel(-1);
                this.save(viewer);
                return;
            }

            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_MIN_MAX.text(), input -> {
                String[] split = input.getTextRaw().split(" ");
                int min = NumberUtil.getAnyInteger(split[0], -1);
                int max = split.length >= 2 ? NumberUtil.getAnyInteger(split[1], -1) : min;

                zone.setMinJobLevel(min);
                zone.setMaxJobLevel(max);
                zone.save();
                return true;
            }));
        }, ItemOptions.builder().setDisplayModifier((viewer, item) -> {
            item.setSkinURL(this.getLink(viewer).isLevelRequired() ? SKIN_TOGGLE_GREEN : SKIN_DISABLED);
        }).build());

        this.addItem(Material.PLAYER_HEAD, Lang.EDITOR_ZONE_PERMISSION_REQUIRED, 24, (viewer, event, zone) -> {
            zone.setPermissionRequired(!zone.isPermissionRequired());
            this.save(viewer);
        }, ItemOptions.builder().setDisplayModifier((viewer, item) -> {
            item.setSkinURL(this.getLink(viewer).isPermissionRequired() ? SKIN_TOGGLE_RED : SKIN_DISABLED);
        }).build());

        this.addItem(Material.CLOCK, Lang.EDITOR_ZONE_HOURS, 12, (viewer, event, zone) -> {
            if (event.isRightClick()) {
                zone.setHoursEnabled(!zone.isHoursEnabled());
                this.save(viewer);
                return;
            }

            this.runNextTick(() -> this.manager.openTimesEditor(viewer.getPlayer(), zone));
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_GOLD), Lang.EDITOR_ZONE_MODIFIERS, 13, (viewer, event, zone) -> {
            this.runNextTick(() -> this.manager.openModifiersEditor(viewer.getPlayer(), zone));
        });

        this.addItem(Material.DIAMOND_SWORD, Lang.EDITOR_ZONE_PVP_ALLOWED, 14, (viewer, event, zone) -> {
            zone.setPvPAllowed(!zone.isPvPAllowed());
            this.save(viewer);
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_ORE), Lang.EDITOR_ZONE_BLOCK_LISTS, 30, (viewer, event, zone) -> {
            this.runNextTick(() -> this.manager.openBlocksEditor(viewer.getPlayer(), zone));
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_WORKBENCH), Lang.EDITOR_ZONE_DISABLED_BLOCKS, 32, (viewer, event, zone) -> {
            if (event.isLeftClick()) {
                this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_MATERIAL.text(), input -> {
                    Material material = BukkitThing.getMaterial(input.getTextRaw());
                    if (material != null && material.isBlock()) {
                        zone.getDisabledInteractions().add(material);
                        zone.save();
                    }
                    return true;
                }));
                return;
            }

            if (event.getClick() == ClickType.DROP) {
                zone.getDisabledInteractions().clear();
                this.save(viewer);
            }
        });
    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        item.replacement(replacer -> replacer.replace(this.getLink(viewer).replaceAllPlaceholders()));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
