/**
 * Copyright (c) 2021 GregTech-6 Team
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

package gregapi.tileentity.connectors;

import static gregapi.data.CS.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetDebugInfo;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.CS.SFX;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.network.INetworkHandler;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityQuickObstructionCheck;
import gregapi.tileentity.data.ITileEntityProgress;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataConductor;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityAxle extends TileEntityBase11ConnectorStraight implements ITileEntityQuickObstructionCheck, ITileEntityEnergy, ITileEntityEnergyDataConductor, ITileEntityProgress, IMTE_GetDebugInfo {
	public long mTransferredPower = 0, mTransferredSpeed = 0, mTransferredEnergy = 0, mTransferredLast = 0, mPower = 1, mSpeed = 32;
	public byte mRotationDir = 0, oRotationDir = 0;

	// GTCH，用来实现轴的单向传递能量，避免一些问题
	public byte mEnergyDir = SIDE_ANY, oEnergyDir = SIDE_ANY;
	public byte oConnections = 0;
	public long mStateSpeed = 0, mPowerLast = 0, mSpeedLast = 0;
	// GTCH, 颜色，预先计算会不会好些？
	protected int mRGBaMark;
	// GTCH, 用于在建筑泡沫上显示 mark
	public int mMarkBuffer = 0;
	public boolean mOutMark = F, oOutMark = F;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_ACTIVE_DATA)) mRotationDir = aNBT.getByte(NBT_ACTIVE_DATA);
		if (aNBT.hasKey(NBT_ENERGY_EMITTED_SIDES)) mEnergyDir = aNBT.getByte(NBT_ENERGY_EMITTED_SIDES);
		if (aNBT.hasKey(NBT_PIPESIZE)) mSpeed = Math.max(1, aNBT.getLong(NBT_PIPESIZE));
		if (aNBT.hasKey(NBT_PIPEBANDWIDTH)) mPower = Math.max(1, aNBT.getLong(NBT_PIPEBANDWIDTH));
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		aNBT.setByte(NBT_ACTIVE_DATA, mRotationDir);
		aNBT.setByte(NBT_ENERGY_EMITTED_SIDES, mEnergyDir);
	}

	@Override
	public NBTTagCompound writeItemNBT2(NBTTagCompound aNBT) {
		if (isFoamDried()){
			aNBT.setByte(NBT_ENERGY_EMITTED_SIDES, mEnergyDir);
		}
		return super.writeItemNBT2(aNBT);
	}
	
	@Override
	public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
		aList.add(Chat.CYAN + LH_CH.get(LH_CH.AXLE_STATS_SPEED) + " " + mSpeed + " " + TD.Energy.RU.getLocalisedNameShort());
		aList.add(Chat.CYAN + LH_CH.get(LH_CH.AXLE_STATS_POWER) + " " + mPower);
		super.addToolTips(aList, aStack, aF3_H);
		aList.add(Chat.DGRAY + LH.get(LH.TOOL_TO_SET_OUTPUT_MONKEY_WRENCH));
		aList.add(Chat.DGRAY + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
		aList.add(LH.Chat.DGRAY + LH_CH.get(LH_CH.TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK));
	}

	// GTCH, 使用活动扳手调整限制输出方向
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		if (isClientSide()) return 0;
		if (aTool.equals(TOOL_monkeywrench)) {
			checkConnection();
			byte aTargetSide = UT.Code.getSideWrenching(aSide, aHitX, aHitY, aHitZ);
			if (connected(aTargetSide)) {
				mEnergyDir = (mEnergyDir == aTargetSide) ? SIDE_ANY : aTargetSide;
//				if (aChatReturn != null) aChatReturn.add(mEnergyDir == SIDE_ANY?"Can transfer energy to both sides":"Only transfer energy to Selected Side");
				return 2500;
			} else {
				return 0;
			}
		}
		if (aTool.equals(TOOL_magnifyingglass) && aSneaking) {
			if (mTransferredLast > 0) {
				if (aChatReturn != null) {
					aChatReturn.add(mSpeedLast<0 ? "Counterclockwise" : "Clockwise");
					aChatReturn.add("Speed: " + Math.abs(mSpeedLast));
					aChatReturn.add("Power: " + mPowerLast);
				}
			} else {
				if (aChatReturn != null) aChatReturn.add("No transferred energy");
			}
			return 1;
		}
		if (aTool.equals(TOOL_magnifyingglass)) {
			checkConnection();
			if (isFoamDried()) {
				mMarkBuffer = 128;
				mOutMark = T;
			}
			if (aChatReturn != null) {
				aChatReturn.add((mEnergyDir == SIDE_ANY)?"Can transfer energy to both sides":"Only transfer energy to Marked Side");
			}
			return 1;
		}
		return 0;
	}

	protected void checkConnection() {
		if (!connected(mEnergyDir)) mEnergyDir = SIDE_ANY;
		oConnections = mConnections;
	}
	
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		super.onTick2(aTimer, aIsServerSide);
		
		if (aIsServerSide) {
			if (mTransferredSpeed == 0 && aTimer > 5) mRotationDir = 0;
			mTransferredLast = mTransferredEnergy;
			mTransferredEnergy = mTransferredSpeed = 0;

			// GTCH, 用于放大镜显示
			mSpeedLast = mStateSpeed;
			mPowerLast = mTransferredPower;
			mStateSpeed = mTransferredPower = 0;

			if (isFoamDried() && mOutMark) {
				--mMarkBuffer;
				if (mMarkBuffer < 0) {
					mOutMark = F;
					mMarkBuffer = 0;
				}
			}
		}
	}

	@Override
	protected int getRenderPasses3(Block aBlock, boolean[] aShouldSideBeRendered) {
		if (worldObj == null && isFoamDried()) mRGBaMark = UT_CH.Code.getMarkRGB(mRGBa);
		return super.getRenderPasses3(aBlock, aShouldSideBeRendered);
	}
	
	@Override public boolean onTickCheck(long aTimer) {return mRotationDir != oRotationDir || mEnergyDir != oEnergyDir || mOutMark != oOutMark || super.onTickCheck(aTimer);}
	@Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oRotationDir = mRotationDir;
		oEnergyDir = mEnergyDir;
		oOutMark = mOutMark;
	}
	@Override public void setVisualData(byte aData) {
		mRotationDir = (byte)(aData & 3);
		mOutMark = ((aData & 4) != 0);
		mEnergyDir = (byte)((aData >> 3) & 7);
	}
	@Override public byte getVisualData() {return (byte)((mRotationDir & 3) | (mOutMark?4:0) | ((mEnergyDir & 7) << 3));}

	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		boolean tOut = super.receiveDataByteArray(aData, aNetworkHandler);
		mRGBaMark = UT_CH.Code.getMarkRGB(mRGBa);
		return tOut;
	}
	
	public long transferRotations(byte aSide, long aSpeed, long aPower, long aChannel, HashSetNoNulls<TileEntity> aAlreadyPassed) {
		if (mTimer < 1) return 0;

		// GTCH, 用于放大镜显示
		if (Math.abs(aSpeed) > Math.abs(mStateSpeed)) mStateSpeed = aSpeed;
		
		// Replaced a switch/case with "simple" Math.
		// Sides pointing to the negative Axis direction are ?1:2, while the positive direction is ?2:1
		// Abusing the Fact that negative is always even and positive is always odd.
		mRotationDir = (byte)(aSpeed < 0 ? 1+(aSide & 1) : 2-(aSide & 1));
		if (oRotationDir == 0) return addToEnergyTransferred(aSpeed, aPower, aPower);
		
		if (!canEmitEnergyTo(OPOS[aSide])) return addToEnergyTransferred(aSpeed, aPower, 0);
		DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(OPOS[aSide]);
		return addToEnergyTransferred(aSpeed, aPower, aAlreadyPassed.add(tDelegator.mTileEntity) ? tDelegator.mTileEntity instanceof MultiTileEntityAxle ? ((MultiTileEntityAxle)tDelegator.mTileEntity).isEnergyAcceptingFrom(TD.Energy.RU, tDelegator.mSideOfTileEntity, F) ? ((MultiTileEntityAxle)tDelegator.mTileEntity).transferRotations(tDelegator.mSideOfTileEntity, aSpeed, aPower, aChannel, aAlreadyPassed) : 0 : ITileEntityEnergy.Util.insertEnergyInto(TD.Energy.RU, aSpeed, aPower, this, tDelegator) : 0);
	}
	
	public long addToEnergyTransferred(long aSpeed, long aOriginalPower, long aPower) {
		mTransferredSpeed += aSpeed;
		mTransferredPower += aPower;
		mTransferredEnergy += Math.abs(aSpeed * aPower);
		// Yes Rotation Speed only becomes a problem when it is actually being transferred,
		// If the Axle just Rotates Idle then it can spin at ludicrous Speeds.
		if (Math.abs(aSpeed) > mSpeed || mTransferredPower > mPower) {
			UT.Sounds.send(SFX.MC_BREAK, this);
			popOff();
			return aOriginalPower;
		}
		return aPower;
	}

	@Override
	public void onConnectionChange(byte aPreviousConnections) {
		super.onConnectionChange(aPreviousConnections);
		// GTCH, 使用内部的改变连接方向来检测连接
		checkConnection();
	}

	@Override
	public boolean canConnect(byte aSide, DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof ITileEntityEnergy) return ((ITileEntityEnergy)aDelegator.mTileEntity).isEnergyAcceptingFrom(TD.Energy.RU, aDelegator.mSideOfTileEntity, T) || ((ITileEntityEnergy)aDelegator.mTileEntity).isEnergyEmittingTo(TD.Energy.RU, aDelegator.mSideOfTileEntity, T);
		return F;
	}
	
	@Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return aEnergyType == TD.Energy.RU;}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return TD.Energy.RU.AS_LIST;}
	
	@Override public boolean isEnergyEmittingTo   (TagData aEnergyType, byte aSide, boolean aTheoretical) {return isEnergyType(aEnergyType, aSide, T) && canEmitEnergyTo    (aSide);}
	@Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return isEnergyType(aEnergyType, aSide, F) && canAcceptEnergyFrom(aSide);}
	@Override public synchronized long doEnergyExtraction(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoExtract) {return 0;}
	@Override public synchronized long doEnergyInjection (TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject ) {return aSize != 0 && isEnergyAcceptingFrom(aEnergyType, aSide, F) ? aDoInject ? transferRotations(aSide, aSize, aAmount, -1, new HashSetNoNulls<TileEntity>(F, this)) : aAmount : 0;}
	@Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mSpeed;}
	@Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return 0;}
	@Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mSpeed;}
	@Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mSpeed;}
	@Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return 0;}
	@Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mSpeed;}
	
	@Override public boolean canDrop(int aInventorySlot) {return F;}
	
	@Override public boolean isEnergyConducting(TagData aEnergyType) {return aEnergyType == TD.Energy.RU;}
	@Override public long getEnergyMaxSize(TagData aEnergyType) {return aEnergyType == TD.Energy.RU ? mSpeed : 0;}
	@Override public long getEnergyMaxPackets(TagData aEnergyType) {return aEnergyType == TD.Energy.RU ? mPower : 0;}
	@Override public long getEnergyLossPerMeter(TagData aEnergyType) {return 0;}
	@Override public OreDictMaterial getEnergyConductorMaterial() {return mMaterial;}
	@Override public OreDictMaterial getEnergyConductorInsulation() {return MT.NULL;}
	
	public boolean canEmitEnergyTo                          (byte aSide) {return SIDES_EQUAL[aSide][mEnergyDir] && connected(aSide);}
	public boolean canAcceptEnergyFrom                      (byte aSide) {return SIDES_EQUAL[aSide][OPOS[mEnergyDir]] && connected(aSide);}
	
	@Override public long getProgressValue                  (byte aSide) {return mTransferredPower;}
	@Override public long getProgressMax                    (byte aSide) {return mPower;}
	
	@Override public ArrayList<String> getDebugInfo(int aScanLevel) {return aScanLevel > 0 ? new ArrayListNoNulls<>(F, "Transferred Power: " + mTransferredEnergy) : null;}
	
	@Override public ITexture getTextureSide                (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureMulti.get(BlockTextureDefault.get(Textures.BlockIcons.AXLES[(mConnections & 12) != 0 ? 0 : (mConnections & 48) != 0 ? 2 : 1][aSide][mRotationDir], mRGBa), BlockTextureDefault.get(Textures.BlockIcons.ARROWS[1][aSide][mEnergyDir], mRGBaMark));}
	@Override public ITexture getTextureConnected           (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(Textures.BlockIcons.AXLES[(mConnections & 12) != 0 ? 0 : (mConnections & 48) != 0 ? 2 : 1][aSide][mRotationDir], mRGBa);}
	@Override
	public ITexture getTextureCFoamDry(byte aSide, byte aConnections, float aDiameter, int aRenderPass) {
		if (mOutMark) return BlockTextureMulti.get(BlockTextureDefault.get(mOwnable?Textures.BlockIcons.CFOAM_HARDENED_OWNED:Textures.BlockIcons.CFOAM_HARDENED, mRGBaFoam), BlockTextureDefault.get(Textures.BlockIcons.ARROWS[1][aSide][mEnergyDir], UT_CH.Code.getMarkRGB(mRGBaFoam)));
		return BlockTextureDefault.get(mOwnable?Textures.BlockIcons.CFOAM_HARDENED_OWNED:Textures.BlockIcons.CFOAM_HARDENED, mRGBaFoam);
	}
	@Override public Collection<TagData> getConnectorTypes  (byte aSide) {return TD.Connectors.AXLE_ROTATION.AS_LIST;}
	
	@Override public String getFacingTool                   () {return TOOL_wrench;}
	@Override public boolean isUsingWrenchingOverlay(ItemStack aStack, byte aSide) {return super.isUsingWrenchingOverlay(aStack, aSide) || ToolsGT.contains(TOOL_monkeywrench, aStack);}
	
	@Override public String getTileEntityName               () {return "gt.multitileentity.connector.axle.rotation";}
}
