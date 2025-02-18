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
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.Comparator;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class BlocksEditor extends LinkedMenu<JobsPlugin, Zone> implements Filled<BlockList> {

    private final ZoneManager manager;

    public BlocksEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin, MenuType.GENERIC_9X5, Lang.EDITOR_TITLE_ZONE_BLOCK_LIST.getString());
        this.manager = manager;

        this.addItem(MenuItem.buildNextPage(this, 44));
        this.addItem(MenuItem.buildPreviousPage(this, 36));
        this.addItem(MenuItem.buildReturn(this, 39, (viewer, event) -> {
            this.runNextTick(() -> this.manager.openEditor(viewer.getPlayer(), this.getLink(viewer)));
        }));

        this.addItem(Material.ANVIL, Lang.EDITOR_ZONE_BLOCK_LIST_CREATE, 41, (viewer, event, zone) -> {
            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_ID, input -> {
                String id = input.getTextRaw();
                if (zone.getBlockList(id) == null) {
                    BlockList blockList = new BlockList(id, Lists.newSet(), Material.STONE, 60, true);
                    zone.getBlockListMap().put(blockList.getId(), blockList);
                }
                return true;
            }));
        });
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public MenuFiller<BlockList> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Zone zone = this.getLink(player);

        return MenuFiller.builder(this)
            .setSlots(IntStream.range(0, 36).toArray())
            .setItems(zone.getBlockListMap().values().stream().sorted(Comparator.comparing(BlockList::getId)).toList())
            .setItemCreator(blockList -> {
                Material material = blockList.getMaterials().isEmpty() ? blockList.getFallbackMaterial() : Rnd.get(blockList.getMaterials());
                if (!material.isItem()) {
                    if (material == Material.CARROTS) material = Material.CARROT;
                    else if (material == Material.POTATOES) material = Material.POTATO;
                    else if (material == Material.BEETROOTS) material = Material.BEETROOT;
                    else material = Material.BARRIER;
                }

                return NightItem.fromType(material)
                    .setHideComponents(true)
                    .localized(Lang.EDITOR_ZONE_BLOCK_LIST_OBJECT)
                    .replacement(replacer -> replacer
                        .replace(blockList.replacePlaceholders())
                    );
            })
            .setItemClick(blockList -> (viewer1, event) -> {
                if (event.isShiftClick() && event.isRightClick()) {
                    zone.getBlockListMap().remove(blockList.getId());
                    zone.save();
                    this.runNextTick(() -> this.flush(viewer1));
                    return;
                }

                this.runNextTick(() -> this.manager.openBlockListEditor(viewer1.getPlayer(), zone, blockList));
            })
            .build();
    }
}
