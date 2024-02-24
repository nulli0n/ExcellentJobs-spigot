package su.nightexpress.excellentjobs.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ObjectiveProcessor<O> {

    void progressObjective(@NotNull Player player, @NotNull O object, int amount);
}
