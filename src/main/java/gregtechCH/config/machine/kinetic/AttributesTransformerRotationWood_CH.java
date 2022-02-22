package gregtechCH.config.machine.kinetic;

import gregapi.data.MD;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_KINETIC;

public class AttributesTransformerRotationWood_CH extends AttributesTransformerRotation_CH {
    public int nbtFlammability;

    public AttributesTransformerRotationWood_CH() {}
    public AttributesTransformerRotationWood_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 8 + 24800;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtFlammability = 150;
        nbtOutput = MA_MACHINE_KINETIC.get(aMat).mNbtOutput/4;
        nbtMultiplier = 4;
        int tAxleID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 24800;
        recipeObject = new Object[]{
                "ASL", "SGS", "PSA",
                'S', OP.gearGtSmall.dat(aMat),
                'G', OP.gearGt.dat(aMat),
                'P', OP.plate.dat(aMat),
                'L', OD.itemLubricantEarly,
                'A', ST.make(MD.GT, "gt.multitileentity", 1, tAxleID)
        };
        recipeNames = new String[]{
                "ASL", "SGS", "PSA",
                "S", "OreDictItemData:" + OP.gearGtSmall.mNameInternal + aMat.mNameInternal,
                "G", "OreDictItemData:" + OP.gearGt.mNameInternal + aMat.mNameInternal,
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricantEarly.name(),
                "A", MD.GT.mID + ":" + "gt.multitileentity:" + tAxleID
        };
    }
}
