package gregtechCH.config.machine.steam;

import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.util.UT_CH;

import static gregapi.data.CS.STEAM_PER_EU;
import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;

public class AttributesStrongSteamEngine_CH extends AttributesSteamEngine_CH {
    public AttributesStrongSteamEngine_CH() {}
    public AttributesStrongSteamEngine_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 1350;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtCapacity = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 4000;
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 4 / STEAM_PER_EU;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss / 3 + 5000, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtEfficiencyWater = 8000;

        recipeObject = new Object[]{
                "PhP", "SIS", "PwP",
                'S', OP.stick.dat(aMat),
                'P', OP.plateDense.dat(aMat),
                'I', OP.spring.dat(aMat)
        };
        recipeNames = new String[]{
                "PhP", "SIS", "PwP",
                "S", "OreDictItemData:" + OP.stick.mNameInternal + aMat.mNameInternal,
                "P", "OreDictItemData:" + OP.plateDense.mNameInternal + aMat.mNameInternal,
                "I", "OreDictItemData:" + OP.spring.mNameInternal + aMat.mNameInternal,
        };
    }
}
