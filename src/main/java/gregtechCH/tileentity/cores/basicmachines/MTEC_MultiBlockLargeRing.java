package gregtechCH.tileentity.cores.basicmachines;

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies, CHanzy
 * 基本的 Ring 类，聚变反应堆，粒子碰撞机等等，用来减少重复代码
 */
public abstract class MTEC_MultiBlockLargeRing extends MTEC_MultiBlockMachine {
    protected MTEC_MultiBlockLargeRing(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    // 中心需要的处理器数量
    public int processorNumberVersatile() {return 3;}
    public int processorNumberLogic() {return 12;}
    public int processorNumberControl() {return 12;}
    // 周围环的材料
    public abstract int ringOuterCoverMeta();
    public abstract int ringInnerCoverMeta();
    public abstract int ringCenterMeta();
    // 背部接口的属性
    public int backPartMode() {return MultiTileEntityMultiBlockPart.ONLY_ENERGY_OUT;}
    
    /* main code */
    @Override
    public final boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        if (mTE.getWorldObj().blockExists(tX-9, tY, tZ-9) && mTE.getWorldObj().blockExists(tX+9, tY, tZ-9) && mTE.getWorldObj().blockExists(tX-9, tY, tZ+9) && mTE.getWorldObj().blockExists(tX+9, tY, tZ+9)) {
            boolean tSuccess = T;
            
            // 中心的处理器
            int tVersatile = processorNumberVersatile(), tLogic = processorNumberLogic(), tControl = processorNumberControl();
            for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) for (int k = -2; k <= 2; k++) {
                if (i*i + j*j + k*k < 4) {
                    if (ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18200, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) {
                        tVersatile--;
                    } else
                    if (ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18201, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) {
                        tLogic--;
                    } else
                    if (ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) {
                        tControl--;
                    } else {
                        tSuccess = F;
                    }
                } else if (i*i + j*j + k*k > 6 || (j == 0 && (((i == -2 || i == 2) && k == 0) || (((k == -2 || k == 2) && i == 0))))) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                }
            }
            if (tVersatile > 0 || tLogic > 0 || tControl > 0) tSuccess = F;
            
            if (mTE.mFacing != SIDE_X_NEG) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-3, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-4, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
            if (mTE.mFacing != SIDE_X_POS) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
            if (mTE.mFacing != SIDE_Z_NEG) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ-3, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ-4, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
            if (mTE.mFacing != SIDE_Z_POS) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ+3, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ+4, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
    
            // 周围的环
            int tRingOuterCoverMeta = ringOuterCoverMeta(), tRingInnerCoverMeta = ringInnerCoverMeta(), tRingCenterMeta = ringCenterMeta();
            tX -= 9; tZ -= 9;
            for (int i = 0; i < 19; i++) for (int j = 0; j < 19; j++) {
                if (OCTAGONS[0][i][j]) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-1, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                    if ((i == 9 && (j == 0 || j == 18)) || (j == 9 && (i == 0 || i == 18))) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 2, backPartMode())) tSuccess = F;
                    } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), mTE.mActive ? 6 : 5, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
                    }
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+1, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
                if (OCTAGONS[1][i][j]) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-2, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-1, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, tRingInnerCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+1, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+2, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
                if (OCTAGONS[2][i][j]) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-2, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-1, tZ+j, tRingInnerCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, tRingCenterMeta    , mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+1, tZ+j, tRingInnerCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+2, tZ+j, tRingOuterCoverMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
            }
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    public static boolean[][][] OCTAGONS = {{
        {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
        {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
        {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
        {F,F,F,T,F,F,F,T,T,T,T,T,F,F,F,T,F,F,F},
        {F,F,T,F,F,F,T,F,F,F,F,F,T,F,F,F,T,F,F},
        {F,T,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,T,F},
        {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
        {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
        {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
        {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
        {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
        {F,T,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,T,F},
        {F,F,T,F,F,F,T,F,F,F,F,F,T,F,F,F,T,F,F},
        {F,F,F,T,F,F,F,T,T,T,T,T,F,F,F,T,F,F,F},
        {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
        {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
        {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
    }, {
        {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
        {F,F,F,F,F,T,F,T,T,T,T,T,F,T,F,F,F,F,F},
        {F,F,F,F,T,F,T,F,F,F,F,F,T,F,T,F,F,F,F},
        {F,F,F,T,F,T,F,F,F,F,F,F,F,T,F,T,F,F,F},
        {F,F,T,F,T,F,F,F,F,F,F,F,F,F,T,F,T,F,F},
        {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
        {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
        {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
        {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
        {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
        {F,F,T,F,T,F,F,F,F,F,F,F,F,F,T,F,T,F,F},
        {F,F,F,T,F,T,F,F,F,F,F,F,F,T,F,T,F,F,F},
        {F,F,F,F,T,F,T,F,F,F,F,F,T,F,T,F,F,F,F},
        {F,F,F,F,F,T,F,T,T,T,T,T,F,T,F,F,F,F,F},
        {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
    }, {
        {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
        {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
        {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
        {F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F},
        {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
        {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
        {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
        {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
        {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
        {F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F},
        {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
        {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
        {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
        {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
    }};
    
    @Override
    public final boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord-2, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        return aX >= tX - 9 && aY >= tY && aZ >= tZ - 9 && aX <= tX + 9 && aY <= tY + 5 && aZ <= tZ + 9;
    }
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return null;}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
}
