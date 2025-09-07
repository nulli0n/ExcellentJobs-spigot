package su.nightexpress.excellentjobs.grind.table;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.grind.GrindReward;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapter;
import su.nightexpress.excellentjobs.grind.adapter.GrindAdapterFamily;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.wrapper.UniDouble;

import java.util.*;

public class SourceTable implements Writeable {

    public static final SourceTable EMPTY = new SourceTable(Collections.emptyMap());

    private final Map<String, SourceReward> entires;

    public SourceTable() {
        this(new HashMap<>());
    }

    public SourceTable(@NotNull Map<String, SourceReward> entires) {
        this.entires = entires;
    }

    @NotNull
    public static SourceTable read(@NotNull FileConfig config, @NotNull String path) {
        Map<String, SourceReward> entires = new HashMap<>();

        config.getSection(path).forEach(name -> {
            String data = config.getString(path + "." + name);
            if (data == null) return;

            SourceReward entry = SourceReward.deserialize(data);
            entires.put(name, entry);
        });

        return new SourceTable(entires);
    }

    @NotNull
    public static <O> SourceTable fromConverted(@NotNull Map<String, SourceReward> convertedEntries, @NotNull GrindAdapterFamily<O> family) {
        Map<String, SourceReward> entires = new HashMap<>();

        convertedEntries.forEach((name, reward) -> {
            family.getAdapters().forEach(adapter -> {
                Object type = adapter.getTypeByName(name);
                if (type == null) return;

                entires.put(adapter.toFullName(name), reward);
            });
        });

        return new SourceTable(entires);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path);
        this.entires.forEach((name, entry) -> {
            config.set(path + "." + name, entry.serialize());
        });
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public <O> GrindReward rollForEntityOrDefault(@NotNull O entity, @NotNull GrindAdapterFamily<O> family) {
        GrindReward reward = new GrindReward();

        Set<SourceReward> rewards = this.getEntries(entity, family);
        if (rewards.isEmpty()) {
            SourceReward defValue = this.getEntry(Placeholders.DEFAULT);
            return defValue == null ? reward : reward.add(defValue.roll());
        }

        rewards.forEach(other -> reward.add(other.roll()));

        return reward;
    }

    @NotNull
    public <O> GrindReward rollForEntity(@NotNull O entity, @NotNull GrindAdapterFamily<O> family) {
        GrindReward reward = new GrindReward();
        this.getEntries(entity, family).forEach(other -> reward.add(other.roll()));
        return reward;
    }

    public <O> boolean contains(@NotNull O entity, @NotNull GrindAdapterFamily<O> family) {
        return !this.getEntries(entity, family).isEmpty();
    }

    @NotNull
    public <O> Set<SourceReward> getEntries(@NotNull O entity, @NotNull GrindAdapterFamily<O> family) {
        Set<SourceReward> entries = new HashSet<>();

        family.getAdaptersFor(entity).forEach(adapter -> {
            SourceReward entry = this.getEntryByEntity(entity, adapter);
            if (entry == null) return;

            entries.add(entry);
        });

        return entries;
    }

    @Nullable
    public <I, O, E extends GrindAdapter<I, O>> SourceReward getEntryByEntity(@NotNull O entity, @NotNull E adapter) {
        String fullName = adapter.toFullNameOfEntity(entity);
        return fullName == null ? null : this.getEntry(fullName);
    }

    @Nullable
    public <I, O, E extends GrindAdapter<I, O>> SourceReward getEntryByType(@NotNull I type, @NotNull E adapter) {
        return this.getEntry(adapter.toFullNameOfType(type));
    }

    @Nullable
    public <I, O, E extends GrindAdapter<I, O>> SourceReward getEntry(@NotNull String fullName, @NotNull E adapter) {
        return this.getEntry(adapter.toFullName(fullName));
    }

    @Nullable
    public SourceReward getEntry(@NotNull String entry) {
        return this.entires.get(entry);
    }

    @NotNull
    public Map<String, SourceReward> getEntryMap() {
        return this.entires;
    }

    public static class Builder {

        private final Map<String, SourceReward> entires;

        private double scale;

        Builder() {
            this.scale = 1D;
            this.entires = new LinkedHashMap<>();
        }

        @NotNull
        public SourceTable build() {
            return new SourceTable(this.entires);
        }

        @NotNull
        public Builder withScale(double scale) {
            this.scale = scale;
            return this;
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addEntry(@NotNull String name, @NotNull E adapter, double xp, double money) {
            return this.addEntry(name, adapter, xp, money, SourceReward.MAX_CHANCE);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addEntry(@NotNull String name, @NotNull E adapter, double xp, double money, double chance) {
            I type = adapter.getTypeByName(name);
            if (type == null) return this;

            return this.addType(type, adapter, xp, money, chance);
        }


        public <I, O, E extends GrindAdapter<I, O>> Builder addTypesAvg(@NotNull Collection<I> types, @NotNull E adapter, double xpAvg) {
            types.forEach(type -> {
                this.addTypeAvg(type, adapter, xpAvg);
            });
            return this;
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addTypeAvg(@NotNull I type, @NotNull E adapter, double xpAvg) {
            return this.addTypeAvg(type, adapter, xpAvg, xpAvg * 0.2, 0.1);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addTypeAvg(@NotNull I type, @NotNull E adapter, double xpAvg, double moneyAvg, double stepOff) {
            double xpMin = Math.max(0, xpAvg * (1D - stepOff));
            double xpMax = xpAvg * (1D + stepOff);

            double moneyMin = Math.max(0, moneyAvg * (1D - stepOff));
            double moneyMax = moneyAvg * (1D + stepOff);

            return this.addType(type, adapter, xpMin, xpMax, moneyMin, moneyMax, SourceReward.MAX_CHANCE);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addType(@NotNull I type, @NotNull E adapter, double xp, double money) {
            return this.addType(type, adapter, xp, money, SourceReward.MAX_CHANCE);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addType(@NotNull I type, @NotNull E adapter, double xp, double money, double chance) {
            return this.addType(type, adapter, xp, xp, money, money, chance);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addType(@NotNull I type, @NotNull E adapter, double xpMin, double xpMax, double moneyMin, double moneyMax, double chance) {
            return this.addEntry(adapter.toFullNameOfType(type), xpMin, xpMax, moneyMin, moneyMax, chance);
        }



        public <I, O, E extends GrindAdapter<I, O>> Builder addEntity(@NotNull O entity, @NotNull E adapter, double xp, double money) {
            return this.addEntity(entity, adapter, xp, money, SourceReward.MAX_CHANCE);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addEntity(@NotNull O entity, @NotNull E adapter, double xp, double money, double chance) {
            return this.addEntity(entity, adapter, xp, xp, money, money, chance);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addEntity(@NotNull O entity, @NotNull E adapter, double xpMin, double xpMax, double moneyMin, double moneyMax) {
            return this.addEntity(entity, adapter, xpMin, xpMax, moneyMin, moneyMax, SourceReward.MAX_CHANCE);
        }

        public <I, O, E extends GrindAdapter<I, O>> Builder addEntity(@NotNull O entity, @NotNull E adapter, double xpMin, double xpMax, double moneyMin, double moneyMax, double chance) {
            String fullName = adapter.toFullNameOfEntity(entity);
            if (fullName == null) return this;

            return this.addEntry(fullName, xpMin, xpMax, moneyMin, moneyMax, chance);
        }

        @NotNull
        public Builder addDefault(double xp, double money) {
            return this.addEntry(Placeholders.DEFAULT, xp, money);
        }

        @NotNull
        public Builder addEntry(@NotNull String fullName, double xp, double money) {
            return this.addEntry(fullName, xp, money, SourceReward.MAX_CHANCE);
        }

        @NotNull
        public Builder addEntry(@NotNull String fullName, double xp, double money, double chance) {
            return this.addEntry(fullName, xp, xp, money, money, chance);
        }

        @NotNull
        public Builder addEntry(@NotNull String fullName, double xpMin, double xpMax, double moneyMin, double moneyMax) {
            return this.addEntry(fullName, xpMin, xpMax, moneyMin, moneyMax, SourceReward.MAX_CHANCE);
        }

        @NotNull
        public Builder addEntry(@NotNull String fullName, double xpMin, double xpMax, double moneyMin, double moneyMax, double chance) {
            double xpMinScaled = xpMin * this.scale;
            double xpMaxScaled = xpMax * this.scale;

            double moneyMinScaled = moneyMin * this.scale;
            double moneyMaxScaled = moneyMax * this.scale;

            this.entires.put(fullName, new SourceReward(UniDouble.of(xpMinScaled, xpMaxScaled), UniDouble.of(moneyMinScaled, moneyMaxScaled), chance));
            return this;
        }
    }
}
