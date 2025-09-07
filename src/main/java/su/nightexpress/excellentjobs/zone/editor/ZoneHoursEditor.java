package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Hours;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.ui.dialog.Dialog;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.time.DayOfWeek;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static su.nightexpress.excellentjobs.Placeholders.*;

@SuppressWarnings("UnstableApiUsage")
public class ZoneHoursEditor extends LinkedMenu<JobsPlugin, Zone> implements Filled<DayOfWeek> {

    private static final String[] TEXTURES_OFF = {
        "bf61269735f1e446becff25f9cb3c823679719a15f7f0fbc9a03911a692bdd",
        "7d81a32d978f933deb7ea26aa326e4174697595a426eaa9f2ae5f9c2e661290",
        "ceadaded81563f1c87769d6c04689dcdb9e8ca01da35281cd8fe251728d2d",
        "6c608c2db525d6d77f7de4b961d67e53e9d7bacdaff31d4ca10fbbf92d66",
        "1144c5193435199c135bd47d166ef1b4e2d3218383df9d34e3bb20d9f8e593",
        "f61f7e38556856eae5566ef1c44a8cc64af8f3a58162b1dd8016a8778c71c",
        "6e1cf31c49a24a8f37849fc3c5463ab64cc9bceb6f276a5c44aedd34fdf520"
    };

    private static final String[] TEXTURES_ON = {
        "6d65ce83f1aa5b6e84f9b233595140d5b6beceb62b6d0c67d1a1d83625ffd",
        "dd54d1f8fbf91b1e7f55f1bdb25e2e33baf6f46ad8afbe08ffe757d3075e3",
        "21e4ea59b54cc99416bc9f624548ddac2a38eea6a2dbf6e4ccd83cec7ac969",
        "8b527b24b5d2bcdc756f995d34eae579d7414b0a5f26c4ffa4a558ecaf6b7",
        "84c8c3710da2559a291adc39629e9ccea31ca9d3d3586bfea6e6e06124b3c",
        "e2113c604a22b224fbd3597f904a7f9227a7c1ae53439c96994bfa23b52eb",
        "24bde79f84fc5f3f1fbc5bc01071066bd20cd263a1654d64d60d84248ba9cd"
    };

    private final ZoneManager manager;

    public ZoneHoursEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin, MenuType.GENERIC_9X4, Lang.EDITOR_TITLE_ZONE_HOURS.text());
        this.manager = manager;

        this.addItem(MenuItem.buildReturn(this, 31, (viewer, event) -> {
            this.runNextTick(() -> this.manager.openEditor(viewer.getPlayer(), this.getLink(viewer)));
        }));
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
    public MenuFiller<DayOfWeek> createFiller(@NotNull MenuViewer viewer) {
        var autoFill = MenuFiller.builder(this);

        Player player = viewer.getPlayer();
        Zone zone = this.getLink(player);

        autoFill.setSlots(IntStream.range(10, 18).toArray());
        autoFill.setItems(Stream.of(DayOfWeek.values()).toList());
        autoFill.setItemCreator(day -> {
            Hours hours = zone.getHours(day);
            var array = hours == null ? TEXTURES_OFF : TEXTURES_ON;

            return NightItem.asCustomHead(array[day.ordinal()])
                .setHideComponents(true)
                .localized(Lang.EDITOR_ZONE_TIME_OBJECT)
                .replacement(replacer -> replacer
                    .replace(GENERIC_NAME, StringUtil.capitalizeUnderscored(day.name()))
                    .replace(GENERIC_VALUE, CoreLang.goodEntry(hours == null ? CoreLang.OTHER_NONE.text() : hours.format()))
                );
        });
        autoFill.setItemClick(day -> (viewer1, event) -> {
            if (event.isRightClick()) {
                zone.getHoursByDayMap().remove(day);
                zone.save();
                this.runNextTick(() -> this.flush(viewer1));
                return;
            }

            if (event.isLeftClick()) {
                this.handleInput(Dialog.builder(viewer1, Lang.EDITOR_GENERIC_ENTER_TIMES.text(), input -> {
                    Hours hours = Hours.parse(input.getTextRaw());
                    if (hours != null) {
                        zone.getHoursByDayMap().put(day, hours);
                        zone.save();
                    }
                    return true;
                }));
            }
        });

        return autoFill.build();
    }
}
