package gregtechCH.tileentity.multiblocks;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.ST;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockCrackingTower;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

import static gregapi.data.CS.*;

/**
* stuff from GT6U
**/
public class MultiTileEntityCrackingTower extends TileEntityBase10MultiBlockMachine implements IDistillationTower {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockCrackingTower(this);}
    
    @Override
    public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {
        String tSideNames = ""; boolean temp = F;
        if (mEnergyTypeAccepted != TD.Energy.TU) {
            for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mEnergyInputs])    {tSideNames += (temp?", ":"")+LH.get(LH.FACES[tSide]); temp = T;}
            LH.addEnergyToolTips(this, aList, mEnergyTypeAccepted, null, tSideNames, null);
        }
    }
    
    @Override
    public void updateAdjacentToggleableEnergySources() {
        int tX = getOffsetXN(mFacing) - 1, tZ = getOffsetZN(mFacing) - 1;
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            DelegatorTileEntity<TileEntity> tDelegator = WD.te(worldObj, tX+i, yCoord-2, tZ+j, SIDE_TOP, F);
            if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
                ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
            }
        }
    }
    
    @Override
    public void doOutputItems() {
        ST.moveAll(delegator(FACING_TO_SIDE[mFacing][mItemAutoOutput]), WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord, getOffsetZN(mFacing, 3), mFacing, F));
    }
    
    // TODO 改成 greg 更新后的形式，固定每种流体的输出口
    @Override
    public void doOutputFluids() {
        for (int i=0;i<mTanksOutput.length;i++) {
            IFluidTank tTank = mTanksOutput[i];
            FluidStack tFluid = tTank.getFluid();
            if (tFluid != null && tFluid.amount > 0) {
                DelegatorTileEntity<TileEntity> tDelegator = null;
                tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+i+1, getOffsetZN(mFacing, 3), mFacing, F);
                
                if (FL.move(tTank, tDelegator) > 0) updateInventory();
            }
        }
    }
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.crackingtower";}
}
