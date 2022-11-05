package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.data.LH_CH;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.RegType;

/**
 * stuff from GT6U
 **/
public class MTEC_MultiBlockAquaticFarm extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockAquaticFarm(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    /* main code */
    @Override
    public boolean checkStructure2() {
        int
        tMinX = mTE.xCoord-(SIDE_X_NEG==mTE.mFacing?0:SIDE_X_POS==mTE.mFacing?6:2),
        tMinZ = mTE.zCoord-(SIDE_Z_NEG==mTE.mFacing?0:SIDE_Z_POS==mTE.mFacing?6:2),
        tMaxX = mTE.xCoord+(SIDE_X_POS==mTE.mFacing?0:SIDE_X_NEG==mTE.mFacing?6:2),
        tMaxZ = mTE.zCoord+(SIDE_Z_POS==mTE.mFacing?0:SIDE_Z_NEG==mTE.mFacing?6:2);
        int
        tD = mTE.mActive?2:mTE.mRunning?1:0;
    
        if (mTE.getWorldObj().blockExists(tMinX, mTE.yCoord, tMinZ) && mTE.getWorldObj().blockExists(tMaxX, mTE.yCoord+2, tMaxZ)) {
            boolean tSuccess = T;
            for (int tX = tMinX; tX <= tMaxX; tX++) for (int tZ = tMinZ; tZ <= tMaxZ; tZ++) {
            
                // Conditions
                boolean isSides = tX == tMinX || tX == tMaxX || tZ == tMinZ || tZ == tMaxZ;
                boolean isCorners = tX == tMinX && tZ == tMinZ || tX == tMinX && tZ == tMaxZ || tX == tMaxX && tZ == tMinZ || tX == tMaxX && tZ == tMaxZ;
                boolean isNotSides = tX != tMinX && tX != tMaxX && tZ != tMinZ && tZ != tMaxZ;
            
                // Layer 1
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord, tZ, isSides ? 18002 : 18298, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY)) tSuccess = F;
            
                // Layer 2
                if (isNotSides) {
                    if (getWater(tX, mTE.yCoord + 1, tZ)) mTE.getWorldObj().setBlock(tX, mTE.yCoord + 1, tZ, Blocks.water, 0, 3); else tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord + 1, tZ, isCorners ? 18002 : 18298, mTE.getMultiTileEntityRegistryID(), isCorners ? 0 : tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
            
                // Layer 3
                if (isNotSides) {
                    if (getWater(tX, mTE.yCoord + 2, tZ)) mTE.getWorldObj().setBlock(tX, mTE.yCoord + 2, tZ, Blocks.water, 0, 3); else tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX, mTE.yCoord + 2, tZ, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
            
            }
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    private boolean getWater(int aX, int aY, int aZ) {
        if (mTE.getWorldObj() == null) return T;
        if (mTE.mIgnoreUnloadedChunks && mTE.crossedChunkBorder(aX, aZ) && !mTE.getWorldObj().blockExists(aX, aY, aZ)) return T;
        return mTE.getWorldObj().getBlock(aX, aY, aZ).getMaterial() == Material.water;
    }
    
    
    static {
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.1", "5(Width)x3(Height)x7(Length) Hollow Cuboid with top side opened");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.2", "Main Block centered on 5x3-Side-Bottom and facing outwards");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.3", "The Edges of the Cuboid are Stainless Steel Walls");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.4", "The Faces of the Cuboid are Aquatic Farm Blocks");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.5", "Fill the Hollow space with Still Water");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.6", "Stuff can go in and out at middle and top layer");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.aquaticfarm.7", "Energy input at the bottom layer");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.1"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.2"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.3"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.4"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.5"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.6"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.aquaticfarm.7"));
    }
    
    
    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return
        aX >= mTE.xCoord - (SIDE_X_NEG == mTE.mFacing ? 0 : SIDE_X_POS == mTE.mFacing ? 6 : 2) &&
        aY >= mTE.yCoord &&
        aZ >= mTE.zCoord - (SIDE_Z_NEG == mTE.mFacing ? 0 : SIDE_Z_POS == mTE.mFacing ? 6 : 2) &&
        aX <= mTE.xCoord + (SIDE_X_POS == mTE.mFacing ? 0 : SIDE_X_NEG == mTE.mFacing ? 6 : 2) &&
        aY <= mTE.yCoord + 2 &&
        aZ <= mTE.zCoord + (SIDE_Z_POS == mTE.mFacing ? 0 : SIDE_Z_NEG == mTE.mFacing ? 6 : 2);
    }
    
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
}
