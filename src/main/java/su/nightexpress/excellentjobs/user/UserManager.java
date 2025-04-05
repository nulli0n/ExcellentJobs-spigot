package su.nightexpress.excellentjobs.user;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.data.DataHandler;
import su.nightexpress.nightcore.db.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<JobsPlugin, JobUser> {

    public UserManager(@NotNull JobsPlugin plugin, @NotNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NotNull
    public JobUser create(@NotNull UUID uuid, @NotNull String name) {
        return JobUser.create(uuid, name);
    }
}
