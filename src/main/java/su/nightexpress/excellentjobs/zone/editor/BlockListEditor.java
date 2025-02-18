package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.ui.dialog.Dialog;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.bukkit.NightItem;

@SuppressWarnings("UnstableApiUsage")
public class BlockListEditor extends LinkedMenu<JobsPlugin, BlockListEditor.Data> {

    public record Data(Zone zone, BlockList blockList){}

    public BlockListEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, MenuType.GENERIC_9X4, Lang.EDITOR_TITLE_ZONE_BLOCK_SETTINGS.getString());

        this.addItem(MenuItem.buildReturn(this, 31, (viewer, event) -> {
            this.runNextTick(() -> zoneManager.openBlocksEditor(viewer.getPlayer(), this.getLink(viewer).zone));
        }));

        this.addItem(Material.MAP, Lang.EDITOR_ZONE_BLOCK_LIST_MATERIALS, 10, (viewer, event, data) -> {
            if (event.isRightClick()) {
                data.blockList.getMaterials().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_MATERIAL, input -> {
                Material material = BukkitThing.getMaterial(input.getTextRaw());
                if (material != null) {
                    data.blockList.getMaterials().add(material);
                    data.zone.save();
                }
                return true;
            }));
        });

        this.addItem(Material.STONE, Lang.EDITOR_ZONE_BLOCK_LIST_FALLBACK_MATERIAL, 12, (viewer, event, data) -> {
            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_MATERIAL, input -> {
                Material material = BukkitThing.getMaterial(input.getTextRaw());
                if (material != null) {
                    data.blockList.setFallbackMaterial(material);
                    data.zone.save();
                }
                return true;
            }));
        });

        this.addItem(Material.CLOCK, Lang.EDITOR_ZONE_BLOCK_LIST_RESET_TIME, 14, (viewer, event, data) -> {
            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, input -> {
                data.blockList.setResetTime(input.asInt(1));
                data.zone.save();
                return true;
            }));
        });

        this.addItem(Material.GLOWSTONE_DUST, Lang.EDITOR_ZONE_BLOCK_LIST_DROP_ITEMS, 16, (viewer, event, data) -> {
            BlockList blockList = data.blockList;
            blockList.setDropItems(!blockList.isDropItems());
            this.save(viewer);
        });
    }

    public void open(@NotNull Player player, @NotNull Zone zone, @NotNull BlockList blockList) {
        this.open(player, new Data(zone, blockList));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).zone.save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        item.replacement(replacer -> replacer.replace(this.getLink(viewer).blockList.replacePlaceholders()));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
