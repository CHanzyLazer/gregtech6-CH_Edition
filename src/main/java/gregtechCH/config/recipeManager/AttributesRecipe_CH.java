package gregtechCH.config.recipeManager;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

public class AttributesRecipe_CH {
    @JSONField(serialize = false, deserializeUsing = RecipeObjectDeserializer_CH.class)
    public Object[] recipeObject;
    @JSONField(deserialize = false, name = "recipeObject")
    public String[] recipeNames;
}
