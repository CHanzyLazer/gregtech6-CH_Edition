package gregtechCH.tileentity.cores.electric;

import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.util.UT;
import gregtechCH.code.Pair;
import gregtechCH.util.UT_CH;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigForge_CH.*;

/**
 * WIP
 * 基本电线的类，存储此电线的电压，电流等数值
 */
public class MTEC_ElectricWireBase {
    protected MTEC_ElectricWiresManager mManager = null; // reference of Manager
    protected final TileEntityBase01Root mTE; // reference of te
    
    public MTEC_ElectricWireBase(TileEntityBase01Root aTE) {UT_CH.Debug.assertWhenDebug(aTE instanceof IMTEC_HasElectricWire); mTE = aTE;}
    protected IMTEC_HasElectricWire te() {return (IMTEC_HasElectricWire)mTE;}
    
    /* main code */
    protected boolean mManagerUpdated = F; // 用于在第一次放置时，连接发生改变或者近邻更改时设置为 F，然后通过 ticking 来进行更新
    public void updateManager() {mManagerUpdated = F;}
    
    private final Map<MTEC_ElectricWiresManager.InputObject, Pair<Long, Long>> mVoltageList = new LinkedHashMap<>(); // linked 用于加速遍历，后一项为小数部分
    private final Map<MTEC_ElectricWiresManager.InputObject, Long> mAmperageList = new LinkedHashMap<>(); // linked 用于加速遍历
    private final Pair<Long, Long> mVoltage = new Pair<>(0L, 0L); // 暂存这个电线的电压值
    private long mAmperage = 0L; // 暂存这个电线的电流值
    // 提供这些接口来方便的修改电压电流值
    protected void clearTemporary() {
        // 清空临时变量值，在修改前统一调用保证永远都是正确值
        mVoltage.first = 0L; mVoltage.second = 0L;
        mVoltageList.clear();
        mAmperage = 0L;
        mAmperageList.clear();
    }
    protected void appendAmperage(MTEC_ElectricWiresManager.InputObject aInputObject, long aAmperage) {
        Long tAmperage = mAmperageList.get(aInputObject);
        mAmperageList.put(aInputObject, tAmperage == null ? aAmperage : tAmperage + aAmperage);
        mAmperage += aAmperage;
    }
    protected Pair<Long, Long> setAndGetVoltageFromSourceVoltage(MTEC_ElectricWiresManager.InputObject aInputObject, Pair<Long, Long> aSourceVoltage) {
        // 只会也只需要设置一次电压
        if (mVoltageList.containsKey(aInputObject)) return mVoltageList.get(aInputObject);
        if (aSourceVoltage.first == 0 && aSourceVoltage.second == 0) {
            Pair<Long, Long> tVoltage = new Pair<>(0L, 0L); mVoltageList.put(aInputObject, tVoltage); return tVoltage;
        }
        long tVoltageCost = UT.Code.divup(mAmperage * mResistance, U);
        long tVoltageRest = aSourceVoltage.second + tVoltageCost * U - mAmperage * mResistance;
        long tVoltageMain = aSourceVoltage.first  - tVoltageCost + tVoltageRest / U;
        tVoltageRest %= U;
        if (tVoltageMain < 0) {tVoltageMain = 0; tVoltageRest = 0;}
        Pair<Long, Long> tVoltage = new Pair<>(tVoltageMain, tVoltageRest);
        mVoltageList.put(aInputObject, tVoltage);
        if (tVoltage.first >= mVoltage.first) {
            mVoltage.first = tVoltage.first;
            if (tVoltage.second > mVoltage.second) mVoltage.second = tVoltage.second;
        }
        return tVoltage;
    }
    // 通过对应的输出得到输出的电压
    protected long getOutputVoltage(MTEC_ElectricWiresManager.InputObject aInputObject) {
        Pair<Long, Long> tVoltage = mVoltageList.get(aInputObject);
        return tVoltage.first + (RNGSUS.nextInt((int)U) < tVoltage.second ? 1 : 0); // 电压小数部分随机取值
    }
    
    protected long mLastVoltage = 0, mLastAmperage = 0; // 用于显示和检测
    public long getVoltage() {return mLastVoltage;}
    public long getAmperage() {return mLastAmperage;}
    
    private long mMaxVoltage = 128, mMaxAmperage = 1, mResistance = U; // 固定属性，最大的电压电流和电阻
    public long getMaxVoltage() {return mMaxVoltage;}
    public long getMaxAmperage() {return mMaxAmperage;}
    public long getLoss() {return UT.Code.divup(mResistance, U);}
    public byte mBurnCounter = 0;
    
    // NBT 读写
    public void readFromNBT(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_PIPELOSS)) mResistance = Math.max(0, aNBT.getLong(NBT_PIPELOSS)) * U;
        if (aNBT.hasKey(NBT_RESISTANCE+".electric")) mResistance = Math.max(0, aNBT.getLong(NBT_RESISTANCE+".electric"));
        if (aNBT.hasKey(NBT_PIPESIZE)) mMaxVoltage = Math.max(1, aNBT.getLong(NBT_PIPESIZE));
        if (aNBT.hasKey(NBT_PIPEBANDWIDTH)) mMaxAmperage = Math.max(1, aNBT.getLong(NBT_PIPEBANDWIDTH));
    }
    
    // ticking
    public void onTick(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide) {
            // 熔毁检测
            if (mBurnCounter >= 8) {
                if (RNGSUS.nextInt(4) == 0) {mTE.setToFire(); return;} // 改为随机熔毁
                else mBurnCounter = 0;
            }
            // 更新 Manager
            if (aTimer > 2 && !mManagerUpdated) updateNetworkManager();
            // TODO 兼容输入
            
            // tick Manager
            if (mManager != null) mManager.onTick();
            // 更新属性用于检测以及下一 tick 的累计统计
            mLastVoltage = mVoltage.first + (RNGSUS.nextInt((int)U) < mVoltage.second ? 1 : 0); // 电压小数部分随机取值
            mLastAmperage = mAmperage;
            clearTemporary();
            // 熔毁计数
            if (mLastAmperage > mMaxAmperage || mLastVoltage > mMaxVoltage) ++mBurnCounter;
            if (aTimer % 32 == (2 + RNGSUS.nextInt(16)) && mBurnCounter > 0) mBurnCounter--;
        }
    }
    
    // tooltips
    public void addToolTips(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_VOLTAGE)  + mMaxVoltage + " " + TD.Energy.EU.getLocalisedNameShort() + " (" + VN[UT.Code.tierMin(mMaxVoltage)] + ")");
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_AMPERAGE) + mMaxAmperage);
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_LOSS)     + LH.numberU(mResistance) + " " + "Ω/m");
    }
    
    // toolclick
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (DATA_GTCH.debugging && aTool.equals(TOOL_magnifyingglass)) {
            if (aChatReturn != null) {
                aChatReturn.add("===========DEBUG INFO===========");
                if (mManager != null) {
                    aChatReturn.add("INPUT: ");
                    for (MTEC_ElectricWiresManager.InputObject tInput : mManager.mInputs.values()) {
                        aChatReturn.add(String.format("  At: (%d, %d, %d)", tInput.mIOTE.xCoord, tInput.mIOTE.yCoord, tInput.mIOTE.zCoord) + "; Energy: " + tInput.mEnergyBuffer.mEnergy);
                    }
                    aChatReturn.add("OUTPUT: ");
                    for (MTEC_ElectricWiresManager.OutputObject tOutput : mManager.mOutputs.values()) {
                        aChatReturn.add(String.format("  At: (%d, %d, %d)", tOutput.mIOTE.xCoord, tOutput.mIOTE.yCoord, tOutput.mIOTE.zCoord));
                        for (MTEC_ElectricWiresManager.EnergyBuffer tBuffer : tOutput.mInputBuffers.values()) aChatReturn.add("    Energy: " + tBuffer.mEnergy);
                    }
                }
                aChatReturn.add("=========DEBUG INFO END=========");
            }
            return 1;
        }
        return 0;
    }
    
    // 为了减少重复代码，获取一个方向的 core，如果不是 core 则返回 null，如果是 te 则添加到输出列表中
    protected MTEC_ElectricWireBase getAdjacentCore(byte aSide) {
        if (!te().connected(aSide)) return null;
        DelegatorTileEntity<TileEntity> tDelegator = mTE.getAdjacentTileEntity(aSide);
        // 保证相邻的也是这个 core，并且也连接了这个方向
        if (tDelegator.mTileEntity instanceof IMTEC_HasElectricWire && ((IMTEC_HasElectricWire)tDelegator.mTileEntity).connected(tDelegator.mSideOfTileEntity))
            return ((IMTEC_HasElectricWire)tDelegator.mTileEntity).core();
        return null;
    }
    protected MTEC_ElectricWireBase getAdjacentCoreAndPutOutput(byte aSide, @NotNull MTEC_ElectricWiresManager aManager) {
        if (!te().connected(aSide)) return null;
        DelegatorTileEntity<TileEntity> tDelegator = mTE.getAdjacentTileEntity(aSide);
        // 保证相邻的也是这个 core，并且也连接了这个方向
        if (tDelegator.mTileEntity instanceof IMTEC_HasElectricWire && ((IMTEC_HasElectricWire)tDelegator.mTileEntity).connected(tDelegator.mSideOfTileEntity))
            return ((IMTEC_HasElectricWire)tDelegator.mTileEntity).core();
        // 如果不是 core，并且有 te，则添加到输出
        if (tDelegator.mTileEntity != null) aManager.putOutput(this, aSide, tDelegator.mTileEntity);
        return null;
    }
    
    // 更新 Manager，注意由于存在拆开网络的情况，无论如何都需要使用新的 Manager 来覆盖旧的，并且注意需要串行执行
    // TODO 应该可以对于小改动进行优化，这里没有很好的思路（对于更新进行延迟，保证连续的修改结构后只在停止修改时进行最后一次更新之类的）
    protected synchronized void updateNetworkManager() {
        if (mManagerUpdated) return; // 考虑到有可能平行执行，因此需要在这里再次进行判断
        Set<MTEC_ElectricWiresManager> tManagersToMerge = new HashSetNoNulls<>();
        
        if (mTE.getTimer() < 2) return; // 此时近邻未加载，直接退出
        // 如果自身不为 null 则需要加入合并
        if (mManager != null) tManagersToMerge.add(mManager);
        // 无论如何都需要使用新的 Manager
        mManager = new MTEC_ElectricWiresManager();
        mManagerUpdated = T;
        // 获取周围连接的 core，同样设置 Manager
        for (byte tSide : ALL_SIDES) {
            MTEC_ElectricWireBase tCore = getAdjacentCoreAndPutOutput(tSide, mManager);
            if (tCore == null) continue;
            tCore.setNetworkManager(mManager, tManagersToMerge);
        }
        
        // 处理合并 Manager
        for (MTEC_ElectricWiresManager tManager : tManagersToMerge) mManager.mergeManager(tManager);
        // 更新 Manager，表示已经初始化
        mManager.update();
    }
    // 向周围传递的方式来初始化 Manager，递归实现
    private void setNetworkManager(@NotNull MTEC_ElectricWiresManager aManager, @NotNull Set<MTEC_ElectricWiresManager> aManagersToMerge) {
        if (mTE.getTimer() < 2) return; // 此时近邻未加载，直接退出
        if (mManager == aManager) return; // 已经设置过则 Manager 相同，退出
        // 设置自身的 Manager，如果不为 null 则需要放入 aManagersToMerge 用于合并
        if (mManager != null) aManagersToMerge.add(mManager);
        mManager = aManager; mManagerUpdated = T;
        // 获取周围连接的 core，同样设置 Manager
        for (byte tSide : ALL_SIDES) {
            MTEC_ElectricWireBase tCore = getAdjacentCoreAndPutOutput(tSide, aManager);
            if (tCore == null) continue;
            tCore.setNetworkManager(aManager, aManagersToMerge);
        }
    }
    
    // 能量注入，并获取成功注入的电流量
    public long doEnergyInjection (TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {return aSize != 0 && mManager != null && te().isEnergyAcceptingFrom(aEnergyType, aSide, F) ? aDoInject ? mManager.doEnergyInjection(this, aSide, aSize, aAmount) : aAmount : 0;}
}
