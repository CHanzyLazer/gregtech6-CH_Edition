/**
 * Copyright (c) 2020 GregTech-6 Team
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

package gregapi.block.multitileentity;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityClassContainer {
	public final Class<? extends TileEntity> mClass;
	public final MultiTileEntityBlock mBlock;
	public final TileEntity mCanonicalTileEntity;
	public final NBTTagCompound mParameters;
	public final byte mBlockMetaData, mStackSize;
	public final short mID, mCreativeTabID;
	public final boolean mHidden;
	// GTCH
	public final RegType mRegType;
	
	// 保留兼容
	public MultiTileEntityClassContainer(int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters) {
		this(RegType.GREG, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters);
	}
	public MultiTileEntityClassContainer(RegType aType, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters) {
		if (!IMultiTileEntity.class.isAssignableFrom(aClass)) throw new IllegalArgumentException("MultiTileEntities must implement the Interface IMultiTileEntity!");
		mRegType = aType;
		mBlockMetaData = (byte)aBlockMetaData;
		mStackSize = (byte)aStackSize;
		mParameters = aParameters==null?UT.NBT.make():aParameters;
		mHidden = mParameters.getBoolean(NBT_HIDDEN);
		mID = (short)aID;
		mCreativeTabID = (short)aCreativeTabID;
		mBlock = aBlock;
		mClass = aClass;
		if (mParameters.hasKey(NBT_MATERIAL) && !mParameters.hasKey(NBT_COLOR)) mParameters.setInteger(NBT_COLOR, UT.Code.getRGBInt(OreDictMaterial.get(mParameters.getString(NBT_MATERIAL)).fRGBaSolid));
		try {mCanonicalTileEntity = aClass.newInstance();} catch (Throwable e) {throw new IllegalArgumentException(e);}
		if (mCanonicalTileEntity instanceof IMultiTileEntity) ((IMultiTileEntity)mCanonicalTileEntity).initFromNBT(mParameters, mID, (short)-1);
	}
}
