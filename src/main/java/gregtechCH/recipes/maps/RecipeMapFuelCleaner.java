package gregtechCH.recipes.maps;

import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.random.IHasWorldAndCoords;
import gregapi.recipes.Recipe;
import gregapi.recipes.Recipe.RecipeMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.RegType;

/**
 * @author YueSha
 * GT6U stuff
 */
public class RecipeMapFuelCleaner extends RecipeMap {
    public RecipeMapFuelCleaner(Collection<Recipe> aRecipeList, String aUnlocalizedName, String aNameLocal, String aNameNEI, long aProgressBarDirection, long aProgressBarAmount, String aNEIGUIPath, long aInputItemsCount, long aOutputItemsCount, long aMinimalInputItems, long aInputFluidCount, long aOutputFluidCount, long aMinimalInputFluids, long aMinimalInputs, long aPower, String aNEISpecialValuePre, long aNEISpecialValueMultiplier, String aNEISpecialValuePost, boolean aShowVoltageAmperageInNEI, boolean aNEIAllowed, boolean aConfigAllowed, boolean aNeedsOutputs, boolean aCombinePower, boolean aUseBucketSizeIn, boolean aUseBucketSizeOut) {
        super(RegType.GT6U, aRecipeList, aUnlocalizedName, aNameLocal, aNameNEI, aProgressBarDirection, aProgressBarAmount, aNEIGUIPath, aInputItemsCount, aOutputItemsCount, aMinimalInputItems, aInputFluidCount, aOutputFluidCount, aMinimalInputFluids, aMinimalInputs, aPower, aNEISpecialValuePre, aNEISpecialValueMultiplier, aNEISpecialValuePost, aShowVoltageAmperageInNEI, aNEIAllowed, aConfigAllowed, aNeedsOutputs, aCombinePower, aUseBucketSizeIn, aUseBucketSizeOut);
    }
    public RecipeMapFuelCleaner(RegType aRegType, Collection<Recipe> aRecipeList, String aUnlocalizedName, String aNameLocal, String aNameNEI, long aProgressBarDirection, long aProgressBarAmount, String aNEIGUIPath, long aInputItemsCount, long aOutputItemsCount, long aMinimalInputItems, long aInputFluidCount, long aOutputFluidCount, long aMinimalInputFluids, long aMinimalInputs, long aPower, String aNEISpecialValuePre, long aNEISpecialValueMultiplier, String aNEISpecialValuePost, boolean aShowVoltageAmperageInNEI, boolean aNEIAllowed, boolean aConfigAllowed, boolean aNeedsOutputs, boolean aCombinePower, boolean aUseBucketSizeIn, boolean aUseBucketSizeOut) {
        super(aRegType, aRecipeList, aUnlocalizedName, aNameLocal, aNameNEI, aProgressBarDirection, aProgressBarAmount, aNEIGUIPath, aInputItemsCount, aOutputItemsCount, aMinimalInputItems, aInputFluidCount, aOutputFluidCount, aMinimalInputFluids, aMinimalInputs, aPower, aNEISpecialValuePre, aNEISpecialValueMultiplier, aNEISpecialValuePost, aShowVoltageAmperageInNEI, aNEIAllowed, aConfigAllowed, aNeedsOutputs, aCombinePower, aUseBucketSizeIn, aUseBucketSizeOut);
    }
    
    @Override
    public Recipe findRecipe(IHasWorldAndCoords aTileEntity, Recipe aRecipe, boolean aNotUnificated, long aSize, ItemStack aSpecialSlot, FluidStack[] aFluids, ItemStack... aInputs) {
        Recipe rRecipe = super.findRecipe(aTileEntity, aRecipe, aNotUnificated, aSize, aSpecialSlot, aFluids, aInputs);
        if (aInputs == null || aFluids == null || aFluids.length < 1 || aFluids[0] == null || GAPI_POST.mFinishedServerStarted <= 0) return rRecipe;
        if (aFluids.length > 1) {
            for (FluidStack tFuel : aFluids) {
                if (tFuel != null && !FL.Hydrogen.is(tFuel)) {
                    if (tFuel.amount >= 16) {
                        if (FL.SPetrol.is(tFuel))
                            return new Recipe(F, F, F, ZL_IS, ZL_IS, null, null, FL.array(FL.Hydrogen.make(1), FL.amount(tFuel, 16)), FL.array(FL.Petrol.make(16), MT.H2S.gas(U1000, T)), 8, 16, 0);
                        if (FL.SFuel.is(tFuel))
                            return new Recipe(F, F, F, ZL_IS, ZL_IS, null, null, FL.array(FL.Hydrogen.make(1), FL.amount(tFuel, 16)), FL.array(FL.Fuel.make(16), MT.H2S.gas(U1000, T)), 8, 16, 0);
                        if (FL.SGasoil.is(tFuel))
                            return new Recipe(F, F, F, ZL_IS, ZL_IS, null, null, FL.array(FL.Hydrogen.make(1), FL.amount(tFuel, 16)), FL.array(FL.Gasoil.make(16), MT.H2S.gas(U1000, T)), 8, 16, 0);
                        if (FL.SKerosine.is(tFuel))
                            return new Recipe(F, F, F, ZL_IS, ZL_IS, null, null, FL.array(FL.Hydrogen.make(1), FL.amount(tFuel, 16)), FL.array(FL.Kerosine.make(16), MT.H2S.gas(U1000, T)), 8, 16, 0);
                        if (FL.SDiesel.is(tFuel))
                            return new Recipe(F, F, F, ZL_IS, ZL_IS, null, null, FL.array(FL.Hydrogen.make(1), FL.amount(tFuel, 16)), FL.array(FL.Diesel.make(16), MT.H2S.gas(U1000, T)), 8, 16, 0);
                        if (FL.SNaphtha.is(tFuel))
                            return new Recipe(F, F, F, ZL_IS, ZL_IS, null, null, FL.array(FL.Hydrogen.make(1), FL.amount(tFuel, 16)), FL.array(FL.Naphtha.make(16), MT.H2S.gas(U1000, T)), 8, 16, 0);
                    }
                }
            }
            return rRecipe;
        }
        return rRecipe;
    }
}
