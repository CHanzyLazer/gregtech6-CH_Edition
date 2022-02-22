package gregtechCH.config.machine.kinetic;

import gregapi.data.MD;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;
import gregtechCH.config.machine.AttributesMachine_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_KINETIC;

public class AttributesGearBox_CH extends AttributesMachine_CH {
    public long nbtInput;

    public AttributesGearBox_CH() {}
    public AttributesGearBox_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 9 + 24800;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtInput = MA_MACHINE_KINETIC.get(aMat).mNbtInput * 2;
        int tAxleID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 24800;
        recipeObject = new Object[]{
                "wAL", "AMA",
                'M', OP.casingMachine.dat(aMat),
                'L', OD.itemLubricantEarly,
                'A', ST.make(MD.GT, "gt.multitileentity", 1, tAxleID)
        };
        recipeNames = new String[]{
                "wAL", "AMA",
                "M", "OreDictItemData:" + OP.casingMachine.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricantEarly.name(),
                "A", MD.GT.mID + ":" + "gt.multitileentity:" + tAxleID
        };
    }
}