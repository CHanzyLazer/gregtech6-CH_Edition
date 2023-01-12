package gregtechCH.tileentity.cores.motors;

import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.MTEC_HasTanks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

public class MTEC_MotorMainSteam extends MTEC_MotorMainBase {
    // the instance of MTEC_HasTanks
    protected final MTEC_HasTanks mCTanks;
    protected MTEC_MotorMainSteam(MTEC_Motor aCore) {this(aCore, ZL_FT);}
    protected MTEC_MotorMainSteam(MTEC_Motor aCore, FluidTankGT[] aOutputTanks) {super(aCore); mCTanks = new MTEC_HasTanks(aCore, mTankSteam.AS_ARRAY, aOutputTanks);}

    /* main code */
    protected FluidTankGT mTankSteam = new FluidTankGT();
    protected long pSteam = 0, mInputSU = 0, mSteamCounter = 0;
    protected int STEAM_PER_WATER_SELF = 200;
    protected short mEfficiencyWater = 8000;
    protected short mEfficiencyOverclock = 5000;

    protected static final byte COOLDOWN_NUM = 16;
    protected byte mCooldownCounter = 0; // 注意默认是停止工作的

    protected TagData mEnergyTypeAccepted = TD.Energy.STEAM;

    protected boolean mOverload = F;

    // NBT读写
    @Override
    public void init(NBTTagCompound aNBT) {
        super.init(aNBT);
        if (aNBT.hasKey(NBT_COOLDOWN_COUNTER)) mCooldownCounter = aNBT.getByte(NBT_COOLDOWN_COUNTER);
        if (aNBT.hasKey(NBT_ENERGY_SU_PRE)) pSteam = aNBT.getLong(NBT_ENERGY_SU_PRE);

        if (aNBT.hasKey(NBT_ENERGY_SU)) mSteamCounter = aNBT.getLong(NBT_ENERGY_SU);
        if (aNBT.hasKey(NBT_EFFICIENCY_WATER)) mEfficiencyWater = (short) UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_WATER));
        if (aNBT.hasKey(NBT_EFFICIENCY_OC)) mEfficiencyOverclock = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_OC));

        if (aNBT.hasKey(NBT_ENERGY_ACCEPTED)) mEnergyTypeAccepted = TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED));

        mCTanks.readFromNBT(aNBT);
    }
    @Override
    public void postInitNBT(NBTTagCompound aNBT) {
        super.postInitNBT(aNBT);
        STEAM_PER_WATER_SELF = mEfficiencyWater < 100 ? -1 : (int)UT.Code.units(STEAM_PER_WATER, mEfficiencyWater, 10000, T);
    }
    @Override
    protected void setInRate2() {
        mInRate  = UT.Code.units(mRate, mEfficiency, 10000, T) * STEAM_PER_EU;
        mInPCost = UT.Code.units(mPCost, mEfficiency, 10000, T) * STEAM_PER_EU;
    }
    @Override public void postInitTank() {mTankSteam.setCapacity(mInRate*16);} // core 不进行容量大小设定

    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        UT.NBT.setNumber(aNBT, NBT_COOLDOWN_COUNTER, mCooldownCounter);
        UT.NBT.setNumber(aNBT, NBT_ENERGY_SU_PRE, pSteam);

        UT.NBT.setNumber(aNBT, NBT_ENERGY_SU, mSteamCounter);
        UT.NBT.setNumber(aNBT, NBT_OUTPUT_SU, mInputSU); // 保留兼容，实际意义为输入的蒸汽速率

        mCTanks.writeToNBT(aNBT);
        UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+0, mTankSteam.capacity());
    }

    // 每 tick 转换
    @Override
    public void onTickConvert(long aTimer) {
        long tSteam = mTankSteam.amount();
        mInputSU = tSteam - pSteam;
        if (mInputSU > 0) mCooldownCounter = COOLDOWN_NUM;
        else if (mInputSU == 0) --mCooldownCounter; //可能在冷却，计数
        if (mTankSteam.has()) {
            if (!mTankSteam.isFull()) {
                //没有超载
                if (mEnergy < mPEnergy) {
                    // 预热时积攒蒸汽，减少运算，减少因效率计算造成的损失
                    if (mEnergy < mPEnergy - mInRate * 2) {
                        convert(mInRate * 2, mRate * 2, F);
                    } else {
                        convert_(Math.min(tSteam, mInRate * 2), F);
                    }
                } else {
                    // 运行状态下不积攒蒸汽，回到原本的两种情况
                    if (tSteam >= mInRate * 2) {
                        // 最高输出
                        convert_(mInRate * 2, mRate * 2, T);
                    } else
                    if (tSteam * 2 >= mInRate) {
                        // 一般输出
                        convert_(tSteam, T);
                    } else {
                        // 蒸汽不够完全不工作
                        if (STEAM_PER_WATER_SELF > 0) mSteamCounter += tSteam;
                        mTankSteam.remove(tSteam);
                        if (tSteam >= mInPCost) mEnergy = mPEnergy + mPCost; // 超过一半的不够则不考虑效率，使用这个方式实现不工作
                    }
                }
            } else {
                //超载
                mTankSteam.remove(tSteam/2);
                mOverload = T;
            }

            //输出蒸馏水，和输出能量不相互干扰
            if (mSteamCounter >= STEAM_PER_WATER_SELF && STEAM_PER_WATER_SELF > 0) {
                // 将转换得到的蒸馏水存储
                mCore.convertToTanks(FL.DistW.make(mSteamCounter / STEAM_PER_WATER_SELF));
                mSteamCounter %= STEAM_PER_WATER_SELF;
            }
        }
        // 自动输出内部蒸馏水
        mCore.convertAutoOutput();
        pSteam = mTankSteam.amount();
    }
    private void convert_(long aInRate, boolean aOverclockLoss) {
        convert_(aInRate, UT.Code.units(aInRate, 10000 * STEAM_PER_EU, mEfficiency, F), aOverclockLoss);
    }
    private void convert_(long aInRate, long aOutRate, boolean aOverclockLoss) {
        if (aOverclockLoss) {
            long tAbove = aOutRate - mRate;
            if (tAbove > 0) aOutRate = Math.min(mRate + UT.Code.units(tAbove, 10000, mEfficiencyOverclock, F), mRate*2);
        }
        if (STEAM_PER_WATER_SELF > 0) mSteamCounter += aInRate;
        mTankSteam.remove(aInRate);
        mEnergy += aOutRate;
    }
    protected void convert(long aInRate, long aOutRate, boolean aOverclockLoss) {if (mTankSteam.has(aInRate)) convert_(aInRate, aOutRate, aOverclockLoss);}

    @Override public boolean onTickCheckOverload(long aTimer) {return mOverload;}
    @Override public void onTickDoOverload(long aTimer) {
        super.onTickDoOverload(aTimer);
        mCore.mTE.overcharge(mTankSteam.capacity(), mEnergyTypeEmitted);
        mOverload = F;
    }
    @Override protected boolean onTickCheckPreheat2() {return mCooldownCounter > 0;}
    @Override protected boolean onTickCheckCooldown2() {return mCooldownCounter <= 0;}
    @Override public void onTickDoCooldown(long aTimer) {
        super.onTickDoCooldown(aTimer);
        mCooldownCounter = 0;
        mTankSteam.setEmpty();
        pSteam = 0;
        mInputSU = 0;
    }
    @Override protected long getActiveOutput() {return mEnergy - mPEnergy;}
    @Override public void onTickDoElse(long aTimer) {
        // 必须要冷却才能清空数据
        if (mCooldownCounter <= 0) stop();
        else super.onTickDoElse(aTimer);
    }
    @Override
    public void stop() {
        super.stop();
        mTankSteam.setEmpty();
        pSteam = 0;
        mInputSU = 0;
        mSteamCounter = 0;
        mCooldownCounter = 0;
    }

    // 重复的接口实现消除
    public void toolTipsUseful_overclock(List<String> aList) {aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_GENERATOR) + " (" + LH_CH.getToolTipEfficiencySimple(mEfficiencyOverclock) + ")");}
    @Override public long getRealEfficiency() {return UT.Code.units(10000 * STEAM_PER_EU, mInputSU, mOutput, F);}
    @Override public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return !mCore.mD.mStopped && FL.steam(aFluidToFill) ? mTankSteam : null;}
    @Override public IFluidTank getFluidTankDrainable(byte aSide, FluidStack aFluidToDrain) {return mCTanks.getFluidTankDrainable(aSide, aFluidToDrain);}
    @Override public IFluidTank[] getFluidTanks(byte aSide) {return mCTanks.getFluidTanks(aSide);}
    @Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return (aEmitting?mEnergyTypeEmitted:mEnergyTypeAccepted)==aEnergyType;}
    @Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mCore.mD.mRate+UT.Code.units(mCore.mD.mRate, 10000, mEfficiencyOverclock, F);}
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return new ArrayListNoNulls<>(F, mEnergyTypeAccepted, mEnergyTypeEmitted);}
    @Override public boolean canFillExtra(FluidStack aFluid) {return T;}
}
