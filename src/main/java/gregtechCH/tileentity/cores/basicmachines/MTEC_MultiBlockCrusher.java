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
public class MTEC_MultiBlockCrusher extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockCrusher(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2)-2, tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2)-2, tD = (SIDES_AXIS_Z[mTE.mFacing]?mTE.mRunning?1:0:mTE.mRunning?3:2);
        if (mTE.getWorldObj().blockExists(tX, tY, tZ) && mTE.getWorldObj().blockExists(tX+4, tY, tZ) && mTE.getWorldObj().blockExists(tX, tY, tZ+4) && mTE.getWorldObj().blockExists(tX+4, tY, tZ+4)) {
            boolean tSuccess = T;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), SIDES_AXIS_Z[mTE.mFacing]?0:3, SIDES_AXIS_Z[mTE.mFacing]?MultiTileEntityMultiBlockPart.NOTHING:MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+1, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+1, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+1, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), SIDES_AXIS_X[mTE.mFacing]?0:3, SIDES_AXIS_X[mTE.mFacing]?MultiTileEntityMultiBlockPart.NOTHING:MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+2, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+2, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+2, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), SIDES_AXIS_X[mTE.mFacing]?0:3, SIDES_AXIS_X[mTE.mFacing]?MultiTileEntityMultiBlockPart.NOTHING:MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+3, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+3, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+3, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), SIDES_AXIS_Z[mTE.mFacing]?0:3, SIDES_AXIS_Z[mTE.mFacing]?MultiTileEntityMultiBlockPart.NOTHING:MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ  , 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+1, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+1, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+1, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+1, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+2, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+2, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+2, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+2, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+3, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+3, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+3, 18107, mTE.getMultiTileEntityRegistryID(),tD, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+3, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+4, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.crusher.1", "5x5x3 'Basin' of 56 Tungstensteel Walls");
        LH.add("gt.tooltip.multiblock.crusher.2", "3x3x2 Filling with Crusher Wheels");
        LH.add("gt.tooltip.multiblock.crusher.3", "Main Block centered on Side-Bottom and facing outwards");
        LH.add("gt.tooltip.multiblock.crusher.4", "Input only at Crusher Wheels, Output at Bottom Layer");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crusher.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crusher.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crusher.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crusher.4"));
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        return aX >= tX - 2 && aY >= tY && aZ >= tZ - 2 && aX <= tX + 2 && aY <= tY + 2 && aZ <= tZ + 2;
    }
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.crusher";}
}
