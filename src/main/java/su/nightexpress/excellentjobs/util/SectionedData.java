package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Numbers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Deprecated
public class SectionedData {

    private static final String DEF_SECTION_DELIMITER = ";";
    private static final String DEF_GLOBAL_DELIMITER = " ";

    private final List<List<String>> data;

    public SectionedData(@NotNull List<List<String>> data) {
        this.data = data;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public static SectionedData deserialize(@NotNull String source) {
        List<List<String>> data = Arrays.stream(source.split(DEF_GLOBAL_DELIMITER)).map(section -> Arrays.asList(section.split(DEF_SECTION_DELIMITER))).toList();

        return new SectionedData(data);
    }

    @NotNull
    public String serialize() {
        return this.data.stream().map(values -> String.join(DEF_SECTION_DELIMITER, values)).collect(Collectors.joining(DEF_GLOBAL_DELIMITER));
    }

    @NotNull
    public Optional<List<String>> section(int index) {
        return index >= this.data.size() ? Optional.empty() : Optional.of(this.data.get(index));
    }

    @Nullable
    public String getAt(int section, int index) {
        return this.section(section).map(list -> index >= list.size() ? null : list.get(index)).orElse(null);
    }

    @NotNull
    public Optional<String> at(int section, int index) {
        return Optional.ofNullable(this.getAt(section, index));
    }

    @NotNull
    public String getString(int section, int index, @NotNull String fallback) {
        return this.at(section, index).orElse(fallback);
    }

    public int getInt(int section, int index, int fallback) {
        return this.at(section, index).map(str -> Numbers.getAnyInteger(str, fallback)).orElse(fallback);
    }

    public double getDouble(int section, int index, double fallback) {
        return this.at(section, index).map(str -> NumberUtil.getDoubleAbs(str, fallback)).orElse(fallback);
    }

    public static class Builder {

        private final List<List<String>> data;

        Builder() {
            this.data = new ArrayList<>();
        }

        @NotNull
        public SectionedData build() {
            return new SectionedData(this.data);
        }

        @NotNull
        public String serialize() {
            return this.build().serialize();
        }

        @NotNull
        public Builder section(double... values) {
            this.data.add(DoubleStream.of(values).boxed().map(String::valueOf).toList());
            return this;
        }

        @NotNull
        public Builder section(int... values) {
            this.data.add(IntStream.of(values).boxed().map(String::valueOf).toList());
            return this;
        }

        @NotNull
        public Builder section(@NotNull String... values) {
            this.data.add(Arrays.asList(values));
            return this;
        }
    }
}
