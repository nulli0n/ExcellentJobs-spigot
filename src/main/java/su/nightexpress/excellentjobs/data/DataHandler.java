package su.nightexpress.excellentjobs.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.booster.impl.Booster;
import su.nightexpress.excellentjobs.data.impl.*;
import su.nightexpress.excellentjobs.data.serialize.*;
import su.nightexpress.excellentjobs.stats.impl.DayStats;
import su.nightexpress.excellentjobs.stats.impl.JobStats;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.db.AbstractUserDataManager;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.db.sql.column.ColumnType;
import su.nightexpress.nightcore.db.sql.query.impl.SelectQuery;
import su.nightexpress.nightcore.db.sql.query.type.ValuedQuery;
import su.nightexpress.nightcore.db.sql.util.WhereOperator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<JobsPlugin, JobUser> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(UserSettings.class, new UserSettingsSerializer())
        .registerTypeAdapter(JobData.class, new JobDataSerializer())
        .registerTypeAdapter(JobLimitData.class, new JobLimitSerializer())
        .registerTypeAdapter(Booster.class, new BoosterSerializer())
        .registerTypeAdapter(JobOrderCount.class, new JobOrderCountSerializer())
        .registerTypeAdapter(JobOrderObjective.class, new JobOrderObjectiveSerializer())
        .registerTypeAdapter(JobOrderData.class, new JobOrderDataSerializer())
        .registerTypeAdapter(DayStats.class, new DayStatsSerializer())
        .registerTypeAdapter(JobStats.class, new JobStatsSerializer())
        .create();

    static final Column COLUMN_DATA     = Column.of("data", ColumnType.STRING);
    static final Column COLUMN_BOOSTS   = Column.of("boosts", ColumnType.STRING);
    static final Column COLUMN_SETTINGS = Column.of("settings", ColumnType.STRING);
    static final Column COLUMN_STATS    = Column.of("stats", ColumnType.STRING);

    public DataHandler(@NotNull JobsPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    protected Function<ResultSet, JobUser> createUserFunction() {
        return resultSet -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Map<String, JobData> jobDataMap = GSON.fromJson(resultSet.getString(COLUMN_DATA.getName()), new TypeToken<Map<String, JobData>>(){}.getType());
                jobDataMap.values().removeIf(Objects::isNull);

                Map<String, Booster> boosters = GSON.fromJson(resultSet.getString(COLUMN_BOOSTS.getName()), new TypeToken<Map<String, Booster>>(){}.getType());
                if (boosters == null) boosters = new HashMap<>();

                Map<String, JobStats> statsMap = new HashMap<>(); // Lazy load

                //UserSettings settings = GSON.fromJson(resultSet.getString(COLUMN_SETTINGS.getName()), new TypeToken<UserSettings>(){}.getType());
                //if (settings == null) settings = new UserSettings();

                JobUser user = new JobUser(uuid, name, dateCreated, lastOnline, jobDataMap, boosters, statsMap, new UserSettings());

                // Update missing jobs.
                plugin.getJobManager().getJobs().forEach(user::getData);

                return user;
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        };
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(user -> {
            if (user.isAutoSavePlanned() || !user.isAutoSyncReady()) return;

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
        return builder;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.dropColumn(this.tableUsers, "boosters");
        this.addColumn(this.tableUsers, COLUMN_STATS, "{}");
        this.addColumn(this.tableUsers, COLUMN_BOOSTS, "{}");
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_DATA);
        columns.add(COLUMN_SETTINGS);
        columns.add(COLUMN_BOOSTS);
        columns.add(COLUMN_STATS);
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<JobUser> query) {
        query.column(COLUMN_DATA);
        query.column(COLUMN_SETTINGS);
        query.column(COLUMN_BOOSTS);
        //query.column(COLUMN_STATS);
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, JobUser> query) {
        query.setValue(COLUMN_DATA, user -> GSON.toJson(user.getDataMap()));
        query.setValue(COLUMN_BOOSTS, user -> GSON.toJson(user.getBoosterMap()));
        query.setValue(COLUMN_SETTINGS, user -> GSON.toJson(user.getSettings()));
        query.setValue(COLUMN_STATS, user -> GSON.toJson(user.getStatsMap()));
    }

    @NotNull
    public Map<String, JobStats> getStats(@NotNull UUID playerId) {
        var map = this.selectFirst(this.tableUsers, DataQueries.STATS_LOADER, query -> {
            query.column(COLUMN_STATS).where(COLUMN_USER_ID, WhereOperator.EQUAL, playerId.toString());
        });

        return map == null ? Collections.emptyMap() : map;
    }

//    @NotNull
//    public Map<Job, Map<String, Integer>> getLevels() {
//        Map<Job, Map<String, Integer>> map = new HashMap<>();
//
//        Function<ResultSet, Void> function = resultSet -> {
//            try {
//                String name = resultSet.getString(COLUMN_USER_NAME.getName());
//                Map<String, JobData> dataMap = gson.fromJson(resultSet.getString(COLUMN_DATA.getName()), new TypeToken<Map<String, JobData>>(){}.getType());
//
//                this.plugin.getJobManager().getJobs().forEach(job -> {
//                    JobData data = dataMap.get(job.getId());
//                    if (data == null) return;
//
//                    map.computeIfAbsent(job, k -> new HashMap<>()).put(name, data.getLevel());
//                });
//            }
//            catch (SQLException exception) {
//                exception.printStackTrace();
//            }
//            return null;
//        };
//
//        SelectQueryExecutor.builder(this.tableUsers, function)
//            .columns(COLUMN_USER_NAME, COLUMN_DATA)
//            .execute(this.getConnector());
//
//        return map;
//    }

//    @NotNull
//    public Map<Job, Map<JobState, Integer>> getEmployees() {
//        Map<Job, Map<JobState, Integer>> map = new HashMap<>();
//
//        Function<ResultSet, Void> function = resultSet -> {
//            try {
//                Map<String, JobData> dataMap = gson.fromJson(resultSet.getString(COLUMN_DATA.getName()), new TypeToken<Map<String, JobData>>(){}.getType());
//
//                dataMap.values().forEach(jobData -> {
//                    if (jobData == null || jobData.getState() == JobState.INACTIVE) return;
//
//                    var jobMap = map.computeIfAbsent(jobData.getJob(), k -> new HashMap<>());
//                    int has = jobMap.computeIfAbsent(jobData.getState(), k -> 0);
//                    jobMap.put(jobData.getState(), has + 1);
//                });
//            }
//            catch (SQLException exception) {
//                exception.printStackTrace();
//            }
//            return null;
//        };
//
//        SelectQueryExecutor.builder(this.tableUsers, function)
//            .columns(COLUMN_DATA)
//            .execute(this.getConnector());
//
//        return map;
//    }
}
