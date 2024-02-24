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
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;

import static su.nightexpress.excellentjobs.Placeholders.ZONE_ID;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLACK;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLUE;

public class ZoneSettingsEditor extends EditorMenu<JobsPlugin, Zone> {

    private final ZoneManager zoneManager;

    public ZoneSettingsEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, BLACK.enclose("Zone Settings [" + BLUE.enclose(ZONE_ID) + "]"), 36);
        this.zoneManager = zoneManager;

        this.addReturn(31, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openEditor(viewer.getPlayer()));
        });

        this.addItem(Material.GOLDEN_AXE, Lang.EDITOR_ZONE_SELECTION, 4, (viewer, event, zone) -> {
            this.zoneManager.giveWand(viewer.getPlayer(), zone);
            this.runNextTick(() -> viewer.getPlayer().closeInventory());
        });

        this.addItem(Material.NAME_TAG, Lang.EDITOR_ZONE_NAME, 10, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, (dialog, handler) -> {
                zone.setName(handler.getTextRaw());
                zone.save();
                return true;
            });
        });

        this.addItem(Material.WRITABLE_BOOK, Lang.EDITOR_ZONE_DESCRIPTION, 11, (viewer, event, zone) -> {
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

        this.addItem(Material.DIAMOND_PICKAXE, Lang.EDITOR_ZONE_LINKED_JOB, 12, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_ZONE_ENTER_JOB_ID, (dialog, handler) -> {
                Job job = this.plugin.getJobManager().getJobById(handler.getTextRaw());
                if (job != null) {
                    zone.setLinkedJob(job);
                    zone.save();
                }
                return true;
            }).setSuggestions(plugin.getJobManager().getJobIds(), true);
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, Lang.EDITOR_ZONE_JOB_LEVEL, 13, (viewer, event, zone) -> {
            if (event.getClick() == ClickType.DROP) {
                zone.setMinJobLevel(-1);
                zone.setMaxJobLevel(-1);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, (dialog, handler) -> {
                int value = handler.asInt();
                if (event.isLeftClick()) zone.setMinJobLevel(value);
                else zone.setMaxJobLevel(value);
                zone.save();
                return true;
            });
        }).getOptions().setDisplayModifier((viewer, item) -> {
            Zone zone = this.getObject(viewer);
            item.setType(zone.isLevelRequired() ? Material.EXPERIENCE_BOTTLE : Material.GLASS_BOTTLE);
        });

        this.addItem(Material.REDSTONE, Lang.EDITOR_ZONE_PERMISSION_REQUIRED, 14, (viewer, event, zone) -> {
            zone.setPermissionRequired(!zone.isPermissionRequired());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            Zone zone = this.getObject(viewer);
            item.setType(zone.isPermissionRequired() ? Material.REDSTONE : Material.GUNPOWDER);
        });

        this.addItem(Material.CLOCK, Lang.EDITOR_ZONE_OPEN_TIMES, 15, (viewer, event, zone) -> {
            if (event.getClick() == ClickType.DROP) {
                zone.getOpenTimes().clear();
                this.save(viewer);
                return;
            }

            this.runNextTick(() -> this.zoneManager.openTimesEditor(viewer.getPlayer(), zone));
        });

        this.addItem(Material.GOLD_NUGGET, Lang.EDITOR_ZONE_MODIFIERS, 16, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openModifiersEditor(viewer.getPlayer(), zone));
        });

        this.addItem(Material.DIAMOND_SWORD, Lang.EDITOR_ZONE_PVP_ALLOWED, 17, (viewer, event, zone) -> {
            zone.setPvPAllowed(!zone.isPvPAllowed());
            this.save(viewer);
        });

        this.addItem(Material.DIAMOND_ORE, Lang.EDITOR_ZONE_BLOCK_LISTS, 22, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openBlocksEditor(viewer.getPlayer(), zone));
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, Placeholders.forZoneAll(this.getObject(viewer)).replacer());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.editObject(viewer, Zone::save);
        this.runNextTick(() -> this.open(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.editObject(viewer, zone -> {
            options.setTitle(zone.replacePlaceholders().apply(options.getTitle()));
        });
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
