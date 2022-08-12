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
import static gregtechCH.data.CS_CH.CONNECTED_SIDE_AXIS;

import java.util.List;

import gregapi.render.ITexture;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Gregorius Techneticies
 */
public abstract class TileEntityBase11ConnectorStraight extends TileEntityBase10ConnectorRendered {
	@Override
	protected void mergeUpdate() {
		if (mDiameter < 1.0F) {
			// 直接套用不需要考虑 shrink 的情况
			System.arraycopy(CONNECTED_SIDE_AXIS[mConnections], 0, mCSides, 0, 6);
			// 统计合并数
			mMergeCount = 0;
			if (SIDES_VALID[mCSides[0]]) {
				++mMergeCount;
				if (ALONG_AXIS[mCSides[0]][mCSides[1]]) ++mMergeCount;
			}
		}
	}

	@Override
	protected int getRenderPasses3(Block aBlock, boolean[] aShouldSideBeRendered) {
		return (isFoam() || isFoamDried()) ? 2 : 1;
	}

	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		if (aRenderPass == 1) return (aShouldSideBeRendered[aSide] && (isFoam() || isFoamDried())) ? (isFoamDried() ? getTextureCFoamDry(aSide, mConnections, mDiameter, aRenderPass) : getTextureCFoam(aSide, mConnections, mDiameter, aRenderPass)) : null;
		if (aRenderPass == 0) {
			if (mDiameter >= 1.0F && !aShouldSideBeRendered[aSide]) return null;
			if (mConnections == 0) return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
			if (!connected(aSide)) return getTextureSide(aSide, mConnections, mDiameter, aRenderPass);
			if (!aShouldSideBeRendered[aSide]) return null;
			return getTextureConnected(aSide, mConnections, mDiameter, aRenderPass);
		}
		return null;
	}

	@Override
	public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
		return aRenderPass == 0 && mDiameter < 1.0F && setBlockBoundsStraight(aBlock);
	}

	// 重写这个方法使得在有建筑泡沫时时只在建筑泡沫上渲染覆盖板，因为 renderpass 改变了
	@Override
	public boolean isCoverSurface(byte aSide, int aRenderpass) {
		boolean tCSurface = super.isCoverSurface(aSide);
		if (tCSurface) {
			if (isFoam() && !isFoamDried()) return aRenderpass==1;
			if (isFoamDried()) {
				// 遮住管道的覆盖板要渲染遮住面
				if (mCRLengths[aSide]<0.0F && aRenderpass==0) return T;
				return aRenderpass==1;
			}
		}
		return tCSurface;
	}

	@Override public boolean usesRenderPass2(int aRenderPass, boolean[] aShouldSideBeRendered) {return T;}
	@Override public void addCollisionBoxesToList2(AxisAlignedBB aAABB, List<AxisAlignedBB> aList, Entity aEntity) {if (!addDefaultCollisionBoxToList()) box(aAABB, aList, FACE_CONNECTED[SIDE_X_NEG][mConnections] ? 0 : (1.0F-mDiameter)/2.0F, FACE_CONNECTED[SIDE_Y_NEG][mConnections] ? 0 : (1.0F-mDiameter)/2.0F, FACE_CONNECTED[SIDE_Z_NEG][mConnections] ? 0 : (1.0F-mDiameter)/2.0F, FACE_CONNECTED[SIDE_X_POS][mConnections] ? 1 : 1-(1.0F-mDiameter)/2.0F, FACE_CONNECTED[SIDE_Y_POS][mConnections] ? 1 : 1-(1.0F-mDiameter)/2.0F, FACE_CONNECTED[SIDE_Z_POS][mConnections] ? 1 : 1-(1.0F-mDiameter)/2.0F);}
	
	// Makes sure the Axles are going actually straight.
	@Override public boolean connect(byte aSide, boolean aNotify) {
		for (byte tSide : ALL_SIDES_VALID_BUT_AXIS[aSide]) if (connected(tSide) && !disconnect(tSide, T)) return F;
		return super.connect(aSide, aNotify);
	}
}
