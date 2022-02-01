package gregtechCH.config.machine.generator;

import gregapi.data.ANY;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.util.UT_CH;
import net.minecraft.init.Blocks;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;


public class AttributesBurningBoxLiquid_CH extends AttributesBurningBox_CH {

    public AttributesBurningBoxLiquid_CH() {}
    public AttributesBurningBoxLiquid_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 1400;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss - 2500, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 2;
        recipeObject = new Object[]{
                "PCP", "IwI", "BBB",
                'B', Blocks.brick_block,
                'P', OP.plate.dat(aMat),
                'I', OP.pipeSmall.dat(aMat),
                'C', OP.plateDouble.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "IwI", "BBB",
                "B", "Blocks:" + "brick_block",
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMat.mNameInternal,
                "I", "OreDictItemData:" + OP.pipeSmall.mNameInternal + aMat.mNameInternal,
                "C", "OreDictItemData:" + OP.plateDouble.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}