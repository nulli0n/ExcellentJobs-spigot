package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import su.nightexpress.excellentjobs.api.booster.MultiplierType;
import su.nightexpress.excellentjobs.booster.impl.Booster;

import java.lang.reflect.Type;

public class BoosterSerializer implements JsonDeserializer<Booster>, JsonSerializer<Booster> {

    @Override
    public Booster deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        double payBoost = object.get("payBoost").getAsDouble();
        double xpBoost = object.get("xpBoost").getAsDouble();
        long expireDate = object.get("expireDate").getAsLong();

        Booster booster = new Booster(expireDate);
        booster.setMultiplier(MultiplierType.INCOME, payBoost);
        booster.setMultiplier(MultiplierType.XP, xpBoost);
        return booster;
    }

    @Override
    public JsonElement serialize(Booster booster, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("payBoost", booster.getValue(MultiplierType.INCOME));
        object.addProperty("xpBoost", booster.getValue(MultiplierType.XP));
        object.addProperty("expireDate", booster.getExpireDate());

        return object;
    }
}
