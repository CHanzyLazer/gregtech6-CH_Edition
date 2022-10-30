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

// 基本的 Tower 类，各种蒸馏塔，裂解塔等等，用来减少重复代码
public abstract class MTEC_MultiBlockTowerBase extends MTEC_MultiBlockMachine {
    protected MTEC_MultiBlockTowerBase(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    public abstract int towerHeight(); // 塔的高度
    public int basePartMeta() {return 18101;} // 底座部分的 Meta / ID，默认是热吸收装置
    public abstract int mainPartMeta(); // 主要部分的 Meta / ID
    
    /* main code */
    @Override
    public final boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing);
        if (mTE.getWorldObj().blockExists(tX-1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX-1, tY, tZ+1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = T;
            int tBaseMeta = basePartMeta();
            int tMainMeta = mainPartMeta();
            
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY-1, tZ-1, tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ-1, tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ-1, tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY-1, tZ  , tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ  , tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ  , tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY-1, tZ+1, tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ+1, tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ+1, tBaseMeta, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY  , tZ-1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ-1, tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ-1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY  , tZ  , tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ  , tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ  , tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY  , tZ+1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+1, tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            for (int i = 1; i < towerHeight(); ++i) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY+i, tZ-1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+i, tZ-1, tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+i, tZ-1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY+i, tZ  , tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+i, tZ  , tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+i, tZ  , tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY+i, tZ+1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+i, tZ+1, tMainMeta, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+i, tZ+1, tMainMeta, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
            }
            
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    @Override
    public final boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing);
        return aX >= tX - 1 && aY >= tY - 1 && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + towerHeight() - 1 && aZ <= tZ + 1;
    }
    
    // tower 需要在内部实现流体分发，这里暂时不包含到 core 里
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return null;}
}
