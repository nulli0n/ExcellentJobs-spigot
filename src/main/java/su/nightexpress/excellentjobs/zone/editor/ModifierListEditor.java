package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.ui.dialog.Dialog;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class ModifierListEditor extends LinkedMenu<JobsPlugin, Zone> implements Filled<Currency> {

    private final ZoneManager manager;

    public ModifierListEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager manager) {
        super(plugin, MenuType.GENERIC_9X5, Lang.EDITOR_TITLE_ZONE_MODIFIER_LIST.getString());
        this.manager = manager;

        this.addItem(MenuItem.buildNextPage(this, 44));
        this.addItem(MenuItem.buildPreviousPage(this, 36));
        this.addItem(MenuItem.buildReturn(this, 39, (viewer, event) -> {
            this.runNextTick(() -> this.manager.openEditor(viewer.getPlayer(), this.getLink(viewer)));
        }));

        this.addItem(Material.ANVIL, Lang.EDITOR_ZONE_MODIFIER_CURRENCY_CREATE, 41, (viewer, event, zone) -> {
            this.handleInput(Dialog.builder(viewer, Lang.EDITOR_GENERIC_ENTER_CURRENCY, input -> {
                Currency currency = EconomyBridge.getCurrency(input.getTextRaw());
                if (currency != null && zone.getPaymentModifier(currency) == null) {
                    zone.getPaymentModifierMap().put(currency.getInternalId(), Modifier.add(0.5, 0, 0));
                    zone.save();
                }
                return true;
            }).setSuggestions(EconomyBridge.getCurrencyIds(), true));
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, Lang.EDITOR_ZONE_MODIFIER_XP_OBJECT, 4, (viewer, event, zone) -> {
            this.runNextTick(() -> this.manager.openModifierEditor(viewer.getPlayer(), zone, zone.getXPModifier()));
        }, ItemOptions.builder().setDisplayModifier((viewer, item) -> {
            Zone zone = this.getLink(viewer);
            item.replacement(replacer -> replacer.replace(zone.getXPModifier().replacePlaceholders()));
        }).build());
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
    public MenuFiller<Currency> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Zone zone = this.getLink(player);

        return MenuFiller.builder(this)
            .setSlots(IntStream.range(9, 36).toArray())
            .setItems(zone.getPaymentCurrencyIds().stream().map(EconomyBridge::getCurrency).filter(Objects::nonNull)
                .sorted(Comparator.comparing(Currency::getInternalId))
                .toList())
            .setItemCreator(currency -> {
                Modifier modifier = zone.getPaymentModifier(currency);
                if (modifier == null) return NightItem.fromType(Material.AIR);

                return NightItem.fromItemStack(currency.getIcon())
                    .setHideComponents(true)
                    .localized(Lang.EDITOR_ZONE_MODIFIER_CURRENCY_OBJECT)
                    .replacement(replacer -> replacer
                        .replace(modifier.replacePlaceholders())
                        .replace(currency.replacePlaceholders()));
            })
            .setItemClick(currency -> (viewer1, event) -> {
                Modifier modifier = zone.getPaymentModifier(currency);
                if (modifier == null) {
                    this.runNextTick(() -> this.flush(viewer));
                    return;
                }

                if (event.getClick() == ClickType.DROP) {
                    zone.getPaymentModifierMap().remove(currency.getInternalId());
                    zone.save();
                    this.runNextTick(() -> this.flush(viewer));
                    return;
                }

                this.runNextTick(() -> this.manager.openModifierEditor(player, zone, modifier));
            })
            .build();
    }
}
