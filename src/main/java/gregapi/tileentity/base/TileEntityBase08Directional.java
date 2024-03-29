/**
 * Copyright (c) 2023 GregTech-6 Team
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

package gregapi.tileentity.base;

import static gregapi.data.CS.*;

import cpw.mods.fml.common.Optional;
import gregapi.data.CS.ModIDs;
import gregapi.data.CS.ToolsGT;
import gregapi.network.INetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Gregorius Techneticies
 */
@Optional.InterfaceList(value = {
	@Optional.Interface(iface = "ic2.api.tile.IWrenchable", modid = ModIDs.IC2)
})
public abstract class TileEntityBase08Directional extends TileEntityBase07Paintable implements ic2.api.tile.IWrenchable {
	
	// GTCH, 重写这个方法来扩展客户端数据
	@Override
	public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {
		super.writeToClientDataPacketByteList(rList);
		rList.add(4, getDirectionData());
	}
	
	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		super.receiveDataByteArray(aData, aNetworkHandler);
		setDirectionData(aData[4]);
		return T;
	}
	
	// Stuff to Override
	public byte getDirectionData() {return 0;}
	public void setDirectionData(byte aData) {/**/}
	public String getFacingTool() {return "";}
	
	@Override public boolean isUsingWrenchingOverlay(ItemStack aStack, byte aSide) {return super.isUsingWrenchingOverlay(aStack, aSide) || (getFacingTool() != null && ToolsGT.contains(getFacingTool(), aStack));}
	@Override public boolean wrenchCanRemove(EntityPlayer aPlayer) {return F;}
	@Override public float getWrenchDropRate() {return 1.0F;}
	@Override public ItemStack getWrenchDrop(EntityPlayer aPlayer) {return null;}
}
