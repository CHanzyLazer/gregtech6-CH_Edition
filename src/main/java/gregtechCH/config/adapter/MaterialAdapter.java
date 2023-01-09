package gregtechCH.config.adapter;

import com.google.gson.*;
import gregapi.oredict.OreDictMaterial;

import java.lang.reflect.Type;

import static gregapi.data.CS.OUT;
import static gregtechCH.data.CS_CH.GSON;

public class MaterialAdapter implements JsonDeserializer<OreDictMaterial>, JsonSerializer<OreDictMaterial> {
    @Override
    public OreDictMaterial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            OUT.println("Cant deserialize to String, json: " + GSON.toJson(json));
            return null;
        }
        return get(json.getAsString());
    }
    
    @Override
    public JsonElement serialize(OreDictMaterial MT, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(MT.mNameInternal);
    }
    
    
    public OreDictMaterial get(String MTName) {
        if (MTName  == null) return null;
        return OreDictMaterial.get(MTName);
    }
}
