package gregtechCH.tileentity.cores.boilers;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.network.INetworkHandler;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.CS.F;
import static gregtechCH.data.CS_CH.*;

public class MTEC_BoilerTank extends MTEC_BoilerTank_Greg {
    /* 将所有是数据使用这个类封装，保证在相互包含时只有一份数据 */
    public MTEC_BoilerTank(TileEntityBase01Root aTE) {super(aTE);}
    
    /* main code */
    protected long mEnergyEffSU = 0, mInput = 64;
    protected long mOutputNow = 0;
    protected short mEfficiencyCH = 10000;
    protected FluidTankGT mInBoilerWater = new FluidTankGT(); // 增加一个锅炉内部的蒸馏水储罐，用于存储内部蒸汽冷却后的蒸馏水
    
    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        if (aNBT.hasKey(NBT_ENERGY_EFF)) mEnergyEffSU = aNBT.getLong(NBT_ENERGY_EFF);
        if (aNBT.hasKey(NBT_OUTPUT_SU)) mInput = aNBT.getLong(NBT_OUTPUT_SU) / STEAM_PER_EU; //保留兼容
        
        if (aNBT.hasKey(NBT_INPUT)) mInput = aNBT.getLong(NBT_INPUT);
        if (aNBT.hasKey(NBT_EFFICIENCY_CH)) mEfficiencyCH = (short) UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_CH));
        mOutput = UT.Code.units(mInput, 10000, mEfficiencyCH, F) * STEAM_PER_EU;
        
        mInBoilerWater.setCapacity(UT.Code.divup(mTanks[1].getCapacity(), STEAM_PER_WATER) + 1000);
        mInBoilerWater.readFromNBT(aNBT, NBT_TANK+"."+mTanks.length);
        // 指定 tank 的存储类型，快速释放和添加
        mTanks[1].fixFluid(FL.Steam.fluid());
        mInBoilerWater.fixFluid(FL.DistW.fluid()).setVoidExcess();
    }
    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        UT.NBT.setNumber(aNBT, NBT_ENERGY_EFF, mEnergyEffSU);
        
        mTanks[1].unfixFluid(); // 由于总是会在读取时 fix 到指定的流体，因此存储时不需要 fix
        mInBoilerWater.unfixFluid().writeToNBT(aNBT, NBT_TANK+"."+mTanks.length);
        super.writeToNBT(aNBT);
        
        if (mOutputNow != 0) aNBT.setLong(NBT_OUTPUT_NOW, mOutputNow); // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_CAPACITY_HU, mCapacity); // for OmniOcular usage 和读取的名称不一致是为了避免被意外修改
        for (int i = 0; i < mTanks.length; ++i) UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+i, mTanks[i].capacity()); // for OmniOcular usage
    }
    @Override
    public void writeItemNBT(NBTTagCompound aNBT) {
        if (mEfficiency != 10000) aNBT.setShort(NBT_EFFICIENCY, mEfficiency); // 用于在拆下后保留钙化程度
    }
    public long realOutput() {return mEfficiency == 10000 ? mOutput : UT.Code.units(mOutput, 10000, mEfficiency, F);}
    
    @Override
    public void toolTipsRecipe(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X)        + " 1 L " + FL.name(FluidRegistry.WATER, T) + " " + LH.get(LH.CONVERTS_TO_Y) + " " + STEAM_PER_WATER + " L " + FL.name(FL.Steam.make(0), T) + " " + LH.get(LH.CONVERTS_USING_Z) + " " + UT.Code.units(EU_PER_WATER, mEfficiencyCH, 10000, F) + " " + mEnergyTypeAccepted.getLocalisedNameShort());
    }
    static {
        LH_CH.add("gt.tooltip.boiler.calcification", "Calcification: ");
    }
    @Override
    public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(mEfficiencyCH));
        if (mEfficiency < 10000) aList.add(LH.Chat.YELLOW + LH_CH.get("gt.tooltip.boiler.calcification") + LH.percent(10000 - mEfficiency) + "%");
        aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_INPUT)           + ": " + LH.Chat.WHITE + mInput       + " - " + (mInput*2)  + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t ("+LH.get(LH.FACE_ANY)+")");
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT)          + ": " + LH.Chat.WHITE + realOutput() + " - " + (mOutput*2) + " " + TD.Energy.STEAM.getLocalisedChatNameLong() + LH.Chat.WHITE + "/t ("+LH.get(LH.FACE_TOP)+")");
    }
    @Override
    public void toolTipsUseful(List<String> aList) {
        aList.add(LH.Chat.GREEN    + LH_CH.get(LH_CH.TOOLTIP_PREHEAT));
    }
    
    // 改写部分原版锅炉运行逻辑
    @Override public void onTickConvert(long aTimer) {
        // Convert HU to effective energy
        if (mEnergy < mInput && mTanks[1].isHalf()) mEnergy = 0; // 输入能量不足不会工作
        // 改用倍率判断并且暂存能量单位为 SU，防止卡最小输入不稳
        if (mEnergyEffSU < STEAM_PER_WATER * 2) {
            int tMul = (int) (mEnergy / mInput);
            if (tMul > 0) {
                mEnergy -= mInput * tMul;
                mEnergyEffSU += UT.Code.units(mOutput * tMul, 10000, mEfficiency, F);
            }
        }
        // Convert Water to Steam
        long tConversionsEff = mEnergyEffSU / STEAM_PER_WATER;
        if (tConversionsEff > 0) {
            // 优先转换 mInBoilerWater 中的蒸馏水
            long tDrained = mInBoilerWater.remove(tConversionsEff);
            // 转换 tank 中的水，需要考虑钙化的问题
            boolean tCalcification = mTE.rng(10) == 0 && mEfficiency > 5000 && mTanks[0].has() && !FL.distw(mTanks[0]);
            long tDrainedTank = mTanks[0].remove(tConversionsEff-tDrained);
            tDrained += tDrainedTank;
            if (tCalcification && tDrainedTank > 0) {
                mEfficiency -= tDrainedTank;
                if (mEfficiency < 5000) mEfficiency = 5000;
            }
            mTanks[1].add(tDrained * STEAM_PER_WATER); // 由于固定了流体种类，因此可以直接添加
            mEnergyEffSU -= tDrained * STEAM_PER_WATER;
        }
    }
    
    protected void resetCoolDownTimer() {mCoolDownResetTimer = STEAM_PER_WATER*2;} // 将 mCoolDownResetTimer 设为 STEAM_PER_WATER*2
    @Override public void onTickCoolDown(long aTimer) {
        // 冷却阶段将蒸汽再次转换为蒸馏水暂存到 mInBoilerWater 中
        // 为了避免出现余数，直接将 mCoolDownResetTimer 设为 STEAM_PER_WATER*2，而冷却倍率则为 STEAM_PER_WATER
        --mCoolDownResetTimer;
        if (mCoolDownResetTimer <= 0) {
            resetCoolDownTimer(); // 逻辑改变，不会在 mEnergy 不为零时高速冷却
            mEnergy -= mInput * EU_PER_WATER * 2;
            if (mEnergy <= 0) mEnergy = 0;
            mEnergyEffSU = 0;
            
            // 只有能量为零时内部蒸汽才会冷却回蒸馏水
            if (mEnergy == 0) {
                long tCoolDownSteam = mOutput * STEAM_PER_WATER;
                long tCoolDownWater = mOutput;
                if (tCoolDownSteam > mTanks[1].amount()) {
                    tCoolDownSteam = mTanks[1].amount();
                    tCoolDownWater = tCoolDownSteam / STEAM_PER_WATER;
                }
                mInBoilerWater.add(tCoolDownWater); // 由于固定了流体种类，因此可以直接添加
                mTanks[1].remove(tCoolDownSteam);
            }
        }
    }
    @Override protected void doEmitSteam(long aAmount) {
        long tOutput = realOutput();
        if (aAmount >= tOutput) {
            mOutputNow = Math.min(aAmount * 4 > mTanks[1].capacity() ?
                            tOutput * 2 :
                            tOutput + UT.Code.units(tOutput, mTanks[1].capacity(), (aAmount - tOutput) * 4, F),
                    aAmount);
            doEmitSteam2(mOutputNow);
        } else {
            mTanks[1].remove(aAmount); // 只是为了让数值好看
            mOutputNow = 0;
        }
    }
    
    // 重写修改搋子的操作，运行时清除会爆炸
    public long onPlunger(Entity aPlayer, List<String> aChatReturn) {
        if (mTanks[0].isEmpty()) return 0;
        long tOut = 0;
        if (mInBoilerWater.has()) tOut = GarbageGT.trash(mInBoilerWater);
        else if (mTanks[0].has()) tOut = GarbageGT.trash(mTanks[0]);
        if (tOut > 0) {
            if (mBarometer > 15) {
                mTE.explode(F);
            } else {
                if (mEnergy+mTanks[1].amount()/STEAM_PER_EU > 2000) UT.Entities.applyHeatDamage(aPlayer, (mEnergy+mTanks[1].amount()/2.0F) / 2000.0F);
            }
            mTanks[1].setEmpty();
            mEnergy = 0;
            return tOut;
        }
        return 0;
    }
    
    @Override
    public void onBreakBlock() {
        if (mTE.isServerSide()) {
            GarbageGT.trash(mTanks[0]);
        }
    }
    @Override public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {return mInput;}
    @Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mInput;}
    
    // 重写这个方法来扩展客户端数据，必须要把 efficiency 发送到客户端才存入物品
    public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {
        if (mEfficiency == 10000) return;
        rList.add(5, UT.Code.toByteS(mEfficiency, 0));
        rList.add(6, UT.Code.toByteS(mEfficiency, 1));
    }
    public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
        if (aData.length > 7) mEfficiency = UT.Code.combine(aData[5], aData[6]); // 注意到最后永远都有一个 paint data
        else mEfficiency = 10000;
        return T;
    }
}
