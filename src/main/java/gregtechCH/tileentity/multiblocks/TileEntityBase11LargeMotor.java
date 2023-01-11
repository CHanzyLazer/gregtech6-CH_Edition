package gregtechCH.tileentity.multiblocks;

import gregapi.code.TagData;
import gregapi.render.ITexture;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.tileentity.machines.ITileEntitySwitchableOnOff;
import gregapi.tileentity.multiblocks.IMultiBlockEnergy;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregtechCH.tileentity.cores.IMTEC_ToolTips;
import gregtechCH.tileentity.cores.motors.IMTEC_MotorTick;
import gregtechCH.tileentity.cores.motors.MTEC_LargeMotor;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author CHanzy
 * base class of Large Motor, using method similar to the base Motor
 */
public abstract class TileEntityBase11LargeMotor extends TileEntityBase10MultiBlockBase implements ITileEntityEnergy, ITileEntityRunningActively, IMultiBlockEnergy, ITileEntitySwitchableOnOff {
    protected MTEC_LargeMotor mCore;

    // NBT读写
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        // GTCH, core init
        if (mCore == null) mCore = getNewCoreLargeMotor();
        mCore.init(aNBT);

        super.readFromNBT2(aNBT);

        // GTCH, core init
        mCore.postInit(aNBT);
    }
    private MTEC_LargeMotor getNewCoreLargeMotor() {MTEC_LargeMotor tCore = getNewCoreLargeMotor2(); tCore.preInit(); return tCore;}
    protected abstract MTEC_LargeMotor getNewCoreLargeMotor2();

    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);

        // GTCH, core save
        mCore.writeToNBT(aNBT);
    }

    // 多方快结构
    @Override public final boolean checkStructure2() {return mCore.checkStructure2();}
    @Override public boolean isInsideStructure(int aX, int aY, int aZ) {return mCore.isInsideStructure(aX, aY, aZ);}
    @Override public int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {return isStructureOkay() ? 2 : 1;}
    @Override public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
        if (aRenderPass == 1) switch(mFacing) {
            case SIDE_X_NEG: case SIDE_X_POS: return box(aBlock, -0.001, -0.999, -0.999,  1.001,  1.999,  1.999);
            case SIDE_Y_NEG: case SIDE_Y_POS: return box(aBlock, -0.999, -0.001, -0.999,  1.999,  1.001,  1.999);
            case SIDE_Z_NEG: case SIDE_Z_POS: return box(aBlock, -0.999, -0.999, -0.001,  1.999,  1.999,  1.001);
        }
        return F;
    }

    // tooltips
    @Override public final void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {IMTEC_ToolTips.Util.addToolTips(mCore, aList, aStack, aF3_H); super.addToolTips(aList, aStack, aF3_H);}

    // 工具右键
    @Override
    public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (rReturn > 0) return rReturn;

        if (isClientSide()) return 0;

        return mCore.onToolClick(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
    }
    @Override public void onMagnifyingGlassSuccess(List<String> aChatReturn, boolean aOldStructureOkay) {
        super.onMagnifyingGlassSuccess(aChatReturn, aOldStructureOkay);
        mCore.onMagnifyingGlass2(aChatReturn);
    }

    // 每 tick 转换
    @Override
    public final void onTick3(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide) {
            if (isStructureOkay()) {
                IMTEC_MotorTick.Util.onTick(mCore, aTimer);
            } else {
                // 结构破坏，停止机器
                mCore.stop();
            }
        }
    }

    // 一些接口
    @Override public void explode(boolean aInstant) {mCore.explode(aInstant);}
    @Override public boolean breakBlock() {mCore.onBreakBlock(); return super.breakBlock();}
    @Override public void onFacingChange(byte aPreviousFacing) {mCore.onFacingChange(aPreviousFacing); super.onFacingChange(aPreviousFacing);}

    @Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return mCore.getFluidTankFillable(aSide, aFluidToFill);}
    @Override protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {return mCore.getFluidTankDrainable(aSide, aFluidToDrain);}
    @Override protected IFluidTank[] getFluidTanks2(byte aSide) {
        return mCore.getFluidTanks(aSide);
    }
    @Override public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return mCore.getDefaultInventory(aNBT);}
    @Override public boolean canDrop(int aInventorySlot) {return mCore.canDrop(aInventorySlot);}

    public boolean canFillExtra(FluidStack aFluid) {return mCore.canFillExtra(aFluid);}
    @Override public boolean allowCovers(byte aSide) {return mCore.allowCovers(aSide);}

    @Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return mCore.isEnergyType(aEnergyType, aSide, aEmitting);}
    @Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return mCore.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical) && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
    @Override public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {return mCore.isEnergyEmittingTo(aEnergyType, aSide, aTheoretical) && super.isEnergyEmittingTo(aEnergyType, aSide, aTheoretical);}
    @Override public long getEnergyOffered(TagData aEnergyType, byte aSide, long aSize) {return mCore.getEnergyOffered(aEnergyType, aSide, aSize);}
    @Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeOutputRecommended(aEnergyType, aSide);}
    @Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeOutputMin(aEnergyType, aSide);}
    @Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeOutputMax(aEnergyType, aSide);}
    @Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeInputRecommended(aEnergyType, aSide);}
    @Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeInputMin(aEnergyType, aSide);}
    @Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeInputMax(aEnergyType, aSide);}
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return mCore.getEnergyTypes(aSide);}

    @Override public boolean getStateRunningPossible() {return mCore.getStateRunningPossible();}
    @Override public boolean getStateRunningPassively() {return mCore.getStateRunningPassively();}
    @Override public boolean getStateRunningActively() {return mCore.getStateRunningActively();}
    public boolean setStateOnOff(boolean aOnOff) {return mCore.setStateOnOff(aOnOff);}
    public boolean getStateOnOff() {return mCore.getStateOnOff();}

    // Icons，图像动画
    @Override public boolean onTickCheck(long aTimer) {return mCore.onTickCheck(aTimer) || super.onTickCheck(aTimer);}
    @Override
    public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        super.onTickResetChecks(aTimer, aIsServerSide);
        mCore.onTickResetChecks(aTimer, aIsServerSide);
    }
    @Override public void setVisualData(byte aData) {mCore.setVisualData(aData);}
    @Override public byte getVisualData() {return mCore.getVisualData();}

    @Override public byte getDefaultSide() {return mCore.getDefaultSide();}
    @Override public boolean[] getValidSides() {return mCore.getValidSides();}

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        return mCore.getTexture(aBlock, aRenderPass, aSide, aShouldSideBeRendered);
    }
}

