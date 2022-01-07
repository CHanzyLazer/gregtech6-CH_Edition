package gregtechCH.config.machine;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;
import gregtechCH.config.recipeManager.AttributesRecipe_CH;

import java.util.Objects;

public class AttributesSteamBoilerTank_CH extends AttributesRecipe_CH {
    public int ID;
    public int stackSize;
    public float nbtHardness;
    public float nbtResistance;
    public long nbtCapacity;
    public long nbtCapacity_SU;
    public long nbtInput_HU;
    public int nbtEfficiency_CH;

    public AttributesSteamBoilerTank_CH() {}
    public AttributesSteamBoilerTank_CH(String aMaterialName) {
        ID = Objects.equals(aMaterialName, MT.Netherite.mNameInternal) ? 1209 : ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1200;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtCapacity = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtInput * 10000;
        nbtCapacity_SU = nbtCapacity;
        nbtInput_HU = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtInput * 2;
        nbtEfficiency_CH = 5000;
        recipeObject = new Object[]{
                " P ", "PwP", "PhP",
                'P', OP.plateDouble.dat(OreDictMaterial.get(aMaterialName))
        };
        recipeNames = new String[]{
                " P ", "PwP", "PhP",
                "P", "OreDictItemData:" + OP.plateDouble.mNameInternal + aMaterialName
        };
    }

}
