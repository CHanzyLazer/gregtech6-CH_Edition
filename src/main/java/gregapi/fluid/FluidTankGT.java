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

package gregapi.fluid;

import gregapi.data.FL;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.util.UT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Map;

import static gregapi.data.CS.F;
import static gregapi.data.CS.T;

public class FluidTankGT implements IFluidTank {
	public final FluidTankGT[] AS_ARRAY = new FluidTankGT[] {this};
	
	private FluidStack mFluid;
	private long mCapacity = 0, mAmount = 0;
	private boolean mPreventDraining = F, mVoidExcess = F, mChangedFluids = F;
	/** HashMap of adjustable Tank Sizes based on Fluids if needed. */
	private Map<String, Long> mAdjustableCapacity = null;
	private long mAdjustableMultiplier = 1;
	/** Gives you a Tank Index in case there is multiple Tanks on a TileEntity that cares. */
	public int mIndex = 0;
	
	// GTCH, 固定储罐的溶液种类，在一些情况避免频繁创建流体以及意外的填充
	private boolean mFixedFluid = F, mSaveEmpty = F; // 注意和 mPreventDraining 等一样，统一不进行 NBT 存储，因此永远都需要在读取 NBT 时手动进行设置
	public FluidTankGT fixFluid(Fluid aFluid) {return fixFluid(FL.make(aFluid, 0));}
	public FluidTankGT fixFluid(FluidStack aFluid) {mFluid = aFluid; mSaveEmpty = mFixedFluid = mPreventDraining = T; return this;} // 统一逻辑，和 mPreventDraining 一样，默认开启时即使空也会存储
	public FluidTankGT unfixFluid() {mSaveEmpty = mFixedFluid = mPreventDraining = F; if (mAmount == 0) mFluid = null; return this;}
	public FluidTankGT setSaveEmpty() {return setSaveEmpty(T);}
	public FluidTankGT setSaveEmpty(boolean aSaveEmpty) {mSaveEmpty = aSaveEmpty; return this;}
	
	public FluidTankGT() {mCapacity = Long.MAX_VALUE;}
	public FluidTankGT(long aCapacity) {mCapacity = aCapacity;}
	public FluidTankGT(FluidStack aFluid) {mFluid = aFluid; if (aFluid != null) {mCapacity = aFluid.amount; mAmount = aFluid.amount;}}
	public FluidTankGT(FluidStack aFluid, long aCapacity) {mFluid = aFluid; mCapacity = aCapacity; mAmount = (aFluid == null ? 0 : aFluid.amount);}
	public FluidTankGT(FluidStack aFluid, long aAmount, long aCapacity) {mFluid = aFluid; mCapacity = aCapacity; mAmount = (aFluid == null ? 0 : aAmount);}
	public FluidTankGT(Fluid aFluid, long aAmount) {this(FL.make(aFluid, aAmount)); mAmount = aAmount;}
	public FluidTankGT(Fluid aFluid, long aAmount, long aCapacity) {this(FL.make(aFluid, aAmount), aCapacity); mAmount = aAmount;}
	public FluidTankGT(NBTTagCompound aNBT, long aCapacity) {mCapacity = aCapacity; if (aNBT != null && !aNBT.hasNoTags()) {mFluid = FL.load_(aNBT); mAmount = (mFluid == null ? 0 : aNBT.hasKey("LAmount") ? aNBT.getLong("LAmount") : mFluid.amount);}}
	public FluidTankGT(NBTTagCompound aNBT, String aKey, long aCapacity) {this(aNBT.hasKey(aKey) ? aNBT.getCompoundTag(aKey) : null, aCapacity);}
	
	public FluidTankGT readFromNBT(NBTTagCompound aNBT, String aKey) {
		if (aNBT.hasKey(aKey)) {
			aNBT = aNBT.getCompoundTag(aKey);
			if (aNBT != null && !aNBT.hasNoTags()) {
				mFluid = FL.load_(aNBT);
				mAmount = (mFluid == null ? 0 : aNBT.hasKey("LAmount") ? aNBT.getLong("LAmount") : mFluid.amount);
			}
		}
		return this;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound aNBT, String aKey) {
		if (mFluid != null && (mSaveEmpty || mAmount > 0)) {
			NBTTagCompound tNBT = UT.NBT.make();
			mFluid.amount = UT.Code.bindInt(mAmount);
			aNBT.setTag(aKey, mFluid.writeToNBT(tNBT));
			if (mAmount > Integer.MAX_VALUE) tNBT.setLong("LAmount", mAmount);
		} else {
			aNBT.removeTag(aKey);
		}
		return aNBT;
	}
	
	public NBTTagCompound writeToNBT(String aKey) {
		NBTTagCompound aNBT = UT.NBT.make();
		if (mFluid != null && (mSaveEmpty || mAmount > 0)) {
			NBTTagCompound tNBT = UT.NBT.make();
			mFluid.amount = UT.Code.bindInt(mAmount);
			aNBT.setTag(aKey, mFluid.writeToNBT(tNBT));
			if (mAmount > Integer.MAX_VALUE) tNBT.setLong("LAmount", mAmount);
		} else {
			aNBT.removeTag(aKey);
		}
		return aNBT;
	}
	
	public static NBTTagCompound writeToNBT(String aKey, FluidStack aFluid) {
		NBTTagCompound rNBT = UT.NBT.make();
		if (aFluid != null && aFluid.amount > 0) {
			rNBT.setTag(aKey, aFluid.writeToNBT(UT.NBT.make()));
		}
		return rNBT;
	}
	
	public static NBTTagCompound writeToNBT(NBTTagCompound aNBT, String aKey, FluidStack aFluid) {
		if (aFluid != null && aFluid.amount > 0) {
			aNBT.setTag(aKey, aFluid.writeToNBT(UT.NBT.make()));
		} else {
			aNBT.removeTag(aKey);
		}
		return aNBT;
	}
	
	public FluidStack drain(int aDrained) {return drain(aDrained, T);}
	@Override
	public FluidStack drain(int aDrained, boolean aDoDrain) {
		if (mFluid == null || aDrained <= 0) return null;
		if (mAmount < aDrained) aDrained = (int)mAmount;
		FluidStack rFluid = new FluidStack(mFluid, aDrained);
		if (aDoDrain) {
			mAmount -= aDrained;
			if (mAmount <= 0) {
				if (mPreventDraining) {
					mAmount = 0;
				} else {
					setEmpty();
				}
			}
		}
		return rFluid;
	}
	
	public boolean drainAll(long aDrained) {
		if (mFluid == null || mAmount < aDrained) return F;
		mAmount -= aDrained;
		if (mAmount <= 0) {
			if (mPreventDraining) {
				mAmount = 0;
			} else {
				setEmpty();
			}
		}
		return T;
	}
	
	public long remove(long aDrained) {
		if (mFluid == null || mAmount <= 0 || aDrained <= 0) return 0;
		if (mAmount < aDrained) aDrained = mAmount;
		mAmount -= aDrained;
		if (mAmount <= 0) {
			if (mPreventDraining) {
				mAmount = 0;
			} else {
				setEmpty();
			}
		}
		return aDrained;
	}
	
	public long add(long aFilled) {
		if (mFluid == null || aFilled <= 0) return 0;
		long tCapacity = capacity();
		if (mAmount + aFilled > tCapacity) {
			if (!mVoidExcess) aFilled = tCapacity - mAmount;
			mAmount = tCapacity;
			return aFilled;
		}
		mAmount += aFilled;
		return aFilled;
	}
	
	public long add(long aFilled, FluidStack aFluid) {
		if (aFluid == null || aFilled <= 0) return 0;
		if (mFluid == null) {
			if (mFixedFluid) return 0; // 如果 fix 到了 null 则不能添加
			mFluid = aFluid.copy();
			mChangedFluids = T;
			mAmount = Math.min(capacity(aFluid), aFilled);
			return mVoidExcess ? aFilled : mAmount;
		}
		return contains(aFluid) ? add(aFilled) : 0;
	}
	
	public int fill(FluidStack aFluid) {return fill(aFluid, T);}
	@Override
	public int fill(FluidStack aFluid, boolean aDoFill) {
		if (aFluid == null) return 0;
		if (aDoFill) {
			if (mFluid == null) {
				if (mFixedFluid) return 0; // 如果 fix 到了 null 则不能添加
				mFluid = aFluid.copy();
				mChangedFluids = T;
				mAmount = Math.min(capacity(aFluid), aFluid.amount);
				return mVoidExcess ? aFluid.amount : (int)mAmount;
			}
			if (!contains(aFluid)) return 0;
			long tCapacity = capacity(aFluid), tFilled = tCapacity - mAmount;
			if (aFluid.amount < tFilled) {
				mAmount += aFluid.amount;
				tFilled = aFluid.amount;
			} else mAmount = tCapacity;
			return mVoidExcess ? aFluid.amount : (int)tFilled;
		}
		return UT.Code.bindInt(mFluid == null ? (mFixedFluid ? 0 : mVoidExcess ? aFluid.amount : Math.min(capacity(aFluid), aFluid.amount)) : (contains(aFluid) ? mVoidExcess ? aFluid.amount : Math.min(capacity(aFluid) - mAmount, aFluid.amount) : 0));
	}
	
	public boolean canFillAll(FluidStack aFluid) {return aFluid == null || aFluid.amount <= 0 || (mFluid == null ? !mFixedFluid && (mVoidExcess || aFluid.amount <= capacity(aFluid)) : contains(aFluid) && (mVoidExcess || mAmount + aFluid.amount <= capacity(aFluid)));}
	public boolean canFillAll(long aAmount) {return aAmount <= 0 || mVoidExcess || mAmount + aAmount <= capacity();}
	
	public boolean fillAll(FluidStack aFluid) {
		if (aFluid == null || aFluid.amount <= 0) return T;
		if (mFluid == null) {
			if (mFixedFluid) return F; // 如果 fix 到了 null 则不能添加
			long tCapacity = capacity(aFluid);
			if (aFluid.amount <= tCapacity || mVoidExcess) {
				mFluid = aFluid.copy();
				mChangedFluids = T;
				mAmount = aFluid.amount;
				if (mAmount > tCapacity) mAmount = tCapacity;
				return T;
			}
			return F;
		}
		if (contains(aFluid)) {
			if (mAmount + aFluid.amount <= capacity()) {
				mAmount += aFluid.amount;
				return T;
			}
			if (mVoidExcess) {
				mAmount = capacity();
				return T;
			}
		}
		return F;
	}
	
	public boolean fillAll(FluidStack aFluid, long aMultiplier) {
		if (aMultiplier <= 0) return T;
		if (aMultiplier == 1) return fillAll(aFluid);
		if (aFluid == null || aFluid.amount <= 0) return T;
		if (mFluid == null) {
			if (mFixedFluid) return F; // 如果 fix 到了 null 则不能添加
			long tCapacity = capacity(aFluid);
			if (aFluid.amount * aMultiplier <= tCapacity || mVoidExcess) {
				mFluid = aFluid.copy();
				mChangedFluids = T;
				mAmount = aFluid.amount * aMultiplier;
				if (mAmount > tCapacity) mAmount = tCapacity;
				return T;
			}
			return F;
		}
		if (contains(aFluid)) {
			if (mAmount + aFluid.amount * aMultiplier <= capacity()) {
				mAmount += aFluid.amount * aMultiplier;
				return T;
			}
			if (mVoidExcess) {
				mAmount = capacity();
				return T;
			}
		}
		return F;
	}
	
	/** Resets Tank Contents entirely */
	public FluidTankGT setEmpty() {
		if (!mFixedFluid) {
			if (mFluid != null) mChangedFluids = T;
			mFluid  = null;
		}
		mAmount = 0;
		return this;
	}
	/** Sets Fluid Content, taking Amount from the Fluid Parameter  */
	public FluidTankGT setFluid(FluidStack aFluid) {
		if (aFluid == null) return setEmpty();
		if (!mFixedFluid || contains(aFluid)) {
			if (!FL.equal(mFluid, aFluid)) mChangedFluids = T;
			mFluid  = aFluid;
			mAmount = mFluid.amount;
		}
		return this;
	}
	/** Sets Fluid Content and Amount */
	public FluidTankGT setFluid(FluidStack aFluid, long aAmount) {
		if (aFluid == null) return setEmpty();
		if (!mFixedFluid || contains(aFluid)) {
			if (!FL.equal(mFluid, aFluid)) mChangedFluids = T;
			mFluid  = aFluid;
			mAmount = aAmount;
		}
		return this;
	}
	/** Sets Fluid Content, taking Amount from the Tank Parameter  */
	public FluidTankGT setFluid(FluidTankGT aTank) {
		if (aTank == null || aTank.mFluid == null) return setEmpty();
		if (!mFixedFluid || contains(aTank.mFluid)) {
			if (!FL.equal(mFluid, aTank.mFluid)) mChangedFluids = T;
			mFluid  = FL.amount(aTank.mFluid, aTank.mAmount);
			mAmount = aTank.mAmount;
		}
		return this;
	}
	/** Sets the Tank Index for easier Reverse Mapping. */
	public FluidTankGT setIndex(int aIndex) {mIndex = aIndex; return this;}
	/** Sets the Capacity, and yes it accepts 63 Bit Numbers */
	public FluidTankGT setCapacity(long aCapacity) {if (aCapacity >= 0) mCapacity = aCapacity; return this;}
	/** Always keeps at least 0 Liters of Fluid instead of setting it to null */
	public FluidTankGT setPreventDraining() {return setPreventDraining(T);}
	/** Always keeps at least 0 Liters of Fluid instead of setting it to null */
	public FluidTankGT setPreventDraining(boolean aPrevent) {
		mSaveEmpty = mPreventDraining = aPrevent; return this;}
	/** Voids any Overlow */
	public FluidTankGT setVoidExcess() {return setVoidExcess(T);}
	/** Voids any Overlow */
	public FluidTankGT setVoidExcess(boolean aVoidExcess) {mVoidExcess = aVoidExcess; return this;}
	/** Sets Tank capacity Map, should it be needed. */
	public FluidTankGT setCapacity(RecipeMap aMap, long aCapacityMultiplier) {mAdjustableCapacity = aMap.mMinInputTankSizes; mAdjustableMultiplier = aCapacityMultiplier; return this;}
	/** Sets Tank capacity Map, should it be needed. */
	public FluidTankGT setCapacity(Map<String, Long> aMap, long aCapacityMultiplier) {mAdjustableCapacity = aMap; mAdjustableMultiplier = aCapacityMultiplier; return this;}
	
	public boolean isEmpty  () {return mFluid == null || (mPreventDraining && mAmount == 0);}
	public boolean isFull   () {return mAmount     >= capacity();}
	public boolean isHalf   () {return mAmount * 2 >= capacity();}
	public boolean overHalf () {return mAmount * 2 >  capacity();}
	public boolean underHalf() {return mAmount * 2 <  capacity();}
	
	public boolean contains(Fluid aFluid) {return mFluid != null && mFluid.getFluid() == aFluid;}
	public boolean contains(FluidStack aFluid) {return FL.equal(mFluid, aFluid);}
	
	public boolean has(long aAmount) {return mAmount >= aAmount;}
	public boolean has() {return mAmount > 0;}
	
	public boolean check() {if (mChangedFluids) {mChangedFluids = F; return T;} return F;}
	public boolean update() {return mChangedFluids = T;}
	public boolean changed() {return mChangedFluids;}
	
	public long amount() {return mFluid == null ? 0 : mAmount;}
	public long amount(long aMax) {return mFluid == null || aMax <= 0 ? 0 : Math.min(mAmount, aMax);}
	
	public long capacity (                 ) {return mAdjustableCapacity == null ? mCapacity : capacity_(mFluid);}
	public long capacity (FluidStack aFluid) {return mAdjustableCapacity == null ? mCapacity : capacity_(aFluid);}
	public long capacity (Fluid      aFluid) {return mAdjustableCapacity == null ? mCapacity : capacity_(aFluid);}
	public long capacity (String     aFluid) {return mAdjustableCapacity == null ? mCapacity : capacity_(aFluid);}
	public long capacity_(FluidStack aFluid) {return aFluid == null ? mCapacity : capacity_(aFluid.getFluid());}
	public long capacity_(Fluid      aFluid) {return aFluid == null ? mCapacity : capacity_(aFluid.getName());}
	public long capacity_(String     aFluid) {
		if (aFluid == null) return mCapacity;
		Long tSize = mAdjustableCapacity.get(aFluid);
		return tSize == null ? Math.max(mAmount, mCapacity) : Math.max(tSize * mAdjustableMultiplier, Math.max(mAmount, mCapacity));
	}
	
	public String name() {return mFluid == null ? null : mFluid.getFluid().getName();}
	public String name(boolean aLocalised) {return FL.name(mFluid, aLocalised);}
	
	public String content() {return content("Empty");}
	public String content(String aEmptyMessage) {return  mFluid == null ? aEmptyMessage                     : UT.Code.makeString(amount()) + " L of " + name(T) + " (" + (FL.gas(mFluid) ? "Gaseous" : "Liquid") + ")";}
	public String contentcap() {return mFluid == null ? "Capacity: " + UT.Code.makeString(mCapacity) + " L" : UT.Code.makeString(amount()) + " L of " + name(T) + " (" + (FL.gas(mFluid) ? "Gaseous" : "Liquid") + "); Max: "+UT.Code.makeString(capacity())+" L)";}
	
	public Fluid fluid() {return mFluid == null ? null : mFluid.getFluid();}
	
	public FluidStack make(int aAmount) {return FL.make(fluid(), aAmount);}
	
	public FluidStack get() {return mFluid;}
	public FluidStack get(long aMax) {return mFluid == null || aMax <= 0 ? null : new FluidStack(mFluid, UT.Code.bindInt(Math.min(mAmount, aMax)));}
	
	@Override public FluidStack getFluid() {if (mFluid != null) mFluid.amount = UT.Code.bindInt(mAmount); return mFluid;}
	@Override public int getFluidAmount() {return UT.Code.bindInt(mAmount);}
	@Override public int getCapacity() {return UT.Code.bindInt(capacity());}
	@Override public FluidTankInfo getInfo() {return new FluidTankInfo(mFluid == null ? null : mFluid.copy(), UT.Code.bindInt(capacity()));}
}
