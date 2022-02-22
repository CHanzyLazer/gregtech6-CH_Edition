package gregtechCH.config.machine.kinetic;

import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.data.CS_CH.*;

import static gregtechCH.config.ConfigJson_CH.*;

public class AttributesAxleWood_CH extends AttributesAxle_CH {
    public int nbtFlammability;

    public AttributesAxleWood_CH() {}
    public AttributesAxleWood_CH(OreDictMaterial aMat, Size aPipeSize) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + MA_PIPE_SIZE.get(aPipeSize).mID + 24800;
        stackSize = MA_PIPE_SIZE.get(aPipeSize).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtFlammability = 150;
        nbtSpeedLimit = MA_MACHINE_KINETIC.get(aMat).mNbtOutput;
        nbtPowerLimit = MA_PIPE_SIZE.get(aPipeSize).mNbtPipeBandwidth;
        nbtDiameter = MA_PIPE_SIZE.get(aPipeSize).mNbtDiameter;

        switch (aPipeSize) {
            case SMALL:
                sizeName = "Small";
                recipeObject = new Object[]{
                        "  S", " r ", "S f",
                        'S', OP.stick.dat(aMat)
                };
                recipeNames = new String[]{
                        "  S", " r ", "S f",
                        "S", "OreDictItemData:" + OP.stick.mNameInternal + aMat.mNameInternal
                };
                break;
            case MEDIUM:
                sizeName = "Medium";
                recipeObject = new Object[]{
                        "  S", " r ", "S f",
                        'S', OP.stickLong.dat(aMat)
                };
                recipeNames = new String[]{
                        "  S", " r ", "S f",
                        "S", "OreDictItemData:" + OP.stickLong.mNameInternal + aMat.mNameInternal
                };
                break;
            case LARGE:
                sizeName = "Large";
                recipeObject = new Object[]{
                        "  S", "SrS", "S f",
                        'S', OP.stickLong.dat(aMat)
                };
                recipeNames = new String[]{
                        "  S", "SrS", "S f",
                        "S", "OreDictItemData:" + OP.stickLong.mNameInternal + aMat.mNameInternal
                };
                break;
            case HUGE:
                sizeName = "Huge";
                recipeObject = new Object[]{
                        "rS" ,  "Bf",
                        'S', OD.beamWood,
                        'B', OD.container1000creosote
                };
                recipeNames = new String[]{
                        "rS" ,  "Bf",
                        "S", "OD:" + OD.beamWood.name(),
                        "B", "OD:" + OD.container1000creosote.name()
                };
                break;
        }
    }

}
