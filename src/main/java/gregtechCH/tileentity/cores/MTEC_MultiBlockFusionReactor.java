package gregtechCH.tileentity.cores;

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

public class MTEC_MultiBlockFusionReactor extends MTEC_MultiblockMachine {
    public MTEC_MultiBlockFusionReactor(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override
    public boolean checkStructure2() {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        if (mTE.getWorldObj().blockExists(tX-9, tY, tZ-9) && mTE.getWorldObj().blockExists(tX+9, tY, tZ-9) && mTE.getWorldObj().blockExists(tX-9, tY, tZ+9) && mTE.getWorldObj().blockExists(tX+9, tY, tZ+9)) {
            boolean tSuccess = T;

            int tVersatile = 3, tLogic = 12, tControl = 12;

            for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) for (int k = -2; k <= 2; k++) {
                if (i*i + j*j + k*k < 4) {
                    if (ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18200, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) {
                        tVersatile--;
                    } else if (ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18201, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) {
                        tLogic--;
                    } else if (ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18202, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) {
                        tControl--;
                    } else {
                        tSuccess = F;
                    }
                } else if (i*i + j*j + k*k > 6 || (j == 0 && (((i == -2 || i == 2) && k == 0) || (((k == -2 || k == 2) && i == 0))))) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                } else {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+j, tZ+k, 18299, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                }
            }

            if (tVersatile > 0 || tLogic > 0 || tControl > 0) tSuccess = F;

            if (mTE.mFacing != SIDE_X_NEG) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-3, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX-4, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
            if (mTE.mFacing != SIDE_X_POS) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+3, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+4, tY, tZ  , 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
            if (mTE.mFacing != SIDE_Z_NEG) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ-3, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ-4, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }
            if (mTE.mFacing != SIDE_Z_POS) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ+3, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX  , tY, tZ+4, 18008, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;
            }

            tX -= 9; tZ -= 9;

            for (int i = 0; i < 19; i++) for (int j = 0; j < 19; j++) {
                if (OCTAGONS[0][i][j]) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-1, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                    if ((i == 9 && (j == 0 || j == 18)) || (j == 9 && (i == 0 || i == 18))) {
                        if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 2, MultiTileEntityMultiBlockPart.ONLY_ENERGY_OUT)) tSuccess = F;
                    } else {
                        if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), mTE.mActive ? 6 : 5, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
                    }
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+1, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
                if (OCTAGONS[1][i][j]) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-2, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-1, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, 18045, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+1, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+2, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
                if (OCTAGONS[2][i][j]) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-2, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY-1, tZ+j, 18045, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY  , tZ+j, 18002, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+1, tZ+j, 18045, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.NOTHING)) tSuccess = F;

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(mTE, tX+i, tY+2, tZ+j, 18003, mTE.getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) tSuccess = F;
                }
            }
            return tSuccess;
        }
        return mTE.isStructureOkay();
    }

    public static boolean[][][] OCTAGONS = {{
            {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
            {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
            {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
            {F,F,F,T,F,F,F,T,T,T,T,T,F,F,F,T,F,F,F},
            {F,F,T,F,F,F,T,F,F,F,F,F,T,F,F,F,T,F,F},
            {F,T,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,T,F},
            {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
            {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
            {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
            {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
            {T,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,T},
            {F,T,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,T,F},
            {F,F,T,F,F,F,T,F,F,F,F,F,T,F,F,F,T,F,F},
            {F,F,F,T,F,F,F,T,T,T,T,T,F,F,F,T,F,F,F},
            {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
            {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
            {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
    }, {
            {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
            {F,F,F,F,F,T,F,T,T,T,T,T,F,T,F,F,F,F,F},
            {F,F,F,F,T,F,T,F,F,F,F,F,T,F,T,F,F,F,F},
            {F,F,F,T,F,T,F,F,F,F,F,F,F,T,F,T,F,F,F},
            {F,F,T,F,T,F,F,F,F,F,F,F,F,F,T,F,T,F,F},
            {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
            {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
            {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
            {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
            {F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F},
            {F,F,T,F,T,F,F,F,F,F,F,F,F,F,T,F,T,F,F},
            {F,F,F,T,F,T,F,F,F,F,F,F,F,T,F,T,F,F,F},
            {F,F,F,F,T,F,T,F,F,F,F,F,T,F,T,F,F,F,F},
            {F,F,F,F,F,T,F,T,T,T,T,T,F,T,F,F,F,F,F},
            {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
    }, {
            {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
            {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
            {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
            {F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F},
            {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
            {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
            {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
            {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
            {F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F},
            {F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F},
            {F,F,F,F,T,F,F,F,F,F,F,F,F,F,T,F,F,F,F},
            {F,F,F,F,F,T,F,F,F,F,F,F,F,T,F,F,F,F,F},
            {F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,T,T,T,T,T,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
            {F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F},
    }};

    static {
        LH.add("gt.tooltip.multiblock.fusionreactor.1", "For Assembly Instructions read the Manual in the GUI.");
        LH.add("gt.tooltip.multiblock.fusionreactor.2", "144 Iridium Coils, 576 Regular Tungstensteel Walls, 50 Ventilation Units.");
        LH.add("gt.tooltip.multiblock.fusionreactor.3", "36 Regular Stainless Steel Walls, 53 Galvanized Steel Walls.");
        LH.add("gt.tooltip.multiblock.fusionreactor.4", "3 Versatile, 12 Logic and 12 Control Quadcore Processing Units.");
        LH.add("gt.tooltip.multiblock.fusionreactor.5", "Energy Output at the Electric Interfaces");
        LH.add("gt.tooltip.multiblock.fusionreactor.6", "Laser Input at the 'Glass' Ring");
        LH.add("gt.tooltip.multiblock.fusionreactor.7", "Items and Fluids are handeled at the normal Walls");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.1"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.2"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.3"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.4"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.5"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.6"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.7"));
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = mTE.getOffsetXN(mTE.mFacing, 2), tY = mTE.yCoord-2, tZ = mTE.getOffsetZN(mTE.mFacing, 2);
        return aX >= tX - 9 && aY >= tY && aZ >= tZ - 9 && aX <= tX + 9 && aY <= tY + 5 && aZ <= tZ + 9;
    }
    @Override public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
        return null;
    }
    @Override public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
        return null;
    }
    @Override public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return null;}
    @Override public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return null;}

    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.fusionreactor";}
}
