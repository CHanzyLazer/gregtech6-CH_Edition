package gregtechCH.config.machine.multiblock;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.IL;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.config.machine.MaterialDeserializer_CH;
import gregtechCH.config.machine.MaterialSerializer_CH;
import gregtechCH.util.UT_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_MULTIBLOCK;
import static gregtechCH.config.ConfigJson_CH.MA_ROTOR;

public class AttributesLargeSteamTurbine_CH extends AttributesLargeMotor_CH {

    public short nbtDesign;
    public int nbtEfficiencyWater;

    public AttributesLargeSteamTurbine_CH() {}
    public AttributesLargeSteamTurbine_CH(OreDictMaterial aMat, OreDictMaterial aRotMat) {
        material = aMat;
        rotorMaterial = aRotMat;
        ID = MA_MACHINE_MULTIBLOCK.get(aMat).mID + 17210;
        stackSize = MA_MACHINE_MULTIBLOCK.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtResistance;
        setEnergy(1, 10, 4, MA_MACHINE_MULTIBLOCK.get(aMat).mNbtOutput, 10000 - Math.max(MA_ROTOR.get(aRotMat).mNbtEfficiencyLoss + 2000, 0));
        nbtDesign = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtDesign;
        nbtEfficiencyWater = 9500;

        recipeObject = new Object[]{
                "PPP", "PMP", "PPP",
                'M', ST.make(MD.GT, "gt.multitileentity", 1, nbtDesign),
                'P', OP.blockPlate.dat(aRotMat)
        };
        recipeNames = new String[]{
                "PPP", "PMP", "PPP",
                "M", MD.GT.mID + ":" + "gt.multitileentity:" + nbtDesign,
                "P", "OreDictItemData:" + OP.blockPlate.mNameInternal + aRotMat.mNameInternal
        };
    }
}
