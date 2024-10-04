package su.nightexpress.excellentjobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.action.ActionRegistry;
import su.nightexpress.excellentjobs.booster.BoosterManager;
import su.nightexpress.excellentjobs.command.base.*;
import su.nightexpress.excellentjobs.command.booster.BoosterCommand;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.currency.CurrencyManager;
import su.nightexpress.excellentjobs.data.DataHandler;
import su.nightexpress.excellentjobs.data.UserManager;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.hook.impl.PlaceholderHook;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.stats.StatsManager;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.command.base.ReloadSubCommand;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

public class JobsPlugin extends NightDataPlugin<JobUser> {

    private DataHandler dataHandler;
    private UserManager userManager;

    private ActionRegistry  actionRegistry;
    private CurrencyManager currencyManager;
    private BoosterManager  boosterManager;
    private JobManager      jobManager;
    private ZoneManager     zoneManager;
    private StatsManager    statsManager;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Jobs", new String[]{"excellentjobs", "jobs", "job"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    public void enable() {
        Keys.load(this);

        this.registerCommands();

        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();
        if (!this.currencyManager.hasCurrency()) {
            this.error("No currencies are available! Plugin will be disabled.");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this);
        this.userManager.setup();

        this.actionRegistry = new ActionRegistry(this);
        this.actionRegistry.setup();

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

        this.boosterManager = new BoosterManager(this);
        this.boosterManager.setup();

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
        if (this.currencyManager != null) this.currencyManager.shutdown();

        this.actionRegistry.shutdown();
        this.userManager.shutdown();
        this.dataHandler.shutdown();
    }

    private void registerCommands() {
        NightPluginCommand baseCommand = this.getBaseCommand();

        baseCommand.addChildren(new StatsCommand(this));
        baseCommand.addChildren(new XPCommand(this));
        baseCommand.addChildren(new LevelCommand(this));
        baseCommand.addChildren(new ResetCommand(this));
        baseCommand.addChildren(new JoinCommand(this));
        baseCommand.addChildren(new LeaveCommand(this));
        baseCommand.addChildren(new SetStateCommand(this));

        if (Config.GENERAL_DEFAULT_MENU_COMMAND_ENABLED.get()) {
            baseCommand.addDefaultCommand(new MenuCommand(this));
        }
        else {
            baseCommand.addChildren(new MenuCommand(this));
        }
        baseCommand.addChildren(new ReloadSubCommand(this, Perms.COMMAND_RELOAD));
        baseCommand.addChildren(new BoosterCommand(this));
        baseCommand.addChildren(new ObjectivesCommand(this));
    }

    @NotNull
    @Override
    public DataHandler getData() {
        return dataHandler;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    @NotNull
    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    @NotNull
    public JobManager getJobManager() {
        return jobManager;
    }

    @Nullable
    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    @Nullable
    public StatsManager getStatsManager() {
        return statsManager;
    }
}