package gregtechCH.config.machine.generator;

import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;


public class AttributesBurningBoxBrick_CH extends AttributesBurningBox_CH {

    public AttributesBurningBoxBrick_CH() {}
    public AttributesBurningBoxBrick_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 1100;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss - 2500, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput;
        recipeObject = new Object[]{
                "BBB", "BBB", "BFB",
                'B', OP.ingot.dat(aMat),
                'F', OD.craftingFirestarter
        };
        recipeNames = new String[]{
                "BBB", "BBB", "BFB",
                "B", "OreDictItemData:" + OP.ingot.mNameInternal + aMat,
                "F", "OD:" + OD.craftingFirestarter.name()
        };
    }
}
