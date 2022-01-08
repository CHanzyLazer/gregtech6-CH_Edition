package gregtechCH.data;

import gregapi.data.ANY;
import gregapi.data.MT;

import java.util.HashMap;

public class MA_Machine_CH extends HashMap<String, MA_Machine_CH.GeneralAttributes> {
    public static class GeneralAttributes {
        public int mID = 0;
        public int mStackSize = 16;
        public float mNbtHardness = 4.0F;
        public float mNbtResistance = 4.0F;
        public int mNbtEfficiency = 10000;
        public long mNbtInput = 16;
        public long mNbtOutput = 16;

        public GeneralAttributes() {}
        public GeneralAttributes(int aID, int aStackSize, float aNbtHardness, float aNbtResistance, int aNbtEfficiency, long aNbtInput, long aNbtOutput) {
            mID = aID;
            mStackSize = aStackSize; mNbtHardness = aNbtHardness; mNbtResistance = aNbtResistance;
            mNbtEfficiency = aNbtEfficiency; mNbtInput = aNbtInput; mNbtOutput = aNbtOutput;
        }
    }

//    private static Map<String, GeneralAttributes> mData = new TreeMap<>();

    public MA_Machine_CH() {
        put(MT.Brick.mNameInternal,         new GeneralAttributes(99,     16,    6.0F,   6.0F,  2500,   16,     16));
        put(MT.Pb.mNameInternal,            new GeneralAttributes(0,      16,    4.0F,   4.0F,  5000,   16,     16));
        put(MT.Bi.mNameInternal,            new GeneralAttributes(1,      16,    4.0F,   4.0F,  4500,   20,     20));
        put(MT.Bronze.mNameInternal,        new GeneralAttributes(2,      16,    7.0F,   7.0F,  7500,   24,     24));
        put(MT.Invar.mNameInternal,         new GeneralAttributes(3,      16,    4.0F,   4.0F,  10000,  16,     16));
        put(ANY.Steel.mNameInternal,        new GeneralAttributes(4,      16,    6.0F,   6.0F,  7000,   32,     32));
        put(MT.Cr.mNameInternal,            new GeneralAttributes(5,      16,    4.0F,   4.0F,  8500,   96,     112));
        put(MT.Ti.mNameInternal,            new GeneralAttributes(6,      16,    9.0F,   9.0F,  8500,   112,    96));
        put(MT.Netherite.mNameInternal,     new GeneralAttributes(10,     16,    9.0F,   9.0F,  9000,   112,    96));
        put(ANY.W.mNameInternal,            new GeneralAttributes(7,      16,    10.0F,  10.0F, 10000,  128,    128));
        put(MT.TungstenSteel.mNameInternal, new GeneralAttributes(8,      16,    12.5F,  12.5F, 9000,   128,    128));
        put(MT.Ta4HfC5.mNameInternal,       new GeneralAttributes(9,      16,    12.5F,  12.5F, 10000,  256,    256));

        put(MT.TinAlloy.mNameInternal,      new GeneralAttributes(1,      16,    4.0F,   4.0F,  6666,   20,     20));
        put(MT.Brass.mNameInternal,         new GeneralAttributes(9,      16,    7.0F,   7.0F,  7500,   24,     24));
        put(MT.IronWood.mNameInternal,      new GeneralAttributes(10,     16,    4.0F,   4.0F,  10000,  16,     16));
        put(MT.FierySteel.mNameInternal,    new GeneralAttributes(11,     16,    7.0F,   7.0F,  8500,   64,     64));
        put(MT.FierySteel.mNameInternal,    new GeneralAttributes(11,     16,    7.0F,   7.0F,  8500,   64,     64));
    }
}
