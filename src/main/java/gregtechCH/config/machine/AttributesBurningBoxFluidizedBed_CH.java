package gregtechCH.config.machine;

import gregapi.data.ANY;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;
import net.minecraft.init.Blocks;


public class AttributesBurningBoxFluidizedBed_CH extends AttributesBurningBox_CH{

    public AttributesBurningBoxFluidizedBed_CH() {}
    public AttributesBurningBoxFluidizedBed_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 9000;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtEfficiency = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 4;
        recipeObject = new Object[]{
                "PCP", "UwU", "BXB",
                'B', Blocks.brick_block,
                'U', OP.plateCurved.dat(OreDictMaterial.get(aMaterialName)),
                'X', OP.rotor.dat(OreDictMaterial.get(aMaterialName)),
                'P', OP.plate.dat(OreDictMaterial.get(aMaterialName)),
                'C', OP.plateDouble.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "BwB", "BIB",
                "B", "Blocks:" + "brick_block",
                "U", "OreDictItemData:" + OP.plateCurved.mNameInternal + aMaterialName,
                "X", "OreDictItemData:" + OP.rotor.mNameInternal + aMaterialName,
                "P", "OreDictItemData:" + OP.plate.mNameInternal + aMaterialName,
                "C", "OreDictItemData:" + OP.plateDouble.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}