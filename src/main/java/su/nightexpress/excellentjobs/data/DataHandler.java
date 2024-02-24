package su.nightexpress.excellentjobs.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.impl.ExpirableBooster;
import su.nightexpress.excellentjobs.data.impl.*;
import su.nightexpress.excellentjobs.data.serialize.*;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.database.AbstractUserDataHandler;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.database.sql.executor.SelectQueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<JobsPlugin, JobUser> {

    private static final SQLColumn COLUMN_DATA     = SQLColumn.of("data", ColumnType.STRING);
    private static final SQLColumn COLUMN_BOOSTERS = SQLColumn.of("boosters", ColumnType.STRING);
    private static final SQLColumn COLUMN_SETTINGS = SQLColumn.of("settings", ColumnType.STRING);

    private final Function<ResultSet, JobUser> userFunction;

    public DataHandler(@NotNull JobsPlugin plugin) {
        super(plugin);

        this.userFunction = resultSet -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Map<String, JobData> jobDataMap = this.gson.fromJson(resultSet.getString(COLUMN_DATA.getName()), new TypeToken<Map<String, JobData>>() {}.getType());
                jobDataMap.values().removeIf(Objects::isNull);

                Map<String, ExpirableBooster> boosters = this.gson.fromJson(resultSet.getString(COLUMN_BOOSTERS.getName()), new TypeToken<Map<String, ExpirableBooster>>(){}.getType());
                if (boosters == null) boosters = new HashMap<>();

                //UserSettings settings = this.gson.fromJson(resultSet.getString(COLUMN_SETTINGS.getName()), new TypeToken<UserSettings>(){}.getType());
                //if (settings == null) settings = new UserSettings();

                return new JobUser(plugin, uuid, name, dateCreated, lastOnline, jobDataMap, boosters, new UserSettings());
            }
            catch (SQLException ex) {
                return null;
            }
        };
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(user -> {
            JobUser fetched = this.getUser(user.getId());
            if (fetched == null) return;

            user.getBoosterMap().clear();
            user.getDataMap().clear();

            user.getBoosterMap().putAll(fetched.getBoosterMap());
            user.getDataMap().putAll(fetched.getDataMap());
        });
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return super.registerAdapters(builder
            .registerTypeAdapter(UserSettings.class, new UserSettingsSerializer())
            .registerTypeAdapter(JobData.class, new JobDataSerializer())
            .registerTypeAdapter(JobLimitData.class, new JobLimitSerializer())
            .registerTypeAdapter(BoosterMultiplier.class, new BoosterMultiplierSerializer())
            .registerTypeAdapter(ExpirableBooster.class, new ExpirableBoosterSerializer())
            .registerTypeAdapter(JobOrderCount.class, new JobOrderCountSerializer())
            .registerTypeAdapter(JobOrderObjective.class, new JobOrderObjectiveSerializer())
            .registerTypeAdapter(JobOrderData.class, new JobOrderDataSerializer())
        );
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(COLUMN_DATA, COLUMN_SETTINGS, COLUMN_BOOSTERS);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull JobUser user) {
        return Arrays.asList(
            COLUMN_DATA.toValue(this.gson.toJson(user.getDataMap())),
            COLUMN_BOOSTERS.toValue(this.gson.toJson(user.getBoosterMap())),
            COLUMN_SETTINGS.toValue(this.gson.toJson(user.getSettings()))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, JobUser> getUserFunction() {
        return this.userFunction;
    }

    @NotNull
    public Map<Job, Map<String, Integer>> getLevels() {
        Map<Job, Map<String, Integer>> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                Map<String, JobData> dataMap = gson.fromJson(resultSet.getString(COLUMN_DATA.getName()), new TypeToken<Map<String, JobData>>(){}.getType());

                this.plugin.getJobManager().getJobs().forEach(job -> {
                    JobData data = dataMap.get(job.getId());
                    if (data == null) return;

                    map.computeIfAbsent(job, k -> new HashMap<>()).put(name, data.getLevel());
                });
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_USER_NAME, COLUMN_DATA)
            .execute(this.getConnector());

        /*map.values().forEach(data -> {
            data.put("MoonBunny", Rnd.get(500));
            data.put("7teen", Rnd.get(1200));
            data.put("har1us", Rnd.get(2000));
            data.put("lPariahl", Rnd.get(800));
            data.put("AquaticFlamesIV", Rnd.get(600));
            data.put("YaZanoZa", Rnd.get(200));
            data.put("ElektroZap", Rnd.get(400));
            data.put("konoos", Rnd.get(100));
            data.put("ApexDragon", Rnd.get(80));
            data.put("FoX", Rnd.get(1337));
        });*/

        return map;
    }

    @NotNull
    public Map<Job, Map<JobState, Integer>> getEmployees() {
        Map<Job, Map<JobState, Integer>> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
            try {
                Map<String, JobData> dataMap = gson.fromJson(resultSet.getString(COLUMN_DATA.getName()), new TypeToken<Map<String, JobData>>(){}.getType());

                dataMap.values().forEach(jobData -> {
                    if (jobData == null || jobData.getState() == JobState.INACTIVE) return;

                    var jobMap = map.computeIfAbsent(jobData.getJob(), k -> new HashMap<>());
                    int has = jobMap.computeIfAbsent(jobData.getState(), k -> 0);
                    jobMap.put(jobData.getState(), has + 1);
                });
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_DATA)
            .execute(this.getConnector());

        /*map.values().forEach(data -> {
            data.put("MoonBunny", Rnd.get(500));
            data.put("7teen", Rnd.get(1200));
            data.put("har1us", Rnd.get(2000));
            data.put("lPariahl", Rnd.get(800));
            data.put("AquaticFlamesIV", Rnd.get(600));
            data.put("YaZanoZa", Rnd.get(200));
            data.put("S_T_I_N_O_L", Rnd.get(400));
            data.put("konoos", Rnd.get(100));
            data.put("ApexDragon", Rnd.get(80));
            data.put("FoX", Rnd.get(1337));
        });*/

        return map;
    }
}
