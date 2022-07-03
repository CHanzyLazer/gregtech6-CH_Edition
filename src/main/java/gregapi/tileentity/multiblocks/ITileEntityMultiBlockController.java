/**
 * Copyright (c) 2019 Gregorius Techneticies
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

import static gregapi.data.CS.*;

import java.util.List;

import gregapi.random.IHasWorldAndCoords;
import gregapi.tileentity.ITileEntityUnloadable;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

/**
 * @author Gregorius Techneticies
 */
public interface ITileEntityMultiBlockController extends ITileEntityUnloadable, IHasWorldAndCoords {
	public boolean isInsideStructure(int aX, int aY, int aZ);
	public boolean checkStructure(boolean aForceReset);
	public boolean checkStructureOnly(boolean aForceReset); // GTCH, 仅检测结构但是不改变结构改变变量的方法
	public void onStructureChange();
	public long onToolClickMultiBlock(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ, ChunkCoordinates aFrom);
	
	public static class Util {
		//由于长度可变，将检测和设置进行分开来提高效率
		public static boolean checkStructurePart(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aRegistryMeta, int aRegistryID) {
			TileEntity tTileEntity = aController.getTileEntity(aX, aY, aZ);
			if (tTileEntity == aController) return T;
			if (tTileEntity instanceof MultiTileEntityMultiBlockPart && ((MultiTileEntityMultiBlockPart)tTileEntity).getMultiTileEntityID() == aRegistryMeta && ((MultiTileEntityMultiBlockPart)tTileEntity).getMultiTileEntityRegistryID() == aRegistryID) {
				ITileEntityMultiBlockController tTarget = ((MultiTileEntityMultiBlockPart)tTileEntity).getTarget(F);
				if (tTarget != aController && tTarget != null && tTarget.isInsideStructure(aX, aY, aZ)) return F;
				return T;
			}
			return F;
		}
		public static void setTarget(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aDesign, int aMode) {
			TileEntity tTileEntity = aController.getTileEntity(aX, aY, aZ);
			if (tTileEntity instanceof MultiTileEntityMultiBlockPart) ((MultiTileEntityMultiBlockPart)tTileEntity).setTarget(aController, aDesign, aMode);
		}
		public static void checkAndResetTarget(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aRegistryMeta, int aRegistryID) {
			TileEntity tTileEntity = aController.getTileEntity(aX, aY, aZ);
			if (tTileEntity == aController) return;
			if (tTileEntity instanceof MultiTileEntityMultiBlockPart && ((MultiTileEntityMultiBlockPart)tTileEntity).getMultiTileEntityID() == aRegistryMeta && ((MultiTileEntityMultiBlockPart)tTileEntity).getMultiTileEntityRegistryID() == aRegistryID) {
				if (((MultiTileEntityMultiBlockPart)tTileEntity).getTarget(F) == aController) {
					((MultiTileEntityMultiBlockPart)tTileEntity).setTarget(aController, 0, MultiTileEntityMultiBlockPart.NOTHING); // 保留内部的 target 可以在放置多方快时更快的识别
				}
			}
		}

		public static boolean checkAndSetTarget(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aRegistryMeta, int aRegistryID, int aDesign, int aMode) {
			TileEntity tTileEntity = aController.getTileEntity(aX, aY, aZ);
			if (tTileEntity == aController) return T;
			if (tTileEntity instanceof MultiTileEntityMultiBlockPart && ((MultiTileEntityMultiBlockPart)tTileEntity).getMultiTileEntityID() == aRegistryMeta && ((MultiTileEntityMultiBlockPart)tTileEntity).getMultiTileEntityRegistryID() == aRegistryID) {
				ITileEntityMultiBlockController tTarget = ((MultiTileEntityMultiBlockPart)tTileEntity).getTarget(F);
				if (tTarget != aController && tTarget != null && tTarget.isInsideStructure(aX, aY, aZ)) return F;
				((MultiTileEntityMultiBlockPart)tTileEntity).setTarget(aController, aDesign, aMode);
				return T;
			}
			return F;
		}

		// 将检测和设置进行分开来提高效率
		public static boolean checkStructurePartOffset(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aRegistryMeta, int aRegistryID) {
			return checkStructurePart(aController, aX+aController.getX(), aY+aController.getY(), aZ+aController.getZ(), aRegistryMeta, aRegistryID);
		}
		public static void setTargetOffset(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aDesign, int aMode) {
			setTarget(aController, aX+aController.getX(), aY+aController.getY(), aZ+aController.getZ(), aDesign, aMode);
		}
		public static void checkAndResetTargetOffset(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aRegistryMeta, int aRegistryID) {
			checkAndResetTarget(aController, aX+aController.getX(), aY+aController.getY(), aZ+aController.getZ(), aRegistryMeta, aRegistryID);
		}

		public static boolean checkAndSetTargetOffset(ITileEntityMultiBlockController aController, int aX, int aY, int aZ, int aRegistryMeta, int aRegistryID, int aDesign, int aMode) {
			return checkAndSetTarget(aController, aX+aController.getX(), aY+aController.getY(), aZ+aController.getZ(), aRegistryMeta, aRegistryID, aDesign, aMode);
		}
	}
}
