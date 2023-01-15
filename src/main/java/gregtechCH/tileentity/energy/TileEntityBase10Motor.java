package gregtechCH.tileentity.energy;

import gregapi.code.TagData;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregtechCH.tileentity.cores.IMTEC_ToolTips;
import gregtechCH.tileentity.cores.motors.IMTEC_MotorTick;
import gregtechCH.tileentity.cores.motors.MTEC_Motor;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

/**
 * @author CHanzy
 * base class of Motor without Converter, use in steam turbine, motor liquid, motor gas (if have).
 * using method similar to the liquid burning box, Motor liquid, Motor, etc
 */
public abstract class TileEntityBase10Motor extends TileEntityBase09FacingSingle implements ITileEntityEnergy, ITileEntityRunningActively, ITileEntityAdjacentOnOff {
	protected MTEC_Motor mCore;

	// NBT读写
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		// GTCH, core init
		if (mCore == null) mCore = getNewCoreMotor();
		mCore.init(aNBT);

		super.readFromNBT2(aNBT);

		// GTCH, core init
		mCore.postInit(aNBT);
	}
	private MTEC_Motor getNewCoreMotor() {MTEC_Motor tCore = getNewCoreMotor2(); tCore.preInit(); return tCore;}
	protected abstract MTEC_Motor getNewCoreMotor2();
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);

		// GTCH, core save
		mCore.writeToNBT(aNBT);
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

	// 每 tick 转换
	@Override public final void onTick2(long aTimer, boolean aIsServerSide) {if (aIsServerSide) {IMTEC_MotorTick.Util.onTick(mCore, aTimer);}}

	@Override public boolean breakBlock() {mCore.onBreakBlock(); return super.breakBlock();}

	// 一些接口
	@Override public float getSurfaceSizeAttachable (byte aSide) {return mCore.getSurfaceSizeAttachable(aSide);}
	@Override public boolean isSideSolid2           (byte aSide) {return mCore.isSideSolid(aSide);}
	@Override public boolean isSurfaceOpaque2       (byte aSide) {return mCore.isSurfaceOpaque(aSide);}

	@Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return mCore.getFluidTankFillable(aSide, aFluidToFill);}
	@Override protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {return mCore.getFluidTankDrainable(aSide, aFluidToDrain);}
	@Override protected IFluidTank[] getFluidTanks2(byte aSide) {
		return mCore.getFluidTanks(aSide);
	}
	@Override public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {return mCore.funnelFill(aSide, aFluid, aDoFill);}
	@Override public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {return mCore.tapDrain(aSide, aMaxDrain, aDoDrain);}
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
	public boolean setAdjacentOnOff(boolean aOnOff) {return mCore.setAdjacentOnOff(aOnOff);}
	public boolean setStateOnOff(boolean aOnOff) {return mCore.setStateOnOff(aOnOff);}
	public boolean getStateOnOff() {return mCore.getStateOnOff();}

	@Override public boolean onTickCheck(long aTimer) {return mCore.onTickCheck(aTimer) || super.onTickCheck(aTimer);}
	@Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		mCore.onTickResetChecks(aTimer, aIsServerSide);
	}
	@Override public void setVisualData(byte aData) {mCore.setVisualData(aData);}
	@Override public byte getVisualData() {return mCore.getVisualData();}

	// Icons，图像动画
	@Override public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return mCore.getTexture(aBlock, aRenderPass, aSide, aShouldSideBeRendered);}
	@Override public byte getDefaultSide() {return mCore.getDefaultSide();}
	@Override public boolean[] getValidSides() {return mCore.getValidSides();}
}
