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

public class MTEC_MultiBlockDistillationTower extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockDistillationTower(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing);
        if (mTE.getWorldObj().blockExists(tX-1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX-1, tY, tZ+1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = T;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY-1, tZ-1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ-1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ-1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ  , 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY-1, tZ+1, 18101, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY  , tZ-1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ-1, 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ-1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY  , tZ  , 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ  , 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ  , 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY  , tZ+1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+1, 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;

            for (int i = 1; i < 8; i++) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY+i, tZ-1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+i, tZ-1, 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+i, tZ-1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY+i, tZ  , 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+i, tZ  , 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+i, tZ  , 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_X_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-1, tY+i, tZ+1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+i, tZ+1, 18102, mTE.getMultiTileEntityRegistryID(),  mTE.mFacing == SIDE_Z_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+i, tZ+1, 18102, mTE.getMultiTileEntityRegistryID(),                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
            }

            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.distillationtower.1", "3x3 Base of Heat Transmitters");
        LH.add("gt.tooltip.multiblock.distillationtower.2", "3x3x8 of Distillation Tower Parts");
        LH.add("gt.tooltip.multiblock.distillationtower.3", "Main centered on Side-Bottom of Tower facing outwards");
        LH.add("gt.tooltip.multiblock.distillationtower.4", "Outputs automatically to the Holes on the Backside");
        LH.add("gt.tooltip.multiblock.distillationtower.5", "Bottom Hole is for outputting all Items");
        LH.add("gt.tooltip.multiblock.distillationtower.6", "Input only possible at Bottom Layer of Tower");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.5"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.6"));
    }
    
    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing);
        return aX >= tX - 1 && aY >= tY - 1 && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 7 && aZ <= tZ + 1;
    }
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.distillationtower";}
}
