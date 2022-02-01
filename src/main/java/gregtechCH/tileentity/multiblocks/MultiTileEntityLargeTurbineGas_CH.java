package gregtechCH.tileentity.multiblocks;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.old.Textures;
import gregapi.recipes.Recipe;
import gregapi.render.IIconContainer;
import gregapi.tileentity.multiblocks.*;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.data.LH_CH;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

public class MultiTileEntityLargeTurbineGas_CH extends MultiTileEntityLargeMotor_CH implements IMultiBlockFluidHandler, IFluidHandler {

    protected boolean mBurning = F;
    protected long mEnergyHU;
    protected long mPRate = 16384, mInPRate = 16384;

    protected long[] mPRateArray;

    protected Recipe.RecipeMap mRecipes = FM.Gas;
    protected Recipe mLastRecipe = null;
    protected FluidTankGT mInputTank = new FluidTankGT(1000);
    public FluidTankGT[] mTanksOutput = new FluidTankGT[] {new FluidTankGT(), new FluidTankGT(), new FluidTankGT()};
    public FluidTankGT[] mTanks = new FluidTankGT[] {mInputTank, mTanksOutput[0], mTanksOutput[1], mTanksOutput[2]};

    // NBT读写
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_BURNING)) mBurning = aNBT.getBoolean(NBT_BURNING);
        if (aNBT.hasKey(NBT_ENERGY_HU)) mEnergyHU = aNBT.getLong(NBT_ENERGY_HU);

        if (aNBT.hasKey(NBT_FUELMAP)) mRecipes = Recipe.RecipeMap.RECIPE_MAPS.get(aNBT.getString(NBT_FUELMAP));
        for (int i = 0; i < mTanksOutput.length; i++) mTanksOutput[i].readFromNBT(aNBT, NBT_TANK+"."+i);
        mInputTank.readFromNBT(aNBT, NBT_TANK);
    }
    @Override
    protected void setEnergyArray(NBTTagCompound aNBT, int aArrayLen) {
        super.setEnergyArray(aNBT, aArrayLen);
        mPRateArray = new long[aArrayLen];
        for (int i = 0; i < aArrayLen; ++i) {
            if (aNBT.hasKey(NBT_PREHEAT_RATE+"."+i)) mPRateArray[i] = aNBT.getLong(NBT_PREHEAT_RATE+"."+i);
        }
    }
    @Override
    protected void setEnergyByLength2(int aI) {
        super.setEnergyByLength2(aI);
        for (FluidTankGT tankOutput : mTanksOutput) tankOutput.setCapacity(mInRate * 16);
        mInputTank.setCapacity(mInRate*8);
    }
    @Override
    protected void setOutRateByLength(int aI) {
        super.setOutRateByLength(aI);
        mPRate = mPRateArray[aI];
    }
    @Override
    protected void setInRateByLength(int aI) {
        super.setInRateByLength(aI);
        mInPRate = UT.Code.units(mPRate, mEfficiency, 10000, T);
    }
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setBoolean(aNBT, NBT_BURNING, mBurning);
        UT.NBT.setNumber(aNBT, NBT_ENERGY_HU, mEnergyHU);

        for (int i = 0; i < mTanksOutput.length; i++) {
            mTanksOutput[i].writeToNBT(aNBT, NBT_TANK + "." + i);
        }
        mInputTank.writeToNBT(aNBT, NBT_TANK);
    }

    // 多方快结构

    // tooltips
    @Override
    protected void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(mRecipes.mNameInternal));
        super.toolTipsEnergy(aList);
    }
    @Override
    protected void toolTipsImportant(List<String> aList) {
        aList.add(LH.Chat.ORANGE   + LH.get(LH.REQUIREMENT_IGNITE_FIRE) + " (" + LH.get(LH.FACE_ANY) + ")");
        super.toolTipsImportant(aList);
    }
    @Override
    protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY   + LH_CH.get("gtch.tooltip.multiblock.gasturbine.4"));
        aList.add(LH.Chat.DGRAY    + LH_CH.get(LH_CH.TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK));
        super.toolTipsOther(aList, aStack, aF3_H);
    }
    @Override
    protected void toolTipsMultiblock(List<String> aList) {
        super.toolTipsMultiblock(aList);
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.5") + " " + mMinLength + " " + LH_CH.get(LH_CH.ENERGY_TO) + " " + mMaxLength);
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.2"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.3"));
    }
    static {
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.1", "3x3xN of the Walls you crafted this with");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.2", "Main centered on the 3x3 facing outwards");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.3", "Input only possible at frontal 3x3");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.4", "Exhaust Gas can still be pumped out at Bottom Layer");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.5", "N can be from");
    }

    // 工具右键
    @Override
    public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (aTool.equals(TOOL_magnifyingglass) && aSneaking) {
            if (aChatReturn != null) {
                aChatReturn.add(mInputTank.content());
                if (mTanksOutput[0].has()) aChatReturn.add(mTanksOutput[0].content());
                if (mTanksOutput[1].has()) aChatReturn.add(mTanksOutput[1].content());
                if (mTanksOutput[2].has()) aChatReturn.add(mTanksOutput[2].content());
            }
            return 1;
        }
        long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (rReturn > 0) return rReturn;

        if (isClientSide()) return 0;

        if (aTool.equals(TOOL_igniter)) {mBurning = T; return 10000;}
        if (aTool.equals(TOOL_extinguisher  )) {mBurning = F; return 10000;}

        if (aTool.equals(TOOL_plunger)) {
            if (mTanksOutput[0].has()) return GarbageGT.trash(mTanksOutput[0]);
            if (mTanksOutput[1].has()) return GarbageGT.trash(mTanksOutput[1]);
            if (mTanksOutput[2].has()) return GarbageGT.trash(mTanksOutput[2]);
            return GarbageGT.trash(mInputTank);
        }
        return 0;
    }

    // 每 tick 转换
    protected boolean isTankOutputFull() {
        return mTanksOutput[0].isHalf()||mTanksOutput[1].isHalf()||mTanksOutput[2].isHalf();
    }
    @Override
    protected void convert() {
        // 燃烧
        if (mBurning) {
            // Check if it needs to burn more Fuel, or if the buffered Energy is enough.
            if (mEnergyHU < mInPRate * 8 + mCRate) {
                // Find and apply fitting Recipe.
                Recipe tRecipe = mRecipes.findRecipe(this, mLastRecipe, F, Long.MAX_VALUE, NI, mInputTank.AS_ARRAY, ZL_IS);
                if (tRecipe != null) {
                    mLastRecipe = tRecipe;
                    long tRecipeEnergy = tRecipe.getAbsoluteTotalPower();
                    int tMax = UT.Code.bindInt(UT.Code.divup(mInPRate, tRecipeEnergy));
                    int tParallel = tRecipe.isRecipeInputEqual(tMax, mInputTank.AS_ARRAY, ZL_IS);
                    if (tParallel > 0) {
                        mEnergyHU += tParallel * tRecipeEnergy;
                        for (int i = 0; i < tRecipe.mFluidOutputs.length && i < mTanksOutput.length; i++) {
                            // 填充废气，并且判断是否填充成功
                            if (!mTanksOutput[i].fillAll(tRecipe.mFluidOutputs[i], tParallel)) {
                                // 废气清空速度不够，停止燃烧
                                mBurning = F;
                            }
                        }
                    } else if (mEnergyHU < mInPRate * 4 + mCRate) {
                        // 有剩余燃料，有熄火风险，清空容器来切换燃料
                        mInputTank.setEmpty();
                    }
                } else {
                    // 目前是直接清除不匹配的液体
                    mInputTank.setEmpty();
                }
            }
            // 能量不够, 机器停止，废气填满则停止燃烧
            if (mEnergyHU < mCRate || mStopped || isTankOutputFull()) mBurning = F;
        }
        // 热量按照效率转换为旋转能
        if (mPreheat){
            convert(mInPRate, mPRate);
        } else {
            convert(mInRate, mRate);
        }
        // 自动输出废气
        long tFluid;
        for (FluidTankGT tankOutput : mTanksOutput) {
            if (tankOutput.has()) {
                FL.move(tankOutput, getFluidEmitter().getAdjacentTank(getEmittingSide()));
                tFluid = tankOutput.amount() - tankOutput.capacity() / 4;
                if (tFluid > 0 && !WD.hasCollide(worldObj, getFluidEmitter().getOffset(getEmittingSide(), 1))) {
                    GarbageGT.trash(tankOutput, tFluid);
                }
            }
        }
    }
    protected void convert(long aInRate, long aOutRate) {
        if (mEnergyHU >= aInRate) {
            mEnergy += aOutRate;
            mEnergyHU -= aInRate;
        } else
        if (mEnergyHU > 0) {
            mEnergy += UT.Code.units(mEnergyHU, 10000, mEfficiency, F);
            mEnergyHU = 0;
        } else {
            mEnergyHU = 0;
        }
    }
    @Override
    protected boolean checkOverload() {
        return F;
    }
    @Override
    protected long getOutput() {
        return mRate;
    }
    @Override
    protected boolean checkPreheat() {
        return super.checkPreheat() && mBurning;
    }
    @Override
    protected boolean checkCooldown() {
        return super.checkCooldown() && !mBurning;
    }
    @Override
    protected void doElse() {
        // 能量耗尽，但是不熄火（因为熄火不在这里判断）
        mActive = F;
        mPreheat = F;
        mCooldown = F;
        mOutput = 0;
        mEnergy = 0;
    }
    @Override
    protected void stop() {
        super.stop();
        mBurning = F;
        mEnergyHU = 0;
    }

    // 一些接口
    @Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return !mStopped && mRecipes.containsInput(aFluidToFill, this, NI) ? mInputTank : null;}
    @Override protected IFluidTank[] getFluidTanks2(byte aSide) {return mTanks;}
    @Override
    protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {
        if (aFluidToDrain == null) {
            for (int i = 0; i < mTanksOutput.length; i++) if (!mTanksOutput[i].isEmpty()) return mTanksOutput[i];
        } else {
            for (int i = 0; i < mTanksOutput.length; i++) if (mTanksOutput[i].contains(aFluidToDrain)) return mTanksOutput[i];
        }
        return null;
    }

    @Override public boolean isEnergyType                   (TagData aEnergyType, byte aSide, boolean aEmitting) {return aEmitting && aEnergyType == mEnergyTypeEmitted;}
    @Override public boolean isEnergyAcceptingFrom          (TagData aEnergyType, byte aSide, boolean aTheoretical) {return F;}
    @Override public long getEnergyOffered                  (TagData aEnergyType, byte aSide, long aSize) {return Math.min(mRate, mEnergy);}
    @Override public long getEnergySizeOutputMin            (TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeOutputRecommended    (TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeOutputMax            (TagData aEnergyType, byte aSide) {return mRate;}
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeEmitted.AS_LIST;}

    @Override public boolean getStateRunningPossible() {return mBurning || (mInputTank.has() && !isTankOutputFull());}

    // Icons，图像动画
    public static final IIconContainer mTextureInactive = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine");
    public static final IIconContainer mTextureActive   = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active");
    public static final IIconContainer mTexturePreheat  = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat");
    public static final IIconContainer mTextureActiveL   = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active_l");
    public static final IIconContainer mTexturePreheatL  = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat_l");
    @Override
    public IIconContainer getIIconContainer(IconType aIconType) {
        switch (aIconType) {
            case OVERLAY_ACTIVE_L: return mTextureActiveL;
            case OVERLAY_ACTIVE_R: return mTextureActive;
            case OVERLAY_PREHEAT_L: return mTexturePreheatL;
            case OVERLAY_PREHEAT_R: return mTexturePreheat;
            case OVERLAY:
            default: return mTextureInactive;
        }
    }

    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.turbine.gas";}
}

