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

/**
 * @author CHanzy
 */
public class ItemDataAdapter implements JsonDeserializer<OreDictItemData>, JsonSerializer<OreDictItemData> {
    @Override
    public OreDictItemData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
    public JsonElement serialize(OreDictItemData data, Type type, JsonSerializationContext jsonSerializationContext) {
        if (data == null) return null;
        JsonArray json = new JsonArray();
        if (data.mMaterial != null) json.add(new JsonPrimitive(data.mMaterial.mMaterial.mNameInternal + ":" + (double)data.mMaterial.mAmount/U));
        if (data.mByProducts != null) for (OreDictMaterialStack tMaterialStack : data.mByProducts) json.add(new JsonPrimitive(tMaterialStack.mMaterial.mNameInternal + ":" + (double)tMaterialStack.mAmount/U));
        return json;
    }
    
    public static OreDictItemData get(String[] aDataName) {
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
            return new OreDictItemData(materialStackF, materialStacks);
        } else {
            return null;
        }
    }
}
