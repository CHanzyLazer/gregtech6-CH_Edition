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
import gregtechCH.util.UT_CH;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityWireRedstone extends MultiTileEntityWireRedstoneInsulated implements IMTE_GetLightValue {
	public byte mState = 0;
	public int mRGBaRedstoneON = UNCOLORED;
	public int mRGBaRedstoneOFF = UNCOLORED;
	public final static float ON_RATIO = 0.12F, OFF_RATIO = -0.16F;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_STATE)) mState = aNBT.getByte(NBT_STATE);
		mRGBaRedstoneON = UT_CH.Code.getBrighterRGB(mRGBa, ON_RATIO);
		mRGBaRedstoneOFF = UT_CH.Code.getBrighterRGB(mRGBa, OFF_RATIO);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		if (mState != 0) aNBT.setByte(NBT_STATE, mState);
	}
	
	@Override
	public boolean onTickCheck(long aTimer) {
		byte tOldState = mState;
		mState = UT.Code.bind4(UT.Code.divup(mRedstone, MAX_RANGE));
		if (tOldState != mState) {
			if (mIsGlowing) updateLightValue();
			return T;
		}
		return super.onTickCheck(aTimer);
	}

	// 在这里进行更新颜色
	@Override
	public void onPaintChangeClient(int aPreviousRGBaPaint) {
		super.onPaintChangeClient(aPreviousRGBaPaint);
		mRGBaRedstoneON = UT_CH.Code.getBrighterRGB(mRGBa, ON_RATIO);
		mRGBaRedstoneOFF = UT_CH.Code.getBrighterRGB(mRGBa, OFF_RATIO);
	}
	
	@Override
	public void setVisualData(byte aData) {
		if (aData != mState) {
			mState = aData;
			if (mIsGlowing) updateLightValue();
		}
	}
	
	@Override
	public byte getVisualData() {
		return mState;
	}
	
	@Override
	public int getLightOpacity() {
		return mIsGlowing ? LIGHT_OPACITY_NONE : super.getLightOpacity();
	}
	
	@Override public int getLightValue                      () {return mIsGlowing ? mState : 0;}

	// GTCH, 还原为原本材料的颜色
	@Override public int getBottomRGB() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}
	
	@Override public ITexture getTextureSide                (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mMaterial, getIconIndexSide       (aSide, aConnections, aDiameter, aRenderPass), mState > 0, worldObj==null?mRGBa:(mState>0?mRGBaRedstoneON:mRGBaRedstoneOFF));}
	@Override public ITexture getTextureConnected           (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(mMaterial, getIconIndexConnected  (aSide, aConnections, aDiameter, aRenderPass), mState > 0, worldObj==null?mRGBa:(mState>0?mRGBaRedstoneON:mRGBaRedstoneOFF));}
	@Override public ITexture getTextureCFoam               (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(Textures.BlockIcons.CFOAM_FRESH       , mRGBaFoam, mIsGlowing && mState > 0);}
	@Override public ITexture getTextureCFoamDry            (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return BlockTextureDefault.get(Textures.BlockIcons.CFOAM_HARDENED    , mRGBaFoam, mIsGlowing && mState > 0);}
	
	@Override public int getIconIndexSide                   (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
	@Override public int getIconIndexConnected              (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
	
	@Override public String getTileEntityName               () {return "gt.multitileentity.connector.wire.redstone";}
}
