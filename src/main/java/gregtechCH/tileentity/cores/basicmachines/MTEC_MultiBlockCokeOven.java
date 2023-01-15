package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
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

/**
 * @author Gregorius Techneticies, CHanzy
 */
public class MTEC_MultiBlockCokeOven extends MTEC_MultiBlockMachine {
    public MTEC_MultiBlockCokeOven(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.getOffsetYN(mTE.mFacing), tZ = mTE.getOffsetZN(mTE.mFacing);
        if (mTE.getWorldObj().blockExists(tX-1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ-1) && mTE.getWorldObj().blockExists(tX-1, tY, tZ+1) && mTE.getWorldObj().blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = T;
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j == 0 && k == 0) {
                    if (mTE.getAir(tX+i, tY+j, tZ+k)) mTE.getWorldObj().setBlockToAir(tX+i, tY+j, tZ+k); else tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18000, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_ENERGY)) tSuccess = F;
                }
            }
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    static {
        LH.add("gt.tooltip.multiblock.cokeoven.1", "3x3x3 Hollow of 25 Fire Bricks filled with Air");
        LH.add("gt.tooltip.multiblock.cokeoven.2", "Main Block centered on Side and facing outwards");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cokeoven.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cokeoven.2"));
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.getOffsetYN(mTE.mFacing), tZ = mTE.getOffsetZN(mTE.mFacing);
        return aX >= tX - 1 && aY >= tY - 1 && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 1 && aZ <= tZ + 1;
    }

    public DelegatorTileEntity<IFluidHandler> mFluidOutputTarget = null;
    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
        if (mFluidOutputTarget != null && mFluidOutputTarget.exists()) return mFluidOutputTarget;
        if (aOutput != null) {
            int tX = mTE.getOffsetXN(mTE.mFacing), tY = mTE.getOffsetYN(mTE.mFacing)-2, tZ = mTE.getOffsetZN(mTE.mFacing);
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
                DelegatorTileEntity<TileEntity> tTarget = WD.te(mTE.getWorldObj(), tX+i, tY, tZ+j, SIDE_TOP, F);
                if (tTarget.mTileEntity instanceof IFluidHandler && ((IFluidHandler)tTarget.mTileEntity).canFill(tTarget.getForgeSideOfTileEntity(), aOutput)) {
                    return mFluidOutputTarget = new DelegatorTileEntity<>((IFluidHandler)tTarget.mTileEntity, tTarget);
                }
            }
        }
        return mFluidOutputTarget = null;
    }
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.cokeoven";}
}
