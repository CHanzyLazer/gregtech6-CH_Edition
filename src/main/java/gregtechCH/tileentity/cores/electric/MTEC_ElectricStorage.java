package gregtechCH.tileentity.cores.electric;

import gregapi.code.TagData;
import gregapi.data.TD;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.util.UT;
import gregtechCH.util.UT_CH;
import net.minecraft.nbt.NBTTagCompound;

import static gregapi.data.CS.*;

/**
 * 需要将存储电量放入 NBT 的实体的 core
 * 可以处理输入输出，限制电流和电压，严格限制容量模式或者不严格模式
 * 暂时只负责存储电能的逻辑
 */
public class MTEC_ElectricStorage {
    protected final TileEntityBase01Root mTE; // reference of te
    
    public MTEC_ElectricStorage(TileEntityBase01Root aTE) {UT_CH.Debug.assertWhenDebug(aTE instanceof IMTEC_HasElectricStorage); mTE = aTE;}
    protected IMTEC_HasElectricStorage te() {return (IMTEC_HasElectricStorage)mTE;}
    
    /* main code */
    // 能量种类，目前只负责电力
    public final TagData mEnergyType = TD.Energy.EU;
    // 容量相关
    protected long mCapacity = 102400; protected boolean mStrict = F;
    protected long mEnergy = 0;
    public final long getEnergy() {return mEnergy;}
    public final long getCapacity() {return mCapacity;}
    public final boolean full() {return mEnergy >= mCapacity;}
    public final boolean halfFull() {return mEnergy*2 >= mCapacity;}
    public final MTEC_ElectricStorage setStrict(boolean aStrict) {mStrict = aStrict; return this;}
    public final MTEC_ElectricStorage setCapacity(long aCapacity) {mCapacity = aCapacity; return this;}
    // 输入限制
    protected long mInputSizeRec = 1024, mInputAmountMax = 4; // 其余参数根据这两个来导出，如有特殊情况重写方法即可
    public final long getInputSizeRec() {return mInputSizeRec;}
    public long getInputSizeMin() {return mInputSizeRec/2;}
    public long getInputSizeMax() {return mInputSizeRec*2;}
    public long getInputAmountMin() {return 1;}
    public final long getInputAmountMax() {return mInputAmountMax;}
    public final MTEC_ElectricStorage setInputAmountMax(long aInputAmountMax) {mInputAmountMax = aInputAmountMax; return this;}
    public final MTEC_ElectricStorage setInputSizeRec(long aInputSizeRec) {mInputSizeRec = aInputSizeRec; return this;}
    // 输出限制
    protected long mOutputSize = 1024, mOutputAmountMax = 4, mOutputAmountLimited = 0;
    public final long getOutputSize() {return mOutputSize;}
    public final long getOutputAmountMax() {return mOutputAmountLimited>0 ? Math.min(mOutputAmountMax, mOutputAmountLimited) : mOutputAmountMax;}
    public final MTEC_ElectricStorage setOutputAmountLimited(long aOutputAmountLimited) {mOutputAmountLimited = aOutputAmountLimited; return this;}
    public final MTEC_ElectricStorage setOutputAmountMax(long aOutputAmountMax) {mOutputAmountMax = aOutputAmountMax; return this;}
    public final MTEC_ElectricStorage setOutputSize(long aOutputSize) {mOutputSize = aOutputSize; return this;}
    
    // NBT 存储
    public void readFromNBT(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_CAPACITY)) mCapacity = aNBT.getLong(NBT_CAPACITY);
        if (aNBT.hasKey(NBT_CAPACITY+".strict")) mStrict = aNBT.getBoolean(NBT_CAPACITY+".strict");
        if (aNBT.hasKey(NBT_ENERGY)) mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_INPUT)) mInputSizeRec = aNBT.getLong(NBT_INPUT);
        if (aNBT.hasKey(NBT_MULTIPLIER+".input")) mInputAmountMax = aNBT.getLong(NBT_MULTIPLIER+".input");
        if (aNBT.hasKey(NBT_OUTPUT)) mOutputSize = aNBT.getLong(NBT_OUTPUT);
        if (aNBT.hasKey(NBT_MULTIPLIER)) mOutputAmountMax = aNBT.getLong(NBT_MULTIPLIER);
    }
    public void writeToNBT(NBTTagCompound aNBT) {writeToNBT(aNBT, F);} // 默认不需要全部存储
    public void writeToNBT(NBTTagCompound aNBT, boolean aSaveAll) {
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        if (aSaveAll) {
            UT.NBT.setNumber(aNBT, NBT_CAPACITY, mCapacity);
            UT.NBT.setBoolean(aNBT, NBT_CAPACITY+".strict", mStrict);
            UT.NBT.setNumber(aNBT, NBT_INPUT, mInputSizeRec);
            UT.NBT.setNumber(aNBT, NBT_MULTIPLIER+".input", mInputAmountMax);
            UT.NBT.setNumber(aNBT, NBT_OUTPUT, mOutputSize);
            UT.NBT.setNumber(aNBT, NBT_MULTIPLIER, mOutputAmountMax);
        }
    }
    public void postInit(NBTTagCompound aNBT) {
        if (mStrict && full()) mEnergy = mCapacity;
    }
    
    // 输入能量，返回成功输入的电流数
    public long injectEnergy(long aSize, long aAmount) {
        if (aAmount < getInputAmountMin()) return 0;
        aAmount = Math.min(aAmount, getInputAmountMax());
        aSize = Math.abs(aSize);
        if (aSize < getInputSizeMin()) return 0; // 注入失败，不主动耗费能量
        if (aSize > getInputSizeMax()) {mTE.overcharge(aSize, mEnergyType); return aAmount;}
        if (full()) return 0; // 满了则注入失败
        mEnergy += aAmount*aSize;
        long tOveredEnergy = mEnergy - mCapacity;
        if (tOveredEnergy <= 0) return aAmount;
        // 这个注入导致满了，则需要减少注入的电流
        long tOveredAmount = mStrict ? UT.Code.divup(tOveredEnergy, aSize) : tOveredEnergy / aSize;
        mEnergy -= tOveredAmount*aSize;
        return aAmount-tOveredAmount;
    }
    // 获取能够输出的电流数
    public long getEmitAmount() {
        long tAmount = getOutputAmountMax();
        if (tAmount <= 0) return 0;
        if (mEnergy >= tAmount*getOutputSize()) return tAmount;
        // 能量不够输出，则需要减少输出
        return mEnergy/getOutputSize();
    }
    // 输出能量，需要保证输出后能量一定大于等于零
    public void emitEnergy(long aAmount) {
        if (aAmount <= 0) return;
        mEnergy -= aAmount*getOutputSize();
        UT_CH.Debug.assertWhenDebug(mEnergy >= 0);
    }
    // 损失能量（用于自动损耗）
    public void costEnergy(long aEnergy) {
        mEnergy -= aEnergy;
        if (mEnergy < 0) mEnergy = 0;
    }
    
    public boolean active() {return mEnergy >= getOutputSize();}
}
