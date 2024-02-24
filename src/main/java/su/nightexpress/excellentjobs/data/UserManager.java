package su.nightexpress.excellentjobs.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.nightcore.database.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<JobsPlugin, JobUser> {

    public UserManager(@NotNull JobsPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public JobUser createUserData(@NotNull UUID uuid, @NotNull String name) {
        return JobUser.create(this.plugin, uuid, name);
    }
}
