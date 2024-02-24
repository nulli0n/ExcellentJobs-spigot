package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.data.impl.JobOrderCount;
import su.nightexpress.excellentjobs.data.impl.JobOrderObjective;

import java.lang.reflect.Type;
import java.util.Map;

public class JobOrderObjectiveSerializer implements JsonSerializer<JobOrderObjective>, JsonDeserializer<JobOrderObjective> {

    @Override
    public JobOrderObjective deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        String objectiveId = object.get("objectiveId").getAsString();
        Map<String, JobOrderCount> countMap = context.deserialize(object.get("objectiveCountMap"), new TypeToken<Map<String, JobOrderCount>>(){}.getType());

        return new JobOrderObjective(objectiveId, countMap);
    }

    @Override
    public JsonElement serialize(JobOrderObjective objective, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("objectiveId", objective.getObjectiveId());
        object.add("objectiveCountMap", context.serialize(objective.getObjectCountMap()));

        return object;
    }
}
