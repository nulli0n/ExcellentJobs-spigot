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
        Map<String, Map<String, Integer>> objectivesCompleted = context.deserialize(object.get("objectivesAmount"), new TypeToken<Map<String, Map<String, Integer>>>(){}.getType());;
        long timestamp = object.get("timestamp").getAsLong();
        int ordersCompleted = object.get("ordersAmount").getAsInt();

        return new DayStats(currencyEarned, objectivesCompleted, timestamp, ordersCompleted);
    }

    @Override
    public JsonElement serialize(DayStats stats, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("currencyAmount", context.serialize(stats.getCurrencyEarned()));
        object.add("objectivesAmount", context.serialize(stats.getObjectivesCompleted()));
        object.addProperty("timestamp", stats.getTimestamp());
        object.addProperty("ordersAmount", stats.getOrdersCompleted());

        return object;
    }
}
