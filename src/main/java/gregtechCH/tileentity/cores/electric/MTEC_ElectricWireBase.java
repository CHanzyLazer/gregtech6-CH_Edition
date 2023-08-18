package gregtechCH.tileentity.cores.electric;

import gregapi.GT_API_Proxy;
import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.tileentity.ITileEntityServerTickPre;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.EnergyCompat;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import gregtechCH.GTCH_Main;
import gregtechCH.code.Pair;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.IMTEServerTickParallel;
import gregtechCH.util.UT_CH;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
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
import static gregtechCH.config.ConfigForge.DATA_GTCH;

/**
 * @author CHanzy
 * 基本电线的类，存储此电线的电压，电流等数值
 * 相比原本的逻辑更加复杂，加入并行来抵消这种影响
 */
public class MTEC_ElectricWireBase implements IMTEServerTickParallel, ITileEntityServerTickPre {
    // par tick stuff
    @Override public void setError(String aError) {mTE.setError(aError);}
    @Override public boolean isDead() {return mTE.isDead();}
    @Override public void onUnregisterPar() {mHasToAddTimerPar = T;}
    private boolean mHasToAddTimerPar = T;
    @Override public void onUnregisterPre() {mHasToAddTimerPre = T;}
    private boolean mHasToAddTimerPre = T;
    
    protected MTEC_ElectricWiresManager mManager = null; // reference of Manager
    protected final TileEntityBase01Root mTE; // reference of te
    
    public MTEC_ElectricWireBase(TileEntityBase01Root aTE) {UT_CH.Debug.assertWhenDebug(aTE instanceof IMTEC_HasElectricWire); mTE = aTE;}
    protected IMTEC_HasElectricWire te() {return (IMTEC_HasElectricWire)mTE;}
    
    /* main code */
    private boolean mManagerUpdated = F; // 用于在第一次放置时，连接发生改变或者近邻更改时设置为 F，然后通过 ticking 来进行更新
    public void markUpdateManager() {if (mManager != null) mManager.setInvalid();} // 用于外部标记需要更新，因此仅通过网络标记需要更新，对于已经有网络的延迟更新。如果网络为空会自动根据 tick 来更新
    
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
    
    public long getVoltage() {return mVoltage.first + (RNGSUS.nextInt((int)U) < mVoltage.second ? 1 : 0);}
    public long getAmperage() {return mAmperage;}
    
    private long mMaxVoltage = 128, mMaxAmperage = 1, mResistance = U; // 固定属性，最大的电压电流和电阻
    public long getMaxVoltage() {return mMaxVoltage;}
    public long getMaxAmperage() {return mMaxAmperage;}
    public long getLoss() {return UT.Code.divup(mResistance, U);}
    private byte mBurnCounter = 0;
    public boolean willBurn() {return mBurnCounter >= 16;}
    public void cooldown() {mBurnCounter -= 4;}
    
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
            // 将自己的网络更新部分加入到串行的 pre tick 中，保证网络更新是串行的，不干扰其他部分的 tick；在 pre 保证更新网络后才会跟着网络的 tick
            if (mHasToAddTimerPre) {
                GT_API_Proxy.SERVER_TICK_PRE.add(this);
                mHasToAddTimerPre = F;
            }
            // 将自己的其余部分加入到并行 tick 中，加入到 pa2 保证在 manager 之后
            if (mHasToAddTimerPar) {
                GTCH_Main.addToServerTickParallel2(this);
                mHasToAddTimerPar = F;
            }
        }
    }
    
    // 将网络更新部分加入到串行的 pre tick 中，保证网络更新是串行的，不干扰其他部分的 tick
    @Override public void onServerTickPre(boolean aFirst) {
        long tTimer = mTE.getTimer();
        if (mManager == null) {
            // 服务器初始化时需要立刻更新，其余情况的 null 为玩家放置，延迟更新
            if (SERVER_TIME < 4 || tTimer > 4) mManagerUpdated = F;
        } else {
            // 如果 mManager 已经需要更新则马上更新（内部进行了延迟计数）
            if (mManager.needUpdate()) mManagerUpdated = F;
        }
        if (!mManagerUpdated && tTimer > 2) updateNetworkManager();
        // 以防万一，还是需要在这里再次将 mManager 添加到并行 tick 中
        if (mManagerUpdated && mManager != null && mManager.mHasToAddTimerPar && tTimer % 16 == 2 && !mManager.isDead()) {
            GTCH_Main.addToServerTickParallel(mManager);
            mManager.mHasToAddTimerPar = F;
        }
    }
    @SuppressWarnings("deprecation")
    @Override public void onServerTickPar(boolean aFirst) {
        // 兼容输入
        if (EnergyCompat.IC_ENERGY && mManagerUpdated && mManager != null) for (byte tSide : ALL_SIDES_VALID) if (te().canAcceptEnergyFrom(tSide)) {
            DelegatorTileEntity<TileEntity> tDelegator = mTE.getAdjacentTileEntity(tSide);
            //noinspection ConditionCoveredByFurtherCondition
            if (!(tDelegator.mTileEntity instanceof ITileEntityEnergy) && !(tDelegator.mTileEntity instanceof gregapi.tileentity.ITileEntityEnergy)) {
                TileEntity tEmitter = tDelegator.mTileEntity instanceof IEnergyTile || EnergyNet.instance == null ? tDelegator.mTileEntity : EnergyNet.instance.getTileEntity(tDelegator.mWorld, tDelegator.mX, tDelegator.mY, tDelegator.mZ);
                if (tEmitter instanceof IEnergySource && ((IEnergySource)tEmitter).emitsEnergyTo(mTE, tDelegator.getForgeSideOfTileEntity())) {
                    long tEU = (long)((IEnergySource)tEmitter).getOfferedEnergy();
                    if (mManager.doEnergyInjection(this, tSide, tEU, 1) == 1) ((IEnergySource)tEmitter).drawEnergy(tEU);
                }
            }
        }
        // 熔毁计数
        if (mAmperage > mMaxAmperage || (mVoltage.first >= mMaxVoltage && (mVoltage.first != mMaxVoltage || mVoltage.second > 0))) {++mBurnCounter;}
        if (mBurnCounter > 0 && (mTE.getTimer() % 512 == 2)) --mBurnCounter;
    }
    
    // tooltips
    public void toolTipsDescribe(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_VOLTAGE)          + mMaxVoltage + " " + TD.Energy.EU.getLocalisedNameShort() + " (" + VN[UT.Code.tierMin(mMaxVoltage)] + ")");
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_AMPERAGE)         + mMaxAmperage);
        aList.add(LH.Chat.CYAN     + LH_CH.get(LH_CH.WIRE_STATS_RESISTANCE) + LH.numberU(mResistance) + " " + "Ω/m");
    }
    public void toolTipsUseful(List<String> aList) {
        aList.add(LH.Chat.GREEN    + LH_CH.get("gtch.tooltip.wire.ohm"));
    }
    public void toolTipsOther(List<String> aList) {
        aList.add(LH.Chat.DGRAY    + LH_CH.get(LH_CH.TOOL_TO_MEASURE_ELECTOMETER));
    }
    static {
        LH_CH.add("gtch.tooltip.wire.ohm", "The Larger the Amperage, the Greater the actual Loss (Ohm's Law)");
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
        if (aTool.equals(TOOL_electrometer)) {
            if (aChatReturn != null) {
                long tVoltage = getVoltage();
                long tAmperage = getAmperage();
                aChatReturn.add(String.format("%d EU/t (Voltage: %d, Amperage: %d)", tAmperage*tVoltage, tVoltage, tAmperage));
            }
            return 1;
        }
        
        return 0;
    }
    
    // 实用方法（考虑了连接）
    protected MTEC_ElectricWireBase getAdjacentCore(byte aSide) {
        if (!te().connected(aSide)) return null;
        DelegatorTileEntity<TileEntity> tDelegator = mTE.getAdjacentTileEntity(aSide);
        // 保证相邻的也是这个 core，并且也连接了这个方向
        if (tDelegator.mTileEntity instanceof IMTEC_HasElectricWire && ((IMTEC_HasElectricWire)tDelegator.mTileEntity).connected(tDelegator.mSideOfTileEntity))
            return ((IMTEC_HasElectricWire)tDelegator.mTileEntity).core();
        return null;
    }
    protected TileEntity getAdjacentTE(byte aSide) {
        if (!te().connected(aSide)) return null;
        DelegatorTileEntity<TileEntity> tDelegator = mTE.getAdjacentTileEntity(aSide);
        if (tDelegator.mTileEntity != null) return tDelegator.mTileEntity;
        return null;
    }
    // 为了减少重复代码，获取一个方向的 core，如果不是 core 则返回 null，如果是 te 则添加到输出列表中
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
    // 判断一个 core 是否是相邻的，考虑了连接。虽然可以使用坐标但是直接遍历应该差距不大
    protected boolean isAdjacentCore(MTEC_ElectricWireBase aCore) {
        for (byte tSide : ALL_SIDES_VALID) if (aCore == getAdjacentCore(tSide)) return T;
        return F;
    }
    
    // 更新 Manager，注意由于存在拆开网络的情况，无论如何都需要使用新的 Manager 来覆盖旧的，串行执行
    private void updateNetworkManager() {
        UT_CH.Debug.assertWhenDebug(!mManagerUpdated, "Update an updated network.");
        UT_CH.Debug.assertWhenDebug(mTE.getTimer() > 2, "Update network too early");
        
        Set<MTEC_ElectricWiresManager> tManagersToMerge = new HashSetNoNulls<>();
        // 如果自身不为 null 则需要加入合并
        if (mManager != null) tManagersToMerge.add(mManager);
        // 无论如何都需要使用新的 Manager
        mManager = new MTEC_ElectricWiresManager();
        mManagerUpdated = T;
        clearTemporary(); // 只是以防万一
        // 获取周围连接的 core，同样设置 Manager
        for (byte tSide : ALL_SIDES) {
            MTEC_ElectricWireBase tCore = getAdjacentCoreAndPutOutput(tSide, mManager);
            if (tCore == null) continue;
            tCore.setNetworkManager(mManager, tManagersToMerge);
        }
        
        /// 使 Manager 合法
        UT_CH.Debug.assertWhenDebug(!mManager.valid());
        // 处理合并 Manager，并将被合并的 manager 设置非法
        for (MTEC_ElectricWiresManager tManager : tManagersToMerge) {
            mManager.mergeManager(tManager);
            tManager.setInvalid(T);
            GTCH_Main.removeFromServerTickParallel(tManager); // 移除已经非法的网络
        }
        // 更新 Manager，表示已经初始化
        mManager.update();
        // 添加到并行 tick 中
        GTCH_Main.addToServerTickParallel(mManager);
        mManager.mHasToAddTimerPar = F;
    }
    
    // 向周围传递的方式来初始化 Manager，递归实现
    private void setNetworkManager(@NotNull MTEC_ElectricWiresManager aManager, @NotNull Set<MTEC_ElectricWiresManager> aManagersToMerge) {
        if (mTE.getTimer() < 2) return; // 此时近邻未加载，直接退出
        if (mManager == aManager) return; // 已经设置过则 Manager 相同，退出
        // 设置自身的 Manager，如果不为 null 则需要放入 aManagersToMerge 用于合并
        if (mManager != null) aManagersToMerge.add(mManager);
        mManager = aManager; mManagerUpdated = T;
        clearTemporary(); // 只是以防万一
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
