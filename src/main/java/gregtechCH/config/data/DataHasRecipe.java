package gregtechCH.config.data;

import com.alibaba.fastjson.annotation.JSONField;
import gregtechCH.config.serializer.RecipeObjectDeserializer;

public class DataHasRecipe {
    @JSONField(serialize = false, deserializeUsing = RecipeObjectDeserializer.class)
    public Object[] recipe = null;
    @JSONField(ordinal = 100, name = "recipe", deserialize = false)
    public String[] recipeName = null;
}
