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
public class MTEC_MultiBlockMatterFabricator extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockMatterFabricator(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2)-2, tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2)-2;
        if (mTE.getWorldObj().blockExists(tX, tY, tZ) && mTE.getWorldObj().blockExists(tX+4, tY, tZ) && mTE.getWorldObj().blockExists(tX, tY, tZ+4) && mTE.getWorldObj().blockExists(tX+4, tY, tZ+4)) {
            boolean tSuccess = T;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY  , tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY  , tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY  , tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY  , tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY  , tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+1, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+1, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+1, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+1, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+1, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (mTE.getAir(tX+2, tY+2, tZ+2)) mTE.getWorldObj().setBlockToAir(tX+2, tY+2, tZ+2); else tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+2, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+2, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+2, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+2, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+2, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+3, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+3, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+3, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+3, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+3, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+3, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+3, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+3, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+3, tZ+1, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+3, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+3, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+3, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+3, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+3, tZ+2, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+3, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+3, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+3, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+3, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+3, tZ+3, 18044, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+3, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+3, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+3, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+3, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+3, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+3, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+4, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+4, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+4, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+4, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+4, tZ  , 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+4, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+4, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+4, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+4, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+4, tZ+1, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+4, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+4, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+4, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+4, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+4, tZ+2, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+4, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+4, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+4, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+4, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+4, tZ+3, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+4, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+4, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+4, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+4, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+4, tZ+4, 18031, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+5, tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+5, tZ  , 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+5, tZ+1, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+5, tZ+1, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+5, tZ+2, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+5, tZ+2, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+5, tZ+3, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+5, tZ+3, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY+5, tZ+4, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+4, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ+4, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+4, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY+5, tZ+4, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ+2, 18200, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

            int tCountA = 0, tCountB = 0;

            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+1, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ+1, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+1, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+2, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+2, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+3, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ+3, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+3, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountA++;

            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+1, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ+1, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+1, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+2, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+2, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+1, tY+5, tZ+3, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+2, tY+5, tZ+3, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;
            if ( ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY+5, tZ+3, 18204, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tCountB++;

            if (tCountA < 4 || tCountB < 4) tSuccess = F;

            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.matterfabricator.1", "5x5x5 'Hollow' of 97 Dense Lead Walls");
        LH.add("gt.tooltip.multiblock.matterfabricator.2", "3x3x3 'Hollow' with 26 Osmium Coils inside the 5x5x5");
        LH.add("gt.tooltip.multiblock.matterfabricator.3", "5x5 Ring of 16 Ventilation Units ontop");
        LH.add("gt.tooltip.multiblock.matterfabricator.4", "4 Control Quadcore Processor Units in that Ring");
        LH.add("gt.tooltip.multiblock.matterfabricator.5", "4 Conversion Quadcore Processor Units in that Ring");
        LH.add("gt.tooltip.multiblock.matterfabricator.6", "1 Versatile Quadcore Processor Unit in the center of that Ring");
        LH.add("gt.tooltip.multiblock.matterfabricator.7", "Main Block centered on Side-Bottom and facing outwards");
        LH.add("gt.tooltip.multiblock.matterfabricator.8", "Stuff can go in and out on any of the Dense Lead Walls");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.1"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.2"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.3"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.4"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.5"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.6"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.7"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.matterfabricator.8"));
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        return aX >= tX - 2 && aY >= tY && aZ >= tZ - 2 && aX <= tX + 2 && aY <= tY + 6 && aZ <= tZ + 2;
    }
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mTE.getAdjacentTank(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mTE.getAdjacentTileEntity(SIDE_BOTTOM);}
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.matterfabricator";}
}
