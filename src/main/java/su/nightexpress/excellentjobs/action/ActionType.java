package su.nightexpress.excellentjobs.action;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

public class ActionType<E extends Event, O> {

    private final String             name;
    private final ObjectFormatter<O> objectFormatter;
    private final EventHelper<E, O>  eventHelper;

    private String displayName;

    public ActionType(@NotNull String name,
                      @NotNull ObjectFormatter<O> objectFormatter,
                      @NotNull EventHelper<E, O> eventHelper) {
        this.name = name.toLowerCase();
        this.objectFormatter = objectFormatter;
        this.eventHelper = eventHelper;
        this.setDisplayName(StringUtil.capitalizeUnderscored(name));
    }

    @NotNull
    public static <E extends Event, O> ActionType<E, O> create(@NotNull String name,
                                                               @NotNull ObjectFormatter<O> objectFormatter,
                                                               @NotNull EventHelper<E, O> eventHelper) {
        return new ActionType<>(name, objectFormatter, eventHelper);
    }

    public boolean loadSettings(@NotNull JobsPlugin plugin) {
        FileConfig config = plugin.getLang();
        String path = "Job.Action_Types." + this.getName();
        this.setDisplayName(ConfigValue.create(path + ".DisplayName", this.getDisplayName()).read(config));
        return true;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public String getObjectName(@NotNull O object) {
        return this.objectFormatter.getName(object).toLowerCase();
    }

    @NotNull
    public String getObjectLocalizedName(@NotNull O object) {
        return this.objectFormatter.getLocalizedName(object);
    }

    @NotNull
    public String getObjectLocalizedName(@NotNull String object) {
        if (object.equals(Placeholders.WILDCARD)) {
            return Lang.OTHER_ANY.getString();
        }
        return this.objectFormatter.getLocalizedName(object);
    }

    @NotNull
    public EventHelper<E, O> getEventHelper() {
        return eventHelper;
    }

    @NotNull
    public ObjectFormatter<O> getObjectFormatter() {
        return objectFormatter;
    }
}
