package gregtechCH.tileentity.cores.motors;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.recipes.Recipe;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.MTEC_HasTanks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public class MTEC_MotorMainFluidBurner extends MTEC_MotorMainBase {
    // the instance of MTEC_HasTanks
    protected final MTEC_HasTanks mCTanks;
    protected MTEC_MotorMainFluidBurner(MTEC_Motor aCore) {this(aCore, ZL_FT);}
    protected MTEC_MotorMainFluidBurner(MTEC_Motor aCore, FluidTankGT[] aOutputTanks) {super(aCore); mCTanks = new MTEC_HasTanks(aCore, mTankFluid.AS_ARRAY, aOutputTanks);}
    
    
    /* main code */
    protected boolean mBurning = F;
    protected long mEnergyHU = 0;
    protected long mPRate = 64, mInPRate = 64; // preheat rate, input preheat rate
    
    protected static final byte COOLDOWN_NUM = 16;
    protected byte mBurningCounter = 0;  // 注意默认是停止工作的
    
    protected Recipe.RecipeMap mRecipes = null;
    protected Recipe mLastRecipe = null;
    protected FluidTankGT mTankFluid = new FluidTankGT(1000);
    
    // NBT读写
    @Override
    public void init(NBTTagCompound aNBT) {
        super.init(aNBT);
        if (aNBT.hasKey(NBT_COOLDOWN_COUNTER)) mBurningCounter = aNBT.getByte(NBT_COOLDOWN_COUNTER);
        if (aNBT.hasKey(NBT_BURNING)) mBurning = aNBT.getBoolean(NBT_BURNING);
        if (aNBT.hasKey(NBT_ENERGY_HU)) mEnergyHU = aNBT.getLong(NBT_ENERGY_HU);

        if (aNBT.hasKey(NBT_FUELMAP)) mRecipes = Recipe.RecipeMap.RECIPE_MAPS.get(aNBT.getString(NBT_FUELMAP));

        mCTanks.readFromNBT(aNBT);
    }
    @Override
    protected void setOutRate(NBTTagCompound aNBT) {
        super.setOutRate(aNBT);
        if (aNBT.hasKey(NBT_PREHEAT_RATE)) mPRate = aNBT.getLong(NBT_PREHEAT_RATE);
    }
    @Override
    protected void setInRate2() {
        super.setInRate2();
        mInPRate = UT.Code.units(mPRate, mEfficiency, 10000, T);
    }
    @Override public void postInitTank() {mTankFluid.setCapacity(mRate * 8);} // core 不进行容量设定
    
    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        UT.NBT.setNumber(aNBT, NBT_COOLDOWN_COUNTER, mBurningCounter);
        UT.NBT.setBoolean(aNBT, NBT_BURNING, mBurning);
        UT.NBT.setNumber(aNBT, NBT_ENERGY_HU, mEnergyHU);

        mCTanks.writeToNBT(aNBT);
    }
    
    // 每 tick 转换，统一采用燃气涡轮的逻辑
    protected boolean isTankOutputFull() {
        if (mCTanks.mTanksOutput.length == 0) return T;
        for (FluidTankGT tTank : mCTanks.mTanksOutput) if (tTank.isHalf()) return T;
        return F;
    }
    @Override
    public void onTickConvert(long aTimer) {
        // 燃烧
        if (mBurning) {
            // Check if it needs to burn more Fuel, or if the buffered Energy is enough.
            if (mEnergyHU < mInPRate * 4 + mInPCost) {
                // Find and apply fitting Recipe.
                Recipe tRecipe = mRecipes.findRecipe(mCore.mTE, mLastRecipe, T, Long.MAX_VALUE, NI, mCTanks.mTanksInput, ZL_IS);
                if (tRecipe != null) {
                    mLastRecipe = tRecipe;
                    long tRecipeEnergy = tRecipe.getAbsoluteTotalPower();
                    int tMax = UT.Code.bindInt(UT.Code.divup(mInPRate, tRecipeEnergy));
                    int tParallel = tRecipe.isRecipeInputEqual(tMax, mCTanks.mTanksInput, ZL_IS);
                    if (tParallel > 0) {
                        mEnergyHU += tParallel * tRecipeEnergy;
                        for (int i = 0; i < tRecipe.mFluidOutputs.length; ++i) if (i < mCTanks.mTanksOutput.length) { // 超出输出 tank 的废气种类不会用于填充
                            // 填充废气，并且判断是否填充成功
                            if (!mCTanks.mTanksOutput[i].fillAll(tRecipe.mFluidOutputs[i], tParallel)) {
                                // 废气清空速度不够，停止燃烧（主要是种类检测）
                                mBurning = F;
                                // 因为可能会 VoidExceed，所以不放入垃圾桶里（防止刷物品）
                            }
                        }
                    } else if (mEnergyHU < mInPCost) {
                        // 有剩余燃料，有熄火风险，清空容器来切换燃料
                        mTankFluid.setEmpty();
                    }
                } else {
                    // 目前是直接清除不匹配的液体
                    mTankFluid.setEmpty();
                }
            }
            // 能量不够, 机器停止，废气填满则停止燃烧
            if (mEnergyHU >= mInPCost) mBurningCounter = COOLDOWN_NUM;
            else --mBurningCounter;
            if (mBurningCounter <= 0 || mStopped || isTankOutputFull()) {
                mBurning = F;
                mBurningCounter = 0;
            }
        }
        // 热量按照效率转换为旋转能
        if (mEnergy < mPEnergy){
            // 预热阶段
            if (mEnergy < mPEnergy - mPRate) convert(mInPRate, mPRate);
            else convert(mInRate, mRate);
        } else {
            // 运行阶段
            mEnergy = mPEnergy; // 只是为了好看
            convert(mInRate, mRate, T);
        }
        // 自动输出废气
        long tFluid;
        for (FluidTankGT tankOutput : mCTanks.mTanksOutput) {
            if (tankOutput.has()) {
                FL.move(tankOutput, mCore.getFluidEmitter().getAdjacentTank(mCore.getFluidEmittingSide()));
                tFluid = tankOutput.amount() - tankOutput.capacity() / 4;
                if (tFluid > 0 && !WD.hasCollide(mCore.mTE.getWorldObj(), mCore.getFluidEmitter().getOffset(mCore.getFluidEmittingSide(), 1))) {
                    GarbageGT.trash(tankOutput, tFluid);
                }
            }
        }
    }
    // 根据输入输出直接转换，最后一项为 T 时会在输入热量不够输出阈值（但是大于预热损耗）时是否浪费掉能量
    protected void convert(long aInRate, long aOutRate) {
        convert(aInRate, aOutRate, F);
    }
    protected void convert(long aInRate, long aOutRate, boolean aWasteEnergy) {
        if (mEnergyHU >= aInRate) {
            mEnergy += aOutRate;
            mEnergyHU -= aInRate;
            return;
        }
        if (aWasteEnergy && mEnergyHU > mInPCost) {
            mEnergy += mPCost;
            mEnergyHU = 0;
            return;
        }
        if (mEnergyHU > 0) {
            mEnergy += UT.Code.units(mEnergyHU, 10000, mEfficiency, F);
            mEnergyHU = 0;
            return;
        }
        mEnergyHU = 0;
    }
    
    @Override protected long getActiveOutput() {return mRate;}
    @Override protected boolean onTickCheckPreheat2() {return mBurning;}
    @Override protected boolean onTickCheckCooldown2() {return !mBurning;}
    @Override public void onTickDoCooldown(long aTimer) {
        super.onTickDoCooldown(aTimer);
        mBurningCounter = 0;
        mBurning = F;
        mEnergyHU = 0;
    }
    @Override public void onTickDoElse(long aTimer) {
        // 能量耗尽，但是不熄火（因为熄火不在这里判断）
        if (!mBurning) stop();
        else super.onTickDoElse(aTimer);
    }
    @Override
    public void stop() {
        super.stop();
        mBurningCounter = 0;
        mBurning = F;
        mEnergyHU = 0;
    }
    
    // 重复的接口消除
    @Override protected long getRealEfficiency() {return UT.Code.units(10000, mInRate, mRate, F);}
    @Override public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return mRecipes.containsInput(aFluidToFill, mCore.mTE, NI) ? mTankFluid : null;}
    @Override public IFluidTank getFluidTankDrainable(byte aSide, FluidStack aFluidToDrain) {return mCTanks.getFluidTankDrainable(aSide, aFluidToDrain);}
    @Override public IFluidTank[] getFluidTanks(byte aSide) {return mCTanks.getFluidTanks(aSide);}
    @Override public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
        if (!mRecipes.containsInput(aFluid, mCore.mTE, NI)) return 0;
        mCore.mTE.updateInventory();
        return mTankFluid.fill(aFluid, aDoFill);
    }
    @Override public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
        mCore.mTE.updateInventory();
        return mCTanks.tapDrain(aSide, aMaxDrain, aDoDrain);
    }
    
    @Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return F;}
    @Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mRate;}
    
    @Override public boolean getStateRunningPossible() {return mActive || (mTankFluid.has() && !isTankOutputFull());}
    
    
    // 重复的接口实现消除
    public void toolTipsRecipe_burn(List<String> aList) {aList.add(LH.Chat.CYAN + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(mRecipes.mNameInternal));}
    public void toolTipsImportant_igniteFire(List<String> aList) {aList.add(LH.Chat.ORANGE + LH.get(LH.REQUIREMENT_IGNITE_FIRE) + " (" + LH.get(LH.FACE_ANY) + ")");}
    public void toolTipsOther_sneakMagnify(List<String> aList) {aList.add(LH.Chat.DGRAY + LH_CH.get(LH_CH.TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK));}
    public long onToolClickFirst_sneakMagnify(String aTool, List<String> aChatReturn, boolean aSneaking) {
        if (aTool.equals(TOOL_magnifyingglass) && aSneaking) {
            if (aChatReturn != null) mCTanks.onMagnifyingGlass(aChatReturn);
            return 1;
        }
        return 0;
    }
    public long onToolClickLast_plungerIgniterExtinguisher(String aTool, Entity aPlayer, List<String> aChatReturn, boolean aSneakingZ) {
        if (aTool.equals(TOOL_plunger)) {
            if (aChatReturn != null) return mCTanks.onPlunger(aPlayer, aChatReturn);
            return 0;
        }
        
        if (aTool.equals(TOOL_igniter)) {mBurning = T; return 10000;}
        if (aTool.equals(TOOL_extinguisher)) {mBurning = F; return 10000;}
        
        return 0;
    }
}
