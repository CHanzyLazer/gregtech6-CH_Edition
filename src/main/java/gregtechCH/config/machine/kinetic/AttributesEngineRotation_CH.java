package gregtechCH.config.machine.kinetic;

import gregapi.data.MD;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.util.UT_CH;

import static gregapi.data.CS.T;
import static gregtechCH.config.ConfigJson_CH.*;

public class AttributesEngineRotation_CH extends AttributesMachine_CH {
    public long nbtInput;
    public long nbtOutput;
    public boolean nbtWasteEnergy;

    public AttributesEngineRotation_CH() {}
    public AttributesEngineRotation_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 7 + 24800;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtInput = MA_MACHINE_KINETIC.get(aMat).mNbtInput;
        nbtOutput = nbtInput/2;
        nbtWasteEnergy = T;
        int tAxleID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 24800;
        recipeObject = new Object[]{
                "SAS", "wML", "GAG",
                'S', OP.gearGtSmall.dat(aMat),
                'G', OP.gearGt.dat(aMat),
                'M', OP.casingMachine.dat(aMat),
                'L', OD.itemLubricantEarly,
                'A', ST.make(MD.GT, "gt.multitileentity", 1, tAxleID)
        };
        recipeNames = new String[]{
                "SAS", "wML", "GAG",
                "S", "OreDictItemData:" + OP.gearGtSmall.mNameInternal + aMat.mNameInternal,
                "G", "OreDictItemData:" + OP.gearGt.mNameInternal + aMat.mNameInternal,
                "M", "OreDictItemData:" + OP.casingMachine.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricantEarly.name(),
                "A", MD.GT.mID + ":" + "gt.multitileentity:" + tAxleID
        };
    }
}
