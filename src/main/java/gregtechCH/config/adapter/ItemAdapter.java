package gregtechCH.config.adapter;

import com.google.gson.*;
import gregapi.code.ModData;
import gregapi.util.ST;
import java.lang.reflect.Type;

import static gregapi.data.CS.OUT;
import static gregtechCH.data.CS_CH.GSON;
import static gregtechCH.config.data.DataItemMaterial.Item;

/**
 * @author CHanzy
 */
public class ItemAdapter implements JsonDeserializer<Item>, JsonSerializer<Item> {
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            OUT.println("Cant deserialize to String, json: " + GSON.toJson(json));
            return null;
        }
        return get(json.getAsString());
    }
    
    @Override
    public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
        return (item == null || item.value == null) ? null : new JsonPrimitive(item.name);
    }
    
    public static Item get(String aItemName) {
        if (aItemName == null) return null;
        String[] itemNamePair = aItemName.split(":", 3);
        if (itemNamePair.length < 2) {
            OUT.println("Invalid ItemName: " + aItemName);
            return null;
        }
        Item item = new Item();
        item.value = ST.make(new ModData(itemNamePair[0], ""), itemNamePair[1], 1,
                itemNamePair.length >= 3 ? Integer.parseInt(itemNamePair[2]) : 0);
        item.name = aItemName;
        return item;
    }
}
