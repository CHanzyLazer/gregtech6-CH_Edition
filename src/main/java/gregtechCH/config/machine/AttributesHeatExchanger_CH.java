package gregtechCH.config.machine;

import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.ConfigJson_CH;
import gregtechCH.config.recipeManager.AttributesRecipe_CH;
import net.minecraft.init.Blocks;

import java.util.Objects;


public class AttributesHeatExchanger_CH extends AttributesRecipe_CH {
    public int ID;
    public int stackSize;
    public float nbtHardness;
    public float nbtResistance;
    public int nbtEfficiency;
    public long nbtOutput;

    public AttributesHeatExchanger_CH() {}
    public AttributesHeatExchanger_CH(String aMaterialName) {
        ID = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mID + 9100;
        stackSize = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mStackSize;
        nbtHardness = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtHardness;
        nbtResistance = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtResistance;
        nbtEfficiency = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtEfficiency;
        nbtOutput = ConfigJson_CH.MATERIALS_ATTRIBUTES.get(aMaterialName).mNbtOutput;
        recipeObject = new Object[]{
                "PCP", "OwO", "PMP",
                'M', OP.casingMachine.dat(OreDictMaterial.get(aMaterialName)),
                'O', OP.pipeSmall.dat(ANY.Cu),
                'P', OP.plate.dat(Objects.equals(aMaterialName, MT.Invar.mNameInternal) ? ANY.Cu:MT.AnnealedCopper),
                'C', OP.plateDouble.dat(ANY.Cu)
        };
        recipeNames = new String[]{
                "PCP", "OwO", "PMP",
                "M", "OreDictItemData:" + OP.casingMachine.mNameInternal + aMaterialName,
                "O", "OreDictItemData:" + OP.pipeSmall.mNameInternal + ANY.Cu.mNameInternal,
                "P", "OreDictItemData:" + OP.plate.mNameInternal + (Objects.equals(aMaterialName, MT.Invar.mNameInternal) ? ANY.Cu.mNameInternal:MT.AnnealedCopper.mNameInternal),
                "C", "OreDictItemData:" + OP.plateDouble.mNameInternal + ANY.Cu.mNameInternal
        };
    }
}