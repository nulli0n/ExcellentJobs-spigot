package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.data.impl.JobOrderObjective;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JobOrderDataSerializer implements JsonSerializer<JobOrderData>, JsonDeserializer<JobOrderData> {

    @Override
    public JobOrderData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        Map<String, JobOrderObjective> objectiveMap = context.deserialize(object.get("objectiveMap"), new TypeToken<Map<String, JobOrderObjective>>(){}.getType());
        List<String> rewardCommands = context.deserialize(object.get("rewards"), new TypeToken<List<String>>(){}.getType());
        boolean rewarded = object.get("rewarded").getAsBoolean();
        long expireDate = object.get("expireDate").getAsLong();

        return new JobOrderData(objectiveMap, rewardCommands, rewarded, expireDate);
    }

    @Override
    public JsonElement serialize(JobOrderData orderData, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("objectiveMap", context.serialize(orderData.getObjectiveMap()));
        object.add("rewards", context.serialize(orderData.getRewards()));
        object.addProperty("rewarded", orderData.isRewarded());
        object.addProperty("expireDate", orderData.getExpireDate());

        return object;
    }
}
