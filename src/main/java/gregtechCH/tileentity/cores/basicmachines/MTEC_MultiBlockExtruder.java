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
public class MTEC_MultiBlockExtruder extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockExtruder(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    /* main code */
    public short mDryerWalls = 18035;
    
    @Override
    public boolean checkStructure2() {
        int
        tMinX = mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?3:1),
        tMinY = mTE.yCoord-(SIDE_Y_NEG==mTE.mFacing?0:SIDE_Y_POS==mTE.mFacing?3:1) + 1,
        tMinZ = mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?3:1),
        tMaxX = mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?3:1),
        tMaxY = mTE.yCoord+(SIDE_Y_POS==mTE.mFacing?0:SIDE_Y_NEG==mTE.mFacing?3:1) + 1,
        tMaxZ = mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?3:1);
        
        if (mTE.getWorldObj().blockExists(tMinX, tMinY, tMinZ) && mTE.getWorldObj().blockExists(tMaxX, tMaxY, tMaxZ)) {
            boolean tSuccess = T;
            for (int tX = tMinX; tX <= tMaxX; tX++) for (int tY = tMinY; tY <= tMaxY; tY++) for (int tZ = tMinZ; tZ <= tMaxZ; tZ++) if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, tY, tZ, (SIDES_AXIS_X[mTE.mFacing]?tX!=tMinX&&tX!=tMaxX:SIDES_AXIS_Z[mTE.mFacing]?tZ!=tMinZ&&tZ!=tMaxZ:tY!=tMinY&&tY!=tMaxY) ? 18043 : mDryerWalls, mTE.getMultiTileEntityRegistryID(),0, (SIDES_AXIS_X[mTE.mFacing]?tX!=tMinX&&tX!=tMaxX:SIDES_AXIS_Z[mTE.mFacing]?tZ!=tMinZ&&tZ!=tMaxZ:tY!=tMinY&&tY!=tMaxY) ? MultiTileEntityMultiBlockPart.NOTHING : MultiTileEntityMultiBlockPart.EVERYTHING)) tSuccess = F;
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    static {
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.extruder.1", "Two 3x3s with 2m inbetween made of the Block you crafted this of");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.extruder.2", "a 3x3x2 of 18 Large Carborundum Coils inbetween");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.extruder.3", "Main centered on Side-Bottom of one of the 3x3s facing outwards");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.extruder.4", "Input and Output at anywhere except the Coils");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.extruder.1"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.extruder.2"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.extruder.3"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.extruder.4"));
    }
    
    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return
        aX >= mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?3:1) &&
        aY >= mTE.yCoord-(SIDE_Y_NEG==mTE.mFacing?0:SIDE_Y_POS==mTE.mFacing?3:1) + 1 &&
        aZ >= mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?3:1) &&
        aX <= mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?3:1) &&
        aY <= mTE.yCoord+(SIDE_Y_POS==mTE.mFacing?0:SIDE_Y_NEG==mTE.mFacing?3:1) + 1 &&
        aZ <= mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?3:1);
    }
    
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
}
