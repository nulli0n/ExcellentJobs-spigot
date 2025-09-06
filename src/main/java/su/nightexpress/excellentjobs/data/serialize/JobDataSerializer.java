package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobLimitData;
import su.nightexpress.excellentjobs.data.impl.JobOrderData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.nightcore.util.StringUtil;

import java.lang.reflect.Type;
import java.util.Set;

public class JobDataSerializer implements JsonDeserializer<JobData>, JsonSerializer<JobData> {

    @Override
    public JobData deserialize(JsonElement json, Type type, JsonDeserializationContext contex) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        String jobId = object.get("job").getAsString();
        String jobState = object.get("state").getAsString();
        JobState state = StringUtil.getEnum(jobState, JobState.class).orElse(JobState.INACTIVE);
        int level = object.get("level").getAsInt();
        int xp = object.get("xp").getAsInt();
        long cooldown = object.get("cooldown") == null ? 0L : object.get("cooldown").getAsLong();

        Job job = JobsAPI.getJobById(jobId);
        if (job == null) return null;

		JobLimitData limitData = contex.deserialize(object.get("dailyLimits"), new TypeToken<JobLimitData>(){}.getType());
        JobOrderData orderData = contex.deserialize(object.get("orderData"), new TypeToken<JobOrderData>(){}.getType());
        if (orderData == null) orderData = JobOrderData.empty();

        long nextOrderDate = object.get("nextOrderDate").getAsLong();

        Set<Integer> obtainedLevelRewards = contex.deserialize(object.get("obtainedLevelRewards"), new TypeToken<Set<Integer>>(){}.getType());

        return new JobData(job, state, level, xp, cooldown, limitData, orderData, nextOrderDate, obtainedLevelRewards);
    }

    @Override
    public JsonElement serialize(JobData data, Type type, JsonSerializationContext contex) {

        JsonObject object = new JsonObject();
        object.addProperty("job", data.getJob().getId());
        object.addProperty("state", data.getState().name());
        object.addProperty("level", data.getLevel());
        object.addProperty("xp", data.getXP());
        object.addProperty("cooldown", data.getCooldown());
        object.add("dailyLimits", contex.serialize(data.getLimitData()));
        object.add("orderData", contex.serialize(data.getOrderData()));
        object.addProperty("nextOrderDate", data.getNextOrderDate());
        object.add("obtainedLevelRewards", contex.serialize(data.getClaimedLevelRewards()));

        return object;
    }
}
