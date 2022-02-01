package gregtechCH.config.machine.generator;

import gregapi.data.ANY;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.util.UT_CH;
import net.minecraft.init.Blocks;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;


public class AttributesBurningBoxFluidizedBed_CH extends AttributesBurningBox_CH{

    public AttributesBurningBoxFluidizedBed_CH() {}
    public AttributesBurningBoxFluidizedBed_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 9000;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss - 2500, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 4;
        recipeObject = new Object[]{
                "PCP", "UwU", "BXB",
                'B', Blocks.brick_block,
                'U', OP.plateCurved.dat(aMat),
                'X', OP.rotor.dat(aMat),
                'P', OP.plate.dat(aMat),
                'C', OP.plateDouble.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "UwU", "BXB",
                "B", "Blocks:" + "brick_block",
                "U", "OreDictItemData:" + OP.plateCurved.mNameInternal + aMat.mNameInternal,
                "X", "OreDictItemData:" + OP.rotor.mNameInternal + aMat.mNameInternal,
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMat.mNameInternal,
                "C", "OreDictItemData:" + OP.plateDouble.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}