package gregtechCH.config.machine.kinetic;

import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.data.CS_CH.Size;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_KINETIC;
import static gregtechCH.config.ConfigJson_CH.MA_PIPE_SIZE;

public class AttributesAxle_CH extends AttributesMachine_CH {
    public String sizeName;
    public long nbtSpeedLimit;
    public long nbtPowerLimit;
    public float nbtDiameter;

    public AttributesAxle_CH() {}
    public AttributesAxle_CH(OreDictMaterial aMat, Size aPipeSize) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + MA_PIPE_SIZE.get(aPipeSize).mID + 24800;
        stackSize = MA_PIPE_SIZE.get(aPipeSize).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtSpeedLimit = MA_MACHINE_KINETIC.get(aMat).mNbtOutput;
        nbtPowerLimit = MA_PIPE_SIZE.get(aPipeSize).mNbtPipeBandwidth;
        nbtDiameter = MA_PIPE_SIZE.get(aPipeSize).mNbtDiameter;

        OreDictPrefix tPrefix = null;
        switch (aPipeSize) {
            case SMALL:
                sizeName = "Small";
                tPrefix = OP.stick;break;
            case MEDIUM:
                sizeName = "Medium";
                tPrefix = OP.stickLong;break;
            case LARGE:
                sizeName = "Large";
                tPrefix = OP.ingotDouble;break;
            case HUGE:
                sizeName = "Huge";
                tPrefix = OP.ingotQuadruple;break;
        }
        recipeObject = new Object[]{
                "  S", " h ", "S f",
                'S', tPrefix.dat(aMat)
        };
        recipeNames = new String[]{
                "  S", " h ", "S f",
                "S", "OreDictItemData:" + tPrefix.mNameInternal + aMat.mNameInternal
        };
    }

}
