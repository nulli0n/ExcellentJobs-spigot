package su.nightexpress.excellentjobs.job.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.grind.table.GrindTable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.integration.currency.CurrencyId;

public class JobObjective implements Writeable {

    private final String currencyId;
    private final String grindTypeId;
    private final GrindTable grindTable;

    public JobObjective(@NotNull String currencyId, @NotNull String grindTypeId, @NotNull GrindTable grindTable) {
        this.currencyId = currencyId;
        this.grindTypeId = grindTypeId;
        this.grindTable = grindTable;
    }

    @NotNull
    public static JobObjective forVault(@NotNull String grindTypeId, @NotNull GrindTable table) {
        return new JobObjective(CurrencyId.VAULT, grindTypeId, table);
    }

    /*public static JobObjective read(@NotNull FileConfig config, @NotNull String path) {
        String currencyId = ConfigValue.create(path + ".Currencu", CurrencyId.VAULT).read(config);
        String grindTypeId = ConfigValue.create(path + ".Type", "null").read(config);
        Gr
    }*/


    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Currency", this.currencyId);
        config.set(path + ".Type", this.grindTypeId);
        config.set(path + ".SourceTable", this.grindTable);
    }

    @NotNull
    public String getCurrencyId() {
        return this.currencyId;
    }

    @NotNull
    public String getGrindTypeId() {
        return this.grindTypeId;
    }

    @NotNull
    public GrindTable getGrindTable() {
        return this.grindTable;
    }
}
