package gregtechCH.tileentity.cores.motors;

import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import gregtechCH.tileentity.cores.IMTEC_ToolTips;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
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

/**
 * @author CHanzy
 * 基本重写了原版的逻辑
 * 使用多态包含 MTEC_MotorMainBase 的方法实现单一 core (因为数据需要共享)
 */
public abstract class MTEC_Motor implements IMTEC_MotorTick, IMTEC_ToolTips, IMTEC_CanExplode {
    // the instance of TileEntityBase09FacingSingle
    protected final TileEntityBase09FacingSingle mTE;
    // the instance of MTEC_MotorMainBase
    protected MTEC_MotorMainBase mD; // Data Motor
    // the instance of IMTEC_Texture
    protected IMTEC_Texture mDI; // Data icon
    protected MTEC_Motor(TileEntityBase09FacingSingle aTE) {UT_CH.Debug.assertWhenDebug(aTE instanceof ITileEntityEnergy); mTE = aTE;}

    /* stuff to override */
    protected abstract MTEC_MotorMainBase getNewCoreMain();
    protected abstract IMTEC_Texture getNewCoreIcon();
    protected ITileEntityEnergy getEnergyEmitter() {return (ITileEntityEnergy)mTE;}
    protected TileEntityBase01Root getFluidEmitter() {return mTE;}
    protected byte getFluidEmittingSide() {return OPOS[mTE.mFacing];}
    protected void convertToTanks(FluidStack... aFluids) {/**/}
    protected void convertAutoOutput() {/**/}

    /* main code */
    // init of core
    // pre: 内部 core 的对象创建，不需要读取 NBT 的初始化
    public void preInit() {mDI = getNewCoreIcon(); mD = getNewCoreMain();}
    // init: 只需要读取 NBT 直接进行的初始化
    public void init(NBTTagCompound aNBT) {mD.init(aNBT);}
    // post: 需要依据读取 NBT 得到的数据进行初始化的数据
    public final void postInit(NBTTagCompound aNBT) {
        postInitNBT(aNBT);
        postInitRate(aNBT);
        postInitTank();
    }
    protected void postInitNBT(NBTTagCompound aNBT) {mD.postInitNBT(aNBT);}
    protected void postInitRate(NBTTagCompound aNBT) {mD.postInitRate(aNBT);}
    protected void postInitTank() {mD.postInitTank();}

    // NBT读写
    public void writeToNBT(NBTTagCompound aNBT) {mD.writeToNBT(aNBT);}

    // Motor 基本方法
    @Override public boolean onTickStopCheck(long aTimer) {return mD.onTickStopCheck(aTimer);}
    @Override public void onTickConvert(long aTimer) {mD.onTickConvert(aTimer);}
    @Override public boolean onTickCheckOverload(long aTimer) {return mD.onTickCheckOverload(aTimer);}
    @Override public void onTickDoOverload(long aTimer) {mD.onTickDoOverload(aTimer);}
    @Override public boolean onTickCheckPreheat(long aTimer) {return mD.onTickCheckPreheat(aTimer);}
    @Override public void onTickDoPreheat(long aTimer) {mD.onTickDoPreheat(aTimer);}
    @Override public boolean onTickCheckCooldown(long aTimer) {return mD.onTickCheckCooldown(aTimer);}
    @Override public void onTickDoCooldown(long aTimer) {mD.onTickDoCooldown(aTimer);}
    @Override public boolean onTickCheckActive(long aTimer) {return mD.onTickCheckActive(aTimer);}
    @Override public final void onTickDoActive(long aTimer) {onTickDoActive2(aTimer); mD.onTickDoEmit(aTimer);}
    protected void onTickDoActive2(long aTimer) {mD.onTickDoActive(aTimer);}
    @Override public void onTickDoElse(long aTimer) {mD.onTickDoElse(aTimer);}
    @Override public void onTickExplodeCheck(long aTimer) {mD.onTickExplodeCheck(aTimer);}

    // tooltips
    @Override public void toolTipsMultiblock(List<String> aList) {/**/}
    @Override public void toolTipsRecipe(List<String> aList) {/**/}
    @Override public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(mD.mEfficiency));
        LH.addEnergyToolTips((ITileEntityEnergy)mTE, aList, null, mD.mEnergyTypeEmitted, null, LH.get(LH.FACE_FRONT));
    }
    @Override public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {/**/}
    @Override public void toolTipsUseful(List<String> aList)  {aList.add(LH.Chat.GREEN + LH_CH.get(LH_CH.TOOLTIP_PREHEAT));}
    @Override public void toolTipsImportant(List<String> aList) {/**/}
    @Override public void toolTipsHazard(List<String> aList) {/**/}
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_SET_DIRECTION_MONKEY_WRENCH));
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
    }

    // 工具右键
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {

        if (toolCheckMonkeyWrench(aTool)) {
            onMonkeyWrench(aChatReturn);
            return 10000;
        }

        if (toolCheckMagnifyingGlass(aTool)) {
            onMagnifyingGlass(aChatReturn);
            return 1;
        }

        return 0;
    }
    protected boolean toolCheckMonkeyWrench(String aTool) {return aTool.equals(TOOL_monkeywrench);}
    protected void onMonkeyWrench(List<String> aChatReturn) {
        mD.mCounterClockwise=!mD.mCounterClockwise;
        if (aChatReturn != null) aChatReturn.add(mD.mCounterClockwise ? "Counterclockwise" : "Clockwise");
        mTE.causeBlockUpdate();
        mTE.doEnetUpdate();
    }
    protected boolean toolCheckMagnifyingGlass(String aTool) {return aTool.equals(TOOL_magnifyingglass);}
    protected final void onMagnifyingGlass(List<String> aChatReturn) {
        if (aChatReturn == null) return;
        onMagnifyingGlass2(aChatReturn);
    }
    protected void onMagnifyingGlass2(List<String> aChatReturn) {
        aChatReturn.add(mD.mCounterClockwise ? "Counterclockwise" : "Clockwise");
        if (mD.mPreheat) {
            aChatReturn.add("Preheating: " + LH.percent(UT.Code.units(Math.min(mD.mEnergy, mD.mPEnergy), mD.mPEnergy, 10000, F)) + "%");
        }
        if (mD.mActive) {
            aChatReturn.add("Active:");
            aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(mD.getRealEfficiency()) + "%");
            aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + mD.mOutput + " " + mD.mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t");
        }
    }

    // data sync
    public boolean onTickCheck(long aTimer) {
        return mD.oActive != mD.mActive || mD.oPreheat != mD.mPreheat || mD.oCooldown != mD.mCooldown || mD.mCounterClockwise != mD.oCounterClockwise;
    }
    public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        mD.oActive = mD.mActive;
        mD.oPreheat = mD.mPreheat;
        mD.oCooldown = mD.mCooldown;
        mD.oCounterClockwise = mD.mCounterClockwise;
    }
    public void setVisualData(byte aData) {
        mD.mActive     		 = ((aData & 1)  != 0);
        mD.mPreheat    		 = ((aData & 2)  != 0);
        mD.mCooldown   		 = ((aData & 4)  != 0);
        mD.mCounterClockwise = ((aData & 8)  != 0);
    }
    public byte getVisualData() {return (byte)((mD.mActive?1:0) | (mD.mPreheat?2:0) | (mD.mCooldown?4:0) | (mD.mCounterClockwise?8:0));}
    
    public boolean canExplode() {return mD.canExplode();}
    public void explode(boolean aInstant) {mD.explode(aInstant);}
    public void onBreakBlock() {/**/}
    public void onFacingChange(byte aPreviousFacing) {/**/}
    public void onWalkOver(EntityLivingBase aEntity) {if (SIDES_TOP[mTE.mFacing] && mD.mActive) {aEntity.rotationYaw=aEntity.rotationYaw+(mD.mCounterClockwise?-5.0F:+5.0F)*entityRotationSpeed(); aEntity.rotationYawHead=aEntity.rotationYawHead+(mD.mCounterClockwise?-5.0F:+5.0F)*entityRotationSpeed();}}
    protected float entityRotationSpeed() {return 1.0F;}

    // surface
    public float getSurfaceSizeAttachable(byte aSide) {return mTE.getSurfaceSize(aSide);}
    public boolean isSideSolid           (byte aSide) {return T;}
    public boolean isSurfaceOpaque       (byte aSide) {return T;}

    // tanks
    public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return mD.getFluidTankFillable(aSide, aFluidToFill);}
    public IFluidTank getFluidTankDrainable(byte aSide, FluidStack aFluidToDrain) {return mD.getFluidTankDrainable(aSide, aFluidToDrain);}
    public IFluidTank[] getFluidTanks(byte aSide) {return mD.getFluidTanks(aSide);}
    public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {return mD.funnelFill(aSide, aFluid, aDoFill);}
    public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {return mD.tapDrain(aSide, aMaxDrain, aDoDrain);}

    public boolean canFillExtra(FluidStack aFluid) {return mD.canFillExtra(aFluid);}

    // inventory
    public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return mD.getDefaultInventory(aNBT);}
    public boolean canDrop(int aInventorySlot) {return mD.canDrop(aInventorySlot);}

    // energy interfaces
    protected abstract boolean isInput (byte aSide);
    protected abstract boolean isOutput(byte aSide);

    public boolean allowCovers(byte aSide) {return T;}
    public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return mD.isEnergyType(aEnergyType, aSide, aEmitting);}
    public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return mD.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
    public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {return mD.isEnergyEmittingTo(aEnergyType, aSide, aTheoretical);}
    public long getEnergyOffered(TagData aEnergyType, byte aSide, long aSize) {return mD.getEnergyOffered(aEnergyType, aSide, aSize);}
    public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mD.getEnergySizeOutputRecommended(aEnergyType, aSide);}
    public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mD.getEnergySizeOutputMin(aEnergyType, aSide);}
    public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mD.getEnergySizeOutputMax(aEnergyType, aSide);}
    public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mD.getEnergySizeInputRecommended(aEnergyType, aSide);}
    public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return mD.getEnergySizeInputMin(aEnergyType, aSide);}
    public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mD.getEnergySizeInputMax(aEnergyType, aSide);}
    public Collection<TagData> getEnergyTypes(byte aSide) {return mD.getEnergyTypes(aSide);}

    public boolean getStateRunningPossible() {return mD.getStateRunningPossible();}
    public boolean getStateRunningPassively() {return mD.getStateRunningPassively();}
    public boolean getStateRunningActively() {return mD.getStateRunningActively();}
    public boolean setAdjacentOnOff(boolean aOnOff) {return mD.setAdjacentOnOff(aOnOff);}
    public boolean setStateOnOff(boolean aOnOff) {return mD.setStateOnOff(aOnOff);}
    public boolean getStateOnOff() {return mD.getStateOnOff();}

    // Icons，图像动画
    public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return mDI.getTexture(aBlock, aRenderPass, aSide, aShouldSideBeRendered);}

    public byte getDefaultSide() {return SIDE_UP;}
    public boolean[] getValidSides() {return SIDES_VALID;}
}
