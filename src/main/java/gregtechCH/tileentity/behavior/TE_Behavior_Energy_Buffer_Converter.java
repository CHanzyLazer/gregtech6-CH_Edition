package gregtechCH.tileentity.behavior;

import gregapi.data.TD;
import gregapi.tileentity.behavior.TE_Behavior;
import gregapi.tileentity.behavior.TE_Behavior_Energy_Capacitor;
import gregapi.tileentity.behavior.TE_Behavior_Energy_Stats;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.*;

/**
 * @author YueSha
 * stuff from GT6U
 * 和 core 的作用类似，用于实现类似多继承的功能
 * 不过 gt 原版以及 gt6u 使用 TE_Behavior 来进行命名，这里暂时保留这种写法
 * TODO 注意存在各种 bug
 */
public class TE_Behavior_Energy_Buffer_Converter extends TE_Behavior {
    public TE_Behavior_Energy_Stats mEnergyIN, mEnergyOUT;
    public TE_Behavior_Energy_Capacitor mStorage;
    public boolean mWasteEnergy = F, mLimitConsumption = F, mOverloaded = F, mEmitsEnergy = F, mCanEmitEnergy = F, mSizeIrrelevant = F, mFast = F;
    public long mMultiplier = 1;
    public byte mFactor = 1;
    
    public TE_Behavior_Energy_Buffer_Converter(TileEntity aTileEntity, NBTTagCompound aNBT, TE_Behavior_Energy_Capacitor aStorage, TE_Behavior_Energy_Stats aEnergyIN, TE_Behavior_Energy_Stats aEnergyOUT, long aMultiplier, boolean aWasteEnergy, boolean aNegativeOutput, boolean aLimitConsumption) {
        super(aTileEntity, aNBT);
        mStorage = aStorage;
        mEnergyIN = aEnergyIN;
        mEnergyOUT = aEnergyOUT;
        mMultiplier = aMultiplier;
        mWasteEnergy = aWasteEnergy;
        mSizeIrrelevant = TD.Energy.ALL_SIZE_IRRELEVANT.contains(mEnergyOUT.mType);
        mLimitConsumption = aLimitConsumption;
        if (aNegativeOutput) mFactor = -1;
    }
    
    @Override
    public void load(NBTTagCompound aNBT) {
        mEmitsEnergy = aNBT.getBoolean(NBT_ACTIVE_ENERGY);
        mCanEmitEnergy = aNBT.getBoolean(NBT_CAN_ENERGY);
//		if (aNBT.hasKey(NBT_MULTIPLIER)) mMultiplier = aNBT.getLong(NBT_MULTIPLIER);
//		if (aNBT.hasKey(NBT_OUTPUT_MAX)) mEnergyOUT.mMax = aNBT.getLong(NBT_OUTPUT_MAX);
//		if (aNBT.hasKey(NBT_OUTPUT)) mEnergyOUT.mRec = aNBT.getLong(NBT_OUTPUT);
//		if (aNBT.hasKey(NBT_OUTPUT_MIN)) mEnergyOUT.mMin = aNBT.getLong(NBT_OUTPUT_MIN);
//		if (aNBT.hasKey(NBT_INPUT_MAX)) mEnergyIN.mMax = aNBT.getLong(NBT_INPUT_MAX);
//		if (aNBT.hasKey(NBT_INPUT)) mEnergyIN.mRec = aNBT.getLong(NBT_INPUT);
//		if (aNBT.hasKey(NBT_INPUT_MIN)) mEnergyIN.mMin = aNBT.getLong(NBT_INPUT_MIN);
    }
    
    @Override
    public void save(NBTTagCompound aNBT) {
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE_ENERGY, mEmitsEnergy);
        UT.NBT.setBoolean(aNBT, NBT_CAN_ENERGY, mCanEmitEnergy);
        UT.NBT.setNumber(aNBT, NBT_MULTIPLIER, mMultiplier);
        UT.NBT.setNumber(aNBT, NBT_OUTPUT_MAX, mEnergyOUT.mMax);
        UT.NBT.setNumber(aNBT, NBT_OUTPUT, mEnergyOUT.mRec);
        UT.NBT.setNumber(aNBT, NBT_OUTPUT_MIN, mEnergyOUT.mMin);
        UT.NBT.setNumber(aNBT, NBT_INPUT_MAX, mEnergyIN.mMax);
        UT.NBT.setNumber(aNBT, NBT_INPUT, mEnergyIN.mRec);
        UT.NBT.setNumber(aNBT, NBT_INPUT_MIN, mEnergyIN.mMin);
    }
    
    public boolean onTickEnergy(long aTimer, TileEntity aEmitter, byte aSide, byte aMode, boolean aNegative) {
        long tOutput = mEnergyOUT.mRec;
        long tEmittedPackets;
        mCanEmitEnergy = mStorage.mEnergy >= tOutput;
        mEmitsEnergy = F;
        if (mCanEmitEnergy) {
            
            if (SIDES_VALID[aSide]) {
                tEmittedPackets = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyOUT.mType, aNegative ? -tOutput*mFactor : tOutput*mFactor, mMultiplier, (ITileEntityEnergy)aEmitter);
            } else {
                tEmittedPackets = ITileEntityEnergy.Util.emitEnergyToSide   (mEnergyOUT.mType, aSide, aNegative ? -tOutput*mFactor : tOutput*mFactor, mMultiplier, aEmitter);
            }
            
            if (tEmittedPackets > 0) {
                mStorage.mEnergy = Math.max(0, mStorage.mEnergy - UT.Code.units(tEmittedPackets * tOutput, mEnergyOUT.mRec * mMultiplier, mEnergyIN.mRec, T));
                mEmitsEnergy = T;
            }
            
        }
        
        return mCanEmitEnergy;
    }
}
