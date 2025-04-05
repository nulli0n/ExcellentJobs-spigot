package su.nightexpress.excellentjobs.data;

import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.stats.impl.JobStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class DataQueries {

    public static final Function<ResultSet, Map<String, JobStats>> STATS_LOADER = resultSet -> {
        try {
            return DataHandler.GSON.fromJson(resultSet.getString(DataHandler.COLUMN_STATS.getName()), new TypeToken<Map<String, JobStats>>(){}.getType());
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return Collections.emptyMap();
        }
    };
}
