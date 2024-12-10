package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

public class ModifiersEditor extends EditorMenu<JobsPlugin, Zone> implements AutoFilled<Currency> {

    private final ZoneManager zoneManager;

    public ModifiersEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, Lang.EDITOR_TITLE_ZONE_MODIFIER_LIST.getString(), MenuSize.CHEST_45);
        this.zoneManager = zoneManager;

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addReturn(39, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openEditor(viewer.getPlayer(), zone));
        });

        this.addCreation(Lang.EDITOR_ZONE_MODIFIER_CURRENCY_CREATE, 41, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_CURRENCY, (dialog, input) -> {
                Currency currency = EconomyBridge.getCurrency(input.getTextRaw());
                if (currency != null && zone.getPaymentModifier(currency) == null) {
                    zone.getPaymentModifierMap().put(currency.getInternalId(), Modifier.add(0.5, 0, 0));
                    zone.save();
                }
                return true;
            }).setSuggestions(EconomyBridge.getCurrencyIds(), true);
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, Lang.EDITOR_ZONE_MODIFIER_XP_OBJECT, 4, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openModifierEditor(viewer.getPlayer(), zone, zone.getXPModifier()));
        }).getOptions().addDisplayModifier((viewer, item) -> {
            Zone zone = this.getLink(viewer);
            ItemReplacer.replace(item, zone.getXPModifier().replacePlaceholders());
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
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Currency> autoFill) {
        Player player = viewer.getPlayer();
        Zone zone = this.getLink(player);

        autoFill.setSlots(IntStream.range(9, 36).toArray());
        autoFill.setItems(zone.getPaymentModifierMap().keySet().stream()
            .map(EconomyBridge::getCurrency)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Currency::getInternalId)).toList());
        autoFill.setItemCreator(currency -> {
            ItemStack item = new ItemStack(Material.GOLD_NUGGET);
            Modifier modifier = zone.getPaymentModifier(currency);
            if (modifier == null) return item;

            ItemReplacer.create(item).trimmed().hideFlags()
                .readLocale(Lang.EDITOR_ZONE_MODIFIER_CURRENCY_OBJECT)
                .replace(modifier.getPlaceholders())
                .replace(currency.replacePlaceholders())
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(currency -> (viewer1, event) -> {
            Modifier modifier = zone.getPaymentModifier(currency);
            if (modifier == null) {
                this.runNextTick(() -> this.open(viewer));
                return;
            }

            if (event.getClick() == ClickType.DROP) {
                zone.getPaymentModifierMap().remove(currency);
                zone.save();
                this.runNextTick(() -> this.flush(viewer));
                return;
            }

            this.runNextTick(() -> this.zoneManager.openModifierEditor(player, zone, modifier));
        });
    }
}
