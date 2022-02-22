package gregtechCH.config.machine.multiblock;

import gregapi.data.MD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.ST;
import gregtechCH.config.machine.AttributesMachine_CH;

import static gregtechCH.config.ConfigJson_CH.MA_MACHINE_MULTIBLOCK;

public class AttributesLargeBoilerTank_CH extends AttributesMachine_CH {

    public short nbtDesign;
    public long nbtCapacity;
    public long nbtCapacity_SU;
    public long nbtInput;
    public int nbtEfficiency_CH;

    public AttributesLargeBoilerTank_CH() {}
    public AttributesLargeBoilerTank_CH(OreDictMaterial aMat) {
        material = aMat;
        ID = MA_MACHINE_MULTIBLOCK.get(aMat).mID + 17200;
        stackSize = MA_MACHINE_MULTIBLOCK.get(aMat).mStackSize;
        nbtHardness = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtHardness;
        nbtResistance = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtResistance;
        nbtDesign = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtDesign;
        nbtCapacity = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtInput * 1000;
        nbtCapacity_SU = nbtCapacity * 40;
        nbtInput = MA_MACHINE_MULTIBLOCK.get(aMat).mNbtInput; // 还是不要翻倍好了
        nbtEfficiency_CH = 8000;
        recipeObject = new Object[]{
                "PPh", "PMP", "wPP",
                'M', ST.make(MD.GT, "gt.multitileentity", 1, nbtDesign),
                'P', OP.plateDense.dat(aMat)
        };
        recipeNames = new String[]{
                "PPh", "PMP", "wPP",
                "M", MD.GT.mID + ":" + "gt.multitileentity:" + nbtDesign,
                "P", "OreDictItemData:" + OP.plateDense.mNameInternal + aMat.mNameInternal
        };
    }

}
