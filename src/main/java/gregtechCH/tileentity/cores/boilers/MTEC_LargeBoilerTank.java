package gregtechCH.tileentity.cores.boilers;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.network.INetworkHandler;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.data.LH_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.CS.F;

/**
 * @author Gregorius Techneticies, CHanzy
 * 为避免多继承，原本大锅炉的部分逻辑被抛弃
 */
public class MTEC_LargeBoilerTank extends MTEC_BoilerTank {
    public MTEC_LargeBoilerTank(TileEntityBase01Root aTE) {super(aTE); UT_CH.Debug.assertWhenDebug(aTE instanceof IMTEC_HasLargeBoilerTank);}
    @Override protected IMTEC_HasLargeBoilerTank te() {return (IMTEC_HasLargeBoilerTank)mTE;} // 返回值可以为器子类，返回值重写
    
    /* main code */
    @Override
    public void writeItemNBT(NBTTagCompound aNBT) {/**/} // 大锅炉不保留钙化
    
    static {
        LH.add("gt.tooltip.multiblock.largeboiler.1", "3x3 Base of Heat Transmitters");
        LH.add("gt.tooltip.multiblock.largeboiler.2", "3x3x3 Hollow of the Block you crafted this one with");
        LH.add("gt.tooltip.multiblock.largeboiler.3", "Main centered on Side-Bottom of Boiler facing outwards");
        LH.add("gt.tooltip.multiblock.largeboiler.4", "Input only possible at Bottom Layer of Boiler");
    }
    @Override public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.largeboiler.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.largeboiler.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.largeboiler.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.largeboiler.4"));
    }
    @Override
    public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(mEfficiencyCH));
        if (mEfficiency < 10000) aList.add(LH.Chat.YELLOW + LH_CH.get("gt.tooltip.boiler.calcification") + LH.percent(10000 - mEfficiency) + "%");
        aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_INPUT)           + ": " + LH.Chat.WHITE + mInput       + " - " + (mInput*2)   + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.get(LH_CH.FACE_HEAT_TRANS) + ")");
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT)          + ": " + LH.Chat.WHITE + realOutput() + " - " + (mOutput*2)  + " " + TD.Energy.STEAM.getLocalisedChatNameLong()      + LH.Chat.WHITE + "/t (" + LH_CH.get(LH_CH.FACE_PIPE_HOLE)  + ")");
    }
    @Override public void toolTipsImportant(List<String> aList) {
        aList.add(LH.Chat.ORANGE   + LH.get(LH.REQUIREMENT_WATER_PURE));
    }
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_DECALCIFY_CHISEL));
    }
    
    // 释放蒸汽使用 Large 的释放
    @Override
    protected void doEmitSteam2(long aAmount) {
        FluidStack tDrainableSteam = mTanks[1].drain(UT.Code.bindInt(aAmount), F);
        
        if (tDrainableSteam != null) {
            int tTargets = 0;
            
            @SuppressWarnings("unchecked")
            DelegatorTileEntity<TileEntity>[] tDelegators = new DelegatorTileEntity[] {
                  WD.te(mTE.getWorldObj(), mTE.getOffsetXN(te().facing(), 1)  , mTE.yCoord+3, mTE.getOffsetZN(te().facing(), 1)  , SIDE_Y_NEG, F)
                , WD.te(mTE.getWorldObj(), mTE.getOffsetXN(te().facing(), 1)-2, mTE.yCoord+1, mTE.getOffsetZN(te().facing(), 1)  , SIDE_X_POS, F)
                , WD.te(mTE.getWorldObj(), mTE.getOffsetXN(te().facing(), 1)+2, mTE.yCoord+1, mTE.getOffsetZN(te().facing(), 1)  , SIDE_X_NEG, F)
                , WD.te(mTE.getWorldObj(), mTE.getOffsetXN(te().facing(), 1)  , mTE.yCoord+1, mTE.getOffsetZN(te().facing(), 1)-2, SIDE_Z_POS, F)
                , WD.te(mTE.getWorldObj(), mTE.getOffsetXN(te().facing(), 1)  , mTE.yCoord+1, mTE.getOffsetZN(te().facing(), 1)+2, SIDE_Z_NEG, F)
            };
            
            long[] tTargetAmounts = new long[tDelegators.length];
            
            for (int i = 0; i < tDelegators.length; i++) if (tDelegators[i].mTileEntity instanceof IFluidHandler && (tTargetAmounts[i] = FL.fill_(tDelegators[i], tDrainableSteam, F)) > 0) tTargets++; else tDelegators[i] = null;
            
            if (tTargets == 1) {
                for (DelegatorTileEntity<TileEntity> tDelegator : tDelegators) if (tDelegator != null) {
                    FL.move_(mTanks[1], tDelegator, tDrainableSteam.amount);
                    break;
                }
            } else if (tTargets > 1 && tDrainableSteam.amount >= tTargets) {
                if (UT.Code.sum(tTargetAmounts) > tDrainableSteam.amount) {
                    int tMoveable = tDrainableSteam.amount, tOriginalTargets = tTargets;
                    for (int i = 0; i < tDelegators.length; i++) if (tDelegators[i] != null) {
                        if (tTargetAmounts[i] <= tDrainableSteam.amount / tOriginalTargets) {
                            tMoveable -= FL.move_(mTanks[1], tDelegators[i], tDrainableSteam.amount / tOriginalTargets);
                            tDelegators[i] = null;
                            if (--tTargets < 2) break;
                        }
                    }
                    if (tTargets == 1) {
                        for (DelegatorTileEntity<TileEntity> tDelegator : tDelegators) if (tDelegator != null) {
                            FL.move_(mTanks[1], tDelegator, tMoveable);
                            break;
                        }
                    } else if (tTargets > 1 && tMoveable >= tTargets) {
                        for (DelegatorTileEntity<TileEntity> tDelegator : tDelegators) if (tDelegator != null) {
                            tMoveable -= FL.move_(mTanks[1], tDelegator, tMoveable / tTargets);
                            if (--tTargets < 1) break;
                        }
                    }
                } else {
                    for (int i = 0; i < tDelegators.length; i++) if (tDelegators[i] != null) FL.move_(mTanks[1], tDelegators[i], tTargetAmounts[i]);
                }
            }
        }
    }
    
    @Override
    protected boolean checkExplode() {
        return (mBarometer > 4 && !te().checkStructureOnly(F)) || super.checkExplode();
    }
    
    // 大型的在 super 中已经检测了放大镜操作
    @Override protected boolean notSuperMagnifyingGlass() {return F;}
    
    // tanks
    @Override public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return FL.water(aFluidToFill) ? mTanks[0] : null;}
    
    // 大锅炉不需要
    @Override public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {/**/}
    @Override public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {return T;}
}
