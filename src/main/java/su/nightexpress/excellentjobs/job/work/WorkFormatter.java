package su.nightexpress.excellentjobs.job.work;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WorkFormatter<O> {

    @NotNull String getName(@NotNull O object);

    @NotNull String getLocalized(@NotNull O object);

    @Nullable O parseObject(@NotNull String name);
}
