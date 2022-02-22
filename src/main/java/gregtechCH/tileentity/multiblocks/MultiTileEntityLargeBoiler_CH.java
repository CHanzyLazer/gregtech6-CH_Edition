package gregtechCH.tileentity.multiblocks;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.data.TD;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtech.tileentity.multiblocks.MultiTileEntityLargeBoiler;
import gregtechCH.data.LH_CH;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

public class MultiTileEntityLargeBoiler_CH extends MultiTileEntityLargeBoiler {
	public long mEnergyEff = 0, mInput = 204800, pEnergy = 0;

	protected long mOutputNow = 0;
	protected short mEfficiencyCH = 10000;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_ENERGY_EFF)) mEnergyEff = aNBT.getLong(NBT_ENERGY_EFF);
		if (aNBT.hasKey(NBT_ENERGY_PRE)) pEnergy = aNBT.getLong(NBT_ENERGY_PRE);
		if (aNBT.hasKey(NBT_OUTPUT_SU)) mInput = aNBT.getLong(NBT_OUTPUT_SU) / STEAM_PER_EU;

		if (aNBT.hasKey(NBT_INPUT)) mInput = aNBT.getLong(NBT_INPUT);
		if (aNBT.hasKey(NBT_EFFICIENCY_CH)) mEfficiencyCH = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY_CH));
		mOutput = UT.Code.units(mInput, 10000, UT.Code.units(mEfficiency, 10000, mEfficiencyCH, F), F) * STEAM_PER_EU;
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setNumber(aNBT, NBT_ENERGY_EFF, mEnergyEff);
		UT.NBT.setNumber(aNBT, NBT_ENERGY_PRE, pEnergy);

		if (mOutputNow != 0) aNBT.setLong(NBT_OUTPUT_NOW, mOutputNow); // for OmniOcular usage
		UT.NBT.setNumber(aNBT, NBT_CAPACITY_HU, mCapacity); // for OmniOcular usage 和读取的名称不一致是为了避免被意外修改
		for (int i = 0; i < mTanks.length; i++) UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+i, mTanks[i].capacity()); // for OmniOcular usage
	}

	@Override
	protected void toolTipsRecipe(List<String> aList) {
		aList.add(Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X)        + " 1 L " + FL.name(FluidRegistry.WATER, T) + " " + LH.get(LH.CONVERTS_TO_Y) + " " + STEAM_PER_WATER + " L " + FL.name(FL.Steam.make(0), T) + " " + LH.get(LH.CONVERTS_USING_Z) + " " + UT.Code.units(EU_PER_WATER, mEfficiencyCH, 10000, F) + " " + mEnergyTypeAccepted.getLocalisedNameShort());
	}
	@Override
	protected void toolTipsEnergy(List<String> aList) {
		aList.add(LH.getToolTipEfficiency(mEfficiencyCH));
		aList.add(Chat.GREEN    + LH.get(LH.ENERGY_INPUT)           + ": " + Chat.WHITE + mInput 	+ " - " + (mInput*2)           		+ " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + Chat.WHITE + "/t (" + LH_CH.get(LH_CH.FACE_HEAT_TRANS) + ")");
		aList.add(Chat.RED      + LH.get(LH.ENERGY_OUTPUT)          + ": " + Chat.WHITE + mOutput	+ " - " + (mOutput*2) 				+ " " + TD.Energy.STEAM.getLocalisedChatNameLong()      + Chat.WHITE + "/t (" + LH_CH.get(LH_CH.FACE_PIPE_HOLE) + ")");
	}
	@Override
	protected void toolTipsUseful(List<String> aList) {
		aList.add(Chat.GREEN    + LH_CH.get(LH_CH.TOOLTIP_PREHEAT));
	}
	
	@Override
	public boolean isInsideStructure(int aX, int aY, int aZ) {
		int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
		return aX >= tX - 1 && aY >= tY - 1 && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 2 && aZ <= tZ + 1;
	}
	
	@Override
	protected void onTick3(long aTimer, boolean aIsServerSide) {
		if (aIsServerSide) {
			// 有接受到热量就不会冷却
			if (mEnergy > pEnergy) mCoolDownResetTimer = 128;
			pEnergy = mEnergy;
			// Convert HU to effective energy
			if (mEnergy < mInput && mTanks[1].isHalf()) mEnergy = 0; // 输入能量不足不会工作
			// 改用倍率判断，防止卡最小输入不稳
			if (mEnergyEff < EU_PER_WATER * 2) {
				int tMul = (int) (mEnergy / mInput);
				if (tMul > 0) {
					mEnergy -= mInput * tMul;
					mEnergyEff += UT.Code.units(mOutput * tMul, 10000, mEfficiency, F) / STEAM_PER_EU;
				}
			}
			// Convert Water to Steam
			long tConversionsEff = mEnergyEff / EU_PER_WATER;
			if (tConversionsEff > 0) {
				mTanks[0].remove(tConversionsEff);
				if (rng(10) == 0 && mEfficiency > 5000 && mTanks[0].has() && !FL.distw(mTanks[0])) {
					mEfficiency -= tConversionsEff;
					if (mEfficiency < 5000) mEfficiency = 5000;
				}
				mTanks[1].setFluid(FL.Steam.make(mTanks[1].amount() + tConversionsEff * STEAM_PER_WATER));
				mEnergyEff -= tConversionsEff * EU_PER_WATER;
			}

			// Remove Steam and Heat during the process of cooling down.
			if (mCoolDownResetTimer-- <= 0) {
				mCoolDownResetTimer = 0;
				mEnergy -= mInput * 64;
				pEnergy = mEnergy;
				mEnergyEff = 0;
				GarbageGT.trash(mTanks[1], mOutput * 64);
				if (mEnergy <= 0) {
					mEnergy = 0;
					mCoolDownResetTimer = 128;
				}
			}

			long tAmount = mTanks[1].amount() - mTanks[1].capacity() / 2;
			
			// Emit Steam
			if (tAmount >= mOutput) {
				mOutputNow = Math.min(tAmount > mTanks[1].capacity() / 4 ?
								mOutput * 2 :
								mOutput + UT.Code.units(mOutput, mTanks[1].capacity() / 4, tAmount - mOutput, F),
						tAmount);
				FluidStack tDrainableSteam = mTanks[1].drain((int)mOutputNow, F);
				
				if (tDrainableSteam != null) {
					int tTargets = 0;
					
					@SuppressWarnings("unchecked")
					DelegatorTileEntity<TileEntity>[] tDelegators = new DelegatorTileEntity[] {
					  WD.te(worldObj, getOffsetXN(mFacing, 1)  , yCoord+3, getOffsetZN(mFacing, 1)  , SIDE_Y_NEG, F)
					, WD.te(worldObj, getOffsetXN(mFacing, 1)-2, yCoord+1, getOffsetZN(mFacing, 1)  , SIDE_X_POS, F)
					, WD.te(worldObj, getOffsetXN(mFacing, 1)+2, yCoord+1, getOffsetZN(mFacing, 1)  , SIDE_X_NEG, F)
					, WD.te(worldObj, getOffsetXN(mFacing, 1)  , yCoord+1, getOffsetZN(mFacing, 1)-2, SIDE_Z_POS, F)
					, WD.te(worldObj, getOffsetXN(mFacing, 1)  , yCoord+1, getOffsetZN(mFacing, 1)+2, SIDE_Z_NEG, F)
					};
					
					long[] tTargetAmounts = new long[tDelegators.length];
					
					for (int i = 0; i < tDelegators.length; i++) if (tDelegators[i].mTileEntity instanceof IFluidHandler && (tTargetAmounts[i] = FL.fill_(tDelegators[i], tDrainableSteam, F)) > 0) tTargets++; else tDelegators[i] = null;
					
					if (tTargets == 1) {
						for (DelegatorTileEntity<TileEntity> tDelegator : tDelegators)
							if (tDelegator != null) {
								FL.move_(mTanks[1], tDelegator, tDrainableSteam.amount);
								break;
							}
					} else if (tTargets > 1 && tDrainableSteam.amount >= tTargets) {
						if (UT.Code.sum(tTargetAmounts) > tDrainableSteam.amount) {
							int tMoveable = tDrainableSteam.amount, tOriginalTargets = tTargets;
							for (int i = 0; i < tDelegators.length; i++) if (tDelegators[i] != null) {
								if (tTargetAmounts[i] <= tDrainableSteam.amount / tOriginalTargets) {
									tMoveable -= FL.move_(mTanks[1], tDelegators[i], tDrainableSteam.amount / tOriginalTargets);
									tDelegators[i] = null;
									if (--tTargets < 2) break;
								}
							}
							if (tTargets == 1) {
								for (DelegatorTileEntity<TileEntity> tDelegator : tDelegators)
									if (tDelegator != null) {
										FL.move_(mTanks[1], tDelegator, tMoveable);
										break;
									}
							} else if (tMoveable >= tTargets) {
								for (DelegatorTileEntity<TileEntity> tDelegator : tDelegators)
									if (tDelegator != null) {
										tMoveable -= FL.move_(mTanks[1], tDelegator, tMoveable / tTargets);
										if (--tTargets < 1) break;
									}
							}
						} else {
							for (int i = 0; i < tDelegators.length; i++) if (tDelegators[i] != null) FL.move_(mTanks[1], tDelegators[i], tTargetAmounts[i]);
						}
					}
				}
			} else {
				mTanks[1].remove(tAmount); // 只是为了让数值好看
				mOutputNow = 0;
			}
			
			// Set Barometer
			mBarometer = (byte)UT.Code.scale(mTanks[1].amount(), mTanks[1].capacity(), 31, F);
			
			// Well the Boiler gets structural Damage when being too hot, or when being too full of Steam.
			if ((mBarometer > 4 && !checkStructure(F)) || mEnergy > mCapacity || mTanks[1].isFull()) {
				explode(F);
			}
		}
	}

	@Override
	public boolean breakBlock() {
		if (isServerSide()) {
			GarbageGT.trash(mTanks[0]);
		}
		return super.breakBlock();
	}

	@Override public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {return mInput;}
	@Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mInput;}
	
	@Override public String getTileEntityName() {return "gtch.multitileentity.multiblock.boiler.steam";}
}
