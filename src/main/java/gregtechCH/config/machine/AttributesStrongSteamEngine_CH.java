package gregtechCH.config.machine;

import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import gregtechCH.config.ConfigJson_CH;

import java.util.Objects;

import static gregapi.data.CS.F;
import static gregapi.data.CS.STEAM_PER_EU;

public class AttributesStrongSteamEngine_CH extends AttributesSteamEngine_CH {
    public AttributesStrongSteamEngine_CH() {}
    public AttributesStrongSteamEngine_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1350;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtCapacity = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 4000;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 4 / STEAM_PER_EU;
        nbtEfficiency = Objects.equals(aMaterialName, MT.IronWood.mNameInternal) ?
                (int) UT.Code.units(ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency / 100 + 1, 10, 7, F) * 100 :
                (int) UT.Code.units(ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency / 100 + 1, 10, 6, F) * 100;

        recipeObject = new Object[]{
                "PhP", "SIS", "PwP",
                'S', OP.stick.dat(OreDictMaterial.get(aMaterialName)),
                'P', OP.plateDense.dat(OreDictMaterial.get(aMaterialName)),
                'I', OP.spring.dat(OreDictMaterial.get(aMaterialName))
        };
        recipeNames = new String[]{
                "PhP", "SIS", "PwP",
                "S", "OreDictItemData:" + OP.stick.mNameInternal + aMaterialName,
                "P", "OreDictItemData:" + OP.plateDense.mNameInternal + aMaterialName,
                "I", "OreDictItemData:" + OP.spring.mNameInternal + aMaterialName,
        };
    }
}
