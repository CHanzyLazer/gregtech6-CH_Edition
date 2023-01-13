package gregtechCH.config.adapter;


import com.google.gson.*;
import java.lang.reflect.Type;

import static gregapi.data.CS.OUT;
import static gregtechCH.data.CS_CH.GSON;
import static gregtechCH.config.data.DataMultiTileEntity.Clazz;

/**
 * @author CHanzy
 */
public class ClazzAdapter implements JsonDeserializer<Clazz>, JsonSerializer<Clazz> {
    @Override
    public Clazz deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            OUT.println("Cant deserialize to String, json: " + GSON.toJson(json));
            return null;
        }
        return get(json.getAsString());
    }
    
    @Override
    public JsonElement serialize(Clazz clazz, Type type, JsonSerializationContext jsonSerializationContext) {
        return (clazz == null || clazz.value == null) ? null : new JsonPrimitive(clazz.value.getName());
    }
    
    
    public static Clazz get(String aClassName) {
        if (aClassName == null) return null;
        try {
            Clazz clazz = new Clazz();
            clazz.value = Class.forName(aClassName);
            return clazz;
        } catch (ClassNotFoundException e) {
            OUT.println("Class Not Found: " + aClassName);
            return null;
        }
    }
}
