package gregtechCH.config.machine;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import gregtechCH.config.serializer.MaterialDeserializer_CH;
import gregtechCH.config.serializer.MaterialSerializer_CH;

public abstract class AttributesMotor_CH extends AttributesMachine_CH {
    @JSONField(serializeUsing = MaterialSerializer_CH.class, deserializeUsing = MaterialDeserializer_CH.class)
    public OreDictMaterial rotorMaterial;

    public int nbtEfficiency;
    public long nbtOutput;
    public long nbtPreheatEnergy;
    public long nbtPreheatCost;
    public long nbtCooldownRate;

    protected void setEnergy() {
        nbtPreheatEnergy = nbtOutput * 4000;
        nbtPreheatCost = UT.Code.divup(nbtOutput, 16);
        nbtCooldownRate = nbtOutput;
    }
}
