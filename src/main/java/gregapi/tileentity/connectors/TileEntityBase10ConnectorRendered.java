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

import java.util.List;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetPlayerRelativeBlockHardness;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetSelectedBoundingBoxFromPool;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_IgnorePlayerCollisionWhenPlacing;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_SetBlockBoundsBasedOnState;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.data.CS.BlocksGT;
import gregapi.data.CS.IconsGT;
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
import gregapi.tileentity.data.ITileEntitySurface;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.util.UT;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

/**
 * @author Gregorius Techneticies
 */
public abstract class TileEntityBase10ConnectorRendered extends TileEntityBase09Connector implements ITileEntityFoamable, IMTE_GetPlayerRelativeBlockHardness, IMTE_IgnorePlayerCollisionWhenPlacing, IMTE_GetSelectedBoundingBoxFromPool, IMTE_SetBlockBoundsBasedOnState {
	public float mDiameter = 1.0F;
	public boolean mTransparent = F, mIsGlowing = F, mContactDamage = F, mFoam = F, mFoamDried = F, mOwnable = F;

	// GTCH, 用于额外长度连接的渲染，仅客户端有用
	protected float[] mCRLengths = new float[6], mCRDiameters = new float[6];
	protected boolean mCROut = F;
	protected final static float MARK_LENGTH = 0.002F, RENDER_LENGTH = 0.01F;
	protected boolean mCRDataUpdated = F;
	protected long oTimer = 0;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_COLOR+".foam")) mRGBaFoam = aNBT.getInteger(NBT_COLOR+".foam");
		if (aNBT.hasKey(NBT_DIAMETER)) mDiameter = Math.max(PX_P[2], Math.min(PX_N[0], (float)aNBT.getDouble(NBT_DIAMETER)));
		if (aNBT.hasKey(NBT_TRANSPARENT)) mTransparent = aNBT.getBoolean(NBT_TRANSPARENT);
		if (aNBT.hasKey(NBT_CONTACTDAMAGE)) mContactDamage = aNBT.getBoolean(NBT_CONTACTDAMAGE);
		if (aNBT.hasKey(NBT_FOAMDRIED)) mFoamDried = aNBT.getBoolean(NBT_FOAMDRIED);
		if (aNBT.hasKey(NBT_FOAMED)) mFoam = aNBT.getBoolean(NBT_FOAMED);
		if (aNBT.hasKey(NBT_OWNABLE)) mOwnable = aNBT.getBoolean(NBT_OWNABLE);
		if (aNBT.hasKey(NBT_OWNER) && !OWNERSHIP_RESET) mOwner = UUID.fromString(aNBT.getString(NBT_OWNER));
		mIsGlowing = mMaterial.contains(TD.Properties.GLOWING);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setNumber(aNBT, NBT_COLOR+".foam", mRGBaFoam);
		UT.NBT.setBoolean(aNBT, NBT_FOAMED, mFoam);
		UT.NBT.setBoolean(aNBT, NBT_FOAMDRIED, mFoamDried);
		UT.NBT.setBoolean(aNBT, NBT_OWNABLE, mOwnable);
		if (mOwner != null) aNBT.setString(NBT_OWNER, mOwner.toString());
	}
	
	@Override
	public NBTTagCompound writeItemNBT2(NBTTagCompound aNBT) {
		UT.NBT.setNumber(aNBT, NBT_COLOR+".foam", mRGBaFoam);
		UT.NBT.setBoolean(aNBT, NBT_FOAMED, mFoam);
		UT.NBT.setBoolean(aNBT, NBT_FOAMDRIED, mFoamDried);
		UT.NBT.setBoolean(aNBT, NBT_OWNABLE, mOwnable);
		if (mFoamDried) UT.NBT.setNumber(aNBT, NBT_CONNECTION, mConnections);
		return super.writeItemNBT2(aNBT);
	}

	// GTCH, 更新所有连接渲染数据
	@SideOnly(Side.CLIENT)
	protected void updateCRData() {
		DelegatorTileEntity<TileEntity> tDelegator;
		mCROut = F;
		for (byte tSide : ALL_SIDES_VALID) {
			tDelegator = getAdjacentTileEntity(tSide, F, F);
			if (connected(tSide)) {
				mCRLengths[tSide] = getConnectorLength(tSide, tDelegator);
				mCRDiameters[tSide] = getConnectorDiameter(tSide, tDelegator);
				// 不满足要求的长度归零
				if (tDelegator.mTileEntity instanceof TileEntityBase10ConnectorRendered) mCRLengths[tSide] = 0.0F;
				// 让建筑泡沫后管道会露出一部分
				if (mDiameter < 1.0F && (mFoam || mFoamDried)) mCRLengths[tSide] = Math.max(mCRLengths[tSide], 0.001F);
				// 让巨型管道和干建筑泡沫接小管道时连接面能正确渲染
				if ((mDiameter >= 1.0F || mFoamDried) && mCRDiameters[tSide] < mDiameter) mCRLengths[tSide] = Math.max(mCRLengths[tSide], MARK_LENGTH+0.001F);
				// 让巨型管道的加长部分不易出现渲染 bug
				if (mDiameter >= 1.0F && mCRLengths[tSide] > RENDER_LENGTH && mCRDiameters[tSide] >= 1.0F) mCRDiameters[tSide] = 0.999F;
				mCROut |= mCRLengths[tSide] > MARK_LENGTH;
			} else {
				mCRDiameters[tSide] = mDiameter;
				mCRLengths[tSide] = 0.0F;
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
			mFoamDried = T;
			updateClientData();
		}
	}
	
	@Override
	public final int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {
		if (worldObj == null) {
			if (!hasCovers() && !mFoamDried) {
				mConnections = (byte)(SBIT_S|SBIT_N);
				mCRDiameters[SIDE_SOUTH] = mCRDiameters[SIDE_NORTH] = mDiameter;
				if (mDiameter < 1.0F && mFoam) mCRLengths[SIDE_SOUTH] = mCRLengths[SIDE_NORTH] = 0.001F;
			} else {
				for (byte tSide : ALL_SIDES_VALID) mCRDiameters[tSide] = mDiameter;
			}
		} else
		// 在 render 的部分进行数据更新，放弃了原本的优化思路（其实这些优化都没什么用）
		if (!mCRDataUpdated || oTimer != mTimer) {
			// 保证每 tick 只更新一次
			updateCRData();
			mCRDataUpdated = T;
			oTimer = mTimer;
		}

		return getRenderPasses3(aBlock, aShouldSideBeRendered);
	}
	// GTCH, 用于重写
	protected int getRenderPasses3(Block aBlock, boolean[] aShouldSideBeRendered) {
		if (mConnections == 0) return 1;
		if (mCROut) return 14;
		if (mFoam) return 8;
		if (mDiameter >= 1.0F) return 1;
		return 7;
	}
	
	@Override
	public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
		if (aRenderPass == 0) {
			if (mFoamDried || mDiameter >= 1.0F) return F;
			return setBlockBoundsDefault(aBlock);
		}
		// TODO: I need to add the old optimizations back somehow.
		// Even though this Version is way more modular and can adjust to stuff much easier, it does also look bad when rendered with ambient occlusion.
		// GTCH, 管道的内部的额外部分，有 mark 和 rubber
		if (aRenderPass <= 6 && aRenderPass >= 1) {
			// 干掉的建筑泡沫专门讨论
			if (mFoamDried) return setBlockBoundsSide(aBlock, (byte)(aRenderPass-1), mDiameter, 0.0F, 0.001F);
			return setBlockBoundsSide(aBlock, (byte)(aRenderPass-1), mCRDiameters[aRenderPass-1], (1.0F- mCRDiameters[aRenderPass-1])/2.0F, mCRLengths[aRenderPass-1]<=MARK_LENGTH? mCRLengths[aRenderPass-1]:0.0F);
		}
		// GTCH, 处理超出方块的材质
		if (aRenderPass >= 8 && aRenderPass <= 13) {
			return setBlockBoundsSide(aBlock, (byte)(aRenderPass-8), mCRDiameters[aRenderPass-8], 0.0F, mCRLengths[aRenderPass-8]);
		}
		return F;
	}
	/* 使用属性设置管道默认的方块 */
	protected boolean setBlockBoundsDefault(Block aBlock) {
		return box(aBlock, (1.0F-mDiameter)/2.0F, (1.0F-mDiameter)/2.0F, (1.0F-mDiameter)/2.0F, 1.0F-(1.0F-mDiameter)/2.0F, 1.0F-(1.0F-mDiameter)/2.0F, 1.0F-(1.0F-mDiameter)/2.0F);
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
		if (aRenderPass == 7) {
			if (!aShouldSideBeRendered[aSide]) return null;
			return getTextureCFoam(aSide, mConnections, mDiameter, aRenderPass);
		}
		boolean tSideShrink = mDiameter > mCRDiameters[aSide];
		if (aRenderPass == 0) {
			if (mFoamDried) {
				// 不能使用 multi 实现，因为涉及了材质的剪切
				if (!aShouldSideBeRendered[aSide]) return null;
				return getTextureCFoamDry(aSide, mConnections, mDiameter, aRenderPass);
			}
			// renderPass==0 时 side 和 connected 相反
			if (mDiameter >= 1.0F) {
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
			if (connected(aSide)) {
				if (tSideShrink) return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
				return null; // 小管道永远有外套层，不需要渲染
			}
			if (mConnections == 0) return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
			return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
		}
		if (aRenderPass <= 6 && aRenderPass >= 1) {
			if (mDiameter >= 1.0F && !aShouldSideBeRendered[aSide]) return null; // 巨型管道没有这个 RenderPass，但是依旧保留兼容
			if (aSide == OPOS[aRenderPass - 1]) return null;
			if (aSide != aRenderPass - 1) {
				if (mFoamDried && mCRLengths[aRenderPass-1] <= RENDER_LENGTH) return null; // 建筑泡沫干掉后不需要渲染侧边
				return getTextureSide(aSide, mConnections, mCRDiameters[aRenderPass-1], aRenderPass);
			}
			if (!aShouldSideBeRendered[aSide]) return null;
			// 这里专门处理干掉的情况
			if (mFoamDried && tSideShrink) return getTexturePFoamDry(aSide, mConnections, mDiameter, aRenderPass);
			// 大于的会有外套层，只需渲染外套层即可
			if (mCRLengths[aRenderPass-1]<=MARK_LENGTH) return getTextureConnected(aSide, mConnections, mCRDiameters[aRenderPass-1], aRenderPass);
			return null;
		}
		if (aRenderPass >= 8 && aRenderPass <= 13) {
//			if (mDiameter >= 1.0F && !aShouldSideBeRendered[aSide]) return null; // 穿出方块的外套层不能根据主方块的遮挡来判断不进行渲染
			if (aSide == OPOS[aRenderPass-8]) return null;
			if (aSide != aRenderPass - 8) {
				if (mCRLengths[aRenderPass-8] <= RENDER_LENGTH) return null; // 延长层过短不用渲染侧边
				return getTextureSide(aSide, mConnections, mCRDiameters[aRenderPass-8], aRenderPass);
			}
			if (!aShouldSideBeRendered[aSide]) return null;
			return getTextureConnected(aSide, mConnections, mCRDiameters[aRenderPass-8], aRenderPass);
		}
		return null;
	}
	
	@Override public boolean usesRenderPass2(int aRenderPass, boolean[] aShouldSideBeRendered) {
		// 将不用渲染的直接不提供 RenderPass
		if (aRenderPass == 0) return T;
		if (aRenderPass == 7) return mFoam && !mFoamDried;
		if (aRenderPass <= 6 && aRenderPass >= 1) if(mDiameter < 1.0F) if(connected((byte)(aRenderPass - 1))) return T;
		if (aRenderPass >= 8 && aRenderPass <= 13) if(connected((byte)(aRenderPass - 8))) return mCRLengths[aRenderPass - 8] > MARK_LENGTH;
		return F;
	}
	
	@Override public int getLightOpacity() {return mFoamDried ? LIGHT_OPACITY_MAX : mTransparent ? mDiameter >= 1.0F ? LIGHT_OPACITY_WATER : mDiameter > 0.5F ? LIGHT_OPACITY_LEAVES : LIGHT_OPACITY_NONE : mDiameter >= 1.0F ? LIGHT_OPACITY_MAX : mDiameter > 0.5F ? LIGHT_OPACITY_WATER : LIGHT_OPACITY_LEAVES;}
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
	// GTCH, 使用这种方式在有建筑泡沫时禁用一些工具，目前只允许温度计和放大镜
	@Override
	public boolean isEnabledTool(String aTool, long aQuality, boolean aSneaking, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (mFoamDried) return aTool.equals(TOOL_thermometer) || aTool.equals(TOOL_magnifyingglass);
		return T;
	}

	// GTCH, 建筑泡沫颜色和管道颜色分开
	protected int mRGBaFoam = UNCOLORED;
	// GTCH, 直接重写 recolourBlock 和 onPainting 方法来让有建筑泡沫时染色变成给建筑泡沫染色
	@Override
	public boolean recolourBlock(byte aSide, byte aColor) {
		if (!mFoam && !mFoamDried) return super.recolourBlock(aSide, aColor);
		if (UT.Code.exists(aColor, DYES_INVERTED)) {
			int tRGBa = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.ConstructionFoam.fRGBaSolid), DYES_INT_INVERTED[aColor]);
			if (tRGBa!=mRGBaFoam) {
				mRGBaFoam=tRGBa;
				updateClientData();
				causeBlockUpdate();
				return T;
			}
		}
		return F;
	}
	@Override
	public boolean onPainting(byte aSide, int aRGB) {
		if (!mFoam && !mFoamDried) return super.onPainting(aSide, aRGB);
		int tRGBa = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.ConstructionFoam.fRGBaSolid), aRGB);
		if (tRGBa!=mRGBaFoam) {
			mRGBaFoam=tRGBa;
			updateClientData();
			causeBlockUpdate();
			return T;
		}
		return F;
	}
	// GTCH, 重写 unpaint 方法来让有建筑泡沫时禁用褪色
	@Override public boolean unpaint() {
		if (!mFoam && !mFoamDried) return super.unpaint();
		return F;
	}
	@Override public boolean canRecolorItem(ItemStack aStack) {
		if (!mFoam && !mFoamDried) return super.canRecolorItem(aStack);
		return F;
	}
	@Override public boolean canDecolorItem(ItemStack aStack) {
		if (!mFoam && !mFoamDried) return super.canDecolorItem(aStack);
		return F;
	}
	
	@Override
	public boolean applyFoam(byte aSide, Entity aPlayer, short[] aCFoamRGB, byte aVanillaColor, boolean aOwned) {
		if (mDiameter >= 1.0F || mFoam || mFoamDried || isClientSide() || !allowInteraction(aPlayer)) return F;
		mFoam = T; mFoamDried = F; mOwnable = aOwned;
		if (mOwnable && aPlayer != null && !OWNERSHIP_RESET) mOwner = aPlayer.getUniqueID();
		mRGBaFoam = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.ConstructionFoam.fRGBaSolid), UT.Code.getRGBInt(aCFoamRGB));
		updateClientData();
		return T;
	}
	
	@Override
	public boolean dryFoam(byte aSide, Entity aPlayer) {
		if (!mFoam || mFoamDried || isClientSide()) return F;
		mFoam = T; mFoamDried = T;
		updateClientData();
		return T;
	}
	
	@Override
	public boolean removeFoam(byte aSide, Entity aPlayer) {
		if (!mFoam || !mFoamDried || isClientSide() || !allowInteraction(aPlayer)) return F;
		mFoam = F; mFoamDried = F; mOwnable = F; mOwner = null;
		mRGBaFoam = UNCOLORED;
		updateClientData();
		return T;
	}
	
	@Override public float getExplosionResistance2() {return Math.max(mFoam ? (mFoamDried?BlocksGT.CFoam:BlocksGT.CFoamFresh).getExplosionResistance(null) : 0, super.getExplosionResistance2());}

	// GTCH, 重写这个方法来扩展客户端数据
	@Override
	public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {
		super.writeToClientDataPacketByteList(rList);
		rList.add(5, (byte)UT.Code.getR(mRGBaFoam));
		rList.add(6, (byte)UT.Code.getG(mRGBaFoam));
		rList.add(7, (byte)UT.Code.getB(mRGBaFoam));
	}

	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		super.receiveDataByteArray(aData, aNetworkHandler);
		mRGBaFoam = UT.Code.getRGBInt(new short[] {UT.Code.unsignB(aData[5]), UT.Code.unsignB(aData[6]), UT.Code.unsignB(aData[7])});
		return T;
	}

	@Override
	public byte getDirectionData() {
		return (byte)(((byte)(mConnections & 63)) | ((byte)((mFoamDried ? mOwnable : mFoam) ? 64 : 0)) | ((byte)(mFoamDried ? 128 : 0)));
	}
	
	@Override
	public void setDirectionData(byte aData) {
		mConnections = (byte)(aData & 63);
		mFoamDried = ((aData & (byte)128) != 0);
		if (mFoamDried) {
			mOwnable = ((aData & 64) != 0);
			mFoam = T;
		} else {
			mOwnable = (mOwner != null);
			mFoam = ((aData & 64) != 0);
		}

		// 朝向改变，需要更新渲染数据
		mCRDataUpdated = F;
	}

	@Override public float getSurfaceSize           (byte aSide) {return mFoamDried ? 1.0F : mDiameter;}
	@Override public float getSurfaceSizeAttachable (byte aSide) {return mDiameter;}
	@Override public float getSurfaceDistance       (byte aSide) {return mFoamDried || connected(aSide)?0.0F:(1.0F-mDiameter)/2.0F;}
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
			if (mCovers.mBehaviours[aConnectorSide].showsConnectorFront(aConnectorSide, mCovers)) rLength = +0.001F;
			else rLength = -0.001F;
		}
		if (aDelegator.mTileEntity instanceof ITileEntitySurface) {
			float tDistance = ((ITileEntitySurface)aDelegator.mTileEntity).getSurfaceDistance(aDelegator.mSideOfTileEntity);
			if (tDistance > 0) return Math.max(rLength, tDistance);
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
