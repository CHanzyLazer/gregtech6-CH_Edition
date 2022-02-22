package gregtechCH.config;

import com.alibaba.fastjson.annotation.JSONField;
import gregtechCH.config.serializer.RecipeObjectDeserializer_CH;

public class AttributesRecipe_CH {
    @JSONField(serialize = false, deserializeUsing = RecipeObjectDeserializer_CH.class)
    public Object[] recipeObject;
    @JSONField(deserialize = false, name = "recipeObject")
    public String[] recipeNames;
}
