package su.nightexpress.excellentjobs.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Colorizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Report {

    private static final String PREFIX_PROBLEM = RED.enclose("✘ ");
    private static final String PREFIX_GOOD = GREEN.enclose("✔ ");
    private static final String PREFIX_WARN = ORANGE.enclose("❗ ");

    public enum Type {
        PROBLEM, WARN, GOOD
    }

    private final Map<Type, List<String>> reportMap;

    public Report() {
        this.reportMap = new HashMap<>();
    }

    public boolean hasProblems() {
        return !this.getProblems().isEmpty();
    }

    public boolean hasWarns() {
        return !this.getWarns().isEmpty();
    }

    @NotNull
    public List<String> getFullReport() {
        List<String> report = new ArrayList<>();
        report.addAll(this.getProblems());
        report.addAll(this.getWarns());
        report.addAll(this.getGoods());
        return report;
    }

    @NotNull
    public Report addProblem(@NotNull String text) {
        return this.addReport(Type.PROBLEM, text);
    }

    @NotNull
    public Report addWarn(@NotNull String text) {
        return this.addReport(Type.WARN, text);
    }

    @NotNull
    public Report addGood(@NotNull String text) {
        return this.addReport(Type.GOOD, text);
    }

    @NotNull
    public Report addReport(@NotNull Type type, @NotNull String text) {
        this.getReports(type).add(text);
        return this;
    }

    @NotNull
    public List<String> getProblems() {
        return this.getReports(Type.PROBLEM).stream().map(Report::problem).toList();
    }

    @NotNull
    public List<String> getWarns() {
        return this.getReports(Type.WARN).stream().map(Report::warn).toList();
    }

    @NotNull
    public List<String> getGoods() {
        return this.getReports(Type.GOOD).stream().map(Report::good).toList();
    }

    @NotNull
    public List<String> getReports(@NotNull Type type) {
        return this.reportMap.computeIfAbsent(type, k -> new ArrayList<>());
    }

    @NotNull
    public static String problem(@NotNull String text) {
        return PREFIX_PROBLEM + GRAY.enclose(text);
    }

    @NotNull
    public static String good(@NotNull String text) {
        return PREFIX_GOOD + GRAY.enclose(text);
    }

    @NotNull
    public static String warn(@NotNull String text) {
        return PREFIX_WARN + GRAY.enclose(text);
    }
}
