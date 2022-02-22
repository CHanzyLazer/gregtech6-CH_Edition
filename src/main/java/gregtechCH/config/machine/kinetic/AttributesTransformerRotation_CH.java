package gregtechCH.config.machine.kinetic;

import gregapi.data.MD;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;
import gregtechCH.config.machine.AttributesMachine_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_KINETIC;

public class AttributesTransformerRotation_CH extends AttributesMachine_CH {
    public long nbtOutput;
    public long nbtMultiplier;

    public AttributesTransformerRotation_CH() {}
    public AttributesTransformerRotation_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 8 + 24800;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtOutput = MA_MACHINE_KINETIC.get(aMat).mNbtOutput/4;
        nbtMultiplier = 4;
        int tAxleID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 24800;
        recipeObject = new Object[]{
                "ASL", "SGS", "MSA",
                'S', OP.gearGtSmall.dat(aMat),
                'G', OP.gearGt.dat(aMat),
                'M', OP.casingMachine.dat(aMat),
                'L', OD.itemLubricantEarly,
                'A', ST.make(MD.GT, "gt.multitileentity", 1, tAxleID)
        };
        recipeNames = new String[]{
                "ASL", "SGS", "MSA",
                "S", "OreDictItemData:" + OP.gearGtSmall.mNameInternal + aMat.mNameInternal,
                "G", "OreDictItemData:" + OP.gearGt.mNameInternal + aMat.mNameInternal,
                "M", "OreDictItemData:" + OP.casingMachine.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricantEarly.name(),
                "A", MD.GT.mID + ":" + "gt.multitileentity:" + tAxleID
        };
    }
}
