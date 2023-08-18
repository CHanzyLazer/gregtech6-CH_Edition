package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.recipes.Recipe;
import gregapi.tileentity.ITileEntityAdjacentInventoryUpdatable;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.IMTEC_ToolTips;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.NBT_CANFILL_STEAM;

/**
 * @author Gregorius Techneticies, CHanzy
 * 用这个类实现自定义实体的行为，使得同一种实体可以有不同的行为
 * 为了减少工作量，仅包含修改后的方法，使用相互包含对方引用的方式来调用对方的接口
 * 放弃旧逻辑的兼容但是这个类可以保留（用来比较方便的看出我的修改），只是限制直接生成
 */
public abstract class MTEC_BasicMachine_Greg implements IMTEC_BasicMachine, IMTEC_ToolTips {
    // the instance of MultiTileEntityBasicMachine
    protected final MultiTileEntityBasicMachine mTE;
    protected MTEC_BasicMachine_Greg(MultiTileEntityBasicMachine aTE) {mTE = aTE;}
    
    
    /* main code */
    public boolean mParallelDuration = F;
    public int mParallel = 1;
    public long mEnergy = 0, mInputMin = 16, mInput = 32, mInputMax = 64, mMinEnergy = 0, mOutputEnergy = 0, mChargeRequirement = 0;
    public long mProgress = 0, mMaxProgress = 0;
    
    protected boolean mCanFillSteam = F;
    public void readFromNBT(NBTTagCompound aNBT) {
        mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_INPUT))             {mInput = aNBT.getLong(NBT_INPUT); mInputMin = mInput / 2; mInputMax = mInput * 2;}
        if (aNBT.hasKey(NBT_INPUT_MIN))         mInputMin = aNBT.getLong(NBT_INPUT_MIN);
        if (aNBT.hasKey(NBT_INPUT_MAX))         mInputMax = aNBT.getLong(NBT_INPUT_MAX);
        if (aNBT.hasKey(NBT_MINENERGY))         mMinEnergy = aNBT.getLong(NBT_MINENERGY);
        if (aNBT.hasKey(NBT_OUTPUT))            mOutputEnergy = aNBT.getLong(NBT_OUTPUT);
        if (aNBT.hasKey(NBT_INPUT_EU))          mChargeRequirement = aNBT.getLong(NBT_INPUT_EU);
        
        if (aNBT.hasKey(NBT_PROGRESS))          mProgress = aNBT.getLong(NBT_PROGRESS);
        if (aNBT.hasKey(NBT_MAXPROGRESS))       mMaxProgress = aNBT.getLong(NBT_MAXPROGRESS);
        
        if (aNBT.hasKey(NBT_PARALLEL_DURATION)) mParallelDuration = aNBT.getBoolean(NBT_PARALLEL_DURATION);
        if (aNBT.hasKey(NBT_PARALLEL))          mParallel = Math.max(1, aNBT.getInteger(NBT_PARALLEL));
        
        if (aNBT.hasKey(NBT_CANFILL_STEAM))     mCanFillSteam = aNBT.getBoolean(NBT_CANFILL_STEAM);
    }
    public void writeToNBT(NBTTagCompound aNBT) {
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        UT.NBT.setNumber(aNBT, NBT_MINENERGY, mMinEnergy);
        UT.NBT.setNumber(aNBT, NBT_OUTPUT, mOutputEnergy);
        UT.NBT.setNumber(aNBT, NBT_INPUT_EU, mChargeRequirement);
    
        UT.NBT.setNumber(aNBT, NBT_PROGRESS, mProgress);
        UT.NBT.setNumber(aNBT, NBT_MAXPROGRESS, mMaxProgress);
    }
    public void writeItemNBT(NBTTagCompound aNBT) {/**/}
    
    // tooltips
    @Override public void toolTipsMultiblock(List<String> aList) {/**/}
    @Override public void toolTipsRecipe(List<String> aList) {/**/}
    @Override public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(mTE.mRecipes.mNameInternal) + (mParallel > 1 ? " (up to "+mParallel+"x processed per run)" : ""));
        if (mTE.mCheapOverclocking)
            aList.add(LH.Chat.YELLOW + LH.get(LH.CHEAP_OVERCLOCKING));
        if (mTE.mEfficiency != 10000)
            aList.add(LH.getToolTipEfficiency(mTE.mEfficiency));
    }
    @Override public void toolTipsUseful(List<String> aList) {/**/}
    @Override public void toolTipsImportant(List<String> aList) {
        if (mTE.mRequiresIgnition) aList.add(LH.Chat.ORANGE   + LH.get(LH.REQUIREMENT_IGNITE_FIRE));
    }
    @Override public void toolTipsHazard(List<String> aList) {/**/}
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_TOGGLE_SCREWDRIVER));
        if (SIDES_VALID[mTE.mFluidAutoInput] || SIDES_VALID[mTE.mItemAutoInput])
            aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_TOGGLE_AUTO_INPUTS_MONKEY_WRENCH));
        if (SIDES_VALID[mTE.mFluidAutoOutput] || SIDES_VALID[mTE.mItemAutoOutput])
            aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_TOGGLE_AUTO_OUTPUTS_MONKEY_WRENCH));
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_RESET_SOFT_HAMMER));
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
    }
    @Override public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {
        String tSideNames = "";
        if (mTE.mEnergyTypeAccepted != TD.Energy.TU) {
            if (mTE.mEnergyInputs != 127) {
                for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mTE.mEnergyInputs]) {tSideNames += (UT.Code.stringValid(tSideNames)?", ":"")+LH.get(LH.FACES[tSide]);}
            }
            LH.addEnergyToolTips(mTE, aList, mTE.mEnergyTypeAccepted, null, tSideNames, null);
            tSideNames = "";
        }
        if (mTE.mEnergyTypeCharged != TD.Energy.TU) {
            if (mTE.mEnergyInputs != 127) {
                for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mTE.mEnergyInputs]) {tSideNames += (UT.Code.stringValid(tSideNames)?", ":"")+LH.get(LH.FACES[tSide]);}
            }
            LH.addEnergyToolTips(mTE, aList, mTE.mEnergyTypeCharged, null, tSideNames, null);
            tSideNames = "";
        }
        if (mTE.mRecipes.mInputItemsCount > 0) {
            if (mTE.mItemInputs != 127) {
                for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mTE.mItemInputs  ]) {tSideNames += (UT.Code.stringValid(tSideNames)?", ":"")+LH.get(LH.FACES[tSide])+(tSide==mTE.mItemAutoInput  ?" (auto)":"");}
                if (UT.Code.stringValid(tSideNames)) aList.add(LH.Chat.GREEN   + LH.get(LH.ITEM_INPUT)     + ": " + LH.Chat.WHITE + tSideNames);
                tSideNames = "";
            } else if (SIDES_VALID[mTE.mItemAutoInput]) {
                aList.add(LH.Chat.GREEN + LH.get(LH.ITEM_INPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACES[mTE.mItemAutoInput]) + " (auto, otherwise any)");
            } else {
                aList.add(LH.Chat.GREEN + LH.get(LH.ITEM_INPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACE_ANY) + " (no auto)");
            }
        }
        if (mTE.mRecipes.mOutputItemsCount > 0) {
            if (mTE.mItemOutputs != 127) {
                for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mTE.mItemOutputs ]) {tSideNames += (UT.Code.stringValid(tSideNames)?", ":"")+LH.get(LH.FACES[tSide])+(tSide==mTE.mItemAutoOutput ?" (auto)":"");}
                if (UT.Code.stringValid(tSideNames)) aList.add(LH.Chat.RED     + LH.get(LH.ITEM_OUTPUT)    + ": " + LH.Chat.WHITE + tSideNames);
                tSideNames = "";
            } else if (SIDES_VALID[mTE.mItemAutoOutput]) {
                aList.add(LH.Chat.RED + LH.get(LH.ITEM_OUTPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACES[mTE.mItemAutoOutput]) + " (auto, otherwise any)");
            } else {
                aList.add(LH.Chat.RED + LH.get(LH.ITEM_OUTPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACE_ANY) + " (no auto)");
            }
        }
        if (mTE.mRecipes.mInputFluidCount > 0) {
            if (mTE.mFluidInputs != 127) {
                for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mTE.mFluidInputs ]) {tSideNames += (UT.Code.stringValid(tSideNames)?", ":"")+LH.get(LH.FACES[tSide])+(tSide==mTE.mFluidAutoInput ?" (auto)":"");}
                if (UT.Code.stringValid(tSideNames)) aList.add(LH.Chat.GREEN   + LH.get(LH.FLUID_INPUT)    + ": " + LH.Chat.WHITE + tSideNames);
                tSideNames = "";
            } else if (SIDES_VALID[mTE.mFluidAutoInput]) {
                aList.add(LH.Chat.GREEN + LH.get(LH.FLUID_INPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACES[mTE.mFluidAutoInput]) + " (auto, otherwise any)");
            } else {
                aList.add(LH.Chat.GREEN + LH.get(LH.FLUID_INPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACE_ANY) + " (no auto)");
            }
        }
        if (mTE.mRecipes.mOutputFluidCount > 0) {
            if (mTE.mFluidOutputs != 127) {
                for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mTE.mFluidOutputs]) {tSideNames += (UT.Code.stringValid(tSideNames)?", ":"")+LH.get(LH.FACES[tSide])+(tSide==mTE.mFluidAutoOutput?" (auto)":"");}
                if (UT.Code.stringValid(tSideNames)) aList.add(LH.Chat.RED     + LH.get(LH.FLUID_OUTPUT)   + ": " + LH.Chat.WHITE + tSideNames);
            } else if (SIDES_VALID[mTE.mFluidAutoOutput]) {
                aList.add(LH.Chat.RED + LH.get(LH.FLUID_OUTPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACES[mTE.mFluidAutoOutput]) + " (auto, otherwise any)");
            } else {
                aList.add(LH.Chat.RED + LH.get(LH.FLUID_OUTPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACE_ANY) + " (no auto)");
            }
        }
    }
    
    public void onMagnifyingGlass(List<String> aChatReturn) {
        aChatReturn.add((mTE.mMode & 1) != 0 ?"Only produce when Output is completely empty":"Produce whenever there is space");
        aChatReturn.add((mTE.mMode & 2) != 0 ?"Only accept Input on empty Input Slots":"Accept Input on all Input Slots");
        if (SIDES_VALID[mTE.mItemAutoInput  ]) aChatReturn.add(mTE.mDisabledItemInput  ?"Auto Item Input Disabled"  :"Auto Item Input Enabled"  );
        if (SIDES_VALID[mTE.mItemAutoOutput ]) aChatReturn.add(mTE.mDisabledItemOutput ?"Auto Item Output Disabled" :"Auto Item Output Enabled" );
        if (SIDES_VALID[mTE.mFluidAutoInput ]) aChatReturn.add(mTE.mDisabledFluidInput ?"Auto Fluid Input Disabled" :"Auto Fluid Input Enabled" );
        if (SIDES_VALID[mTE.mFluidAutoOutput]) aChatReturn.add(mTE.mDisabledFluidOutput?"Auto Fluid Output Disabled":"Auto Fluid Output Enabled");
    }
    
    // GTCH, 阻止非自动输入输出面的自动连接
    public boolean interceptAutoConnectItem(byte aSide)  {if (SIDES_VALID[aSide] && (FACING_TO_SIDE[mTE.mFacing][mTE.mItemAutoInput]  == aSide || FACING_TO_SIDE[mTE.mFacing][mTE.mItemAutoOutput]  == aSide)) return F; else return T;}
    public boolean interceptAutoConnectFluid(byte aSide) {if (SIDES_VALID[aSide] && (FACING_TO_SIDE[mTE.mFacing][mTE.mFluidAutoInput] == aSide || FACING_TO_SIDE[mTE.mFacing][mTE.mFluidAutoOutput] == aSide)) return F; else return T;}
    // GTCH, 不能输入和输出的面阻止 MOD 管道连接
    public boolean interceptModConnectItem(byte aSide)   {return !FACE_CONNECTED[FACING_ROTATIONS[mTE.mFacing][aSide]][mTE.mItemInputs]  && !FACE_CONNECTED[FACING_ROTATIONS[mTE.mFacing][aSide]][mTE.mItemOutputs];}
    public boolean interceptModConnectFluid(byte aSide)  {return !FACE_CONNECTED[FACING_ROTATIONS[mTE.mFacing][aSide]][mTE.mFluidInputs] && !FACE_CONNECTED[FACING_ROTATIONS[mTE.mFacing][aSide]][mTE.mFluidOutputs];}
    
    
    // 配方处理相关
    // 你不会希望重写这么复杂的函数的，将其中的一些过程改成独立的函数单独重写
    protected final int canOutput(Recipe aRecipe) {
        mTE.doOutputItems();
        
        int rMaxTimes = calMaxProcessCountFirst(aRecipe);
        
        for (int i = 0, j = mTE.mRecipes.mInputItemsCount; i < mTE.mRecipes.mOutputItemsCount && i < aRecipe.mOutputs.length; i++, j++) if (ST.valid(aRecipe.mOutputs[i])) {
            if (mTE.slotHas(j)) {
                if ((mTE.mMode & 1) != 0 || aRecipe.mNeedsEmptyOutput) {
                    mTE.mOutputBlocked++;
                    return 0;
                }
                if (!ST.equal(mTE.slot(j), aRecipe.mOutputs[i], F)) {
                    mTE.mOutputBlocked++;
                    return 0;
                }
                rMaxTimes = Math.min(rMaxTimes, (mTE.slot(j).getMaxStackSize() - mTE.slot(j).stackSize) / aRecipe.mOutputs[i].stackSize);
                if (rMaxTimes <= 0) {
                    mTE.mOutputBlocked++;
                    return 0;
                }
            } else {
                rMaxTimes = Math.min(rMaxTimes, Math.max(1, 64 / aRecipe.mOutputs[i].stackSize));
            }
        }
        if (aRecipe.mFluidOutputs.length > 0) {
            int tEmptyOutputTanks = 0, tRequiredEmptyTanks = aRecipe.mFluidOutputs.length;
            for (FluidTankGT fluidTankGT : mTE.mTanksOutput) if (fluidTankGT.isEmpty()) tEmptyOutputTanks++; else if (aRecipe.mNeedsEmptyOutput || (mTE.mMode & 1) != 0) return 0;
            // This optimisation would not work! The Tanks would not have an Output Amount Limiter if this was in the Code!
            //if (tRequiredEmptyTanks <= tEmptyOutputTanks) {
            for (int j = 0; j < aRecipe.mFluidOutputs.length; j++) {
                if (aRecipe.mFluidOutputs[j] == null) {
                    tRequiredEmptyTanks--;
                } else for (FluidTankGT fluidTankGT : mTE.mTanksOutput) if (fluidTankGT.contains(aRecipe.mFluidOutputs[j])) {
                    if (fluidTankGT.has(Math.max(16000, 1+aRecipe.mFluidOutputs[j].amount*mParallel)) && !FluidsGT.VOID_OVERFLOW.contains(aRecipe.mFluidOutputs[j].getFluid().getName())) return 0;
                    tRequiredEmptyTanks--;
                    break;
                }
            }
            if (tRequiredEmptyTanks > tEmptyOutputTanks) return 0;
            //}
        }
        return rMaxTimes;
    }
    
    // 根据配方类型计算初步的最大并行数，是并行数的下界，用于重写
    protected int calMaxProcessCountFirst(Recipe aRecipe) {
        // Don't do more than 30 to 120 Seconds worth of Input at a time, when doing Chain Processing.
        if (mParallelDuration) {
            // Ugh, I do not feel like Maths right now, but the previous incarnation of this seemed a tiny bit wrong, so I will make sure it works properly.
//			while (rMaxTimes > 1 && aRecipe.getAbsoluteTotalPower() * rMaxTimes > mInputMax * 600) rMaxTimes--;
            // 重写原本的算法，并行不影响最低功率，保留原本结果
            return (int) UT.Code.bind(1, mParallel, mInputMax * 600 / aRecipe.getAbsoluteTotalPower());
        }
        return mParallel;
    }
    
    /** return codes for checkRecipe() */
    public static final int
    DID_NOT_FIND_RECIPE = 0,
    FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS = 1,
    FOUND_AND_SUCCESSFULLY_USED_RECIPE = 2,
    FOUND_AND_COULD_HAVE_USED_RECIPE = 3;
    
    /**
     * Override this to check the Recipes yourself, super calls to this could be useful if you just want to add a special case
     * I thought about Enum too, but Enum doesn't add support for people adding other return Systems.
     * Funny how Eclipse marks the word Enum as not correctly spelled.
     * @return see constants above
     */
    // 你不会希望重写这么复杂的函数的，将其中的一些过程改成独立的函数单独重写
    public final int checkRecipe(boolean aApplyRecipe, boolean aUseAutoIO) {
        mTE.mCouldUseRecipe = F;
        if (mTE.mRecipes == null) return DID_NOT_FIND_RECIPE;
        
        if (aUseAutoIO) mTE.doInputItems();
        
        int tInputItemsCount = 0, tInputFluidsCount = 0;
        ItemStack[] tInputs = new ItemStack[mTE.mRecipes.mInputItemsCount];
        for (int i = 0; i < mTE.mRecipes.mInputItemsCount; i++) {
            tInputs[i] = mTE.slot(i);
            if (ST.valid(tInputs[i])) tInputItemsCount++;
        }
        
        byte tAutoInput = FACING_TO_SIDE[mTE.mFacing][mTE.mFluidAutoInput];
        if (aUseAutoIO && !mTE.mDisabledFluidInput && SIDES_VALID[tAutoInput]) {
            DelegatorTileEntity<IFluidHandler> tTileEntity = mTE.getFluidInputTarget(tAutoInput);
            if (tTileEntity != null && tTileEntity.mTileEntity != null) {
                FluidTankInfo[] tInfos = tTileEntity.mTileEntity.getTankInfo(FORGE_DIR[tTileEntity.mSideOfTileEntity]);
                if (tInfos != null) for (FluidTankInfo tInfo : tInfos) if (tInfo != null && tInfo.fluid != null && tInfo.fluid.amount > 0 && mTE.canFill(FORGE_DIR[SIDE_ANY], tInfo.fluid.getFluid())) {
                    if (FL.move_(tTileEntity, mTE.delegator(tAutoInput), tInfo.fluid) > 0) mTE.updateInventory();
                }
            }
        }
        for (FluidTankGT tTank : mTE.mTanksInput) if (tTank.has()) tInputFluidsCount++;
        
        if (tInputItemsCount                     < mTE.mRecipes.mMinimalInputItems ) return DID_NOT_FIND_RECIPE;
        if (tInputFluidsCount                    < mTE.mRecipes.mMinimalInputFluids) return DID_NOT_FIND_RECIPE;
        if (tInputItemsCount + tInputFluidsCount < mTE.mRecipes.mMinimalInputs     ) return DID_NOT_FIND_RECIPE;
        
        Recipe tRecipe = mTE.mRecipes.findRecipe(mTE, mTE.mLastRecipe, F, mTE.mEnergyTypeAccepted == TD.Energy.RF ? mInputMax / RF_PER_EU : mInputMax, mTE.slot(mTE.mRecipes.mInputItemsCount+mTE.mRecipes.mOutputItemsCount), mTE.mTanksInput, tInputs);
        
        int tMaxProcessCount = 0;
        
        if (tRecipe == null) {
            if (!mTE.mCanUseOutputTanks) return DID_NOT_FIND_RECIPE;
            tRecipe = mTE.mRecipes.findRecipe(mTE, mTE.mLastRecipe, F, mTE.mEnergyTypeAccepted == TD.Energy.RF ? mInputMax / RF_PER_EU : mInputMax, mTE.slot(mTE.mRecipes.mInputItemsCount+mTE.mRecipes.mOutputItemsCount), mTE.mTanksOutput, tInputs);
            if (tRecipe == null) return DID_NOT_FIND_RECIPE;

            if (tRecipe.mCanBeBuffered) mTE.mLastRecipe = tRecipe;
            tMaxProcessCount = canOutput(tRecipe);
            if (tMaxProcessCount <= 0) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
            if (aApplyRecipe) aApplyRecipe = (!mTE.mRequiresIgnition || mTE.mIgnited > 0 || mTE.mActive);
            if (!tRecipe.isRecipeInputEqual(aApplyRecipe, F, mTE.mTanksOutput, tInputs)) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
            mTE.mCouldUseRecipe = T;
            if (!aApplyRecipe) return FOUND_AND_COULD_HAVE_USED_RECIPE;
            
            if (tMaxProcessCount > 1) {
                if (!mParallelDuration && mTE.mEnergyTypeAccepted != TD.Energy.TU) tMaxProcessCount = (int)UT.Code.bind(1, tMaxProcessCount, getBoundInput() / Math.max(1, (mTE.mEnergyTypeAccepted == TD.Energy.RF ? tRecipe.mEUt * RF_PER_EU : tRecipe.mEUt)));
                tMaxProcessCount = 1+tRecipe.isRecipeInputEqual(tMaxProcessCount-1, mTE.mTanksOutput, tInputs);
            }
        } else {
            if (tRecipe.mCanBeBuffered) mTE.mLastRecipe = tRecipe;
            tMaxProcessCount = canOutput(tRecipe);
            if (tMaxProcessCount <= 0) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
            if (aApplyRecipe) aApplyRecipe = (!mTE.mRequiresIgnition || mTE.mIgnited > 0 || mTE.mActive);
            if (!tRecipe.isRecipeInputEqual(aApplyRecipe, F, mTE.mTanksInput, tInputs)) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
            mTE.mCouldUseRecipe = T;
            if (!aApplyRecipe) return FOUND_AND_COULD_HAVE_USED_RECIPE;
            
            if (tMaxProcessCount > 1) {
                if (!mParallelDuration && mTE.mEnergyTypeAccepted != TD.Energy.TU) tMaxProcessCount = (int)UT.Code.bind(1, tMaxProcessCount, getBoundInput() / Math.max(1, (mTE.mEnergyTypeAccepted == TD.Energy.RF ? tRecipe.mEUt * RF_PER_EU : tRecipe.mEUt)));
                tMaxProcessCount = 1+tRecipe.isRecipeInputEqual(tMaxProcessCount-1, mTE.mTanksInput, tInputs);
            }
        }
        
        for (byte tSide : ALL_SIDES_VALID_FIRST[FACING_TO_SIDE[mTE.mFacing][mTE.mItemAutoInput]]) if (FACE_CONNECTED[FACING_ROTATIONS[mTE.mFacing][tSide]][mTE.mItemInputs]) {
            DelegatorTileEntity<IInventory> tDelegator = mTE.getItemInputTarget(tSide);
            if (tDelegator != null && tDelegator.mTileEntity instanceof ITileEntityAdjacentInventoryUpdatable) {
                ((ITileEntityAdjacentInventoryUpdatable)tDelegator.mTileEntity).adjacentInventoryUpdated(tDelegator.mSideOfTileEntity, mTE);
            }
        }
        
        if (mTE.mSpecialIsStartEnergy && (!mTE.mActive || (mTE.mCurrentRecipe != null && mTE.mCurrentRecipe != tRecipe))) mChargeRequirement = tRecipe.mSpecialValue;
        
        mTE.mCurrentRecipe = tRecipe;
        mTE.mOutputItems   = tRecipe.getOutputs(RNGSUS, tMaxProcessCount);
        mTE.mOutputFluids  = tRecipe.getFluidOutputs(RNGSUS, tMaxProcessCount);
        
        if (tRecipe.mEUt < 0) {
            mOutputEnergy = -tRecipe.mEUt;
            mMaxProgress = tRecipe.mDuration;
            mMinEnergy = 0;
        } else {
            calMaxProgress(tMaxProcessCount, tRecipe);
        }
        
        mTE.removeAllDroppableNullStacks();
        return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
    }
    
    // 返回限制并行达到的最低功率 ，用于重写来实现自适应并行数
    protected long getBoundInput() {
        return mInput;
    }
    
    // 根据并行数，配方类型等计算处理需要的能量和最低能量，用于重写
    protected void calMaxProgress(int aProcessCount, Recipe aRecipe) {
        if (mParallelDuration) {
            mMinEnergy = Math.max(1, (mTE.mEnergyTypeAccepted == TD.Energy.RF ? aRecipe.mEUt * RF_PER_EU : aRecipe.mEUt));
            mMaxProgress = Math.max(1, UT.Code.units(mMinEnergy * Math.max(1, aRecipe.mDuration) * aProcessCount, mTE.mEfficiency, 10000, T));
        } else {
            mMinEnergy = Math.max(1, (mTE.mEnergyTypeAccepted == TD.Energy.RF ? aRecipe.mEUt * RF_PER_EU * aProcessCount : mTE.mEnergyTypeAccepted == TD.Energy.TU ? aRecipe.mEUt : aRecipe.mEUt * aProcessCount));
            mMaxProgress = Math.max(1, UT.Code.units(mMinEnergy * Math.max(1, aRecipe.mDuration), mTE.mEfficiency, 10000, T));
        }
        if (!mTE.mCheapOverclocking) while (mMinEnergy < mInputMin && mMinEnergy * 4 <= mInputMax) {mMinEnergy *= 4; mMaxProgress *= 2;}
    }
    
    @Override public void doWorkFirst(long aTimer) {/**/}
    
    @Override public boolean doWorkCheck(long aTimer) {
        return mEnergy >= mInputMin && mEnergy >= mMinEnergy && mTE.isStructureOkay();
    }
    @Override public void doWorkActive(long aTimer) {
        mTE.mActive = doActive(aTimer, Math.min(mInputMax, mEnergy));
        mTE.mRunning = T;
    }
    @Override public void doWorkInactive(long aTimer) {
        if (aTimer > 40) {
            mTE.mActive = doInactive(aTimer);
            mTE.mRunning = F;
        }
        mTE.mSuccessful = F;
    }
    @Override public void doWorkFinal(long aTimer) {
        mEnergy -= mInputMax; if (mEnergy < 0) mEnergy = 0;
        if (mTE.mIgnited > 0) mTE.mIgnited--;
    }
    
    // 你不会希望重写这么复杂的函数的，将其中的一些过程改成独立的函数单独重写
    protected final boolean doActive(long aTimer, long aEnergy) {
        boolean rActive = F;

        if (mMaxProgress <= 0) {
            // Successfully produced something or just got ignited || Some Inventory Stuff changes || The Machine has just been turned ON || Check once every Minute
            if ((mTE.mIgnited > 0 || mTE.mInventoryChanged || !mTE.mRunning || aTimer%1200 == 5) && checkRecipe(!mTE.mStopped, T) == FOUND_AND_SUCCESSFULLY_USED_RECIPE) {
                mTE.onProcessStarted();
            } else {
                mProgress = 0;
            }
        }
        
        mTE.mSuccessful = F;
        
        if (mMaxProgress > 0 && !(mTE.mSpecialIsStartEnergy && mChargeRequirement > 0)) {
            rActive = T;
            if (mProgress <= mMaxProgress) {
                if (mOutputEnergy > 0) mTE.doOutputEnergy();
                mProgress += aEnergy;
            }
            if (mProgress >= mMaxProgress && (mTE.mStateOld&&!mTE.mStateNew || !TD.Energy.ALL_ALTERNATING.contains(mTE.mEnergyTypeAccepted))) {
                for (int i = 0; i < mTE.mOutputItems .length; i++) {
                    if (mTE.mOutputItems [i] != null && mTE.addStackToSlot(mTE.mRecipes.mInputItemsCount+(i % mTE.mRecipes.mOutputItemsCount), mTE.mOutputItems[i])) {
                        mTE.mSuccessful = T;
                        mTE.mIgnited = 40;
                        mTE.mOutputItems[i] = null;
                        continue;
                    }
                }
                for (int i = 0; i < mTE.mOutputFluids.length; i++) if (mTE.mOutputFluids[i] != null) for (int j = 0; j < mTE.mTanksOutput.length; j++) {
                    if (mTE.mTanksOutput[j].contains(mTE.mOutputFluids[i])) {
                        mTE.updateInventory();
                        mTE.mTanksOutput[j].add(mTE.mOutputFluids[i].amount);
                        mTE.mSuccessful = T;
                        mTE.mIgnited = 40;
                        mTE.mOutputFluids[i] = null;
                        break;
                    }
                }
                for (int i = 0; i < mTE.mOutputFluids.length; i++) if (mTE.mOutputFluids[i] != null) for (int j = 0; j < mTE.mTanksOutput.length; j++) {
                    if (mTE.mTanksOutput[j].isEmpty()) {
                        mTE.mTanksOutput[j].setFluid(mTE.mOutputFluids[i]);
                        mTE.mSuccessful = T;
                        mTE.mIgnited = 40;
                        mTE.mOutputFluids[i] = null;
                        break;
                    }
                }
                
                if (UT.Code.containsSomething(mTE.mOutputItems) || UT.Code.containsSomething(mTE.mOutputFluids)) {
                    mMinEnergy = 0;
                    mOutputEnergy = 0;
                    mChargeRequirement = 0;
                    mProgress = mMaxProgress;
                } else {
                    mProgress -= mMaxProgress; // this way the leftover energy can be used on the next processed thing, unless it gets stuck on an output.
                    mMinEnergy = 0;
                    mMaxProgress = 0;
                    mOutputEnergy = 0;
                    mChargeRequirement = 0;
                    mTE.mOutputItems = ZL_IS;
                    mTE.mOutputFluids = ZL_FS;
                    mTE.mSuccessful = T;
                    mTE.mIgnited = 40;

                    for (byte tSide : ALL_SIDES_VALID_FIRST[FACING_TO_SIDE[mTE.mFacing][mTE.mItemAutoOutput]]) if (FACE_CONNECTED[FACING_ROTATIONS[mTE.mFacing][tSide]][mTE.mItemOutputs]) {
                        DelegatorTileEntity<TileEntity> tDelegator = mTE.getItemOutputTarget(tSide);
                        if (tDelegator != null && tDelegator.mTileEntity instanceof ITileEntityAdjacentInventoryUpdatable) {
                            ((ITileEntityAdjacentInventoryUpdatable)tDelegator.mTileEntity).adjacentInventoryUpdated(tDelegator.mSideOfTileEntity, mTE);
                        }
                    }
                    
                    mTE.onProcessFinished();
                }
            }
        }
        
        mTE.mStateOld = mTE.mStateNew;
        
        if (!mTE.mDisabledItemOutput && SIDES_VALID[mTE.mItemAutoOutput]) {
            boolean
            tOutputEmpty = T;
            for (int i = mTE.mRecipes.mInputItemsCount, j = i + mTE.mRecipes.mOutputItemsCount; i < j; i++) if (mTE.slotHas(i)) {tOutputEmpty = F; break;}
            
            // Output not Empty && (Successfully produced something or just got ignited || Some Inventory Stuff changes || The Machine has just been turned ON || Output has been blocked since 256 active ticks || Check once every 10 Seconds)
            if (!tOutputEmpty && (mTE.mIgnited > 0 || mTE.mInventoryChanged || !mTE.mRunning || mTE.mOutputBlocked == 1 || aTimer%200 == 5)) {
                boolean tInventoryChanged = mTE.mInventoryChanged;
                mTE.mInventoryChanged = F;
                mTE.doOutputItems();
                if (mTE.mInventoryChanged) mTE.mOutputBlocked = 0; else mTE.mInventoryChanged |= tInventoryChanged;
            }
            
            tOutputEmpty = T;
            for (int i = mTE.mRecipes.mInputItemsCount, j = i + mTE.mRecipes.mOutputItemsCount; i < j; i++) if (mTE.slotHas(i)) {tOutputEmpty = F; mTE.mOutputBlocked++; break;}
            
            if (tOutputEmpty) mTE.mOutputBlocked = 0;
        }
        
        return rActive;
    }
    
    protected boolean doInactive(long aTimer) {
        if (mTE.mActive) {
            mTE.doSoundInterrupt();
            mTE.doOutputItems();
            if (!mTE.mDisabledItemOutput) mTE.doOutputItems();
        }
        if (CONSTANT_ENERGY && !mTE.mNoConstantEnergy) mProgress = 0;
        if (mTE.mRunning || mTE.mIgnited > 0 || mTE.mInventoryChanged || aTimer % 1200 == 5) {
            mTE.checkRecipe(F, T);
        }
        return F;
    }
    
    
    public boolean hasWork() {return mMaxProgress > 0 || mChargeRequirement > 0;}
    public long getProgressValue(byte aSide) {return mTE.mSuccessful ? getProgressMax(aSide) : mMinEnergy < 1 ? mProgress    : UT.Code.divup(mProgress    , mMinEnergy ) ;}
    public long getProgressMax  (byte aSide) {return Math.max(1,                               mMinEnergy < 1 ? mMaxProgress : UT.Code.divup(mMaxProgress , mMinEnergy ));}
    
    public boolean canFillExtra(FluidStack aFluid) {
        if (mCanFillSteam) return FL.anysteam(aFluid);
        return F;
    }
}
