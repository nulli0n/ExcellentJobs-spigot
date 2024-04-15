package su.nightexpress.excellentjobs.api.event.bukkit;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCollectedHoneyEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Block block;

    public PlayerCollectedHoneyEvent(@NotNull Player player, @NotNull Block block) {
        super(player);
        this.block = block;
    }

    @NotNull
    public Block getBlock() {
        return block;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
