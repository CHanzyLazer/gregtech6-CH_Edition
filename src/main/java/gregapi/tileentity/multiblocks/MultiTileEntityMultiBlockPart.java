/**
 * Copyright (c) 2022 GregTech-6 Team
 *
 * This file is part of GregTech.
 *
 * GregTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GregTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GregTech. If not, see <http://www.gnu.org/licenses/>.
 */

package gregapi.tileentity.multiblocks;

import gregapi.GT_API;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_BreakBlock;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnBlockAdded;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnWalkOver;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityAdjacentInventoryUpdatable;
import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityProgress;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.tileentity.data.ITileEntityWeight;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.logistics.ITileEntityLogistics;
import gregapi.tileentity.machines.*;
import gregapi.tileentity.notick.TileEntityBase05Paintable;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.fluid.IFluidHandler_CH;
import gregtechCH.tileentity.connectors.ITEInterceptAutoConnectFluid;
import gregtechCH.tileentity.connectors.ITEInterceptAutoConnectItem;
import gregtechCH.tileentity.connectors.ITEInterceptModConnectFluid;
import gregtechCH.tileentity.connectors.ITEInterceptModConnectItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityMultiBlockPart extends TileEntityBase05Paintable implements ITEInterceptModConnectItem, ITEInterceptModConnectFluid, ITEInterceptAutoConnectItem, ITEInterceptAutoConnectFluid, ITileEntityEnergy, ITileEntityCrucible, ITileEntityLogistics, IMTE_OnWalkOver, ITileEntityTemperature, ITileEntityGibbl, ITileEntityProgress, ITileEntityWeight, ITileEntityTapAccessible, ITileEntityFunnelAccessible, ITileEntityEnergyDataCapacitor, ITileEntityAdjacentInventoryUpdatable, IFluidHandler_CH, IMTE_OnBlockAdded, IMTE_BreakBlock, ITileEntityRunningSuccessfully, ITileEntitySwitchableMode, ITileEntitySwitchableOnOff {
	public ChunkCoordinates mTargetPos = null;
	
	public ITileEntityMultiBlockController mTarget = null;
	
	protected IIconContainer[][] mTextures = L1L6_IICONCONTAINER;
	
	// 用 private 封装防止意料外的修改
	private short mDesign = 0;
	
	public int mMode = 0;
	
	public static final int
	  EVERYTHING                 = 0
	
	, NO_ENERGY_OUT              = 1
	, NO_ENERGY_IN               = 2
	, NO_FLUID_OUT               = 4
	, NO_FLUID_IN                = 8
	, NO_ITEM_OUT                = 16
	, NO_ITEM_IN                 = 32
	, NO_LOGISTICS               = 64
	, NO_CRUCIBLE                = 128
	
	, NO_ENERGY                  = NO_ENERGY_IN | NO_ENERGY_OUT
	, NO_FLUID                   = NO_FLUID_IN  | NO_FLUID_OUT
	, NO_ITEM                    = NO_ITEM_IN   | NO_ITEM_OUT
	
	, ONLY_IN                    = NO_ENERGY_OUT | NO_FLUID_OUT | NO_ITEM_OUT | NO_LOGISTICS | NO_CRUCIBLE
	, ONLY_OUT                   = NO_ENERGY_IN  | NO_FLUID_IN  | NO_ITEM_IN  | NO_LOGISTICS | NO_CRUCIBLE
	
	, ONLY_ENERGY_OUT            = ~NO_ENERGY_OUT
	, ONLY_ENERGY_IN             = ~NO_ENERGY_IN
	, ONLY_FLUID_OUT             = ~NO_FLUID_OUT
	, ONLY_FLUID_IN              = ~NO_FLUID_IN
	, ONLY_ITEM_OUT              = ~NO_ITEM_OUT
	, ONLY_ITEM_IN               = ~NO_ITEM_IN
	, ONLY_ITEM_FLUID_OUT        = ~(NO_ITEM_OUT | NO_FLUID_OUT)
	, ONLY_ITEM_FLUID_IN         = ~(NO_ITEM_IN  | NO_FLUID_IN )
	, ONLY_ITEM_FLUID_ENERGY_OUT = ~(NO_ITEM_OUT | NO_FLUID_OUT | NO_ENERGY_OUT)
	, ONLY_ITEM_FLUID_ENERGY_IN  = ~(NO_ITEM_IN  | NO_FLUID_IN  | NO_ENERGY_IN )
	
	, ONLY_CRUCIBLE              = ~NO_CRUCIBLE
	, ONLY_LOGISTICS             = ~NO_LOGISTICS
	, ONLY_ENERGY                = ~NO_ENERGY
	, ONLY_FLUID                 = ~NO_FLUID
	, ONLY_ITEM                  = ~NO_ITEM
	, ONLY_ITEM_FLUID            = ~(NO_ITEM  | NO_FLUID )
	, ONLY_ITEM_FLUID_ENERGY     = ~(NO_ITEM  | NO_FLUID | NO_ENERGY)
	, ONLY_ITEM_ENERGY           = ~(NO_ITEM  | NO_ENERGY)
	, ONLY_FLUID_ENERGY          = ~(NO_FLUID | NO_ENERGY)
	
	, NOTHING                    = ~EVERYTHING
	;
	
	// GTCH, 增加 designMode 来更加灵活的控制外观
	private int mDesignMode = 0;
	// 记录所有的 designMode
	@SuppressWarnings("CStyleArrayDeclaration")
	public static final int
	  DEFAULT      = 0
	
	, NO_Y_NEG     = SBIT[SIDE_Y_NEG]
	, NO_Y_POS     = SBIT[SIDE_Y_POS]
	, NO_Z_NEG     = SBIT[SIDE_Z_NEG]
	, NO_Z_POS     = SBIT[SIDE_Z_POS]
	, NO_X_NEG     = SBIT[SIDE_X_NEG]
	, NO_X_POS     = SBIT[SIDE_X_POS]
	, GLOW         = 64  // 发光多方快部件
	, TRANSPARENT  = 128 // 透明多方快部件
	, AUTO_CONNECT = 256 // 是否允许染色管道自动连接（需要这个面没有被禁用 overlay）
	
	, ONLY_Y_NEG   =            NO_Y_POS | NO_Z_NEG | NO_Z_POS | NO_X_NEG | NO_X_POS
	, ONLY_Y_POS   = NO_Y_NEG |            NO_Z_NEG | NO_Z_POS | NO_X_NEG | NO_X_POS
	, ONLY_Z_NEG   = NO_Y_NEG | NO_Y_POS |            NO_Z_POS | NO_X_NEG | NO_X_POS
	, ONLY_Z_POS   = NO_Y_NEG | NO_Y_POS | NO_Z_NEG |            NO_X_NEG | NO_X_POS
	, ONLY_X_NEG   = NO_Y_NEG | NO_Y_POS | NO_Z_NEG | NO_Z_POS |            NO_X_POS
	, ONLY_X_POS   = NO_Y_NEG | NO_Y_POS | NO_Z_NEG | NO_Z_POS | NO_X_NEG
	
	, ONLY_SIDES[] = {ONLY_Y_NEG, ONLY_Y_POS, ONLY_Z_NEG, ONLY_Z_POS, ONLY_X_NEG, ONLY_X_POS, DEFAULT}
	;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_TARGET)) {mTargetPos = new ChunkCoordinates(UT.Code.bindInt(aNBT.getLong(NBT_TARGET_X)), UT.Code.bindInt(aNBT.getLong(NBT_TARGET_Y)), UT.Code.bindInt(aNBT.getLong(NBT_TARGET_Z)));}
		if (aNBT.hasKey(NBT_DESIGN)) mDesign = UT.Code.unsignB(aNBT.getByte(NBT_DESIGN));
		if (aNBT.hasKey(NBT_DESIGN+".mode")) mDesignMode = aNBT.getInteger(NBT_DESIGN+".mode"); // NBT 修改会有统一的更新和优化，不需要在这里再次调用
		if (aNBT.hasKey(NBT_MODE)) mMode = aNBT.getInteger(NBT_MODE);
		
		if (CODE_CLIENT) {
			if (GT_API.sBlockIcons == null && aNBT.hasKey(NBT_TEXTURE)) {
				String tTextureName = aNBT.getString(NBT_TEXTURE);
				mTextures = new IIconContainer[UT.Code.bind8(aNBT.getShort(NBT_DESIGNS))+1][6];
				for (short i = 0; i < mTextures.length; i++) {mTextures[i] = new IIconContainer[] {
				new Textures.BlockIcons.CustomIcon("machines/multiblockparts/"+tTextureName+"/"+i+"/colored/bottom"),
				new Textures.BlockIcons.CustomIcon("machines/multiblockparts/"+tTextureName+"/"+i+"/colored/top"),
				new Textures.BlockIcons.CustomIcon("machines/multiblockparts/"+tTextureName+"/"+i+"/colored/side"),
				new Textures.BlockIcons.CustomIcon("machines/multiblockparts/"+tTextureName+"/"+i+"/overlay/bottom"),
				new Textures.BlockIcons.CustomIcon("machines/multiblockparts/"+tTextureName+"/"+i+"/overlay/top"),
				new Textures.BlockIcons.CustomIcon("machines/multiblockparts/"+tTextureName+"/"+i+"/overlay/side")
				};}
			} else {
				TileEntity tCanonicalTileEntity = MultiTileEntityRegistry.getCanonicalTileEntity(getMultiTileEntityRegistryID(), getMultiTileEntityID());
				if (tCanonicalTileEntity instanceof MultiTileEntityMultiBlockPart) {
					mTextures = ((MultiTileEntityMultiBlockPart)tCanonicalTileEntity).mTextures;
				}
			}
		}
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		if (mDesign     != 0) aNBT.setByte(NBT_DESIGN, (byte)mDesign);
		if (mDesignMode != 0) aNBT.setInteger(NBT_DESIGN+".mode", mDesignMode);
		if (mMode       != 0) aNBT.setInteger(NBT_MODE, mMode);
		if (mTargetPos  != null) {
		UT.NBT.setBoolean(aNBT, NBT_TARGET, T);
		UT.NBT.setNumber(aNBT, NBT_TARGET_X, mTargetPos.posX);
		UT.NBT.setNumber(aNBT, NBT_TARGET_Y, mTargetPos.posY);
		UT.NBT.setNumber(aNBT, NBT_TARGET_Z, mTargetPos.posZ);
		}
	}
	
	@Override
	public boolean breakBlock() {
		ITileEntityMultiBlockController tTarget = getTarget(F);
		if (tTarget != null) {
			mTargetPos = null;
			mTarget = null;
			tTarget.onStructureChange();
		}
		return F;
	}
	
	@Override
	public void onBlockAdded() {
		for (byte tSide : ALL_SIDES_VALID) {
			DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide);
			if (tDelegator.mTileEntity instanceof MultiTileEntityMultiBlockPart) {
				ITileEntityMultiBlockController tController = ((MultiTileEntityMultiBlockPart)tDelegator.mTileEntity).getTarget(F);
				if (tController != null) tController.onStructureChange();
			} else if (tDelegator.mTileEntity instanceof ITileEntityMultiBlockController) {
				((ITileEntityMultiBlockController)tDelegator.mTileEntity).onStructureChange();
			}
		}
	}
	
	@Override
	public void adjacentInventoryUpdated(byte aSide, IInventory aTileEntity) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityAdjacentInventoryUpdatable) ((ITileEntityAdjacentInventoryUpdatable)tTileEntity).adjacentInventoryUpdated(aSide, aTileEntity);
	}
	
	public ITileEntityMultiBlockController getTarget(boolean aCheckValidity) {
		if (mTargetPos == null) return null;
		if (mTarget == null || mTarget.isDead()) {
			mTarget = null;
			if (worldObj.blockExists(mTargetPos.posX, mTargetPos.posY, mTargetPos.posZ)) {
				TileEntity tTarget = WD.te(worldObj, mTargetPos, T);
				if (tTarget instanceof ITileEntityMultiBlockController && ((ITileEntityMultiBlockController)tTarget).isInsideStructure(xCoord, yCoord, zCoord)) {
					mTarget = (ITileEntityMultiBlockController)tTarget;
				} else {
					mTargetPos = null;
					boolean
					tIsSet  = setDesign_(0);
					tIsSet |= setDesignMode_(DEFAULT);
					if (tIsSet) sendClientData(F, null); // 只进行图像更新，不需要 sendAll
				}
			}
		}
		return aCheckValidity && mTarget != null && !mTarget.checkStructureOnly(F) ? null : mTarget;
	}
	
	public void setTarget(ITileEntityMultiBlockController aTarget, int aDesign, int aMode) {setTarget(aTarget, aDesign, aMode, DEFAULT);}
	public void setTarget(ITileEntityMultiBlockController aTarget, int aDesign, int aMode, int aDesignMode) {
		mTarget = aTarget;
		mTargetPos = (mTarget == null ? null : mTarget.getCoords());
		mMode = aMode;
		boolean
		tIsSet  = setDesign_(aDesign);
		tIsSet |= setDesignMode_(aDesignMode);
		if (tIsSet) sendClientData(F, null); // 只进行图像更新，不需要 sendAll
	}
	
	/// GTCH, 图像部分
	// 需要在其是透明部件或者发光部件时设置透光
	@Override public int getLightOpacity() {return (mDesignMode & (GLOW | TRANSPARENT)) != 0 ? LIGHT_OPACITY_NONE : super.getLightOpacity();}
	
	private boolean setDesign_(int aDesign) {
		short tDesign = UT.Code.unsignB((byte)aDesign);
		if (tDesign == mDesign) return F;
		mDesign = tDesign;
		return T;
	}
	// 增加的用于主方块直接调用的调整 Design 的接口
	public void setDesign(int aDesign) {
		if (setDesign_(aDesign)) sendClientData(F, null); // 只进行图像更新，不需要 sendAll
	}
	private boolean setDesignMode_(int aDesignMode) {
		if (aDesignMode == mDesignMode) return F;
		int tOldOpacity = getLightOpacity();
		mDesignMode = aDesignMode;
		if (tOldOpacity != getLightOpacity()) updateLightOpacity(tOldOpacity); // 改为强制更新的版本来避免更新失效
		return T;
	}
	// 增加的用于主方块直接调用的调整 DesignMode 的接口
	public final void setDesignMode(int aDesignMode) {
		if (setDesignMode_(aDesignMode)) sendClientData(F, null); // 只进行图像更新，不需要 sendAll
	}
	
	
	/// 得到材质
	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		if (!aShouldSideBeRendered[aSide]) return null;
		if ((mDesignMode & TRANSPARENT) != 0) return null; // 透明即不输出任何材质
		boolean tGlow = (mDesignMode & GLOW) != 0;
		short tDesign = mDesign >= mTextures.length ? 0 : mDesign;
		ITexture tBase = BlockTextureDefault.get(mTextures[tDesign][FACES_TBS[aSide]], mRGBa, tGlow);
		ITexture tOverlay = sideNoOverlay(aSide) ? null : BlockTextureDefault.get(mTextures[tDesign][FACES_TBS[aSide] + 3], tGlow);
		return BlockTextureMulti.get(tBase, tOverlay);
	}
	private boolean sideNoOverlay(byte aSide) {
		if (SIDES_INVALID[aSide]) return T;
		return (mDesignMode & SBIT[aSide]) != 0;
	}
	
	
	// 添加 mDesignMode 的同步
	// GTCH, 重写这个方法保证和原本的逻辑一致，这里直接禁用 VisualData
	@Override
	@SuppressWarnings("SuspiciousIndentAfterControlStatement")
	public IPacket getClientDataPacketNoSendAll(boolean aSendAll) {
		if (mDesignMode == 0)               return getClientDataPacketByteArray(aSendAll, (byte)mDesign);
		if (mDesignMode <= Byte.MAX_VALUE)  return getClientDataPacketByteArray(aSendAll, (byte)mDesign, (byte)mDesignMode);
		if (mDesignMode <= Short.MAX_VALUE) return getClientDataPacketByteArray(aSendAll, (byte)mDesign, UT.Code.toByteS((short)mDesignMode, 0), UT.Code.toByteS((short)mDesignMode, 1));
		                                    return getClientDataPacketByteArray(aSendAll, (byte)mDesign, UT.Code.toByteI(mDesignMode, 0), UT.Code.toByteI(mDesignMode, 1), UT.Code.toByteI(mDesignMode, 2), UT.Code.toByteI(mDesignMode, 3));
	}
	@Override
	public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {
		rList.add(3, (byte)mDesign);
		rList.add(4, UT.Code.toByteI(mDesignMode, 0));
		rList.add(5, UT.Code.toByteI(mDesignMode, 1));
		rList.add(6, UT.Code.toByteI(mDesignMode, 2));
		rList.add(7, UT.Code.toByteI(mDesignMode, 3));
	}
	
	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		if (aData.length > 8) {
			setRGBData(aData[0], aData[1], aData[2], aData[aData.length-1]);
			setDesign_(aData[3]);
			setDesignMode_(UT.Code.combine(aData[4], aData[5], aData[6], aData[7]));
		} else
		if (aData.length > 4) {
			setDesign_(aData[0]);
			setDesignMode_(UT.Code.combine(aData[1], aData[2], aData[3], aData[4]));
		} else
		if (aData.length > 2) {
			setDesign_(aData[0]);
			setDesignMode_(UT.Code.combine(aData[1], aData[2]));
		} else
		if (aData.length > 1) {
			setDesign_(aData[0]);
			setDesignMode_(aData[1]);
		} else {
			setDesign_(aData[0]);
			setDesignMode_(0);
		}
		return T;
	}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.part";}
	
	// Relay Tool Uses
	
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (aTool.equals(TOOL_wrench) || aTool.equals(TOOL_crowbar)) return super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity == null) {
			if (aTool.equals(TOOL_magnifyingglass)) {
				aChatReturn.add("There is no Multiblock Controller, that has this Block as Part of it.");
				return 1;
			}
		} else {
			if (tTileEntity.isInsideStructure(xCoord, yCoord, zCoord)) return tTileEntity.onToolClickMultiBlock(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ, getCoords());
			mTargetPos = null;
			mTarget = null;
		}
		return super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
	}
	
	// Relay Inventories
	
	@Override
	public ItemStack decrStackSize(int aSlot, int aDecrement) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).decrStackSize(this, aSlot, aDecrement);
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int aSlot) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).getStackInSlotOnClosing(this, aSlot);
		return null;
	}
	@Override
	public ItemStack getStackInSlot(int aSlot) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).getStackInSlot(this, aSlot);
		return null;
	}
	@Override
	public String getInventoryName() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).getInventoryName(this);
		return getCustomName();
	}
	@Override
	public int getSizeInventory() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).getSizeInventory(this);
		return 0;
	}
	@Override
	public int getInventoryStackLimit() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).getInventoryStackLimit(this);
		return 0;
	}
	@Override
	public void setInventorySlotContents(int aSlot, ItemStack aStack) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) ((IMultiBlockInventory)tTileEntity).setInventorySlotContents(this, aSlot, aStack);
	}
	@Override
	public boolean hasCustomInventoryName() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).hasCustomInventoryName(this);
		return getCustomName() != null;
	}
	@Override
	public boolean isItemValidForSlot(int aSlot, ItemStack aStack) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).isItemValidForSlot(this, aSlot, aStack);
		return F;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide2(byte aSide) {
		if ((mMode & NO_ITEM) == NO_ITEM) return ZL_INTEGER;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).getAccessibleSlotsFromSide(this, aSide);
		return ZL_INTEGER;
	}
	@Override
	public boolean canInsertItem2(int aSlot, ItemStack aStack, byte aSide) {
		if ((mMode & NO_ITEM_IN) != 0) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).canInsertItem(this, aSlot, aStack, aSide);
		return F;
	}
	@Override
	public boolean canExtractItem2(int aSlot, ItemStack aStack, byte aSide) {
		if ((mMode & NO_ITEM_OUT) != 0) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockInventory) return ((IMultiBlockInventory)tTileEntity).canExtractItem(this, aSlot, aStack, aSide);
		return F;
	}
	
	// Relay Tanks
	@Override
	public boolean canFillExtra(FluidStack aFluid) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) {
			return (tTileEntity instanceof IFluidHandler_CH) ? ((IFluidHandler_CH)tTileEntity).canFillExtra(aFluid) : F;
		}
		return F;
	}
	
	@Override
	public int fill(ForgeDirection aDirection, FluidStack aFluid, boolean aDoFill) {
		if ((mMode & NO_FLUID_IN) != 0) return 0;
		byte aSide = UT.Code.side(aDirection);
		if (hasCovers() && SIDES_VALID[aSide] && mCovers.mBehaviours[aSide] != null && mCovers.mBehaviours[aSide].interceptFluidFill(aSide, mCovers, aSide, aFluid)) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) return ((IMultiBlockFluidHandler)tTileEntity).fill(this, aSide, aFluid, aDoFill);
		return 0;
	}
	@Override
	public FluidStack drain(ForgeDirection aDirection, FluidStack aFluid, boolean aDoDrain) {
		if ((mMode & NO_FLUID_OUT) != 0) return NF;
		byte aSide = UT.Code.side(aDirection);
		if (hasCovers() && SIDES_VALID[aSide] && mCovers.mBehaviours[aSide] != null && mCovers.mBehaviours[aSide].interceptFluidDrain(aSide, mCovers, aSide, aFluid)) return null;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) return ((IMultiBlockFluidHandler)tTileEntity).drain(this, aSide, aFluid, aDoDrain);
		return NF;
	}
	@Override
	public FluidStack drain(ForgeDirection aDirection, int aMaxDrain, boolean aDoDrain) {
		if ((mMode & NO_FLUID_OUT) != 0) return NF;
		byte aSide = UT.Code.side(aDirection);
		if (hasCovers() && SIDES_VALID[aSide] && mCovers.mBehaviours[aSide] != null && mCovers.mBehaviours[aSide].interceptFluidDrain(aSide, mCovers, aSide, null)) return null;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) return ((IMultiBlockFluidHandler)tTileEntity).drain(this, aSide, aMaxDrain, aDoDrain);
		return NF;
	}
	@Override
	public boolean canFill(ForgeDirection aDirection, Fluid aFluid) {
		if ((mMode & NO_FLUID_IN) != 0) return F;
		byte aSide = UT.Code.side(aDirection);
		if (hasCovers() && SIDES_VALID[aSide] && mCovers.mBehaviours[aSide] != null && mCovers.mBehaviours[aSide].interceptFluidFill(aSide, mCovers, aSide, FL.make(aFluid, 1))) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) return ((IMultiBlockFluidHandler)tTileEntity).canFill(this, UT.Code.side(aDirection), aFluid);
		return F;
	}
	@Override
	public boolean canDrain(ForgeDirection aDirection, Fluid aFluid) {
		if ((mMode & NO_FLUID_OUT) != 0) return F;
		byte aSide = UT.Code.side(aDirection);
		if (hasCovers() && SIDES_VALID[aSide] && mCovers.mBehaviours[aSide] != null && mCovers.mBehaviours[aSide].interceptFluidDrain(aSide, mCovers, aSide, FL.make(aFluid, 1))) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) return ((IMultiBlockFluidHandler)tTileEntity).canDrain(this, UT.Code.side(aDirection), aFluid);
		return F;
	}
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection aDirection) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockFluidHandler) return ((IMultiBlockFluidHandler)tTileEntity).getTankInfo(this, UT.Code.side(aDirection));
		return ZL_FLUIDTANKINFO;
	}
	
	@Override
	public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityFunnelAccessible) return ((ITileEntityFunnelAccessible)tTileEntity).funnelFill(aSide, aFluid, aDoFill);
		return 0;
	}
	
	@Override
	public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityTapAccessible) return ((ITileEntityTapAccessible)tTileEntity).tapDrain(aSide, aMaxDrain, aDoDrain);
		return null;
	}
	
	@Override
	public FluidStack nozzleDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityTapAccessible) return ((ITileEntityTapAccessible)tTileEntity).nozzleDrain(aSide, aMaxDrain, aDoDrain);
		return null;
	}
	
	// GTCH, 阻止非输入输出面的自动连接
	@Override
	public boolean interceptAutoConnectFluid(byte aSide) {
		// 如果不能输入输出流体则一定阻止自动连接
		if ((mMode & NO_FLUID) == NO_FLUID) return T;
		// 如果有专门启用自动连接并且这面没有被禁用 overlay 才会自动连接
		if ((mDesignMode & AUTO_CONNECT) != 0 && (mDesignMode & SBIT[aSide]) == 0) return F;
		return T;
	}
	@Override
	public boolean interceptAutoConnectItem(byte aSide)  {
		if ((mMode & NO_ITEM) == NO_ITEM) return T;
		// 如果有专门启用自动连接并且这面没有被禁用 overlay 才会自动连接
		if ((mDesignMode & AUTO_CONNECT) != 0 && (mDesignMode & SBIT[aSide]) == 0) return F;
		return T;
	}
	// GTCH, 不能输入和输出的面阻止 MOD 管道连接
	@Override public boolean interceptModConnectFluid(byte aSide) {return (mMode & NO_FLUID) == NO_FLUID;}
	@Override public boolean interceptModConnectItem(byte aSide)  {return (mMode & NO_ITEM)  == NO_ITEM;}
	
	
	// Relay Control Covers and such
	
	@Override
	public boolean getStateRunningPossible() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityRunningPossible) return ((ITileEntityRunningPossible)tTileEntity).getStateRunningPossible();
		return F;
	}
	
	@Override
	public boolean getStateRunningPassively() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityRunningPassively) return ((ITileEntityRunningPassively)tTileEntity).getStateRunningPassively();
		return F;
	}
	
	@Override
	public boolean getStateRunningActively() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityRunningActively) return ((ITileEntityRunningActively)tTileEntity).getStateRunningActively();
		return F;
	}
	
	@Override
	public boolean getStateRunningSuccessfully() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityRunningSuccessfully) return ((ITileEntityRunningSuccessfully)tTileEntity).getStateRunningSuccessfully();
		return F;
	}
	
	@Override
	public boolean getStateOnOff() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntitySwitchableOnOff) return ((ITileEntitySwitchableOnOff)tTileEntity).getStateOnOff();
		return F;
	}
	
	@Override
	public byte getStateMode() {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntitySwitchableMode) return ((ITileEntitySwitchableMode)tTileEntity).getStateMode();
		return 0;
	}
	
	@Override
	public boolean setStateOnOff(boolean aOnOff) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntitySwitchableOnOff) return ((ITileEntitySwitchableOnOff)tTileEntity).setStateOnOff(aOnOff);
		return F;
	}
	
	@Override
	public byte setStateMode(byte aMode) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntitySwitchableMode) return ((ITileEntitySwitchableMode)tTileEntity).setStateMode(aMode);
		return 0;
	}
	
	@Override
	public long getProgressValue(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityProgress) return ((ITileEntityProgress)tTileEntity).getProgressValue(aSide);
		return 0;
	}
	
	@Override
	public long getProgressMax(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityProgress) return ((ITileEntityProgress)tTileEntity).getProgressMax(aSide);
		return 0;
	}
	
	// Relay Energy
	
	@Override
	public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {
		if (aEmitting) {if ((mMode & NO_ENERGY_OUT) != 0) return F;} else {if ((mMode & NO_ENERGY_IN) != 0) return F;}
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).isEnergyType(this, aEnergyType, aSide, aEmitting);
		return F;
	}
	
	@Override
	public Collection<TagData> getEnergyTypes(byte aSide) {
		if ((mMode & NO_ENERGY) == NO_ENERGY) return Collections.emptyList();
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergyTypes(this, aSide);
		return Collections.emptyList();
	}
	
	@Override
	public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {
		if ((mMode & NO_ENERGY_IN) != 0) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).isEnergyAcceptingFrom(this, aEnergyType, aSide, aTheoretical);
		return F;
	}
	
	@Override
	public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {
		if ((mMode & NO_ENERGY_OUT) != 0) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).isEnergyEmittingTo(this, aEnergyType, aSide, aTheoretical);
		return F;
	}
	
	@Override
	public synchronized long doEnergyInjection(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {
		if ((mMode & NO_ENERGY_IN) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).doEnergyInjection(this, aEnergyType, aSide, aSize, aAmount, aDoInject);
		return 0;
	}
	
	@Override
	public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {
		if ((mMode & NO_ENERGY_IN) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergyDemanded(this, aEnergyType, aSide, aSize);
		return 0;
	}
	
	@Override
	public synchronized long doEnergyExtraction(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoExtract) {
		if ((mMode & NO_ENERGY_OUT) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).doEnergyExtraction(this, aEnergyType, aSide, aSize, aAmount, aDoExtract);
		return 0;
	}
	
	@Override
	public long getEnergyOffered(TagData aEnergyType, byte aSide, long aSize) {
		if ((mMode & NO_ENERGY_OUT) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergyOffered(this, aEnergyType, aSide, aSize);
		return 0;
	}
	
	@Override
	public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {
		if ((mMode & NO_ENERGY_IN) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergySizeInputMin(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {
		if ((mMode & NO_ENERGY_OUT) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergySizeOutputMin(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {
		if ((mMode & NO_ENERGY_IN) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergySizeInputRecommended(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {
		if ((mMode & NO_ENERGY_OUT) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergySizeOutputRecommended(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {
		if ((mMode & NO_ENERGY_IN) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergySizeInputMax(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {
		if ((mMode & NO_ENERGY_OUT) != 0) return 0;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergy) return ((IMultiBlockEnergy)tTileEntity).getEnergySizeOutputMax(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergyStored(TagData aEnergyType, byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergyDataCapacitor) return ((IMultiBlockEnergyDataCapacitor)tTileEntity).getEnergyStored(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public long getEnergyCapacity(TagData aEnergyType, byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergyDataCapacitor) return ((IMultiBlockEnergyDataCapacitor)tTileEntity).getEnergyCapacity(this, aEnergyType, aSide);
		return 0;
	}
	
	@Override
	public boolean isEnergyCapacitorType(TagData aEnergyType, byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergyDataCapacitor) return ((IMultiBlockEnergyDataCapacitor)tTileEntity).isEnergyCapacitorType(this, aEnergyType, aSide);
		return F;
	}
	
	@Override
	public Collection<TagData> getEnergyCapacitorTypes(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof IMultiBlockEnergyDataCapacitor) return ((IMultiBlockEnergyDataCapacitor)tTileEntity).getEnergyCapacitorTypes(this, aSide);
		return Collections.emptyList();
	}
	
	
	
	@Override
	public double getWeightValue(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityWeight) return ((ITileEntityWeight)tTileEntity).getWeightValue(aSide);
		return 0;
	}
	
	@Override
	public long getGibblValue(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityGibbl) return ((ITileEntityGibbl)tTileEntity).getGibblValue(aSide);
		return 0;
	}
	
	@Override
	public long getGibblMax(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityGibbl) return ((ITileEntityGibbl)tTileEntity).getGibblMax(aSide);
		return 0;
	}
	
	@Override
	public long getTemperatureValue(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityTemperature) return ((ITileEntityTemperature)tTileEntity).getTemperatureValue(aSide);
		return 0;
	}
	
	@Override
	public long getTemperatureMax(byte aSide) {
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityTemperature) return ((ITileEntityTemperature)tTileEntity).getTemperatureMax(aSide);
		return 0;
	}
	
	@Override
	public void onWalkOver(EntityLivingBase aEntity) {
		ITileEntityMultiBlockController tTileEntity = getTarget(F);
		if (tTileEntity instanceof IMTE_OnWalkOver) ((IMTE_OnWalkOver)tTileEntity).onWalkOver(aEntity);
	}
	
	@Override
	public boolean canLogistics(byte aSide) {
		if ((mMode & NO_LOGISTICS) != 0) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityLogistics) return ((ITileEntityLogistics)tTileEntity).canLogistics(aSide);
		return F;
	}
	
	@Override
	public boolean fillMoldAtSide(ITileEntityMold aMold, byte aSide, byte aSideOfMold) {
		if ((mMode & NO_CRUCIBLE) != 0) return F;
		ITileEntityMultiBlockController tTileEntity = getTarget(T);
		if (tTileEntity instanceof ITileEntityCrucible) return ((ITileEntityCrucible)tTileEntity).fillMoldAtSide(aMold, aSide, aSideOfMold);
		return F;
	}
	
	// Useless Garbage :P
	@Override public boolean isUseableByPlayer(EntityPlayer aPlayer) {return aPlayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;}
	@Override public void openInventory() {/**/}
	@Override public void closeInventory() {/**/}
}
