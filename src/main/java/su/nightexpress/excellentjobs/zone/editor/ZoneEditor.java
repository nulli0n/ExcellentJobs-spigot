package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.wrapper.UniInt;

public class ZoneEditor extends EditorMenu<JobsPlugin, Zone> {

    private static final String SKIN_LETTER       = "8ff88b122ff92513c6a27b7f67cb3fea97439e078821d6861b74332a2396";
    private static final String SKIN_BOUNDS       = "ad62c8d3f5a7dfcca6e7bfd02df0ea358e51621453e05a21cd8a1ea9816ec6b8";
    private static final String SKIN_BOOKS        = "67b0f5a72193e770086d0a4a86ed1225199fa7b82716dab362cfa99065649c07";
    private static final String SKIN_JOB          = "5a140a8dfb0f1f5ab979241e58c253a94d65e725f180ab52396e1d8e4c81ce37";
    private static final String SKIN_TOGGLE_RED   = "455665e311a71239e75fc4c1822005a692d0fe699c17fff60b6f4b1a0543475b";
    private static final String SKIN_TOGGLE_GREEN = "86f1c9ecbcd49842dcbe9a3d85abba6479ea46bdd424dbd9d0ef54c28bf502d7";
    private static final String SKIN_DISABLED = "7df0ee9d25b41cb645dd2fe5c7746cbb8a1d37fd3e01e25e013242f9d03a30d6";
    private static final String SKIN_ORE      = "632ccf7814539a61f8bfc15bcf111a39ad8ae163c36e44b6379415556475d72a";
    private static final String SKIN_GOLD     = "c8c758ab08cbe59730972c9c2941f95475804858ce4b0a2b49f5b5c5027d66c";
    private static final String SKIN_WORKBENCH    = "e3c81adc6c06d95c65b6c1089755a04d7ebc414f51ba66d14d0c4c1d71520df6";

    private final ZoneManager zoneManager;

    public ZoneEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, Lang.EDITOR_TITLE_ZONE_SETTINGS.getString(), MenuSize.CHEST_54);
        this.zoneManager = zoneManager;

        this.addReturn(49, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openEditor(viewer.getPlayer()));
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_BOUNDS), Lang.EDITOR_ZONE_SELECTION, 4, (viewer, event, zone) -> {
            this.zoneManager.startSelection(viewer.getPlayer(), zone);
            this.runNextTick(() -> viewer.getPlayer().closeInventory());
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_LETTER), Lang.EDITOR_ZONE_NAME, 20, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, (dialog, handler) -> {
                zone.setName(handler.getTextRaw());
                zone.save();
                return true;
            });
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_BOOKS), Lang.EDITOR_ZONE_DESCRIPTION, 21, (viewer, event, zone) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_ZONE_ENTER_DESCRIPTION, (dialog, handler) -> {
                    zone.getDescription().add(handler.getTextRaw());
                    zone.save();
                    return true;
                });
                return;
            }
            if (event.isRightClick()) {
                if (zone.getDescription().isEmpty()) return;

                zone.getDescription().remove(zone.getDescription().size() - 1);
                this.save(viewer);
                return;
            }
            if (event.getClick() == ClickType.DROP) {
                zone.getDescription().clear();
                this.save(viewer);
            }
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_JOB), Lang.EDITOR_ZONE_LINKED_JOB, 22, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_ZONE_ENTER_JOB_ID, (dialog, handler) -> {
                Job job = this.plugin.getJobManager().getJobById(handler.getTextRaw());
                if (job != null) {
                    zone.setLinkedJob(job);
                    zone.save();
                }
                return true;
            }).setSuggestions(plugin.getJobManager().getJobIds(), true);
        });

        this.addItem(Material.PLAYER_HEAD, Lang.EDITOR_ZONE_JOB_LEVEL, 23, (viewer, event, zone) -> {
            if (event.getClick() == ClickType.DROP) {
                zone.setMinJobLevel(-1);
                zone.setMaxJobLevel(-1);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_MIN_MAX, (dialog, input) -> {
                UniInt values = input.asUniInt();
                zone.setMinJobLevel(values.getMinValue());
                zone.setMaxJobLevel(values.getMaxValue());
                zone.save();
                return true;
            });
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemUtil.setHeadSkin(item, this.getLink(viewer).isLevelRequired() ? SKIN_TOGGLE_GREEN : SKIN_DISABLED);
        });

        this.addItem(Material.PLAYER_HEAD, Lang.EDITOR_ZONE_PERMISSION_REQUIRED, 24, (viewer, event, zone) -> {
            zone.setPermissionRequired(!zone.isPermissionRequired());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemUtil.setHeadSkin(item, this.getLink(viewer).isPermissionRequired() ? SKIN_TOGGLE_RED : SKIN_DISABLED);
            //item.setType(this.getLink(viewer).isPermissionRequired() ? Material.REDSTONE : Material.GUNPOWDER);
        });

        this.addItem(Material.CLOCK, Lang.EDITOR_ZONE_OPEN_TIMES, 12, (viewer, event, zone) -> {
            if (event.getClick() == ClickType.DROP) {
                zone.getOpenTimes().clear();
                this.save(viewer);
                return;
            }

            this.runNextTick(() -> this.zoneManager.openTimesEditor(viewer.getPlayer(), zone));
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_GOLD), Lang.EDITOR_ZONE_MODIFIERS, 13, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openModifiersEditor(viewer.getPlayer(), zone));
        });

        this.addItem(Material.DIAMOND_SWORD, Lang.EDITOR_ZONE_PVP_ALLOWED, 14, (viewer, event, zone) -> {
            zone.setPvPAllowed(!zone.isPvPAllowed());
            this.save(viewer);
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_ORE), Lang.EDITOR_ZONE_BLOCK_LISTS, 30, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openBlocksEditor(viewer.getPlayer(), zone));
        });

        this.addItem(ItemUtil.getSkinHead(SKIN_WORKBENCH), Lang.EDITOR_ZONE_DISABLED_BLOCKS, 32, (viewer, event, zone) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_MATERIAL, (dialog, input) -> {
                    Material material = BukkitThing.getMaterial(input.getTextRaw());
                    if (material != null && material.isBlock()) {
                        zone.getDisabledInteractions().add(material);
                        zone.save();
                    }
                    return true;
                });
                return;
            }

            if (event.getClick() == ClickType.DROP) {
                zone.getDisabledInteractions().clear();
                this.save(viewer);
            }
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, Placeholders.forZoneAll(this.getLink(viewer)).replacer());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
