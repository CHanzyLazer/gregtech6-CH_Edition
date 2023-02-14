package gregtechCH.config.adapter;

import com.google.gson.*;
import gregapi.code.ModData;
import gregapi.data.IL;
import gregapi.data.OD;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static gregapi.data.CS.OUT;
import static gregtechCH.data.CS_CH.GSON;
import static gregtechCH.config.data.DataMultiTileEntity.Recipe;

/**
 * @author CHanzy
 */
public class RecipeAdapter implements JsonDeserializer<Recipe>, JsonSerializer<Recipe> {
    @Override
    public Recipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) {
            OUT.println("Cant deserialize to String[], json: " + GSON.toJson(json));
            return null;
        }
    
        List<String> recipeName = new ArrayList<>();
        for (JsonElement tJson : json.getAsJsonArray()) {
            if (tJson.isJsonPrimitive()) recipeName.add(tJson.getAsString());
            else OUT.println("Cant deserialize to String, json: " + GSON.toJson(tJson));
        }
        return get(recipeName.toArray(new String[0]));
    }
    
    @Override
    public JsonElement serialize(Recipe recipe, Type type, JsonSerializationContext jsonSerializationContext) {
        if (recipe == null || recipe.value == null || recipe.name == null) return null;
        JsonArray json = new JsonArray();
        for (String subRecipeName : recipe.name) json.add(new JsonPrimitive(subRecipeName));
        return json;
    }
    
    
    public Recipe get(String[] aRecipeName) {
        if (aRecipeName == null) return null;
        String[] tRecipePair;
        // 先判断字母索引有多长，顺便可以直接排除不合理的合成表
        int recipeSize = 0;
        for (String subRecipe : aRecipeName) {
            if (!subRecipe.isEmpty()) {
                tRecipePair = subRecipe.split(":", 2);
                if (tRecipePair.length >= 2) break;
                if (recipeSize >= 4) {
                    OUT.println("Invalid Recipe: " + Arrays.toString(aRecipeName));
                    return null;
                }
            }
            ++recipeSize;
        }
        --recipeSize;

        Object[] recipeObject = new Object[aRecipeName.length];
        int i = 0;
        for (String subRecipe : aRecipeName) {
            if (!subRecipe.isEmpty()) {
                if ((i >= recipeSize) && ((i-recipeSize+1) % 2 == 0)) {
                    tRecipePair = subRecipe.split(":", 2);
                    if (tRecipePair.length >= 2) {
                        if (Objects.equals(tRecipePair[0], "OreDictItemData")){
                            recipeObject[i] = OM.data(tRecipePair[1]);
                        } else
                        if (Objects.equals(tRecipePair[0], "OD")) {
                            try {
                                recipeObject[i] = OD.valueOf(tRecipePair[1]);
                            } catch (IllegalArgumentException e) {
                                recipeObject[i] = null;
                                e.printStackTrace();
                            }
                        } else
                        if (Objects.equals(tRecipePair[0], "IL")) {
                            try {
                                recipeObject[i] = IL.valueOf(tRecipePair[1]);
                            } catch (IllegalArgumentException e) {
                                recipeObject[i] = null;
                                e.printStackTrace();
                            }
                        } else
                        if (Objects.equals(tRecipePair[0], "Blocks")) {
                            recipeObject[i] = Block.blockRegistry.getObject(tRecipePair[1]);
                        } else
                        if (Objects.equals(tRecipePair[0], "Items")) {
                            recipeObject[i] = Item.itemRegistry.getObject(tRecipePair[1]);
                        } else
                        if (Objects.equals(tRecipePair[0], "ore")) {
                            recipeObject[i] = tRecipePair[1];
                        } else {
                            tRecipePair = subRecipe.split(":", 3);
                            recipeObject[i] = ST.make(new ModData(tRecipePair[0], ""), tRecipePair[1], 1, tRecipePair.length >= 3 ? Integer.parseInt(tRecipePair[2]) : 0);
                        }
                    } else {
                        OUT.println("Invalid Recipe: " + Arrays.toString(aRecipeName));
                        return null;
                    }
                } else
                if (i >= recipeSize) {
                    recipeObject[i] = subRecipe.charAt(0);
                } else {
                    recipeObject[i] = subRecipe;
                }
            } else {
                OUT.println("Invalid Recipe: " + Arrays.toString(aRecipeName));
                return null;
            }
            ++i;
        }
        Recipe recipe = new Recipe();
        recipe.value = recipeObject;
        recipe.name = aRecipeName;
        return recipe;
    }
}
