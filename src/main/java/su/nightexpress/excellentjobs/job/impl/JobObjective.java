package su.nightexpress.excellentjobs.job.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.action.ActionType;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JobObjective {

    private final String                    id;
    private final ActionType<?, ?>          type;
    private final String                    displayName;
    private final ItemStack                 icon;
    private final Set<String>                    objects;
    private final Map<Currency, ObjectiveReward> paymentMap;
    private final ObjectiveReward                xpReward;
    private final int                            unlockLevel;

    private final boolean specialOrderAllowed;
    private final UniInt specialOrderObjectsAmount;
    private final UniInt specialOrderObjectCount;

    public JobObjective(@NotNull String id,
                        @NotNull ActionType<?, ?> type,
                        @NotNull String displayName,
                        @NotNull ItemStack icon,
                        @NotNull Set<String> objects,
                        @NotNull Map<Currency, ObjectiveReward> paymentMap,
                        @NotNull ObjectiveReward xpReward,
                        int unlockLevel,
                        boolean specialOrderAllowed,
                        UniInt specialOrderObjectsAmount,
                        UniInt specialOrderObjectCount) {
        this.id = id.toLowerCase();
        this.type = type;
        this.displayName = displayName;
        this.icon = icon;
        this.objects = new HashSet<>(objects);
        this.paymentMap = paymentMap;
        this.xpReward = xpReward;
        this.unlockLevel = unlockLevel;
        this.specialOrderAllowed = specialOrderAllowed;
        this.specialOrderObjectsAmount = specialOrderObjectsAmount;
        this.specialOrderObjectCount = specialOrderObjectCount;
    }

    @Nullable
    public static JobObjective read(@NotNull JobsPlugin plugin, @NotNull FileConfig cfg, @NotNull String path, @NotNull String id) {
        //if (!cfg.getBoolean(path + ".Enabled")) return null;

        String typeRaw = cfg.getString(path + ".Type", "null");
        ActionType<?, ?> type = plugin.getActionRegistry().getActionType(typeRaw);
        if (type == null) {
            plugin.warn("Invalid objective type: '" + typeRaw + "'.");
            return null;
        }

        // Add missing currencies for users to know they can use them.
        plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
            if (!cfg.contains(path + ".Payment." + currency.getId())) {
                ObjectiveReward.EMPTY.write(cfg, path + ".Payment." + currency.getId());
            }
        });

        String displayName = cfg.getString(path + ".Display.Name", id);
        ItemStack icon = cfg.getItem(path + ".Display.Icon");

        Set<String> objects = cfg.getStringSet(path + ".Objects")
            .stream().map(String::toLowerCase).collect(Collectors.toSet());

        Map<Currency, ObjectiveReward> currencyDrop = new HashMap<>();
        for (String curId : cfg.getSection(path + ".Payment")) {
            Currency currency = plugin.getCurrencyManager().getCurrency(curId);
            if (currency == null) {
                plugin.error("Invalid currency '" + curId + "' for '" + id + "' objective. File: '" + cfg.getFile().getName() + "'.");
                continue;
            }

            ObjectiveReward objectiveReward = ObjectiveReward.read(cfg, path + ".Payment." + curId);
            currencyDrop.put(currency, objectiveReward);
        }
        ObjectiveReward xpDrop = ObjectiveReward.read(cfg, path + ".Job_XP");

        int unlockLevel = cfg.getInt(path + ".Unlock_Level");

        boolean specialOrderAllowed = false;
        UniInt specialOrderObjectsAmount = null;
        UniInt specialOrderObjectCount = null;

        if (Config.SPECIAL_ORDERS_ENABLED.get()) {
            specialOrderAllowed = ConfigValue.create(path + ".SpecialOrder.Allowed", true).read(cfg);

            specialOrderObjectsAmount = ConfigValue.create(path + ".SpecialOrder.Objects_Amount",
                (cfg2, path2, def) -> UniInt.read(cfg2, path2),
                (cfg2, path2, obj) -> obj.write(cfg2, path2),
                () -> UniInt.of(1, 5)).read(cfg);

            specialOrderObjectCount = ConfigValue.create(path + ".SpecialOrder.Objects_Count",
                (cfg2, path2, def) -> UniInt.read(cfg2, path2),
                (cfg2, path2, obj) -> obj.write(cfg2, path2),
                () -> UniInt.of(100, 500)).read(cfg);
        }

        return new JobObjective(id, type, displayName, icon, objects, currencyDrop, xpDrop, unlockLevel,
            specialOrderAllowed, specialOrderObjectsAmount, specialOrderObjectCount
        );
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Type", this.getType().getName());
        cfg.set(path + ".Display.Name", this.getDisplayName());
        cfg.setItem(path + ".Display.Icon", this.getIcon());

        cfg.set(path + ".Objects", this.getObjects());

        this.getPaymentMap().forEach((currency, objectiveReward) -> {
            objectiveReward.write(cfg, path + ".Payment." + currency.getId());
        });
        this.getXPReward().write(cfg, path + ".Job_XP");

        cfg.set(path + ".Unlock_Level", this.getUnlockLevel());

        if (Config.SPECIAL_ORDERS_ENABLED.get()) {
            cfg.set(path + ".SpecialOrder.Allowed", this.isSpecialOrderAllowed());
            this.getSpecialOrderObjectsAmount().write(cfg, path + ".SpecialOrder.Objects_Amount");
            this.getSpecialOrderObjectCount().write(cfg, path + ".SpecialOrder.Objects_Count");
        }
    }

    public boolean hasObject(@NotNull String name) {
        return this.getObjects().contains(name.toLowerCase()) || this.getObjects().contains(Placeholders.WILDCARD);
    }

    public boolean isUnlocked(@NotNull Player player, @NotNull JobData jobData) {
        if (player.hasPermission(Perms.BYPASS_OBJECTIVE_UNLOCK_LEVEL)) return true;

        return this.isUnlocked(jobData.getLevel());
    }

    public boolean isUnlocked(int skillLevel) {
        return skillLevel >= this.getUnlockLevel();
    }

    public boolean canPay() {
        return !this.getPaymentMap().values().stream().allMatch(ObjectiveReward::isEmpty);// && !this.getXPReward().isEmpty();
    }

    /*@NotNull
    public DropInfo getPaymentInfo(@NotNull Currency currency) {
        return this.getPaymentInfo(currency.getId());
    }*/

    @NotNull
    public ObjectiveReward getPaymentInfo(@NotNull Currency currency) {
        return this.getPaymentMap().getOrDefault(currency, ObjectiveReward.EMPTY);
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public ActionType<?, ?> getType() {
        return type;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
    }

    @NotNull
    public Set<String> getObjects() {
        return objects;
    }

    @NotNull
    public Map<Currency, ObjectiveReward> getPaymentMap() {
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
