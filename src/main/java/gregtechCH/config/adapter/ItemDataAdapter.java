package gregtechCH.config.adapter;

import com.google.gson.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static gregapi.data.CS.OUT;
import static gregapi.data.CS.U;
import static gregtechCH.data.CS_CH.GSON;
import static gregtechCH.config.data.DataItemMaterial.ItemData;

public class ItemDataAdapter implements JsonDeserializer<ItemData>, JsonSerializer<ItemData> {
    @Override
    public ItemData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) {
            OUT.println("Cant deserialize to String[], json: " + GSON.toJson(json));
            return null;
        }
    
        List<String> dataName = new ArrayList<>();
        for (JsonElement tJson : json.getAsJsonArray()) {
            if (tJson.isJsonPrimitive()) dataName.add(tJson.getAsString());
            else OUT.println("Cant deserialize to String, json: " + GSON.toJson(tJson));
        }
        return get(dataName.toArray(new String[0]));
    }
    
    @Override
    public JsonElement serialize(ItemData data, Type type, JsonSerializationContext jsonSerializationContext) {
        if (data == null || data.value == null || data.name == null) return null;
        JsonArray json = new JsonArray();
        for (String subDataName : data.name) json.add(new JsonPrimitive(subDataName));
        return json;
    }
    
    
    public static ItemData get(String[] aDataName) {
        if (aDataName == null) return null;
        if (aDataName.length >= 1) {
            OreDictMaterialStack materialStackF = null;
            OreDictMaterialStack[] materialStacks = new OreDictMaterialStack[aDataName.length - 1];
            String[] materialPair;
            int i = 0;
            for (String subMaterial : aDataName) {
                if (!subMaterial.isEmpty()) {
                    materialPair = subMaterial.split(":", 2);
                    if (materialPair.length >= 2) {
                        if (i == 0) materialStackF = OM.stack(OreDictMaterial.get(materialPair[0]), Math.round(Double.parseDouble(materialPair[1]) * U));
                        else materialStacks[i-1] = OM.stack(OreDictMaterial.get(materialPair[0]), Math.round(Double.parseDouble(materialPair[1]) * U));
                    } else {
                        OUT.println("Invalid DataName: " + subMaterial);
                        if (i == 0) materialStackF = null;
                        else materialStacks[i-1] = null;
                    }
                } else {
                    OUT.println("Empty DataName");
                    if (i == 0) materialStackF = null;
                    else materialStacks[i-1] = null;
                }
                ++i;
            }
            ItemData data = new ItemData();
            data.value = new OreDictItemData(materialStackF, materialStacks);
            data.name = aDataName;
            return data;
        } else {
            return null;
        }
    }
}
