package gregtechCH.config.machine.multiblock;

import gregapi.data.IL;
import gregapi.data.MD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;

import static gregtechCH.config.ConfigJson_CH.*;

public class AttributesLargeGasTurbine_CH extends AttributesLargeMotor_CH {

    public short nbtDesign;
    public long[] nbtPreheatRate;

    @Override
    protected void setEnergy(int aLengthMin, int aLengthMax, int aLengthMid, long aOutputMid, long aEfficiencyMid) {
        super.setEnergy(aLengthMin, aLengthMax, aLengthMid, aOutputMid, aEfficiencyMid);
        int tLm = aLengthMax - aLengthMin + 1;
        nbtPreheatRate = new long[tLm];
        System.arraycopy(nbtOutput, 0, nbtPreheatRate, 0, tLm); // 燃气涡轮不再具有加速预热功能
    }

    public AttributesLargeGasTurbine_CH() {}
    public AttributesLargeGasTurbine_CH(OreDictMaterial aMat, OreDictMaterial aRotMat) {
        material = aMat;
        rotorMaterial = aRotMat;
        ID = MA_MACHINE_MULTIBLOCK.get(aMat).mID + 17230;
        stackSize = MA_MACHINE_MULTIBLOCK.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtResistance;
        setEnergy(3, 12, 6, MA_MACHINE_MULTIBLOCK.get(aMat).mNbtOutput, 10000 - Math.max(MA_ROTOR.get(aRotMat).mNbtEfficiencyLoss + 2000, 0));
        nbtDesign = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtDesign;

        recipeObject = new Object[]{
                "PwP", "BMC", "PEP",
                'M', ST.make(MD.GT, "gt.multitileentity", 1, MA_MACHINE_MULTIBLOCK.get(aMat).mID + 17210),
                'B', "gt:re-battery" + MA_MACHINE_MULTIBLOCK.get(aMat).mID,
                'C', IL.Processor_Crystal_Diamond,
                'E', IL.MOTORS[1],
                'P', OP.plateDense.dat(MA_MACHINE_MULTIBLOCK.get(aMat).mMaterial)
        };
        recipeNames = new String[]{
                "PwP", "BMC", "PEP",
                "M", MD.GT.mID + ":" + "gt.multitileentity:" + String.valueOf(MA_MACHINE_MULTIBLOCK.get(aMat).mID + 17210),
                "B", "ore:gt:re-battery" + MA_MACHINE_MULTIBLOCK.get(aMat).mID,
                "C", "IL:" + IL.Processor_Crystal_Diamond.name(),
                "E", "IL:" + IL.MOTORS[MA_MACHINE_MULTIBLOCK.get(aMat).mID - 1].name(),
                "P", "OreDictItemData:" + OP.plateDense.mNameInternal + MA_MACHINE_MULTIBLOCK.get(aMat).mMaterial.mNameInternal
        };
    }
}
