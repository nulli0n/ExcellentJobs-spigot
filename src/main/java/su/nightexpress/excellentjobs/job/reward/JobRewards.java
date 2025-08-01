package su.nightexpress.excellentjobs.job.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;

import java.util.*;
import java.util.stream.Collectors;

public class JobRewards implements Writeable {

    private static final String DEF_MOD_MONEY = "money";

    private final Map<String, LevelReward> rewardMap;
    private final Map<String, Modifier>    modifierMap;

    public JobRewards(@NotNull Map<String, LevelReward> rewardMap, @NotNull Map<String, Modifier> modifierMap) {
        this.rewardMap = rewardMap;
        this.modifierMap = modifierMap;
    }

    @NotNull
    public static JobRewards getDefault() {
        return new JobRewards(getDefaultRewards(), getDefaultModifiers());
    }

    @NotNull
    public static JobRewards read(@NotNull FileConfig config, @NotNull String path) {
        Map<String, LevelReward> rewardMap = new LinkedHashMap<>();
        Map<String, Modifier> modifierMap = new HashMap<>();

        config.getSection(path + ".List").forEach(sId -> {
            LevelReward reward = LevelReward.read(config, path + ".List." + sId, sId);
            rewardMap.put(reward.getId(), reward);
        });

        config.getSection(path + ".Modifiers").forEach(sId -> {
            Modifier modifier = Modifier.read(config, path + ".Modifiers." + sId);
            modifierMap.put(sId.toLowerCase(), modifier);
        });

        return new JobRewards(rewardMap, modifierMap);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path + ".List");
        config.remove(path + ".Modifiers");
        this.rewardMap.forEach((id, reward) -> reward.write(config, path + ".List." + id));
        this.modifierMap.forEach((id, modifier) -> modifier.write(config, path + ".Modifiers." + id));
    }

    @NotNull
    public static Map<String, LevelReward> getDefaultRewards() {
        Map<String, LevelReward> map = new LinkedHashMap<>();

        LevelReward moneyReward = new LevelReward(
            "every_1_level", new int[]{1}, true, "$" + Placeholders.REWARD_MODIFIER.apply(DEF_MOD_MONEY),
            Lists.newList(),
            Lists.newList("money give " + Placeholders.PLAYER_NAME + " " + Placeholders.REWARD_MODIFIER_RAW.apply(DEF_MOD_MONEY)),
            "null",
            Lists.newList(),
            Lists.newSet(),
            Lists.newList("")
        );

        LevelReward donatorReward = new LevelReward(
            "donator_10_levels", new int[]{10}, true, "x1 Jobs Crate Key " + TagWrappers.RED.wrap("(Premium only)"),
            Lists.newList(),
            Lists.newList("crates key give " + Placeholders.PLAYER_NAME + " jobs 1"),
            "null",
            Lists.newList("vip", "premium"),
            Lists.newSet(JobState.PRIMARY),
            Lists.newList()
        );

        map.put(moneyReward.getId(), moneyReward);
        map.put(donatorReward.getId(), donatorReward);

        return map;
    }

    @NotNull
    public static Map<String, Modifier> getDefaultModifiers() {
        Map<String, Modifier> map = new HashMap<>();

        map.put(DEF_MOD_MONEY, Modifier.add(0, 1000, 1));

        return map;
    }

    @NotNull
    public List<LevelReward> getRewards(int jobLevel) {
        return this.getRewards(jobLevel, null);
    }

    @NotNull
    public List<LevelReward> getRewards(int jobLevel, @Nullable JobState state) {
        Replacer replacer = this.getModifierReplacer(jobLevel);

        return this.rewardMap.values().stream()
            .filter(reward -> reward.isGoodLevel(jobLevel) && (state == null || reward.isGoodState(state)))
            .map(reward -> reward.parse(replacer))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @NotNull
    public Replacer getModifierReplacer(int jobLevel) {
        Replacer replacer = Replacer.create();

        this.modifierMap.forEach((id, modifier) -> {
            replacer.replace(Placeholders.REWARD_MODIFIER.apply(id), NumberUtil.format(modifier.getValue(jobLevel)));
            replacer.replace(Placeholders.REWARD_MODIFIER_RAW.apply(id), String.valueOf(modifier.getValue(jobLevel)));
        });

        return replacer;
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
