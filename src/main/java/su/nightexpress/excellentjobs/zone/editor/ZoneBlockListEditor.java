package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.Pair;

import static su.nightexpress.excellentjobs.Placeholders.BLOCK_LIST_ID;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLACK;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLUE;

public class ZoneBlockListEditor extends EditorMenu<JobsPlugin, Pair<Zone, BlockList>> {

    public ZoneBlockListEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, BLACK.enclose("Block List Editor [" + BLUE.enclose(BLOCK_LIST_ID) + "]"), 36);

        this.addReturn(31, (viewer, event, pair) -> {
            this.runNextTick(() -> {
                zoneManager.openBlocksEditor(viewer.getPlayer(), pair.getFirst());
            });
        });

        this.addItem(Material.MAP, Lang.EDITOR_ZONE_BLOCK_LIST_MATERIALS, 10, (viewer, event, pair) -> {
            if (event.isRightClick()) {
                pair.getSecond().getMaterials().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_MATERIAL, (dialog, input) -> {
                Material material = BukkitThing.getMaterial(input.getTextRaw());
                if (material != null) {
                    pair.getSecond().getMaterials().add(material);
                    pair.getFirst().save();
                }
                return true;
            });
        });

        this.addItem(Material.STONE, Lang.EDITOR_ZONE_BLOCK_LIST_FALLBACK_MATERIAL, 12, (viewer, event, pair) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_MATERIAL, (dialog, input) -> {
                Material material = BukkitThing.getMaterial(input.getTextRaw());
                if (material != null) {
                    pair.getSecond().setFallbackMaterial(material);
                    pair.getFirst().save();
                }
                return true;
            });
        });

        this.addItem(Material.CLOCK, Lang.EDITOR_ZONE_BLOCK_LIST_RESET_TIME, 14, (viewer, event, pair) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, (dialog, input) -> {
                pair.getSecond().setResetTime(input.asInt());
                pair.getFirst().save();
                return true;
            });
        });

        this.addItem(Material.GLOWSTONE_DUST, Lang.EDITOR_ZONE_BLOCK_LIST_DROP_ITEMS, 16, (viewer, event, pair) -> {
            BlockList blockList = pair.getSecond();
            blockList.setDropItems(!blockList.isDropItems());
            this.save(viewer);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            BlockList blockList = this.getObject(viewer).getSecond();
            ItemReplacer.replace(item, blockList.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.editObject(viewer, pair -> pair.getFirst().save());
        this.runNextTick(() -> this.open(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.editObject(viewer, pair -> {
            options.setTitle(pair.getSecond().replacePlaceholders().apply(options.getTitle()));
        });
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
