package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

public class Modifier implements Placeholder {

    private double base;
    private double perLevel;
    private double step;
    private ModifierAction action;

    private final PlaceholderMap placeholderMap;

    public Modifier(double base, double perLevel, double step, @NotNull ModifierAction action) {
        this.setBase(base);
        this.setPerLevel(perLevel);
        this.setStep(step);
        this.setAction(action);

        this.placeholderMap = Placeholders.forModifier(this);
    }

    @NotNull
    public static Modifier add(double base, double perLevel, double step) {
        return new Modifier(base, perLevel, step, ModifierAction.ADD);
    }

    @NotNull
    public static Modifier multiply(double base, double perLevel, double step) {
        return new Modifier(base, perLevel, step, ModifierAction.MULTIPLY);
    }

    @NotNull
    public static Modifier read(@NotNull FileConfig cfg, @NotNull String path, @NotNull Modifier def, String ... comments) {
        return new ConfigValue<>(path,
            (cfg2, path2, def2) -> Modifier.read(cfg2, path2),
            (cfg2, path2, mod) -> mod.write(cfg2, path2),
            def,
            comments
        ).read(cfg);
    }

    @NotNull
    public static Modifier read(@NotNull FileConfig cfg, @NotNull String path) {
        double base = ConfigValue.create(path + ".Base", 0D,
            "Start modifier value."
        ).read(cfg);

        // TODO Per State ?

        double perLevel = ConfigValue.create(path + ".Per_Level", 0D,
            "Additional value calculated by job level step (see below). Formula: <per_level> * <step>"
        ).read(cfg);

        double step = ConfigValue.create(path + ".Step" , 1D,
            "Defines level step for 'Per_Level' value calculation. Formula: <job_level> / <step>"
        ).read(cfg);

        ModifierAction action = ConfigValue.create(path + ".Action", ModifierAction.class, ModifierAction.ADD,
            "Sets action performed between 'Base' and final 'Per_Level' values.",
            "Available types: " + StringUtil.inlineEnum(ModifierAction.class, ", ")
        ).read(cfg);

        return new Modifier(base, perLevel, step, action);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Base", this.getBase());
        cfg.set(path + ".Per_Level", this.getPerLevel());
        cfg.set(path + ".Step", this.getStep());
        cfg.set(path + ".Action", this.getAction().name());
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public double getValue(int level) {
        double step = this.getStep() == 0D ? 1D : Math.floor((double) level / this.getStep());

        return this.action.math(this.getBase(), this.getPerLevel() * step);
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getPerLevel() {
        return this.perLevel;
    }

    public void setPerLevel(double perLevel) {
        this.perLevel = perLevel;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    @NotNull
    public ModifierAction getAction() {
        return this.action;
    }

    public void setAction(@NotNull ModifierAction action) {
        this.action = action;
    }
}
