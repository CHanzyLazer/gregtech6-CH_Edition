package gregtechCH.tileentity.cores;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
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

public class MTEC_MultiBlockSluice extends MTEC_MultiblockMachine {
    public MTEC_MultiBlockSluice(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int
        tMinX = mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?6:1),
        tMinZ = mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?6:1),
        tMaxX = mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?6:1),
        tMaxZ = mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?6:1),
        tD = (mTE.mActive?mTE.mFacing+2:mTE.mFacing-2);

        if (mTE.getWorldObj().blockExists(tMinX, mTE.yCoord, tMinZ) && mTE.getWorldObj().blockExists(tMaxX, mTE.yCoord+2, tMaxZ)) {
            boolean tSuccess = T;
            for (int tX = tMinX; tX <= tMaxX; tX++) for (int tZ = tMinZ; tZ <= tMaxZ; tZ++) {
                if (SIDES_AXIS_X[mTE.mFacing] ? tX == mTE.xCoord : tZ == mTE.zCoord) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord  , tZ, 18006, mTE.getMultiTileEntityRegistryID(), 1, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord  , tZ, 18006, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                }

                if (SIDES_AXIS_X[mTE.mFacing] ? Math.abs(tX-mTE.xCoord)==5 && tZ != mTE.zCoord : Math.abs(tZ-mTE.zCoord)==5 && tX != mTE.xCoord) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord+1, tZ, 18006, mTE.getMultiTileEntityRegistryID(), 3, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord+1, tZ, 18006, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                }

                if (SIDES_AXIS_X[mTE.mFacing] ? Math.abs(tX-mTE.xCoord)==6 : Math.abs(tZ-mTE.zCoord)==6) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord+2, tZ, 18106, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord+2, tZ, 18106, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                }
            }
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.sluice.1", "Two 3x7 Layers of Titanium Walls");
        LH.add("gt.tooltip.multiblock.sluice.2", "3x7 Layer of Sluice Parts ontop of that");
        LH.add("gt.tooltip.multiblock.sluice.3", "Main Block centered on Slim-Side-Bottom and facing outwards");
        LH.add("gt.tooltip.multiblock.sluice.4", "Input only at the Top of the Far Side");
        LH.add("gt.tooltip.multiblock.sluice.5", "Output only at the Bottom of the Close Side");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.sluice.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.sluice.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.sluice.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.sluice.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.sluice.5"));
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return
        aX >= mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?6:1) &&
        aY >= mTE.yCoord   &&
        aZ >= mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?6:1) &&
        aX <= mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?6:1) &&
        aY <= mTE.yCoord+2 &&
        aZ <= mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?6:1);
    }
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.sluice";}
}
