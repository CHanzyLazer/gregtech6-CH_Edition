package gregtechCH.tileentity.cores.boilers;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.CS.F;
import static gregtechCH.data.CS_CH.*;

public class MTEC_BoilerTank extends MTEC_BoilerTank_Greg {
    /* 将所有是数据使用这个类封装，保证在相互包含时只有一份数据 */
    public MTEC_BoilerTank(TileEntityBase09FacingSingle aTE) {super(aTE);}

    /* main code */
    protected long mEnergyEff = 0, mInput = 64, pEnergy = 0;
    protected long mOutputNow = 0;
    protected short mEfficiencyCH = 10000;
    
    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        if (aNBT.hasKey(NBT_ENERGY_EFF)) mEnergyEff = aNBT.getLong(NBT_ENERGY_EFF);
        if (aNBT.hasKey(NBT_ENERGY_PRE)) pEnergy = aNBT.getLong(NBT_ENERGY_PRE);
        if (aNBT.hasKey(NBT_OUTPUT_SU)) mInput = aNBT.getLong(NBT_OUTPUT_SU) / STEAM_PER_EU; //保留兼容

        if (aNBT.hasKey(NBT_INPUT)) mInput = aNBT.getLong(NBT_INPUT);
        if (aNBT.hasKey(NBT_EFFICIENCY_CH)) mEfficiencyCH = (short) UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_CH));
        mOutput = UT.Code.units(mInput, 10000, UT.Code.units(mEfficiency, 10000, mEfficiencyCH, F), F) * STEAM_PER_EU;
    }
    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        UT.NBT.setNumber(aNBT, NBT_ENERGY_EFF, mEnergyEff);
        UT.NBT.setNumber(aNBT, NBT_ENERGY_PRE, pEnergy);

        if (mOutputNow != 0) aNBT.setLong(NBT_OUTPUT_NOW, mOutputNow); // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_CAPACITY_HU, mCapacity); // for OmniOcular usage 和读取的名称不一致是为了避免被意外修改
        for (int i = 0; i < mTanks.length; i++) UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+i, mTanks[i].capacity()); // for OmniOcular usage
    }

    @Override
    public void toolTipsRecipe(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X)        + " 1 L " + FL.name(FluidRegistry.WATER, T) + " " + LH.get(LH.CONVERTS_TO_Y) + " " + STEAM_PER_WATER + " L " + FL.name(FL.Steam.make(0), T) + " " + LH.get(LH.CONVERTS_USING_Z) + " " + UT.Code.units(EU_PER_WATER, mEfficiencyCH, 10000, F) + " " + mEnergyTypeAccepted.getLocalisedNameShort());
    }
    @Override
    public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(mEfficiencyCH));
        aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_INPUT)           + ": " + LH.Chat.WHITE + mInput + " - " + (mInput*2)   + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t ("+LH.get(LH.FACE_ANY)+")");
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT)          + ": " + LH.Chat.WHITE + mOutput	+ " - " + (mOutput*2)	+ " " + TD.Energy.STEAM.getLocalisedChatNameLong()         + LH.Chat.WHITE + "/t ("+LH.get(LH.FACE_TOP)+")");
    }
    @Override
    public void toolTipsUseful(List<String> aList) {
        aList.add(LH.Chat.GREEN    + LH_CH.get(LH_CH.TOOLTIP_PREHEAT));
    }

    // 改写部分原版锅炉运行逻辑
    @Override public void onTickConvert() {
        // 有接受到热量就不会冷却
        if (mEnergy > pEnergy) mCoolDownResetTimer = 128;
        pEnergy = mEnergy;
        // Convert HU to effective energy
        if (mEnergy < mInput && mTanks[1].isHalf()) mEnergy = 0; // 输入能量不足不会工作
        // 改用倍率判断，防止卡最小输入不稳
        if (mEnergyEff < EU_PER_WATER * 2) {
            int tMul = (int) (mEnergy / mInput);
            if (tMul > 0) {
                mEnergy -= mInput * tMul;
                mEnergyEff += UT.Code.units(mOutput * tMul, 10000, mEfficiency, F) / STEAM_PER_EU;
            }
        }
        // Convert Water to Steam
        long tConversionsEff = mEnergyEff / EU_PER_WATER;
        if (tConversionsEff > 0) {
            mTanks[0].remove(tConversionsEff);
            if (mTE.rng(10) == 0 && mEfficiency > 5000 && mTanks[0].has() && !FL.distw(mTanks[0])) {
                mEfficiency -= tConversionsEff;
                if (mEfficiency < 5000) mEfficiency = 5000;
            }
            mTanks[1].setFluid(FL.Steam.make(mTanks[1].amount() + tConversionsEff * STEAM_PER_WATER));
            mEnergyEff -= tConversionsEff * EU_PER_WATER;
        }
    }
    @Override public void onTickCoolDown() {
        // Remove Steam and Heat during the process of cooling down.
        if (mCoolDownResetTimer-- <= 0) {
            mCoolDownResetTimer = 0;
            mEnergy -= mInput * 64;
            pEnergy = mEnergy;
            mEnergyEff = 0;
            GarbageGT.trash(mTanks[1], mOutput * 64);
            if (mEnergy <= 0) {
                mEnergy = 0;
                mCoolDownResetTimer = 128;
            }
        }
    }
    @Override protected void doEmitSteam(long aAmount) {
        if (aAmount >= mOutput) {
            mOutputNow = Math.min(aAmount > mTanks[1].capacity() / 4 ?
                            mOutput * 2 :
                            mOutput + UT.Code.units(mOutput, mTanks[1].capacity() / 4, aAmount - mOutput, F),
                    aAmount);
            doEmitSteam2(mOutputNow);
        } else {
            mTanks[1].remove(aAmount); // 只是为了让数值好看
            mOutputNow = 0;
        }
    }


    @Override
    public void onBreakBlock() {
        if (mTE.isServerSide()) {
            GarbageGT.trash(mTanks[0]);
        }
    }
    @Override public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {return mInput;}
    @Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mInput;}
}
