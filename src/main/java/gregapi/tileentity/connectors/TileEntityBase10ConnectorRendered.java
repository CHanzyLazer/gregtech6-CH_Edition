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

package gregapi.tileentity.connectors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetPlayerRelativeBlockHardness;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetSelectedBoundingBoxFromPool;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_IgnorePlayerCollisionWhenPlacing;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_SetBlockBoundsBasedOnState;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.network.INetworkHandler;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityFoamable;
import gregapi.tileentity.ITileEntityQuickObstructionCheck;
import gregapi.tileentity.data.ITileEntitySurface;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.util.UT;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.CONNECTED_SIDE_AXIS;
import static gregtechCH.util.UT_CH.Code.RENDER_LENGTH;

/**
 * @author Gregorius Techneticies
 */
public abstract class TileEntityBase10ConnectorRendered extends TileEntityBase09Connector implements ITileEntityQuickObstructionCheck, ITileEntityFoamable, IMTE_GetPlayerRelativeBlockHardness, IMTE_IgnorePlayerCollisionWhenPlacing, IMTE_GetSelectedBoundingBoxFromPool, IMTE_SetBlockBoundsBasedOnState {
	public float mDiameter = 1.0F;
	public boolean mTransparent = F, mIsGlowing = F, mContactDamage = F, mOwnable = F;
	// 用 private 封装防止意料外的修改
	private boolean mFoamDried = F, mFoam = F;
	public boolean isFoamDried() {return mFoamDried;}
	public boolean isFoam() {return mFoam;}
	// GTCH, 用于在干掉后添加不透明度更新
	private void setFoamDried(boolean aFoamDried) {
		if (aFoamDried == mFoamDried) return;
		int tOldOpacity = getLightOpacity();
		mFoamDried = aFoamDried;
		updateLightOpacity(tOldOpacity);
	}
	// GTCH, 用于在添加建筑泡沫后添加不透明度更新
	private void setFoam(boolean aFoam) {
		if (aFoam == mFoam) return;
		int tOldOpacity = getLightOpacity();
		mFoam = aFoam;
		updateLightOpacity(tOldOpacity);
	}
	
	// GTCH, 用于额外长度连接的渲染，仅客户端有用
	protected float[] mCRLengths = new float[7], mCRDiameters = new float[7];
	protected boolean mCROut = F;
	protected final static float MARK_LENGTH = UT_CH.Code.RENDER_EPS * 2.0F;
	protected boolean mCRDataUpdated = F;
	// 用于表示需要合并渲染的边（前一个或两个），顺序为 x z y 轴，并且 Diameter 等于自身的优先
	byte[] mCSides = {SIDE_INVALID, SIDE_INVALID, SIDE_INVALID, SIDE_INVALID, SIDE_INVALID, SIDE_INVALID};
	byte mConnectionsNoShrink = 0;
	// 表示需要合并渲染的边 0 1 2
	byte mMergeCount = 0;
	
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_DIAMETER)) mDiameter = Math.max(PX_P[2], Math.min(PX_N[0], (float)aNBT.getDouble(NBT_DIAMETER))); // NBT 修改会有统一的更新和优化，不需要在这里再次调用
		if (aNBT.hasKey(NBT_TRANSPARENT)) mTransparent = aNBT.getBoolean(NBT_TRANSPARENT); // NBT 修改会有统一的更新和优化，不需要在这里再次调用
		if (aNBT.hasKey(NBT_CONTACTDAMAGE)) mContactDamage = aNBT.getBoolean(NBT_CONTACTDAMAGE);
		if (aNBT.hasKey(NBT_FOAMDRIED)) mFoamDried = aNBT.getBoolean(NBT_FOAMDRIED); // NBT 修改会有统一的更新和优化，不需要在这里再次调用
		if (aNBT.hasKey(NBT_FOAMED)) mFoam = aNBT.getBoolean(NBT_FOAMED); // NBT 修改会有统一的更新和优化，不需要在这里再次调用
		if (aNBT.hasKey(NBT_OWNABLE)) mOwnable = aNBT.getBoolean(NBT_OWNABLE);
		if (aNBT.hasKey(NBT_OWNER) && !OWNERSHIP_RESET) mOwner = UUID.fromString(aNBT.getString(NBT_OWNER));
		mIsGlowing = mMaterial.contains(TD.Properties.GLOWING);
		
		if (mFoam||mFoamDried) {
			if (aNBT.hasKey(NBT_PAINTED+".foam")) mIsPaintedFoam = aNBT.getBoolean(NBT_PAINTED+".foam");
			if (mIsPaintedFoam && aNBT.hasKey(NBT_COLOR+".foam")) mRGBPaintFoam = (int) UT_CH.NBT.getItemNumber(aNBT.getInteger(NBT_COLOR+".foam")); // 由于 0 值在 item 中还是会莫名丢失，因此默认值设为此值
			else if (isPainted() && aNBT.hasKey(NBT_COLOR)) mRGBPaintFoam=(int) UT_CH.NBT.getItemNumber(aNBT.getInteger(NBT_COLOR)); mIsPaintedFoam=T; // 兼容旧版染色
			// 应用染色
			if (mIsPaintedFoam) mRGBaFoam = UT_CH.Code.getPaintRGB(getBottomRGBFoam(), mRGBPaintFoam) & ALL_NON_ALPHA_COLOR;
			else mRGBaFoam = getBottomRGBFoam(); // 可以防止一些问题
		}
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		if (mFoam||mFoamDried) UT.NBT.setBoolean(aNBT, NBT_PAINTED+".foam", mIsPaintedFoam);
		if (mIsPaintedFoam && (mFoam||mFoamDried)) aNBT.setInteger(NBT_COLOR+".foam", mRGBPaintFoam);
		
		UT.NBT.setBoolean(aNBT, NBT_FOAMED, mFoam);
		UT.NBT.setBoolean(aNBT, NBT_FOAMDRIED, mFoamDried);
		UT.NBT.setBoolean(aNBT, NBT_OWNABLE, mOwnable);
		if (mOwner != null) aNBT.setString(NBT_OWNER, mOwner.toString());
	}
	
	@Override
	public NBTTagCompound writeItemNBT2(NBTTagCompound aNBT) {
		UT.NBT.setBoolean(aNBT, NBT_PAINTED+".foam", mIsPaintedFoam);
		if (mIsPaintedFoam && (mFoam||mFoamDried)) aNBT.setInteger(NBT_COLOR+".foam", (int) UT_CH.NBT.toItemNumber(mRGBPaintFoam));
		
		UT.NBT.setBoolean(aNBT, NBT_FOAMED, mFoam);
		UT.NBT.setBoolean(aNBT, NBT_FOAMDRIED, mFoamDried);
		UT.NBT.setBoolean(aNBT, NBT_OWNABLE, mOwnable);
		if (driedFoam(SIDE_ANY)) UT.NBT.setNumber(aNBT, NBT_CONNECTION, mConnections);
		return super.writeItemNBT2(aNBT);
	}
	
	// GTCH, 更新所有连接渲染数据
	@SideOnly(Side.CLIENT)
	protected void updateCRData() {
		// 先统一更新 （除了 mCROut）
		DelegatorTileEntity<TileEntity> tDelegator;
		for (byte tSide : ALL_SIDES_VALID) {
			tDelegator = getAdjacentTileEntity(tSide, F, F);
			if (connected(tSide)) {
				mCRLengths[tSide] = getConnectorLength(tSide, tDelegator);
				mCRDiameters[tSide] = getConnectorDiameter(tSide, tDelegator);
			} else {
				mCRDiameters[tSide] = mDiameter;
				mCRLengths[tSide] = 0.0F;
			}
		}
		// 巨型管道的情况
		if (mDiameter >= 1.0F) {
			mCROut = F;
			for (byte tSide : ALL_SIDES_VALID) if (connected(tSide)) {
				// 接小管道时连接面能正确渲染
				if (mCRDiameters[tSide] < mDiameter && mCRLengths[tSide] >= 0.0F) mCRLengths[tSide] = Math.max(mCRLengths[tSide], MARK_LENGTH+UT_CH.Code.RENDER_EPS);
				// 让巨型管道的加长部分不易出现渲染 bug
				if (mCRLengths[tSide] > RENDER_LENGTH && mCRDiameters[tSide] >= 1.0F) mCRDiameters[tSide] = 1.0F-UT_CH.Code.RENDER_EPS;
				mCROut |= mCRLengths[tSide] > MARK_LENGTH;
			}
		} else
		// 建筑泡沫的情况
		if (mFoam || mFoamDried) {
			mCROut = F;
			for (byte tSide : ALL_SIDES_VALID) if (connected(tSide)) {
				// 让上建筑泡沫后管道会露出一部分
				if (mCRLengths[tSide] >= 0.0F) mCRLengths[tSide] = Math.max(mCRLengths[tSide], 0.001F);
				// 让干建筑泡沫接小管道时连接面能正确渲染
				if (driedFoam(tSide) && mCRDiameters[tSide] < mDiameter && mCRLengths[tSide] >= 0.0F) mCRLengths[tSide] = Math.max(mCRLengths[tSide], MARK_LENGTH+UT_CH.Code.RENDER_EPS);
				mCROut |= mCRLengths[tSide] > MARK_LENGTH;
			}
		} else
		// 一般管道的情况
		if (mDiameter < 1.0F) {
			mCROut = F;
			for (byte tSide : ALL_SIDES_VALID) if (connected(tSide)) {
				mCROut |= mCRLengths[tSide] > MARK_LENGTH;
			}
		}
		// 处理合并渲染
		mergeUpdate();
	}
	
	// 用于子类重写简化合并渲染
	protected void mergeUpdate() {
		if (mDiameter < 1.0F && !driedFoam(SIDE_ANY)) {
			// 统计没有 shrink 的连接
			mConnectionsNoShrink = 0;
			for (byte tSide : ALL_SIDES_VALID) if (connected(tSide) && mCRDiameters[tSide] >= mDiameter) mConnectionsNoShrink |= SBIT[tSide];
			System.arraycopy(CONNECTED_SIDE_AXIS[mConnectionsNoShrink], 0, mCSides, 0, 6);
			// 统计合并数
			mMergeCount = 0;
			if (SIDES_VALID[mCSides[0]]) {
				++mMergeCount;
				if (ALONG_AXIS[mCSides[0]][mCSides[1]]) ++mMergeCount;
			}
			// 将 shrink 的边从后面加入 mCSides
			byte i = 5;
			for (byte tSide : ALL_SIDES_VALID) if (connected(tSide) && mCRDiameters[tSide] < mDiameter) {
				mCSides[i] = tSide;
				--i;
			}
		}
	}
	
	@Override
	public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
		toolTipsImportant(aList);
		toolTipsHazard(aList);
//		if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) // 可以实现按键显示更多 tooltips
		toolTipsOther(aList, aStack, aF3_H);
	}
	protected void toolTipsImportant(List<String> aList) {
		if (mOwnable) aList.add(Chat.ORANGE + LH.get(LH.OWNER_CONTROLLED));
	}
	protected void toolTipsHazard(List<String> aList) {
	}
	protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
		super.addToolTips(aList, aStack, aF3_H);
	}
	
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		super.onTick2(aTimer, aIsServerSide);
		
		if (aIsServerSide && aTimer >= 100 && mFoam && !mFoamDried && rng(5900) == 0) {
			setFoamDried(T);
			updateClientData();
		}
	}
	
	/*
	 * RenderPass:
	 * 0:    管道中心主体（如果有合并的则包含合并的部分）或者巨型管道或者干掉的建筑泡沫
	 * 1-6:  一般管道的侧边连接部分（不能合并的部分）或者建筑泡沫干掉后的覆盖材质
	 * 7:    未干的建筑泡沫
	 * 8-13: 延长到其他方块的部分或者巨型管道连接小管道时的收缩材质
	 * */
	@Override
	public final int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {
		if (worldObj == null) {
			if (!hasCovers() && !hasFoam(SIDE_ANY)) {
				mConnectionsNoShrink = mConnections = (byte)(SBIT_S|SBIT_N);
				mCRDiameters[SIDE_SOUTH] = mCRDiameters[SIDE_NORTH] = mDiameter;
				mCSides[0] = SIDE_SOUTH; mCSides[1] = SIDE_NORTH; mCSides[2] = mCSides[3] = mCSides[4] = mCSides[5] = 6;
				mMergeCount = 2;
			} else {
				for (byte tSide : ALL_SIDES_VALID) mCRDiameters[tSide] = mDiameter;
				if (mDiameter < 1.0F && driedFoam(SIDE_ANY)) for (byte tSide : ALL_SIDES_VALID) if (connected(tSide)) mCRLengths[tSide] = UT_CH.Code.RENDER_EPS;
			}
		} else
		// 在 render 的部分进行数据更新
		if (!mCRDataUpdated) {
			updateCRData();
			mCRDataUpdated = T;
		}
		
		return getRenderPasses3(aBlock, aShouldSideBeRendered);
	}
	// GTCH, 用于重写
	protected int getRenderPasses3(Block aBlock, boolean[] aShouldSideBeRendered) {
		if (mCROut) return 14;
		if (mFoam && !mFoamDried) return 8;
		if (mConnections != 0 && mDiameter < 1.0F) return 7;
		return 1;
	}
	
	@Override
	public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
		// 没干建筑泡沫的情况
		if (aRenderPass == 7) return F;
		// GTCH, 处理超出方块的材质
		if (aRenderPass >= 8 && aRenderPass <= 13) {
			return setBlockBoundsSide(aBlock, (byte)(aRenderPass-8), mCRDiameters[aRenderPass-8], 0.0F, mCRLengths[aRenderPass-8]);
		}
		// 巨型管道的情况
		if (mDiameter >= 1.0F) return F;
		// 建筑泡沫干掉的情况，注意 mCSides 此时没有意义
		if (driedFoam(SIDE_ANY)) {
			if (aRenderPass == 0) return F;
			if (aRenderPass <= 6 && aRenderPass >= 1) {
				return setBlockBoundsSide(aBlock, (byte)(aRenderPass-1), mDiameter, 0.0F, UT_CH.Code.RENDER_EPS);
			}
			return F;
		}
		// 一般管道的情况
		if (mDiameter < 1.0F) {
			if (aRenderPass == 0) {
				// 不使用方法为了防止超出数组的调用
				switch (mMergeCount) {
					case 0 : return setBlockBoundsDefault(aBlock, mDiameter);
					case 1 : return setBlockBoundsSide(aBlock, mCSides[0], mDiameter, (1.0F+mDiameter)/2.0F, mCRLengths[mCSides[0]]<=MARK_LENGTH?mCRLengths[mCSides[0]]:0.0F);
					case 2 : return setBlockBoundsSide(aBlock, mCSides[0], mDiameter, 1.0F+(mCRLengths[mCSides[1]]<=MARK_LENGTH?mCRLengths[mCSides[1]]:0.0F), mCRLengths[mCSides[0]]<=MARK_LENGTH?mCRLengths[mCSides[0]]:0.0F);
					default: return F;
				}
			}
			if (aRenderPass <= 6 && aRenderPass >= 1) {
				return setBlockBoundsSide(aBlock, mCSides[aRenderPass-1], mCRDiameters[mCSides[aRenderPass-1]], (1.0F- mCRDiameters[mCSides[aRenderPass-1]])/2.0F, mCRLengths[mCSides[aRenderPass-1]]<=MARK_LENGTH?mCRLengths[mCSides[aRenderPass-1]]:0.0F);
			}
			return F;
		}
		return F;
	}
	/* 使用属性设置管道默认的方块 */
	protected boolean setBlockBoundsDefault(Block aBlock) {
		return setBlockBoundsDefault(aBlock, mDiameter);
	}
	/* 使用直径设置管道默认的方块 */
	protected boolean setBlockBoundsDefault(Block aBlock, float aDiameter) {
		return box(aBlock, (1.0F-aDiameter)/2.0F, (1.0F-aDiameter)/2.0F, (1.0F-aDiameter)/2.0F, 1.0F-(1.0F-aDiameter)/2.0F, 1.0F-(1.0F-aDiameter)/2.0F, 1.0F-(1.0F-aDiameter)/2.0F);
	}
	/* 直接根据内部属性设置直线连接器的方块大小，使用 mMergeCount 判断如何连接 */
	protected boolean setBlockBoundsStraight(Block aBlock) {
		switch (mMergeCount) {
			case 0 : return setBlockBoundsDefault(aBlock, mDiameter);
			case 1 : return setBlockBoundsSide(aBlock, mCSides[0], mDiameter, (1.0F+mDiameter)/2.0F, mCRLengths[mCSides[0]]);
			case 2 : return setBlockBoundsSide(aBlock, mCSides[0], mDiameter, 1.0F+mCRLengths[mCSides[1]], mCRLengths[mCSides[0]]);
			default: return F;
		}
	}
	/* 使用直径和两个长度设置管道某个朝向的方块，两个长度分别为起始位置和终止位置，aLength1向内延申，aLength2向外延申 */
	protected boolean setBlockBoundsSide(Block aBlock, byte aSide, float aDiameter, float aLength1, float aLength2) {
		switch(aSide) {
			case SIDE_X_NEG: return box(aBlock,	-aLength2,	            (1.0F-aDiameter)/2.0F, 	(1.0F-aDiameter)/2.0F,	aLength1, 				1-(1.0F-aDiameter)/2.0F, 1-(1.0F-aDiameter)/2.0F	);
			case SIDE_Y_NEG: return box(aBlock,	(1.0F-aDiameter)/2.0F, 	-aLength2,           	(1.0F-aDiameter)/2.0F, 	1-(1.0F-aDiameter)/2.0F,	aLength1, 			 	1-(1.0F-aDiameter)/2.0F	);
			case SIDE_Z_NEG: return box(aBlock,	(1.0F-aDiameter)/2.0F,	(1.0F-aDiameter)/2.0F, 	-aLength2,				1-(1.0F-aDiameter)/2.0F, 1-(1.0F-aDiameter)/2.0F,	aLength1 			   	);
			case SIDE_X_POS: return box(aBlock,	1.0F-aLength1,			(1.0F-aDiameter)/2.0F,	(1.0F-aDiameter)/2.0F, 	1.0F+aLength2, 	        1-(1.0F-aDiameter)/2.0F, 1-(1.0F-aDiameter)/2.0F	);
			case SIDE_Y_POS: return box(aBlock,	(1.0F-aDiameter)/2.0F,	1.0F-aLength1,			(1.0F-aDiameter)/2.0F, 	1-(1.0F-aDiameter)/2.0F, 1.0F+aLength2,			1-(1.0F-aDiameter)/2.0F	);
			case SIDE_Z_POS: return box(aBlock,	(1.0F-aDiameter)/2.0F,	(1.0F-aDiameter)/2.0F,	1.0F-aLength1,			1-(1.0F-aDiameter)/2.0F, 1-(1.0F-aDiameter)/2.0F, 1.0F+aLength2			);
			default: return F;
		}
	}
	
	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		if (SIDES_INVALID[aSide]) return null;
		// 没干建筑泡沫的情况
		if (aRenderPass == 7) {
//			if (!aShouldSideBeRendered[aSide]) return null; // 因为没干的建筑泡沫是透明的，所以还是要渲染
			return getTextureCFoam(aSide, mConnections, mDiameter, aRenderPass);
		}
		// 延长层的情况
		if (aRenderPass >= 8 && aRenderPass <= 13) {
			if (aSide == OPOS[aRenderPass-8]) return null;
			if (aSide != aRenderPass - 8) {
				if (mCRLengths[aRenderPass-8] <= RENDER_LENGTH) return null; // 延长层过短不用渲染侧边
				return getTextureSide(aSide, mConnections, mCRDiameters[aRenderPass-8], aRenderPass);
			}
			if (!aShouldSideBeRendered[aRenderPass-8]) return null;
			return getTextureConnected(aSide, mConnections, mCRDiameters[aRenderPass-8], aRenderPass);
		}
		boolean tSideShrink = mDiameter > mCRDiameters[aSide];
		// 巨型管道情况（巨型管道不会有建筑泡沫）
		if (mDiameter >= 1.0F) {
			// 没有内部延长的情况
			if (aRenderPass == 0) {
				if (!aShouldSideBeRendered[aSide]) return null;
				if (connected(aSide)) {
					if (mCRLengths[aSide]>MARK_LENGTH) { // 拥有外套层
						if (!tSideShrink) return null; // 拥有完整外套层，不需要渲染
						return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
					}
					return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
				}
				if (mConnections == 0) return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
				return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
			}
			return null;
		}
		// 建筑泡沫干掉的情况
		if (driedFoam(aSide)) {
			if (aRenderPass == 0) {
				// 不能使用 multi 实现，因为涉及了材质的剪切
				if (!aShouldSideBeRendered[aSide]) return null;
				return getTextureCFoamDry(aSide, mConnections, mDiameter, aRenderPass);
			}
			// 干掉的情况下不会进行连接合并，所以 mCSides 此时没有意义
			if (aRenderPass <= 6 && aRenderPass >= 1) {
				// 干掉的情况下这个 renderPass 只是作为裁剪材质 multi 的用途
				if (!aShouldSideBeRendered[aRenderPass-1]) return null;
				if (aSide != aRenderPass-1) return null;
				if (tSideShrink) return getTexturePFoamDry(aSide, mConnections, mDiameter, aRenderPass);
				return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
			}
			return null;
		}
		// 一般管道的情况
		if (mDiameter < 1.0F) {
			if (aRenderPass == 0) {
				if (connected(aSide)) {
					if (tSideShrink) return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
					// 根据合并数分情况讨论
					if (mMergeCount == 0) return null;
					if ((mMergeCount == 1 && aSide == mCSides[0]) || (mMergeCount == 2 && (aSide == mCSides[0] || aSide == mCSides[1]))) {
						if (!aShouldSideBeRendered[aSide]) return null;
						return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
					}
					return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
				}
				if (mConnections == 0) return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
				return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
			}
			if (aRenderPass <= 6 && aRenderPass >= 1) {
				byte tSide = mCSides[aRenderPass-1]; if (SIDES_INVALID[tSide]) return null;
				if (aSide == OPOS[tSide]) return null;
				if (aSide != tSide) return getTextureSide(aSide, mConnections, mCRDiameters[tSide], aRenderPass);
				if (!aShouldSideBeRendered[tSide]) return null;
				// 大于的会有外套层，只需渲染外套层即可
				if (mCRLengths[tSide]<=MARK_LENGTH) return getTextureConnected(aSide, mConnections, mCRDiameters[tSide], aRenderPass);
				return null;
			}
			return null;
		}
		return null;
	}
	
	@Override public boolean usesRenderPass2(int aRenderPass, boolean[] aShouldSideBeRendered) {
		// 将不用渲染的直接不提供 RenderPass
		if (aRenderPass == 0) return T;
		if (aRenderPass == 7) return mFoam && !mFoamDried;
		if (aRenderPass <= 6 && aRenderPass >= 1) {
			// 建筑泡沫干掉的情况
			if (driedFoam((byte)(aRenderPass-1))) return connected((byte)(aRenderPass-1));
			// 一般管道的情况
			if(mDiameter < 1.0F) if (aRenderPass >= 1+mMergeCount) return SIDES_VALID[mCSides[aRenderPass-1]];
			return F;
		}
		if (aRenderPass >= 8 && aRenderPass <= 13) if(connected((byte)(aRenderPass - 8))) return mCRLengths[aRenderPass-8] > MARK_LENGTH;
		return F;
	}
	// 重写这个方法使得在有建筑泡沫时时只在建筑泡沫上渲染覆盖板
	@Override
	public boolean isCoverSurface(byte aSide, int aRenderpass) {
		boolean tCSurface = super.isCoverSurface(aSide);
		if (tCSurface) {
			if (mFoam && !mFoamDried) return aRenderpass == 7;
			if (driedFoam(aSide)) {
				// 遮住管道的覆盖板要渲染遮住面
				if (mCRLengths[aSide]<0.0F && aRenderpass-1==aSide) return T;
				return aRenderpass == 0;
			}
		}
		return tCSurface;
	}
	
	@Override public final int getLightOpacity() {
		if (mFoamDried) return LIGHT_OPACITY_MAX;
		if (mFoam) return LIGHT_OPACITY_WATER;
		return getLightOpacity2();
	}
	public int getLightOpacity2() {
		if (mTransparent) {
			if (mDiameter >= 1.0F) return LIGHT_OPACITY_WATER;
			if (mDiameter > 0.5F) return LIGHT_OPACITY_LEAVES;
			return LIGHT_OPACITY_NONE;
		}
		if (mDiameter >= 1.0F) return LIGHT_OPACITY_MAX;
		if (mDiameter > 0.5F) return LIGHT_OPACITY_WATER;
		return LIGHT_OPACITY_LEAVES;
	}
	// 对于套上建筑泡沫的情况需要使用建筑泡沫的颜色作为粒子效果
	@SideOnly(Side.CLIENT) @Override public final int colorMultiplier() {
		if (mFoam || mFoamDried) return mRGBaFoam;
		return colorMultiplier2();
	}
	@SideOnly(Side.CLIENT) protected int colorMultiplier2() {
		return super.colorMultiplier();
	}
	
	
	@Override public boolean ignorePlayerCollisionWhenPlacing(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {return !mFoam && mDiameter < 1.0F;}
	
	@Override
	public boolean onPlaced(ItemStack aStack, EntityPlayer aPlayer, MultiTileEntityContainer aMTEContainer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (mOwnable && aPlayer != null && !OWNERSHIP_RESET) mOwner = aPlayer.getUniqueID();
		return super.onPlaced(aStack, aPlayer, aMTEContainer, aWorld, aX, aY, aZ, aSide, aHitX, aHitY, aHitZ);
	}
	
	@Override
	public boolean allowInteraction(Entity aEntity) {
		return !mOwnable || super.allowInteraction(aEntity);
	}
	// GTCH, 使用这种方式在有建筑泡沫时禁用一些工具，目前只允许温度计，放大镜，撬棍
	@Override
	public boolean isEnabledTool(String aTool, long aQuality, boolean aSneaking, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (driedFoam(aSide)) return aTool.equals(TOOL_thermometer) || aTool.equals(TOOL_magnifyingglass) || aTool.equals(TOOL_crowbar) ;
		return T;
	}
	// GTCH, 直接重写这两个方法来让建筑泡沫干掉时禁用重新连接和断开
	@Override public boolean connect(byte aSide, boolean aNotify) {
		if (driedFoam(aSide)) return F;
		return super.connect(aSide, aNotify);
	}
	@Override public boolean disconnect(byte aSide, boolean aNotify) {
		if (driedFoam(aSide)) return F;
		return super.disconnect(aSide, aNotify);
	}
	
	// GTCH, 建筑泡沫颜色和管道颜色分开
	protected boolean mIsPaintedFoam = F;
	protected int mRGBPaintFoam = UNCOLORED;
	protected int mRGBaFoam = UNCOLORED; // 仅客户端有效
	protected int getBottomRGBFoam() {return UT.Code.getRGBInt(MT.ConstructionFoam.fRGBaSolid);}
	protected boolean paintFoam(int aRGB) {if (aRGB!= mRGBPaintFoam) {mRGBPaintFoam =aRGB; mIsPaintedFoam=T; return T;} return F;}
	protected boolean unpaintFoam() {if (mIsPaintedFoam) {mRGBPaintFoam=getBottomRGBFoam(); mIsPaintedFoam=F; return T;} return F;}
	// GTCH, 直接重写 recolourBlock 和 onPainting 方法来让有建筑泡沫时染色变成给建筑泡沫染色
	@Override public boolean recolourBlock(byte aSide, byte aColor) {
		if (!mFoam && !mFoamDried) return super.recolourBlock(aSide, aColor);
		if (isClientSide()) return F;
		if (UT.Code.exists(aColor, DYES_INVERTED)) {
			int aRGBFoam = (mIsPaintedFoam ? UT_CH.Code.mixRGBInt(mRGBPaintFoam, DYES_INT_INVERTED[aColor]) : DYES_INT_INVERTED[aColor]) & ALL_NON_ALPHA_COLOR;
			if (paintFoam(aRGBFoam)) {updateClientData(T); causeBlockUpdate(); return T;}
		}
		return F;
	}
	@Override public boolean onPainting(byte aSide, int aRGB) {
		if (!mFoam && !mFoamDried) return super.onPainting(aSide, aRGB);
		if (paintFoam(aRGB)) {updateClientData(T); causeBlockUpdate(); return T;}
		return F;
	}
	@Override public boolean unpaint() {
		if (!mFoam && !mFoamDried) return super.unpaint();
		return unpaintFoam();
	}
	@Override public boolean canRecolorItem(ItemStack aStack) {
		if (!mFoam && !mFoamDried) return super.canRecolorItem(aStack);
		return T;
	}
	@Override public boolean canDecolorItem(ItemStack aStack) {
		if (!mFoam && !mFoamDried) return super.canDecolorItem(aStack);
		return mIsPaintedFoam;
	}
	@Override public boolean recolorItem(ItemStack aStack, int aRGB) {
		if (!mFoam && !mFoamDried) return super.recolorItem(aStack, aRGB);
		if (paintFoam((mIsPaintedFoam ? UT_CH.Code.mixRGBInt(mRGBPaintFoam, aRGB) : aRGB) & ALL_NON_ALPHA_COLOR)) {UT.NBT.set(aStack, writeItemNBT(aStack.hasTagCompound() ? aStack.getTagCompound() : UT.NBT.make())); return T;} return F;
	}
	
	@Override
	public boolean decolorItem(ItemStack aStack) {
		if (!mFoam && !mFoamDried) return super.decolorItem(aStack);
		if (unpaintFoam()) {
			if (aStack.hasTagCompound()) {
				aStack.getTagCompound().removeTag(NBT_PAINTED+".foam");
				aStack.getTagCompound().removeTag(NBT_COLOR+".foam");
				UT.NBT.set(aStack, writeItemNBT(aStack.getTagCompound()));
			} else {
				UT.NBT.set(aStack, writeItemNBT(UT.NBT.make()));
			}
			return T;
		}
		return F;
	}
	
	@Override
	public boolean applyFoam(byte aSide, Entity aPlayer, short[] aCFoamRGB, byte aVanillaColor, boolean aOwned) {
		if (mDiameter >= 1.0F || mFoam || mFoamDried || isClientSide() || !allowInteraction(aPlayer)) return F;
		setFoam(T); setFoamDried(F); mOwnable = aOwned;
		if (mOwnable && aPlayer != null && !OWNERSHIP_RESET) mOwner = aPlayer.getUniqueID();
		paintFoam(UT.Code.getRGBInt(aCFoamRGB));
		updateClientData(T);
		return T;
	}
	
	@Override
	public boolean dryFoam(byte aSide, Entity aPlayer) {
		if (!mFoam || mFoamDried || isClientSide()) return F;
		setFoam(T); setFoamDried(T);
		updateClientData(T);
		return T;
	}
	
	@Override
	public boolean removeFoam(byte aSide, Entity aPlayer) {
		if (!mFoam || !mFoamDried || isClientSide() || !allowInteraction(aPlayer)) return F;
		setFoam(F); setFoamDried(F); mOwnable = F; mOwner = null;
		unpaintFoam();
		updateClientData(T);
		return T;
	}
	
	@Override public float getExplosionResistance2() {return Math.max(mFoam ? (mFoamDried?BlocksGT.CFoam:BlocksGT.CFoamFresh).getExplosionResistance(null) : 0, super.getExplosionResistance2());}
	
	// GTCH, 重写这个方法来扩展客户端数据
	@Override
	public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {
		super.writeToClientDataPacketByteList(rList);
		rList.add(5, (byte)UT.Code.getR(mRGBPaintFoam));
		rList.add(6, (byte)UT.Code.getG(mRGBPaintFoam));
		rList.add(7, (byte)UT.Code.getB(mRGBPaintFoam));
	}
	
	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		boolean oIsPaintedFoam = mIsPaintedFoam;
		int oRGBaPaintFoam = mRGBPaintFoam;
		super.receiveDataByteArray(aData, aNetworkHandler);
		mRGBPaintFoam = UT.Code.getRGBInt(new short[] {UT.Code.unsignB(aData[5]), UT.Code.unsignB(aData[6]), UT.Code.unsignB(aData[7])});
		if (oIsPaintedFoam!=mIsPaintedFoam || oRGBaPaintFoam!= mRGBPaintFoam) {
			onPaintFoamChangeClient(oRGBaPaintFoam); // 仅客户端，用于在染色改变时客户端更改对应的显示颜色
		}
		return T;
	}
	/* 仅客户端，用于在染色改变时客户端更改对应的显示颜色 */
	@SideOnly(Side.CLIENT)
	public void onPaintFoamChangeClient(int aPreviousRGBaPaintFoam) {
		mRGBaFoam = mIsPaintedFoam ? UT_CH.Code.getPaintRGB(getBottomRGBFoam(), mRGBPaintFoam) : getBottomRGBFoam();
	}
	@Override public byte getPaintData() {return (byte)(super.getPaintData() | (byte)(mIsPaintedFoam?4:0));}
	@Override public void setPaintData(byte aData) {super.setPaintData(aData); mIsPaintedFoam = ((aData&4)!=0);}
	
	@Override
	public byte getDirectionData() {
		return (byte)(((byte)(mConnections & 63)) | ((byte)((mFoamDried ? mOwnable : mFoam) ? 64 : 0)) | ((byte)(mFoamDried ? 128 : 0)));
	}
	
	@Override
	public void setDirectionData(byte aData) {
		mConnections = (byte)(aData & 63);
		setFoamDried((aData & (byte)128) != 0);
		if (mFoamDried) {
			mOwnable = ((aData & 64) != 0);
			setFoam(T);
		} else {
			mOwnable = (mOwner != null);
			setFoam((aData & 64) != 0);
		}
		
		// 朝向改变，需要更新渲染数据
		mCRDataUpdated = F;
	}
	// 相邻方块改变，需要更新渲染数据
	@Override public void onAdjacentBlockChange2(int aTileX, int aTileY, int aTileZ) {super.onAdjacentBlockChange2(aTileX, aTileY, aTileZ); if (isClientSide()) mCRDataUpdated = F;}
	
	@Override public int getFireSpreadSpeed         (byte aSide, boolean aDefault) {return mFoam ? 0 : super.getFireSpreadSpeed(aSide, aDefault);}
	@Override public int getFlammability            (byte aSide, boolean aDefault) {return mFoam ? 0 : super.getFlammability   (aSide, aDefault);}
	@Override public float getSurfaceSize           (byte aSide) {return (mFoam || mFoamDried) ? 1.0F : mDiameter;}
	@Override public float getSurfaceSizeAttachable (byte aSide) {return mDiameter;}
	@Override public float getSurfaceDistance       (byte aSide) {return (mFoam || mFoamDried || connected(aSide))?0.0F:(1.0F-mDiameter)/2.0F;}
	@Override public boolean isSurfaceSolid         (byte aSide) {return mFoamDried ||  mDiameter == 1.0F;}
	@Override public boolean isSurfaceOpaque2       (byte aSide) {return mFoamDried || (mDiameter == 1.0F && !mTransparent);}
	@Override public boolean isSideSolid2           (byte aSide) {return mFoamDried ||  mDiameter == 1.0F;}
	@Override public boolean isSealable2            (byte aSide) {return mFoamDried;}
	@Override public boolean usePipePlacementMode   (byte aSide) {return T;}
	@Override public boolean hasFoam                (byte aSide) {return mFoam;}
	@Override public boolean driedFoam              (byte aSide) {return mFoam && mFoamDried;}
	@Override public boolean ownedFoam              (byte aSide) {return mFoam && mOwnable;}
	@Override public boolean addDefaultCollisionBoxToList() {return mDiameter >= 1.0F || mFoamDried;}
	@Override public AxisAlignedBB getCollisionBoundingBoxFromPool() {return mContactDamage && !mFoamDried ? box(PX_P[2], PX_P[2], PX_P[2], PX_N[2], PX_N[2], PX_N[2]) : super.getCollisionBoundingBoxFromPool();}
	
	@Override
	public void addCollisionBoxesToList2(AxisAlignedBB aAABB, List<AxisAlignedBB> aList, Entity aEntity) {
		if (!addDefaultCollisionBoxToList()) {
			byte tSide;                                                                                                                                                                                                              box(aAABB, aList,   (1.0F-mDiameter)/2.0F,   (1.0F-mDiameter)/2.0F,   (1.0F-mDiameter)/2.0F, 1-(1.0F-mDiameter)/2.0F, 1-(1.0F-mDiameter)/2.0F, 1-(1.0F-mDiameter)/2.0F);
			if (connected(tSide = SIDE_X_NEG)) {DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide, F, F); float tDiameter = getConnectorDiameter(tSide, tDelegator), tLength = mContactDamage ? -PX_P[2] : 0; box(aAABB, aList, 0-tLength              ,   (1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F);}
			if (connected(tSide = SIDE_Y_NEG)) {DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide, F, F); float tDiameter = getConnectorDiameter(tSide, tDelegator), tLength = mContactDamage ? -PX_P[2] : 0; box(aAABB, aList,   (1.0F-tDiameter)/2.0F, 0-tLength              ,   (1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F);}
			if (connected(tSide = SIDE_Z_NEG)) {DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide, F, F); float tDiameter = getConnectorDiameter(tSide, tDelegator), tLength = mContactDamage ? -PX_P[2] : 0; box(aAABB, aList,   (1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F, 0-tLength              , 1-(1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F);}
			if (connected(tSide = SIDE_X_POS)) {DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide, F, F); float tDiameter = getConnectorDiameter(tSide, tDelegator), tLength = mContactDamage ? -PX_P[2] : 0; box(aAABB, aList, 1-(1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F, 1+tLength              , 1-(1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F);}
			if (connected(tSide = SIDE_Y_POS)) {DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide, F, F); float tDiameter = getConnectorDiameter(tSide, tDelegator), tLength = mContactDamage ? -PX_P[2] : 0; box(aAABB, aList,   (1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F, 1+tLength              , 1-(1.0F-tDiameter)/2.0F);}
			if (connected(tSide = SIDE_Z_POS)) {DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide, F, F); float tDiameter = getConnectorDiameter(tSide, tDelegator), tLength = mContactDamage ? -PX_P[2] : 0; box(aAABB, aList,   (1.0F-tDiameter)/2.0F,   (1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F, 1-(1.0F-tDiameter)/2.0F, 1+tLength              );}
		}
	}
	
	@Override
	public float[] shrunkBox() {
		if (mFoam || mDiameter >= 1.0F || hasCovers()) return PX_BOX;
		float tDiameter = (1.0F-mDiameter)/2.0F;
		return new float[] {connected(SIDE_X_NEG) ? 0 : tDiameter, connected(SIDE_Y_NEG) ? 0 : tDiameter, connected(SIDE_Z_NEG) ? 0 : tDiameter, connected(SIDE_X_POS) ? 1 : 1-tDiameter, connected(SIDE_Y_POS) ? 1 : 1-tDiameter, connected(SIDE_Z_POS) ? 1 : 1-tDiameter};
	}
	
	public float getConnectorLength(byte aConnectorSide, DelegatorTileEntity<TileEntity> aDelegator) {
		float rLength = 0;
		if (mDiameter < 1.0F && hasCovers() && mCovers.mBehaviours[aConnectorSide] != null) {
			if (mCovers.mBehaviours[aConnectorSide].showsConnectorFront(aConnectorSide, mCovers)) rLength = +UT_CH.Code.RENDER_EPS;
			else rLength = -UT_CH.Code.RENDER_EPS;
		}
		// 不考虑管道之间相互连接
		if ((aDelegator.mTileEntity instanceof ITileEntitySurface) && !(aDelegator.mTileEntity instanceof TileEntityBase10ConnectorRendered)) {
			float tDistance = ((ITileEntitySurface)aDelegator.mTileEntity).getSurfaceDistance(aDelegator.mSideOfTileEntity);
			if (tDistance > 0.0F) return Math.max(rLength, tDistance);
		}
		// TODO check for regular Collision Box.
		return rLength;
	}
	
	public float getConnectorDiameter(byte aConnectorSide, DelegatorTileEntity<TileEntity> aDelegator) {
		float rDiameter = mDiameter;
		if (aDelegator.mTileEntity instanceof ITileEntitySurface) rDiameter = ((ITileEntitySurface)aDelegator.mTileEntity).getSurfaceSizeAttachable(aDelegator.mSideOfTileEntity);
		// Connect with normal Size when the target cannot connect and ensure that minimum and maximum size are properly done.
		if (rDiameter <= 0.0F || rDiameter > mDiameter) rDiameter = mDiameter; else if (rDiameter < PX_P[2]) rDiameter = PX_P[2];
		return rDiameter;
	}
	
	// GTCH, 只有足够粗的管道或者已经连接才会阻挡
	@Override public final boolean isObstructingBlockAt(byte aSide) {return isObstructingBlockAt2(aSide) && (connected(aSide)||mDiameter>0.5F);}
	public boolean isObstructingBlockAt2(byte aSide) {return T;}
	
	// 关闭环境光遮蔽：UT.Code.getRGBaArray(mRGBa), mIsGlowing, F
	
	// GTCH, P为pipe，用于干掉后的管道材质（由于需要重新绘制，还有各种材质的，这样就不如直接multi了，所以略）
	public ITexture getTexturePFoamDry  (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return getTextureSide(aSide, aConnections, aDiameter, aRenderPass);}
	public ITexture getTextureSide      (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mMaterial, getIconIndexSide       (aSide, aConnections, aDiameter, aRenderPass), mIsGlowing, mRGBa);}
	public ITexture getTextureConnected (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mMaterial, getIconIndexConnected  (aSide, aConnections, aDiameter, aRenderPass), mIsGlowing, mRGBa);}
	public ITexture getTextureCFoam     (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mOwnable?Textures.BlockIcons.CFOAM_FRESH_OWNED:Textures.BlockIcons.CFOAM_FRESH, mRGBaFoam);}
	public ITexture getTextureCFoamDry  (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mOwnable?Textures.BlockIcons.CFOAM_HARDENED_OWNED:Textures.BlockIcons.CFOAM_HARDENED, mRGBaFoam);}
	
	public int getIconIndexSide         (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return IconsGT.INDEX_BLOCK_PIPE_SIDE;}
	public int getIconIndexConnected    (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return aDiameter<0.37F?OP.pipeTiny.mIconIndexBlock:aDiameter<0.49F?OP.pipeSmall.mIconIndexBlock:aDiameter<0.74F?OP.pipeMedium.mIconIndexBlock:aDiameter<0.99F?OP.pipeLarge.mIconIndexBlock:OP.pipeHuge.mIconIndexBlock;}
}
