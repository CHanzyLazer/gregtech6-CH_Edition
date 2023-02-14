package gregtechCH.config.adapter;

import com.google.gson.*;
import gregtechCH.util.ST_CH;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;

import static gregapi.data.CS.OUT;
import static gregapi.data.CS.T;
import static gregtechCH.data.CS_CH.GSON;

/**
 * @author CHanzy
 */
public class ItemAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            OUT.println("Cant deserialize to String, json: " + GSON.toJson(json));
            return null;
        }
        return get(json.getAsString());
    }
    
    @Override
    public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext jsonSerializationContext) {
        String tItemName = ST_CH.uniqueName(item);
        return tItemName == null ? null : new JsonPrimitive(tItemName);
    }
    
    public static ItemStack get(String aItemName) {
        return ST_CH.make(aItemName, T);
    }
}
