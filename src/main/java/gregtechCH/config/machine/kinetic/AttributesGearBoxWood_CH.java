package gregtechCH.config.machine.kinetic;

import gregapi.data.MD;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_KINETIC;

public class AttributesGearBoxWood_CH extends AttributesGearBox_CH {
    public int nbtFlammability;

    public AttributesGearBoxWood_CH() {}
    public AttributesGearBoxWood_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 9 + 24800;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        nbtFlammability = 150;
        nbtInput = MA_MACHINE_KINETIC.get(aMat).mNbtInput * 2;
        int tAxleID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + 24800;
        recipeObject = new Object[]{
                "PsP", "ALA", "PAP",
                'P', OP.plate.dat(aMat),
                'L', OD.itemLubricantEarly,
                'A', ST.make(MD.GT, "gt.multitileentity", 1, tAxleID)
        };
        recipeNames = new String[]{
                "PsP", "ALA", "PAP",
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricantEarly.name(),
                "A", MD.GT.mID + ":" + "gt.multitileentity:" + tAxleID
        };
    }
}