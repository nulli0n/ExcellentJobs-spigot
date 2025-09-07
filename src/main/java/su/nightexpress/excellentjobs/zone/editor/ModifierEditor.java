package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.ui.dialog.Dialog;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.function.BiConsumer;

@SuppressWarnings("UnstableApiUsage")
public class ModifierEditor extends LinkedMenu<JobsPlugin, ModifierEditor.Data> {

    public record Data(Zone zone, Modifier modifier){}

    public ModifierEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin, MenuType.GENERIC_9X4, Lang.EDITOR_TITLE_ZONE_MODIFIER_SETTINGS.text());

        this.addItem(MenuItem.buildReturn(this, 31, (viewer, event) -> {
            this.runNextTick(() -> manager.openModifiersEditor(viewer.getPlayer(), this.getLink(viewer).zone()));
        }));

        this.addItem(Material.GLOWSTONE_DUST, Lang.EDITOR_MODIFIER_BASE, 10, (viewer, event, pair) -> {
            this.modifyValue(viewer, pair, Modifier::setBase);
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, Lang.EDITOR_MODIFIER_PER_LEVEL, 12, (viewer, event, pair) -> {
            this.modifyValue(viewer, pair, Modifier::setPerLevel);
        });

        this.addItem(Material.REDSTONE, Lang.EDITOR_MODIFIER_STEP, 14, (viewer, event, pair) -> {
            this.modifyValue(viewer, pair, Modifier::setStep);
        });

        this.addItem(Material.COMPARATOR, Lang.EDITOR_MODIFIER_ACTION, 16, (viewer, event, pair) -> {
            Modifier modifier = pair.modifier();
            modifier.setAction(Lists.next(modifier.getAction()));
            pair.zone().save();
            this.runNextTick(() -> this.flush(viewer));
        });
    }

    public void open(@NotNull Player player, @NotNull Zone zone, @NotNull Modifier modifier) {
        this.open(player, new Data(zone, modifier));
    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        item.replacement(replacer -> replacer.replace(this.getLink(viewer).modifier().replacePlaceholders()));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void modifyValue(@NotNull MenuViewer viewer, @NotNull Data pair, @NotNull BiConsumer<Modifier, Double> consumer) {
        this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER.text(), input -> {
            consumer.accept(pair.modifier(), input.asDouble(0D));
            pair.zone().save();
            return true;
        }));
    }
}
