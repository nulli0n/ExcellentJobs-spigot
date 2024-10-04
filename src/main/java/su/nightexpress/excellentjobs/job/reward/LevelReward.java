package su.nightexpress.excellentjobs.job.reward;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class LevelReward implements Placeholder {

    private final String       id;
    private final int          level;
    private final boolean      repeatable;
    private final String name;
    private final List<String> description;
    private final List<String> commands;

    private final PlaceholderMap placeholders;

    public LevelReward(@NotNull String id, int level, boolean repeatable,
                       @NotNull String name,
                       @NotNull List<String> description,
                       @NotNull List<String> commands) {
        this.id = id.toLowerCase();
        this.level = Math.abs(level);
        this.repeatable = repeatable;
        this.name = name;
        this.description = description;
        this.commands = commands;

        this.placeholders = Placeholders.forReward(this);
    }

    @NotNull
    public static LevelReward read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        int level = ConfigValue.create(path + ".Level", 0).read(config);
        boolean repeatable = ConfigValue.create(path + ".Repeatable", false).read(config);
        String name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(id)).read(config);
        List<String> description = ConfigValue.create(path + ".Description", Lists.newList()).read(config);
        List<String> commands = ConfigValue.create(path + ".Commands", Lists.newList()).read(config);

        return new LevelReward(id, level, repeatable, name, description, commands);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Level", this.level);
        config.set(path + ".Repeatable", this.repeatable);
        config.set(path + ".Name", this.name);
        config.set(path + ".Description", this.description);
        config.set(path + ".Commands", this.commands);
    }

    @NotNull
    @Override
    public PlaceholderMap getPlaceholders() {
        return this.placeholders;
    }

    public boolean isGoodLevel(int jobLevel) {
        if (jobLevel <= 0) return false;

        return this.level == jobLevel || (this.isRepeatable() && jobLevel % this.level == 0);
    }

    public void run(@NotNull Player player) {
        Players.dispatchCommands(player, this.commands);
    }

    @NotNull
    public LevelReward parse(@NotNull UnaryOperator<String> modifierReplacer) {
        String name = modifierReplacer.apply(this.name);
        List<String> description = this.parseDescription(modifierReplacer);
        List<String> commands = this.parseCommands(modifierReplacer);

        return new LevelReward(this.id, this.level, this.repeatable, name, description, commands);
    }

    @NotNull
    public List<String> parseDescription(@NotNull UnaryOperator<String> modifierReplacer) {
        return this.parseList(this.description, modifierReplacer);
    }

    @NotNull
    public List<String> parseCommands(@NotNull UnaryOperator<String> modifierReplacer) {
        return parseList(this.commands, modifierReplacer);
    }

    @NotNull
    private List<String> parseList(@NotNull List<String> list, @NotNull UnaryOperator<String> modifierReplacer) {
        return list.stream().map(modifierReplacer).toList();
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isRepeatable() {
        return this.repeatable;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public List<String> getDescription() {
        return new ArrayList<>(this.description);
    }

    @NotNull
    public List<String> getCommands() {
        return new ArrayList<>(this.commands);
    }
}
