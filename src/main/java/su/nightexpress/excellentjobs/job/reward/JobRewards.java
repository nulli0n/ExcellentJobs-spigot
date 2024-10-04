package su.nightexpress.excellentjobs.job.reward;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.util.*;
import java.util.function.UnaryOperator;

public class JobRewards {

    private static final String DEF_MOD_MONEY = "money";

    private final Map<String, LevelReward> rewardMap;
    private final Map<String, Modifier>    modifierMap;

    public JobRewards() {
        this.rewardMap = new HashMap<>();
        this.modifierMap = new HashMap<>();
    }

    public void load(@NotNull FileConfig config, @NotNull String path) {
        this.rewardMap.putAll(ConfigValue.forMap(path + ".List",
            (cfg, path1, id) -> LevelReward.read(cfg, path1 + "." + id, id),
            (cfg, path1, map) -> map.forEach((id, reward) -> reward.write(cfg, path1 + "." + id)),
            JobRewards::getDefaultRewards,
            "Here you can create unlimited amount of custom job rewards.",
            "Settings:",
            "  [Level] = Required player's job level.",
            "  [Repeatable] true = Reward given every N job levels; false = Reward given once at exact level.",
            "Placeholders:",
            "  Use '" + Placeholders.PLAYER_NAME + "' placeholder for a player name.",
            "  Use '" + Placeholders.REWARD_MODIFIER.apply("name") + "' placeholder to display modifier value, where 'name' is name of the modifier.",
            "  Use '" + Players.PLAYER_COMMAND_PREFIX + "' prefix to run command by a player."
        ).read(config));

        this.modifierMap.putAll(ConfigValue.forMap(path + ".Modifiers",
            (cfg, path1, id) -> Modifier.read(cfg, path1 + "." + id),
            (cfg, path1, map) -> map.forEach((id, reward) -> reward.write(cfg, path1 + "." + id)),
            JobRewards::getDefaultModifiers,
            "Here you can create unlimited amount of custom modifiers for rewards."
        ).read(config));
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path + ".List");
        config.remove(path + ".Modifiers");
        this.rewardMap.forEach((id, reward) -> reward.write(config, path + ".List." + id));
        this.modifierMap.forEach((id, modifier) -> modifier.write(config, path + ".Modifiers." + id));
    }

    @NotNull
    public static Map<String, LevelReward> getDefaultRewards() {
        Map<String, LevelReward> map = new HashMap<>();

        LevelReward reward1 = new LevelReward(
            "every_5_levels", 5, true, "Money",
            Lists.newList("Small $" + Placeholders.REWARD_MODIFIER.apply(DEF_MOD_MONEY) + " reward."),
            Lists.newList("money give " + Placeholders.PLAYER_NAME + " " + Placeholders.REWARD_MODIFIER.apply(DEF_MOD_MONEY))
        );

        map.put(reward1.getId(), reward1);

        return map;
    }

    @NotNull
    public static Map<String, Modifier> getDefaultModifiers() {
        Map<String, Modifier> map = new HashMap<>();

        map.put(DEF_MOD_MONEY, Modifier.add(100, 10, 1));

        return map;
    }

    @NotNull
    public List<LevelReward> getRewards(int jobLevel) {
        UnaryOperator<String> replacer = this.getModifierReplacer(jobLevel);

        return this.getRewards().stream().filter(reward -> reward.isGoodLevel(jobLevel)).map(reward -> reward.parse(replacer)).toList();
    }

    @NotNull
    public UnaryOperator<String> getModifierReplacer(int jobLevel) {
        PlaceholderMap map = new PlaceholderMap();

        this.modifierMap.forEach((id, modifier) -> {
            map.add(Placeholders.REWARD_MODIFIER.apply(id), NumberUtil.format(modifier.getValue(jobLevel)));
        });

        return map.replacer();
    }

    @NotNull
    public Set<LevelReward> getRewards() {
        return new HashSet<>(this.rewardMap.values());
    }

    @NotNull
    public Set<Modifier> getModifiers() {
        return new HashSet<>(this.modifierMap.values());
    }

    @NotNull
    public Map<String, LevelReward> getRewardMap() {
        return this.rewardMap;
    }

    @NotNull
    public Map<String, Modifier> getModifierMap() {
        return this.modifierMap;
    }
}
