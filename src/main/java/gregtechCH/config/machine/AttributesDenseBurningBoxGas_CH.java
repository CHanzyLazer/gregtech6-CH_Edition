package gregtechCH.config.machine;

import gregapi.data.ANY;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;
import net.minecraft.init.Blocks;


public class AttributesDenseBurningBoxGas_CH extends AttributesBurningBox_CH{

    public AttributesDenseBurningBoxGas_CH() {}
    public AttributesDenseBurningBoxGas_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1650;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtEfficiency = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 8;
        recipeObject = new Object[]{
                "PCP", "BwB", "BIB",
                'B', Blocks.brick_block,
                'P', OP.plateQuintuple.dat(OreDictMaterial.get(aMaterialName)),
                'I', OP.pipeLarge.dat(OreDictMaterial.get(aMaterialName)),
                'C', OP.plateDense.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "BwB", "BIB",
                "B", "Blocks:" + "brick_block",
                "P", "OreDictItemData:" + OP.plateQuintuple.mNameInternal + aMaterialName,
                "I", "OreDictItemData:" + OP.pipeLarge.mNameInternal + aMaterialName,
                "C", "OreDictItemData:" + OP.plateDense.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}