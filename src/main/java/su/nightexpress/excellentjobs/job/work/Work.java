package su.nightexpress.excellentjobs.job.work;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.ArrayList;
import java.util.List;

public abstract class Work<E extends Event, O> {

    protected final JobsPlugin plugin;
    protected final String id;
    protected final Class<E> eventClass;

    private WorkListener<E, O> listener;

    private String displayName;
    private List<String> description;
    private NightItem icon;

    public Work(@NotNull JobsPlugin plugin, @NotNull Class<E> eventClass, @NotNull String id) {
        this.plugin = plugin;
        this.eventClass = eventClass;
        this.id = id.toLowerCase();
        this.setDisplayName(StringUtil.capitalizeUnderscored(id));
        this.setDescription(new ArrayList<>());
        this.setIcon(NightItem.fromType(Material.WOODEN_PICKAXE));
    }

    public void register() {
        this.listener = new WorkListener<>(eventClass, this);
        this.plugin.getPluginManager().registerEvent(this.eventClass, this.listener, EventPriority.HIGHEST, this.listener, plugin, true);
    }

    public void unregister() {
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
            this.listener = null;
        }
    }

    @NotNull
    public abstract WorkFormatter<O> getFormatter();

    public void doObjective(@NotNull Player player, @NotNull O object, int amount) {
        this.doObjective(player, object, amount, 0D);
    }

    public void doObjective(@NotNull Player player, @NotNull O object, int amount, double multiplier) {
        if (!JobManager.canWorkHere(player)) return;

        String objectName = this.getObjectName(object);
        String localizedName = this.getObjectLocalizedName(object);
        WorkObjective objective = new WorkObjective(this.id, objectName, localizedName);

        this.plugin.getJobManager().doObjective(player, objective, amount, multiplier);
    }

    public abstract boolean handle(@NotNull E event);

    @Nullable
    public O parse(@NotNull String objectId) {
        return this.getFormatter().parseObject(objectId);
    }

    @NotNull
    public String getObjectName(@NotNull O object) {
        return this.getFormatter().getName(object).toLowerCase();
    }

    @NotNull
    public String getObjectLocalizedName(@NotNull O object) {
        return this.getFormatter().getLocalized(object);
    }

    @NotNull
    public String getObjectLocalizedName(@NotNull String object) {
        if (object.equals(Placeholders.WILDCARD)) {
            return Lang.OTHER_ANY.getString();
        }

        O parsed = this.parse(object);
        return parsed == null ? object : this.getObjectLocalizedName(parsed);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getDisplayName() {
        return this.displayName;
    }

    @NotNull
    public Work<E, O> setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
        return this;
    }

    @NotNull
    public List<String> getDescription() {
        return this.description;
    }

    @NotNull
    public Work<E, O> setDescription(@NotNull List<String> description) {
        this.description = description;
        return this;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    @NotNull
    public Work<E, O> setIcon(@NotNull NightItem icon) {
        this.icon = icon.copy();
        return this;
    }
}
