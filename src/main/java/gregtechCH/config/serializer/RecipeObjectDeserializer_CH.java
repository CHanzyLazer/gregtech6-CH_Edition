package gregtechCH.config.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import gregapi.code.ModData;
import gregapi.data.IL;
import gregapi.data.OD;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static gregapi.data.CS.OUT;

public class RecipeObjectDeserializer_CH implements ObjectDeserializer {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        List<String> recipeList = new ArrayList<>();
        parser.parseArray(recipeList);
        return (T) get(recipeList.toArray(new String[0]));
    }

    @Override
    public int getFastMatchToken() {return 0;}

    public Object[] get(String[] aRecipeName) {
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
        Field tField;
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
                            recipeObject[i] = ST.make(new ModData(tRecipePair[0], ""), tRecipePair[1], 1,
                                    tRecipePair.length >= 3 ? Integer.parseInt(tRecipePair[2]) : 0);
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

        return recipeObject;
    }
}
