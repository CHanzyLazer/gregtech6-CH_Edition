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
import static gregtechCH.data.CS_CH.*;

import java.util.*;

import com.google.common.primitives.Longs;
import com.mojang.realmsclient.util.Pair;
import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetOreDictItemData;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnEntityCollidedWithBlock;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.data.CS.GarbageGT;
import gregapi.data.CS.IconsGT;
import gregapi.data.CS.SFX;
import gregapi.data.LH.Chat;
import gregapi.fluid.FluidTankGT;
import gregapi.network.INetworkHandler;
import gregapi.old.Textures;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityAdjacentInventoryUpdatable;
import gregapi.tileentity.ITileEntityQuickObstructionCheck;
import gregapi.tileentity.ITileEntityServerTickPre;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityProgress;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.util.*;
import gregtechCH.data.LH_CH;
import gregtechCH.fluid.IFluidHandler_CH;
import gregtechCH.tileentity.compat.PipeCompat_CH;
import gregtechCH.tileentity.data.ITileEntityFlowrate_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityPipeFluid extends TileEntityBase10ConnectorRendered implements ITileEntityFlowrate_CH, IMTE_GetOreDictItemData, ITileEntityQuickObstructionCheck, IFluidHandler_CH, ITileEntityGibbl, ITileEntityTemperature, ITileEntityProgress, ITileEntityServerTickPre, IMTE_GetCollisionBoundingBoxFromPool, IMTE_OnEntityCollidedWithBlock {
	private byte[] mLastReceivedFrom = ZL_BYTE;
	private static final byte LRF_COOLDOWN_NUM = 8;
	private byte[][] mLRFCooldownCounters =  ZL_BI_BYTE;

	public byte mRenderType = 0;
	public long mTemperature = DEF_ENV_TEMP, mMaxTemperature, mTransferredAmount = 0, mCapacity = 1000;
	public boolean mGasProof = F, mAcidProof = F, mPlasmaProof = F, mBlocking = F;
	public FluidTankGT[] mTanks = ZL_FT;

	// GTCH，用来实现限制方向传递流体
	public byte mFluidDir = SIDE_ANY, oFluidDir = SIDE_ANY;
	public Mode mFluidMode = Mode.DEFAULT, oFluidMode = Mode.DEFAULT;
	// GTCH，用来限制流量（通过限制容量）
	public byte mCapacityLimit = 0;
	public final static byte MAX_LIMIT = 8;
	private byte[] mBeginIdx = ZL_BYTE; // 用来存储进行均分时下一个起始位置，可以实现比较完美的均分
	// GTCH, 控制流速
	public boolean mFlowControl = F, oFlowControl = F;
	public boolean mAllowSwitchFC = T; // 是否允许切换流速控制
	// GTCH, 颜色，预先计算会不会好些？
	protected int mRGBaMark, mRGBaRubber = UT.Code.getRGBInt(65, 65, 65);
	// GTCH, 改为直接平均来实现平滑
	protected static final int BUFFER_LENGTH = 20;
	private final List<LinkedList<Long>> mInBuffers = new ArrayList<>(); // 用于对输入统计平均
	private long[] mOutputs = ZL_LONG, oAmounts = ZL_LONG; // 平滑后的输出值和用于计算的上一 tick 的储罐值
	// GTCH, 用于在建筑泡沫上显示 mark
	public int mMarkBuffer = 0;
	public boolean mOutMark = F, oOutMark = F;

	/**
	 * Utility to quickly add a whole set of Fluid Pipes.
	 * May use up to 20 IDs, even if it is just 7 right now!
	 */
	public static void addFluidPipes(int aID, int aCreativeTabID, long aStat, boolean aGasProof, boolean aAcidProof, boolean aPlasmaProof, boolean aContactDamage, boolean aFlammable, boolean aRecipe, boolean aBlocking, MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aBlock, Class<? extends TileEntity> aClass, OreDictMaterial aMat) {
		addFluidPipes(aID, aCreativeTabID, aStat, aGasProof, aAcidProof, aPlasmaProof, aContactDamage, aFlammable, aRecipe, aBlocking, aRegistry, aBlock, aClass, (long)(aMat.mMeltingPoint * 1.25), aMat);
	}
	
	/**
	 * Utility to quickly add a whole set of Fluid Pipes.
	 * May use up to 20 IDs, even if it is just 7 right now!
	 */
	public static void addFluidPipes(int aID, int aCreativeTabID, long aStat, boolean aGasProof, boolean aAcidProof, boolean aPlasmaProof, boolean aContactDamage, boolean aFlammable, boolean aRecipe, boolean aBlocking, MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aBlock, Class<? extends TileEntity> aClass, long aMaxTemperature, OreDictMaterial aMat) {
		OreDictManager.INSTANCE.setTarget_(OP.pipeTiny     , aMat, aRegistry.add("Tiny " + aMat.getLocal() + " Fluid Pipe"     , "Fluid Pipes", aID   , aCreativeTabID, aClass, aMat.mToolQuality, 64, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 4], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat    , NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 1, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), aRecipe?new Object[]{"sP ", "wzh"       , 'P', OP.plateCurved.dat(aMat)}:ZL), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.pipeSmall    , aMat, aRegistry.add("Small " + aMat.getLocal() + " Fluid Pipe"    , "Fluid Pipes", aID+ 1, aCreativeTabID, aClass, aMat.mToolQuality, 64, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 6], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat* 2L, NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 1, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), aRecipe?new Object[]{" P ", "wzh"       , 'P', OP.plateCurved.dat(aMat)}:ZL), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.pipeMedium   , aMat, aRegistry.add(aMat.getLocal() + " Fluid Pipe"               , "Fluid Pipes", aID+ 2, aCreativeTabID, aClass, aMat.mToolQuality, 32, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 8], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat* 6L, NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 1, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), aRecipe?new Object[]{"PPP", "wzh"       , 'P', OP.plateCurved.dat(aMat)}:ZL), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.pipeLarge    , aMat, aRegistry.add("Large " + aMat.getLocal() + " Fluid Pipe"    , "Fluid Pipes", aID+ 3, aCreativeTabID, aClass, aMat.mToolQuality, 16, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[12], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat*12L, NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 1, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), aRecipe?new Object[]{"PPP", "wzh", "PPP", 'P', OP.plateCurved.dat(aMat)}:ZL), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.pipeHuge     , aMat, aRegistry.add("Huge " + aMat.getLocal() + " Fluid Pipe"     , "Fluid Pipes", aID+ 4, aCreativeTabID, aClass, aMat.mToolQuality, 16, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[16], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat*24L, NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 1, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), aRecipe?new Object[]{"PPP", "wzh", "PPP", 'P', OP.plateDouble.dat(aMat)}:ZL), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.pipeQuadruple, aMat, aRegistry.add("Quadruple " + aMat.getLocal() + " Fluid Pipe", "Fluid Pipes", aID+ 5, aCreativeTabID, aClass, aMat.mToolQuality, 16, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[16], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat* 6L, NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 4, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), new Object[]{"PP" , "PP"        , 'P', OP.pipeMedium.dat(aMat)}), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.pipeNonuple  , aMat, aRegistry.add("Nonuple " + aMat.getLocal() + " Fluid Pipe"  , "Fluid Pipes", aID+ 6, aCreativeTabID, aClass, aMat.mToolQuality, 16, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 6.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[16], NBT_CONTACTDAMAGE, aContactDamage, NBT_TANK_CAPACITY, aStat* 2L, NBT_OPAQUE, aBlocking, NBT_GASPROOF, aGasProof, NBT_ACIDPROOF, aAcidProof, NBT_PLASMAPROOF, aPlasmaProof, NBT_FLAMMABILITY, aFlammable ? 150 : 0, NBT_TEMPERATURE, aMaxTemperature, NBT_TANK_COUNT, 9, NBT_ADD_BOOL+".fc", aMat==MT.Rubber, NBT_ADD_BOOL+".allowswitch.fc", aMat!=MT.Rubber), new Object[]{"PPP", "PPP", "PPP", 'P', OP.pipeSmall.dat(aMat)}), T, F, T);
		
		CR.shapeless(aRegistry.getItem(aID+2, 4), CR.DEF_NCC, new Object[] {aRegistry.getItem(aID+5)});
		CR.shapeless(aRegistry.getItem(aID+1, 9), CR.DEF_NCC, new Object[] {aRegistry.getItem(aID+6)});
	}
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_ADD_BYTE + ".dir")) mFluidDir = (byte) UT_CH.NBT.getItemNumber(aNBT.getByte(NBT_ADD_BYTE + ".dir"));
		if (aNBT.hasKey(NBT_ADD_BYTE + ".mode")) mFluidMode = Mode.values()[(byte) UT_CH.NBT.getItemNumber(aNBT.getByte(NBT_ADD_BYTE + ".mode"))];
		if (aNBT.hasKey(NBT_ADD_BYTE + ".limit")) mCapacityLimit = aNBT.getByte(NBT_ADD_BYTE + ".limit");
		if (aNBT.hasKey(NBT_ADD_BOOL + ".fc")) mFlowControl = aNBT.getBoolean(NBT_ADD_BOOL + ".fc");

		if (aNBT.hasKey(NBT_ADD_BOOL + ".allowswitch.fc")) mAllowSwitchFC = aNBT.getBoolean(NBT_ADD_BOOL + ".allowswitch.fc");

		if (aNBT.hasKey("gt.mtransfer")) mTransferredAmount = aNBT.getLong("gt.mtransfer");
		if (aNBT.hasKey(NBT_PIPERENDER)) mRenderType = aNBT.getByte(NBT_PIPERENDER);
		if (aNBT.hasKey(NBT_OPAQUE)) mBlocking = aNBT.getBoolean(NBT_OPAQUE);
		if (aNBT.hasKey(NBT_GASPROOF)) mGasProof = aNBT.getBoolean(NBT_GASPROOF);
		if (aNBT.hasKey(NBT_ACIDPROOF)) mAcidProof = aNBT.getBoolean(NBT_ACIDPROOF);
		if (aNBT.hasKey(NBT_PLASMAPROOF)) mPlasmaProof = aNBT.getBoolean(NBT_PLASMAPROOF);
		if (aNBT.hasKey(NBT_TANK_CAPACITY)) mCapacity = aNBT.getLong(NBT_TANK_CAPACITY);
		long tCapacity = mCapacity >> mCapacityLimit;
		if (tCapacity <= 0 || mCapacityLimit == MAX_LIMIT) tCapacity = 0;

		if (aNBT.hasKey(NBT_TEMPERATURE)) mMaxTemperature = aNBT.getLong(NBT_TEMPERATURE);
		if (aNBT.hasKey(NBT_TANK_COUNT)) {
			int tTankCount = Math.max(1, aNBT.getInteger(NBT_TANK_COUNT));
			mBeginIdx = new byte[tTankCount];
			for (int i = 0; i < tTankCount; ++i) mBeginIdx[i] = aNBT.hasKey(NBT_BEGIN+"."+i)?aNBT.getByte(NBT_BEGIN+"."+i):SIDE_INVALID;
			mOutputs = new long[tTankCount];
			oAmounts = new long[tTankCount];
			for (int i = 0; i < tTankCount; ++i) oAmounts[i] = aNBT.getLong(NBT_TANK+".o."+i);
			mInBuffers.clear();
			for (int i = 0; i < tTankCount; ++i)
				mInBuffers.add(aNBT.hasKey(NBT_INPUT_BUFFER +"."+i) ? UT_CH.STL.toList(UT_CH.NBT.getNumberArray(aNBT, NBT_INPUT_BUFFER +"."+i), BUFFER_LENGTH) : new LinkedList<Long>());

			mTanks = new FluidTankGT[tTankCount];
			mLastReceivedFrom = new byte[mTanks.length];
			mLRFCooldownCounters = new byte[mTanks.length][SIDE_NUMBER];
			for (int i = 0; i < mTanks.length; i++) {
				mTanks[i] = new FluidTankGT(aNBT, NBT_TANK+"."+i, tCapacity).setIndex(i);
				mLastReceivedFrom[i] = aNBT.getByte("gt.mlast."+i);
				long[] tNumbers = UT_CH.NBT.getNumberArray(aNBT, "gt.mlast.cooldown."+i);
				for (int j = 0; j < SIDE_NUMBER; ++j) mLRFCooldownCounters[i][j] = (j<tNumbers.length)?(byte)tNumbers[j]:0;
			}
		} else {
			mBeginIdx = new byte[1];
			mBeginIdx[0] = aNBT.hasKey(NBT_BEGIN+"."+0)?aNBT.getByte(NBT_BEGIN+"."+0):SIDE_INVALID;
			mOutputs = new long[1];
			oAmounts = new long[1];
			oAmounts[0] = aNBT.getLong(NBT_TANK+".o."+0);
			mInBuffers.clear();
			mInBuffers.add(aNBT.hasKey(NBT_INPUT_BUFFER +"."+0) ? UT_CH.STL.toList(UT_CH.NBT.getNumberArray(aNBT, NBT_INPUT_BUFFER +"."+0), BUFFER_LENGTH) : new LinkedList<Long>());

			mTanks = new FluidTankGT(aNBT, NBT_TANK+".0", tCapacity).AS_ARRAY;

			mLastReceivedFrom = new byte[1];
			mLastReceivedFrom[0] = aNBT.getByte("gt.mlast.0");
			mLRFCooldownCounters = new byte[1][SIDE_NUMBER];
			long[] tNumbers = UT_CH.NBT.getNumberArray(aNBT, "gt.mlast.cooldown."+0);
			for (int j = 0; j < SIDE_NUMBER; ++j) mLRFCooldownCounters[0][j] = (j<tNumbers.length)?(byte)tNumbers[j]:0;
		}
		
		if (worldObj != null && isServerSide() && mHasToAddTimer) {
			if (WD.even(this)) {
				GT_API_Proxy.SERVER_TICK_PRE.add(this);
			} else {
				GT_API_Proxy.SERVER_TICK_PR2.add(this);
			}
			mHasToAddTimer = F;
		}
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		// 默认值不为零（或者可能不为零）的需要专门设置
		if (SIDES_VALID[mFluidDir]) aNBT.setByte(NBT_ADD_BYTE + ".dir", mFluidDir);
		if (mFluidMode != Mode.DEFAULT) aNBT.setByte(NBT_ADD_BYTE + ".mode", (byte)mFluidMode.ordinal());
		UT.NBT.setNumber(aNBT, NBT_ADD_BYTE + ".limit", mCapacityLimit);
		UT.NBT.setBoolean(aNBT, NBT_ADD_BOOL + ".fc", mFlowControl);
		for (int i = 0; i < mInBuffers.size(); i++) UT_CH.NBT.setNumberArray(aNBT, NBT_INPUT_BUFFER +"."+i, Longs.toArray(mInBuffers.get(i)));
		for (int i = 0; i < oAmounts.length; i++) UT.NBT.setNumber(aNBT, NBT_TANK+".o."+i, oAmounts[i]);
		for (int i = 0; i < mBeginIdx.length; i++) if (mBeginIdx[i] != SIDE_INVALID) aNBT.setByte(NBT_BEGIN+"."+i, mBeginIdx[i]);

		for (int i = 0; i < mTanks.length; i++) {
			mTanks[i].writeToNBT(aNBT, NBT_TANK+"."+i);
			UT.NBT.setNumber(aNBT, "gt.mlast."+i, mLastReceivedFrom[i]);
			UT_CH.NBT.setNumberArray(aNBT, "gt.mlast.cooldown."+i, UT_CH.STL.toLongArray(mLRFCooldownCounters[i]));
		}
		UT.NBT.setNumber(aNBT, "gt.mtransfer", mTransferredAmount);
	}

	@Override
	public NBTTagCompound writeItemNBT2(NBTTagCompound aNBT) {
		// 用于在拆下后保留橡胶圈
		UT.NBT.setBoolean(aNBT, NBT_ADD_BOOL + ".fc", mFlowControl);
		if (isFoamDried()){
			UT.NBT.setNumber(aNBT, NBT_ADD_BYTE + ".dir", mFluidDir);
			// 默认值不为零（或者可能不为零）的需要专门设置
			if (SIDES_VALID[mFluidDir]) aNBT.setByte(NBT_ADD_BYTE + ".dir", (byte) UT_CH.NBT.toItemNumber(mFluidDir));
			if (mFluidMode != Mode.DEFAULT) aNBT.setByte(NBT_ADD_BYTE + ".mode", (byte) UT_CH.NBT.toItemNumber(mFluidMode.ordinal()));
		}
		return super.writeItemNBT2(aNBT);
	}
	
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (isClientSide()) return 0;
		if (aTool.equals(TOOL_wrench) && aSneaking && mAllowSwitchFC) {
			// 先清空平滑的数据
			for (FluidTankGT tTank : mTanks) resetOutputSoft(tTank);
			final int tRubberCount = getRubberCount();
			if (mFlowControl) {
				// 有橡胶圈，取下
				ItemStack tRubber = OP.ring.mat(MT.Rubber, tRubberCount);
				if (!(aPlayer instanceof EntityPlayer) || !UT.Inventories.addStackToPlayerInventory((EntityPlayer)aPlayer, tRubber)) ST.place(getWorld(), getOffset(aSide, 1), tRubber);
				mFlowControl = F;
				updateClientData();
				causeBlockUpdate();
//				if (aChatReturn != null) aChatReturn.add("No flow controlled");
				return 10000;
			}
			// 没有，尝试装上橡胶圈
			if (UT.Entities.hasInfiniteItems(aPlayer)) {
				// 创造模式情况，不减少
				mFlowControl = T;
				updateClientData();
				causeBlockUpdate();
//				if (aChatReturn != null) aChatReturn.add("Flow Controlled, pipe will keep the flow rate");
				return 10000;
			}
			if (aPlayerInventory != null) {
				// 生存模式，获取玩家背包
				// 先判断是否有足够的物品
				boolean tEnough = F;
				int tCounter = tRubberCount;
				List<Pair<Integer, Integer>> tRightIndex = new LinkedList<>(); // 用于记录符合条件的槽位和需要消除的数目
				ItemStack tItemStack;
				OreDictItemData tData;
				for (int i = 0, j = aPlayerInventory.getSizeInventory(); i < j; i++) {
					tItemStack = aPlayerInventory.getStackInSlot(i);
					tData = OM.data(tItemStack);
					if (tData != null && tData.mPrefix == OP.ring) {
						assert tData.mMaterial != null;
						if (tData.mMaterial.mMaterial == MT.Rubber || MT.Rubber.mToThis.contains(tData.mMaterial.mMaterial)) {
							tCounter -= tItemStack.stackSize;
							if (tCounter <= 0) {
								tRightIndex.add(Pair.of(i, tItemStack.stackSize + tCounter));
								tEnough = T;
								break;
							} else {
								tRightIndex.add(Pair.of(i, tItemStack.stackSize));
							}
						}
					}
				}
				if (tEnough) {
					// 确实有足够的物品，放心循环删除
					for (Pair<Integer, Integer> tPair : tRightIndex) {
						if (aPlayer == null) aPlayerInventory.decrStackSize(tPair.first(), tPair.second());
						else ST.use(aPlayer, T, aPlayerInventory.getStackInSlot(tPair.first()), tPair.second());
					}
					mFlowControl = T;
					updateClientData();
					causeBlockUpdate();
//					if (aChatReturn != null) aChatReturn.add("Flow Controlled, pipe will keep the flow rate");
					return 10000;
				} else {
					// 没有足够的物品，操作失败
					if (aChatReturn != null) aChatReturn.add(String.format("You dont have %d Rubber Rings in your Inventory!", tRubberCount));
					return 0;
				}
			}
		}
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		if (aTool.equals(TOOL_plunger)) return GarbageGT.trash(mTanks);
		if (aTool.equals(TOOL_thermometer)) {if (aChatReturn != null) aChatReturn.add("Temperature: " + mTemperature + "K"); return 10000;}
		if (aTool.equals(TOOL_magnifyingglass) && aSneaking) {
			if (!isCovered(UT.Code.getSideWrenching(aSide, aHitX, aHitY, aHitZ))) {
				if (aChatReturn != null) {
					boolean tPipeEmpty = T;
					for (FluidTankGT tTank : mTanks) if (!tTank.isEmpty()) {
						aChatReturn.add(tTank.content());
						tPipeEmpty = F;
					}
					
					Set<MultiTileEntityPipeFluid>
					tDone = new HashSetNoNulls<>(F, this),
					tNow  = new HashSetNoNulls<>(F, this),
					tNext = new HashSetNoNulls<>(),
					tSwap;
					
					List<FluidTankGT> tFluids = new ArrayListNoNulls<>();
					
					while (T) {
						for (MultiTileEntityPipeFluid tPipe : tNow) {
							for (FluidTankGT tTank : tPipe.mTanks) if (!tTank.isEmpty()) {
								boolean temp = T;
								for (FluidTankGT tFluid : tFluids) if (tFluid.contains(tTank.get())) {
									tFluid.add(tTank.amount());
									temp = F;
									break;
								}
								if (temp) tFluids.add(new FluidTankGT().setFluid(tTank));
							}
							
							for (byte tSide : ALL_SIDES_VALID) if (tPipe.connected(tSide)) {
								DelegatorTileEntity<TileEntity> tDelegator = tPipe.getAdjacentTileEntity(tSide);
								if (tDelegator.mTileEntity instanceof MultiTileEntityPipeFluid) {
									if (tDone.add((MultiTileEntityPipeFluid)tDelegator.mTileEntity)) {
										tNext.add((MultiTileEntityPipeFluid)tDelegator.mTileEntity);
									}
								}
							}
						}
						if (tNext.isEmpty()) break;
						tSwap = tNow;
						tNow  = tNext;
						tNext = tSwap;
						tNext.clear();
					}
					
					if (tFluids.isEmpty()) {
						aChatReturn.add("=== This Fluid Pipe Network is empty ===");
					} else {
						if (tPipeEmpty) aChatReturn.add("This particular Pipe Segment is currently empty");
						aChatReturn.add("=== This Fluid Pipe Network contains: ===");
						for (FluidTankGT tFluid : tFluids) aChatReturn.add(tFluid.content());
					}
				}
				return mTanks.length;
			}
		}

		// GTCH, 直接扳手改为显示状态
		if (aTool.equals(TOOL_magnifyingglass)) {
			checkConnection();
			if (isFoamDried()) {
				mMarkBuffer = 128;
				mOutMark = T;
			}
			if (aChatReturn != null) {
				aChatReturn.add(mFlowControl?"Stable Flow Rate":"Unstable Flow Rate");
				switch (mFluidMode) {
					case LIMIT: aChatReturn.add("Limit Mode, Only output to Marked Side");break;
					case PRIORITY: aChatReturn.add("Priority Mode, output to Marked Side First");break;
					case DEFAULT:
					default: aChatReturn.add("Default Mode");break;
				}
				if (mCapacityLimit > 0) {
					aChatReturn.add("Capacity Limited: ");
					aChatReturn.add("Limit: " + mCapacityLimit + ", Capacity: " + mTanks[0].capacity());
				}
			}
			return 1;
		}

		// GTCH, 使用活动扳手调整限制输出方向
		if (aTool.equals(TOOL_monkeywrench)) {
			byte aTargetSide = UT.Code.getSideWrenching(aSide, aHitX, aHitY, aHitZ);
			if (connected(aTargetSide)) {
				changeFluidMode(aTargetSide, aSneaking);
				return 2500;
			} else {
				return 0;
			}
		}

		// TODO: 使用阀门控制容量
		// GTCH，暂定直接使用螺丝刀调整容量
		if (aTool.equals(TOOL_screwdriver)) {
			mCapacityLimit = aSneaking ? changeModeRed(mCapacityLimit, MAX_LIMIT) : changeModeAdd(mCapacityLimit, MAX_LIMIT);
			long tCapacity = mCapacity >> mCapacityLimit;
			if (tCapacity <= 0 || mCapacityLimit == MAX_LIMIT) tCapacity = 0;
			// 调整容量之前先将多余流体尝试输出到相邻的容器
			clearTanks(tCapacity);
			for (FluidTankGT tank : mTanks) {
				tank.setCapacity(tCapacity);
			}
			if (aChatReturn != null) aChatReturn.add("Limit: " + mCapacityLimit);
			if (aChatReturn != null) aChatReturn.add("Capacity: " + tCapacity);
			return 1000;
		}
		return 0;
	}

	private void changeFluidMode(byte aTargetSide, boolean aReverse) {
		// 先让模式合法
		checkConnection();
		// 切换模式
		if (mFluidDir == aTargetSide && mFluidMode == Mode.LIMIT) {
			if (aReverse)
				mFluidMode = Mode.PRIORITY;
			else {
				mFluidDir = SIDE_ANY;
				mFluidMode = Mode.DEFAULT;
			}
		} else
		if (mFluidDir == aTargetSide && mFluidMode == Mode.PRIORITY) {
			if (aReverse) {
				mFluidDir = SIDE_ANY;
				mFluidMode = Mode.DEFAULT;
			}
			else
				mFluidMode = Mode.LIMIT;
		} else {
			mFluidDir = aTargetSide;
			mFluidMode = aReverse?Mode.LIMIT:Mode.PRIORITY;
		}
	}

	private byte changeModeAdd(byte aCurrentMode, byte aMaxMode) {
		return aCurrentMode < aMaxMode ? (byte) (aCurrentMode + 1) : 0;
	}
	private byte changeModeRed(byte aCurrentMode, byte aMaxMode) {
		return aCurrentMode > 0 ? (byte) (aCurrentMode - 1) : aMaxMode;
	}

	static {
		LH_CH.add("gtch.tooltip.pipefluid.custom.1", "Use Wrench in sneaking to mount Rubber Rings from your Inventory");
		LH_CH.add("gtch.tooltip.pipefluid.custom.2", "Mount the Rubber Rings to stabilize Flow Rate");
		LH_CH.add("gtch.tooltip.pipefluid.custom.3", "Stable Flow Rate");
	}

	@Override
	public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
		toolTipsDescribe(aList);
		toolTipsUseful(aList);
		super.addToolTips(aList, aStack, aF3_H);
	}
	protected void toolTipsDescribe(List<String> aList) {
		aList.add(Chat.CYAN     + LH.get(LH.PIPE_STATS_BANDWIDTH) + UT.Code.makeString(mCapacity/2) + " L/t");
		aList.add(Chat.CYAN     + LH.get(LH.PIPE_STATS_CAPACITY) + UT.Code.makeString(mCapacity) + " L");
		if (mTanks.length > 1)
		aList.add(Chat.CYAN     + LH.get(LH.PIPE_STATS_AMOUNT) + mTanks.length);
	}
	protected void toolTipsUseful(List<String> aList) {
		if (mAllowSwitchFC && !mFlowControl) aList.add(Chat.GREEN + LH_CH.get("gtch.tooltip.pipefluid.custom.2"));
		if(mFlowControl) aList.add(Chat.GREEN + LH_CH.get("gtch.tooltip.pipefluid.custom.3"));
	}
	@Override
	protected void toolTipsImportant(List<String> aList) {
		if (mGasProof       ) aList.add(Chat.ORANGE     + LH.get(LH.TOOLTIP_GASPROOF));
		if (mAcidProof      ) aList.add(Chat.ORANGE     + LH.get(LH.TOOLTIP_ACIDPROOF));
		if (mPlasmaProof    ) aList.add(Chat.ORANGE     + LH.get(LH.TOOLTIP_PLASMAPROOF));
		super.toolTipsImportant(aList);
	}
	@Override
	protected void toolTipsHazard(List<String> aList) {
		aList.add(Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + mMaxTemperature + " K)");
		if (mContactDamage  ) aList.add(Chat.DRED       + LH.get(LH.HAZARD_CONTACT));
	}
	@Override
	protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
		super.toolTipsOther(aList, aStack, aF3_H);
		if (mAllowSwitchFC	) aList.add(Chat.DGRAY 		+ LH_CH.get("gtch.tooltip.pipefluid.custom.1"));
		aList.add(Chat.DGRAY 	+ LH.get(LH.TOOL_TO_SET_OUTPUT_MONKEY_WRENCH));
		aList.add(Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
		aList.add(Chat.DGRAY    + LH_CH.get(LH_CH.TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK));
	}
	
	private boolean mHasToAddTimer = T;
	
	@Override public void onUnregisterPre() {mHasToAddTimer = T;}

	protected void checkConnection() {
		if (!modeValid()) {
			mFluidDir = SIDE_ANY;
			mFluidMode = Mode.DEFAULT;
			for (FluidTankGT tTank : mTanks) resetOutputSoft(tTank);
		}
	}
	protected boolean modeValid() {
		return (mFluidDir == SIDE_ANY && mFluidMode == Mode.DEFAULT) || (mFluidDir != SIDE_ANY && connected(mFluidDir) && (mFluidMode == Mode.LIMIT || mFluidMode == Mode.PRIORITY));
	}

	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		super.onTick2(aTimer, aIsServerSide);
		if (aIsServerSide && mHasToAddTimer) {
			if (WD.even(this)) {
				GT_API_Proxy.SERVER_TICK_PRE.add(this);
			} else {
				GT_API_Proxy.SERVER_TICK_PR2.add(this);
			}
			mHasToAddTimer = F;
		}
		if (aIsServerSide) {
			if (isFoamDried() && mOutMark) {
				--mMarkBuffer;
				if (mMarkBuffer < 0) {
					mOutMark = F;
					mMarkBuffer = 0;
				}
			}
		}
	}
	
	@Override
	public void onCoordinateChange() {
		super.onCoordinateChange();
		GT_API_Proxy.SERVER_TICK_PRE.remove(this);
		GT_API_Proxy.SERVER_TICK_PR2.remove(this);
		onUnregisterPre();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onServerTickPre(boolean aFirst) {
		mTransferredAmount = 0;
		
		DelegatorTileEntity<MultiTileEntityPipeFluid>[] tAdjacentPipes = new DelegatorTileEntity[6];
		DelegatorTileEntity<IFluidHandler>[] tAdjacentTanks = new DelegatorTileEntity[6];
		DelegatorTileEntity<TileEntity>[] tAdjacentOther = new DelegatorTileEntity[6];
		
		for (byte tSide : ALL_SIDES_VALID) if (canEmitFluidsTo(tSide)) {
			DelegatorTileEntity<TileEntity> tTileEntity = getAdjacentTileEntity(tSide);
			if (tTileEntity != null) {
				if (tTileEntity.mTileEntity instanceof MultiTileEntityPipeFluid) {
					tAdjacentPipes[tSide] = new DelegatorTileEntity<>((MultiTileEntityPipeFluid)tTileEntity.mTileEntity, tTileEntity);
				} else if (tTileEntity.mTileEntity instanceof IFluidHandler) {
					tAdjacentTanks[tSide] = new DelegatorTileEntity<>((IFluidHandler)tTileEntity.mTileEntity, tTileEntity);
				} else {
					tAdjacentOther[tSide] = tTileEntity;
				}
			}
		}
		
		boolean tCheckTemperature = T;

		for (FluidTankGT tTank : mTanks) {
			// GTCH, 计算平滑的输出，只有限制流速的才会进行计算
			if (mFlowControl) setOutputSoft(tTank);

			FluidStack tFluid = tTank.get();
			if (tFluid != null && tFluid.amount > 0) {
				mTemperature = (tCheckTemperature ? FL.temperature(tFluid) : Math.max(mTemperature, FL.temperature(tFluid)));
				tCheckTemperature = F;
				
				if (!mGasProof && FL.gas(tFluid)) {
					mTransferredAmount += GarbageGT.trash(tTank, 8);
					UT.Sounds.send(worldObj, SFX.MC_FIZZ, 1.0F, 1.0F, getCoords());
					try {for (Entity tEntity : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box(-2, -2, -2, +3, +3, +3))) UT.Entities.applyTemperatureDamage(tEntity, mTemperature, 2.0F, 10.0F);} catch(Throwable e) {e.printStackTrace(ERR);}
				}
				
				if (!mPlasmaProof && FL.plasma(tFluid)) {
					mTransferredAmount += GarbageGT.trash(tTank, 64);
					UT.Sounds.send(worldObj, SFX.MC_FIZZ, 1.0F, 1.0F, getCoords());
					try {for (Entity tEntity : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box(-2, -2, -2, +3, +3, +3))) UT.Entities.applyTemperatureDamage(tEntity, mTemperature, 2.0F, 10.0F);} catch(Throwable e) {e.printStackTrace(ERR);}
				}
				
				if (!mAcidProof && FL.acid(tFluid)) {
					mTransferredAmount += GarbageGT.trash(tTank, 16);
					UT.Sounds.send(worldObj, SFX.MC_FIZZ, 1.0F, 0.5F, getCoords());
					try {for (Entity tEntity : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box(-1, -1, -1, +2, +2, +2))) UT.Entities.applyChemDamage(tEntity, 2);} catch(Throwable e) {e.printStackTrace(ERR);}
					if (rng(100) == 0) {
						GarbageGT.trash(mTanks);
						setToAir();
						return;
					}
				}
			}
			
			if (mTemperature > mMaxTemperature) {
				setOnFire();
				if (rng(100) == 0) {
					GarbageGT.trash(mTanks);
					setToFire();
					return;
				}
			}
			
			if (tTank.has()) {
				switch (mFluidMode) {
					case LIMIT:
						if (mFlowControl) distributeLimitFC(tTank, tAdjacentPipes, tAdjacentTanks, tAdjacentOther);
						else distributeLimit(tTank, tAdjacentPipes, tAdjacentTanks, tAdjacentOther);
						break;
					case PRIORITY:
						if (mFlowControl) distributePriorityFC(tTank, tAdjacentPipes, tAdjacentTanks, tAdjacentOther);
						else distributePriority(tTank, tAdjacentPipes, tAdjacentTanks, tAdjacentOther);
						break;
					case DEFAULT: default:
						if (mFlowControl) distributeFC(tTank, tAdjacentPipes, tAdjacentTanks, tAdjacentOther);
						else distribute(tTank, tAdjacentPipes, tAdjacentTanks, tAdjacentOther);
						break;
				}
			}

			resetLastReceivedFrom(tTank.mIndex);
			if (mFlowControl) oAmounts[tTank.mIndex] = tTank.amount();
		}
		
		if (tCheckTemperature) {
			long tEnvTemp = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
			if (mTemperature < tEnvTemp) mTemperature++; else if (mTemperature > tEnvTemp) mTemperature--;
		}
	}

	// GTCH, 用于给重置防倒流加入一个延迟
	protected void resetLastReceivedFrom(int tTankIdx) {
		for (byte tSide : ALL_SIDES_VALID) {
			if (mLRFCooldownCounters[tTankIdx][tSide] > 0) {
				--mLRFCooldownCounters[tTankIdx][tSide];
			}
			else {
				mLastReceivedFrom[tTankIdx] &= ~SBIT[tSide]; // 每个方向分别重置
				mLRFCooldownCounters[tTankIdx][tSide] = 0;
			}
		}
	}
	protected void setLastReceivedFrom(int tTankIdx, byte aSide) {
		if (SIDES_VALID[aSide]) mLRFCooldownCounters[tTankIdx][aSide] = LRF_COOLDOWN_NUM;
		else Arrays.fill(mLRFCooldownCounters[tTankIdx], LRF_COOLDOWN_NUM);
		mLastReceivedFrom[tTankIdx] |= SBIT[aSide];
	}

	protected boolean coverCheck(byte aSide, FluidStack aFluid) {
		return isCovered(aSide) && mCovers.mBehaviours[aSide].interceptFluidDrain(aSide, mCovers, aSide, aFluid);
	}

	protected byte handleDistributeOther(FluidTankGT aTank, DelegatorTileEntity<TileEntity> aTEOther) {
		Block tBlock = aTEOther.getBlock();
		// Filling up Cauldrons from Vanilla. Yes I need to check for both to make this work. Some Mods override the Cauldron in a bad way.
		if ((tBlock == Blocks.cauldron || tBlock instanceof BlockCauldron) && aTank.has(334) && FL.water(aTank.get())) {
			switch(aTEOther.getMetaData()) {
				case 0:
					if (aTank.drainAll(1000)) {aTEOther.setMetaData(3); break;}
					if (aTank.drainAll( 667)) {aTEOther.setMetaData(2); break;}
					if (aTank.drainAll( 334)) {aTEOther.setMetaData(1); break;}
					break;
				case 1:
					if (aTank.drainAll( 667)) {aTEOther.setMetaData(3); break;}
					if (aTank.drainAll( 334)) {aTEOther.setMetaData(2); break;}
					break;
				case 2:
					if (aTank.drainAll( 334)) {aTEOther.setMetaData(3); break;}
					break;
				default: break;
			}
		}
		return aTEOther.getMetaData();
	}

	// GTCH, 更加智能的获取周围管道的容器的函数，可以保证多合一管道在传输液体时尽量维持在同一位置
	// 需要注意覆盖版或者手动设置的情况
	protected FluidTankGT getAdjacentPipeTankFillable(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid> aAdjacentPipe) {
		FluidTankGT tTank = (FluidTankGT)aAdjacentPipe.mTileEntity.getFluidTankFillable(aAdjacentPipe.mSideOfTileEntity, aTank.get());
		// 对于多合一的管道相互连接的情况，并且管道数目相同的情况，进行专门考虑
		// tTank 为 null 时表示通过了原版的方法禁用了输入，否则一定可以进行输入（说明这个修改一定会兼容原版的逻辑）
		if (tTank!=null && mTanks.length > 1 && mTanks.length == aAdjacentPipe.mTileEntity.mTanks.length) {
			if (aAdjacentPipe.mTileEntity.canTankIdxFill(aTank.mIndex, aTank.get()))
				return aAdjacentPipe.mTileEntity.mTanks[aTank.mIndex];
			else
				return null; // 对于相同尺寸的多合一管道，如果相同的管道口不能填充，则完全不允许填充（禁止串味）
		}
		return tTank;
	}
	// GTCH, 对于多合一管道检测某个位置能否输入
	// TODO 后续自定义具体管道输入可能会通过修改这个函数实现
	protected boolean canTankIdxFill(int aTankIdx, FluidStack aFluidToFill) {
		if (aTankIdx >= mTanks.length) return F;
		if (mTanks[aTankIdx].isEmpty() || mTanks[aTankIdx].contains(aFluidToFill)) return T;
		return F;
	}
	// 用来管道间传输液体时计算压力，避免流速控制管道清空时流速慢的问题
	protected long getPressure(int aTankIdx) {
		if (mFlowControl && mTanks[aTankIdx].amount()*4 <= mTanks[aTankIdx].capacity()*3)
			return mTanks[aTankIdx].isEmpty()?0:1;
		return mTanks[aTankIdx].amount();
	}

	// 默认的输出流体逻辑
	public void distribute(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 直接调用内部默认模式减少代码量
		distributeDefault_(ALL_SIDES_VALID, aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
	}

	// GTCH，限制方向输出，没有流速控制。限制方向输出可以对代码进行一定简化，所以重新写了一份
	protected void distributeLimit(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 由于限制方向输出，相邻的只有一个，为了格式一致还是都采用数组的形式
		// 非法调用回到默认模式
		if (mFluidDir == SIDE_ANY || mFluidMode != Mode.LIMIT) {distribute(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 如果覆盖版禁止了直接退出，由于只有一个面不考虑开销问题
		if (coverCheck(mFluidDir, aTank.get())) return;
		// 同样还是先考虑炼药锅的情况
		if (aAdjacentOther[mFluidDir] != null) {
			handleDistributeOther(aTank, aAdjacentOther[mFluidDir]);
			// 所有事完成，退出
			return;
		}
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// 由于只有一个输出，算法可以进行一定简化，不过依旧保持结果一致
		long tAmount;
		// 由于限制了输出方向，不存在倒流的情况
		// 先处理储罐的情况
		if (aAdjacentTanks[mFluidDir] != null) {
			// 检测储罐能够填充
			if (aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.make(1), F) > 0 || aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.get(Long.MAX_VALUE), F) > 0) {
				// 直接进行填充，按照原本逻辑就是填充一半容量的流体
				tAmount = UT.Code.divup(aTank.amount(), 2);
				mTransferredAmount += aTank.remove(FL.fill(aAdjacentTanks[mFluidDir], aTank.get(tAmount), T));
				// 所有事完成，退出
				return;
			}
			// 所有事完成，退出
			return;
		}
		// 再处理管道的情况
		if (aAdjacentPipes[mFluidDir] != null) {
			// Check if the Pipe can be filled with this Fluid.
			FluidTankGT tTank = getAdjacentPipeTankFillable(aTank, aAdjacentPipes[mFluidDir]);
			if (tTank == null) return;
			long toPressure = aAdjacentPipes[mFluidDir].mTileEntity.getPressure(tTank.mIndex);
			long fromPressure = getPressure(aTank.mIndex);
			// 即使压力相同也要设置接收方向来防止倒流
			if (toPressure <= fromPressure) aAdjacentPipes[mFluidDir].mTileEntity.setLastReceivedFrom(tTank.mIndex, aAdjacentPipes[mFluidDir].mSideOfTileEntity);
			if (toPressure < fromPressure) {
				// 直接进行填充，按照原本逻辑是这个结果
				tAmount = UT.Code.divup(fromPressure + toPressure, 2);
				mTransferredAmount += aTank.remove(tTank.add(aTank.amount(tAmount-toPressure), aTank.get()));
				// 流体超过一半，存在一个压力
				tAmount = (aTank.amount() - aTank.capacity()/2);
				if (tAmount > 0 && !tTank.isFull()) mTransferredAmount += aTank.remove(tTank.add(aTank.amount(tAmount), aTank.get()));
				// 所有事完成，退出
				return;
			}
			// 所有事完成，退出
			return;
		}
	}

	// GTCH，优先方向输出，没有流速控制。优先方向输出可以对代码进行一定简化，所以重新写了一份
	protected void distributePriority(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 非法调用回到默认模式
		if (mFluidDir == SIDE_ANY || mFluidMode != Mode.PRIORITY) {distribute(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 如果覆盖版禁止了直接进入内部的没有流速控制的默认模式
		if (coverCheck(mFluidDir, aTank.get())) {distributeDefault_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 同样还是先考虑炼药锅的情况
		if (aAdjacentOther[mFluidDir] != null) {
			// 需要了解炼药锅是否填满
			byte tOut = handleDistributeOther(aTank, aAdjacentOther[mFluidDir]);
			// 炼药锅填满了或者还存在压力，进入默认模式
			if ((tOut == 3) || (aTank.amount() * 2 > aTank.capacity())) distributeDefault_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
			// 所有事完成，退出
			return;
		}
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// 由于只有一个输出，算法可以进行一定简化，不过依旧保持结果一致
		long tAmount;
		// 倒流情况在默认模式考虑
		// 先处理储罐的情况
		if (aAdjacentTanks[mFluidDir] != null) {
			// 检测储罐能够填充
			if (aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.make(1), F) > 0 || aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.get(Long.MAX_VALUE), F) > 0) {
				// 直接进行填充，按照原本逻辑就是填充一半容量的流体
				tAmount = UT.Code.divup(aTank.amount(), 2);
				mTransferredAmount += aTank.remove(FL.fill(aAdjacentTanks[mFluidDir], aTank.get(tAmount), T));
				// 是否还有压力，有则进入默认情况
				if (aTank.amount() * 2 > aTank.capacity()) distributeDefault_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
				// 所有事完成，退出
				return;
			}
			// 储罐填充失败，进入默认模式
			distributeDefault_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
			// 所有事完成，退出
			return;
		}
		// 再处理管道的情况
		if (aAdjacentPipes[mFluidDir] != null) {
			// Check if the Pipe can be filled with this Fluid.
			FluidTankGT tTank = getAdjacentPipeTankFillable(aTank, aAdjacentPipes[mFluidDir]);
			if (tTank != null) {
				long toPressure = aAdjacentPipes[mFluidDir].mTileEntity.getPressure(tTank.mIndex);
				long fromPressure = getPressure(aTank.mIndex);
				// 即使压力相同也要设置接收方向来防止倒流
				if (toPressure <= fromPressure) aAdjacentPipes[mFluidDir].mTileEntity.setLastReceivedFrom(tTank.mIndex, aAdjacentPipes[mFluidDir].mSideOfTileEntity);
				if (toPressure < fromPressure) {
					// 直接进行填充，按照原本逻辑是这个结果
					tAmount = UT.Code.divup(fromPressure + toPressure, 2);
					mTransferredAmount += aTank.remove(tTank.add(aTank.amount(tAmount-toPressure), aTank.get()));
					// 流体超过一半，存在一个压力
					tAmount = (aTank.amount() - aTank.capacity()/2);
					if (tAmount > 0 && !tTank.isFull()) mTransferredAmount += aTank.remove(tTank.add(aTank.amount(tAmount), aTank.get()));
					// 是否还有压力，有则进入默认情况
					if (aTank.amount() * 2 > aTank.capacity()) distributeDefault_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
					// 所有事完成，退出
					return;
				}
			}
			// 管道填充失败，进入默认情况
			distributeDefault_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
			// 所有事完成，退出
			return;
		}
	}

	// GTCH, 内部的没有流速控制的默认模式，只考虑设定的方向（注意由于输出方向不同了，还是要检查覆盖版）
	@SuppressWarnings("rawtypes")
	protected void distributeDefault_(byte[] aSides, FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// Top Priority is filling Cauldrons and other specialties.
		for (byte tSide : aSides) if (aAdjacentOther[tSide] != null) {
			// Covers let distribution happen, right?
			if (coverCheck(tSide, aTank.get())) continue;
			handleDistributeOther(aTank, aAdjacentOther[tSide]);
		}
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// Compile all possible Targets into one List.
		List<DelegatorTileEntity> tTanks = new ArrayListNoNulls<>();
		List<Pair<FluidTankGT, Long>> tPipes = new ArrayListNoNulls<>();
		// Amount to check for Distribution
		long fromPressure = getPressure(aTank.mIndex);
		long tAmount = fromPressure;
		// Count all Targets. Also includes THIS for even distribution, thats why it starts at 1.
		int tTargetCount = 1;
		// Put Targets into Lists.
		for (byte tSide : aSides) {
			// Don't you dare flow backwards!
			if (FACE_CONNECTED[tSide][mLastReceivedFrom[aTank.mIndex]]) continue;
			// Are we even connected to this Side? (Only gets checked due to the Cover check being slightly expensive)
			if (!canEmitFluidsTo(tSide)) continue;
			// Covers let distribution happen, right?
			if (coverCheck(tSide, aTank.get())) continue;
			// Is it a Pipe?
			if (aAdjacentPipes[tSide] != null) {
				// Check if the Pipe can be filled with this Fluid.
				FluidTankGT tTank = getAdjacentPipeTankFillable(aTank, aAdjacentPipes[tSide]);
				if (tTank == null) continue;
				long toPressure = aAdjacentPipes[tSide].mTileEntity.getPressure(tTank.mIndex);
				// 即使压力相同也要设置接收方向来防止倒流
				if (toPressure <= fromPressure) aAdjacentPipes[tSide].mTileEntity.setLastReceivedFrom(tTank.mIndex, aAdjacentPipes[tSide].mSideOfTileEntity);
				if (toPressure < fromPressure) {
					// Add to a random Position in the List.
					tPipes.add(rng(tPipes.size()+1), Pair.of(tTank, toPressure));
					// For Balancing the Pipe Output.
					tAmount += toPressure;
					// One more Target.
					tTargetCount++;
					// Done everything.
					continue;
				}
				// Done everything.
				continue;
			}
			// No Tank? Nothing to do then.
			if (aAdjacentTanks[tSide] == null) continue;
			// Check if the Tank can be filled with this Fluid.
			if (aAdjacentTanks[tSide].mTileEntity.fill(aAdjacentTanks[tSide].getForgeSideOfTileEntity(), aTank.make(1), F) > 0 || aAdjacentTanks[tSide].mTileEntity.fill(aAdjacentTanks[tSide].getForgeSideOfTileEntity(), aTank.get(Long.MAX_VALUE), F) > 0) {
				// Add to a random Position in the List.
				tTanks.add(rng(tTanks.size()+1), aAdjacentTanks[tSide]);
				// One more Target.
				tTargetCount++;
				// Done everything.
				continue;
			}
		}
		// No Targets? Nothing to do then.
		if (tTargetCount <= 1) return;
		// Amount to distribute normally.
		tAmount = UT.Code.divup(tAmount, tTargetCount);
		// Distribute to Pipes first.
		for (Pair<FluidTankGT, Long> tPair : tPipes) mTransferredAmount += aTank.remove(tPair.first().add(aTank.amount(tAmount-tPair.second()), aTank.get()));
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// Distribute to Tanks afterwards.
		for (DelegatorTileEntity tTank : tTanks) mTransferredAmount += aTank.remove(FL.fill(tTank, aTank.get(tAmount), T));
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// No Targets? Nothing to do then.
		if (tPipes.isEmpty()) return;
		// And then if there still is pressure, distribute to Pipes again.
		tAmount = (aTank.amount() - aTank.capacity()/2) / tPipes.size();
		if (tAmount > 0) for (Pair<FluidTankGT, Long> tPair : tPipes) mTransferredAmount += aTank.remove(tPair.first().add(aTank.amount(tAmount), aTank.get()));
	}

	// GTCH，默认模式，有流速控制
	public void distributeFC(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 非法调用回到默认模式
		if (!mFlowControl) {distribute(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 直接调用内部默认模式减少代码量
		distributeDefaultFC_(ALL_SIDES_VALID, aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
	}

	// GTCH，限制方向输出，有流速控制。限制方向输出可以对代码进行一定简化，所以重新写了一份
	protected void distributeLimitFC(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 由于限制方向输出，相邻的只有一个，为了格式一致还是都采用数组的形式
		// 非法调用回到默认模式
		if (mFluidDir == SIDE_ANY || mFluidMode != Mode.LIMIT) {distributeFC(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		if (!mFlowControl) {distributeLimit(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 如果覆盖版禁止了直接退出，由于只有一个面不考虑开销问题
		if (coverCheck(mFluidDir, aTank.get())) return;
		// 同样还是先考虑炼药锅的情况
		if (aAdjacentOther[mFluidDir] != null) {
			handleDistributeOther(aTank, aAdjacentOther[mFluidDir]);
			// 所有事完成，退出
			return;
		}
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// 由于只有一个输出，速度就是这个值
		final long tAmount = mOutputs[aTank.mIndex];
		// 由于限制了输出方向，不存在倒流的情况
		// 先处理储罐的情况
		if (aAdjacentTanks[mFluidDir] != null) {
			// 检测储罐能够填充
			if (aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.make(1), F) > 0 || aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.get(Long.MAX_VALUE), F) > 0) {
				// 直接进行填充
				mTransferredAmount += aTank.remove(FL.fill(aAdjacentTanks[mFluidDir], aTank.get(tAmount), T));
				// 所有事完成，退出
				return;
			}
			// 所有事完成，退出
			return;
		}
		// 再处理管道的情况
		if (aAdjacentPipes[mFluidDir] != null) {
			// Check if the Pipe can be filled with this Fluid.
			FluidTankGT tTank = getAdjacentPipeTankFillable(aTank, aAdjacentPipes[mFluidDir]);
			if (tTank != null) {
				// 设置输出管道的接受流体方向，无论是否填满都要防止倒流
				aAdjacentPipes[mFluidDir].mTileEntity.setLastReceivedFrom(tTank.mIndex, aAdjacentPipes[mFluidDir].mSideOfTileEntity);
				if (!tTank.isFull()) {
					// 直接进行填充，虽然一定不会超过容量，但是为了以防万一还是加上 aTank.amount()
					mTransferredAmount += aTank.remove(tTank.add(aTank.amount(tAmount), aTank.get()));
					// 所有事完成，退出
					return;
				}
			}
			// 所有事完成，退出
			return;
		}
	}

	// GTCH，优先方向输出，有流速控制
	protected void distributePriorityFC(FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 非法调用回到默认模式
		if (mFluidDir == SIDE_ANY || mFluidMode != Mode.PRIORITY) {distributeFC(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		if (!mFlowControl) {distributePriority(aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 如果覆盖版禁止了直接进入内部的没有流速控制的默认模式
		if (coverCheck(mFluidDir, aTank.get())) {distributeDefaultFC_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther); return;}
		// 同样还是先考虑炼药锅的情况
		if (aAdjacentOther[mFluidDir] != null) {
			// 需要了解炼药锅是否填满
			byte tOut = handleDistributeOther(aTank, aAdjacentOther[mFluidDir]);
			// 炼药锅填满了或者还存在压力，进入默认模式
			if ((tOut == 3) || (aTank.amount() * 2 > aTank.capacity())) distributeDefaultFC_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
			// 所有事完成，退出
			return;
		}
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// 由于只有一个输出，速度就是这个值
		final long tAmount = mOutputs[aTank.mIndex];
		// 倒流情况在默认模式考虑
		// 先处理储罐的情况
		if (aAdjacentTanks[mFluidDir] != null) {
			// 检测储罐能够填充
			if (aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.make(1), F) > 0 || aAdjacentTanks[mFluidDir].mTileEntity.fill(aAdjacentTanks[mFluidDir].getForgeSideOfTileEntity(), aTank.get(Long.MAX_VALUE), F) > 0) {
				// 直接进行填充
				mTransferredAmount += aTank.remove(FL.fill(aAdjacentTanks[mFluidDir], aTank.get(tAmount), T));
				// 是否还有压力，有则进入默认情况
				if (aTank.amount() * 2 > aTank.capacity()) distributeDefaultFC_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
				// 所有事完成，退出
				return;
			}
			// 储罐填充失败，进入默认模式
			distributeDefaultFC_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
			// 所有事完成，退出
			return;
		}
		// 再处理管道的情况
		if (aAdjacentPipes[mFluidDir] != null) {
			// Check if the Pipe can be filled with this Fluid.
			FluidTankGT tTank = getAdjacentPipeTankFillable(aTank, aAdjacentPipes[mFluidDir]);
			if (tTank != null) {
				// 设置输出管道的接受流体方向，无论是否填满都要防止倒流
				aAdjacentPipes[mFluidDir].mTileEntity.setLastReceivedFrom(tTank.mIndex, aAdjacentPipes[mFluidDir].mSideOfTileEntity);
				if(!tTank.isFull()){
					// 直接进行填充，虽然一定不会超过容量，但是为了以防万一还是加上 aTank.amount()
					mTransferredAmount += aTank.remove(tTank.add(aTank.amount(tAmount), aTank.get()));
					// 是否还有压力，有则进入默认情况
					if (aTank.amount() * 2 > aTank.capacity()) distributeDefaultFC_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
					// 所有事完成，退出
					return;
				}
			}
			// 填入管道失败，进入默认情况
			distributeDefaultFC_(ALL_SIDES_VALID_BUT[mFluidDir], aTank, aAdjacentPipes, aAdjacentTanks, aAdjacentOther);
			// 所有事完成，退出
			return;
		}
	}

	// GTCH, 内部的有流速控制的默认模式，只考虑设定的方向，不检查非法调用（注意由于输出方向不同了，还是要检查覆盖版）
	@SuppressWarnings("rawtypes")
	protected void distributeDefaultFC_(byte[] aSides, FluidTankGT aTank, DelegatorTileEntity<MultiTileEntityPipeFluid>[] aAdjacentPipes, DelegatorTileEntity<IFluidHandler>[] aAdjacentTanks, DelegatorTileEntity<TileEntity>[] aAdjacentOther) {
		// 无论怎样还是得先处理炼药锅，这个不太好处理
		for (byte tSide : aSides) if (aAdjacentOther[tSide] != null) {
			// Covers let distribution happen, right?
			if (coverCheck(tSide, aTank.get())) continue;
			handleDistributeOther(aTank, aAdjacentOther[tSide]);
		}
		// Check if we are empty.
		if (aTank.isEmpty()) return;
		// 输出逻辑是，对于所有的非输入面，按照流速均分
		// 所有输出的对象，还是需要区分管道和储罐（管道效率更高）
		List<DelegatorTileEntity> tTanks = new LinkedList<>();
		List<FluidTankGT> tPipes = new LinkedList<>();
		// 流速计算，选用平滑后的流速
		long tAmount = mOutputs[aTank.mIndex];
		// 对象数目，控制流速不考虑自身
		int tTargetCount = 0;
		// Put Targets into Lists.
		for (byte tSide : aSides) {
			// Don't you dare flow backwards!
			if (FACE_CONNECTED[tSide][mLastReceivedFrom[aTank.mIndex]]) continue;
			// Are we even connected to this Side? (Only gets checked due to the Cover check being slightly expensive)
			if (!canEmitFluidsTo(tSide)) continue;
			// Covers let distribution happen, right?
			if (coverCheck(tSide, aTank.get())) continue;
			// Is it a Pipe?
			if (aAdjacentPipes[tSide] != null) {
				// Check if the Pipe can be filled with this Fluid.
				FluidTankGT tTank = getAdjacentPipeTankFillable(aTank, aAdjacentPipes[tSide]);
				if (tTank != null) {
					// 设置输出管道的接受流体方向，无论是否填满都要防止倒流
					aAdjacentPipes[tSide].mTileEntity.setLastReceivedFrom(tTank.mIndex, aAdjacentPipes[tSide].mSideOfTileEntity);
					// 必须要非空的管道才能加入队列
					if (!tTank.isFull()) {
						// 直接加入 List
						tPipes.add(tTank);
						// One more Target.
						++tTargetCount;
						// Done everything.
						continue;
					}
				}
				// Done everything.
				continue;
			}
			// No Tank? Nothing to do then.
			if (aAdjacentTanks[tSide] == null) continue;
			// Check if the Tank can be filled with this Fluid.
			if (aAdjacentTanks[tSide].mTileEntity.fill(aAdjacentTanks[tSide].getForgeSideOfTileEntity(), aTank.make(1), F) > 0 || aAdjacentTanks[tSide].mTileEntity.fill(aAdjacentTanks[tSide].getForgeSideOfTileEntity(), aTank.get(Long.MAX_VALUE), F) > 0) {
				// 直接加入 List
				tTanks.add(aAdjacentTanks[tSide]);
				// One more Target.
				++tTargetCount;
				// Done everything.
				continue;
			}
		}
		// No Targets? Nothing to do then. 注意此时是 0 表示没有目标
		if (tTargetCount <= 0) return;
		// 使用另外一种算法实现均分
		int tRemain = (int) (tAmount%tTargetCount);
		tAmount = tAmount/tTargetCount;
		if (tAmount > 0) {
			// 直接完全均分
			for (FluidTankGT tPipe : tPipes) mTransferredAmount += aTank.remove(tPipe.add(aTank.amount(tAmount), aTank.get()));
			for (DelegatorTileEntity tTank : tTanks) mTransferredAmount += aTank.remove(FL.fill(tTank, aTank.get(tAmount), T));
		}
		// 不再使用 else 来保证每一 tick 的 output 一般都能输出掉
		if (tRemain > 0) {
			// 剩下的从随机位置开始，依次分配
			if (mBeginIdx[aTank.mIndex]==SIDE_INVALID || mBeginIdx[aTank.mIndex]>=tTargetCount) mBeginIdx[aTank.mIndex] = (byte)rng(tTargetCount);
			int tBeginIdx = mBeginIdx[aTank.mIndex];
			// 使用取模的方法合并讨论
			for (int tShiftIdx = 0; tShiftIdx < tRemain; ++tShiftIdx) {
				int tDistributed = (tBeginIdx+tShiftIdx)%tTargetCount;
				if (tDistributed < tPipes.size()) mTransferredAmount += aTank.remove(tPipes.get(tDistributed).add(aTank.amount(1), aTank.get()));
				else mTransferredAmount += aTank.remove(FL.fill(tTanks.get(tDistributed-tPipes.size()), aTank.get(1), T));
			}
			// 将其移动到下一个开始位置
			mBeginIdx[aTank.mIndex] += tRemain;
			mBeginIdx[aTank.mIndex] %= tTargetCount;
		}
		// 由于流速控制，即使塞不进去也不会改变其他方向流速
	}

	private void addInBuffer(int aIdx, long aInAmount) {
		mInBuffers.get(aIdx).addLast(aInAmount);
		while (mInBuffers.get(aIdx).size() > BUFFER_LENGTH) mInBuffers.get(aIdx).pollFirst();
	}
	private long getSmoothOutput(int aIdx) {
		// 输出则直接是对 buffer 的统计平均
		long tSum = 0;
		for (long tInAmount : mInBuffers.get(aIdx)) tSum += tInAmount;
		return UT.Code.divup(tSum, BUFFER_LENGTH);
	}

	//使用这个算法使输出平滑
	protected void setOutputSoft(FluidTankGT aTank) {
		if (aTank.amount() * 4 > aTank.capacity() * 3) {
			// 为了让更多时候都可以平滑流速，流速控制管道的压力判断更加宽裕
			// 超过压力时平滑算法失效
			mOutputs[aTank.mIndex] = UT.Code.divup(aTank.capacity(), 2);
			// 估计此时等效的 buffer 输入，减少对正常平滑的干扰
			addInBuffer(aTank.mIndex, Math.max(0, aTank.amount()-oAmounts[aTank.mIndex]+getSmoothOutput(aTank.mIndex)-mOutputs[aTank.mIndex]));
			return;
		}
		// 对于空管道专门优化
		if (aTank.isEmpty()) {
			addInBuffer(aTank.mIndex, 0);
			mOutputs[aTank.mIndex] = 0;
			return;
		}
		// 首先将输入量计入 buffer
		addInBuffer(aTank.mIndex, Math.max(0, aTank.amount()-oAmounts[aTank.mIndex]));
		// 输出则直接是对 buffer 的统计平均
		long tOut = getSmoothOutput(aTank.mIndex);
		if (tOut > 0) {
			mOutputs[aTank.mIndex] = aTank.amount(Math.min(tOut, UT.Code.divup(aTank.capacity(), 2))); // 添加限制最大流速
			return;
		}
		if (aTank.isEmpty()) return;
		// 对于 tOut 为零但是管道有残存流体的情况，为了防止残存流体停止流动，将残存流体放入 buffer 重新计算
		addInBuffer(aTank.mIndex, aTank.amount());
		mOutputs[aTank.mIndex] = aTank.amount(Math.min(getSmoothOutput(aTank.mIndex), UT.Code.divup(aTank.capacity(), 2)));
	}
	protected void resetOutputSoft(FluidTankGT aTank) {
		// 简单的重置
		mBeginIdx[aTank.mIndex] = SIDE_INVALID;
		mInBuffers.get(aTank.mIndex).clear();
		mOutputs[aTank.mIndex] = 0;
		oAmounts[aTank.mIndex] = 0;
	}

	@Override
	public boolean breakBlock() {
		clearTanks(0);
		// Drop, uh Inventory? Eh, it is a super Call that is needed regardless, just in case. 
		return super.breakBlock();
	}
	// 清空容器直到保留 aRemain 的流体，尝试移动多余流体到临近的容器
	protected void clearTanks(long aRemain) {
		// Do the same thing Factorio does and just dump Fluid to adjacent connected things.
		for (byte tSide : ALL_SIDES_VALID) if (canEmitFluidsTo(tSide)) {
			DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide);
			for (FluidTankGT tTank : mTanks) if (tTank.has(aRemain + 1)) {
				if (isCovered(tSide) && mCovers.mBehaviours[tSide].interceptFluidDrain(tSide, mCovers, tSide, tTank.get())) continue;
				mTransferredAmount += FL.move(tTank, tDelegator, tTank.amount() - aRemain);
			}
		}
		// And if that doesn't work, go to the trash!
		if (aRemain <= 0) {
			GarbageGT.trash(mTanks);
		} else {
			for (FluidTankGT tTank : mTanks) if (tTank.has(aRemain + 1)) {
				GarbageGT.trash(tTank, tTank.amount() - aRemain);
			}
		}
	}
	
	@Override
	public void onConnectionChange(byte aPreviousConnections) {
		super.onConnectionChange(aPreviousConnections);
		// 改变连接方式后需要重置均分的 idx
		Arrays.fill(mBeginIdx, SIDE_INVALID);
		// GTCH, 使用内部的改变连接方向来检测连接
		checkConnection();
		for (byte tSide : ALL_SIDES_VALID) {
			DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentInventoryUpdatable) {
				((ITileEntityAdjacentInventoryUpdatable)tDelegator.mTileEntity).adjacentInventoryUpdated(tDelegator.mSideOfTileEntity, this);
			}
		}
	}
	
	@Override public boolean canDrop(int aInventorySlot) {return F;}
	@Override public boolean isObstructingBlockAt2(byte aSide) {return mBlocking;} // Btw, Wires have this but Pipes don't. This is because Wires are flexible, while Pipes aren't.
	
	@Override public void onEntityCollidedWithBlock(Entity aEntity) {if (mContactDamage && !isFoamDried()) UT.Entities.applyTemperatureDamage(aEntity, mTemperature, 1, 5.0F);}
	
	@Override
	protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {
		if (SIDES_VALID[aSide] && !canAcceptFluidsFrom(aSide)) return null;
		for (FluidTankGT tTank : mTanks) if (tTank.contains(aFluidToFill)) return tTank; // 永远只填充相同液体种类的 tank，无论是否已经填满（默认就是这个逻辑，挺好）
		for (FluidTankGT tTank : mTanks) if (tTank.isEmpty()) return tTank;
		return null;
	}
	
	@Override
	protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {
		if (SIDES_VALID[aSide] && !canEmitFluidsTo(aSide)) return null;
		for (FluidTankGT tTank : mTanks) if (tTank.contains(aFluidToDrain)) return tTank;
		return null;
	}
	
	@Override protected IFluidTank[] getFluidTanks2(byte aSide) {return mTanks;}
	
	@Override
	public int fill(ForgeDirection aDirection, FluidStack aFluid, boolean aDoFill) {
		if (aFluid == null || aFluid.amount <= 0) return 0;
		FluidTankGT tTank = (FluidTankGT)getFluidTankFillable(UT.Code.side(aDirection), aFluid);
		if (tTank == null) return 0;
		int rFilledAmount = tTank.fill(aFluid, aDoFill);
		if (aDoFill) {
			if (rFilledAmount > 0) updateInventory();
			setLastReceivedFrom(tTank.mIndex, UT.Code.side(aDirection));
		}
		return rFilledAmount;
	}
	
	@Override public boolean canConnect(byte aSide, DelegatorTileEntity<TileEntity> aDelegator)     {return PipeCompat_CH.canConnectFluid(this, aDelegator, aDelegator.mSideOfTileEntity);}
	@Override public boolean canAutoConnect(byte aSide, DelegatorTileEntity<TileEntity> aDelegator) {return PipeCompat_CH.canAutoConnectFluid(this, aDelegator, aDelegator.mSideOfTileEntity);}


	// GTCH，根据状态修改是否可以输入输出
	public boolean canEmitFluidsTo(byte aSide) {
		return (mFluidMode == Mode.LIMIT) ? (SIDES_EQUAL[aSide][mFluidDir] && connected(aSide)) : connected(aSide);
	}
	public boolean canAcceptFluidsFrom(byte aSide) {
		if (mFluidMode == Mode.DEFAULT) {
			return connected(aSide);
		} else {
			return SIDES_ANY_BUT[aSide][mFluidDir] && connected(aSide);
		}
	}
	
	@Override public long getGibblValue                     (byte aSide) {long rAmount = 0; for (FluidTankGT tTank : mTanks) rAmount += tTank.amount(); return rAmount;}
	@Override public long getGibblMax                       (byte aSide) {return mTanks[0].capacity() * mTanks.length;}
	
	@Override public long getProgressValue                  (byte aSide) {return mTransferredAmount;}
	@Override public long getProgressMax                    (byte aSide) {return mTanks[0].capacity() * mTanks.length;}
	
	@Override public long getTemperatureValue               (byte aSide) {return mTemperature;}
	@Override public long getTemperatureMax                 (byte aSide) {return mMaxTemperature;}

	@Override public long getFlowrateValue					(byte aSide) {return mTransferredAmount;}
	@Override public long getFlowrateMax					(byte aSide) {return UT.Code.divup(mTanks[0].capacity() * mTanks.length, 2);}
	
	@Override public ITexture getTextureSide                (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {
		ITexture tBase = UT_CH.Texture.BlockTextureDefaultAO(mMaterial, getIconIndexSide(aSide, aConnections, aDiameter, aRenderPass), mRGBa, mIsGlowing, (aRenderPass <= 6 && !mIsGlowing));
		ITexture tMark = null;
		ITexture tRubber = null;
		// 合法部分才做 mark 和 rubber
		if (aRenderPass <= 6) {
			tMark = BlockTextureDefault.get(Textures.BlockIcons.ARROWS[mFluidMode.ordinal()][aSide][mFluidDir], mRGBaMark, mIsGlowing);
			if (mFlowControl && mAllowSwitchFC) {
				// 专门处理干掉的情况，干掉时不再需要侧边的橡胶材质
				if (isFoamDried()) tRubber = getTextureRubber(aDiameter);
				else tRubber = BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR, mRGBaRubber, mIsGlowing);
			}
		}
		return BlockTextureMulti.get(tBase, tRubber, tMark);
	}
	@Override public ITexture getTextureConnected           (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {
		ITexture tBase = UT_CH.Texture.BlockTextureDefaultAO(mMaterial, getIconIndexConnected(aSide, aConnections, aDiameter, aRenderPass), mRGBa, mIsGlowing, (aRenderPass <= 6 && !mIsGlowing));
		ITexture tRubber = null;
		// 同样合法部分才做 rubber
		if (aRenderPass <= 6) if (mFlowControl && mAllowSwitchFC) {
			tRubber = getTextureRubber(aDiameter);
		}
		return BlockTextureMulti.get(tBase, tRubber);
	}

	protected ITexture getTextureRubber(float aDiameter) {
		if (mTanks.length >= 9) {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_HUGE, mRGBaRubber, mIsGlowing);
		} else if (mTanks.length >= 4) {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_HUGE, mRGBaRubber, mIsGlowing);
		} else if (aDiameter < 0.37F) {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_TINY, mRGBaRubber, mIsGlowing);
		} else if (aDiameter < 0.49F) {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_SMALL, mRGBaRubber, mIsGlowing);
		} else if (aDiameter < 0.74F) {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_MEDIUM, mRGBaRubber, mIsGlowing);
		} else if (aDiameter < 0.99F) {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_LARGE, mRGBaRubber, mIsGlowing);
		} else {
			return BlockTextureDefault.get(Textures.BlockIcons.PIPE_RESTRICTOR_HUGE, mRGBaRubber, mIsGlowing);
		}
	}
	
	@Override public int getIconIndexSide                   (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return IconsGT.INDEX_BLOCK_PIPE_SIDE;}
	@Override public int getIconIndexConnected              (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return mTanks.length>=9?OP.pipeNonuple.mIconIndexBlock:mTanks.length>=4?OP.pipeQuadruple.mIconIndexBlock:aDiameter<0.37F?OP.pipeTiny.mIconIndexBlock:aDiameter<0.49F?OP.pipeSmall.mIconIndexBlock:aDiameter<0.74F?OP.pipeMedium.mIconIndexBlock:aDiameter<0.99F?OP.pipeLarge.mIconIndexBlock:OP.pipeHuge.mIconIndexBlock;}
	@Override
	public ITexture getTextureCFoamDry(byte aSide, byte aConnections, float aDiameter, int aRenderPass) {
		if (mOutMark) return BlockTextureMulti.get(BlockTextureDefault.get(mOwnable?Textures.BlockIcons.CFOAM_HARDENED_OWNED:Textures.BlockIcons.CFOAM_HARDENED, mRGBaFoam), BlockTextureDefault.get(Textures.BlockIcons.ARROWS[mFluidMode.ordinal()][aSide][mFluidDir], UT_CH.Code.getMarkRGB(mRGBaFoam)));
		return BlockTextureDefault.get(mOwnable?Textures.BlockIcons.CFOAM_HARDENED_OWNED:Textures.BlockIcons.CFOAM_HARDENED, mRGBaFoam);
	}

	// GTCH，图像动画数据
	@Override
	protected int getRenderPasses3(Block aBlock, boolean[] aShouldSideBeRendered) {
		if (worldObj == null && isFoamDried()) mRGBaMark = UT_CH.Code.getMarkRGB(mRGBa);
		return super.getRenderPasses3(aBlock, aShouldSideBeRendered);
	}
	@Override public boolean onTickCheck(long aTimer) {return mFluidDir != oFluidDir || mFluidMode != oFluidMode || mFlowControl != oFlowControl || mOutMark != oOutMark || super.onTickCheck(aTimer);}
	@Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {super.onTickResetChecks(aTimer, aIsServerSide); oFluidDir = mFluidDir; oFluidMode = mFluidMode; oFlowControl = mFlowControl; oOutMark = mOutMark; }
	@Override public void setVisualData(byte aData) {
		mFluidMode = Mode.values()[(byte)(aData & 3)];
		mFlowControl = ((aData & 4) != 0);
		mOutMark = ((aData & 8) != 0);
		mFluidDir = (byte)((aData >> 4) & 7);
	}
	@Override public byte getVisualData() {return (byte)((mFluidMode.ordinal() & 3) | (mFlowControl?4:0) | (mOutMark?8:0) | ((mFluidDir & 7) << 4));}

	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		boolean tOut = super.receiveDataByteArray(aData, aNetworkHandler);
		mRGBaMark = UT_CH.Code.getMarkRGB(mRGBa);
		return tOut;
	}

	@Override public Collection<TagData> getConnectorTypes  (byte aSide) {return TD.Connectors.PIPE_FLUID.AS_LIST;}
	
	@Override public String getFacingTool() {return TOOL_wrench;}
	@Override public boolean isUsingWrenchingOverlay(ItemStack aStack, byte aSide) {return super.isUsingWrenchingOverlay(aStack, aSide) || ToolsGT.contains(TOOL_monkeywrench, aStack);}

	public int getRubberCount() {
		if (mTanks.length>=9) {
			return 36;
		} else
		if (mTanks.length>=4) {
			return 24;
		} else
		if (mDiameter<0.37F) {
			return 2;
		} else
		if (mDiameter<0.49F) {
			return 4;
		} else
		if (mDiameter<0.74F) {
			return 6;
		} else
		if (mDiameter<0.99F) {
			return 9;
		} else {
			return 12;
		}
	}

	@Override
	public List<OreDictItemData> getOreDictItemData(List<OreDictItemData> aList) {
		// 只有能够切换状态的才会有橡胶圈
		if (mFlowControl && mAllowSwitchFC) aList.add(new OreDictItemData(MT.Rubber, OP.ring.mAmount * getRubberCount()));
		return aList;
	}

	@Override public String getTileEntityName() {return "gt.multitileentity.connector.pipe.fluid";}

	@Override
	public boolean canFillExtra(FluidStack aFluid) {
		return T;
	}
}
