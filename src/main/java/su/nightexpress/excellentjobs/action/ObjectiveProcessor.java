package su.nightexpress.excellentjobs.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ObjectiveProcessor<O> {

    default void progressObjective(@NotNull Player player, @NotNull O object, int amount) {
        this.progressObjective(player, object, amount, 0D);
    }

    void progressObjective(@NotNull Player player, @NotNull O object, int amount, double multiplier);
}
