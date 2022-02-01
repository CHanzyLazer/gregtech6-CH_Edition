package gregtechCH.config.machine.generator;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;


public class AttributesDenseHeatExchanger_CH extends AttributesHeatExchanger_CH {

    public AttributesDenseHeatExchanger_CH() {}
    public AttributesDenseHeatExchanger_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 9150;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss - 2500, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 8;
        recipeObject = new Object[]{
                "PCP", "OwO", "PMP",
                'M', OP.casingMachineQuadruple.dat(aMat),
                'O', OP.pipeLarge.dat(ANY.Cu),
                'P', OP.plateQuadruple.dat(MT.Pb),
                'C', OP.plateDense.dat((aMat.hashCode() == MT.Invar.hashCode()) ? ANY.Cu : MT.AnnealedCopper)
        };
        recipeNames = new String[]{
                "PCP", "OwO", "PMP",
                "M", "OreDictItemData:" + OP.casingMachineQuadruple.mNameInternal + aMat.mNameInternal,
                "O", "OreDictItemData:" + OP.pipeLarge.mNameInternal + ANY.Cu.mNameInternal,
                "P", "OreDictItemData:" + OP.plateQuadruple.mNameInternal + MT.Pb.mNameInternal,
                "C", "OreDictItemData:" + OP.plateDense.mNameInternal + ((aMat.hashCode() ==  MT.Invar.hashCode()) ? ANY.Cu.mNameInternal : MT.AnnealedCopper.mNameInternal)
        };
    }
}