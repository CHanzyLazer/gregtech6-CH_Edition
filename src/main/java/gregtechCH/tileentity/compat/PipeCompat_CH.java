package gregtechCH.tileentity.compat;

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.delegate.ITileEntityCanDelegate;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtechCH.tileentity.ITEInterceptAutoConnectFluid_CH;
import gregtechCH.tileentity.ITEInterceptAutoConnectItem_CH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import static gregapi.data.CS.*;

// 用于处理管道和其他 mod 的连接
public class PipeCompat_CH {
    public static boolean BC_PIPES = F, EIO_PIPES = F;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkAvailabilities() {
        try {
            buildcraft.api.transport.IPipeTile.class.getCanonicalName();
            BC_PIPES = T;
        } catch(Throwable e) {/**/}
        try {
            crazypants.enderio.conduit.IConduitBundle.class.getCanonicalName();
            EIO_PIPES = T;
        } catch(Throwable e) {/**/}
    }

    // 注意方向是 target 的接收方向
    public static boolean canConnectFluid(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        if (aTarget == null || aThis == null) return F;
        // 通用和特殊的连接都允许
        if (checkConnectFluidCom(aThis, aTarget, aSide) || checkConnectFluidSpecial(aThis, aTarget, aSide)) return T;
        return F;
    }

    public static boolean canAutoConnectFluid(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        if (aTarget == null || aThis == null) return F;
        // 先拒绝需要阻止自动连接的类型
        if (interceptAutoConnectFluid(aThis, aTarget, aSide)) return F;
        // 仅通用连接允许
        if (checkConnectFluidCom(aThis, aTarget, aSide)) return T;
        return F;
    }

    public static boolean canConnectItem(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        if (aTarget == null || aThis == null) return F;
        if (checkConnectItemCom(aThis, aTarget, aSide) || checkConnectItemSpecial(aThis, aTarget, aSide)) return T;
        return F;
    }
    public static boolean canAutoConnectItem(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        if (aTarget == null || aThis == null) return F;
        if (interceptAutoConnectItem(aThis, aTarget, aSide)) return F;
        if (checkConnectItemCom(aThis, aTarget, aSide)) return T;
        return F;
    }

    // 用来减少重复代码
    // 通用检测
    private static boolean checkConnectFluidCom(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        // 对于 GT 或其他方块的检测
        if (aTarget.mTileEntity instanceof IFluidHandler) {
            // Extenders should always be connectable.
            if (aTarget.mTileEntity instanceof ITileEntityCanDelegate) return T;
            // Make sure at least one Tank exists at this Side to connect to.
            if (UT.Code.exists(0, ((IFluidHandler)aTarget.mTileEntity).getTankInfo(FORGE_DIR[aSide]))) return T;
        }
        // 坩埚检测
        if (aThis instanceof IFluidHandler) {
            FluidTankInfo[] tInfo = ((IFluidHandler)aThis).getTankInfo(FORGE_DIR[aSide]);
            if (tInfo != null && tInfo.length > 0 && tInfo[0].capacity >= 334) {
                Block tBlock = aTarget.getBlock();
                // Yes I need to check for both to make this work. Some Mods override the Cauldron in a bad way.
                if (tBlock == Blocks.cauldron || tBlock instanceof BlockCauldron) return T;
            }
        }
        return F;
    }
    // 特殊的需要能够连接的检测
    private static boolean checkConnectFluidSpecial(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        // 对于 BC 管道的兼容
        if (BC_PIPES && aTarget.mTileEntity instanceof buildcraft.api.transport.IPipeTile) {
            buildcraft.api.transport.IPipeTile tPipe = (buildcraft.api.transport.IPipeTile)aTarget.mTileEntity;
            if (tPipe.getPipeType()!=buildcraft.api.transport.IPipeTile.PipeType.FLUID) return F;
            if (tPipe.hasBlockingPluggable(FORGE_DIR[aSide])) return F;
            return T;
        }
        // TODO 更多 mod 的管道兼容
        return F;
    }
    // 需要阻止自动连接的检测
    private static boolean interceptAutoConnectFluid(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        if (aTarget.mTileEntity instanceof ITEInterceptAutoConnectFluid_CH && ((ITEInterceptAutoConnectFluid_CH)aTarget.mTileEntity).interceptConnectFluid(aSide)) return T;
        // 对于 EnderIO 管道的兼容，防止流体管道自动连接 EnderIO 的管道
        if (EIO_PIPES && aTarget.mTileEntity instanceof crazypants.enderio.conduit.IConduitBundle) return T;
        return F;
    }

    // 通用检测
    private static boolean checkConnectItemCom(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        // 对于 GT 或其他方块的检测
        if (aTarget.mTileEntity instanceof ISidedInventory) {
            // Extenders should always be connectable.
            if (aTarget.mTileEntity instanceof ITileEntityCanDelegate) return T;
            if (((ISidedInventory)aTarget.mTileEntity).getAccessibleSlotsFromSide(aSide).length > 0) return T;
        }
        return F;
    }
    // 特殊的需要能够连接的检测
    private static boolean checkConnectItemSpecial(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        // 对于 BC 管道的兼容
        if (BC_PIPES && aTarget.mTileEntity instanceof buildcraft.api.transport.IPipeTile) {
            buildcraft.api.transport.IPipeTile tPipe = (buildcraft.api.transport.IPipeTile)aTarget.mTileEntity;
            if (tPipe.getPipeType()!= buildcraft.api.transport.IPipeTile.PipeType.ITEM) return F;
            if (tPipe.hasBlockingPluggable(FORGE_DIR[aSide])) return F;
            return T;
        }
        // greg 提供的兼容接口
        if (ST.canConnect(aTarget)) return T;
        // TODO 更多 mod 的管道兼容
        return F;
    }
    // 需要阻止自动连接的检测
    private static boolean interceptAutoConnectItem(TileEntity aThis, DelegatorTileEntity<TileEntity> aTarget, byte aSide) {
        if (aTarget.mTileEntity instanceof ITEInterceptAutoConnectItem_CH && ((ITEInterceptAutoConnectItem_CH)aTarget.mTileEntity).interceptConnectItem(aSide)) return T;
        // 对于 EnderIO 管道的兼容，防止物品管道自动连接 EnderIO 的管道
        if (EIO_PIPES && aTarget.mTileEntity instanceof crazypants.enderio.conduit.IConduitBundle) return T;
        return F;
    }

}
