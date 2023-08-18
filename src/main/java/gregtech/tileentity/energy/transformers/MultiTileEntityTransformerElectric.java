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

package gregtech.tileentity.energy.transformers;

import static gregapi.data.CS.*;

import gregapi.data.LH;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.energy.ITileEntityEnergyElectricityAcceptor;
import gregapi.tileentity.energy.TileEntityBase11Bidirectional;
import gregtechCH.data.LH_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MultiTileEntityTransformerElectric extends TileEntityBase11Bidirectional implements ITileEntityEnergyElectricityAcceptor {
	
	@Override
	public void addToolTipsEnergy(List<String> aList, ItemStack aStack, boolean aF3_H) {
		aList.add(LH.Chat.CYAN 	+ LH_CH.get(LH_CH.TRANSFORMER_NORMAL));
		aList.add(LH.Chat.GREEN	+ LH.get(LH.ENERGY_INPUT ) + ": " + LH.Chat.WHITE 						            + mConverter.mEnergyIN .mRec + " " + mConverter.mEnergyIN .mType.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, mConverter.mEnergyIN .mMin, mConverter.mEnergyIN .mMax) + ", "+getLocalisedInputSide()  +")");
		aList.add(LH.Chat.RED	+ LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE +     mConverter.mMultiplier+"x " + mConverter.mEnergyOUT.mRec + " " + mConverter.mEnergyOUT.mType.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, mConverter.mEnergyOUT.mMin, mConverter.mEnergyOUT.mMax) + ", "+getLocalisedOutputSide() +")");
		aList.add(LH.Chat.CYAN 	+ LH_CH.get(LH_CH.TRANSFORMER_REVERSED));
		aList.add(LH.Chat.GREEN	+ LH.get(LH.ENERGY_INPUT ) + ": " + LH.Chat.WHITE 						            + mConverter.mEnergyOUT.mRec + " " + mConverter.mEnergyOUT.mType.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, mConverter.mEnergyOUT.mMin, mConverter.mEnergyIN .mMax) + ", "+getLocalisedOutputSide() +")");
		aList.add(LH.Chat.RED	+ LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE +"1/"+mConverter.mMultiplier+"x " + mConverter.mEnergyIN .mRec + " " + mConverter.mEnergyIN .mType.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, mConverter.mEnergyIN .mMin, mConverter.mEnergyIN .mMax) + ", "+getLocalisedInputSide()  +")");
	}
	@Override
	public void addToolTipsUseful(List<String> aList, ItemStack aStack, boolean aF3_H) {
		aList.add(LH.Chat.GREEN + LH_CH.get("gtch.tooltip.transformer.electric.1"));
	}
	static {
		LH_CH.add("gtch.tooltip.transformer.electric.1", "Have the Same Maximum Input Voltage as Normal mode in Reversed mode");
	}
	
	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		if (!aShouldSideBeRendered[aSide]) return null;
		int aIndex = aSide==mFacing?0:aSide==OPOS[mFacing]?1:2;
		ITexture tTexture = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[aIndex], mRGBa), BlockTextureDefault.get(sOverlays[mActivity.mState][aIndex]));
		if (aSide != mFacing) tTexture = BlockTextureMulti.get(tTexture, BlockTextureDefault.get(mReversed ? Textures.BlockIcons.ARROW_IN : Textures.BlockIcons.ARROW_OUT, UT_CH.Code.getMarkRGB(mRGBa)));
		return tTexture;
	}
	
	// Icons
	public static IIconContainer sColoreds[] = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/colored/front"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/colored/back"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/colored/side"),
	}, sOverlays[][] = new IIconContainer[][] {{
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay/front"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay/back"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay/side"),
	}, {
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay_active/front"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay_active/back"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay_active/side"),
	}, {
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay_blinking/front"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay_blinking/back"),
		new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_electric/overlay_blinking/side"),
	}};
	
	@Override public String getTileEntityName() {return "gt.multitileentity.transformers.transformer_electric";}
}
