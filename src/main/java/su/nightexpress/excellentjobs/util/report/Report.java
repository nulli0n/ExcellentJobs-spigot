package su.nightexpress.excellentjobs.util.report;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class Report {

    private final Map<ReportType, Supplier<Problem>> problems;

    public Report() {
        this.problems = new LinkedHashMap<>();
    }

    @NotNull
    public Set<ReportType> getKnownReports() {
        return new HashSet<>(this.problems.keySet());
    }

    @NotNull
    public List<String> getFullReport() {
        List<String> report = new ArrayList<>();
        this.problems.values().forEach(function -> {
            report.add(function.get().getFormatted());
        });

        return report;
    }

    @NotNull
    public Report add(@NotNull ReportType type, @NotNull Supplier<Problem> function) {
        this.problems.put(type, function);
        return this;
    }

    @Nullable
    public String getProblem(@NotNull ReportType type) {
        var function = this.problems.get(type);
        if (function == null) return null;

        return function.get().getFormatted();
    }
}
