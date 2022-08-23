package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.WD;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

import static gregapi.data.CS.*;

public class MTEC_MultiBlockFermenter extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockFermenter(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2)-2, tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2)-2;
        if (mTE.getWorldObj().blockExists(tX-2, tY, tZ-2) && mTE.getWorldObj().blockExists(tX+2, tY, tZ-2) && mTE.getWorldObj().blockExists(tX-2, tY, tZ+2) && mTE.getWorldObj().blockExists(tX+2, tY, tZ+2)) {
            boolean tSuccess = T;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ+2, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ+2, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY-1, tZ+2, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY-1, tZ+2, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY-1, tZ+2, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ+3, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ+3, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY-1, tZ+3, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY-1, tZ+3, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY-1, tZ+3, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ+4, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ+4, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY-1, tZ+4, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY-1, tZ+4, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY-1, tZ+4, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), SIDE_Z_POS == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), SIDE_X_POS == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), SIDE_X_NEG == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), SIDE_Z_NEG == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), SIDE_Z_POS == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), SIDE_X_POS == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), SIDE_X_NEG == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+3, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), SIDE_Z_NEG == mTE.mFacing ? 7 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+4, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;

            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.fermenter.1", "5x5 of Heat Transmitters as bottom Layer");
        LH.add("gt.tooltip.multiblock.fermenter.2", "5x5x2 of Stainless Steel Walls");
        LH.add("gt.tooltip.multiblock.fermenter.3", "Main Block centered on Side-Middle and facing outwards");
        LH.add("gt.tooltip.multiblock.fermenter.4", "Walls accept Input, Top Back emits Items, Bottom Back emits Fluids");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.fermenter.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.fermenter.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.fermenter.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.fermenter.4"));
    }
    
    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        return aX >= tX - 2 && aY >= tY - 1 && aZ >= tZ - 2 && aX <= tX + 2 && aY <= tY + 1 && aZ <= tZ + 2;
    }
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
        DelegatorTileEntity tDelegator = WD.te(mTE.getWorldObj(), mTE.getOffsetX(OPOS[mTE.mFacing], 5), mTE.yCoord, mTE.getOffsetZ(OPOS[mTE.mFacing], 5), OPOS[mTE.mFacing], F);
        return tDelegator.mTileEntity instanceof IFluidHandler ? tDelegator : null;
    }
    @Override
    public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
        return WD.te(mTE.getWorldObj(), mTE.getOffsetX(OPOS[mTE.mFacing], 5), mTE.yCoord+1, mTE.getOffsetZ(OPOS[mTE.mFacing], 5), OPOS[mTE.mFacing], F);
    }
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.fermenter";}
}
