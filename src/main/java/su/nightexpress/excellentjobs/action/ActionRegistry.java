package su.nightexpress.excellentjobs.action;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.api.event.bukkit.PlayerCollectedHoneyEvent;
import su.nightexpress.excellentjobs.hook.HookId;
import su.nightexpress.excellentjobs.hook.impl.EvenMoreFishHook;
import su.nightexpress.excellentjobs.hook.impl.MythicMobsHook;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ActionRegistry extends AbstractManager<JobsPlugin> {

    private static final Map<String, ActionType<?, ?>> ACTION_TYPE_MAP = new HashMap<>();

    public ActionRegistry(@NotNull JobsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.registerAction(InventoryClickEvent.class, EventPriority.HIGHEST, ActionTypes.ANVIL_RENAME);
        this.registerAction(InventoryClickEvent.class, EventPriority.HIGHEST, ActionTypes.ANVIL_REPAIR);

        // Block Material related
        this.registerAction(BlockBreakEvent.class, EventPriority.HIGHEST, ActionTypes.BLOCK_BREAK);
        this.registerAction(BlockFertilizeEvent.class, EventPriority.MONITOR, ActionTypes.BLOCK_FERTILIZE);
        this.registerAction(BlockPlaceEvent.class, EventPriority.MONITOR, ActionTypes.BLOCK_PLACE);
        this.registerAction(PlayerHarvestBlockEvent.class, EventPriority.HIGHEST, ActionTypes.BLOCK_HARVEST);

        // Damage Cause related
        this.registerAction(EntityDamageByEntityEvent.class, EventPriority.MONITOR, ActionTypes.DAMAGE_INFLICT);
        this.registerAction(EntityDamageEvent.class, EventPriority.MONITOR, ActionTypes.DAMAGE_RECEIVE);

        // Entity Type related
        this.registerAction(EntityBreedEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_BREED);
        this.registerAction(EntityDeathEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_KILL);
        this.registerAction(EntityDeathEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_SHOOT);
        this.registerAction(PlayerShearEntityEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_SHEAR);
        this.registerAction(EntityTameEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_TAME);
        this.registerAction(ProjectileLaunchEvent.class, EventPriority.MONITOR, ActionTypes.PROJECTILE_LAUNCH);
        this.registerAction(PlayerBucketFillEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_MILK);

        // Item Material related
        this.registerAction(PlayerItemConsumeEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_CONSUME);
        this.registerAction(CraftItemEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_CRAFT);
        this.registerAction(InventoryClickEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_DISENCHANT);
        this.registerAction(EnchantItemEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_ENCHANT);
        this.registerAction(PlayerFishEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_FISH);
        this.registerAction(FurnaceExtractEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_FURNACE);
        this.registerAction(InventoryClickEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_TRADE);

        // PotionEffectType related
        this.registerAction(BrewEvent.class, EventPriority.MONITOR, ActionTypes.POTION_BREW);
        this.registerAction(PlayerItemConsumeEvent.class, EventPriority.MONITOR, ActionTypes.POTION_DRINK);

        // Enchantment related
        this.registerAction(EnchantItemEvent.class, EventPriority.MONITOR, ActionTypes.ENCHANT_GET);
        this.registerAction(InventoryClickEvent.class, EventPriority.MONITOR, ActionTypes.ENCHANT_REMOVE);

        // Misc
        this.registerAction(PlayerCollectedHoneyEvent.class, EventPriority.MONITOR, ActionTypes.HONEY_COLLECT);

        this.registerHooks();
    }

    private void registerHooks() {
        this.registerExternal(HookId.MYTHIC_MOBS, MythicMobsHook::register);
        this.registerExternal(HookId.EVEN_MORE_FISH, EvenMoreFishHook::register);
    }

    private void registerExternal(@NotNull String name, @NotNull Consumer<ActionRegistry> consumer) {
        if (Plugins.isInstalled(name)) {
            this.plugin.info("Found " + name + "! Registering new objective types...");
            consumer.accept(this);
        }
    }

    @Override
    protected void onShutdown() {
        ACTION_TYPE_MAP.clear();
    }

    @Nullable
    public <E extends Event, O> ActionType<E, O> registerAction(@NotNull Class<E> eventClass,
                                                                @NotNull EventPriority priority,
                                                                @NotNull String name,
                                                                @NotNull ObjectFormatter<O> objectFormatter,
                                                                @NotNull EventHelper<E, O> dataGather) {
        return this.registerAction(eventClass, priority, ActionType.create(name, objectFormatter, dataGather));
    }

    @Nullable
    public <E extends Event, O> ActionType<E, O> registerAction(@NotNull Class<E> eventClass,
                                                                @NotNull EventPriority priority,
                                                                @NotNull ActionType<E, O> actionType) {

        if (!actionType.loadSettings(this.plugin)) return null;

        //for (EventPriority priority : EventPriority.values()) {
        WrappedEvent<E, O> event = new WrappedEvent<>(plugin, eventClass, actionType);
        plugin.getPluginManager().registerEvent(eventClass, event, priority, event, plugin, true);
        //}

        ACTION_TYPE_MAP.put(actionType.getName(), actionType);
        return actionType;
    }

    @Nullable
    public ActionType<?, ?> getActionType(@NotNull String name) {
        return ACTION_TYPE_MAP.get(name.toLowerCase());
    }
}
