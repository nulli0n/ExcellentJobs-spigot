package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.JobUtils;
import su.nightexpress.excellentjobs.util.Report;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.StringUtil;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLACK;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLUE;

public class ZoneTimesEditor extends EditorMenu<JobsPlugin, Zone> implements AutoFilled<DayOfWeek> {

    private static final Map<Integer, String> DAY_TEXTURES = new HashMap<>() {
        {
            put(1, "bf61269735f1e446becff25f9cb3c823679719a15f7f0fbc9a03911a692bdd");
            put(2, "7d81a32d978f933deb7ea26aa326e4174697595a426eaa9f2ae5f9c2e661290");
            put(3, "ceadaded81563f1c87769d6c04689dcdb9e8ca01da35281cd8fe251728d2d");
            put(4, "6c608c2db525d6d77f7de4b961d67e53e9d7bacdaff31d4ca10fbbf92d66");
            put(5, "1144c5193435199c135bd47d166ef1b4e2d3218383df9d34e3bb20d9f8e593");
            put(6, "f61f7e38556856eae5566ef1c44a8cc64af8f3a58162b1dd8016a8778c71c");
            put(7, "6e1cf31c49a24a8f37849fc3c5463ab64cc9bceb6f276a5c44aedd34fdf520");
        }
    };

    private final ZoneManager zoneManager;

    public ZoneTimesEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, BLACK.enclose("Zone Times Editor [" + BLUE.enclose(ZONE_ID) + "]"), 18);
        this.zoneManager = zoneManager;

        this.addReturn(13, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openEditor(viewer.getPlayer(), zone));
        });
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.editObject(viewer, zone -> {
            options.setTitle(zone.replacePlaceholders().apply(options.getTitle()));
        });

        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<DayOfWeek> autoFill) {
        Player player = viewer.getPlayer();
        autoFill.setSlots(IntStream.range(1, 9).toArray());
        autoFill.setItems(Stream.of(DayOfWeek.values()).toList());
        autoFill.setItemCreator(day -> {
            int index = day.ordinal() + 1;
            Zone zone = this.getObject(player);
            List<String> times = zone.getOpenTimes(day).stream()
                .map(pair -> Report.good(pair.getFirst().format(DateTimeFormatter.ISO_LOCAL_TIME) + " - " + pair.getSecond().format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .toList();

            ItemStack item = ItemUtil.getSkinHead(DAY_TEXTURES.get(index));
            ItemReplacer.create(item).trimmed().hideFlags()
                .readLocale(Lang.EDITOR_ZONE_TIME_OBJECT)
                .replace(GENERIC_NAME, StringUtil.capitalizeFully(day.name()))
                .replaceLoreExact(GENERIC_VALUE, times)
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(day -> (viewer1, event) -> {
            this.editObject(viewer1, zone -> {
                if (event.getClick() == ClickType.DROP) {
                    zone.getOpenTimes(day).clear();
                    zone.save();
                    this.runNextTick(() -> this.open(viewer1));
                    return;
                }

                if (event.isRightClick()) {
                    var timesList = zone.getOpenTimes(day);
                    if (timesList.isEmpty()) return;

                    timesList.remove(timesList.size() - 1);
                    zone.save();
                    this.runNextTick(() -> this.open(viewer1));
                    return;
                }

                if (event.isLeftClick()) {
                    this.handleInput(viewer1, Lang.EDITOR_GENERIC_ENTER_TIMES, (dialog, handler) -> {
                        var times = JobUtils.parseTimes(handler.getTextRaw());
                        if (times != null) {
                            zone.getOpenTimes(day).add(times);
                            zone.save();
                        }
                        return true;
                    });
                }
            });
        });
    }
}
