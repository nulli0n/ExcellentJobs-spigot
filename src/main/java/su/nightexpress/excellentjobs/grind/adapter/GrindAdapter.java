package su.nightexpress.excellentjobs.grind.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GrindAdapter<I, O> {

    boolean canHandle(@NotNull O entity);

    @NotNull String getName();

    @Nullable I getTypeByName(@NotNull String name);

    @Nullable I getType(@NotNull O entity);

    @NotNull String getName(@NotNull I type);

    @Nullable String toFullNameOfEntity(@NotNull O entity);

    @NotNull String toFullNameOfType(@NotNull I type);

    @NotNull String toFullName(@NotNull String name);
}
