package gregtechCH.data;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;

import java.util.HashMap;

import static gregapi.data.CS.V;

// MA: Material Attribute
public class MA_MachineKinetic extends HashMap<String, MA_MachineKinetic.GeneralAttributes> {
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

    public MA_MachineKinetic() {
        put(ANY.Wood.mNameInternal,         new GeneralAttributes(0,     16,    4.0F,   4.0F,  0,   V[0],  V[0]));
        put(MT.Bronze.mNameInternal,        new GeneralAttributes(1,     16,    7.0F,   7.0F,  0,   V[1],  V[1]));
        put(ANY.Steel.mNameInternal,        new GeneralAttributes(2,     16,    6.0F,   6.0F,  0,   V[2],  V[2]));
        put(MT.Ti.mNameInternal,            new GeneralAttributes(3,     16,    9.0F,   9.0F,  0,   V[3],  V[3]));
        put(MT.TungstenSteel.mNameInternal, new GeneralAttributes(4,     16,    12.5F,  12.5F, 0,   V[4],  V[4]));
        put(MT.Ir.mNameInternal,            new GeneralAttributes(5,     16,    12.5F,  12.5F, 0,   V[5],  V[5]));
        put(MT.Os.mNameInternal,            new GeneralAttributes(6,     16,    12.5F,  12.5F, 0,   V[6],  V[6]));

        put(MT.WoodTreated.mNameInternal,   new GeneralAttributes(0,     16,    4.0F,   4.0F,  0,   V[0],  V[0]));
        put(MT.Steel.mNameInternal,         new GeneralAttributes(2,     16,    6.0F,   6.0F,  0,   V[2],  V[2]));
        put(MT.Iritanium.mNameInternal,     new GeneralAttributes(6,     16,    12.5F,  12.5F, 0,   V[6],  V[6]));
        put(MT.Trinitanium.mNameInternal,   new GeneralAttributes(7,     16,    12.5F,  12.5F, 0,   V[7],  V[7]));
        put(MT.Trinaquadalloy.mNameInternal,new GeneralAttributes(8,     16,    12.5F,  12.5F, 0,   V[8],  V[8]));
        put(MT.Ad.mNameInternal,            new GeneralAttributes(9,     16,    12.5F,  12.5F, 0,   V[9],  V[9]));
    }

    public GeneralAttributes get(OreDictMaterial aMat) {
        return this.get(aMat.mNameInternal);
    }
}
