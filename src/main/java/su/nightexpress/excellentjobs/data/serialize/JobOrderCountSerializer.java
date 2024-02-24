package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import su.nightexpress.excellentjobs.data.impl.JobOrderCount;

import java.lang.reflect.Type;

public class JobOrderCountSerializer implements JsonSerializer<JobOrderCount>, JsonDeserializer<JobOrderCount> {

    @Override
    public JobOrderCount deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        int current = object.get("current").getAsInt();
        int required = object.get("required").getAsInt();

        return new JobOrderCount(current, required);
    }

    @Override
    public JsonElement serialize(JobOrderCount count, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("current", count.getCurrent());
        object.addProperty("required", count.getRequired());

        return object;
    }
}
