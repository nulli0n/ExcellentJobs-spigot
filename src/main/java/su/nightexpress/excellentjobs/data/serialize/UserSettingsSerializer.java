package su.nightexpress.excellentjobs.data.serialize;

import com.google.gson.*;
import su.nightexpress.excellentjobs.data.impl.UserSettings;

import java.lang.reflect.Type;

@Deprecated
public class UserSettingsSerializer implements JsonSerializer<UserSettings>, JsonDeserializer<UserSettings> {

    @Override
    public UserSettings deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        return new UserSettings();
    }

    @Override
    public JsonElement serialize(UserSettings settings, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        return object;
    }
}
