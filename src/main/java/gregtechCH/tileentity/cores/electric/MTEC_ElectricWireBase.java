package gregtechCH.tileentity.cores.electric;

import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static gregapi.data.CS.*;

/**
 * WIP
 * 基本电线的类，存储此电线的电压，电流等数值
 */
public class MTEC_ElectricWireBase {
    protected MTEC_ElectricWiresManager mManager = null; // reference of Manager
    protected final TileEntityBase01Root mTE; // reference of te
    
    public MTEC_ElectricWireBase(TileEntityBase01Root aTE) {assert aTE instanceof IMTEC_HasElectricCore; mTE = aTE;}
    protected IMTEC_HasElectricCore te() {return (IMTEC_HasElectricCore)mTE;}
    
    /* main code */
    protected boolean mManagerUpdated = F; // 用于在第一次放置时，连接发生改变或者近邻更改时设置为 F，然后通过 ticking 来进行更新
    public void updateManager() {mManagerUpdated = F;}
    
    public long mVoltage = 0, mAmperage = 0; // 保存这个电线的电压电流值 咱没有实际意义
    
    // ticking
    public void onTick(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide) {
            // 更新 Manager
            if (aTimer > 2 && !mManagerUpdated) updateNetworkManager();
            // tick Manager
            if (mManager != null) mManager.onTick();
        }
    }
    
    // 为了减少重复代码，获取一个方向的 core，如果不是 core 则返回 null，如果是 te 则添加到输出列表中
    protected MTEC_ElectricWireBase getAdjacentCoreAndPutOutput(byte aSide, @NotNull MTEC_ElectricWiresManager aManager) {
        if (!te().connected(aSide)) return null;
        DelegatorTileEntity<TileEntity> tDelegator = mTE.getAdjacentTileEntity(aSide);
        // 保证相邻的也是这个 core，并且也连接了这个方向
        if (tDelegator.mTileEntity instanceof IMTEC_HasElectricCore && ((IMTEC_HasElectricCore)tDelegator.mTileEntity).connected(tDelegator.mSideOfTileEntity))
            return ((IMTEC_HasElectricCore)tDelegator.mTileEntity).core();
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
