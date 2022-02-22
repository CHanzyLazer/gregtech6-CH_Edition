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

package gregapi.render;

import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public interface ITexture {
	public void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds);
	public void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds);
	public void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds);
	public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds);
	public void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds);
	public void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds);
	
	public boolean isValidTexture();
	
	public static class Util {
		public static boolean OPTIFINE_LOADED = F, GT_ALPHA_BLENDING = F, MC_ALPHA_BLENDING = F, IS_RENDERING_ALPHA = F;
		public static float OFFSET_X_POS = 0.0F, OFFSET_X_NEG = 0.0F, OFFSET_Y_POS = 0.0F, OFFSET_Y_NEG = 0.0F, OFFSET_Z_POS = 0.0F, OFFSET_Z_NEG = 0.0F, OFFSET_DEFAULT = 0.0F, OFFSET_ADD = 0.0001F, OFFSET_BREAK = 0.0001F;
		
		public static void startRendering(RenderBlocks aRenderer, Block aBlock, IBlockAccess aWorld, int aX, int aY, int aZ) {
			OFFSET_X_POS = OFFSET_X_NEG = OFFSET_Y_POS = OFFSET_Y_NEG = OFFSET_Z_POS = OFFSET_Z_NEG = aRenderer.hasOverrideBlockTexture()?OFFSET_BREAK:OFFSET_DEFAULT;
			if (aWorld != null) {
				if (aRenderer.hasOverrideBlockTexture()) {
					if (aBlock.getRenderBlockPass() > 0) {
						GL11.glDisable(GL11.GL_BLEND);
					}
				} else {
					if (GT_ALPHA_BLENDING && aBlock.getRenderBlockPass() < 1) {
						IS_RENDERING_ALPHA = T;
						Tessellator.instance.draw();
						Tessellator.instance.startDrawingQuads();
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
					}
				}
			}
		}
		
		public static void endRendering(RenderBlocks aRenderer, Block aBlock, IBlockAccess aWorld, int aX, int aY, int aZ) {
			if (aWorld != null) {
				if (IS_RENDERING_ALPHA) {
					IS_RENDERING_ALPHA = F;
					Tessellator.instance.draw();
					Tessellator.instance.startDrawingQuads();
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				}
			}
		}
		
		//=============================================================================================================
		// prepare and then do Rendering
		//=============================================================================================================
		
		public static boolean renderSide(byte aSide, IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			if (aIcon == null) return F;
			switch(aSide) {
			case SIDE_Y_NEG: return renderYNeg(aIcon, aRGBa, aAllowAlpha, aUseConstantBrightness, aEnableAO, aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
			case SIDE_Y_POS: return renderYPos(aIcon, aRGBa, aAllowAlpha, aUseConstantBrightness, aEnableAO, aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
			case SIDE_Z_NEG: return renderZNeg(aIcon, aRGBa, aAllowAlpha, aUseConstantBrightness, aEnableAO, aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
			case SIDE_Z_POS: return renderZPos(aIcon, aRGBa, aAllowAlpha, aUseConstantBrightness, aEnableAO, aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
			case SIDE_X_NEG: return renderXNeg(aIcon, aRGBa, aAllowAlpha, aUseConstantBrightness, aEnableAO, aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
			case SIDE_X_POS: return renderXPos(aIcon, aRGBa, aAllowAlpha, aUseConstantBrightness, aEnableAO, aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
			default: return F;
			}
		}
		
		/** Side = 5 */
		public static boolean renderXPos(IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			boolean tAmbientOcclusion = aRenderer.enableAO;
			if (!aEnableAO) aRenderer.enableAO = F;
			prepareRenderXPos(aRGBa, aAllowAlpha, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);
			boolean rReturn = doRenderXPos(aIcon, aRenderer, aBlock, aX, aY, aZ, aChangedBlockBounds);
			aRenderer.enableAO = tAmbientOcclusion;
			return rReturn;
		}
		/** Side = 4 */
		public static boolean renderXNeg(IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			boolean tAmbientOcclusion = aRenderer.enableAO;
			if (!aEnableAO) aRenderer.enableAO = F;
			prepareRenderXNeg(aRGBa, aAllowAlpha, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);
			boolean rReturn = doRenderXNeg(aIcon, aRenderer, aBlock, aX, aY, aZ, aChangedBlockBounds);
			aRenderer.enableAO = tAmbientOcclusion;
			return rReturn;
		}
		/** Side = 1 */
		public static boolean renderYPos(IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			boolean tAmbientOcclusion = aRenderer.enableAO;
			if (!aEnableAO) aRenderer.enableAO = F;
			prepareRenderYPos(aRGBa, aAllowAlpha, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);
			boolean rReturn = doRenderYPos(aIcon, aRenderer, aBlock, aX, aY, aZ, aChangedBlockBounds);
			aRenderer.enableAO = tAmbientOcclusion;
			return rReturn;
		}
		/** Side = 0 */
		public static boolean renderYNeg(IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			boolean tAmbientOcclusion = aRenderer.enableAO;
			if (!aEnableAO) aRenderer.enableAO = F;
			prepareRenderYNeg(aRGBa, aAllowAlpha, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);
			boolean rReturn = doRenderYNeg(aIcon, aRenderer, aBlock, aX, aY, aZ, aChangedBlockBounds);
			aRenderer.enableAO = tAmbientOcclusion;
			return rReturn;
		}
		/** Side = 3 */
		public static boolean renderZPos(IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			boolean tAmbientOcclusion = aRenderer.enableAO;
			if (!aEnableAO) aRenderer.enableAO = F;
			prepareRenderZPos(aRGBa, aAllowAlpha, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);
			boolean rReturn = doRenderZPos(aIcon, aRenderer, aBlock, aX, aY, aZ, aChangedBlockBounds);
			aRenderer.enableAO = tAmbientOcclusion;
			return rReturn;
		}
		/** Side = 2 */
		public static boolean renderZNeg(IIcon aIcon, short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, boolean aEnableAO, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
			boolean tAmbientOcclusion = aRenderer.enableAO;
			if (!aEnableAO) aRenderer.enableAO = F;
			prepareRenderZNeg(aRGBa, aAllowAlpha, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);
			boolean rReturn = doRenderZNeg(aIcon, aRenderer, aBlock, aX, aY, aZ, aChangedBlockBounds);
			aRenderer.enableAO = tAmbientOcclusion;
			return rReturn;
		}
		
		//=============================================================================================================
		// prepare Rendering
		//=============================================================================================================
		
		/** Side = 5 */
		public static void prepareRenderXPos(short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRGBa == null) aRGBa = UNCOLOURED;
			Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0]) * 0.6F), (int)(UT.Code.bind8(aRGBa[1]) * 0.6F), (int)(UT.Code.bind8(aRGBa[2]) * 0.6F), aAllowAlpha?UT.Code.bind8(aRGBa[3]):255);
			Tessellator.instance.setBrightness(aBrightness);
			if (aRenderer.enableAO) applyAmbientOcclusionXPos(aRGBa, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);// else {aRenderer.aoLightValueScratchXYNN = aRenderer.aoLightValueScratchXYNP = aRenderer.aoLightValueScratchXYPN = aRenderer.aoLightValueScratchXYPP = aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZNN = aRenderer.aoLightValueScratchXZNP = aRenderer.aoLightValueScratchXZPN = aRenderer.aoLightValueScratchXZPP = aRenderer.aoLightValueScratchYZNN = aRenderer.aoLightValueScratchYZNP = aRenderer.aoLightValueScratchYZPN = aRenderer.aoLightValueScratchYZPP = aRenderer.aoBrightnessXYNN = aRenderer.aoBrightnessXYNP = aRenderer.aoBrightnessXYPN = aRenderer.aoBrightnessXYPP = aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZNN = aRenderer.aoBrightnessXZNP = aRenderer.aoBrightnessXZPN = aRenderer.aoBrightnessXZPP = aRenderer.aoBrightnessYZNN = aRenderer.aoBrightnessYZNP = aRenderer.aoBrightnessYZPN = aRenderer.aoBrightnessYZPP = aRenderer.brightnessBottomLeft = aRenderer.brightnessBottomRight = aRenderer.brightnessTopLeft = aRenderer.brightnessTopRight = aBrightness;}
		}
		/** Side = 4 */
		public static void prepareRenderXNeg(short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRGBa == null) aRGBa = UNCOLOURED;
			Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0]) * 0.6F), (int)(UT.Code.bind8(aRGBa[1]) * 0.6F), (int)(UT.Code.bind8(aRGBa[2]) * 0.6F), aAllowAlpha?UT.Code.bind8(aRGBa[3]):255);
			Tessellator.instance.setBrightness(aBrightness);
			if (aRenderer.enableAO) applyAmbientOcclusionXNeg(aRGBa, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);// else {aRenderer.aoLightValueScratchXYNN = aRenderer.aoLightValueScratchXYNP = aRenderer.aoLightValueScratchXYPN = aRenderer.aoLightValueScratchXYPP = aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZNN = aRenderer.aoLightValueScratchXZNP = aRenderer.aoLightValueScratchXZPN = aRenderer.aoLightValueScratchXZPP = aRenderer.aoLightValueScratchYZNN = aRenderer.aoLightValueScratchYZNP = aRenderer.aoLightValueScratchYZPN = aRenderer.aoLightValueScratchYZPP = aRenderer.aoBrightnessXYNN = aRenderer.aoBrightnessXYNP = aRenderer.aoBrightnessXYPN = aRenderer.aoBrightnessXYPP = aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZNN = aRenderer.aoBrightnessXZNP = aRenderer.aoBrightnessXZPN = aRenderer.aoBrightnessXZPP = aRenderer.aoBrightnessYZNN = aRenderer.aoBrightnessYZNP = aRenderer.aoBrightnessYZPN = aRenderer.aoBrightnessYZPP = aRenderer.brightnessBottomLeft = aRenderer.brightnessBottomRight = aRenderer.brightnessTopLeft = aRenderer.brightnessTopRight = aBrightness;}
		}
		/** Side = 1 */
		public static void prepareRenderYPos(short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRGBa == null) aRGBa = UNCOLOURED;
			Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0]) * 1.0F), (int)(UT.Code.bind8(aRGBa[1]) * 1.0F), (int)(UT.Code.bind8(aRGBa[2]) * 1.0F), aAllowAlpha?UT.Code.bind8(aRGBa[3]):255);
			Tessellator.instance.setBrightness(aBrightness);
			if (aRenderer.enableAO) applyAmbientOcclusionYPos(aRGBa, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);// else {aRenderer.aoLightValueScratchXYNN = aRenderer.aoLightValueScratchXYNP = aRenderer.aoLightValueScratchXYPN = aRenderer.aoLightValueScratchXYPP = aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZNN = aRenderer.aoLightValueScratchXZNP = aRenderer.aoLightValueScratchXZPN = aRenderer.aoLightValueScratchXZPP = aRenderer.aoLightValueScratchYZNN = aRenderer.aoLightValueScratchYZNP = aRenderer.aoLightValueScratchYZPN = aRenderer.aoLightValueScratchYZPP = aRenderer.aoBrightnessXYNN = aRenderer.aoBrightnessXYNP = aRenderer.aoBrightnessXYPN = aRenderer.aoBrightnessXYPP = aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZNN = aRenderer.aoBrightnessXZNP = aRenderer.aoBrightnessXZPN = aRenderer.aoBrightnessXZPP = aRenderer.aoBrightnessYZNN = aRenderer.aoBrightnessYZNP = aRenderer.aoBrightnessYZPN = aRenderer.aoBrightnessYZPP = aRenderer.brightnessBottomLeft = aRenderer.brightnessBottomRight = aRenderer.brightnessTopLeft = aRenderer.brightnessTopRight = aBrightness;}
		}
		/** Side = 0 */
		public static void prepareRenderYNeg(short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRGBa == null) aRGBa = UNCOLOURED;
			Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0]) * 0.5F), (int)(UT.Code.bind8(aRGBa[1]) * 0.5F), (int)(UT.Code.bind8(aRGBa[2]) * 0.5F), aAllowAlpha?UT.Code.bind8(aRGBa[3]):255);
			Tessellator.instance.setBrightness(aBrightness);
			if (aRenderer.enableAO) applyAmbientOcclusionYNeg(aRGBa, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);// else {aRenderer.aoLightValueScratchXYNN = aRenderer.aoLightValueScratchXYNP = aRenderer.aoLightValueScratchXYPN = aRenderer.aoLightValueScratchXYPP = aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZNN = aRenderer.aoLightValueScratchXZNP = aRenderer.aoLightValueScratchXZPN = aRenderer.aoLightValueScratchXZPP = aRenderer.aoLightValueScratchYZNN = aRenderer.aoLightValueScratchYZNP = aRenderer.aoLightValueScratchYZPN = aRenderer.aoLightValueScratchYZPP = aRenderer.aoBrightnessXYNN = aRenderer.aoBrightnessXYNP = aRenderer.aoBrightnessXYPN = aRenderer.aoBrightnessXYPP = aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZNN = aRenderer.aoBrightnessXZNP = aRenderer.aoBrightnessXZPN = aRenderer.aoBrightnessXZPP = aRenderer.aoBrightnessYZNN = aRenderer.aoBrightnessYZNP = aRenderer.aoBrightnessYZPN = aRenderer.aoBrightnessYZPP = aRenderer.brightnessBottomLeft = aRenderer.brightnessBottomRight = aRenderer.brightnessTopLeft = aRenderer.brightnessTopRight = aBrightness;}
		}
		/** Side = 3 */
		public static void prepareRenderZPos(short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRGBa == null) aRGBa = UNCOLOURED;
			Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0]) * 0.8F), (int)(UT.Code.bind8(aRGBa[1]) * 0.8F), (int)(UT.Code.bind8(aRGBa[2]) * 0.8F), aAllowAlpha?UT.Code.bind8(aRGBa[3]):255);
			Tessellator.instance.setBrightness(aBrightness);
			if (aRenderer.enableAO) applyAmbientOcclusionZPos(aRGBa, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);// else {aRenderer.aoLightValueScratchXYNN = aRenderer.aoLightValueScratchXYNP = aRenderer.aoLightValueScratchXYPN = aRenderer.aoLightValueScratchXYPP = aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZNN = aRenderer.aoLightValueScratchXZNP = aRenderer.aoLightValueScratchXZPN = aRenderer.aoLightValueScratchXZPP = aRenderer.aoLightValueScratchYZNN = aRenderer.aoLightValueScratchYZNP = aRenderer.aoLightValueScratchYZPN = aRenderer.aoLightValueScratchYZPP = aRenderer.aoBrightnessXYNN = aRenderer.aoBrightnessXYNP = aRenderer.aoBrightnessXYPN = aRenderer.aoBrightnessXYPP = aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZNN = aRenderer.aoBrightnessXZNP = aRenderer.aoBrightnessXZPN = aRenderer.aoBrightnessXZPP = aRenderer.aoBrightnessYZNN = aRenderer.aoBrightnessYZNP = aRenderer.aoBrightnessYZPN = aRenderer.aoBrightnessYZPP = aRenderer.brightnessBottomLeft = aRenderer.brightnessBottomRight = aRenderer.brightnessTopLeft = aRenderer.brightnessTopRight = aBrightness;}
		}
		/** Side = 2 */
		public static void prepareRenderZNeg(short[] aRGBa, boolean aAllowAlpha, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRGBa == null) aRGBa = UNCOLOURED;
			Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0]) * 0.8F), (int)(UT.Code.bind8(aRGBa[1]) * 0.8F), (int)(UT.Code.bind8(aRGBa[2]) * 0.8F), aAllowAlpha?UT.Code.bind8(aRGBa[3]):255);
			Tessellator.instance.setBrightness(aBrightness);
			if (aRenderer.enableAO) applyAmbientOcclusionZNeg(aRGBa, aUseConstantBrightness, aRenderer, aBlock, aX, aY, aZ, aBrightness);// else {aRenderer.aoLightValueScratchXYNN = aRenderer.aoLightValueScratchXYNP = aRenderer.aoLightValueScratchXYPN = aRenderer.aoLightValueScratchXYPP = aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZNN = aRenderer.aoLightValueScratchXZNP = aRenderer.aoLightValueScratchXZPN = aRenderer.aoLightValueScratchXZPP = aRenderer.aoLightValueScratchYZNN = aRenderer.aoLightValueScratchYZNP = aRenderer.aoLightValueScratchYZPN = aRenderer.aoLightValueScratchYZPP = aRenderer.aoBrightnessXYNN = aRenderer.aoBrightnessXYNP = aRenderer.aoBrightnessXYPN = aRenderer.aoBrightnessXYPP = aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZNN = aRenderer.aoBrightnessXZNP = aRenderer.aoBrightnessXZPN = aRenderer.aoBrightnessXZPP = aRenderer.aoBrightnessYZNN = aRenderer.aoBrightnessYZNP = aRenderer.aoBrightnessYZPN = aRenderer.aoBrightnessYZPP = aRenderer.brightnessBottomLeft = aRenderer.brightnessBottomRight = aRenderer.brightnessTopLeft = aRenderer.brightnessTopRight = aBrightness;}
		}
		
		//=============================================================================================================
		// do Rendering
		//=============================================================================================================
		
		/** Side = 5 */
		public static boolean doRenderXPos(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, boolean aChangedBlockBounds) {
			if (aChangedBlockBounds) {aRenderer.flipTexture = !aRenderer.flipTexture; aRenderer.field_152631_f = T;}
			if (aBlock.getRenderBlockPass() > 0) {
				double tOldValue = aRenderer.renderMaxX;
				aRenderer.renderMaxX += (aRenderer.renderFromInside?-1:+1)*OFFSET_X_POS;
				renderFixedPositiveXFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
				aRenderer.renderMaxX = tOldValue;
				OFFSET_X_POS += OFFSET_ADD;
			} else {
				renderFixedPositiveXFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
			}
			if (aChangedBlockBounds) {aRenderer.flipTexture = !aRenderer.flipTexture; aRenderer.field_152631_f = F;}
			return T;
		}
		/** Side = 4 */
		public static boolean doRenderXNeg(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, boolean aChangedBlockBounds) {
			if (aBlock.getRenderBlockPass() > 0) {
				double tOldValue = aRenderer.renderMinX;
				aRenderer.renderMinX -= (aRenderer.renderFromInside?-1:+1)*OFFSET_X_NEG;
//				aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, aIcon);
				renderFixedNegativeXFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
				aRenderer.renderMinX = tOldValue;
				OFFSET_X_NEG += OFFSET_ADD;
			} else {
//				aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, aIcon);
				renderFixedNegativeXFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
			}
			return T;
		}
		/** Side = 1 */
		public static boolean doRenderYPos(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, boolean aChangedBlockBounds) {
			if (aBlock.getRenderBlockPass() > 0) {
				double tOldValue = aRenderer.renderMaxY;
				aRenderer.renderMaxY += (aRenderer.renderFromInside?-1:+1)*OFFSET_Y_POS;
				renderFixedPositiveYFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
				aRenderer.renderMaxY = tOldValue;
				OFFSET_Y_POS += OFFSET_ADD;
			} else {
				renderFixedPositiveYFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
			}
			return T;
		}
		/** Side = 0 */
		public static boolean doRenderYNeg(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, boolean aChangedBlockBounds) {
			if (aChangedBlockBounds) {aRenderer.flipTexture = !aRenderer.flipTexture; aRenderer.field_152631_f = T;}
			if (aBlock.getRenderBlockPass() > 0) {
				double tOldValue = aRenderer.renderMinY;
				aRenderer.renderMinY -= (aRenderer.renderFromInside?-1:+1)*OFFSET_Y_NEG;
				renderFixedNegativeYFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
				aRenderer.renderMinY = tOldValue;
				OFFSET_Y_NEG += OFFSET_ADD;
			} else {
				renderFixedNegativeYFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
			}
			if (aChangedBlockBounds) {aRenderer.flipTexture = !aRenderer.flipTexture; aRenderer.field_152631_f = F;}
			return T;
		}
		/** Side = 3 */
		public static boolean doRenderZPos(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, boolean aChangedBlockBounds) {
			if (aBlock.getRenderBlockPass() > 0) {
				double tOldValue = aRenderer.renderMaxZ;
				aRenderer.renderMaxZ += (aRenderer.renderFromInside?-1:+1)*OFFSET_Z_POS;
				renderFixedPositiveZFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
				aRenderer.renderMaxZ = tOldValue;
				OFFSET_Z_POS += OFFSET_ADD;
			} else {
				renderFixedPositiveZFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
			}
			return T;
		}
		/** Side = 2 */
		public static boolean doRenderZNeg(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, boolean aChangedBlockBounds) {
			if (aChangedBlockBounds) {aRenderer.flipTexture = !aRenderer.flipTexture; aRenderer.field_152631_f = T;}
			if (aBlock.getRenderBlockPass() > 0) {
				double tOldValue = aRenderer.renderMinZ;
				aRenderer.renderMinZ -= (aRenderer.renderFromInside?-1:+1)*OFFSET_Z_NEG;
				renderFixedNegativeZFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
				aRenderer.renderMinZ = tOldValue;
				OFFSET_Z_NEG += OFFSET_ADD;
			} else {
				renderFixedNegativeZFacing(aIcon, aRenderer, aBlock, aX, aY, aZ);
			}
			if (aChangedBlockBounds) {aRenderer.flipTexture = !aRenderer.flipTexture; aRenderer.field_152631_f = F;}
			return T;
		}

		// GTCH，重写所有面的渲染方法
		public static void renderFixedNegativeYFacing(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
			if (aRenderer.hasOverrideBlockTexture()) aIcon = aRenderer.overrideBlockTexture;

			// 重写这个部分，超过边界的进行平移（永远是拉伸中心，也就是底端材质不变）
			double tRenderMinX = aRenderer.renderMinX;
			double tRenderMaxX = aRenderer.renderMaxX;
			double tRenderMinZ = aRenderer.renderMinZ;
			double tRenderMaxZ = aRenderer.renderMaxZ;
			if (tRenderMaxX - tRenderMinX > 1.0) {
				tRenderMinX = 0.0;
				tRenderMaxX = 1.0;
			}
			if (tRenderMaxZ - tRenderMinZ > 1.0) {
				tRenderMinZ = 0.0;
				tRenderMaxZ = 1.0;
			}
			if (tRenderMaxX > 1.0) {
				tRenderMinX -= tRenderMaxX - 1.0;
				tRenderMaxX = 1.0;
			} else
			if (tRenderMinX < 0.0) {
				tRenderMaxX -= tRenderMinX;
				tRenderMinX = 0.0;
			}
			if (tRenderMaxZ > 1.0) {
				tRenderMinZ -= tRenderMaxZ - 1.0;
				tRenderMaxZ = 1.0;
			} else
			if (tRenderMinZ < 0.0) {
				tRenderMaxZ -= tRenderMinZ;
				tRenderMinZ = 0.0;
			}

			double d3 = aIcon.getInterpolatedU(tRenderMinX * 16.0);
			double d4 = aIcon.getInterpolatedU(tRenderMaxX * 16.0);
			if (aRenderer.field_152631_f) {
				d4 = aIcon.getInterpolatedU((1.0 - tRenderMinX) * 16.0);
				d3 = aIcon.getInterpolatedU((1.0 - tRenderMaxX) * 16.0);
			}
			double d5 = aIcon.getInterpolatedV(tRenderMinZ * 16.0);
			double d6 = aIcon.getInterpolatedV(tRenderMaxZ * 16.0);
			double d7;

			if (aRenderer.flipTexture) {
				d7 = d3;
				d3 = d4;
				d4 = d7;
			}

			d7 = d4;
			double d8 = d3;
			double d9 = d5;
			double d10 = d6;

			if (aRenderer.uvRotateBottom == 2) {
				d3 = aIcon.getInterpolatedU(tRenderMinZ * 16.0);
				d6 = aIcon.getInterpolatedV(16.0 - tRenderMaxX * 16.0);
				d4 = aIcon.getInterpolatedU(tRenderMaxZ * 16.0);
				d5 = aIcon.getInterpolatedV(16.0 - tRenderMinX * 16.0);
				d9 = d5;
				d10 = d6;
				d7 = d3;
				d8 = d4;
				d5 = d6;
				d6 = d9;
			}
			else if (aRenderer.uvRotateBottom == 1) {
				d3 = aIcon.getInterpolatedU(16.0 - tRenderMaxZ * 16.0);
				d6 = aIcon.getInterpolatedV(tRenderMinX * 16.0);
				d4 = aIcon.getInterpolatedU(16.0 - tRenderMinZ * 16.0);
				d5 = aIcon.getInterpolatedV(tRenderMaxX * 16.0);
				d7 = d4;
				d8 = d3;
				d3 = d4;
				d4 = d8;
				d9 = d6;
				d10 = d5;
			}
			else if (aRenderer.uvRotateBottom == 3) {
				d3 = aIcon.getInterpolatedU(16.0 - tRenderMinX * 16.0);
				d4 = aIcon.getInterpolatedU(16.0 - tRenderMaxX * 16.0);
				d5 = aIcon.getInterpolatedV(16.0 - tRenderMinZ * 16.0);
				d6 = aIcon.getInterpolatedV(16.0 - tRenderMaxZ * 16.0);
				d7 = d4;
				d8 = d3;
				d9 = d5;
				d10 = d6;
			}

			double d11 = aX + aRenderer.renderMinX;
			double d12 = aX + aRenderer.renderMaxX;
			double d13 = aY + aRenderer.renderMinY;
			double d14 = aZ + aRenderer.renderMinZ;
			double d15 = aZ + aRenderer.renderMaxZ;
			
			if (aRenderer.renderFromInside) {
				d11 = aX + aRenderer.renderMaxX;
				d12 = aX + aRenderer.renderMinX;
			}
			
			if (aRenderer.enableAO) {
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopLeft);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d4, d6);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomLeft);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d7, d9);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomRight);
				Tessellator.instance.addVertexWithUV(d12, d13, d14, d3, d5);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopRight);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d8, d10);
			} else {
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d4, d6);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d7, d9);
				Tessellator.instance.addVertexWithUV(d12, d13, d14, d3, d5);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d8, d10);
			}
		}

		public static void renderFixedPositiveYFacing(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
			if (aRenderer.hasOverrideBlockTexture()) aIcon = aRenderer.overrideBlockTexture;

			// 重写这个部分，超过边界的进行平移（永远是拉伸中心，也就是底端材质不变）
			double tRenderMinX = aRenderer.renderMinX;
			double tRenderMaxX = aRenderer.renderMaxX;
			double tRenderMinZ = aRenderer.renderMinZ;
			double tRenderMaxZ = aRenderer.renderMaxZ;
			if (tRenderMaxX - tRenderMinX > 1.0) {
				tRenderMinX = 0.0;
				tRenderMaxX = 1.0;
			}
			if (tRenderMaxZ - tRenderMinZ > 1.0) {
				tRenderMinZ = 0.0;
				tRenderMaxZ = 1.0;
			}
			if (tRenderMaxX > 1.0) {
				tRenderMinX -= tRenderMaxX - 1.0;
				tRenderMaxX = 1.0;
			} else
			if (tRenderMinX < 0.0) {
				tRenderMaxX -= tRenderMinX;
				tRenderMinX = 0.0;
			}
			if (tRenderMaxZ > 1.0) {
				tRenderMinZ -= tRenderMaxZ - 1.0;
				tRenderMaxZ = 1.0;
			} else
			if (tRenderMinZ < 0.0) {
				tRenderMaxZ -= tRenderMinZ;
				tRenderMinZ = 0.0;
			}

			double d3 = aIcon.getInterpolatedU(tRenderMinX * 16.0D);
			double d4 = aIcon.getInterpolatedU(tRenderMaxX * 16.0D);
			double d5 = aIcon.getInterpolatedV(tRenderMinZ * 16.0D);
			double d6 = aIcon.getInterpolatedV(tRenderMaxZ * 16.0D);

			double d7 = d4;
			double d8 = d3;
			double d9 = d5;
			double d10 = d6;

			if (aRenderer.uvRotateTop == 1) {
				d3 = aIcon.getInterpolatedU(tRenderMinZ * 16.0D);
				d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxX * 16.0D);
				d4 = aIcon.getInterpolatedU(tRenderMaxZ * 16.0D);
				d6 = aIcon.getInterpolatedV(16.0D - tRenderMinX * 16.0D);
				d9 = d5;
				d10 = d6;
				d7 = d3;
				d8 = d4;
				d5 = d6;
				d6 = d9;
			}
			else if (aRenderer.uvRotateTop == 2) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMaxZ * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMinX * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMinZ * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMaxX * 16.0D);
				d7 = d4;
				d8 = d3;
				d3 = d4;
				d4 = d8;
				d9 = d6;
				d10 = d5;
			}
			else if (aRenderer.uvRotateTop == 3) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMinX * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMaxX * 16.0D);
				d5 = aIcon.getInterpolatedV(16.0D - tRenderMinZ * 16.0D);
				d6 = aIcon.getInterpolatedV(16.0D - tRenderMaxZ * 16.0D);
				d7 = d4;
				d8 = d3;
				d9 = d5;
				d10 = d6;
			}

			double d11 = aX + aRenderer.renderMinX;
			double d12 = aX + aRenderer.renderMaxX;
			double d13 = aY + aRenderer.renderMaxY;
			double d14 = aZ + aRenderer.renderMinZ;
			double d15 = aZ + aRenderer.renderMaxZ;

			if (aRenderer.renderFromInside) {
				d11 = aX + aRenderer.renderMaxX;
				d12 = aX + aRenderer.renderMinX;
			}

			if (aRenderer.enableAO) {
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopLeft);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d4, d6);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomLeft);
				Tessellator.instance.addVertexWithUV(d12, d13, d14, d7, d9);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomRight);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d3, d5);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopRight);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d8, d10);
			} else {
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d4, d6);
				Tessellator.instance.addVertexWithUV(d12, d13, d14, d7, d9);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d3, d5);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d8, d10);
			}
		}
		
		public static void renderFixedNegativeZFacing(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
			if (aRenderer.hasOverrideBlockTexture()) aIcon = aRenderer.overrideBlockTexture;

			// 重写这个部分，超过边界的进行平移（永远是拉伸中心，也就是底端材质不变）
			double tRenderMinX = aRenderer.renderMinX;
			double tRenderMaxX = aRenderer.renderMaxX;
			double tRenderMinY = aRenderer.renderMinY;
			double tRenderMaxY = aRenderer.renderMaxY;
			if (tRenderMaxX - tRenderMinX > 1.0) {
				tRenderMinX = 0.0;
				tRenderMaxX = 1.0;
			}
			if (tRenderMaxY - tRenderMinY > 1.0) {
				tRenderMinY = 0.0;
				tRenderMaxY = 1.0;
			}
			if (tRenderMaxX > 1.0) {
				tRenderMinX -= tRenderMaxX - 1.0;
				tRenderMaxX = 1.0;
			} else
			if (tRenderMinX < 0.0) {
				tRenderMaxX -= tRenderMinX;
				tRenderMinX = 0.0;
			}
			if (tRenderMaxY > 1.0) {
				tRenderMinY -= tRenderMaxY - 1.0;
				tRenderMaxY = 1.0;
			} else
			if (tRenderMinY < 0.0) {
				tRenderMaxY -= tRenderMinY;
				tRenderMinY = 0.0;
			}

			double d3 = aIcon.getInterpolatedU(tRenderMinX * 16.0D);
			double d4 = aIcon.getInterpolatedU(tRenderMaxX * 16.0D);

			if (aRenderer.field_152631_f) {
				d4 = aIcon.getInterpolatedU((1.0D - tRenderMinX) * 16.0D);
				d3 = aIcon.getInterpolatedU((1.0D - tRenderMaxX) * 16.0D);
			}

			double d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxY * 16.0D);
			double d6 = aIcon.getInterpolatedV(16.0D - tRenderMinY * 16.0D);
			double d7;

			if (aRenderer.flipTexture) {
				d7 = d3;
				d3 = d4;
				d4 = d7;
			}

			d7 = d4;
			double d8 = d3;
			double d9 = d5;
			double d10 = d6;

			if (aRenderer.uvRotateEast == 2) {
				d3 = aIcon.getInterpolatedU(tRenderMinY * 16.0D);
				d4 = aIcon.getInterpolatedU(tRenderMaxY * 16.0D);
				d5 = aIcon.getInterpolatedV(16.0D - tRenderMinX * 16.0D);
				d6 = aIcon.getInterpolatedV(16.0D - tRenderMaxX * 16.0D);
				d9 = d5;
				d10 = d6;
				d7 = d3;
				d8 = d4;
				d5 = d6;
				d6 = d9;
			}
			else if (aRenderer.uvRotateEast == 1) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMaxY * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMinY * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMaxX * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMinX * 16.0D);
				d7 = d4;
				d8 = d3;
				d3 = d4;
				d4 = d8;
				d9 = d6;
				d10 = d5;
			}
			else if (aRenderer.uvRotateEast == 3) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMinX * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMaxX * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMaxY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMinY * 16.0D);
				d7 = d4;
				d8 = d3;
				d9 = d5;
				d10 = d6;
			}

			double d11 = aX + aRenderer.renderMinX;
			double d12 = aX + aRenderer.renderMaxX;
			double d13 = aY + aRenderer.renderMinY;
			double d14 = aY + aRenderer.renderMaxY;
			double d15 = aZ + aRenderer.renderMinZ;

			if (aRenderer.renderFromInside) {
				d11 = aX + aRenderer.renderMaxX;
				d12 = aX + aRenderer.renderMinX;
			}

			if (aRenderer.enableAO) {
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopLeft);
				Tessellator.instance.addVertexWithUV(d11, d14, d15, d7, d9);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomLeft);
				Tessellator.instance.addVertexWithUV(d12, d14, d15, d3, d5);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomRight);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d8, d10);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopRight);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d4, d6);
			} else {
				Tessellator.instance.addVertexWithUV(d11, d14, d15, d7, d9);
				Tessellator.instance.addVertexWithUV(d12, d14, d15, d3, d5);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d8, d10);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d4, d6);
			}
		}
		
		public static void renderFixedPositiveZFacing(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
			if (aRenderer.hasOverrideBlockTexture()) aIcon = aRenderer.overrideBlockTexture;

			// 重写这个部分，超过边界的进行平移（永远是拉伸中心，也就是底端材质不变）
			double tRenderMinX = aRenderer.renderMinX;
			double tRenderMaxX = aRenderer.renderMaxX;
			double tRenderMinY = aRenderer.renderMinY;
			double tRenderMaxY = aRenderer.renderMaxY;
			if (tRenderMaxX - tRenderMinX > 1.0) {
				tRenderMinX = 0.0;
				tRenderMaxX = 1.0;
			}
			if (tRenderMaxY - tRenderMinY > 1.0) {
				tRenderMinY = 0.0;
				tRenderMaxY = 1.0;
			}
			if (tRenderMaxX > 1.0) {
				tRenderMinX -= tRenderMaxX - 1.0;
				tRenderMaxX = 1.0;
			} else
			if (tRenderMinX < 0.0) {
				tRenderMaxX -= tRenderMinX;
				tRenderMinX = 0.0;
			}
			if (tRenderMaxY > 1.0) {
				tRenderMinY -= tRenderMaxY - 1.0;
				tRenderMaxY = 1.0;
			} else
			if (tRenderMinY < 0.0) {
				tRenderMaxY -= tRenderMinY;
				tRenderMinY = 0.0;
			}

			double d3 = aIcon.getInterpolatedU(tRenderMinX * 16.0D);
			double d4 = aIcon.getInterpolatedU(tRenderMaxX * 16.0D);
			double d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxY * 16.0D);
			double d6 = aIcon.getInterpolatedV(16.0D - tRenderMinY * 16.0D);
			double d7;

			if (aRenderer.flipTexture) {
				d7 = d3;
				d3 = d4;
				d4 = d7;
			}

			d7 = d4;
			double d8 = d3;
			double d9 = d5;
			double d10 = d6;

			if (aRenderer.uvRotateWest == 1) {
				d3 = aIcon.getInterpolatedU(tRenderMinY * 16.0D);
				d6 = aIcon.getInterpolatedV(16.0D - tRenderMinX * 16.0D);
				d4 = aIcon.getInterpolatedU(tRenderMaxY * 16.0D);
				d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxX * 16.0D);
				d9 = d5;
				d10 = d6;
				d7 = d3;
				d8 = d4;
				d5 = d6;
				d6 = d9;
			}
			else if (aRenderer.uvRotateWest == 2) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMaxY * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMinX * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMinY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMaxX * 16.0D);
				d7 = d4;
				d8 = d3;
				d3 = d4;
				d4 = d8;
				d9 = d6;
				d10 = d5;
			}
			else if (aRenderer.uvRotateWest == 3) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMinX * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMaxX * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMaxY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMinY * 16.0D);
				d7 = d4;
				d8 = d3;
				d9 = d5;
				d10 = d6;
			}

			double d11 = aX + aRenderer.renderMinX;
			double d12 = aX + aRenderer.renderMaxX;
			double d13 = aY + aRenderer.renderMinY;
			double d14 = aY + aRenderer.renderMaxY;
			double d15 = aZ + aRenderer.renderMaxZ;

			if (aRenderer.renderFromInside) {
				d11 = aX + aRenderer.renderMaxX;
				d12 = aX + aRenderer.renderMinX;
			}

			if (aRenderer.enableAO) {
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopLeft);
				Tessellator.instance.addVertexWithUV(d11, d14, d15, d3, d5);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomLeft);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d8, d10);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomRight);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d4, d6);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopRight);
				Tessellator.instance.addVertexWithUV(d12, d14, d15, d7, d9);
			} else {
				Tessellator.instance.addVertexWithUV(d11, d14, d15, d3, d5);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d8, d10);
				Tessellator.instance.addVertexWithUV(d12, d13, d15, d4, d6);
				Tessellator.instance.addVertexWithUV(d12, d14, d15, d7, d9);
			}
		}
		
		public static void renderFixedNegativeXFacing(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
			if (aRenderer.hasOverrideBlockTexture()) aIcon = aRenderer.overrideBlockTexture;

			// 重写这个部分，超过边界的进行平移（永远是拉伸中心，也就是底端材质不变）
			double tRenderMinZ = aRenderer.renderMinZ;
			double tRenderMaxZ = aRenderer.renderMaxZ;
			double tRenderMinY = aRenderer.renderMinY;
			double tRenderMaxY = aRenderer.renderMaxY;
			if (tRenderMaxZ - tRenderMinZ > 1.0) {
				tRenderMinZ = 0.0;
				tRenderMaxZ = 1.0;
			}
			if (tRenderMaxY - tRenderMinY > 1.0) {
				tRenderMinY = 0.0;
				tRenderMaxY = 1.0;
			}
			if (tRenderMaxZ > 1.0) {
				tRenderMinZ -= tRenderMaxZ - 1.0;
				tRenderMaxZ = 1.0;
			} else
			if (tRenderMinZ < 0.0) {
				tRenderMaxZ -= tRenderMinZ;
				tRenderMinZ = 0.0;
			}
			if (tRenderMaxY > 1.0) {
				tRenderMinY -= tRenderMaxY - 1.0;
				tRenderMaxY = 1.0;
			} else
			if (tRenderMinY < 0.0) {
				tRenderMaxY -= tRenderMinY;
				tRenderMinY = 0.0;
			}

			double d3 = aIcon.getInterpolatedU(tRenderMinZ * 16.0D);
			double d4 = aIcon.getInterpolatedU(tRenderMaxZ * 16.0D);
			double d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxY * 16.0D);
			double d6 = aIcon.getInterpolatedV(16.0D - tRenderMinY * 16.0D);
			double d7;

			if (aRenderer.flipTexture) {
				d7 = d3;
				d3 = d4;
				d4 = d7;
			}

			d7 = d4;
			double d8 = d3;
			double d9 = d5;
			double d10 = d6;

			if (aRenderer.uvRotateNorth == 1)
			{
				d3 = aIcon.getInterpolatedU(tRenderMinY * 16.0D);
				d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxZ * 16.0D);
				d4 = aIcon.getInterpolatedU(tRenderMaxY * 16.0D);
				d6 = aIcon.getInterpolatedV(16.0D - tRenderMinZ * 16.0D);
				d9 = d5;
				d10 = d6;
				d7 = d3;
				d8 = d4;
				d5 = d6;
				d6 = d9;
			}
			else if (aRenderer.uvRotateNorth == 2) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMaxY * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMinZ * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMinY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMaxZ * 16.0D);
				d7 = d4;
				d8 = d3;
				d3 = d4;
				d4 = d8;
				d9 = d6;
				d10 = d5;
			}
			else if (aRenderer.uvRotateNorth == 3) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMinZ * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMaxZ * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMaxY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMinY * 16.0D);
				d7 = d4;
				d8 = d3;
				d9 = d5;
				d10 = d6;
			}

			double d11 = aX + aRenderer.renderMinX;
			double d12 = aY + aRenderer.renderMinY;
			double d13 = aY + aRenderer.renderMaxY;
			double d14 = aZ + aRenderer.renderMinZ;
			double d15 = aZ + aRenderer.renderMaxZ;

			if (aRenderer.renderFromInside) {
				d14 = aZ + aRenderer.renderMaxZ;
				d15 = aZ + aRenderer.renderMinZ;
			}

			if (aRenderer.enableAO) {
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopLeft);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d7, d9);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomLeft);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d3, d5);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomRight);
				Tessellator.instance.addVertexWithUV(d11, d12, d14, d8, d10);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopRight);
				Tessellator.instance.addVertexWithUV(d11, d12, d15, d4, d6);
			} else {
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d7, d9);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d3, d5);
				Tessellator.instance.addVertexWithUV(d11, d12, d14, d8, d10);
				Tessellator.instance.addVertexWithUV(d11, d12, d15, d4, d6);
			}
		}
		
		public static void renderFixedPositiveXFacing(IIcon aIcon, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
			if (aRenderer.hasOverrideBlockTexture()) aIcon = aRenderer.overrideBlockTexture;

			// 重写这个部分，超过边界的进行平移（永远是拉伸中心，也就是底端材质不变）
			double tRenderMinZ = aRenderer.renderMinZ;
			double tRenderMaxZ = aRenderer.renderMaxZ;
			double tRenderMinY = aRenderer.renderMinY;
			double tRenderMaxY = aRenderer.renderMaxY;
			if (tRenderMaxZ - tRenderMinZ > 1.0) {
				tRenderMinZ = 0.0;
				tRenderMaxZ = 1.0;
			}
			if (tRenderMaxY - tRenderMinY > 1.0) {
				tRenderMinY = 0.0;
				tRenderMaxY = 1.0;
			}
			if (tRenderMaxZ > 1.0) {
				tRenderMinZ -= tRenderMaxZ - 1.0;
				tRenderMaxZ = 1.0;
			} else
			if (tRenderMinZ < 0.0) {
				tRenderMaxZ -= tRenderMinZ;
				tRenderMinZ = 0.0;
			}
			if (tRenderMaxY > 1.0) {
				tRenderMinY -= tRenderMaxY - 1.0;
				tRenderMaxY = 1.0;
			} else
			if (tRenderMinY < 0.0) {
				tRenderMaxY -= tRenderMinY;
				tRenderMinY = 0.0;
			}

			double d3 = aIcon.getInterpolatedU(tRenderMinZ * 16.0D);
			double d4 = aIcon.getInterpolatedU(tRenderMaxZ * 16.0D);

			if (aRenderer.field_152631_f) {
				d4 = aIcon.getInterpolatedU((1.0D - tRenderMinZ) * 16.0D);
				d3 = aIcon.getInterpolatedU((1.0D - tRenderMaxZ) * 16.0D);
			}

			double d5 = aIcon.getInterpolatedV(16.0D - tRenderMaxY * 16.0D);
			double d6 = aIcon.getInterpolatedV(16.0D - tRenderMinY * 16.0D);
			double d7;

			if (aRenderer.flipTexture) {
				d7 = d3;
				d3 = d4;
				d4 = d7;
			}

			d7 = d4;
			double d8 = d3;
			double d9 = d5;
			double d10 = d6;

			if (aRenderer.uvRotateSouth == 2) {
				d3 = aIcon.getInterpolatedU(tRenderMinY * 16.0D);
				d5 = aIcon.getInterpolatedV(16.0D - tRenderMinZ * 16.0D);
				d4 = aIcon.getInterpolatedU(tRenderMaxY * 16.0D);
				d6 = aIcon.getInterpolatedV(16.0D - tRenderMaxZ * 16.0D);
				d9 = d5;
				d10 = d6;
				d7 = d3;
				d8 = d4;
				d5 = d6;
				d6 = d9;
			}
			else if (aRenderer.uvRotateSouth == 1) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMaxY * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMaxZ * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMinY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMinZ * 16.0D);
				d7 = d4;
				d8 = d3;
				d3 = d4;
				d4 = d8;
				d9 = d6;
				d10 = d5;
			}
			else if (aRenderer.uvRotateSouth == 3) {
				d3 = aIcon.getInterpolatedU(16.0D - tRenderMinZ * 16.0D);
				d4 = aIcon.getInterpolatedU(16.0D - tRenderMaxZ * 16.0D);
				d5 = aIcon.getInterpolatedV(tRenderMaxY * 16.0D);
				d6 = aIcon.getInterpolatedV(tRenderMinY * 16.0D);
				d7 = d4;
				d8 = d3;
				d9 = d5;
				d10 = d6;
			}

			double d11 = aX + aRenderer.renderMaxX;
			double d12 = aY + aRenderer.renderMinY;
			double d13 = aY + aRenderer.renderMaxY;
			double d14 = aZ + aRenderer.renderMinZ;
			double d15 = aZ + aRenderer.renderMaxZ;

			if (aRenderer.renderFromInside) {
				d14 = aZ + aRenderer.renderMaxZ;
				d15 = aZ + aRenderer.renderMinZ;
			}

			if (aRenderer.enableAO) {
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopLeft);
				Tessellator.instance.addVertexWithUV(d11, d12, d15, d8, d10);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomLeft);
				Tessellator.instance.addVertexWithUV(d11, d12, d14, d4, d6);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessBottomRight);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d7, d9);
				Tessellator.instance.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
				Tessellator.instance.setBrightness(aRenderer.brightnessTopRight);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d3, d5);
			} else {
				Tessellator.instance.addVertexWithUV(d11, d12, d15, d8, d10);
				Tessellator.instance.addVertexWithUV(d11, d12, d14, d4, d6);
				Tessellator.instance.addVertexWithUV(d11, d13, d14, d7, d9);
				Tessellator.instance.addVertexWithUV(d11, d13, d15, d3, d5);
			}
		}
		
		//=============================================================================================================
		// apply Ambient Occlusion
		//=============================================================================================================
		
		public static void applyAmbientOcclusionXPos(short[] aRGBa, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRenderer.renderMaxX >= 1.0) aX++;
			float f3 = 0.0F, f4 = 0.0F, f5 = 0.0F, f6 = 0.0F, f7 = 1.0F;
			aRenderer.aoLightValueScratchXYPN = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXZPN = aRenderer.blockAccess.getBlock(aX, aY, aZ - 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXZPP = aRenderer.blockAccess.getBlock(aX, aY, aZ + 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXYPP = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoBrightnessXYPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ);
			aRenderer.aoBrightnessXZPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ - 1);
			aRenderer.aoBrightnessXZPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ + 1);
			aRenderer.aoBrightnessXYPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ);
			boolean flag2 = aRenderer.blockAccess.getBlock(aX + 1, aY + 1, aZ).getCanBlockGrass(), flag3 = aRenderer.blockAccess.getBlock(aX + 1, aY - 1, aZ).getCanBlockGrass(), flag4 = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ + 1).getCanBlockGrass(), flag5 = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ - 1).getCanBlockGrass();
			if (!flag3 && !flag5) {
				aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXZPN;
				aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXZPN;
			} else {
				aRenderer.aoLightValueScratchXYZPNN = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ - 1);
			}
			if (!flag3 && !flag4) {
				aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXZPP;
				aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXZPP;
			} else {
				aRenderer.aoLightValueScratchXYZPNP = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ + 1);
			}
			if (!flag2 && !flag5) {
				aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXZPN;
				aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXZPN;
			} else {
				aRenderer.aoLightValueScratchXYZPPN = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ - 1);
			}
			if (!flag2 && !flag4) {
				aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZPP;
				aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZPP;
			} else {
				aRenderer.aoLightValueScratchXYZPPP = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ + 1);
			}
			if (aRenderer.renderMaxX >= 1.0) aX--;
			if (aRenderer.renderMaxX >= 1.0 || !WD.visOpq(aRenderer.blockAccess.getBlock(aX + 1, aY, aZ))) aBrightness = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ);
			f7 = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ).getAmbientOcclusionLightValue();
			f3 = (aRenderer.aoLightValueScratchXYPN + aRenderer.aoLightValueScratchXYZPNP + f7 + aRenderer.aoLightValueScratchXZPP) / 4.0F;
			f4 = (aRenderer.aoLightValueScratchXYZPNN + aRenderer.aoLightValueScratchXYPN + aRenderer.aoLightValueScratchXZPN + f7) / 4.0F;
			f5 = (aRenderer.aoLightValueScratchXZPN + f7 + aRenderer.aoLightValueScratchXYZPPN + aRenderer.aoLightValueScratchXYPP) / 4.0F;
			f6 = (f7 + aRenderer.aoLightValueScratchXZPP + aRenderer.aoLightValueScratchXYPP + aRenderer.aoLightValueScratchXYZPPP) / 4.0F;
			aRenderer.brightnessTopLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYPN, aRenderer.aoBrightnessXYZPNP, aRenderer.aoBrightnessXZPP, aBrightness);
			aRenderer.brightnessTopRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXZPP, aRenderer.aoBrightnessXYPP, aRenderer.aoBrightnessXYZPPP, aBrightness);
			aRenderer.brightnessBottomRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXZPN, aRenderer.aoBrightnessXYZPPN, aRenderer.aoBrightnessXYPP, aBrightness);
			aRenderer.brightnessBottomLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYZPNN, aRenderer.aoBrightnessXYPN, aRenderer.aoBrightnessXZPN, aBrightness);
			aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight = aRenderer.colorRedTopRight = (UT.Code.bind8(aRGBa[0]) / 256.0F) * 0.6F;
			aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft = aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = (UT.Code.bind8(aRGBa[1]) / 256.0F) * 0.6F;
			aRenderer.colorBlueTopLeft = aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = (UT.Code.bind8(aRGBa[2]) / 256.0F) * 0.6F;
			aRenderer.colorRedTopLeft *= f3;
			aRenderer.colorGreenTopLeft *= f3;
			aRenderer.colorBlueTopLeft *= f3;
			aRenderer.colorRedBottomLeft *= f4;
			aRenderer.colorGreenBottomLeft *= f4;
			aRenderer.colorBlueBottomLeft *= f4;
			aRenderer.colorRedBottomRight *= f5;
			aRenderer.colorGreenBottomRight *= f5;
			aRenderer.colorBlueBottomRight *= f5;
			aRenderer.colorRedTopRight *= f6;
			aRenderer.colorGreenTopRight *= f6;
			aRenderer.colorBlueTopRight *= f6;
		}
		
		public static void applyAmbientOcclusionXNeg(short[] aRGBa, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRenderer.renderMinX <= 0.0) aX--;
			float f3 = 0.0F, f4 = 0.0F, f5 = 0.0F, f6 = 0.0F, f7 = 1.0F;
			aRenderer.aoLightValueScratchXYNN = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXZNN = aRenderer.blockAccess.getBlock(aX, aY, aZ - 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXZNP = aRenderer.blockAccess.getBlock(aX, aY, aZ + 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXYNP = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoBrightnessXYNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ);
			aRenderer.aoBrightnessXZNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ - 1);
			aRenderer.aoBrightnessXZNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ + 1);
			aRenderer.aoBrightnessXYNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ);
			boolean flag2 = aRenderer.blockAccess.getBlock(aX - 1, aY + 1, aZ).getCanBlockGrass(), flag3 = aRenderer.blockAccess.getBlock(aX - 1, aY - 1, aZ).getCanBlockGrass(), flag4 = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ - 1).getCanBlockGrass(), flag5 = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ + 1).getCanBlockGrass();
			if (!flag4 && !flag3) {
				aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXZNN;
				aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXZNN;
			} else {
				aRenderer.aoLightValueScratchXYZNNN = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ - 1);
			}
			if (!flag5 && !flag3) {
				aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXZNP;
				aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXZNP;
			} else {
				aRenderer.aoLightValueScratchXYZNNP = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ + 1);
			}
			if (!flag4 && !flag2) {
				aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXZNN;
				aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXZNN;
			} else {
				aRenderer.aoLightValueScratchXYZNPN = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ - 1);
			}
			if (!flag5 && !flag2) {
				aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXZNP;
				aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXZNP;
			} else {
				aRenderer.aoLightValueScratchXYZNPP = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ + 1);
			}
			if (aRenderer.renderMinX <= 0.0) aX++;
			if (aRenderer.renderMinX <= 0.0 || !WD.visOpq(aRenderer.blockAccess.getBlock(aX - 1, aY, aZ))) aBrightness = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ);
			f7 = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ).getAmbientOcclusionLightValue();
			f6 = (aRenderer.aoLightValueScratchXYNN + aRenderer.aoLightValueScratchXYZNNP + f7 + aRenderer.aoLightValueScratchXZNP) / 4.0F;
			f3 = (f7 + aRenderer.aoLightValueScratchXZNP + aRenderer.aoLightValueScratchXYNP + aRenderer.aoLightValueScratchXYZNPP) / 4.0F;
			f4 = (aRenderer.aoLightValueScratchXZNN + f7 + aRenderer.aoLightValueScratchXYZNPN + aRenderer.aoLightValueScratchXYNP) / 4.0F;
			f5 = (aRenderer.aoLightValueScratchXYZNNN + aRenderer.aoLightValueScratchXYNN + aRenderer.aoLightValueScratchXZNN + f7) / 4.0F;
			aRenderer.brightnessTopRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYNN, aRenderer.aoBrightnessXYZNNP, aRenderer.aoBrightnessXZNP, aBrightness);
			aRenderer.brightnessTopLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXZNP, aRenderer.aoBrightnessXYNP, aRenderer.aoBrightnessXYZNPP, aBrightness);
			aRenderer.brightnessBottomLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXZNN, aRenderer.aoBrightnessXYZNPN, aRenderer.aoBrightnessXYNP, aBrightness);
			aRenderer.brightnessBottomRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYZNNN, aRenderer.aoBrightnessXYNN, aRenderer.aoBrightnessXZNN, aBrightness);
			aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight = aRenderer.colorRedTopRight = (UT.Code.bind8(aRGBa[0]) / 256.0F) * 0.6F;
			aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft = aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = (UT.Code.bind8(aRGBa[1]) / 256.0F) * 0.6F;
			aRenderer.colorBlueTopLeft = aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = (UT.Code.bind8(aRGBa[2]) / 256.0F) * 0.6F;
			aRenderer.colorRedTopLeft *= f3;
			aRenderer.colorGreenTopLeft *= f3;
			aRenderer.colorBlueTopLeft *= f3;
			aRenderer.colorRedBottomLeft *= f4;
			aRenderer.colorGreenBottomLeft *= f4;
			aRenderer.colorBlueBottomLeft *= f4;
			aRenderer.colorRedBottomRight *= f5;
			aRenderer.colorGreenBottomRight *= f5;
			aRenderer.colorBlueBottomRight *= f5;
			aRenderer.colorRedTopRight *= f6;
			aRenderer.colorGreenTopRight *= f6;
			aRenderer.colorBlueTopRight *= f6;
		}
		
		public static void applyAmbientOcclusionYPos(short[] aRGBa, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRenderer.renderMaxY >= 1.0) aY++;
			float f3 = 0.0F, f4 = 0.0F, f5 = 0.0F, f6 = 0.0F, f7 = 1.0F;
			aRenderer.aoBrightnessXYNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ);
			aRenderer.aoBrightnessXYPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ);
			aRenderer.aoBrightnessYZPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ - 1);
			aRenderer.aoBrightnessYZPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ + 1);
			aRenderer.aoLightValueScratchXYNP = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXYPP = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZPN = aRenderer.blockAccess.getBlock(aX, aY, aZ - 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZPP = aRenderer.blockAccess.getBlock(aX, aY, aZ + 1).getAmbientOcclusionLightValue();
			boolean flag2 = aRenderer.blockAccess.getBlock(aX + 1, aY + 1, aZ).getCanBlockGrass(), flag3 = aRenderer.blockAccess.getBlock(aX - 1, aY + 1, aZ).getCanBlockGrass(), flag4 = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ + 1).getCanBlockGrass(), flag5 = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ - 1).getCanBlockGrass();
			if (!flag5 && !flag3) {
				aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXYNP;
				aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXYNP;
			} else {
				aRenderer.aoLightValueScratchXYZNPN = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ - 1);
			}
			if (!flag5 && !flag2) {
				aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXYPP;
				aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXYPP;
			} else {
				aRenderer.aoLightValueScratchXYZPPN = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ - 1);
			}
			if (!flag4 && !flag3) {
				aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXYNP;
				aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXYNP;
			} else {
				aRenderer.aoLightValueScratchXYZNPP = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ + 1);
			}
			if (!flag4 && !flag2) {
				aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXYPP;
				aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXYPP;
			} else {
				aRenderer.aoLightValueScratchXYZPPP = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ + 1);
			}
			if (aRenderer.renderMaxY >= 1.0) aY--;
			if (aRenderer.renderMaxY >= 1.0 || !WD.visOpq(aRenderer.blockAccess.getBlock(aX, aY + 1, aZ))) aBrightness = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ);
			f7 = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ).getAmbientOcclusionLightValue();
			f6 = (aRenderer.aoLightValueScratchXYZNPP + aRenderer.aoLightValueScratchXYNP + aRenderer.aoLightValueScratchYZPP + f7) / 4.0F;
			f3 = (aRenderer.aoLightValueScratchYZPP + f7 + aRenderer.aoLightValueScratchXYZPPP + aRenderer.aoLightValueScratchXYPP) / 4.0F;
			f4 = (f7 + aRenderer.aoLightValueScratchYZPN + aRenderer.aoLightValueScratchXYPP + aRenderer.aoLightValueScratchXYZPPN) / 4.0F;
			f5 = (aRenderer.aoLightValueScratchXYNP + aRenderer.aoLightValueScratchXYZNPN + f7 + aRenderer.aoLightValueScratchYZPN) / 4.0F;
			aRenderer.brightnessTopRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYZNPP, aRenderer.aoBrightnessXYNP, aRenderer.aoBrightnessYZPP, aBrightness);
			aRenderer.brightnessTopLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZPP, aRenderer.aoBrightnessXYZPPP, aRenderer.aoBrightnessXYPP, aBrightness);
			aRenderer.brightnessBottomLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZPN, aRenderer.aoBrightnessXYPP, aRenderer.aoBrightnessXYZPPN, aBrightness);
			aRenderer.brightnessBottomRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYNP, aRenderer.aoBrightnessXYZNPN, aRenderer.aoBrightnessYZPN, aBrightness);
			aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight = aRenderer.colorRedTopRight = (UT.Code.bind8(aRGBa[0]) / 256.0F);
			aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft = aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = (UT.Code.bind8(aRGBa[1]) / 256.0F);
			aRenderer.colorBlueTopLeft = aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = (UT.Code.bind8(aRGBa[2]) / 256.0F);
			aRenderer.colorRedTopLeft *= f3;
			aRenderer.colorGreenTopLeft *= f3;
			aRenderer.colorBlueTopLeft *= f3;
			aRenderer.colorRedBottomLeft *= f4;
			aRenderer.colorGreenBottomLeft *= f4;
			aRenderer.colorBlueBottomLeft *= f4;
			aRenderer.colorRedBottomRight *= f5;
			aRenderer.colorGreenBottomRight *= f5;
			aRenderer.colorBlueBottomRight *= f5;
			aRenderer.colorRedTopRight *= f6;
			aRenderer.colorGreenTopRight *= f6;
			aRenderer.colorBlueTopRight *= f6;
		}
		
		public static void applyAmbientOcclusionYNeg(short[] aRGBa, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRenderer.renderMinY <= 0.0) aY--;
			float f3 = 0.0F, f4 = 0.0F, f5 = 0.0F, f6 = 0.0F, f7 = 1.0F;
			aRenderer.aoBrightnessXYNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ);
			aRenderer.aoBrightnessYZNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ - 1);
			aRenderer.aoBrightnessYZNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ + 1);
			aRenderer.aoBrightnessXYPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ);
			aRenderer.aoLightValueScratchXYNN = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZNN = aRenderer.blockAccess.getBlock(aX, aY, aZ - 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZNP = aRenderer.blockAccess.getBlock(aX, aY, aZ + 1).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXYPN = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ).getAmbientOcclusionLightValue();
			boolean flag2 = aRenderer.blockAccess.getBlock(aX + 1, aY - 1, aZ).getCanBlockGrass(), flag3 = aRenderer.blockAccess.getBlock(aX - 1, aY - 1, aZ).getCanBlockGrass(), flag4 = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ + 1).getCanBlockGrass(), flag5 = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ - 1).getCanBlockGrass();
			if (!flag5 && !flag3) {
				aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXYNN;
				aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXYNN;
			} else {
				aRenderer.aoLightValueScratchXYZNNN = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ - 1);
			}
			if (!flag4 && !flag3) {
				aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXYNN;
				aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXYNN;
			} else {
				aRenderer.aoLightValueScratchXYZNNP = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ + 1);
			}
			if (!flag5 && !flag2) {
				aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXYPN;
				aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXYPN;
			} else {
				aRenderer.aoLightValueScratchXYZPNN = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ - 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ - 1);
			}
			if (!flag4 && !flag2) {
				aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXYPN;
				aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXYPN;
			} else {
				aRenderer.aoLightValueScratchXYZPNP = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ + 1).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ + 1);
			}
			if (aRenderer.renderMinY <= 0.0) aY++;
			if (aRenderer.renderMinY <= 0.0 || !WD.visOpq(aRenderer.blockAccess.getBlock(aX, aY - 1, aZ))) aBrightness = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ);
			f7 = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ).getAmbientOcclusionLightValue();
			f3 = (aRenderer.aoLightValueScratchXYZNNP + aRenderer.aoLightValueScratchXYNN + aRenderer.aoLightValueScratchYZNP + f7) / 4.0F;
			f6 = (aRenderer.aoLightValueScratchYZNP + f7 + aRenderer.aoLightValueScratchXYZPNP + aRenderer.aoLightValueScratchXYPN) / 4.0F;
			f5 = (f7 + aRenderer.aoLightValueScratchYZNN + aRenderer.aoLightValueScratchXYPN + aRenderer.aoLightValueScratchXYZPNN) / 4.0F;
			f4 = (aRenderer.aoLightValueScratchXYNN + aRenderer.aoLightValueScratchXYZNNN + f7 + aRenderer.aoLightValueScratchYZNN) / 4.0F;
			aRenderer.brightnessTopLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYZNNP, aRenderer.aoBrightnessXYNN, aRenderer.aoBrightnessYZNP, aBrightness);
			aRenderer.brightnessTopRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZNP, aRenderer.aoBrightnessXYZPNP, aRenderer.aoBrightnessXYPN, aBrightness);
			aRenderer.brightnessBottomRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZNN, aRenderer.aoBrightnessXYPN, aRenderer.aoBrightnessXYZPNN, aBrightness);
			aRenderer.brightnessBottomLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYNN, aRenderer.aoBrightnessXYZNNN, aRenderer.aoBrightnessYZNN, aBrightness);
			aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight = aRenderer.colorRedTopRight = (UT.Code.bind8(aRGBa[0]) / 256.0F) * 0.5F;
			aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft = aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = (UT.Code.bind8(aRGBa[1]) / 256.0F) * 0.5F;
			aRenderer.colorBlueTopLeft = aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = (UT.Code.bind8(aRGBa[2]) / 256.0F) * 0.5F;
			aRenderer.colorRedTopLeft *= f3;
			aRenderer.colorGreenTopLeft *= f3;
			aRenderer.colorBlueTopLeft *= f3;
			aRenderer.colorRedBottomLeft *= f4;
			aRenderer.colorGreenBottomLeft *= f4;
			aRenderer.colorBlueBottomLeft *= f4;
			aRenderer.colorRedBottomRight *= f5;
			aRenderer.colorGreenBottomRight *= f5;
			aRenderer.colorBlueBottomRight *= f5;
			aRenderer.colorRedTopRight *= f6;
			aRenderer.colorGreenTopRight *= f6;
			aRenderer.colorBlueTopRight *= f6;
		}
		
		public static void applyAmbientOcclusionZPos(short[] aRGBa, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRenderer.renderMaxZ >= 1.0) aZ++;
			float f3 = 0.0F, f4 = 0.0F, f5 = 0.0F, f6 = 0.0F, f7 = 1.0F;
			aRenderer.aoLightValueScratchXZNP = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXZPP = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZNP = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZPP = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoBrightnessXZNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ);
			aRenderer.aoBrightnessXZPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ);
			aRenderer.aoBrightnessYZNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ);
			aRenderer.aoBrightnessYZPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ);
			boolean flag2 = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ + 1).getCanBlockGrass(), flag3 = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ + 1).getCanBlockGrass(), flag4 = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ + 1).getCanBlockGrass(), flag5 = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ + 1).getCanBlockGrass();
			if (!flag3 && !flag5) {
				aRenderer.aoLightValueScratchXYZNNP = aRenderer.aoLightValueScratchXZNP;
				aRenderer.aoBrightnessXYZNNP = aRenderer.aoBrightnessXZNP;
			} else {
				aRenderer.aoLightValueScratchXYZNNP = aRenderer.blockAccess.getBlock(aX - 1, aY - 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY - 1, aZ);
			}
			if (!flag3 && !flag4) {
				aRenderer.aoLightValueScratchXYZNPP = aRenderer.aoLightValueScratchXZNP;
				aRenderer.aoBrightnessXYZNPP = aRenderer.aoBrightnessXZNP;
			} else {
				aRenderer.aoLightValueScratchXYZNPP = aRenderer.blockAccess.getBlock(aX - 1, aY + 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY + 1, aZ);
			}
			if (!flag2 && !flag5) {
				aRenderer.aoLightValueScratchXYZPNP = aRenderer.aoLightValueScratchXZPP;
				aRenderer.aoBrightnessXYZPNP = aRenderer.aoBrightnessXZPP;
			} else {
				aRenderer.aoLightValueScratchXYZPNP = aRenderer.blockAccess.getBlock(aX + 1, aY - 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPNP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY - 1, aZ);
			}
			if (!flag2 && !flag4) {
				aRenderer.aoLightValueScratchXYZPPP = aRenderer.aoLightValueScratchXZPP;
				aRenderer.aoBrightnessXYZPPP = aRenderer.aoBrightnessXZPP;
			} else {
				aRenderer.aoLightValueScratchXYZPPP = aRenderer.blockAccess.getBlock(aX + 1, aY + 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPPP = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY + 1, aZ);
			}
			if (aRenderer.renderMaxZ >= 1.0) aZ--;
			if (aRenderer.renderMaxZ >= 1.0 || !WD.visOpq(aRenderer.blockAccess.getBlock(aX, aY, aZ + 1))) aBrightness = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ + 1);
			f7 = aRenderer.blockAccess.getBlock(aX, aY, aZ + 1).getAmbientOcclusionLightValue();
			f3 = (aRenderer.aoLightValueScratchXZNP + aRenderer.aoLightValueScratchXYZNPP + f7 + aRenderer.aoLightValueScratchYZPP) / 4.0F;
			f6 = (f7 + aRenderer.aoLightValueScratchYZPP + aRenderer.aoLightValueScratchXZPP + aRenderer.aoLightValueScratchXYZPPP) / 4.0F;
			f5 = (aRenderer.aoLightValueScratchYZNP + f7 + aRenderer.aoLightValueScratchXYZPNP + aRenderer.aoLightValueScratchXZPP) / 4.0F;
			f4 = (aRenderer.aoLightValueScratchXYZNNP + aRenderer.aoLightValueScratchXZNP + aRenderer.aoLightValueScratchYZNP + f7) / 4.0F;
			aRenderer.brightnessTopLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXZNP, aRenderer.aoBrightnessXYZNPP, aRenderer.aoBrightnessYZPP, aBrightness);
			aRenderer.brightnessTopRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZPP, aRenderer.aoBrightnessXZPP, aRenderer.aoBrightnessXYZPPP, aBrightness);
			aRenderer.brightnessBottomRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZNP, aRenderer.aoBrightnessXYZPNP, aRenderer.aoBrightnessXZPP, aBrightness);
			aRenderer.brightnessBottomLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYZNNP, aRenderer.aoBrightnessXZNP, aRenderer.aoBrightnessYZNP, aBrightness);
			aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight = aRenderer.colorRedTopRight = (UT.Code.bind8(aRGBa[0]) / 256.0F) * 0.8F;
			aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft = aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = (UT.Code.bind8(aRGBa[1]) / 256.0F) * 0.8F;
			aRenderer.colorBlueTopLeft = aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = (UT.Code.bind8(aRGBa[2]) / 256.0F) * 0.8F;
			aRenderer.colorRedTopLeft *= f3;
			aRenderer.colorGreenTopLeft *= f3;
			aRenderer.colorBlueTopLeft *= f3;
			aRenderer.colorRedBottomLeft *= f4;
			aRenderer.colorGreenBottomLeft *= f4;
			aRenderer.colorBlueBottomLeft *= f4;
			aRenderer.colorRedBottomRight *= f5;
			aRenderer.colorGreenBottomRight *= f5;
			aRenderer.colorBlueBottomRight *= f5;
			aRenderer.colorRedTopRight *= f6;
			aRenderer.colorGreenTopRight *= f6;
			aRenderer.colorBlueTopRight *= f6;
		}
		
		public static void applyAmbientOcclusionZNeg(short[] aRGBa, boolean aUseConstantBrightness, RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness) {
			if (aRenderer.renderMinZ <= 0.0)  aZ--;
			float f3 = 0.0F, f4 = 0.0F, f5 = 0.0F, f6 = 0.0F, f7 = 1.0F;
			aRenderer.aoLightValueScratchXZNN = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZNN = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchYZPN = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoLightValueScratchXZPN = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ).getAmbientOcclusionLightValue();
			aRenderer.aoBrightnessXZNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY, aZ);
			aRenderer.aoBrightnessYZNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY - 1, aZ);
			aRenderer.aoBrightnessYZPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY + 1, aZ);
			aRenderer.aoBrightnessXZPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY, aZ);
			boolean flag2 = aRenderer.blockAccess.getBlock(aX + 1, aY, aZ - 1).getCanBlockGrass(), flag3 = aRenderer.blockAccess.getBlock(aX - 1, aY, aZ - 1).getCanBlockGrass(), flag4 = aRenderer.blockAccess.getBlock(aX, aY + 1, aZ - 1).getCanBlockGrass(), flag5 = aRenderer.blockAccess.getBlock(aX, aY - 1, aZ - 1).getCanBlockGrass();
			if (!flag3 && !flag5) {
				aRenderer.aoLightValueScratchXYZNNN = aRenderer.aoLightValueScratchXZNN;
				aRenderer.aoBrightnessXYZNNN = aRenderer.aoBrightnessXZNN;
			} else {
				aRenderer.aoLightValueScratchXYZNNN = aRenderer.blockAccess.getBlock(aX - 1, aY - 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY - 1, aZ);
			}
			if (!flag3 && !flag4) {
				aRenderer.aoLightValueScratchXYZNPN = aRenderer.aoLightValueScratchXZNN;
				aRenderer.aoBrightnessXYZNPN = aRenderer.aoBrightnessXZNN;
			} else {
				aRenderer.aoLightValueScratchXYZNPN = aRenderer.blockAccess.getBlock(aX - 1, aY + 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZNPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX - 1, aY + 1, aZ);
			}
			if (!flag2 && !flag5) {
				aRenderer.aoLightValueScratchXYZPNN = aRenderer.aoLightValueScratchXZPN;
				aRenderer.aoBrightnessXYZPNN = aRenderer.aoBrightnessXZPN;
			} else {
				aRenderer.aoLightValueScratchXYZPNN = aRenderer.blockAccess.getBlock(aX + 1, aY - 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPNN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY - 1, aZ);
			}
			if (!flag2 && !flag4) {
				aRenderer.aoLightValueScratchXYZPPN = aRenderer.aoLightValueScratchXZPN;
				aRenderer.aoBrightnessXYZPPN = aRenderer.aoBrightnessXZPN;
			} else {
				aRenderer.aoLightValueScratchXYZPPN = aRenderer.blockAccess.getBlock(aX + 1, aY + 1, aZ).getAmbientOcclusionLightValue();
				aRenderer.aoBrightnessXYZPPN = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX + 1, aY + 1, aZ);
			}
			if (aRenderer.renderMinZ <= 0.0)  aZ++;
			if (aRenderer.renderMinZ <= 0.0 || !WD.visOpq(aRenderer.blockAccess.getBlock(aX, aY, aZ - 1))) aBrightness = aUseConstantBrightness?aBrightness:aBlock.getMixedBrightnessForBlock(aRenderer.blockAccess, aX, aY, aZ - 1);
			f7 = aRenderer.blockAccess.getBlock(aX, aY, aZ - 1).getAmbientOcclusionLightValue();
			f3 = (aRenderer.aoLightValueScratchXZNN + aRenderer.aoLightValueScratchXYZNPN + f7 + aRenderer.aoLightValueScratchYZPN) / 4.0F;
			f4 = (f7 + aRenderer.aoLightValueScratchYZPN + aRenderer.aoLightValueScratchXZPN + aRenderer.aoLightValueScratchXYZPPN) / 4.0F;
			f5 = (aRenderer.aoLightValueScratchYZNN + f7 + aRenderer.aoLightValueScratchXYZPNN + aRenderer.aoLightValueScratchXZPN) / 4.0F;
			f6 = (aRenderer.aoLightValueScratchXYZNNN + aRenderer.aoLightValueScratchXZNN + aRenderer.aoLightValueScratchYZNN + f7) / 4.0F;
			aRenderer.brightnessTopLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessXZNN, aRenderer.aoBrightnessXYZNPN, aRenderer.aoBrightnessYZPN, aBrightness);
			aRenderer.brightnessBottomLeft = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZPN, aRenderer.aoBrightnessXZPN, aRenderer.aoBrightnessXYZPPN, aBrightness);
			aRenderer.brightnessBottomRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessYZNN, aRenderer.aoBrightnessXYZPNN, aRenderer.aoBrightnessXZPN, aBrightness);
			aRenderer.brightnessTopRight = aRenderer.getAoBrightness(aRenderer.aoBrightnessXYZNNN, aRenderer.aoBrightnessXZNN, aRenderer.aoBrightnessYZNN, aBrightness);
			aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight = aRenderer.colorRedTopRight = (UT.Code.bind8(aRGBa[0]) / 256.0F) * 0.8F;
			aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft = aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = (UT.Code.bind8(aRGBa[1]) / 256.0F) * 0.8F;
			aRenderer.colorBlueTopLeft = aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = (UT.Code.bind8(aRGBa[2]) / 256.0F) * 0.8F;
			aRenderer.colorRedTopLeft *= f3;
			aRenderer.colorGreenTopLeft *= f3;
			aRenderer.colorBlueTopLeft *= f3;
			aRenderer.colorRedBottomLeft *= f4;
			aRenderer.colorGreenBottomLeft *= f4;
			aRenderer.colorBlueBottomLeft *= f4;
			aRenderer.colorRedBottomRight *= f5;
			aRenderer.colorGreenBottomRight *= f5;
			aRenderer.colorBlueBottomRight *= f5;
			aRenderer.colorRedTopRight *= f6;
			aRenderer.colorGreenTopRight *= f6;
			aRenderer.colorBlueTopRight *= f6;
		}
	}
}
