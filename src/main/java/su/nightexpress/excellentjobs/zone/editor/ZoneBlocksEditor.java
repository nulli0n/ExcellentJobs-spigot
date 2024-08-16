package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.Comparator;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.ZONE_ID;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLACK;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLUE;

public class ZoneBlocksEditor extends EditorMenu<JobsPlugin, Zone> implements AutoFilled<BlockList> {

    private final ZoneManager zoneManager;

    public ZoneBlocksEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, BLACK.enclose("Zone Blocks [" + BLUE.enclose(ZONE_ID) + "]"), 45);
        this.zoneManager = zoneManager;

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addReturn(39, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openEditor(viewer.getPlayer(), zone));
        });
        this.addCreation(Lang.EDITOR_ZONE_BLOCK_LIST_CREATE, 41, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_ID, (dialog, input) -> {
                String id = StringUtil.lowerCaseUnderscoreStrict(input.getTextRaw());
                if (zone.getBlockList(id) == null) {
                    BlockList blockList = new BlockList(id, Lists.newSet(), Material.STONE, 60, true);
                    zone.getBlockListMap().put(blockList.getId(), blockList);
                }
                return true;
            });
        });
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);

        this.editObject(viewer, zone -> {
            options.setTitle(zone.replacePlaceholders().apply(options.getTitle()));
        });
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<BlockList> autoFill) {
        Player player = viewer.getPlayer();
        Zone zone = this.getObject(player);

        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(zone.getBlockListMap().values().stream().sorted(Comparator.comparing(BlockList::getId)).toList());
        autoFill.setItemCreator(blockList -> {
            Material material = blockList.getMaterials().isEmpty() ? blockList.getFallbackMaterial() : Rnd.get(blockList.getMaterials());
            if (!material.isItem()) {
                if (material == Material.CARROTS) material = Material.CARROT;
                else if (material == Material.POTATOES) material = Material.POTATO;
                else if (material == Material.BEETROOTS) material = Material.BEETROOT;
                else material = Material.BARRIER;
            }

            ItemStack item = new ItemStack(material);
            ItemReplacer.create(item).hideFlags().trimmed()
                .readLocale(Lang.EDITOR_ZONE_BLOCK_LIST_OBJECT)
                .replace(blockList.replacePlaceholders())
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(blockList -> (viewer1, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                zone.getBlockListMap().remove(blockList.getId());
                zone.save();
                this.runNextTick(() -> this.open(viewer1));
                return;
            }

            this.runNextTick(() -> this.zoneManager.openBlockListEditor(viewer1.getPlayer(), zone, blockList));
        });
    }
}
