package gregtechCH.data;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;

import java.util.HashMap;

// MA: Material Attribute
public class MA_MachineKinetic_CH extends HashMap<String, MA_MachineKinetic_CH.GeneralAttributes> {
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

    public MA_MachineKinetic_CH() {
        put(ANY.Wood.mNameInternal,         new GeneralAttributes(0,     16,    4.0F,   4.0F,  0,   8,      8));
        put(MT.Bronze.mNameInternal,        new GeneralAttributes(1,     16,    7.0F,   7.0F,  0,   32,     32));
        put(ANY.Steel.mNameInternal,        new GeneralAttributes(2,     16,    6.0F,   6.0F,  0,   128,    128));
        put(MT.Ti.mNameInternal,            new GeneralAttributes(3,     16,    9.0F,   9.0F,  0,   512,    512));
        put(MT.TungstenSteel.mNameInternal, new GeneralAttributes(4,     16,    12.5F,  12.5F, 0,   2048,   2048));
        put(MT.Ir.mNameInternal,            new GeneralAttributes(4,     16,    12.5F,  12.5F, 0,   8192,   8192));
        put(MT.Os.mNameInternal,            new GeneralAttributes(5,     16,    12.5F,  12.5F, 0,   32768,  32768));
    }

    public GeneralAttributes get(OreDictMaterial aMat) {
        return this.get(aMat.mNameInternal);
    }
}
