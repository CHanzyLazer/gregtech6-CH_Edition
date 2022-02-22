package gregtechCH.config.machine.steam;

import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.machine.AttributesMotor_CH;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.*;

public class AttributesSteamTurbine_CH extends AttributesMotor_CH {

    public int nbtEfficiencyWater;
    public int nbtEfficiencyOC;

    public AttributesSteamTurbine_CH() {}
    public AttributesSteamTurbine_CH(OreDictMaterial aMat, OreDictMaterial aRotMat) {
        material = aMat;
        rotorMaterial = aRotMat;
        ID = MA_MACHINE_KINETIC.get(aMat).mID * 10 + MA_ROTOR.get(aRotMat).mID + 1500;
        stackSize = MA_MACHINE_KINETIC.get(aMat).mStackSize;
        nbtHardness = 4.0F;
        nbtResistance = 4.0F;
        nbtOutput = MA_ROTOR.get(aRotMat).mNbtOutput;
        nbtEfficiency = 10000 - Math.max(MA_ROTOR.get(aRotMat).mNbtEfficiencyLoss / 2 + 4000, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        setEnergy();
        nbtEfficiencyWater = 8000;
        nbtEfficiencyOC = 5000;

        recipeObject = new Object[]{
                "TwT", "GSG", "TMT",
                'S', OP.stickLong.dat(aMat),
                'M', OP.casingMachineDouble.dat(aMat),
                'G', OP.gearGt.dat(aMat),
                'T', OP.rotor.dat(aRotMat)
        };
        recipeNames = new String[]{
                "TwT", "GSG", "TMT",
                "S", "OreDictItemData:" + OP.stickLong.mNameInternal + aMat.mNameInternal,
                "M", "OreDictItemData:" + OP.casingMachineDouble.mNameInternal + aMat.mNameInternal,
                "G", "OreDictItemData:" + OP.gearGt.mNameInternal + aMat.mNameInternal,
                "T", "OreDictItemData:" + OP.rotor.mNameInternal + aRotMat.mNameInternal
        };
    }
}
