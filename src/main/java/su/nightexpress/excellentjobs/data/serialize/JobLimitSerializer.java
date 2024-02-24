package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.data.impl.JobLimitData;

import java.lang.reflect.Type;
import java.util.Map;

public class JobLimitSerializer implements JsonSerializer<JobLimitData>, JsonDeserializer<JobLimitData> {

    @Override
    public JobLimitData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        String job = object.get("jobId").getAsString();
        Map<String, Double> currencyEarned = context.deserialize(object.get("currencyEarned"), new TypeToken<Map<String, Double>>(){}.getType());
        int xpEarned = object.get("xpEarned").getAsInt();
        long since = object.get("expireDate").getAsLong();

        return new JobLimitData(job, currencyEarned, xpEarned, since);
    }

    @Override
    public JsonElement serialize(JobLimitData data, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("jobId", data.getJobId());
        object.add("currencyEarned", context.serialize(data.getCurrencyEarned()));
        object.addProperty("xpEarned", data.getXPEarned());
        object.addProperty("expireDate", data.getExpireDate());

        return object;
    }
}
