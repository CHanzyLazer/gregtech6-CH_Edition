package gregtechCH.tileentity.cores.motors;

import gregapi.code.TagData;
import gregapi.data.TD;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import gregtechCH.config.ConfigForge;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

/* 将重复的主要部分单独拆出来 */
public abstract class MTEC_MotorMainBase implements IMTEC_MotorTick {
    // the instance of MTEC_Motor
    protected final MTEC_Motor mCore;
    protected MTEC_MotorMainBase(MTEC_Motor aCore) {mCore = aCore;}
    
    /* main code */
    protected short mEfficiency = 1000;
    protected long mEnergy = 0, mRate = 16, mInRate = 16;
    protected long mPEnergy = 1600; // preheat energy
    protected long mPCost = 2, mCRate = 16; // preheat cost, cool-down rate
    protected long mInPCost = 4; // input preheat cost
    protected long mOutput = 0;
    
    protected boolean mStopped = F;
    protected boolean mEmitsEnergy = F;
    protected boolean mActive = F, oActive = F;
    protected boolean mPreheat = F, oPreheat = F;
    protected boolean mCooldown = F, oCooldown = F;
    protected boolean mCounterClockwise = F, oCounterClockwise = F;
    
    protected TagData mEnergyTypeEmitted = TD.Energy.RU;
    
    // NBT读写
    public void init(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_ENERGY)) mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
        if (aNBT.hasKey(NBT_REVERSED)) mCounterClockwise = aNBT.getBoolean(NBT_REVERSED);

        if (aNBT.hasKey(NBT_ENERGY_EMITTED)) mEnergyTypeEmitted = TagData.createTagData(aNBT.getString(NBT_ENERGY_EMITTED));
    }
    public void postInitNBT(NBTTagCompound aNBT) {}
    public final void postInitRate(NBTTagCompound aNBT) {setOutRate(aNBT); setInRate(aNBT);}
    protected void setOutRate(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_PREHEAT_ENERGY)) mPEnergy = aNBT.getLong(NBT_PREHEAT_ENERGY);
        mEnergy = Math.min(mEnergy, mPEnergy+getEnergySizeOutputMax(mEnergyTypeEmitted, SIDE_ANY)); // 避免预热能量阈值降低导致输出过高
        if (aNBT.hasKey(NBT_EFFICIENCY)) mEfficiency = (short) UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY));
        if (aNBT.hasKey(NBT_OUTPUT)) mRate = aNBT.getLong(NBT_OUTPUT);
        if (aNBT.hasKey(NBT_PREHEAT_COST)) mPCost = aNBT.getLong(NBT_PREHEAT_COST);
        if (aNBT.hasKey(NBT_COOLDOWN_RATE)) mCRate = aNBT.getLong(NBT_COOLDOWN_RATE);
    }
    protected final void setInRate(NBTTagCompound aNBT) {setInRate2();}
    protected void setInRate2() {
        mInRate  = UT.Code.units(mRate, mEfficiency, 10000, T);
        mInPCost = UT.Code.units(mPCost, mEfficiency, 10000, T);
    }
    public void postInitTank() {}
    
    public void writeToNBT(NBTTagCompound aNBT) {
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
        UT.NBT.setBoolean(aNBT, NBT_REVERSED, mCounterClockwise);
        
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mActive); // for OmniOcular usage
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE_ENERGY, mEmitsEnergy); // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_OUTPUT_NOW, mOutput); // for OmniOcular usage
        UT.NBT.setBoolean(aNBT, NBT_PREHEAT, mPreheat); // for OmniOcular usage
        UT.NBT.setBoolean(aNBT, NBT_COOLDOWN_CH, mCooldown); // for OmniOcular usage
        
        UT.NBT.setNumber(aNBT, NBT_PREHEAT_ENERGY+".oo", mPEnergy); // for OmniOcular usage
    }
    
    // Motor 基本方法
    @Override public boolean onTickStopCheck() {
        if (mStopped) {
            // 主动关机
            stop();
            return T;
        }
        return F;
    }
    @Override public boolean onTickCheckOverload() {return F;} // 默认禁用超载
    @Override public void onTickDoOverload() {
        mActive = F;
        mPreheat = F;
        mCooldown = F;
        mOutput = 0;
    }
    @Override public final boolean onTickCheckPreheat() {return onTickCheckPreheat2() && mEnergy < mPEnergy && mEnergy >= mPCost;}
    protected boolean onTickCheckPreheat2() {return F;} // 默认禁用预热
    @Override public void onTickDoPreheat() {
        mActive = F;
        mPreheat = T;
        mCooldown = F;
        mOutput = 0;
        mEnergy -= mPCost;
    }
    @Override public final boolean onTickCheckCooldown() {return onTickCheckCooldown2() && mEnergy >= mCRate;}
    protected boolean onTickCheckCooldown2() {return F;} // 默认禁用冷却
    @Override public void onTickDoCooldown() {
        mActive = F;
        mPreheat = F;
        mCooldown = T;
        mOutput = 0;
        mEnergy -= mCRate;
    }
    @Override public boolean onTickCheckActive() {return mEnergy >= mPEnergy + mCore.mTE.getEnergySizeOutputMin(mEnergyTypeEmitted, SIDE_ANY);}
    @Override public void onTickDoActive() {
        mActive = T;
        mPreheat = F;
        mCooldown = F;
        mOutput = getActiveOutput();
        mEnergy -= mOutput;
    }
    protected abstract long getActiveOutput(); // 固定输出或是根据 convert 获取输出
    public void onTickDoEmit() {
        mEmitsEnergy = F;
        if (mCounterClockwise) {
            mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, -mOutput, 1L, mCore.getEnergyEmitter()) > 0; // 使用基类中的接口可以在后续继承中重写这个方法
        } else {
            mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, mOutput, 1L, mCore.getEnergyEmitter()) > 0;
        }
    }
    
    @Override public void onTickDoElse() {
        mActive = F;
        mPreheat = F;
        mCooldown = F;
        mOutput = 0;
    }
    public void stop() {
        mActive = F;
        mPreheat = F;
        mCooldown = F;
        mOutput = 0;
        mEnergy = 0;
    }
    @Override public void onTickExplodeCheck(long aTimer) {
        if (ConfigForge.DATA_MACHINES.motorExplodeCheck && (aTimer % 600 == 5) && (mActive || mPreheat)) mCore.mTE.doDefaultStructuralChecks();
    }
    
    protected abstract long getRealEfficiency();
    
    // tanks
    public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return null;}
    public IFluidTank getFluidTankDrainable(byte aSide, FluidStack aFluidToDrain) {return null;}
    public IFluidTank[] getFluidTanks(byte aSide) {return ZL_FT;}
    public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {return 0;}
    public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {return null;}
    
    public boolean canFillExtra(FluidStack aFluid) {return F;}
    
    // inventory
    public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return ZL_IS;}
    public boolean canDrop(int aInventorySlot) {return F;}
    
    // energy interfaces
    public boolean allowCovers(byte aSide) {return T;}
    public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return aEmitting && aEnergyType == mEnergyTypeEmitted;}
    public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || (!mStopped )) && (SIDES_INVALID[aSide] || mCore.isInput(aSide));}
    public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {return SIDES_INVALID[aSide] || mCore.isOutput(aSide);}
    public long getEnergyOffered(TagData aEnergyType, byte aSide, long aSize) {return mOutput;}
    public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mRate;}
    public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mRate/2;}
    public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mRate*2;}
    public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mInRate;}
    public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return mInRate/2;}
    public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mInRate*2;}
    public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeEmitted.AS_LIST;}
    
    public boolean getStateRunningPossible() {return T;}
    public boolean getStateRunningPassively() {return mPreheat || mActive;}
    public boolean getStateRunningActively() {return mEmitsEnergy;}
    public boolean setAdjacentOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    public boolean getStateOnOff() {return !mStopped;}
}
