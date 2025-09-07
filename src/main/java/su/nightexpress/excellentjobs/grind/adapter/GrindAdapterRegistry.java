package su.nightexpress.excellentjobs.grind.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.grind.adapter.impl.CustomCropsAdapter;
import su.nightexpress.excellentjobs.grind.adapter.impl.CustomFishingAdapter;
import su.nightexpress.excellentjobs.grind.adapter.impl.EvenMoreFishAdapter;
import su.nightexpress.excellentjobs.grind.adapter.impl.MythicMobAdapter;
import su.nightexpress.excellentjobs.hook.HookPlugin;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GrindAdapterRegistry {

    private static final Map<String, GrindAdapter<?, ?>> ADAPTER_BY_ID = new HashMap<>();
    //private static final Map<String, GrindAdapter<?, ?>> ADAPTER_BY_KEY = new HashMap<>();

    private static JobsPlugin plugin;

    public static void load(@NotNull JobsPlugin instance) {
        plugin = instance;

        registerDefaults();
    }

    public static void clear() {
        ADAPTER_BY_ID.clear();
        //ADAPTER_BY_KEY.clear();
        plugin = null;
    }

    public static void registerDefaults() {
        register(GrindAdapters.VANILLA_MOB, GrindAdapterFamily.ENTITY);
        register(GrindAdapters.VANILLA_BLOCK, GrindAdapterFamily.BLOCK);
        register(GrindAdapters.VANILLA_BLOCK_STATE, GrindAdapterFamily.BLOCK_STATE);
        register(GrindAdapters.VANILLA_ITEM, GrindAdapterFamily.ITEM);
        register(GrindAdapters.VANILLA_ENCHANTMENT, GrindAdapterFamily.ENCHANTMENT);

        registerExternal(HookPlugin.MYTHIC_MOBS, MythicMobAdapter::new, GrindAdapterFamily.ENTITY);
        registerExternal(HookPlugin.EVEN_MORE_FISH, EvenMoreFishAdapter::new, GrindAdapterFamily.ITEM);
        registerExternal(HookPlugin.CUSTOM_FISHING, CustomFishingAdapter::new, GrindAdapterFamily.ITEM);
        registerExternal(HookPlugin.CUSTOM_CROPS, CustomCropsAdapter::new, GrindAdapterFamily.BLOCK);
    }

    public static <I, O, E extends GrindAdapter<I, O>> void registerExternal(@NotNull String pluginName, @NotNull Function<String, E> function, @NotNull GrindAdapterFamily<O> family) {
        if (!Plugins.isInstalled(pluginName)) return;

        plugin.info("Registering adapter for the '" + pluginName + "' plugin.");
        register(pluginName, function, family);
    }

    public static <I, O, E extends GrindAdapter<I, O>> void register(@NotNull String name, @NotNull Function<String, E> function, @NotNull GrindAdapterFamily<O> family) {
        E adapter = function.apply(name);
        register(adapter, family);
    }

    public static <I, O, E extends GrindAdapter<I, O>> void register(@NotNull E adapter, @NotNull GrindAdapterFamily<O> family) {
        // TODO Check disabled
        family.addAdapter(adapter);
        ADAPTER_BY_ID.put(adapter.getName(), adapter);
        //ADAPTER_BY_KEY.put(adapter.getKey(), adapter);
    }

    @Nullable
    public static GrindAdapter<?, ?> getAdapterByName(@NotNull String name) {
        return ADAPTER_BY_ID.get(LowerCase.INTERNAL.apply(name));
    }

    /*@Nullable
    public static GrindAdapter<?, ?> getAdapterByKey(@NotNull String key) {
        return ADAPTER_BY_KEY.get(LowerCase.INTERNAL.apply(key));
    }*/
}
