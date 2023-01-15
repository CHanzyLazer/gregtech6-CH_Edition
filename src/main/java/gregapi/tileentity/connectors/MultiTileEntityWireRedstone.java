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

import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetLightValue;
import gregapi.data.OP;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.ITexture;
import gregapi.util.UT;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityWireRedstone extends MultiTileEntityWireRedstoneInsulated {
	// 为了减少光照更新造成的卡顿，激活时不再发光
	@Override public int getLightOpacity2() {return LIGHT_OPACITY_NONE;}
	
	// GTCH, 还原为原本材料的颜色
	@Override public int getBottomRGB() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}
	public int getRedstoneRGB() {return mRGBa;}
	
	@Override public ITexture getTextureSide                (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mMaterial, getIconIndexSide       (aSide, aConnections, aDiameter, aRenderPass), getState()>0, worldObj==null?getRedstoneRGB():(getState()>0?mRGBaRedstoneON:mRGBaRedstoneOFF));}
	@Override public ITexture getTextureConnected           (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mMaterial, getIconIndexConnected  (aSide, aConnections, aDiameter, aRenderPass), getState()>0, worldObj==null?getRedstoneRGB():(getState()>0?mRGBaRedstoneON:mRGBaRedstoneOFF));}
	@Override public ITexture getTextureCFoam               (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(Textures.BlockIcons.CFOAM_FRESH       , mRGBaFoam, mIsGlowing && getState()>0);}
	@Override public ITexture getTextureCFoamDry            (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(Textures.BlockIcons.CFOAM_HARDENED    , mRGBaFoam, mIsGlowing && getState()>0);}
	
	@Override public int getIconIndexSide                   (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
	@Override public int getIconIndexConnected              (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
	
	@Override public String getTileEntityName               () {return "gt.multitileentity.connector.wire.redstone";}
}
