package su.nightexpress.excellentjobs.command.zone;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.zone.ZoneManager;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

abstract class ZoneSubCommand extends AbstractCommand<JobsPlugin> {

    protected final ZoneManager zoneManager;

    public ZoneSubCommand(@NotNull JobsPlugin plugin, @NotNull ZoneManager zoneManager, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_ZONE);
        this.zoneManager = zoneManager;
    }
}
