package su.nightexpress.excellentjobs.grind.provider;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.type.GrindType;

public interface GrindTypeProvider<T extends GrindType<?>> {

    @NotNull T provide(@NotNull String id);
}
