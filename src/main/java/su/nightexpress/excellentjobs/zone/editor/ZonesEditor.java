package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.click.ClickResult;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.Players;

import java.util.Comparator;
import java.util.stream.IntStream;

public class ZonesEditor extends EditorMenu<JobsPlugin, ZoneManager> implements AutoFilled<Zone> {

    public ZonesEditor(@NotNull JobsPlugin plugin) {
        super(plugin, Lang.EDITOR_TITLE_ZONES.getString(), MenuSize.CHEST_45);

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addExit(39);
        this.addCreation(Lang.EDITOR_ZONE_CREATE, 41, (viewer, event, zoneManager) -> {
            this.getLink(viewer).startSelection(viewer.getPlayer(), null);
            this.runNextTick(() -> viewer.getPlayer().closeInventory());
        });
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Zone> autoFill) {
        Player player = viewer.getPlayer();
        ZoneManager zoneManager = this.getLink(player);

            autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(zoneManager.getZones().stream().sorted(Comparator.comparing(Zone::getId)).toList());
        autoFill.setItemCreator(zone -> {
            ItemStack item = zone.getIcon();
            ItemReplacer.create(item).trimmed().hideFlags()
                .readLocale(Lang.EDITOR_ZONE_OBJECT)
                .replace(Placeholders.forZoneAll(zone))
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(zone -> (viewer1, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                Players.addItem(viewer1.getPlayer(), cursor);
                zone.setIcon(cursor);
                zone.save();
                event.getView().setCursor(null);
                this.runNextTick(() -> this.flush(viewer1));
                return;
            }

            if (event.isShiftClick() && event.isRightClick()) {
                zoneManager.deleteZone(zone.getId());
                this.runNextTick(() -> this.flush(viewer1));
                return;
            }

            this.runNextTick(() -> zoneManager.openEditor(viewer1.getPlayer(), zone));
        });
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @NotNull ClickResult result, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, result, event);
        if (result.isInventory()) {
            event.setCancelled(false);
        }
    }
}
