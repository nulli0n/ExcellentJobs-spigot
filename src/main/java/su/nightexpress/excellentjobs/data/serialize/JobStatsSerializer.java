package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.stats.impl.DayStats;
import su.nightexpress.excellentjobs.stats.impl.JobStats;

import java.lang.reflect.Type;
import java.util.Map;

public class JobStatsSerializer implements JsonSerializer<JobStats>, JsonDeserializer<JobStats> {

    @Override
    public JobStats deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        Map<String, DayStats> dayStatsMap = context.deserialize(object.get("dayStats"), new TypeToken<Map<String, DayStats>>(){}.getType());

        return new JobStats(dayStatsMap);
    }

    @Override
    public JsonElement serialize(JobStats jobStats, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("dayStats", context.serialize(jobStats.getDayStatsMap()));

        return object;
    }
}
