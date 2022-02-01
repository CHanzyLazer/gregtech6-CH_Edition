package gregtechCH.config.machine.steam;

import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;

public class AttributesSteamBoilerTank_CH extends AttributesMachine_CH {
    public long nbtCapacity;
    public long nbtCapacity_SU;
    public long nbtInput;
    public int nbtEfficiency_CH;

    public AttributesSteamBoilerTank_CH() {}
    public AttributesSteamBoilerTank_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = (aMat.hashCode() == MT.Netherite.hashCode()) ? 1209 : MA_MACHINE_GENERATOR.get(aMat).mID + 1200;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtCapacity = MA_MACHINE_GENERATOR.get(aMat).mNbtInput * 10000;
        nbtCapacity_SU = nbtCapacity;
        nbtInput = MA_MACHINE_GENERATOR.get(aMat).mNbtInput * 2;
        nbtEfficiency_CH = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss / 5 + 4000, 0);
        nbtEfficiency_CH = (int) UT_CH.Code.effNormalize(nbtEfficiency_CH);
        recipeObject = new Object[]{
                " P ", "PwP", "PhP",
                'P', OP.plateDouble.dat(aMat)
        };
        recipeNames = new String[]{
                " P ", "PwP", "PhP",
                "P", "OreDictItemData:" + OP.plateDouble.mNameInternal + aMat.mNameInternal
        };
    }

}
