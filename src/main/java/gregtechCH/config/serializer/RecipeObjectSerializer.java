package gregtechCH.config.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import gregapi.data.OD;
import gregapi.oredict.OreDictItemData;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class RecipeObjectSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object instanceof Object[]) {
            Object[] recipeObject = (Object[]) object;
            List<String> recipeList = new ArrayList<>();
            int i = 0;
            for (Object subObject : recipeObject) {
                if (i > 2 && i % 2 == 0) {
                    if (subObject instanceof OreDictItemData){
                        recipeList.add("OreDictItemData:" + ((OreDictItemData)subObject).toString());
                    } else
                    if (subObject instanceof Block) {
                        recipeList.add("Block:" + String.valueOf(Block.getIdFromBlock((Block) subObject)));
                    } else
                    if (subObject instanceof ItemStack) {
                        recipeList.add("ItemStack:" +
                                String.valueOf(((ItemStack) subObject).getUnlocalizedName()) + ":" +
                                String.valueOf(((ItemStack) subObject).getItemDamage()));
                    } else
                    if (subObject instanceof OD) {
                        recipeList.add("OD:" + ((OD)subObject).name());
                    } else {
                        recipeList.add("null");
                    }
                } else {
                    recipeList.add(subObject.toString());
                }
                ++i;
            }
            out.write(recipeList);
        } else {
            out.writeNull();
        }
    }
}
