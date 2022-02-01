package gregtechCH.config.machine.steam;

import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.util.UT_CH;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;

public class AttributesSteamEngine_CH extends AttributesMachine_CH {
    public int nbtEfficiency;
    public long nbtCapacity;
    public long nbtOutput;
    public int nbtEfficiencyWater;

    public AttributesSteamEngine_CH() {}
    public AttributesSteamEngine_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 1300;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtCapacity = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 1000;
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput / STEAM_PER_EU;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss / 3 + 5000, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtEfficiencyWater = 8000;

        recipeObject = new Object[]{
                "PhP", "SIS", "PwP",
                'S', OP.stick.dat(aMat),
                'P', OP.plateDouble.dat(aMat),
                'I', OP.springSmall.dat(aMat)
        };
        recipeNames = new String[]{
                "PhP", "SIS", "PwP",
                "S", "OreDictItemData:" + OP.stick.mNameInternal + aMat.mNameInternal,
                "P", "OreDictItemData:" + OP.plateDouble.mNameInternal + aMat.mNameInternal,
                "I", "OreDictItemData:" + OP.springSmall.mNameInternal + aMat.mNameInternal
        };
    }
}
