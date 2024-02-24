package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class OrderReward {

    private final String id;
    private final String name;
    private final List<String> commands;

    public OrderReward(@NotNull String id, @NotNull String name, @NotNull List<String> commands) {
        this.id = id.toLowerCase();
        this.name = name;
        this.commands = new ArrayList<>(commands);
    }

    @NotNull
    public static OrderReward read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String name = config.getString(path + ".Name", StringUtil.capitalizeUnderscored(id));
        List<String> commands = config.getStringList(path + ".Commands");

        return new OrderReward(id, name, commands);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.getName());
        config.set(path + ".Commands", this.getCommands());
    }

    public void give(@NotNull Player player) {
        this.getCommands().forEach(command -> Players.dispatchCommand(player, command));
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getCommands() {
        return commands;
    }
}
