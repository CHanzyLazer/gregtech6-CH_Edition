package gregtechCH.tileentity.cores.boilers;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.IMTEC_ToolTips;
import gregtechCH.util.UT_CH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

// 放弃旧逻辑的兼容但是这个类可以保留（用来比较方便的看出我的修改），只是限制直接生成
public abstract class MTEC_BoilerTank_Greg implements IMTEC_ToolTips, IMTEC_BoilerTank {
    // the instance of TileEntityBase01Root
    protected final TileEntityBase01Root mTE;
    public MTEC_BoilerTank_Greg(TileEntityBase01Root aTE) {UT_CH.Debug.assertWhenDebug(aTE instanceof IMTEC_HasBoilerTank); mTE = aTE;}
    protected IMTEC_HasBoilerTank te() {return (IMTEC_HasBoilerTank)mTE;}
    
    /* main code */
    protected byte mBarometer = 0, oBarometer = 0;
    protected short mEfficiency = 10000;
    protected short mCoolDownResetTimer = 128;
    protected long mEnergy = 0, mCapacity = 640000, mOutput = 6400;
    protected TagData mEnergyTypeAccepted = TD.Energy.HU;
    protected FluidTankGT[] mTanks = new FluidTankGT[] {new FluidTankGT(4000), new FluidTankGT(64000)};
    
    public void readFromNBT(NBTTagCompound aNBT) {
        mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_VISUAL)) mBarometer = aNBT.getByte(NBT_VISUAL);
        if (aNBT.hasKey(NBT_CAPACITY)) mCapacity = aNBT.getLong(NBT_CAPACITY);
        if (aNBT.hasKey(NBT_CAPACITY_SU)) mTanks[1].setCapacity(aNBT.getLong(NBT_CAPACITY_SU));
        if (aNBT.hasKey(NBT_OUTPUT_SU)) mOutput = aNBT.getLong(NBT_OUTPUT_SU);
        if (aNBT.hasKey(NBT_EFFICIENCY)) mEfficiency = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY));
        if (aNBT.hasKey(NBT_ENERGY_ACCEPTED)) mEnergyTypeAccepted = TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED));
        for (int i = 0; i < mTanks.length; ++i) mTanks[i].readFromNBT(aNBT, NBT_TANK+"."+i);
    }
    public void writeToNBT(NBTTagCompound aNBT) {
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        if (mEfficiency != 10000) aNBT.setShort(NBT_EFFICIENCY, mEfficiency);
        for (int i = 0; i < mTanks.length; ++i) mTanks[i].writeToNBT(aNBT, NBT_TANK+"."+i);
    }
    
    // tooltips
    @Override public void toolTipsMultiblock(List<String> aList) {/**/}
    @Override public void toolTipsRecipe(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X)        + " 1 L " + FL.name(FluidRegistry.WATER, T) + " " + LH.get(LH.CONVERTS_TO_Y) + " 160 L " + FL.name(FL.Steam.make(0), T) + " " + LH.get(LH.CONVERTS_USING_Z) + " 80 " + mEnergyTypeAccepted.getLocalisedNameShort());
    }
    @Override public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(mEfficiency));
        aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_INPUT)           + ": " + LH.Chat.WHITE + (mOutput/STEAM_PER_EU)                        + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t ("+LH.get(LH.FACE_ANY)+")");
        aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_CAPACITY)        + ": " + LH.Chat.WHITE + mCapacity                                     + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE);
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT)          + ": " + LH.Chat.WHITE + UT.Code.units(mOutput, 10000, mEfficiency, F) + " " + TD.Energy.STEAM.getLocalisedChatNameLong()      + LH.Chat.WHITE + "/t ("+LH.get(LH.FACE_TOP)+")");
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_CAPACITY)        + ": " + LH.Chat.WHITE + mCapacity                                     + " " + TD.Energy.STEAM.getLocalisedChatNameLong()      + LH.Chat.WHITE);
    }
    @Override public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {/**/}
    @Override public void toolTipsUseful(List<String> aList) {
    }
    @Override public void toolTipsImportant(List<String> aList) {
        aList.add(LH.Chat.ORANGE   + LH.get(LH.REQUIREMENT_WATER_PURE));
        aList.add(LH.Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TO_TANK));
    }
    @Override public void toolTipsHazard(List<String> aList) {
        aList.add(LH.Chat.DRED     + LH.get(LH.HAZARD_EXPLOSION_STEAM));
        aList.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN));
    }
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_DECALCIFY_CHISEL));
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
    }
    
    // ticking
    protected void resetCoolDownTimer() {mCoolDownResetTimer = 128;}
    @Override public void onTickConvert(long aTimer) {
        // Convert Water to Steam
        long tConversions = Math.min(mTanks[1].capacity() / 2560, Math.min(mEnergy / 80, mTanks[0].amount()));
        if (tConversions > 0) {
            mTanks[0].remove(tConversions);
            if (mTE.rng(10) == 0 && mEfficiency > 5000 && mTanks[0].has() && !FL.distw(mTanks[0])) {
                mEfficiency -= tConversions;
                if (mEfficiency < 5000) mEfficiency = 5000;
            }
            mTanks[1].setFluid(FL.Steam.make(mTanks[1].amount() + UT.Code.units(tConversions, 10000, mEfficiency * 160, F)));
            mEnergy -= tConversions * 80;
            resetCoolDownTimer();
        }
    }
    @Override public void onTickCoolDown(long aTimer) {
        // Remove Steam and Heat during the process of cooling down.
        if (mCoolDownResetTimer-- <= 0) {
            mCoolDownResetTimer = 0;
            mEnergy -= (mOutput * 64) / STEAM_PER_EU;
            GarbageGT.trash(mTanks[1], mOutput * 64);
            if (mEnergy <= 0) {
                mEnergy = 0;
                resetCoolDownTimer();
            }
        }
    }
    @Override public final void onTickEmitSteam(long aTimer) {
        long tAmount = mTanks[1].amount() - mTanks[1].capacity() / 2;
        // Emit Steam
        doEmitSteam(tAmount);
    }
    @Override public void onTickSetBarometer(long aTimer) {
        // Set Barometer
        mBarometer = (byte)UT.Code.scale(mTanks[1].amount(), mTanks[1].capacity(), 31, F);
    }
    @Override public final void onTickExplodeCheck(long aTimer) {
        // Well the Boiler gets structural Damage when being too hot, or when being too full of Steam.
        if (checkExplode()) {
            mTE.explode(F);
        }
    }
    // 用于重写
    protected void doEmitSteam(long aAmount) {
        if (aAmount > 0) doEmitSteam2(Math.min(aAmount > mTanks[1].capacity() / 4 ? mOutput * 2 : mOutput, aAmount));
    }
    protected void doEmitSteam2(long aAmount) {
        FL.move(mTanks[1], mTE.getAdjacentTank(SIDE_UP), aAmount);
    }
    protected boolean checkExplode() {
        return mEnergy > mCapacity || mTanks[1].isFull();
    }
    
    
    // toolClick
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {

        if (aTool.equals(TOOL_plunger)) {
            return onPlunger(aPlayer, aChatReturn);
        }
        if (aTool.equals(TOOL_chisel)) {
            int rResult = 10000 - mEfficiency;
            if (rResult > 0) {
                if (mBarometer > 15) {
                    mTE.explode(F);
                } else {
                    if (mEnergy+mTanks[1].amount()/STEAM_PER_EU > 2000) UT.Entities.applyHeatDamage(aPlayer, (mEnergy+mTanks[1].amount()/2.0F) / 2000.0F);
                    mTanks[1].setEmpty();
                    mEfficiency = 10000;
                    mEnergy = 0;
                    return rResult;
                }
            }
            return 0;
        }
        
        if (aTool.equals(TOOL_thermometer)) {
            if (aChatReturn != null) aChatReturn.add("Stored Heat Units: " + mEnergy + " / " + mCapacity + " HU");
            return 10000;
        }

        if (notSuperMagnifyingGlass() && aTool.equals(TOOL_magnifyingglass)) {
            if (aChatReturn != null) onMagnifyingGlass(aChatReturn);
            return 1;
        }

        return 0;
    }
    
    // 重写修改搋子的操作
    public long onPlunger(Entity aPlayer, List<String> aChatReturn) {
        if (mTanks[0].has()) return GarbageGT.trash(mTanks[0]);
        return GarbageGT.trash(mTanks[1]);
    }
    
    // GTCH，是否 super 中不包含放大镜的操作
    protected boolean notSuperMagnifyingGlass() {return T;}

    public void onMagnifyingGlass(List<String> aChatReturn) {
        if (mEfficiency < 10000) {
            aChatReturn.add("Calcification: " + LH.percent(10000 - mEfficiency) + "%");
        } else {
            aChatReturn.add("No Calcification in this Boiler");
        }
        aChatReturn.add(mTanks[0].content("WARNING: NO WATER!!!"));
    }
    
    
    // explode things
    public void onRemovedByPlayer(World aWorld, EntityPlayer aPlayer, boolean aWillHarvest) {
        if (mBarometer > 4 && mTE.isServerSide() && !UT.Entities.isCreative(aPlayer)) mTE.explode(T);
    }
    public void onExploded(Explosion aExplosion) {
        if (mTE.isServerSide() && mBarometer > 4) mTE.explode(T);
    }
    public void explode(boolean aInstant, double aDivisor) {
        mTE.explode(aInstant, Math.max(1, Math.sqrt(mTanks[1].amount()) / aDivisor));
    }
    
    // data sync
    public boolean onTickCheck(long aTimer) {
        mBarometer = UT.Code.bind5(mBarometer);
        return mBarometer != oBarometer;
    }
    public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        oBarometer = mBarometer;
    }
    public void setVisualData(byte aData) {
        mBarometer = (byte)(aData&31);
    }
    public byte getVisualData() {
        return mBarometer;
    }
    public byte getBarometer() {
        return mBarometer;
    }
    
    // 烫伤计算
    public void onEntityCollidedWithBlock(Entity aEntity) {if (mEnergy+mTanks[1].amount()/STEAM_PER_EU > 2000) UT.Entities.applyHeatDamage(aEntity, Math.min(10.0F, (mEnergy+mTanks[1].amount()/2.0F) / 2000.0F));}
    
    public void onBreakBlock() {/**/}
    
    // energy interfaces
    public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return !aEmitting && aEnergyType == mEnergyTypeAccepted;}
    public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return mTE.isEnergyType(aEnergyType, aSide, F);}
    public boolean isEnergyCapacitorType(TagData aEnergyType, byte aSide) {return aEnergyType == mEnergyTypeAccepted;}
    public long doInject(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {if (aDoInject) {mEnergy += Math.abs(aAmount * aSize); resetCoolDownTimer();} return aAmount;}
    public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {return mOutput/2;}
    public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mOutput/2;}
    public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return 1;}
    public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return Long.MAX_VALUE;}
    public long getEnergyStored(TagData aEnergyType, byte aSide) {return aEnergyType == mEnergyTypeAccepted ? mEnergy : 0;}
    public long getEnergyCapacity(TagData aEnergyType, byte aSide) {return aEnergyType == mEnergyTypeAccepted ? mCapacity : 0;}
    public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeAccepted.AS_LIST;}
    public Collection<TagData> getEnergyCapacitorTypes(byte aSide) {return mEnergyTypeAccepted.AS_LIST;}
    
    // tanks
    public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return SIDES_BOTTOM_HORIZONTAL[aSide] && FL.water(aFluidToFill) ? mTanks[0] : null;}
    public IFluidTank getFluidTankDrainable(byte aSide, FluidStack aFluidToDrain) {return null;}
    public IFluidTank[] getFluidTanks(byte aSide) {return mTanks;}
    public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {return FL.water(aFluid) ? mTanks[0].fill(aFluid, aDoFill) : 0;}
    
    public long getGibblValue(byte aSide) {return mTanks[1].amount();}
    public long getGibblMax(byte aSide) {return mTanks[1].capacity();}
}
