package gregtechCH.config.machine.generator;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.config.machine.AttributesMotor_CH;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_GENERATOR;


public class AttributesDieselEngine_CH extends AttributesMotor_CH {
    public long nbtPreheatRate;

    public AttributesDieselEngine_CH() {}
    public AttributesDieselEngine_CH(OreDictMaterial aMat) {
        material = aMat;
        if (aMat.hashCode() == ANY.Steel.hashCode()) {
            ID = 9148;
        } else
        if (aMat.hashCode() == MT.Invar.hashCode()) {
            ID = 9149;
        } else
        if (aMat.hashCode() == MT.Ti.hashCode()) {
            ID = 9197;
        } else
        if (aMat.hashCode() == MT.TungstenSteel.hashCode()) {
            ID = 9198;
        } else {
            ID = MA_MACHINE_GENERATOR.get(aMat).mID + 9145;
        }
        stackSize = MA_MACHINE_GENERATOR.get(aMat).mStackSize;
        nbtHardness = 6.0F;
        nbtResistance = 6.0F;
        if (aMat.hashCode() ==  MT.Bronze.hashCode()) {
            nbtOutput = 16;
        } else
        if (aMat.hashCode() == ANY.Steel.hashCode()) {
            nbtOutput = 32;
        } else
        if (aMat.hashCode() == MT.Invar.hashCode()) {
            nbtOutput = 64;
        } else
        if (aMat.hashCode() ==  MT.Ti.hashCode()) {
            nbtOutput = 128;
        } else
        if (aMat.hashCode() ==  MT.TungstenSteel.hashCode()) {
            nbtOutput = 256;
        } else
        if (aMat.hashCode() == MT.Ir.hashCode()) {
            nbtOutput = 512;
        } else {
            nbtOutput = MA_MACHINE_GENERATOR.get(aMat).mNbtOutput;
        }
        nbtEfficiency = 10000 - Math.max(MA_MACHINE_GENERATOR.get(aMat).mNbtEfficiencyLoss / 4 + 6000, 0);
        nbtEfficiency = (int) UT_CH.Code.effNormalize(nbtEfficiency);
        setEnergy();
        nbtPreheatEnergy = nbtOutput * 1000;
        nbtPreheatRate = nbtOutput;

        recipeObject = new Object[]{
                "PLP", "SMS", "GOC",
                'M', OP.casingMachineDense.dat(aMat),
                'O', OP.pipeQuadruple.dat(aMat),
                'P', OP.rotor.dat(aMat),
                'S', OP.stickLong.dat(aMat),
                'G', OP.gearGt.dat(aMat),
                'C', OP.gearGtSmall.dat(aMat),
                'L', OD.itemLubricant
        };
        recipeNames = new String[]{
                "PLP", "SMS", "GOC",
                "M", "OreDictItemData:" + OP.casingMachineDense.mNameInternal + aMat.mNameInternal,
                "O", "OreDictItemData:" + OP.pipeQuadruple.mNameInternal + aMat.mNameInternal,
                "P", "OreDictItemData:" + OP.rotor.mNameInternal + aMat.mNameInternal,
                "S", "OreDictItemData:" + OP.stickLong.mNameInternal + aMat.mNameInternal,
                "G", "OreDictItemData:" + OP.gearGt.mNameInternal + aMat.mNameInternal,
                "C", "OreDictItemData:" + OP.gearGtSmall.mNameInternal + aMat.mNameInternal,
                "L", "OD:" + OD.itemLubricant.name()
        };
    }
}
