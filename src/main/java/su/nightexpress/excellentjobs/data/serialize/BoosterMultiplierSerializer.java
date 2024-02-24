package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.excellentjobs.booster.BoosterMultiplier;

import java.lang.reflect.Type;
import java.util.Map;

public class BoosterMultiplierSerializer implements JsonDeserializer<BoosterMultiplier>, JsonSerializer<BoosterMultiplier> {

    @Override
    public BoosterMultiplier deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        Map<String, Double> currencyMod = context.deserialize(object.get("currencyMultiplier"), new TypeToken<Map<String, Double>>(){}.getType());
        double xpMultiplier = object.get("xpMultiplier").getAsDouble();

        return new BoosterMultiplier(currencyMod,  xpMultiplier);
    }

    @Override
    public JsonElement serialize(BoosterMultiplier multiplier, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("currencyMultiplier", context.serialize(multiplier.getCurrencyPercent()));
        object.addProperty("xpMultiplier", multiplier.getXPPercent());

        return object;
    }
}
