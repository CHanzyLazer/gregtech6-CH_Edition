package gregtechCH.config.data;

import com.alibaba.fastjson.annotation.JSONField;
import gregtechCH.config.serializer.RecipeObjectDeserializer_CH;

public class DataHasRecipe_CH {
    @JSONField(serialize = false, deserializeUsing = RecipeObjectDeserializer_CH.class)
    public Object[] recipe = null;
    @JSONField(ordinal = 100, name = "recipe", deserialize = false)
    public String[] recipeName = null;
}
