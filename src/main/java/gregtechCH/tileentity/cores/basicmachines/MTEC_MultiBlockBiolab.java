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
 * stuff from GT6U
 **/
public class MTEC_MultiBlockBiolab extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockBiolab(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing)-1, tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing)-1;
        boolean isNorth = mTE.mFacing == SIDE_NORTH;
        boolean isSouth = mTE.mFacing == SIDE_SOUTH;
        boolean isEast = mTE.mFacing == SIDE_EAST;
        boolean isWest = mTE.mFacing == SIDE_WEST;
        
        boolean isNS = isNorth || isSouth;
        boolean isEW = isEast || isWest;
        
        if (mTE.getWorldObj().blockExists(tX-1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX-1, tY, tZ+1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = T;
            
            //Center
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+1  , tY  , isEW?tZ+1:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 2, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX+1, tY  , isEW?tZ+1:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            //Left half
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX    , tY  , isEW?tZ:tZ    , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX-1  , tY  , isEW?tZ-1:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX-2  , tY  , isEW?tZ-2:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX  , tY  , isEW?tZ:tZ+1  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX-1, tY  , isEW?tZ-1:tZ+1, 18040, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX-2, tY  , isEW?tZ-2:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX  , tY  , isEW?tZ:tZ+2  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX-1, tY  , isEW?tZ-1:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX-2, tY  , isEW?tZ-2:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX    , tY+1, isEW?tZ:tZ    , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX-1  , tY+1, isEW?tZ-1:tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX-2  , tY+1, isEW?tZ-2:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX  , tY+1, isEW?tZ:tZ+1  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            
            if (mTE.getAir(tX-1, tY+1, tZ+1)) mTE.getWorldObj().setBlockToAir(isEW?tX+1:tX-1, tY+1   , isEW?tZ-1:tZ+1); else tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX-2, tY+1, isEW?tZ-2:tZ+1, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX  , tY+1, isEW?tZ:tZ+2  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX-1, tY+1, isEW?tZ-1:tZ+2, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX-2, tY+1, isEW?tZ-2:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX    , tY+2, isEW?tZ:tZ    , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX-1  , tY+2, isEW?tZ-1:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX-2  , tY+2, isEW?tZ-2:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX  , tY+2, isEW?tZ:tZ+1  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX-1, tY+2, isEW?tZ-1:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 2, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX-2, tY+2, isEW?tZ-2:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX  , tY+2, isEW?tZ:tZ+2  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX-1, tY+2, isEW?tZ-1:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX-2, tY+2, isEW?tZ-2:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            
            if(isNS) tX += 2;
            if(isEW) tZ += 2;
            
            //Right half
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX    , tY  , isEW?tZ:tZ    , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+1  , tY  , isEW?tZ+1:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+2  , tY  , isEW?tZ+2:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX  , tY  , isEW?tZ:tZ+1  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX+1, tY  , isEW?tZ+1:tZ+1, 18040, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX+2, tY  , isEW?tZ+2:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX  , tY  , isEW?tZ:tZ+2  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX+1, tY  , isEW?tZ+1:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX+2, tY  , isEW?tZ+2:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX    , tY+1, isEW?tZ:tZ    , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+1  , tY+1, isEW?tZ+1:tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+2  , tY+1, isEW?tZ+2:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX  , tY+1, isEW?tZ:tZ+1  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            
            if (mTE.getAir(tX-1, tY+1, tZ+1)) mTE.getWorldObj().setBlockToAir(isEW?tX+1:tX+1, tY+1   , isEW?tZ+1:tZ+1); else tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX+2, tY+1, isEW?tZ+2:tZ+1, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX  , tY+1, isEW?tZ:tZ+2  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX+1, tY+1, isEW?tZ+1:tZ+2, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX+2, tY+1, isEW?tZ+2:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX    , tY+2, isEW?tZ:tZ    , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+1  , tY+2, isEW?tZ+1:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX:tX+2  , tY+2, isEW?tZ+2:tZ  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX  , tY+2, isEW?tZ:tZ+1  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX+1, tY+2, isEW?tZ+1:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 2, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+1:tX+2, tY+2, isEW?tZ+2:tZ+1, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX  , tY+2, isEW?tZ:tZ+2  , 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX+1, tY+2, isEW?tZ+1:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, isEW?tX+2:tX+2, tY+2, isEW?tZ+2:tZ+2, 18119, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            
            
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    static {
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.1", "The Biolab consists two exactly same modules and a connection part");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.2", "Each module is 3x3x3 Hollow Sterile Machine Casing with its bottom centers a Large Copper Coil");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.3", "On the Side-centers locate 4 Ventilation Units");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.4", "Place the two modules on same level with one block space between them");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.5", "Main at Side-Bottom of the spacing right between the two modules facing outwards");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.6", "Fill the bottom layer of the spacing with 2 Sterile Machine Casing");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.7", "Input and Output Items and Fluids at any Sterile Machin Casing");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.8", "Energy input at the top centers of the two modules");
       LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.biolab.9", "Requires LU to disinfect before start");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.1"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.2"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.3"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.4"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.5"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.6"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.7"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.8"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.biolab.9"));
    }
    
    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing, 1), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 1);
        return aX >= tX - 1 && aY >= tY && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 2 && aZ <= tZ + 1;
    }
    
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
}
