package gregtechCH.data;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;

import java.util.HashMap;

// MA: Material Attribute
public class MA_MachineGenerator_CH extends HashMap<String, MA_MachineGenerator_CH.GeneralAttributes> {
    public static class GeneralAttributes {
        public int mID = 0;
        public int mStackSize = 16;
        public float mNbtHardness = 4.0F;
        public float mNbtResistance = 4.0F;
        public int mNbtEfficiencyLoss = 0;
        public long mNbtInput = 16;
        public long mNbtOutput = 16;

        public GeneralAttributes() {}
        public GeneralAttributes(int aID, int aStackSize, float aNbtHardness, float aNbtResistance, int aNbtEfficiencyLoss, long aNbtInput, long aNbtOutput) {
            mID = aID;
            mStackSize = aStackSize; mNbtHardness = aNbtHardness; mNbtResistance = aNbtResistance;
            mNbtEfficiencyLoss = aNbtEfficiencyLoss; mNbtInput = aNbtInput; mNbtOutput = aNbtOutput;
        }
    }

//    private static Map<String, GeneralAttributes> mData = new TreeMap<>();

    public MA_MachineGenerator_CH() {
        put(MT.Brick.mNameInternal,         new GeneralAttributes(99,     16,    6.0F,   6.0F,  10000,  16,     16));
        put(MT.Pb.mNameInternal,            new GeneralAttributes(0,      16,    4.0F,   4.0F,  7500,   16,     16));
        put(MT.Bi.mNameInternal,            new GeneralAttributes(1,      16,    4.0F,   4.0F,  8000,   20,     20));
        put(MT.Bronze.mNameInternal,        new GeneralAttributes(2,      16,    7.0F,   7.0F,  5000,   24,     24));
        put(MT.Invar.mNameInternal,         new GeneralAttributes(3,      16,    4.0F,   4.0F,  1500,   16,     16));
        put(ANY.Steel.mNameInternal,        new GeneralAttributes(4,      16,    6.0F,   6.0F,  6000,   32,     32));
        put(MT.Cr.mNameInternal,            new GeneralAttributes(5,      16,    4.0F,   4.0F,  4500,   96,     112));
        put(MT.Ti.mNameInternal,            new GeneralAttributes(6,      16,    9.0F,   9.0F,  4500,   112,    96));
        put(MT.Netherite.mNameInternal,     new GeneralAttributes(10,     16,    9.0F,   9.0F,  3500,   112,    96));
        put(ANY.W.mNameInternal,            new GeneralAttributes(7,      16,    10.0F,  10.0F, 2000,   128,    128));
        put(MT.TungstenSteel.mNameInternal, new GeneralAttributes(8,      16,    12.5F,  12.5F, 3500,   128,    128));
        put(MT.Ta4HfC5.mNameInternal,       new GeneralAttributes(9,      16,    12.5F,  12.5F, 3000,   256,    256));

        put(MT.TinAlloy.mNameInternal,      new GeneralAttributes(1,      16,    4.0F,   4.0F,  6000,   20,     20));
        put(MT.Brass.mNameInternal,         new GeneralAttributes(9,      16,    7.0F,   7.0F,  5000,   24,     24));
        put(MT.IronWood.mNameInternal,      new GeneralAttributes(10,     16,    4.0F,   4.0F,  1000,   16,     16));
        put(MT.FierySteel.mNameInternal,    new GeneralAttributes(11,     16,    7.0F,   7.0F,  2000,   64,     64));

        put(MT.Ir.mNameInternal,            new GeneralAttributes(54,     16,    12.5F,  12.5F, 2500,   512,    512));
        put(MT.StainlessSteel.mNameInternal,new GeneralAttributes(0,      16,    6.0F,   6.0F,  2000,   64,     64));
    }

    public GeneralAttributes get(OreDictMaterial aMat) {
        return this.get(aMat.mNameInternal);
    }
}
