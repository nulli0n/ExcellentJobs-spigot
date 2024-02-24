package su.nightexpress.excellentjobs.zone.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.click.ClickAction;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.Lists;

import java.util.Comparator;
import java.util.stream.IntStream;

import static su.nightexpress.excellentjobs.Placeholders.ZONE_ID;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLACK;
import static su.nightexpress.nightcore.util.text.tag.Tags.BLUE;

public class ZoneModifiersEditor extends EditorMenu<JobsPlugin, Zone> implements AutoFilled<Currency> {

    private final ZoneManager zoneManager;

    public ZoneModifiersEditor(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager) {
        super(plugin, BLACK.enclose("Zone Modifiers [" + BLUE.enclose(ZONE_ID) + "]"), 45);
        this.zoneManager = zoneManager;

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addReturn(39, (viewer, event, zone) -> {
            this.runNextTick(() -> this.zoneManager.openEditor(viewer.getPlayer(), zone));
        });
        this.addCreation(Lang.EDITOR_ZONE_MODIFIER_CURRENCY_CREATE, 41, (viewer, event, zone) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_CURRENCY, (dialog, input) -> {
                Currency currency = this.plugin.getCurrencyManager().getCurrency(input.getTextRaw());
                if (currency != null && zone.getPaymentModifier(currency) == null) {
                    zone.getPaymentModifierMap().put(currency, Modifier.add(0.5, 0, 0));
                    zone.save();
                }
                return true;
            }).setSuggestions(plugin.getCurrencyManager().getCurrencyIds(), true);
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, Lang.EDITOR_ZONE_MODIFIER_XP_OBJECT, 37, (viewer, event, zone) -> {
            this.getModifierClick(zone.getXPModifier()).onClick(viewer, event);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            Zone zone = this.getObject(viewer);
            ItemReplacer.replace(item, zone.getXPModifier().replacePlaceholders());
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
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Currency> autoFill) {
        Player player = viewer.getPlayer();
        Zone zone = this.getObject(player);

        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(zone.getPaymentModifierMap().keySet().stream().sorted(Comparator.comparing(Currency::getId)).toList());
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
                this.runNextTick(() -> this.open(viewer));
                return;
            }

            this.getModifierClick(modifier).onClick(viewer, event);
        });
    }

    @NotNull
    private ClickAction getModifierClick(@NotNull Modifier modifier) {
        return (viewer, event) -> {
            this.editObject(viewer, zone -> {
                if (event.isShiftClick() && event.isShiftClick()) {
                    modifier.setAction(Lists.next(modifier.getAction()));
                    zone.save();
                    this.runNextTick(() -> this.open(viewer));
                    return;
                }

                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, (dialog, input) -> {
                    double value = input.asAnyDouble(0D);
                    if (event.isShiftClick() && event.isLeftClick()) modifier.setStep(value);
                    else if (event.isLeftClick()) modifier.setBase(value);
                    else if (event.isRightClick()) modifier.setPerLevel(value);

                    zone.save();

                    return true;
                });
            });
        };
    }
}
