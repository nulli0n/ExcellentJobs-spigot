package su.nightexpress.excellentjobs;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.excellentjobs.hook.work.EvenMoreFishWork;
import su.nightexpress.excellentjobs.hook.work.MythicMobsWork;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.booster.BoosterManager;
import su.nightexpress.excellentjobs.command.impl.BaseCommands;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.DataHandler;
import su.nightexpress.excellentjobs.hook.HookPlugin;
import su.nightexpress.excellentjobs.hook.work.CustomFishingWork;
import su.nightexpress.excellentjobs.hook.impl.PlaceholderHook;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.work.WorkRegistry;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.user.UserManager;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

import java.util.function.Supplier;

public class JobsPlugin extends NightPlugin implements ImprovedCommands {

    private DataHandler dataHandler;
    private UserManager userManager;

    private BoosterManager  boosterManager;
    private JobManager      jobManager;
    private ZoneManager     zoneManager;
    private StatsManager    statsManager;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Jobs", new String[]{"jobs", "job", "excellentjobs"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    public void enable() {
        if (!EconomyBridge.hasCurrency()) {
            this.error("No currencies are available! Please setup EconomyBridge correctly. Plugin will be disabled.");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        JobsAPI.load(this);
        Keys.load(this);
        WorkRegistry.load(this);
        BaseCommands.load(this);

        this.loadIntegrations();

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.jobManager = new JobManager(this);
        this.jobManager.setup();

        if (Config.ZONES_ENABLED.get()) {
            this.zoneManager = new ZoneManager(this);
            this.zoneManager.setup();
        }

        if (Config.isStatisticEnabled()) {
            this.statsManager = new StatsManager(this);
            this.statsManager.setup();
        }

        if (Config.isBoostersEnabled()) {
            this.boosterManager = new BoosterManager(this);
            this.boosterManager.setup();
        }

        if (Config.ABUSE_TRACK_PLAYER_BLOCKS.get()) {
            PlayerBlockTracker.initialize();
            PlayerBlockTracker.BLOCK_FILTERS.add(block -> true);
        }

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }
        if (this.boosterManager != null) this.boosterManager.shutdown();
        if (this.zoneManager != null) this.zoneManager.shutdown();
        if (this.statsManager != null) this.statsManager.shutdown();
        if (this.jobManager != null) this.jobManager.shutdown();

        this.userManager.shutdown();
        this.dataHandler.shutdown();

        WorkRegistry.clear();
        JobsAPI.clear();
    }

    private void loadIntegrations() {
        this.loadIntegration(HookPlugin.MYTHIC_MOBS, () -> new MythicMobsWork(this, "kill_mythic_mob"));
        this.loadIntegration(HookPlugin.EVEN_MORE_FISH, () -> new EvenMoreFishWork(this, "emf_fish_item"));
        this.loadIntegration(HookPlugin.CUSTOM_FISHING, () -> new CustomFishingWork(this, "custom_fishing"));
    }

    private <E extends Event, O> void loadIntegration(@NotNull String plugin, @NotNull Supplier<Work<E, O>> supplier) {
        if (Plugins.isLoaded(plugin)) {
            this.info("Found " + plugin + "! Adding new work types...");

            var work = supplier.get();
            String displayName = ConfigValue.create("WorkType." + work.getId(), work.getDisplayName()).read(this.getLang());
            work.setDisplayName(displayName);

            WorkRegistry.register(work);
        }
    }

    @NotNull
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }

    @Nullable
    public BoosterManager getBoosterManager() {
        return this.boosterManager;
    }

    @NotNull
    public JobManager getJobManager() {
        return this.jobManager;
    }

    @Nullable
    public ZoneManager getZoneManager() {
        return this.zoneManager;
    }

    @Nullable
    public StatsManager getStatsManager() {
        return this.statsManager;
    }
}