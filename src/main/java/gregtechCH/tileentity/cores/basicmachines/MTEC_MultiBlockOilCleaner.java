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
public class MTEC_MultiBlockOilCleaner extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockOilCleaner(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.yCoord+1, tZ = mTE.getOffsetZN(mTE.mFacing);
        if (mTE.getWorldObj().blockExists(tX-1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX-1, tY, tZ+1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = T;
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j == 0 && k == 0) {
                    if (mTE.getAir(tX+i, tY+j, tZ+k)) mTE.getWorldObj().setBlockToAir(tX+i, tY+j, tZ+k); else tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18114, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
                }
            }
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }
    
    static {
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.oilcleaner.1", "3x3x3 Hollow of 25 Clean StainlessSteel Machine Casing filled with Air");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.oilcleaner.2", "Can input all thing at all wall.");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.oilcleaner.3", "Main Block centered on Side-Bottom and facing outwards");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.oilcleaner.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.oilcleaner.2"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.oilcleaner.3"));
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
