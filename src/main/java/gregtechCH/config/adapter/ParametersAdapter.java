package gregtechCH.config.adapter;

import com.google.gson.*;
import gregtechCH.util.UT_CH;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Type;

import static gregapi.data.CS.OUT;
import static gregtechCH.data.CS_CH.GSON;

/**
 * @author CHanzy
 */
public class ParametersAdapter implements JsonDeserializer<NBTTagCompound>, JsonSerializer<NBTTagCompound> {
    @Override
    public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            OUT.println("Cant deserialize to JsonObject, json: " + GSON.toJson(json));
            return null;
        }
        return get(json.getAsJsonObject());
    }
    
    @Override
    public JsonElement serialize(NBTTagCompound nbt, Type type, JsonSerializationContext jsonSerializationContext) {
        return nbt != null ? UT_CH.NBT.NBT2Json(nbt) : null;
    }
    
    
    public NBTTagCompound get(JsonObject aJson) {
        if (aJson == null) return null;
        NBTBase nbt = UT_CH.NBT.json2NBT(aJson);
        return nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : null;
    }
}
