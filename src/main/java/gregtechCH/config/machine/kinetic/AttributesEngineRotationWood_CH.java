package gregtechCH.config.machine.kinetic;

import gregapi.data.MD;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;

import static gregapi.data.CS.T;
import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_KINETIC;

public class AttributesEngineRotationWood_CH extends AttributesEngineRotation_CH {
    public int nbtFlammability;

    public AttributesEngineRotationWood_CH() {}
    public AttributesEngineRotationWood_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 7 + 24800;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtFlammability = 150;
        nbtInput = MA_MACHINE_KINETIC.get(aMat).mNbtInput;
        nbtOutput = nbtInput/2;
        nbtWasteEnergy = T;
        int tAxleID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 24800;
        recipeObject = new Object[]{
                "PSP", "wAL", "GAG",
                'S', OP.gearGtSmall.dat(aMat),
                'G', OP.gearGt.dat(aMat),
                'P', OP.plate.dat(aMat),
                'L', OD.itemLubricantEarly,
                'A', ST.make(MD.GT, "gt.multitileentity", 1, tAxleID)
        };
        recipeNames = new String[]{
                "PSP", "wAL", "GAG",
                "S", "OreDictItemData:" + OP.gearGtSmall.mNameInternal + aMat.mNameInternal,
                "G", "OreDictItemData:" + OP.gearGt.mNameInternal + aMat.mNameInternal,
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricantEarly.name(),
                "A", MD.GT.mID + ":" + "gt.multitileentity:" + tAxleID
        };
    }

}
