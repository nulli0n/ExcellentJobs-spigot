package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.function.UnaryOperator;

public class Modifier implements Writeable {

    public static final Modifier EMPTY = new Modifier(0D, 0D, 0D, ModifierAction.ADD);

    private double base;
    private double perLevel;
    private double step;
    private ModifierAction action;

    public Modifier(double base, double perLevel, double step, @NotNull ModifierAction action) {
        this.setBase(base);
        this.setPerLevel(perLevel);
        this.setStep(step);
        this.setAction(action);
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
    public static Modifier read(@NotNull FileConfig config, @NotNull String path) {
        double base = ConfigValue.create(path + ".Base", 0D).read(config);
        double perLevel = ConfigValue.create(path + ".Per_Level", 0D).read(config);
        double step = ConfigValue.create(path + ".Step" , 1D).read(config);
        ModifierAction action = ConfigValue.create(path + ".Action", ModifierAction.class, ModifierAction.ADD).read(config);

        return new Modifier(base, perLevel, step, action);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Base", this.base);
        config.set(path + ".Per_Level", this.perLevel);
        config.set(path + ".Step", this.step);
        config.set(path + ".Action", this.action.name());
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.MODIFIER.replacer(this);
    }

    public double getValue(int level) {
        int whole = this.step <= 0 ? 1 : (int) ((double) level / this.step);

        return this.action.math(this.base, this.perLevel * whole);
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
        return this.step;
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
