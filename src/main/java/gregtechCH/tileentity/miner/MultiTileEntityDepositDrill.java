package gregtechCH.tileentity.miner;

import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.tileentity.machines.ITileEntitySwitchableOnOff;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.misc.MultiTileEntityDeposit;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.NBT_LEVEL;


/**
 * @author CHanzy
 * 矿藏钻头，用于开采矿藏
 */
public class MultiTileEntityDepositDrill extends TileEntityBase09FacingSingle implements ITileEntityEnergy, ITileEntityRunningActively, ITileEntitySwitchableOnOff {
    
    protected short mEfficiency = 10000;
    protected long mRate = 32, mEnergy = 0;
    protected int mLevel = 0;
    
    protected boolean mActive = F, mRunning = F, mStopped = F;
    protected boolean oActive = F, oRunning = F;
    
    protected ItemStack mOreBuffer = null;
    
    protected TagData mEnergyTypeAccepted = TD.Energy.RU;
    
    @Override public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        mOreBuffer = ST.load(aNBT, NBT_INV_OUT);
        mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_INPUT)) mRate = aNBT.getLong(NBT_INPUT);
        if (aNBT.hasKey(NBT_LEVEL)) mLevel = aNBT.getInteger(NBT_LEVEL);
        if (aNBT.hasKey(NBT_EFFICIENCY)) mEfficiency = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY));
        if (aNBT.hasKey(NBT_ACTIVE)) mActive = aNBT.getBoolean(NBT_ACTIVE);
        if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
        if (aNBT.hasKey(NBT_RUNNING)) mRunning = aNBT.getBoolean(NBT_RUNNING);
        // 超过阈值能量截断
        mEnergy = Math.min(getEnergySizeInputMax(mEnergyTypeAccepted, SIDE_ANY), mEnergy);
    }
    
    @Override public void writeToNBT2(@NotNull NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mActive);
        UT.NBT.setBoolean(aNBT, NBT_RUNNING, mRunning);
        UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        ST.save(aNBT, NBT_INV_OUT, mOreBuffer);
    }
    
    @Override public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        addToolTipsRecipe(aList, aStack, aF3_H);
        addToolTipsEnergy(aList, aStack, aF3_H);
        addToolTipsSided(aList, aStack, aF3_H);
        addToolTipsUseful(aList, aStack, aF3_H);
        super.addToolTips(aList, aStack, aF3_H);
        aList.add(LH.Chat.RED + LH.Chat.BOLD + "WIP");
    }
    
    public void addToolTipsRecipe(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN  + LH_CH.get(LH_CH.DEPOSIT_LEVEL) + LH.Chat.WHITE + mLevel);
    }
    public void addToolTipsEnergy(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.getToolTipEfficiency(mEfficiency));
        aList.add(LH.Chat.GREEN + LH.get(LH.ENERGY_INPUT) + ": " + LH.Chat.WHITE + mRate + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t ("+LH_CH.getNumber(LH_CH.ENERGY_TO, getEnergySizeInputMin(mEnergyTypeAccepted, SIDE_ANY), getEnergySizeInputMax(mEnergyTypeAccepted, SIDE_ANY))+", "+LH.get(LH.FACE_TOP)+")");
    }
    public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.RED   + LH.get(LH.ITEM_OUTPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACE_RIGHT) + " (" + LH_CH.get(LH_CH.AUTO) + "), " + LH.get(LH.FACE_SIDES));
        aList.add(LH.Chat.GREEN + LH.get(LH.FLUID_INPUT) + ": " + LH.Chat.WHITE + LH.get(LH.FACE_LEFT)  + " (" + LH_CH.get(LH_CH.AUTO) + "), " + LH.get(LH.FACE_SIDES));
    }
    public void addToolTipsUseful(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.GREEN + LH_CH.get("gtch.tooltip.deposit.drill.1"));
        aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_EXPENSIVE)+ " (" + LH_CH.get(LH_CH.OVERCLOCK_SQRT) + ")");
    }
    static {
        LH_CH.add("gtch.tooltip.deposit.drill.1", "Input Lubricant can increase 1 mining level");
    }
    
    
    @Override public void onTick2(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide) {
            doFirst();
            // 活动检测
            if (checkActive()) {
                // 可以输出
                doActive();
            } else {
                doInActive();
            }
            doFinal();
        }
    }
    protected void doFirst() {
        if (mStopped) {
            // 主动关机
            mActive = F;
            mRunning = F;
            mEnergy = 0;
        } else {
            // 超过阈值能量截断
            mEnergy = Math.min(getEnergySizeInputMax(mEnergyTypeAccepted, SIDE_ANY), mEnergy);
        }
    }
    protected boolean checkActive() {
        return mEnergy >= getEnergySizeInputMin(mEnergyTypeAccepted, SIDE_ANY);
    }
    protected void doActive() {
        mRunning = T;
        // 检测之前先执行一次转移
        if (addStackToSlot(0, mOreBuffer)) mOreBuffer = null;
        // 只有 buffer 为空才能运行
        if (mOreBuffer != null) {mActive = F; return;}
        // 检测下面的方块是否为矿藏
        MultiTileEntityDeposit tDeposit = getDeposit();
        if (tDeposit == null) {mActive = F; return;}
        // 检测挖掘等级
        if (tDeposit.level() > mLevel) {mActive = F; return;}
        mActive = T;
        mOreBuffer = tDeposit.dig(UT.Code.units(mEnergy, 10000, mEfficiency, F));
        // 如果有挖掘到则直接输出
        if (mOreBuffer != null) {
            // 由于 api 限制，这里需要先暂存到机器中
            if (addStackToSlot(0, mOreBuffer)) mOreBuffer = null;
            ST.moveAll(delegator(FACING_TO_SIDE[mFacing][SIDE_RIGHT]), getAdjacentTileEntity(FACING_TO_SIDE[mFacing][SIDE_RIGHT]));
        }
    }
    protected void doInActive() {
        mActive = F;
        mRunning = F;
    }
    protected void doFinal() {
        mEnergy = 0; // 无论怎样最后都要清空能量，因为反正 RU 都会浪费能量
        // 淋雨损坏等检测
        if (mTimer % 600 == 5) {
            if (mActive) doDefaultStructuralChecks();
        }
    }
    protected @Nullable MultiTileEntityDeposit getDeposit() {
        DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(SIDE_BOTTOM);
        if ((tDelegator != null) && (tDelegator.mTileEntity instanceof MultiTileEntityDeposit)) return (MultiTileEntityDeposit)tDelegator.mTileEntity;
        return null;
    }
    
    @Override public boolean onTickCheck(long aTimer) {
        return mActive != oActive || mRunning != oRunning || super.onTickCheck(aTimer);
    }
    @Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        super.onTickResetChecks(aTimer, aIsServerSide);
        oRunning = mRunning;
        oActive  = mActive;
    }
    
    // 能量注入
    @Override public long doInject(TagData aEnergyType, byte aSide, long aSpeed, long aPower, boolean aDoInject) {
        if (!aDoInject || mStopped) return aPower; // 默认消耗能量
        long tMaxEnergy = getEnergySizeInputMax(mEnergyTypeAccepted, SIDE_ANY);
        if (mEnergy >= tMaxEnergy) return aPower;
        
        long tSpeed = Math.abs(aSpeed);
        // 判断是否超载
        if (tSpeed > tMaxEnergy) {
            if (mTimer < 10) return aPower;
            overcharge(aSpeed, mEnergyTypeAccepted);
            return aPower;
        }
        // 获取能量，全部加入并进行超出截断
        mEnergy += tSpeed * aPower;
        if (mEnergy > tMaxEnergy) mEnergy = tMaxEnergy;
        
        return aPower;
    }
    
    public boolean isInput(byte aSide) {return aSide == SIDE_TOP;}
    
    @Override public boolean isEnergyType               (TagData aEnergyType, byte aSide, boolean aEmitting) {return !aEmitting && aEnergyType == mEnergyTypeAccepted;}
    @Override public boolean isEnergyAcceptingFrom      (TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || !mStopped) && (SIDES_INVALID[aSide] || isInput(aSide)) && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
    @Override public long getEnergyDemanded             (TagData aEnergyType, byte aSide, long aSize) {return mRate*2;}
    @Override public long getEnergySizeInputMin         (TagData aEnergyType, byte aSide) {return mRate/2;}
    @Override public long getEnergySizeInputRecommended (TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeInputMax         (TagData aEnergyType, byte aSide) {return mRate*2;}
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeAccepted.AS_LIST;}
    
    @Override public boolean getStateRunningPossible() {
        if (mActive) return T;
        MultiTileEntityDeposit tDeposit = getDeposit();
        return tDeposit!=null && tDeposit.level()<=mLevel;
    }
    @Override public boolean getStateRunningPassively() {return mRunning;}
    @Override public boolean getStateRunningActively() {return mActive;}
    @Override public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    @Override public boolean getStateOnOff() {return !mStopped;}
    
    // Inventory Stuff
    private static final int[] ACCESSABLE_SLOTS = new int[] {0};
    @Override public int[] getAccessibleSlotsFromSide2(byte aSide) {return SIDES_HORIZONTAL[aSide] ? ACCESSABLE_SLOTS : ZL_INTEGER;}
    @Override public boolean canInsertItem2(int aSlot, ItemStack aStack, byte aSide) {return F;}
    @Override public boolean canExtractItem2(int aSlot, ItemStack aStack, byte aSide) {return aStack!= null && aSlot==0 && SIDES_HORIZONTAL[aSide];}
    @Override public boolean canDrop(int aInventorySlot) {return T;}
    @Override public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return new ItemStack[1];}
    // 破坏时输出 buffer
    @Override public boolean breakBlock() {
        if (isServerSide()) {
            if (mOreBuffer != null) {
                ST.drop(worldObj, getCoords(), mOreBuffer);
                mOreBuffer = null;
            }
        }
        return super.breakBlock();
    }
    
    @Override public byte getVisualData() {return (byte)((mActive?1:0)|(mRunning?2:0));}
    @Override public void setVisualData(byte aData) {mRunning=((aData&2)!=0); mActive=((aData&1)!=0);}
    @Override public byte getDefaultSide() {return SIDE_FRONT;}
    @Override public boolean[] getValidSides() {return mActive ? SIDES_THIS[mFacing] : SIDES_HORIZONTAL;}
    
    @Override public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        int aIndex = SIDES_TOP[aSide]?2:SIDES_BOTTOM[aSide]?1:0;
        return BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[aIndex], mRGBa), BlockTextureDefault.get(sOverlaysBase[FACING_ROTATIONS[mFacing][aSide]]), BlockTextureDefault.get((mActive||worldObj==null?sOverlaysActive:mRunning?sOverlaysRunning:sOverlays)[aIndex]));
    }
    
    // Icons
    public static IIconContainer[] sColoreds = new IIconContainer[] {
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/colored/side"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/colored/bottom"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/colored/top"),
    }, sOverlays = new IIconContainer[] {
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay/side"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay/bottom"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay/top"),
    }, sOverlaysActive = new IIconContainer[] {
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_active/side"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_active/bottom"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_active/top"),
    }, sOverlaysRunning = new IIconContainer[] {
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_running/side"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_running/bottom"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_running/top"),
    }, sOverlaysBase = new IIconContainer[] {
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_base/bottom"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_base/top"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_base/left"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_base/front"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_base/right"),
        new Textures.BlockIcons.CustomIcon("machines/depositdrill/overlay_base/back")
    };
    
    @Override public String getTileEntityName() {return "gt.multitileentity.deposit.drill";}
}
