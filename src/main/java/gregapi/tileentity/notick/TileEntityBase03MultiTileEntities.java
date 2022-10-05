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

package gregapi.tileentity.notick;

import static gregapi.data.CS.*;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_ColorMultiplier;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetDrops;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetPickBlock;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetStackFromBlock;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnBlockActivated;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnPainting;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnRegistrationFirst;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_RecolourBlock;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_ShouldSideBeRendered;
import gregapi.block.multitileentity.MultiTileEntityClassContainer;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.network.IPacket;
import gregapi.network.packets.data.PacketSyncDataByte;
import gregapi.network.packets.data.PacketSyncDataByteArray;
import gregapi.network.packets.data.PacketSyncDataInteger;
import gregapi.network.packets.data.PacketSyncDataLong;
import gregapi.network.packets.data.PacketSyncDataName;
import gregapi.network.packets.data.PacketSyncDataShort;
import gregapi.network.packets.ids.PacketSyncDataByteAndIDs;
import gregapi.network.packets.ids.PacketSyncDataByteArrayAndIDs;
import gregapi.network.packets.ids.PacketSyncDataIDs;
import gregapi.network.packets.ids.PacketSyncDataIntegerAndIDs;
import gregapi.network.packets.ids.PacketSyncDataLongAndIDs;
import gregapi.network.packets.ids.PacketSyncDataShortAndIDs;
import gregapi.render.IRenderedBlockObject;
import gregapi.render.IRenderedBlockObjectSideCheck;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.config.ConfigForge_CH;
import gregtechCH.tileentity.ITEAfterUpdateRender;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.util.UT_CH;
import gregtechCH.util.WD_CH;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.FakePlayer;

/**
 * @author Gregorius Techneticies
 */
public abstract class TileEntityBase03MultiTileEntities extends TileEntityBase02Sync implements IMTE_ColorMultiplier, ITEAfterUpdateRender, IRenderedBlockObjectSideCheck, IRenderedBlockObject, IMTE_OnPainting, IMTE_GetPickBlock, IMTE_GetStackFromBlock, IMTE_OnRegistrationFirst, IMTE_RecolourBlock, IMTE_GetDrops, IMTE_OnBlockActivated, IMTE_ShouldSideBeRendered {
	@Override
	public void sendClientData(boolean aSendAll, EntityPlayerMP aPlayer) {
		super.sendClientData(aSendAll, aPlayer);
		if (aSendAll && UT.Code.stringValid(mCustomName)) {
			if (aPlayer == null) {
				getNetworkHandler().sendToAllPlayersInRange(new PacketSyncDataName(getCoords(), mCustomName), worldObj, getCoords());
			} else {
				getNetworkHandler().sendToPlayer(new PacketSyncDataName(getCoords(), mCustomName), aPlayer);
			}
		}
	}
	
	private short mMTEID = W, mMTERegistry = W;
	private String mCustomName = null;
	
	/** return the internal Name of this TileEntity to be registered. DO NOT START YOUR NAME WITH "gt."!!! */
	public abstract String getTileEntityName();
	
	@Override
	public void onRegistrationFirst(MultiTileEntityRegistry aRegistry, short aID) {
		if (this instanceof ITileEntityNameCompat) GameRegistry.registerTileEntity(getClass(), ((ITileEntityNameCompat)this).getTileEntityNameCompat()); // 注册旧的名称来兼容旧版 GTCH
		GameRegistry.registerTileEntity(getClass(), getTileEntityName());
	}
	
	@Override
	public IPacket getClientDataPacket(boolean aSendAll)                                {return aSendAll ? new PacketSyncDataIDs                (getCoords(), getMultiTileEntityRegistryID(), getMultiTileEntityID()                ) : null;}
	public IPacket getClientDataPacketByte(boolean aSendAll, byte aByte)                {return aSendAll ? new PacketSyncDataByteAndIDs         (getCoords(), getMultiTileEntityRegistryID(), getMultiTileEntityID(), aByte         ) : new PacketSyncDataByte      (getCoords(), aByte         );}
	public IPacket getClientDataPacketShort(boolean aSendAll, short aShort)             {return aSendAll ? new PacketSyncDataShortAndIDs        (getCoords(), getMultiTileEntityRegistryID(), getMultiTileEntityID(), aShort        ) : new PacketSyncDataShort     (getCoords(), aShort        );}
	public IPacket getClientDataPacketInteger(boolean aSendAll, int aInteger)           {return aSendAll ? new PacketSyncDataIntegerAndIDs      (getCoords(), getMultiTileEntityRegistryID(), getMultiTileEntityID(), aInteger      ) : new PacketSyncDataInteger   (getCoords(), aInteger      );}
	public IPacket getClientDataPacketLong(boolean aSendAll, long aLong)                {return aSendAll ? new PacketSyncDataLongAndIDs         (getCoords(), getMultiTileEntityRegistryID(), getMultiTileEntityID(), aLong         ) : new PacketSyncDataLong      (getCoords(), aLong         );}
	public IPacket getClientDataPacketByteArray(boolean aSendAll, byte... aByteArray)   {return aSendAll ? new PacketSyncDataByteArrayAndIDs    (getCoords(), getMultiTileEntityRegistryID(), getMultiTileEntityID(), aByteArray    ) : new PacketSyncDataByteArray (getCoords(), aByteArray    );}
	
	@Override
	public final void initFromNBT(NBTTagCompound aNBT, short aMTEID, short aMTERegistry) {
		// Set ID and Registry ID.
		mMTEID = aMTEID;
		mMTERegistry = aMTERegistry;
		// Read the Default Parameters from NBT.
		if (aNBT != null) readFromNBT(aNBT);
	}

	@Override
	public final void readFromNBT(NBTTagCompound aNBT) {
		super.readFromNBT(aNBT);
		// Check if this is a World/Chunk Loading Process calling readFromNBT.
		if (mMTEID == W || mMTERegistry == W) {
			// Yes it is, so read the ID Tags first.
			mMTEID = aNBT.getShort(NBT_MTE_ID);
			mMTERegistry = aNBT.getShort(NBT_MTE_REG);
			// And add additional Default Parameters, in case the Mod updated with new ones.
			MultiTileEntityRegistry tRegistry = MultiTileEntityRegistry.getRegistry(mMTERegistry);
			if (tRegistry != null) {
				MultiTileEntityClassContainer tClass = tRegistry.getClassContainer(mMTEID);
				if (tClass != null) {
					// Add the Default Parameters.
					aNBT = UT.NBT.fuse(aNBT, tClass.mParameters);
				}
			}
		}
		
		// read the custom Name.
		if (aNBT.hasKey("display")) mCustomName = aNBT.getCompoundTag("display").getString("Name");
		// And now your custom readFromNBT.
		try {readFromNBT2(aNBT);} catch(Throwable e) {e.printStackTrace(ERR);}
	}
	
	public void readFromNBT2(NBTTagCompound aNBT) {/**/}
	
	@Override
	public final void writeToNBT(NBTTagCompound aNBT) {
		super.writeToNBT(aNBT);
		// write the IDs
		aNBT.setShort(NBT_MTE_ID, mMTEID);
		aNBT.setShort(NBT_MTE_REG, mMTERegistry);
		// write the Custom Name
		if (UT.Code.stringValid(mCustomName)) aNBT.setTag("display", UT.NBT.makeString(aNBT.getCompoundTag("display"), "Name", mCustomName));
		if (isPainted()) {aNBT.setInteger(NBT_COLOR, getPaint()); aNBT.setBoolean(NBT_PAINTED, T);}
		// write the rest
		try {writeToNBT2(aNBT);} catch(Throwable e) {e.printStackTrace(ERR);}
	}
	
	public void writeToNBT2(NBTTagCompound aNBT) {/**/}
	
	@Override
	public NBTTagCompound writeItemNBT(NBTTagCompound aNBT) {
		if (UT.Code.stringValid(mCustomName)) aNBT.setTag("display", UT.NBT.makeString(aNBT.getCompoundTag("display"), "Name", mCustomName));
		if (UT.Code.stringValid(ERROR_MESSAGE) && isClientSide()) aNBT.setTag("display", UT.NBT.makeString(aNBT.getCompoundTag("display"), "Name", ERROR_MESSAGE));
		if (isPainted()) {aNBT.setInteger(NBT_COLOR, (int) UT_CH.NBT.toItemNumber(getPaint())); aNBT.setBoolean(NBT_PAINTED, T);}
		return aNBT;
	}
	
	@Override
	public final boolean onBlockActivated(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
		worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
		return allowRightclick(aPlayer) && (checkObstruction(aPlayer, aSide, aHitX, aHitY, aHitZ) || onBlockActivated2(aPlayer, aSide, aHitX, aHitY, aHitZ));
	}
	
	public boolean onBlockActivated2(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
		return F;
	}
	
	public boolean checkObstruction(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
		return !(aPlayer == null || aPlayer instanceof FakePlayer || SIDES_INVALID[aSide] || !WD.obstructed(worldObj, xCoord, yCoord, zCoord, aSide));
	}
	
	@Override
	public ArrayListNoNulls<ItemStack> getDrops(int aFortune, boolean aSilkTouch) {
		ArrayListNoNulls<ItemStack> rList = new ArrayListNoNulls<>();
		MultiTileEntityRegistry tRegistry = MultiTileEntityRegistry.getRegistry(getMultiTileEntityRegistryID());
		if (tRegistry != null) rList.add(tRegistry.getItem(getMultiTileEntityID(), writeItemNBT(UT.NBT.make())));
		return rList;
	}
	
	@Override
	public boolean recolourBlock(byte aSide, byte aColor) {
		if (UT.Code.exists(aColor, DYES_INVERTED)) {
			int aRGB = (isPainted() ? UT_CH.Code.mixRGBInt(getPaint(), DYES_INT_INVERTED[aColor]) : DYES_INT_INVERTED[aColor]) & ALL_NON_ALPHA_COLOR;
			if (paint(aRGB)) {updateClientData(T); causeBlockUpdate(); worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this); return T;}
			return F;
		}
		if (unpaint()) {updateClientData(T); causeBlockUpdate(); worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this); return T;}
		return F;
	}
	
	@Override
	public boolean onPainting(byte aSide, int aRGB) {
		if (paint(aRGB)) {updateClientData(T); causeBlockUpdate(); worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this); return T;}
		return F;
	}

	@SideOnly(Side.CLIENT) @Override public int colorMultiplier() {return UNCOLORED;}
	
	public boolean unpaint() {return F;}
	public boolean isPainted() {return F;}
	public void setIsPainted(boolean aIsPainted) {/**/}
	public boolean paint(int aRGB) {return F;}
	public int getPaint() {return UNCOLORED;}
	// GTCH, 原本逻辑过于麻烦，直接把是否已经染色也同步到客户端好了，这样还可以多出来一些数据用于专门处理颜色动画，可能可以方便后续的温度变色之类的开发（因为温度并没有传到客户端）
	public byte getPaintData() {return (byte) ((isPainted()?1:0) | (willRerendImmediate?2:0));}
	public void setPaintData(byte aData) {setIsPainted((aData&1)!=0); willRerendImmediate = ((aData&2)!=0);}
	// GTCH, 在染色时立刻重新渲染
	private boolean willRerendImmediate = F;
	public void updateClientData(boolean aRerendImmediate) {willRerendImmediate = aRerendImmediate; updateClientData(); willRerendImmediate = F;} // 后续在需要立刻重新渲染时调用此函数即可
	@Override
	public final void doAfterUpdateRender_CH(IBlockAccess aWorld, int aX, int aY, int aZ) {
		if (!ConfigForge_CH.DATA_GTCH.rerenderAll && (willRerendImmediate || willRerendImmediateAny())) WD_CH.updateRender(aWorld, aX, aY, aZ, T, F);
		willRerendImmediate = F;
	}
	public boolean willRerendImmediateAny() {return F;} // 也可重写此方法，在不用 sendAll 的情况下立刻重新渲染
	
	@Override public String getCustomName() {return UT.Code.stringValid(mCustomName) ? mCustomName : null;}
	@Override public void setCustomName(String aName) {mCustomName = aName;}
	@Override public ItemStack getPickBlock(MovingObjectPosition aTarget) {MultiTileEntityRegistry tRegistry = MultiTileEntityRegistry.getRegistry(mMTERegistry); return tRegistry == null ? null : tRegistry.getItem(mMTEID, writeItemNBT(UT.NBT.make()));}
	@Override public ItemStack getStackFromBlock(byte aSide) {MultiTileEntityRegistry tRegistry = MultiTileEntityRegistry.getRegistry(mMTERegistry); return tRegistry == null ? null : tRegistry.getItem(mMTEID, writeItemNBT(UT.NBT.make()));}
	@Override public short getMultiTileEntityID() {return mMTEID;}
	@Override public short getMultiTileEntityRegistryID() {return mMTERegistry;}
	@Override public void setShouldRefresh(boolean aShouldRefresh) {mShouldRefresh = aShouldRefresh;}
}
