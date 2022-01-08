package gregtechCH.config.machine;

import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;


public class AttributesBurningBoxBrick_CH extends AttributesBurningBox_CH {

    public AttributesBurningBoxBrick_CH() {}
    public AttributesBurningBoxBrick_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1100;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtEfficiency = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput;
        recipeObject = new Object[]{
                "BBB", "BBB", "BFB",
                'B', OP.ingot.dat(OreDictMaterial.get(aMaterialName)),
                'F', OD.craftingFirestarter
        };
        recipeNames = new String[]{
                "BBB", "BBB", "BFB",
                "B", "OreDictItemData:" + OP.ingot.mNameInternal + aMaterialName,
                "F", "OD:" + OD.craftingFirestarter.name()
        };
    }
}
