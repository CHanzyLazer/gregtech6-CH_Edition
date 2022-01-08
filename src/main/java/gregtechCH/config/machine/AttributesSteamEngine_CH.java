package gregtechCH.config.machine;

import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import gregtechCH.config.ConfigJson_CH;
import gregtechCH.config.recipeManager.AttributesRecipe_CH;

import java.util.Objects;

import static gregapi.data.CS.*;

public class AttributesSteamEngine_CH extends AttributesRecipe_CH {
    public int ID;
    public int stackSize;
    public float nbtHardness;
    public float nbtResistance;
    public int nbtEfficiency;
    public long nbtCapacity;
    public long nbtOutput;

    public AttributesSteamEngine_CH() {}
    public AttributesSteamEngine_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1300;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtCapacity = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 1000;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput / STEAM_PER_EU;
        nbtEfficiency = Objects.equals(aMaterialName, MT.IronWood.mNameInternal) ?
                (int) UT.Code.units(ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency / 100 + 1, 10, 7, F) * 100 :
                (int) UT.Code.units(ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency / 100 + 1, 10, 6, F) * 100;

        recipeObject = new Object[]{
                "PhP", "SIS", "PwP",
                'S', OP.stick.dat(OreDictMaterial.get(aMaterialName)),
                'P', OP.plateDouble.dat(OreDictMaterial.get(aMaterialName)),
                'I', OP.springSmall.dat(OreDictMaterial.get(aMaterialName))
        };
        recipeNames = new String[]{
                "PhP", "SIS", "PwP",
                "S", "OreDictItemData:" + OP.stick.mNameInternal + aMaterialName,
                "P", "OreDictItemData:" + OP.plateDouble.mNameInternal + aMaterialName,
                "I", "OreDictItemData:" + OP.springSmall.mNameInternal + aMaterialName,
        };
    }
}
