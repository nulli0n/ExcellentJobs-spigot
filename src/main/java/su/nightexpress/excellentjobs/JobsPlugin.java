package su.nightexpress.excellentjobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.booster.BoosterManager;
import su.nightexpress.excellentjobs.booster.command.BoosterCommands;
import su.nightexpress.excellentjobs.command.impl.BaseCommands;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.DataHandler;
import su.nightexpress.excellentjobs.grind.GrindManager;
import su.nightexpress.excellentjobs.grind.GrindRegistry;
import su.nightexpress.excellentjobs.hook.impl.PlaceholderHook;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.stats.command.StatsCommands;
import su.nightexpress.excellentjobs.user.UserManager;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

public class JobsPlugin extends NightPlugin {

    private DataHandler dataHandler;
    private UserManager userManager;

    private BoosterManager  boosterManager;
    private GrindManager grindManager;
    private JobManager      jobManager;
    private ZoneManager     zoneManager;
    private StatsManager    statsManager;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Jobs", new String[]{BaseCommands.JOBS_ALIAS, "job", "excellentjobs"})
            .setConfigClass(Config.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    protected void addRegistries() {
        this.registerLang(Lang.class);
    }

    @Override
    protected boolean disableCommandManager() {
        return true;
    }

    @Override
    public void enable() {
        if (!EconomyBridge.hasCurrency()) {
            this.error("No currencies are available! Please setup EconomyBridge correctly. Plugin will be disabled.");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        this.loadEngine();

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.grindManager = new GrindManager(this);
        this.grindManager.setup();

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

        this.loadCommands();
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
        if (this.grindManager != null) this.grindManager.shutdown();

        this.userManager.shutdown();
        this.dataHandler.shutdown();

        JobsAPI.clear();
        Keys.clear();
    }

    private void loadEngine() {
        JobsAPI.load(this);
        Keys.load(this);
    }

    private void loadCommands() {
        this.rootCommand = NightCommand.forPlugin(this, builder -> {
            BaseCommands.load(this, builder);

            if (this.boosterManager != null) {
                BoosterCommands.load(this, this.boosterManager, builder);
            }
            if (this.statsManager != null) {
                StatsCommands.load(this, this.statsManager, builder);
            }
        });
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
    public GrindManager getGrindManager() {
        return this.grindManager;
    }

    @NotNull
    public GrindRegistry getGrindRegistry() {
        return this.grindManager.getGrindRegistry();
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
