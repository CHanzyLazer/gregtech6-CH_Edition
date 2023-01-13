package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.data.LH_CH;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.RegType;

/**
 * @author Gregorius Techneticies, YueSha, CHanzy
 * stuff from GT6U
 */
public class MTEC_MultiBlockHeatMixer extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockHeatMixer(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    /* main code */
    // TODO 这是什么逻辑？
    @Override
    public boolean checkStructure2() {
        int
        tMinX = mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?3:1),
        tMinY = mTE.yCoord-(SIDE_Y_NEG==mTE.mFacing?0:SIDE_Y_POS==mTE.mFacing?3:1),
        tMinZ = mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?3:1),
        tMaxX = mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?3:1),
        tMaxY = mTE.yCoord+(SIDE_Y_POS==mTE.mFacing?0:SIDE_Y_NEG==mTE.mFacing?3:1),
        tMaxZ = mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?3:1),
        tOutX = mTE.getOffsetXN(mTE.mFacing, 3),
        tOutY = mTE.getOffsetYN(mTE.mFacing, 3),
        tOutZ = mTE.getOffsetZN(mTE.mFacing, 3);
    
        if (mTE.getWorldObj().blockExists(tMinX, tMinY, tMinZ) && mTE.getWorldObj().blockExists(tMaxX, tMaxY, tMaxZ)) {
            boolean tSuccess = T;
            for (int tX = tMinX; tX <= tMaxX; tX++) for (int tY = tMinY; tY <= tMaxY; tY++) for (int tZ = tMinZ; tZ <= tMaxZ; tZ++) {
                int tBits = 0;
                if (tX == tOutX && tY == tOutY && tZ == tOutZ) {
                    tBits = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;
                } else {
                    if (SIDES_AXIS_X[mTE.mFacing] && tX == mTE.xCoord || SIDES_AXIS_Y[mTE.mFacing] && tY == mTE.yCoord || SIDES_AXIS_Z[mTE.mFacing] && tZ == mTE.zCoord) {
                        tBits = (tY == tMinY ? MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID : MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN);
                    } else {
                        tBits = MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT;
                    }
                }
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, tY, tZ, 18009, mTE.getMultiTileEntityRegistryID(), tX == tOutX && tY == tOutY && tZ == tOutZ ? 2 : 0, tBits)) tSuccess = F;
            }
        
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    static {
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.heatmixer.1", "3x3x4 of Steel Walls");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.heatmixer.2", "Main centered on the 3x3 facing outwards");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.heatmixer.3", "Input only possible at frontal 3x3");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.heatmixer.4", "Energy input at back center");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.heatmixer.5", "Output possible at all Steel Walls except frontal 3x3");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.heatmixer.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.heatmixer.2"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.heatmixer.3"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.heatmixer.4"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.heatmixer.5"));
    }
    
    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return
        aX >= mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?3:1) &&
        aY >= mTE.yCoord-(SIDE_Y_NEG==mTE.mFacing?0:SIDE_Y_POS==mTE.mFacing?3:1) &&
        aZ >= mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?3:1) &&
        aX <= mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?3:1) &&
        aY <= mTE.yCoord+(SIDE_Y_POS==mTE.mFacing?0:SIDE_Y_NEG==mTE.mFacing?3:1) &&
        aZ <= mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?3:1);
    }
    
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
    
}
