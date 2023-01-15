package gregtechCH.tileentity.multiblocks;

import gregapi.code.TagData;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.electric.IMTEC_HasElectricStorage;
import gregtechCH.tileentity.cores.electric.MTEC_ElectricStorage;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author YueSha, CHanzy
 * stuff from GT6U
 */
public abstract class TileEntityBase11MultiBlockEnergyStorage extends TileEntityBase10MultiBlockBase implements IMTEC_HasElectricStorage {
    
    private final MTEC_ElectricStorage mCore = new MTEC_ElectricStorage(this).setStrict(T);
    @Override public MTEC_ElectricStorage core() {return mCore;}
    
    // TODO 这些用于监控和状态控制的需要使用另一个 core
    protected boolean mEmitsEnergy = F, mStopped = F, mActive = F;
    private byte mActiveState = 0, mMode = 0;
    public void setMode(byte aMode) {
        mMode = aMode;
        mCore.setOutputAmountLimited(aMode); // 暂设置限制的输出电流即为 Mode 数
    }
    
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        mCore.readFromNBT(aNBT);
        if (aNBT.hasKey(NBT_ACTIVE_ENERGY)) mEmitsEnergy = aNBT.getBoolean(NBT_ACTIVE_ENERGY);
        if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
        if (aNBT.hasKey(NBT_ACTIVE)) mActive = aNBT.getBoolean(NBT_ACTIVE);
        if (aNBT.hasKey(NBT_MODE)) setMode(aNBT.getByte(NBT_MODE));
        readFromNBT3(aNBT);
        mCore.postInit(aNBT);
    }
    public void readFromNBT3(NBTTagCompound aNBT) {/**/}
    
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        mCore.writeToNBT(aNBT);
        if (mMode != 0) aNBT.setByte(NBT_MODE, mMode);
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mActive);
        UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE_ENERGY, mEmitsEnergy);
    }
    
    @Override
    public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (rReturn > 0) return rReturn;
        if (isClientSide()) return 0;
        
        if (aTool.equals(TOOL_screwdriver) && aSneaking) {
            if (aChatReturn != null) onScrewdriverSneaking(aChatReturn);
            return 1000;
        }
        
        if (aTool.equals(TOOL_screwdriver)) {
            if (aChatReturn != null) onScrewdriver(aChatReturn);
            return 1000;
        }
        return 0;
    }
    
    public void onScrewdriver(List<String> aChatReturn) {aChatReturn.add("Structure is formed already!");}
    public void onScrewdriverSneaking(List<String> aChatReturn) {aChatReturn.add("Structure is formed already!");}
    
    @Override
    public void onTick3(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide && isStructureOkay()) {
            onTickEnergy(aTimer);
        }
    }
    
    public void onTickEnergy(long aTimer) {
        mActive = mCore.active();
        if (mActive) {
            if (!mStopped) {
                long tEmittedAmount;
                byte tEmitSide = getEmittingSide();
                if (SIDES_VALID[tEmitSide]) tEmittedAmount = ITileEntityEnergy.Util.emitEnergyToSide(mCore.mEnergyType, tEmitSide, mCore.getOutputSize(), mCore.emitAmount(), getEmittingTileEntity());
                else tEmittedAmount = ITileEntityEnergy.Util.emitEnergyToNetwork(mCore.mEnergyType, mCore.getOutputSize(), mCore.emitAmount(), (ITileEntityEnergy)getEmittingTileEntity());
                mEmitsEnergy = (tEmittedAmount > 0);
                mCore.emit(tEmittedAmount);
            }
        }
    }
    
    @Override
    public long doInject(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {
        return mCore.inject(aSize, aAmount);
    }
    
    // TODO 还没有做动画，虽然只有主方块的
    @Override public boolean onTickCheck(long aTimer) {
        if (aTimer % 20 != 0) return super.onTickCheck(aTimer);
        byte tActiveState = mActiveState;
        if (mCore.full()) {
            mActiveState = 1;
        } else if (mCore.active()) {
            mActiveState = 2;
        } else {
            mActiveState = 0;
        }
        return tActiveState != mActiveState || super.onTickCheck(aTimer);
    }
    @Override public void setVisualData(byte aData) {mActiveState = aData;}
    @Override public byte getVisualData() {return mActiveState;}
    
    public abstract TileEntity getEmittingTileEntity();
    public abstract byte getEmittingSide();
    
    @Override public boolean isEnergyType                   (TagData aEnergyType, byte aSide, boolean aEmitting) {return aEnergyType == mCore.mEnergyType;}
    @Override public boolean isEnergyCapacitorType          (TagData aEnergyType, byte aSide) {return aEnergyType == mCore.mEnergyType;}
    @Override public boolean isEnergyAcceptingFrom          (TagData aEnergyType, byte aSide, boolean aTheoretical) {return                                 (SIDES_INVALID[aSide] || isInput (aSide)) && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
    @Override public boolean isEnergyEmittingTo             (TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || !mStopped) &&  (SIDES_INVALID[aSide] || isOutput(aSide)) && super.isEnergyEmittingTo   (aEnergyType, aSide, aTheoretical);}
    @Override public long getEnergySizeOutputMin            (TagData aEnergyType, byte aSide) {return mCore.getOutputSize();}
    @Override public long getEnergySizeOutputRecommended    (TagData aEnergyType, byte aSide) {return mCore.getOutputSize();}
    @Override public long getEnergySizeOutputMax            (TagData aEnergyType, byte aSide) {return mCore.getOutputSize();}
    @Override public long getEnergySizeInputRecommended     (TagData aEnergyType, byte aSide) {return mCore.getInputSizeRec();}
    @Override public long getEnergyStored                   (TagData aEnergyType, byte aSide) {return mCore.energy();}
    @Override public long getEnergyCapacity                 (TagData aEnergyType, byte aSide) {return mCore.capacity();}
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return mCore.mEnergyType.AS_LIST;}
    @Override public Collection<TagData> getEnergyCapacitorTypes(byte aSide) {return mCore.mEnergyType.AS_LIST;}
    
    @Override public boolean getStateRunningPossible() {return mCore.active();}
    @Override public boolean getStateRunningPassively() {return mActive;}
    @Override public boolean getStateRunningActively() {return mEmitsEnergy;}
    @Override public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    @Override public boolean getStateOnOff() {return !mStopped;}
    
    @Override public byte setStateMode(byte aMode) {setMode(aMode); return mMode;}
    @Override public byte getStateMode() {return mMode;}
    
    @Override public byte getDefaultSide() {return SIDE_BOTTOM;}
    
    public boolean isInput (byte aSide) {return aSide == mFacing;}
    public boolean isOutput(byte aSide) {return aSide != mFacing;}
}
