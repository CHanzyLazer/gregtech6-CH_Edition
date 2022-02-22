package gregtechCH.config.machine.steam;

import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;

public class AttributesStrongSteamBoilerTank_CH extends AttributesSteamBoilerTank_CH {

    public AttributesStrongSteamBoilerTank_CH() {}
    public AttributesStrongSteamBoilerTank_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = (aMat.hashCode() == MT.Netherite.hashCode()) ? 1259 : MA_MACHINE_GENERATOR.get(aMat).mID + 1250;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtCapacity = MA_MACHINE_GENERATOR.get(aMat).mNbtInput * 4 * 1000;
        nbtCapacity_SU = nbtCapacity * 10;
        nbtInput = MA_MACHINE_GENERATOR.get(aMat).mNbtInput * 8;
        nbtEfficiency_CH = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss / 5 + 3000, 0);
        nbtEfficiency_CH = (int) UT_CH.Code.effNormalize(nbtEfficiency_CH);
        recipeObject = new Object[]{
                " P ", "PwP", "PhP",
                'P', OP.plateDense.dat(aMat)
        };
        recipeNames = new String[]{
                " P ", "PwP", "PhP",
                "P", "OreDictItemData:" + OP.plateDense.mNameInternal + aMat.mNameInternal
        };
    }
}
