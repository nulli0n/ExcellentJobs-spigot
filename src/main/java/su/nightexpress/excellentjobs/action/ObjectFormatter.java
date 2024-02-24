package su.nightexpress.excellentjobs.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectFormatter<O> {

    @NotNull
    String getName(@NotNull O object);

    @NotNull
    String getLocalizedName(@NotNull O object);

    @Nullable
    O parseObject(@NotNull String name);

    @NotNull
    default String getLocalizedName(@NotNull String name) {
        O object = this.parseObject(name);
        return object == null ? name : this.getLocalizedName(object);
    }
}
