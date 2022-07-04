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

package gregapi.tileentity.base;

import static gregapi.data.CS.*;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetBlockHardness;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetExplosionResistance;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetLightOpacity;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetSubItems;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_SyncDataByte;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_SyncDataByteArray;
import gregapi.block.multitileentity.MultiTileEntityBlockInternal;
import gregapi.data.MT;
import gregapi.item.IItemColorableRGB;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.ITileEntityDecolorable;
import gregapi.util.UT;
import gregtechCH.tileentity.ITEPaintable_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

/**
 * @author Gregorius Techneticies
 */
public abstract class TileEntityBase07Paintable extends TileEntityBase06Covers implements ITEPaintable_CH, IItemColorableRGB, ITileEntityDecolorable, IMTE_GetSubItems, IMTE_GetExplosionResistance, IMTE_GetBlockHardness, IMTE_GetLightOpacity, IMTE_SyncDataByte, IMTE_SyncDataByteArray {
	protected boolean mIsPainted = F;
	protected int mFlammability = 0;
	protected float mHardness = 1.0F, mResistance = 3.0F;
	protected OreDictMaterial mMaterial = MT.NULL;

	// GTCH, 用于在染色后保留一定原本颜色
	protected int mRGBaPaint = UNCOLORED;
	// 仅客户端有效
	protected int mRGBa = UNCOLORED;

	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
//		if (aNBT.hasKey(NBT_COLOR)) mRGBa = aNBT.getInteger(NBT_COLOR); // 变成了临时变量，不需要存储
		if (aNBT.hasKey(NBT_PAINTED)) mIsPainted = aNBT.getBoolean(NBT_PAINTED);
		if (aNBT.hasKey(NBT_HARDNESS)) mHardness = aNBT.getFloat(NBT_HARDNESS);
		if (aNBT.hasKey(NBT_RESISTANCE)) mResistance = aNBT.getFloat(NBT_RESISTANCE);
		if (aNBT.hasKey(NBT_FLAMMABILITY)) mFlammability = aNBT.getInteger(NBT_FLAMMABILITY);
		if (aNBT.hasKey(NBT_MATERIAL)) mMaterial = OreDictMaterial.get(aNBT.getString(NBT_MATERIAL));

		// 需要分情况讨论，考虑有不允许染色的，带有默认颜色的，并且不是材料颜色的方块
		if (isPainted()) {
			if (aNBT.hasKey(NBT_COLOR)) mRGBaPaint = aNBT.getInteger(NBT_COLOR); // mRGBaPaint 替代原本的 NBT_COLOR
			mRGBa = UT_CH.Code.getPaintRGB(getBottomRGB(), mRGBaPaint);
		} else {
			mRGBaPaint = getBottomRGB();
			if (aNBT.hasKey(NBT_COLOR)) mRGBa = aNBT.getInteger(NBT_COLOR);
			else mRGBa = getOriginalRGB(); // 可以防止一些问题
		}
	}

	// 禁用重写来避免合并时出现意料外的重写
	@Override
	public final IPacket getClientDataPacket(boolean aSendAll) {
		// GTCH, 使用 list 的方式来实现动态数组扩容，减少后续代码中的 mRGBa 重复代码，希望不会严重影响效率
		if (sendAny(aSendAll)) {
			List<Byte> rList = Lists.newArrayList((byte)UT.Code.getR(mRGBaPaint), (byte)UT.Code.getG(mRGBaPaint), (byte)UT.Code.getB(mRGBaPaint));
			writeToClientDataPacketByteList(rList);
			rList.add(getPaintData()); // 放到最后避免下标变化
			return getClientDataPacketSendAll(T, rList);
		}
		return getClientDataPacketNoSendAll(F);
	}
	// 用于重写发送数据
	public boolean sendAny(boolean aSendAll) {return aSendAll;}
	public IPacket getClientDataPacketSendAll(boolean aSendAll, List<Byte> rList) {
		return getClientDataPacketByteArray(aSendAll, Bytes.toArray(rList));
	}
	public IPacket getClientDataPacketNoSendAll(boolean aSendAll) {
		return getClientDataPacketByte(aSendAll, getVisualData());
	}
	// GTCH, 重写这个方法来扩展客户端数据
	public void writeToClientDataPacketByteList(@NotNull List<Byte> rList) {
		rList.add(3, getVisualData());
	}
	
	@Override
	public boolean receiveDataByte(byte aData, INetworkHandler aNetworkHandler) {
		setVisualData(aData);
		return T;
	}
	
	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		setRGBData(aData[0], aData[1], aData[2], aData[aData.length-1]);
		setVisualData(aData[3]);
		return T;
	}
	// 用于在重写接受数据代码时调用简单的设置颜色
	protected final void setRGBData(byte aR, byte aG, byte aB, byte aPaintData) {
		boolean oIsPainted = mIsPainted;
		int oRGBPaint = mRGBaPaint;
		setPaintData(aPaintData);
		mRGBaPaint = UT.Code.getRGBInt(new short[] {UT.Code.unsignB(aR), UT.Code.unsignB(aG), UT.Code.unsignB(aB)});
		if (oIsPainted!=mIsPainted || oRGBPaint!=mRGBaPaint) {
			onPaintChangeClient(oRGBPaint); // 仅客户端，用于在染色改变时客户端更改对应的显示颜色
		}
	}

	/* 仅客户端，用于在染色改变时客户端更改对应的显示颜色 */
	@SideOnly(Side.CLIENT)
	public void onPaintChangeClient(int aPreviousRGBaPaint) {
		mRGBa = isPainted() ? UT_CH.Code.getPaintRGB(getBottomRGB(), mRGBaPaint) : getOriginalRGB();
	}

	// GTCH, 原本逻辑过于麻烦，直接把是否已经染色也同步到客户端好了，这样还可以多出来一些数据用于专门处理颜色动画，可能可以方便后续的温度变色之类的开发（因为温度并没有传到客户端）
	public byte getPaintData() {return (byte) (mIsPainted?1:0);}
	public void setPaintData(byte aData) {mIsPainted = ((aData & 1) != 0);}

	// GTCH, 返回染色中用于叠底的颜色，用于给有外套层的机器重写，也用于客户端判断是否有染色
	@Override public int getBottomRGB() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}
	// GTCH, 返回机器原本的颜色，用于客户端判断是否有染色，由于原本的默认 RGB 都是材料颜色，所以不允许重写
	@Override public final int getOriginalRGB() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}
	
	@Override public boolean unpaint() {if (mIsPainted) {mIsPainted=F; mRGBaPaint=getBottomRGB(); updateClientData(); return T;} return F;}
	// GTCH, 原本逻辑过于麻烦，直接把是否已经染色也同步到客户端好了，这样还可以多出来一些数据用于专门处理颜色动画，可能可以方便后续的温度变色之类的开发（因为温度并没有传到客户端，不过实际用时需要优化把这个放一份到 NoSendAll里）
	@Override public boolean isPainted() {return mIsPainted;}
	@Override public boolean paint(int aRGB) {if (aRGB!=mRGBaPaint) {mRGBaPaint=aRGB; mIsPainted=T; return T;} return F;}
	@Override public int getPaint() {return mRGBaPaint;}
	@Override public boolean canRecolorItem(ItemStack aStack) {return T;}
	@Override public boolean canDecolorItem(ItemStack aStack) {return mIsPainted;}
	@Override public boolean recolorItem(ItemStack aStack, int aRGB) {if (paint((isPainted() ? UT.Code.mixRGBInt(aRGB, getPaint()) : aRGB) & ALL_NON_ALPHA_COLOR)) {UT.NBT.set(aStack, writeItemNBT(aStack.hasTagCompound() ? aStack.getTagCompound() : UT.NBT.make())); return T;} return F;}
	
	@Override
	public boolean decolorItem(ItemStack aStack) {
		if (unpaint()) {
			if (aStack.hasTagCompound()) {
				aStack.getTagCompound().removeTag(NBT_PAINTED);
				aStack.getTagCompound().removeTag(NBT_COLOR);
				UT.NBT.set(aStack, writeItemNBT(aStack.getTagCompound()));
			} else {
				UT.NBT.set(aStack, writeItemNBT(UT.NBT.make()));
			}
			return T;
		}
		return F;
	}
	
	@Override public int getLightOpacity() {return LIGHT_OPACITY_MAX;}
	@Override public int getFireSpreadSpeed(byte aSide, boolean aDefault) {return mFlammability;}
	@Override public int getFlammability(byte aSide, boolean aDefault) {return mFlammability;}
	@Override public float getBlockHardness() {return mHardness;}
	@Override public float getExplosionResistance2() {return mResistance;}
	@Override public boolean getSubItems(MultiTileEntityBlockInternal aBlock, Item aItem, CreativeTabs aTab, List<ItemStack> aList, short aID) {return showInCreative();}
	
	// Stuff to Override
	public byte getVisualData() {return 0;}
	public void setVisualData(byte aData) {/**/}
	public boolean showInCreative() {return SHOW_HIDDEN_MATERIALS || !mMaterial.mHidden;}
}
