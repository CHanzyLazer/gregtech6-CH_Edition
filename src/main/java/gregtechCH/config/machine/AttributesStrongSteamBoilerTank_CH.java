package gregtechCH.config.machine;

import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;

import java.util.Objects;

public class AttributesStrongSteamBoilerTank_CH extends AttributesSteamBoilerTank_CH {

    public AttributesStrongSteamBoilerTank_CH() {}
    public AttributesStrongSteamBoilerTank_CH(String aMaterialName) {
        ID = Objects.equals(aMaterialName, MT.Netherite.mNameInternal) ? 1259 : ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1250;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtCapacity = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtInput * 4 * 10000;
        nbtCapacity_SU = nbtCapacity;
        nbtInput_HU = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtInput * 8;
        nbtEfficiency_CH = 6000;
        recipeObject = new Object[]{
                " P ", "PwP", "PhP",
                'P', OP.plateDense.dat(OreDictMaterial.get(aMaterialName))
        };
        recipeNames = new String[]{
                " P ", "PwP", "PhP",
                "P", "OreDictItemData:" + OP.plateDense.mNameInternal + aMaterialName
        };
    }
}
