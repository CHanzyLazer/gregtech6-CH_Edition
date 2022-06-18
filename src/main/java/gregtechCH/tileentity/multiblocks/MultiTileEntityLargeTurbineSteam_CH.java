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

package gregtechCH.tileentity.multiblocks;

import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.multiblocks.IMultiBlockFluidHandler;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.fluid.IFluidHandler_CH;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityLargeTurbineSteam_CH extends MultiTileEntityLargeMotor_CH implements IMultiBlockFluidHandler, IFluidHandler_CH {
	public FluidTankGT[] mTanks = new FluidTankGT[] {new FluidTankGT(0), new FluidTankGT(0)};
	public long pSteam = 0, mOutputSU = 0, mSteamCounter = 0;
	protected int STEAM_PER_WATER_SELF = 170;
	protected short mEfficiencyWater = 9500;
	protected short mEfficiencyOverclock = 5000;

	protected long mOutputConvert = 0;

	protected static final byte COOLDOWN_NUM = 16;
	protected byte mCooldownCounter = 0;  // 注意默认是停止工作的

	public TagData mEnergyTypeAccepted = TD.Energy.STEAM;

	protected boolean mOverload = F;

	// NBT读写
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_COOLDOWN_COUNTER)) mCooldownCounter = aNBT.getByte(NBT_COOLDOWN_COUNTER);
		if (aNBT.hasKey(NBT_ENERGY_SU_PRE)) pSteam = aNBT.getLong(NBT_ENERGY_SU_PRE);

		if (aNBT.hasKey(NBT_ENERGY_SU)) mSteamCounter = aNBT.getLong(NBT_ENERGY_SU);
		if (aNBT.hasKey(NBT_OUTPUT_SU)) mOutputSU = aNBT.getLong(NBT_OUTPUT_SU);
		if (aNBT.hasKey(NBT_EFFICIENCY_WATER)) mEfficiencyWater = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_WATER));
		STEAM_PER_WATER_SELF = mEfficiencyWater < 100 ? -1 : (int)UT.Code.units(STEAM_PER_WATER, mEfficiencyWater, 10000, T);
		if (aNBT.hasKey(NBT_EFFICIENCY_OC)) mEfficiencyOverclock = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_OC));


		if (aNBT.hasKey(NBT_ENERGY_ACCEPTED)) mEnergyTypeAccepted = TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED));

		for (int i = 0; i < mTanks.length; i++) mTanks[i].readFromNBT(aNBT, NBT_TANK+"."+i);
	}
	@Override
	protected void setEnergyByLength2(int aI) {
		super.setEnergyByLength2(aI);
		mTanks[0].setCapacity(mInRate*16);
		mTanks[1].setCapacity(mInRate*16); // 不需要 voidExceed
	}
	@Override
	protected void setInRateByLength(int aI) {
		mInRate  = UT.Code.units(mRate, mEfficiency, 10000, T) * STEAM_PER_EU;
		mInPCost = UT.Code.units(mPCost, mEfficiency, 10000, T) * STEAM_PER_EU;
	}
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setNumber(aNBT, NBT_COOLDOWN_COUNTER, mCooldownCounter);
		UT.NBT.setNumber(aNBT, NBT_ENERGY_SU_PRE, pSteam);

		UT.NBT.setNumber(aNBT, NBT_ENERGY_SU, mSteamCounter);
		UT.NBT.setNumber(aNBT, NBT_OUTPUT_SU, mOutputSU); // 保留兼容
		for (int i = 0; i < mTanks.length; i++) mTanks[i].writeToNBT(aNBT, NBT_TANK+"."+i);
		for (int i = 0; i < mTanks.length; i++) UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+i, mTanks[i].capacity());
	}

	// 多方快结构

	// tooltips
	@Override
	protected void toolTipsEnergy(List<String> aList) {
		aList.add(LH.Chat.CYAN     + LH_CH.getNumber(LH_CH.ENERGY_LENGTH, mMidLength));
		aList.add(LH.getToolTipEfficiency(mEfficiencyArray[mMidLength-mMinLength]));
		long tInput = UT.Code.units(mRateArray[mMidLength-mMinLength], mEfficiencyArray[mMidLength-mMinLength], 10000, T) * STEAM_PER_EU, tOutput = mRateArray[mMidLength-mMinLength];
		aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_INPUT ) + ": " + LH.Chat.WHITE 	+ tInput  + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, tInput/2,  tInput*2)  + ")");
		aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE 	+ tOutput + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, tOutput/2, tOutput + UT.Code.units(tOutput, 10000, mEfficiencyOverclock, F)) + ")");
	}
	@Override
	protected void toolTipsUseful(List<String> aList) {
		aList.add(Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_GENERATOR) + " (" + LH_CH.getToolTipEfficiencySimple(mEfficiencyOverclock) + ")");
		super.toolTipsUseful(aList);
	}
	@Override
	protected void toolTipsImportant(List<String> aList) {
		aList.add(Chat.ORANGE + LH.get(LH.EMITS_USED_STEAM) + " ("+LH_CH.get(LH_CH.FACE_PIPE_HOLE)+", " + LH_CH.getToolTipEfficiencySimple(mEfficiencyWater) + ")");
		super.toolTipsImportant(aList);
	}
	@Override
	protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
		aList.add(Chat.DGRAY    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.4"));
		super.toolTipsOther(aList, aStack, aF3_H);
	}
	@Override
	protected void toolTipsMultiblock(List<String> aList) {
		super.toolTipsMultiblock(aList);
		aList.add(Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.1"));
		aList.add(Chat.WHITE    + LH_CH.getNumber("gtch.tooltip.multiblock.steamturbine.5", mMinLength, mMaxLength));
		aList.add(Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.2"));
		aList.add(Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.3"));
	}
	static {
		LH_CH.add("gtch.tooltip.multiblock.steamturbine.1", "3x3xN of the Walls you crafted this with");
		LH_CH.add("gtch.tooltip.multiblock.steamturbine.2", "Main centered on the 3x3 facing outwards");
		LH_CH.add("gtch.tooltip.multiblock.steamturbine.3", "Input only possible at frontal 3x3");
		LH_CH.add("gtch.tooltip.multiblock.steamturbine.4", "Distilled Water can still be pumped out at Bottom Layer");
		LH_CH.add("gtch.tooltip.multiblock.steamturbine.5", "N can be from %d to %d");
	}

	// 工具右键
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		
		if (isClientSide()) return 0;
		
		if (aTool.equals(TOOL_plunger)) {
			if (mTanks[0].has()) return GarbageGT.trash(mTanks[0]);
			return GarbageGT.trash(mTanks[1]);
		}
		return 0;
	}
	@Override
	public void onMagnifyingGlassEnergy(List<String> aChatReturn) {
		if (mPreheat) {
			aChatReturn.add("Preheating: " + LH.percent(UT.Code.units(Math.min(mEnergy, mPEnergy), mPEnergy, 10000, F)) + "%");
		}
		if (mActive) {
			aChatReturn.add("Active:");
			aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(UT.Code.units(mEfficiency, mOutputConvert, mOutput, F)) + "%");
			aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + mOutput + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t");
		}
		if (mEnergy == 0) {
			aChatReturn.add("Length: " + mLength);
			aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(mEfficiency) + "%");
			aChatReturn.add(LH.get(LH.ENERGY_INPUT)   + ": " + getEnergySizeInputMin(mEnergyTypeAccepted, SIDE_ANY) + " - " + getEnergySizeInputMax(mEnergyTypeAccepted, SIDE_ANY) + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
			aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + getEnergySizeOutputMin(mEnergyTypeEmitted, SIDE_ANY) + " - " + getEnergySizeOutputMax(mEnergyTypeEmitted, SIDE_ANY) + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t");
		}
	}

	// 每 tick 转换
	@Override
	protected void convert() {
		long tSteam = mTanks[0].amount();
		mOutputSU = tSteam - pSteam;
		if (mOutputSU > 0) mCooldownCounter = COOLDOWN_NUM;
		if (mTanks[0].has()) {
			if (!mTanks[0].isFull()) {
				//没有超载
				if (mEnergy < mPEnergy) {
					// 预热时积攒蒸汽，减少运算，减少因效率计算造成的损失
					if (mEnergy < mPEnergy - mRate * 2) {
						convert(mInRate * 2, mRate * 2);
					} else {
						convert_(tSteam);
					}
				} else {
					// 运行状态也进行下不积攒蒸汽，回到原本的两种情况
					if (tSteam >= mInRate * 2) {
						// 最高输出
						convert_(mInRate * 2, mRate * 2);
					} else
					if (tSteam >= mInRate / 2) {
						// 一般输出
						convert_(tSteam);
					} else {
						// 蒸汽不够完全不工作
						if (STEAM_PER_WATER_SELF > 0) mSteamCounter += tSteam;
						mTanks[0].remove(tSteam);
						if (tSteam >= mInPCost) mEnergy = mPEnergy + mPCost; // 超过一半的不够则不考虑效率，使用这个方式实现不工作
					}
				}
			} else {
				//超载
				mTanks[0].remove(tSteam/2);
				mOverload = T;
			}

			//输出蒸馏水，和输出能量不相互干扰
			if (mSteamCounter >= STEAM_PER_WATER_SELF && STEAM_PER_WATER_SELF > 0) {
				mTanks[1].fillAll(FL.DistW.make(mSteamCounter / STEAM_PER_WATER_SELF));
				mSteamCounter %= STEAM_PER_WATER_SELF;
			}
		}
		pSteam = mTanks[0].amount();
		// 自动输出蒸馏水
		long tFluid;
		if (mTanks[1].has()) {
			FL.move(mTanks[1], getFluidEmitter().getAdjacentTank(getEmittingSide()));
			tFluid = mTanks[1].amount() - mTanks[1].capacity() / 2;
			if (tFluid > 0) GarbageGT.trash(mTanks[1], tFluid);
		}
	}
	@Override
	protected boolean checkOverload() {
		return mOverload;
	}
	@Override
	protected void doOverload() {
		super.doOverload();
		overcharge(mTanks[0].capacity(), mEnergyTypeEmitted);
		mOverload = F;
	}
	@Override
	protected long getOutput() {
		//根据 mOutputConvert，如果超过了标准输出则按照超频效率输出
		//不再进行平滑，去除下限检测因为现在的算法都不会达到下限
		mOutputConvert = mEnergy - mPEnergy;
		return mOutputConvert <= mRate ? mOutputConvert :  Math.min(mRate + UT.Code.units(mOutputConvert - mRate, 10000, mEfficiencyOverclock, F), getEnergySizeOutputMax(mEnergyTypeEmitted, SIDE_ANY));
	}
	@Override
	protected void energyReduce() {
		mEnergy -= mOutputConvert;
	}
	@Override
	protected boolean checkPreheat() {
		return super.checkPreheat() && mCooldownCounter > 0;
	}
	@Override
	protected void doPreheat() {
		super.doPreheat();
		if (mOutputSU == 0) {
			//可能在冷却，计数
			--mCooldownCounter;
		}
	}
	@Override
	protected boolean checkCooldown() {
		return super.checkCooldown() && mCooldownCounter <= 0;
	}
	@Override
	protected void doCooldown() {
		super.doCooldown();
		mCooldownCounter = 0;
		pSteam = 0;
		mOutputSU = 0;
	}
	@Override
	protected void doElse() {
		// 必须要冷却才能清空数据
		super.stop();
		if (mCooldownCounter <= 0) {
			mTanks[0].setEmpty();
			pSteam = 0;
			mOutputSU = 0;
			mSteamCounter = 0;
			mCooldownCounter = 0;
		}
	}
	@Override
	protected void stop() {
		super.stop();
		mTanks[0].setEmpty();
		pSteam = 0;
		mOutputSU = 0;
		mSteamCounter = 0;
		mCooldownCounter = 0;
	}

	protected void convert_(long aInRate) {
		convert_(aInRate, UT.Code.units(aInRate, 10000, mEfficiency, F));
	}
	protected void convert_(long aInRate, long aOutRate) {
		if (STEAM_PER_WATER_SELF > 0) mSteamCounter += aInRate;
		mTanks[0].remove(aInRate);
		mEnergy += aOutRate;
	}
	protected void convert(long aInRate, long aOutRate) {
		if (mTanks[0].has(aInRate)) convert_(aInRate, aOutRate);
	}

	// 一些接口
	@Override
	public boolean breakBlock() {
		if (isServerSide()) {
			GarbageGT.trash(mTanks[1]);
		}
		return super.breakBlock();
	}
	@Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return !mStopped && FL.steam(aFluidToFill) ? mTanks[0] : null;}
	@Override protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {return mTanks[1];}
	@Override protected IFluidTank[] getFluidTanks2(byte aSide) {return mTanks;}

	@Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return (aEmitting?mEnergyTypeEmitted:mEnergyTypeAccepted)==aEnergyType;}
	@Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mRate+UT.Code.units(mRate, 10000, mEfficiencyOverclock, F);}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return new ArrayListNoNulls<>(F, mEnergyTypeAccepted, mEnergyTypeEmitted);}

	// Icons，图像动画
	public static final IIconContainer mTextureInactive	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine");
	public static final IIconContainer mTextureActive   = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active");
	public static final IIconContainer mTexturePreheat  = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat");
	public static final IIconContainer mTextureActiveL	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active_l");
	public static final IIconContainer mTexturePreheatL	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat_l");
	public static final IIconContainer mTextureOutput  	= new Textures.BlockIcons.CustomIcon("machines/multiblockparts/metalwalldense/3/overlay/top");
	@Override
	public IIconContainer getIIconContainer(IconType aIconType) {
		switch (aIconType) {
			case OVERLAY_ACTIVE_L: return mTextureActiveL;
			case OVERLAY_ACTIVE_R: return mTextureActive;
			case OVERLAY_PREHEAT_L: return mTexturePreheatL;
			case OVERLAY_PREHEAT_R: return mTexturePreheat;
			case OVERLAY_ENERGY_RU: return mTextureOutput;
			case OVERLAY:
			default: return mTextureInactive;
		}
	}

	@Override public String getTileEntityName() {return "gtch.multitileentity.multiblock.turbine.steam";}

	@Override
	public boolean canFillExtra(FluidStack aFluid) {
		return T;
	}
}
