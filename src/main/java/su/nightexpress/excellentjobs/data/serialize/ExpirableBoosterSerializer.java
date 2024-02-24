package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;
import su.nightexpress.excellentjobs.booster.impl.ExpirableBooster;

import java.lang.reflect.Type;

public class ExpirableBoosterSerializer implements JsonSerializer<ExpirableBooster>, JsonDeserializer<ExpirableBooster> {

    @Override
    public ExpirableBooster deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        BoosterMultiplier multiplier = context.deserialize(object.get("multiplier"), new TypeToken<BoosterMultiplier>(){}.getType());
        long expireDate = object.get("expireDate").getAsLong();

        return new ExpirableBooster(multiplier, expireDate);
    }

    @Override
    public JsonElement serialize(ExpirableBooster booster, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("multiplier", context.serialize(booster.getMultiplier()));
        object.addProperty("expireDate", booster.getExpireDate());

        return object;
    }
}
