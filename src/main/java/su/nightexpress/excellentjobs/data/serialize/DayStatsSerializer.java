package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.stats.impl.DayStats;

import java.lang.reflect.Type;
import java.util.Map;

public class DayStatsSerializer implements JsonSerializer<DayStats>, JsonDeserializer<DayStats> {

    @Override
    public DayStats deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        Map<String, Double> currencyEarned = context.deserialize(object.get("currencyAmount"), new TypeToken<Map<String, Double>>(){}.getType());
        long timestamp = object.get("timestamp").getAsLong();

        return new DayStats(currencyEarned, timestamp);
    }

    @Override
    public JsonElement serialize(DayStats stats, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("currencyAmount", context.serialize(stats.getCurrencyEarned()));
        object.addProperty("timestamp", stats.getTimestamp());

        return object;
    }
}
