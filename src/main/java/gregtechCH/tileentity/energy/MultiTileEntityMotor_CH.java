package gregtechCH.tileentity.energy;

import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.data.TD;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.util.UT;
import gregtechCH.config.ConfigForge_CH.*;
import gregtechCH.data.LH_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;
import static gregtechCH.data.CS_CH.IconType.*;
/**
 * @author CHanzy
 * base class of Motor, use in steam turbine, motor liquid, motor gas (if have).
 * using method similar to the liquid burning box, Motor liquid, Motor, etc
 */
public abstract class MultiTileEntityMotor_CH extends TileEntityBase09FacingSingle implements ITileEntityFunnelAccessible, ITileEntityTapAccessible, ITileEntityEnergy, ITileEntityRunningActively, ITileEntityAdjacentOnOff {

	protected short mEfficiency = 1000;
	protected long mEnergy = 0, mRate = 16, mInRate = 16;
	protected long mPEnergy = 1600;
	protected long mPCost = 2, mCRate = 16;
	protected long mInPCost = 4;
	protected long mOutput = 0;

	protected boolean mStopped = F;
	protected boolean mEmitsEnergy = F;
	protected boolean mActive = F, oActive = F;
	protected boolean mPreheat = F, oPreheat = F;
	protected boolean mCooldown = F, oCooldown = F;
	protected boolean mCounterClockwise = F, oCounterClockwise = F;

	public TagData mEnergyTypeEmitted = TD.Energy.RU;

	// NBT读写
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_ENERGY)) mEnergy = aNBT.getLong(NBT_ENERGY);
		if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
		if (aNBT.hasKey(NBT_REVERSED)) mCounterClockwise = aNBT.getBoolean(NBT_REVERSED);

		if (aNBT.hasKey(NBT_ENERGY_EMITTED)) mEnergyTypeEmitted = TagData.createTagData(aNBT.getString(NBT_ENERGY_EMITTED));

		setOutRate(aNBT);
		setInRate(aNBT);
	}
	protected void setOutRate(NBTTagCompound aNBT) {
		if (aNBT.hasKey(NBT_PREHEAT_ENERGY)) mPEnergy = aNBT.getLong(NBT_PREHEAT_ENERGY);
		if (aNBT.hasKey(NBT_EFFICIENCY)) mEfficiency = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY));
		if (aNBT.hasKey(NBT_OUTPUT)) mRate = aNBT.getLong(NBT_OUTPUT);
		if (aNBT.hasKey(NBT_PREHEAT_COST)) mPCost = aNBT.getLong(NBT_PREHEAT_COST);
		if (aNBT.hasKey(NBT_COOLDOWN_RATE)) mCRate = aNBT.getLong(NBT_COOLDOWN_RATE);
	}
	protected void setInRate(NBTTagCompound aNBT) {
		mInRate  = UT.Code.units(mRate, mEfficiency, 10000, T);
		mInPCost = UT.Code.units(mPCost, mEfficiency, 10000, T);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
		UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
		UT.NBT.setBoolean(aNBT, NBT_REVERSED, mCounterClockwise);

		UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mActive); // for OmniOcular usage
		UT.NBT.setBoolean(aNBT, NBT_ACTIVE_ENERGY, mEmitsEnergy); // for OmniOcular usage
		UT.NBT.setNumber(aNBT, NBT_OUTPUT_NOW, mOutput); // for OmniOcular usage
		UT.NBT.setBoolean(aNBT, NBT_PREHEAT, mPreheat); // for OmniOcular usage
		UT.NBT.setBoolean(aNBT, NBT_COOLDOWN_CH, mCooldown); // for OmniOcular usage

		UT.NBT.setNumber(aNBT, NBT_PREHEAT_ENERGY, mPEnergy);
	}

	// tooltips
	@Override
	public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
		toolTipsEnergy(aList);
		toolTipsUseful(aList);
		toolTipsImportant(aList);
		toolTipsOther(aList, aStack, aF3_H);
	}
	protected void toolTipsEnergy(List<String> aList) {
		aList.add(LH.getToolTipEfficiency(mEfficiency));
		LH.addEnergyToolTips(this, aList, null, mEnergyTypeEmitted, null, LH.get(LH.FACE_FRONT));
	}
	protected void toolTipsUseful(List<String> aList) {
		aList.add(Chat.GREEN + LH_CH.get(LH_CH.TOOLTIP_PREHEAT));
	}
	protected void toolTipsImportant(List<String> aList) {}
	protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
		aList.add(Chat.DGRAY    + LH.get(LH.TOOL_TO_SET_DIRECTION_MONKEY_WRENCH));
		aList.add(Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
		super.addToolTips(aList, aStack, aF3_H);
	}

	// 工具右键
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;

		if (isClientSide()) return 0;

		if (aTool.equals(TOOL_monkeywrench)) {
			mCounterClockwise=!mCounterClockwise;
			if (aChatReturn != null) aChatReturn.add(mCounterClockwise ? "Counterclockwise" : "Clockwise");
			causeBlockUpdate();
			doEnetUpdate();
			return 10000;
		}
		if (aTool.equals(TOOL_magnifyingglass)) {
			if (aChatReturn != null) {
				aChatReturn.add(mCounterClockwise ? "Counterclockwise" : "Clockwise");
				if (mPreheat) aChatReturn.add("Preheating: " + LH.percent(UT.Code.units(Math.min(mEnergy, mPEnergy), mPEnergy, 10000, F)) + "%");
			}
			return 1;
		}

		return 0;
	}

	// 每 tick 转换
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		if (aIsServerSide) {
			// 转换能量
			convert();
			// 状态判断
			if (checkOverload()) {
				// 超载
				doOverload();
			} else
			if (checkActive()) {
				// 可以输出
				doActive();
			} else
			if (checkPreheat()) {
				// 正在预热
				doPreheat();
			} else
			if (checkCooldown()) {
				// 正在冷却
				doCooldown();
			} else {
				// 能量耗尽
				doElse();
			}
			// 可以释放能量
			emitEnergy();
			// 淋雨损坏等
			explodeCheck();
		}
	}

	protected abstract void convert();

	protected boolean checkOverload() {
		return F;
	}
	protected void doOverload() {
		mActive = F;
		mPreheat = F;
		mCooldown = F;
		mOutput = 0;
	}
	protected boolean checkActive() {
		return mEnergy >= mPEnergy + getEnergySizeOutputMin(mEnergyTypeEmitted, SIDE_ANY);
	}
	protected void doActive() {
		mActive = T;
		mPreheat = F;
		mCooldown = F;
		mOutput = getOutput();
		mEnergy -= mOutput;
	}
	protected abstract long getOutput();
	protected boolean checkPreheat() {
		return mEnergy >= mPCost;
	}
	protected void doPreheat() {
		mActive = F;
		mPreheat = T;
		mCooldown = F;
		mOutput = 0;
		mEnergy -= mPCost;
	}
	protected boolean checkCooldown() {
		return mEnergy >= mCRate;
	}
	protected void doCooldown() {
		mActive = F;
		mPreheat = F;
		mCooldown = T;
		mOutput = 0;
		mEnergy -= mCRate;
	}
	protected void doElse() {
		stop();
	}
	protected void stop() {
		mActive = F;
		mPreheat = F;
		mCooldown = F;
		mOutput = 0;
		mEnergy = 0;
	}
	protected void emitEnergy() {
		mEmitsEnergy = F;
		if (!mStopped) {
			if (mActive) {
				doEmitEnergy();
			}
		} else {
			// 主动关机
			stop();
		}
	}
	protected void doEmitEnergy() {
		if (mCounterClockwise) {
			mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, -mOutput, 1, this) > 0;
		} else {
			mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, mOutput, 1, this) > 0;
		}
	}
	protected void explodeCheck() {
		if (DATA_MACHINES.motorExplodeCheck) {
			if (mTimer % 600 == 5) {
				if (mActive) doDefaultStructuralChecks();
			}
		}
	}

	// 一些接口
	@Override public boolean allowCovers(byte aSide) {return T;}
	
	public boolean isInput (byte aSide) {return aSide == OPOS[mFacing];}
	public boolean isOutput(byte aSide) {return aSide == mFacing;}

	@Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return aEmitting && aEnergyType == mEnergyTypeEmitted;}
	@Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || (!mStopped )) && (SIDES_INVALID[aSide] || isInput (aSide)) && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
	@Override public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {return (SIDES_INVALID[aSide] || isOutput(aSide)) && super.isEnergyEmittingTo   (aEnergyType, aSide, aTheoretical);}
	@Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mRate;}
	@Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mRate/2;}
	@Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mRate*2;}
	@Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mInRate;}
	@Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return mInRate/2;}
	@Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mInRate*2;}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeEmitted.AS_LIST;}

	@Override public boolean canDrop(int aInventorySlot) {return F;}

	@Override public boolean getStateRunningPossible() {return T;}
	@Override public boolean getStateRunningPassively() {return mPreheat || mActive;}
	@Override public boolean getStateRunningActively() {return mEmitsEnergy;}
	public boolean setAdjacentOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
	public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
	public boolean getStateOnOff() {return !mStopped;}

	// Icons，图像动画
	public abstract IIconContainer[] getIIconContainers(IconType aIconType);

	@Override
	public boolean onTickCheck(long aTimer) {
		return oActive != mActive || oPreheat != mPreheat || oCooldown != mCooldown || mCounterClockwise != oCounterClockwise || super.onTickCheck(aTimer);
	}
	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oActive = mActive;
		oPreheat = mPreheat;
		oCooldown = mCooldown;
		oCounterClockwise = mCounterClockwise;
	}
	@Override public void setVisualData(byte aData) {
		mActive     		= ((aData & 1)  != 0);
		mPreheat    		= ((aData & 2)  != 0);
		mCooldown   		= ((aData & 4)  != 0);
		mCounterClockwise   = ((aData & 8)  != 0);
	}
	@Override public byte getVisualData() {return (byte)((mActive?1:0) | (mPreheat?2:0) | (mCooldown?4:0) | (mCounterClockwise?8:0));}

	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		if (!aShouldSideBeRendered[aSide]) return null;
		int aIndex = aSide==mFacing?0:aSide==OPOS[mFacing]?1:2;
		return BlockTextureMulti.get(BlockTextureDefault.get(getIIconContainers(COLORED)[aIndex], mRGBa), BlockTextureDefault.get(((mPreheat||mCooldown)?(mCounterClockwise?getIIconContainers(OVERLAY_PREHEAT_L):getIIconContainers(OVERLAY_PREHEAT_R)):(mActive?(mCounterClockwise?getIIconContainers(OVERLAY_ACTIVE_L):getIIconContainers(OVERLAY_ACTIVE_R)):getIIconContainers(OVERLAY)))[aIndex]));
	}
}
