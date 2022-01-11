package gregtechCH.config.machine;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;
import gregtechCH.config.recipeManager.AttributesRecipe_CH;
import net.minecraft.init.Blocks;

import java.util.Objects;


public class AttributesDenseHeatExchanger_CH extends AttributesHeatExchanger_CH {

    public AttributesDenseHeatExchanger_CH() {}
    public AttributesDenseHeatExchanger_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 9150;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtEfficiency = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput * 2;
        recipeObject = new Object[]{
                "PCP", "OwO", "PMP",
                'M', OP.casingMachineQuadruple.dat(OreDictMaterial.get(aMaterialName)),
                'O', OP.pipeLarge.dat(ANY.Cu),
                'P', OP.plateQuintuple.dat(Objects.equals(aMaterialName, MT.Invar.mNameInternal) ? ANY.Cu:MT.AnnealedCopper),
                'C', OP.plateDense.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "OwO", "PMP",
                "M", "OreDictItemData:" + OP.casingMachineQuadruple.mNameInternal + aMaterialName,
                "O", "OreDictItemData:" + OP.pipeLarge.mNameInternal + ANY.Cu.mNameInternal,
                "P", "OreDictItemData:" + OP.plateQuintuple.mNameInternal + (Objects.equals(aMaterialName, MT.Invar.mNameInternal) ? ANY.Cu.mNameInternal:MT.AnnealedCopper.mNameInternal),
                "C", "OreDictItemData:" + OP.plateDense.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}