package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.click.ClickResult;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.Comparator;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class ZoneListEditor extends LinkedMenu<JobsPlugin, ZoneManager> implements Filled<Zone> {

    public ZoneListEditor(@NotNull JobsPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X5, Lang.EDITOR_TITLE_ZONES.text());

        this.addItem(MenuItem.buildNextPage(this, 44));
        this.addItem(MenuItem.buildPreviousPage(this, 36));
        this.addItem(MenuItem.buildExit(this, 39));

        this.addItem(Material.ANVIL, Lang.EDITOR_ZONE_CREATE, 41, (viewer, event, manager) -> {
            Player player = viewer.getPlayer();
            manager.startSelection(player, null);
            this.runNextTick(player::closeInventory);
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
    public MenuFiller<Zone> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        ZoneManager manager = this.getLink(player);

        return MenuFiller.builder(this)
            .setSlots(IntStream.range(0, 36).toArray())
            .setItems(manager.getZones().stream().sorted(Comparator.comparing(Zone::getId)).toList())
            .setItemCreator(zone -> {
                return zone.getIcon()
                    .localized(Lang.EDITOR_ZONE_OBJECT)
                    .replacement(replacer -> replacer
                        .replace(zone.replaceAllPlaceholders())
                    );
            })
            .setItemClick(zone -> (viewer1, event) -> {
                ItemStack cursor = event.getCursor();
                if (cursor != null && !cursor.getType().isAir()) {
                    Players.addItem(viewer1.getPlayer(), cursor);
                    zone.setIcon(NightItem.fromItemStack(cursor));
                    zone.save();
                    event.getView().setCursor(null);
                    this.runNextTick(() -> this.flush(viewer1));
                    return;
                }

                if (event.isShiftClick() && event.isRightClick()) {
                    manager.deleteZone(zone.getId());
                    this.runNextTick(() -> this.flush(viewer1));
                    return;
                }

                this.runNextTick(() -> manager.openEditor(viewer1.getPlayer(), zone));
            })
            .build();
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @NotNull ClickResult result, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, result, event);
        if (result.isInventory()) {
            event.setCancelled(false);
        }
    }
}
