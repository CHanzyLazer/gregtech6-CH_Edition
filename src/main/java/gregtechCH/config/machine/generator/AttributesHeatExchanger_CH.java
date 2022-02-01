package gregtechCH.config.machine.generator;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;


public class AttributesHeatExchanger_CH extends AttributesMachine_CH {
    public int nbtEfficiency;
    public long nbtOutput;

    public AttributesHeatExchanger_CH() {}
    public AttributesHeatExchanger_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_GENERATOR.get(aMat).mID + 9100;
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_GENERATOR.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_GENERATOR.get(aMat).mNbtResistance;
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss - 2500, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput * 2;
        recipeObject = new Object[]{
                "PCP", "OwO", "PMP",
                'M', OP.casingMachine.dat(aMat),
                'O', OP.pipeSmall.dat(ANY.Cu),
                'P', OP.plate.dat(MT.Pb),
                'C', OP.plateDouble.dat((aMat.hashCode() == MT.Invar.hashCode()) ? ANY.Cu : MT.AnnealedCopper)
        };
        recipeNames = new String[]{
                "PCP", "OwO", "PMP",
                "M", "OreDictItemData:" + OP.casingMachine.mNameInternal + aMat.mNameInternal,
                "O", "OreDictItemData:" + OP.pipeSmall.mNameInternal + ANY.Cu.mNameInternal,
                "P", "OreDictItemData:" + OP.plate.mNameInternal + MT.Pb.mNameInternal,
                "C", "OreDictItemData:" + OP.plateDouble.mNameInternal + ((aMat.hashCode() == MT.Invar.hashCode()) ? ANY.Cu.mNameInternal : MT.AnnealedCopper.mNameInternal)
        };
    }
}