package su.nightexpress.excellentjobs.job.legacy;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LegacyJobObjective {

    private final String                             id;
    private final String                             workId;
    private final Set<String>                        items;
    private final Map<String, LegacyObjectiveReward> paymentMap;
    private final LegacyObjectiveReward              xpReward;
    private final int                                unlockLevel;

    public LegacyJobObjective(@NotNull String id,
                              @NotNull String workId,
                              @NotNull Set<String> items,
                              @NotNull Map<String, LegacyObjectiveReward> paymentMap,
                              @NotNull LegacyObjectiveReward xpReward,
                              int unlockLevel) {
        this.id = id.toLowerCase();
        this.workId = workId;
        this.items = new HashSet<>(items);
        this.paymentMap = paymentMap;
        this.xpReward = xpReward;
        this.unlockLevel = unlockLevel;
    }

    @NotNull
    public static LegacyJobObjective read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String workType = ConfigValue.create(path + ".Type", "null").read(config);

        Set<String> objects = config.getStringSet(path + ".Objects");
        if (objects.remove(Placeholders.WILDCARD)) {
            objects.add(Placeholders.DEFAULT);
        }

        Map<String, LegacyObjectiveReward> currencyDrop = new HashMap<>();
        for (String curId : config.getSection(path + ".Payment")) {
            LegacyObjectiveReward objectiveReward = LegacyObjectiveReward.read(config, path + ".Payment." + curId);
            currencyDrop.put(CurrencyId.reroute(curId), objectiveReward);
        }
        LegacyObjectiveReward xpDrop = LegacyObjectiveReward.read(config, path + ".Job_XP");

        int unlockLevel = ConfigValue.create(path + ".Unlock_Level", 1).read(config);

        return new LegacyJobObjective(id, workType, objects, currencyDrop, xpDrop, unlockLevel);
    }

    public boolean isUnlocked(@NotNull Player player, @NotNull JobData jobData) {
        if (player.hasPermission(Perms.BYPASS_OBJECTIVE_UNLOCK_LEVEL)) return true;

        return this.isUnlocked(jobData.getLevel());
    }

    public boolean isUnlocked(int jobLevel) {
        return jobLevel >= this.getUnlockLevel();
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getWorkId() {
        return this.workId;
    }

    @NotNull
    public Set<String> getItems() {
        return this.items;
    }

    @NotNull
    public Map<String, LegacyObjectiveReward> getPaymentMap() {
        return paymentMap;
    }

    @NotNull
    public LegacyObjectiveReward getXPReward() {
        return xpReward;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }
}
