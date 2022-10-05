package gregtechCH.data;

import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;

import java.util.HashMap;

// MA: Material Attribute
public class MA_MachineMultiblock extends HashMap<String, MA_MachineMultiblock.GeneralAttributes> {
    public static class GeneralAttributes {
        public int mID = 0;
        public int mStackSize = 16;
        public float mNbtHardness = 4.0F;
        public float mNbtResistance = 4.0F;
        public int mNbtEfficiencyLoss = 0;
        public long mNbtInput = 16;
        public long mNbtOutput = 16;
        public short mNbtDesign = 18022;
        public OreDictMaterial mMaterial = MT.Invar;

        public GeneralAttributes() {}
        public GeneralAttributes(int aID, int aStackSize, float aNbtHardness, float aNbtResistance, int aNbtEfficiencyLoss, long aNbtInput, long aNbtOutput, short aNbtDesign, OreDictMaterial aMaterial) {
            mID = aID;
            mStackSize = aStackSize; mNbtHardness = aNbtHardness; mNbtResistance = aNbtResistance;
            mNbtEfficiencyLoss = aNbtEfficiencyLoss; mNbtInput = aNbtInput; mNbtOutput = aNbtOutput;
            mNbtDesign = aNbtDesign; mMaterial = aMaterial;
        }
    }

//    private static Map<String, GeneralAttributes> mData = new TreeMap<>();

    public MA_MachineMultiblock() {
        put(MT.StainlessSteel.mNameInternal,    new GeneralAttributes(1,     16,    6.0F,   6.0F,  0,   4096,   4096,   (short)18022, MT.Invar));
        put(MT.Ti.mNameInternal,                new GeneralAttributes(2,     16,    9.0F,   9.0F,  0,   8192,   8192,   (short)18026, MT.TungstenSteel));
        put(MT.TungstenSteel.mNameInternal,     new GeneralAttributes(3,     16,    12.5F,  12.5F, 0,   16384,  16384,  (short)18023, MT.W));
        put(MT.Ad.mNameInternal,                new GeneralAttributes(4,     16,    100.0F, 100.0F,0,   131072, 131072, (short)18025, MT.Ad));
        put(MT.Invar.mNameInternal,             new GeneralAttributes(5,     16,    6.0F,   6.0F,  0,   4096,   4096,   (short)18027, MT.Invar));
    }

    public GeneralAttributes get(OreDictMaterial aMat) {
        return this.get(aMat.mNameInternal);
    }
}
