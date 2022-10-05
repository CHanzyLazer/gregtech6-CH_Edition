package gregtechCH.data;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;

import java.util.HashMap;

// MA: Material Attribute
public class MA_Rotor extends HashMap<String, MA_Rotor.GeneralAttributes> {
    public static class GeneralAttributes {
        public int mID = 0;
        public int mNbtEfficiencyLoss = 0;
        public long mNbtInput = 16;
        public long mNbtOutput = 16;

        public GeneralAttributes() {}
        public GeneralAttributes(int aID, int aNbtEfficiencyLoss, long aNbtInput, long aNbtOutput) {
            mID = aID;
            mNbtEfficiencyLoss = aNbtEfficiencyLoss; mNbtInput = aNbtInput; mNbtOutput = aNbtOutput;
        }
    }

//    private static Map<String, GeneralAttributes> mData = new TreeMap<>();

    public MA_Rotor() {
        put(MT.Bronze.mNameInternal,        new GeneralAttributes(2,  5000,   24,     16));
        put(MT.Brass.mNameInternal,         new GeneralAttributes(5,  5000,   36,     24));
        put(MT.Invar.mNameInternal,         new GeneralAttributes(8,  4000,   48,     32));

        put(ANY.Steel.mNameInternal,        new GeneralAttributes(2,  6000,   96,     64));
        put(MT.Cr.mNameInternal,            new GeneralAttributes(5,  4000,   144,    96));
        put(MT.IronWood.mNameInternal,      new GeneralAttributes(7,  2500,   192,    128));
        put(MT.Steeleaf.mNameInternal,      new GeneralAttributes(8,  3000,   192,    128));
        put(MT.Thaumium.mNameInternal,      new GeneralAttributes(9,  4000,   192,    128));

        put(MT.Ti.mNameInternal,            new GeneralAttributes(0,  3000,   384,    256));
        put(MT.FierySteel.mNameInternal,    new GeneralAttributes(1,  5000,   384,    256));
        put(MT.Al.mNameInternal,            new GeneralAttributes(5,  1800,   576,    374));
        put(MT.Magnalium.mNameInternal,     new GeneralAttributes(8,  1500,   768,    512));

        put(MT.VoidMetal.mNameInternal,     new GeneralAttributes(0,  500,    1152,   768));
        put(MT.Trinitanium.mNameInternal,   new GeneralAttributes(5,  1500,   1536,   1024));
        put(MT.Graphene.mNameInternal,      new GeneralAttributes(8,  1500,   3072,   2048));

        put(MT.Vibramantium.mNameInternal,  new GeneralAttributes(0,  1500,   6144,   4096));
    }

    public GeneralAttributes get(OreDictMaterial aMat) {
        return this.get(aMat.mNameInternal);
    }
}
