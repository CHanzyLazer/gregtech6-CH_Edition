package gregtechCH.config.recipeManager;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import gregapi.code.ModData;
import gregapi.data.OD;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeObjectDeserializer_CH implements ObjectDeserializer {

    @Override
    public Object[] deserialze(DefaultJSONParser parser, Type type, Object fieldName){
        List<String> recipeList = new ArrayList<>();
        parser.parseArray(recipeList);
        Object[] recipeObject = new Object[recipeList.size()];

        Field tField;
        String[] tRecipePair;
        int i = 0;
        for (String subRecipe : recipeList) {
            if (!subRecipe.isEmpty()) {
                if (i > 2 && i % 2 == 0) {
                    tRecipePair = subRecipe.split(":");
                    if (tRecipePair.length >= 2) {
                        if (Objects.equals(tRecipePair[0], "OreDictItemData")){
                            recipeObject[i] = OM.data(tRecipePair[1]);
                        } else
                        if (Objects.equals(tRecipePair[0], "OD")) {
                            recipeObject[i] = OD.valueOf(tRecipePair[1]);
                        } else
                        if (Objects.equals(tRecipePair[0], "Blocks")) {
                            try {
                                tField = Blocks.class.getField(tRecipePair[1]);
                                recipeObject[i] = tField.get(null);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                recipeObject[i] = null;
                                e.printStackTrace();
                            }
                        } else
                        if (Objects.equals(tRecipePair[0], "Items")) {
                            try {
                                tField = Items.class.getField(tRecipePair[1]);
                                recipeObject[i] = tField.get(null);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                recipeObject[i] = null;
                                e.printStackTrace();
                            }
                        } else
                        if (Objects.equals(tRecipePair[0], "ore")) {
                            recipeObject[i] = tRecipePair[1];
                        } else {
                            recipeObject[i] = ST.make(new ModData(tRecipePair[0], ""), tRecipePair[1], 1,
                                    tRecipePair.length >= 3 ? Integer.parseInt(tRecipePair[2]) : 0);
                        }
                    } else {
                        recipeObject[i] = null;
                    }
                } else
                if (i > 2) {
                    recipeObject[i] = subRecipe.charAt(0);
                } else {
                    recipeObject[i] = subRecipe;
                }
            } else {
                recipeObject[i] = null;
            }
            ++i;
        }

        return recipeObject;
    }

    @Override
    public int getFastMatchToken() {return 0;}
}
