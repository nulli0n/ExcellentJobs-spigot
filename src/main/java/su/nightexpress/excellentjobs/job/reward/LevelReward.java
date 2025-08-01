package su.nightexpress.excellentjobs.job.reward;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class LevelReward implements Writeable {

    private final String       id;
    private final int[]          levels;
    private final boolean      repeatable;
    private final String       name;
    private final List<String> description;
    private final List<String> commands;
    private final String       requiredPermission;
    private final List<String> requiredRanks;
    private final Set<JobState> requiredStates;
    private final List<String> requirementText;

    public LevelReward(@NotNull String id,
                       int[] levels,
                       boolean repeatable,
                       @NotNull String name,
                       @NotNull List<String> description,
                       @NotNull List<String> commands,
                       @NotNull String requiredPermission,
                       @NotNull List<String> requiredRanks,
                       @NotNull Set<JobState> requiredStates,
                       @NotNull List<String> requirementText) {
        this.id = id.toLowerCase();
        this.levels = levels;
        this.repeatable = repeatable;
        this.name = name;
        this.description = description;
        this.commands = commands;
        this.requiredPermission = requiredPermission;
        this.requiredRanks = requiredRanks;
        this.requiredStates = requiredStates;
        this.requirementText = requirementText;
    }

    @NotNull
    public static LevelReward read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        if (config.contains(path + ".Level")) {
            int oldLevel = config.getInt(path + ".Level", 0);
            config.setIntArray(path + ".Levels", new int[]{oldLevel});
            config.remove(path + ".Level");
        }

        int[] levels = ConfigValue.create(path + ".Levels", new int[0]).read(config);
        boolean repeatable = ConfigValue.create(path + ".Repeatable", false).read(config);
        String name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(id)).read(config);
        List<String> description = ConfigValue.create(path + ".Description", Lists.newList()).read(config);
        List<String> commands = ConfigValue.create(path + ".Commands", Lists.newList()).read(config);
        String permission = ConfigValue.create(path + ".Required_Permission", "null").read(config);
        List<String> ranks = ConfigValue.create(path + ".Required_Ranks", Lists.newList()).onRead(set -> Lists.modify(set, String::toLowerCase)).read(config);
        Set<JobState> states = Lists.modify(ConfigValue.create(path + ".Required_States", Lists.newSet()).read(config), s -> Enums.get(s, JobState.class));
        List<String> requirementInfo = ConfigValue.create(path + ".RequirementInfo", Lists.newList()).read(config);

        return new LevelReward(id, levels, repeatable, name, description, commands, permission, ranks, states, requirementInfo);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.setIntArray(path + ".Levels", this.levels);
        config.set(path + ".Repeatable", this.repeatable);
        config.set(path + ".Name", this.name);
        config.set(path + ".Description", this.description);
        config.set(path + ".Commands", this.commands);
        config.set(path + ".Required_Permission", this.requiredPermission);
        config.set(path + ".Required_Ranks", this.requiredRanks);
        config.set(path + ".Required_States", Lists.modify(this.requiredStates, Enum::name));
        config.set(path + ".RequirementInfo", this.requirementText);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.LEVEL_REWARD.replacer(this);
    }

    public boolean isAvailable(@NotNull Player player) {
        return this.hasPermission(player) && this.isGoodRank(player);
    }

    public boolean isGoodRank(@NotNull Player player) {
        return Players.getInheritanceGroupsOrDefault(player).stream().anyMatch(this::isGoodRank);
    }

    public boolean hasPermission(@NotNull Player player) {
        if (this.requiredPermission.isBlank()) return true;
        if (this.requiredPermission.equalsIgnoreCase("null")) return true;

        return player.hasPermission(this.requiredPermission);
    }

    public boolean isGoodRank(@NotNull String rank) {
        if (this.requiredRanks.isEmpty()) return true;
        if (this.requiredRanks.contains(Placeholders.WILDCARD)) return true;

        return this.requiredRanks.contains(rank.toLowerCase());
    }

    public boolean isGoodLevel(int jobLevel) {
        if (jobLevel <= 0) return false;

        return Lists.contains(this.levels, jobLevel) || (this.repeatable && IntStream.of(this.levels).anyMatch(level -> jobLevel % level == 0));
    }

    public boolean isGoodState(@NotNull JobState state) {
        return state != JobState.INACTIVE && this.requiredStates.isEmpty() || this.requiredStates.contains(state);
    }

    public void run(@NotNull Player player) {
        Players.dispatchCommands(player, this.commands);
    }

    @NotNull
    public LevelReward parse(@NotNull Replacer modifierReplacer) {
        String name = modifierReplacer.apply(this.name);
        List<String> description = this.parseList(this.description, modifierReplacer);
        List<String> commands = this.parseList(this.commands, modifierReplacer);
        List<String> ranks = this.parseList(this.requiredRanks, modifierReplacer);
        List<String> requirementInfo = this.parseList(this.requirementText, modifierReplacer);
        Set<JobState> states = this.getRequiredStates();

        return new LevelReward(this.id, this.levels, this.repeatable, name, description, commands, this.requiredPermission, ranks, states, requirementInfo);
    }

    @NotNull
    private List<String> parseList(@NotNull List<String> list, @NotNull Replacer modifierReplacer) {
        return list.stream().map(modifierReplacer::apply).toList();
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public int[] getLevels() {
        return this.levels;
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

    @NotNull
    public String getRequiredPermission() {
        return this.requiredPermission;
    }

    @NotNull
    public List<String> getRequiredRanks() {
        return new ArrayList<>(this.requiredRanks);
    }

    @NotNull
    public Set<JobState> getRequiredStates() {
        return new HashSet<>(this.requiredStates);
    }

    @NotNull
    public List<String> getRequirementText() {
        return this.requirementText;
    }
}
