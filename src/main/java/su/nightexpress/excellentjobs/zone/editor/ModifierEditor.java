package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Pair;

import java.util.function.BiConsumer;

public class ModifierEditor extends EditorMenu<JobsPlugin, Pair<Zone, Modifier>> {

    private final ZoneManager zoneManager;

    public ModifierEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, Lang.EDITOR_TITLE_ZONE_MODIFIER_SETTINGS.getString(), MenuSize.CHEST_36);
        this.zoneManager = zoneManager;

        this.addReturn(31, (viewer, event, mod) -> {
            this.runNextTick(() -> this.zoneManager.openModifiersEditor(viewer.getPlayer(), this.getLink(viewer).getFirst()));
        });

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
            Modifier modifier = pair.getSecond();
            modifier.setAction(Lists.next(modifier.getAction()));
            pair.getFirst().save();
            this.runNextTick(() -> this.open(viewer));
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            Modifier modifier = this.getLink(viewer).getSecond();
            ItemReplacer.replace(item, modifier.replacePlaceholders());
        }));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void modifyValue(@NotNull MenuViewer viewer, @NotNull Pair<Zone, Modifier> pair, @NotNull BiConsumer<Modifier, Double> consumer) {
        this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, (dialog, input) -> {
            consumer.accept(pair.getSecond(), input.asAnyDouble(0D));
            pair.getFirst().save();
            return true;
        });
    }
}
