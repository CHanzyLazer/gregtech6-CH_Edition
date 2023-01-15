package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public class MTEC_MultiBlockMixer extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockMixer(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing)-1, tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing)-1;
        if (mTE.getWorldObj().blockExists(tX-1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX-1, tY, tZ+1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = T;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 3, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN     )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ  , 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 3, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN     )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+1, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+2, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN )) tSuccess = F;

            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.mixer.1", "3x3x2 of Stainless Steel Walls");
        LH.add("gt.tooltip.multiblock.mixer.2", "Main Block centered on Side-Bottom and facing outwards");
        LH.add("gt.tooltip.multiblock.mixer.3", "Top Half accepts Input, Bottom Half and Main Block emit Output");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.mixer.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.mixer.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.mixer.3"));
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing);
        return aX >= tX - 1 && aY >= tY && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 1 && aZ <= tZ + 1;
    }
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.mixer";}
}
