package gregtechCH.config.machine;

import gregapi.data.ANY;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;
import net.minecraft.init.Blocks;


public class AttributesBurningBoxGas_CH extends AttributesBurningBox_CH{

    public AttributesBurningBoxGas_CH() {}
    public AttributesBurningBoxGas_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 1600;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtEfficiency = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 2;
        recipeObject = new Object[]{
                "PCP", "BwB", "BIB",
                'B', Blocks.brick_block,
                'P', OP.plate.dat(OreDictMaterial.get(aMaterialName)),
                'I', OP.pipeSmall.dat(OreDictMaterial.get(aMaterialName)),
                'C', OP.plateDouble.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "BwB", "BIB",
                "B", "Blocks:" + "brick_block",
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMaterialName,
                "I", "OreDictItemData:" + OP.pipeSmall.mNameInternal + aMaterialName,
                "C", "OreDictItemData:" + OP.plateDouble.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}