package gregtechCH.config.machine;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.AttributesRecipe_CH;
import gregtechCH.config.serializer.MaterialDeserializer_CH;
import gregtechCH.config.serializer.MaterialSerializer_CH;

public abstract class AttributesMachine_CH extends AttributesRecipe_CH {
    @JSONField(serializeUsing = MaterialSerializer_CH.class, deserializeUsing = MaterialDeserializer_CH.class)
    public OreDictMaterial material;

    public int ID;
    public int stackSize;
    public float nbtHardness;
    public float nbtResistance;

}