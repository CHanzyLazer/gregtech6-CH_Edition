package gregtechCH.tileentity.energy.converters;

import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.fluid.IFluidHandler_CH;
import gregtechCH.tileentity.energy.MultiTileEntityMotor_CH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

public class MultiTileEntityTurbineSteam_CH extends MultiTileEntityMotor_CH implements IFluidHandler_CH {
	public FluidTankGT mTank = new FluidTankGT();
	public long pSteam = 0, mOutputSU = 0, mSteamCounter = 0;
	protected int STEAM_PER_WATER_SELF = 200;
	protected short mEfficiencyWater = 8000;
	protected short mEfficiencyOverclock = 5000;

	protected long mOutputConvert = 0;

	protected static final byte COOLDOWN_NUM = 16;
	protected byte mCooldownCounter = 0; // 注意默认是停止工作的

	public TagData mEnergyTypeAccepted = TD.Energy.STEAM;

	protected boolean mFast = F, oFast = F;
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
		if (aNBT.hasKey(NBT_VISUAL)) mFast = aNBT.getBoolean(NBT_VISUAL);

		mTank.readFromNBT(aNBT, NBT_TANK+"."+0);
		mTank.setCapacity(mInRate*16);
	}
	@Override
	protected void setInRate(NBTTagCompound aNBT) {
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
		mTank.writeToNBT(aNBT, NBT_TANK+"."+0);
		UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+0, mTank.capacity());

		UT.NBT.setBoolean(aNBT, NBT_VISUAL, mFast);
	}

	// tooltips
	@Override
	protected void toolTipsEnergy(List<String> aList) {
		aList.add(LH.getToolTipEfficiency(mEfficiency));
		LH.addEnergyToolTips(this, aList, mEnergyTypeAccepted, mEnergyTypeEmitted, getLocalisedInputSide(), getLocalisedOutputSide());
	}
	@Override
	protected void toolTipsUseful(List<String> aList) {
		aList.add(Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_GENERATOR) + " (" + LH_CH.getToolTipEfficiencySimple(mEfficiencyOverclock) + ")");
		super.toolTipsUseful(aList);
	}
	@Override
	protected void toolTipsImportant(List<String> aList) {
		aList.add(Chat.ORANGE + LH.get(LH.EMITS_USED_STEAM) + " ("+LH.get(LH.FACE_SIDES)+", " + LH_CH.getToolTipEfficiencySimple(mEfficiencyWater) + ")");
		super.toolTipsImportant(aList);
	}

	// 工具右键
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		
		if (isClientSide()) return 0;
		
		if (aTool.equals(TOOL_plunger)) return GarbageGT.trash(mTank);
		return 0;
	}
	@Override
	protected void onMagnifyingGlassEnergy(List<String> aChatReturn) {
		if (mPreheat) {
			aChatReturn.add("Preheating: " + LH.percent(UT.Code.units(Math.min(mEnergy, mPEnergy), mPEnergy, 10000, F)) + "%");
		}
		if (mActive) {
			aChatReturn.add("Active:");
			aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(UT.Code.units(mEfficiency, mOutputConvert, mOutput, F)) + "%");
			aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + mOutput + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t");
		}
	}

	// 每 tick 转换
	@Override
	protected void convert() {
		long tSteam = mTank.amount();
		mOutputSU = tSteam - pSteam;
		if (mOutputSU > 0) mCooldownCounter = COOLDOWN_NUM;
		if (mTank.has()) {
			if (!mTank.isFull()) {
				//没有超载
				if (mEnergy < mPEnergy) {
					// 预热时积攒蒸汽，减少运算，减少因效率计算造成的损失
					if (mEnergy < mPEnergy - mInRate * 2) {
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
						mTank.remove(tSteam);
						if (tSteam >= mInPCost) mEnergy = mPEnergy + mPCost; // 超过一半的不够则不考虑效率，使用这个方式实现不工作
					}
				}
			} else {
				//超载
				mTank.remove(tSteam/2);
				mOverload = T;
			}

			//输出蒸馏水，和输出能量不相互干扰
			if (mSteamCounter >= STEAM_PER_WATER_SELF && STEAM_PER_WATER_SELF > 0) {
				FluidStack tDistilledWater = FL.DistW.make(mSteamCounter / STEAM_PER_WATER_SELF);
				for (byte tDir : FACING_SIDES[mFacing]) {
					tDistilledWater.amount -= FL.fill(getAdjacentTank(tDir), tDistilledWater.copy(), T);
					if (tDistilledWater.amount <= 0) break;
				}
				GarbageGT.trash(tDistilledWater);
				mSteamCounter %= STEAM_PER_WATER_SELF;
			}
		}
		pSteam = mTank.amount();
	}
	@Override
	protected boolean checkOverload() {
		return mOverload;
	}
	@Override
	protected void doOverload() {
		super.doOverload();
		overcharge(mTank.capacity(), mEnergyTypeEmitted);
		mOverload = F;
	}
	@Override
	protected void doActive() {
		super.doActive();
		mFast = mOutput > getEnergySizeOutputRecommended(mEnergyTypeEmitted, SIDE_ANY);
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
			mTank.setEmpty();
			pSteam = 0;
			mOutputSU = 0;
			mSteamCounter = 0;
			mCooldownCounter = 0;
		}
	}
	@Override
	protected void stop() {
		super.stop();
		mTank.setEmpty();
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
		mTank.remove(aInRate);
		mEnergy += aOutRate;
	}
	protected void convert(long aInRate, long aOutRate) {
		if (mTank.has(aInRate)) convert_(aInRate, aOutRate);
	}

	// 一些接口
	@Override public float getSurfaceSizeAttachable (byte aSide) {return ALONG_AXIS[aSide][mFacing]?0.5F:0.25F;}
	@Override public boolean isSideSolid2           (byte aSide) {return T;}
	@Override public boolean isSurfaceOpaque2       (byte aSide) {return T;}
	
	@Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return isInput(aSide) && !mStopped && FL.steam(aFluidToFill) ? mTank : null;}
	@Override protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {return null;}
	@Override protected IFluidTank[] getFluidTanks2(byte aSide) {return isOutput(aSide) ? null : mTank.AS_ARRAY;}

	public String getLocalisedInputSide () {return LH.get(LH.FACE_BACK);}
	public String getLocalisedOutputSide() {return LH.get(LH.FACE_FRONT);}

	@Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return (aEmitting?mEnergyTypeEmitted:mEnergyTypeAccepted)==aEnergyType;}
	@Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mRate+UT.Code.units(mRate, 10000, mEfficiencyOverclock, F);}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return new ArrayListNoNulls<>(F, mEnergyTypeAccepted, mEnergyTypeEmitted);}

	// Icons，图像动画
	public final static IIconContainer[] sColoreds = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/front"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/back"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/side"),
	}, sOverlays = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/back"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/side"),
	}, sOverlaysActiveLS = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_ls/front"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_ls/back"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_ls/side"),
	}, sOverlaysActiveLF = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_lf/front"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_lf/back"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_lf/side"),
	}, sOverlaysActiveRS = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rs/front"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rs/back"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rs/side"),
	}, sOverlaysActiveRF = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rf/front"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rf/back"),
		new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rf/side"),
	}, sOverlaysPreheatL = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_l/front"),
			new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_l/back"),
			new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_l/side"),
	}, sOverlaysPreheatR = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_r/front"),
			new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_r/back"),
			new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_r/side"),
	};

	@Override
	public IIconContainer[] getIIconContainers(IconType aIconType) {
		switch (aIconType) {
			case COLORED: return sColoreds;
			case OVERLAY: return sOverlays;
			case OVERLAY_ACTIVE_L: return mFast?sOverlaysActiveLF:sOverlaysActiveLS;
			case OVERLAY_ACTIVE_LS: return sOverlaysActiveLS;
			case OVERLAY_ACTIVE_LF: return sOverlaysActiveLF;
			case OVERLAY_ACTIVE_R: return mFast?sOverlaysActiveRF:sOverlaysActiveRS;
			case OVERLAY_ACTIVE_RS: return sOverlaysActiveRS;
			case OVERLAY_ACTIVE_RF: return sOverlaysActiveRF;
			case OVERLAY_PREHEAT_L: return sOverlaysPreheatL;
			case OVERLAY_PREHEAT_R: return sOverlaysPreheatR;
			default: return sOverlays;
		}
	}

	@Override
	public boolean onTickCheck(long aTimer) {
		return mFast != oFast || super.onTickCheck(aTimer);
	}
	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oFast = mFast;
	}
	@Override public void setVisualData(byte aData) {
		super.setVisualData(aData);
		mFast   			= ((aData & 16) != 0);
	}
	@Override public byte getVisualData() {return (byte)(super.getVisualData() | (mFast?16:0));}

	@Override public void onWalkOver2(EntityLivingBase aEntity) {if (SIDES_TOP[mFacing] && mActive) {aEntity.rotationYaw=aEntity.rotationYaw+(mCounterClockwise?-5:+5)*(mFast?2:1); aEntity.rotationYawHead=aEntity.rotationYawHead+(mCounterClockwise?-5:+5)*(mFast?2:1);}}

	@Override public String getTileEntityName() {return "gtch.multitileentity.turbines.rotation_steam";}

	@Override
	public boolean canFillExtra(FluidStack aFluid) {
		return T;
	}
}