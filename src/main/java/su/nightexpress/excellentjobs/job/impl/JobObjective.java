package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.work.Work;
import su.nightexpress.excellentjobs.job.work.WorkObjective;
import su.nightexpress.excellentjobs.job.work.WorkRegistry;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JobObjective {

    private static final String EXCLUSIVE_PREFIX = "-";

    private final String                       id;
    private final String                       workId;
    private final String                       displayName;
    private final NightItem                    icon;
    private final Set<String>                  items;
    private final Map<String, ObjectiveReward> paymentMap;
    private final ObjectiveReward              xpReward;
    private final int                          unlockLevel;

    private final boolean specialOrderAllowed;
    private final UniInt specialOrderObjectsAmount;
    private final UniInt specialOrderObjectCount;

    public JobObjective(@NotNull String id,
                        @NotNull String workId,
                        @NotNull String displayName,
                        @NotNull NightItem icon,
                        @NotNull Set<String> items,
                        @NotNull Map<String, ObjectiveReward> paymentMap,
                        @NotNull ObjectiveReward xpReward,
                        int unlockLevel,
                        boolean specialOrderAllowed,
                        UniInt specialOrderObjectsAmount,
                        UniInt specialOrderObjectCount) {
        this.id = id.toLowerCase();
        this.workId = workId;
        this.displayName = displayName;
        this.icon = icon;
        this.items = new HashSet<>(items);
        this.paymentMap = paymentMap;
        this.xpReward = xpReward;
        this.unlockLevel = unlockLevel;
        this.specialOrderAllowed = specialOrderAllowed;
        this.specialOrderObjectsAmount = specialOrderObjectsAmount;
        this.specialOrderObjectCount = specialOrderObjectCount;
    }

    @NotNull
    public static JobObjective read(@NotNull JobsPlugin plugin, @NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String workType = ConfigValue.create(path + ".Type", "null").read(config);
        Work<?, ?> work = WorkRegistry.getByName(workType);

        String displayName = config.getString(path + ".Display.Name", id);
        NightItem icon = config.getCosmeticItem(path + ".Display.Icon");

        Set<String> objects = Lists.modify(config.getStringSet(path + ".Objects"), raw -> {
            if (work == null || raw.equalsIgnoreCase(Placeholders.WILDCARD)) return raw;

            boolean negative = raw.startsWith(EXCLUSIVE_PREFIX);
            if (negative) raw = raw.substring(1);

            String fined = work.reparse(raw);
            return negative ? (EXCLUSIVE_PREFIX + fined) : fined;
        });

        Map<String, ObjectiveReward> currencyDrop = new HashMap<>();
        for (String curId : config.getSection(path + ".Payment")) {
            ObjectiveReward objectiveReward = ObjectiveReward.read(config, path + ".Payment." + curId);
            currencyDrop.put(CurrencyId.reroute(curId), objectiveReward);
        }
        ObjectiveReward xpDrop = ObjectiveReward.read(config, path + ".Job_XP");

        int unlockLevel = ConfigValue.create(path + ".Unlock_Level", 1).read(config);

        boolean specialOrderAllowed = false;
        UniInt specialOrderObjectsAmount = null;
        UniInt specialOrderObjectCount = null;

        if (Config.SPECIAL_ORDERS_ENABLED.get()) {
            specialOrderAllowed = ConfigValue.create(path + ".SpecialOrder.Allowed", true).read(config);

            specialOrderObjectsAmount = ConfigValue.create(path + ".SpecialOrder.Objects_Amount",
                UniInt::read,
                UniInt.of(1, 5)).read(config);

            specialOrderObjectCount = ConfigValue.create(path + ".SpecialOrder.Objects_Count",
                UniInt::read,
                UniInt.of(100, 500)).read(config);
        }

        return new JobObjective(id, workType, displayName, icon, objects, currencyDrop, xpDrop, unlockLevel,
            specialOrderAllowed, specialOrderObjectsAmount, specialOrderObjectCount
        );
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Type", this.workId);
        config.set(path + ".Display.Name", this.getDisplayName());
        config.set(path + ".Display.Icon", this.getIcon());

        config.set(path + ".Objects", this.getItems());

        this.getPaymentMap().forEach((currencyId, objectiveReward) -> {
            objectiveReward.write(config, path + ".Payment." + currencyId);
        });
        this.getXPReward().write(config, path + ".Job_XP");

        config.set(path + ".Unlock_Level", this.getUnlockLevel());

        if (Config.SPECIAL_ORDERS_ENABLED.get()) {
            config.set(path + ".SpecialOrder.Allowed", this.isSpecialOrderAllowed());
            this.getSpecialOrderObjectsAmount().write(config, path + ".SpecialOrder.Objects_Amount");
            this.getSpecialOrderObjectCount().write(config, path + ".SpecialOrder.Objects_Count");
        }
    }

    public boolean isObjective(@NotNull WorkObjective workObjective) {
        return this.workId.equalsIgnoreCase(workObjective.getWorkId()) && this.hasObject(workObjective.getObjectName());
    }

    public boolean isWork(@NotNull Work<?, ?> work) {
        return this.workId.equalsIgnoreCase(work.getId());
    }

    @Nullable
    public Work<?, ?> getWork() {
        return WorkRegistry.getByName(this.workId);
    }

    public boolean hasObject(@NotNull String name) {
        String itemId = name.toLowerCase();

        if (this.items.contains(itemId)) return true;

        if (this.items.contains(Placeholders.WILDCARD)) {
            return !this.items.contains(EXCLUSIVE_PREFIX + itemId);
        }

        return false;
    }

    public boolean isUnlocked(@NotNull Player player, @NotNull JobData jobData) {
        if (player.hasPermission(Perms.BYPASS_OBJECTIVE_UNLOCK_LEVEL)) return true;

        return this.isUnlocked(jobData.getLevel());
    }

    public boolean isUnlocked(int jobLevel) {
        return jobLevel >= this.getUnlockLevel();
    }

    public boolean canPay() {
        return !this.getPaymentMap().values().stream().allMatch(ObjectiveReward::isEmpty);
    }

    @NotNull
    public ObjectiveReward getPaymentInfo(@NotNull Currency currency) {
        return this.getPaymentMap().getOrDefault(currency.getInternalId(), ObjectiveReward.EMPTY);
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
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    @NotNull
    public Set<String> getItems() {
        return this.items;
    }

    @NotNull
    public Map<String, ObjectiveReward> getPaymentMap() {
        return paymentMap;
    }

    @NotNull
    public ObjectiveReward getXPReward() {
        return xpReward;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public boolean isSpecialOrderAllowed() {
        return specialOrderAllowed;
    }

    @NotNull
    public UniInt getSpecialOrderObjectsAmount() {
        return specialOrderObjectsAmount;
    }

    @NotNull
    public UniInt getSpecialOrderObjectCount() {
        return specialOrderObjectCount;
    }
}
