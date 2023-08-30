package gregtech.tileentity.energy.transformers;

import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

public class MultiTileEntityTransformerRotation extends TileEntityBase09FacingSingle implements ITileEntityNameCompat, ITileEntityAdjacentOnOff, ITileEntityEnergy, ITileEntityRunningActively {
    protected long mRate = 16, mMultiplier = 4;
    
    public long mPower = 0, mSpeed = 0, mEnergy = 0;
    public boolean mStopped = F;
    public boolean mReversed = F, oReversed = F;
    public boolean mCounterClockwise = F, oCounterClockwise = F;
    
    public boolean mActive = F, oActive = F;
    public boolean mEmitsEnergy = F;
    
    protected TagData mEnergyTypeAccepted = TD.Energy.RU;
    protected TagData mEnergyTypeEmitted = TD.Energy.RU;
    
    // NBT读写
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_ENERGY)) mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
        if (aNBT.hasKey(NBT_REVERSED)) mReversed = aNBT.getBoolean(NBT_REVERSED);
        if (aNBT.hasKey(NBT_REVERSED+".rotation")) mCounterClockwise = aNBT.getBoolean(NBT_REVERSED+".rotation");
        
        if (aNBT.hasKey(NBT_OUTPUT)) mRate = aNBT.getLong(NBT_OUTPUT);
        if (aNBT.hasKey(NBT_MULTIPLIER)) mMultiplier = aNBT.getLong(NBT_MULTIPLIER);
        if (aNBT.hasKey(NBT_ENERGY_ACCEPTED)) mEnergyTypeAccepted = TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED));
        if (aNBT.hasKey(NBT_ENERGY_EMITTED)) mEnergyTypeEmitted = TagData.createTagData(aNBT.getString(NBT_ENERGY_EMITTED));
    }
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
        UT.NBT.setBoolean(aNBT, NBT_REVERSED, mReversed);
        UT.NBT.setBoolean(aNBT, NBT_REVERSED+".rotation", mCounterClockwise);
    }
    
    // tooltips
    static {
        LH_CH.add("gtch.tooltip.transformer.rotation.1", "Retain at least 1 Power in Reversed mode");
    }
    @Override
    public final void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        toolTipsEnergy(aList);
        toolTipsUseful(aList);
        toolTipsImportant(aList);
        toolTipsOther(aList, aStack, aF3_H);
    }
    protected void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(10000));
        aList.add(LH.Chat.CYAN 	+ LH_CH.get(LH_CH.TRANSFORMER_NORMAL));
        aList.add(LH.Chat.GREEN	+ LH.get(LH.ENERGY_INPUT ) + ": " + LH.Chat.WHITE 						+ getESIRecN() + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, getESIMinN(), getESIMaxN()) + ", "+getLocalisedInputSide()  +")");
        aList.add(LH.Chat.RED	+ LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE + mMultiplier + "x " 	+ getESORecN() + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, getESOMinN(), getESOMaxN()) + ", "+getLocalisedOutputSide() +")");
        aList.add(LH.Chat.CYAN 	+ LH_CH.get(LH_CH.TRANSFORMER_REVERSED));
        aList.add(LH.Chat.GREEN	+ LH.get(LH.ENERGY_INPUT ) + ": " + LH.Chat.WHITE 						+ getESIRecR() + " " + mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, getESIMinR(), getESIMaxR()) + ", "+getLocalisedOutputSide() +")");
        aList.add(LH.Chat.RED	+ LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE +"1/"+mMultiplier+"x "+ getESORecR() + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, getESOMinR(), getESOMaxR()) + ", "+getLocalisedInputSide()  +")");
    }
    protected void toolTipsUseful(List<String> aList) {
        aList.add(LH.Chat.GREEN + LH_CH.get("gtch.tooltip.transformer.rotation.1"));
    }
    protected void toolTipsImportant(List<String> aList) {/**/}
    protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        super.addToolTips(aList, aStack, aF3_H);
        aList.add(LH.Chat.DGRAY + LH_CH.get(LH_CH.TOOL_TO_SET_MODE_MONKEY_WRENCH));
    }
    
    // 工具右键
    @Override
    public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (rReturn > 0) return rReturn;
        
        if (isClientSide()) return 0;
        if (aTool.equals(TOOL_monkeywrench)) {
            mReversed=!mReversed;
            if (aChatReturn != null) aChatReturn.add(mReversed ? "Reversed" : "Normal");
            causeBlockUpdate();
            doEnetUpdate();
            return 10000;
        }
        if (aTool.equals(TOOL_magnifyingglass)) {
            if (mActive) aChatReturn.add(mCounterClockwise ? "Counterclockwise" : "Clockwise");
            if (aChatReturn != null) aChatReturn.add(mReversed ? "Reversed" : "Normal");
            return 1;
        }
        return 0;
    }
    
    // 每 tick 转换
    @Override
    public final void onTick2(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide) {
            // 活动检测
            if (checkActive()) {
                // 可以输出
                doActive();
            } else {
                doInActive();
            }
            // 释放能量
            emitEnergy();
            // 淋雨损坏等
            explodeCheck();
        }
    }
    protected boolean checkActive() {
        return (mSpeed >= getEnergySizeInputMin(mEnergyTypeAccepted, SIDE_ANY)) && mPower > 0;
    }
    protected void doActive() {
        mActive = T;
    }
    protected void doInActive() {
        mActive = F;
        mEnergy = mPower = mSpeed = 0;
    }
    protected void stop() {
        mActive = F;
        mEnergy = mPower = mSpeed = 0;
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
        mEnergy = mSpeed * mPower;
        if (mReversed) {
            //反向，合并功率
            mPower = UT.Code.divup(mPower, mMultiplier);
            mSpeed = mCounterClockwise? (-mEnergy/mPower) : (mEnergy/mPower);
        } else {
            //正常，拆分功率
            mPower *= mMultiplier;
            mSpeed = mCounterClockwise? (-mSpeed/mMultiplier) : (mSpeed/mMultiplier);
        }
        mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, mSpeed, mPower, this) > 0;
        mPower = mSpeed = 0; //输出后清空能量
    }
    protected void explodeCheck() {
        if (mTimer % 600 == 5) {
            if (mActive) doDefaultStructuralChecks();
        }
    }
    // 能量注入
    @Override
    public long doInject(TagData aEnergyType, byte aSide, long aSpeed, long aPower, boolean aDoInject) {
        if (!aDoInject || mStopped) return aPower; // 默认消耗能量
        
        long tSpeed = Math.abs(aSpeed);
        // 判断是否超载
        if (tSpeed > getEnergySizeInputMax(mEnergyTypeAccepted, SIDE_ANY)) {
            if (mTimer < 10) return aPower;
            overcharge(aSpeed, mEnergyTypeAccepted);
            return aPower;
        }
        // 获取能量
        mCounterClockwise = aSpeed < 0;
        mSpeed = (mSpeed==0) ? tSpeed : Math.min(tSpeed, mSpeed);
        mPower += aPower;
        
        return aPower;
    }
    
    // 一些接口
    protected long getESORecN() {return mRate;}
    protected long getESOMinN() {return mRate/2;}
    protected long getESOMaxN() {return mRate*2;}
    protected long getESIRecN() {return mRate*mMultiplier;}
    protected long getESIMinN() {return mRate*mMultiplier/2;}
    protected long getESIMaxN() {return mRate*mMultiplier*2;}
    protected long getESORecR() {return mRate*mMultiplier;}
    protected long getESOMinR() {return mRate/2;} // 反向可以有更低的输出，和变压器不同
    protected long getESOMaxR() {return mRate*mMultiplier*2;}
    protected long getESIRecR() {return mRate;}
    protected long getESIMinR() {return mRate/2;}
    protected long getESIMaxR() {return mRate*2;}
    
    @Override public boolean allowCovers(byte aSide) {return T;}
    
    public boolean isInput (byte aSide) {return mReversed ? aSide == OPOS[mFacing] : aSide == mFacing;}
    public boolean isOutput(byte aSide) {return mReversed ? aSide == mFacing : aSide == OPOS[mFacing];}
    public String getLocalisedInputSide () {return LH.get(LH.FACE_FRONT);}
    public String getLocalisedOutputSide() {return LH.get(LH.FACE_BACK);}
    
    @Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return aEmitting ? aEnergyType == mEnergyTypeEmitted : aEnergyType == mEnergyTypeAccepted;}
    @Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || (!mStopped)) && (SIDES_INVALID[aSide] || isInput(aSide)) && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
    @Override public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {return (SIDES_INVALID[aSide] || isOutput(aSide)) && super.isEnergyEmittingTo(aEnergyType, aSide, aTheoretical);}
    @Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mReversed?getESORecR():getESORecN();}
    @Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mReversed?getESOMinR():getESOMinN();}
    @Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mReversed?getESOMaxR():getESOMaxN();}
    @Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mReversed?getESIRecR():getESIRecN();}
    @Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return mReversed?getESIMinR():getESIMinN();}
    @Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mReversed?getESIMaxR():getESIMaxN();}
    
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeEmitted.AS_LIST;}
    
    @Override public boolean canDrop(int aInventorySlot) {return F;}
    
    @Override public boolean getStateRunningPossible() {return T;}
    @Override public boolean getStateRunningPassively() {return mActive;}
    @Override public boolean getStateRunningActively() {return mEmitsEnergy;}
    @Override public boolean setAdjacentOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    @Override public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    @Override public boolean getStateOnOff() {return !mStopped;}
    
    // Icons
    public final static IIconContainer
          sColoredFront 		= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored/front")
        , sColoredBack  		= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored/back")
        , sColoredSide  		= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored/side")
        , sColoredFrontActiveR 	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored_active_r/front")
        , sColoredBackActiveR  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored_active_r/back")
        , sColoredSideActiveR  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored_active_r/side")
        , sColoredFrontActiveL 	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored_active_l/front")
        , sColoredBackActiveL  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored_active_l/back")
        , sColoredSideActiveL  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/colored_active_l/side")
        , sOverlayFront 		= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay/front")
        , sOverlayBack  		= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay/back")
        , sOverlaySide  		= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay/side")
        , sOverlayFrontActiveR 	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay_active_r/front")
        , sOverlayBackActiveR  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay_active_r/back")
        , sOverlaySideActiveR  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay_active_r/side")
        , sOverlayFrontActiveL 	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay_active_l/front")
        , sOverlayBackActiveL  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay_active_l/back")
        , sOverlaySideActiveL  	= new Textures.BlockIcons.CustomIcon("machines/transformers/transformer_rotation/overlay_active_l/side")
        ;
    public static IIconContainer[][] sColoreds = new IIconContainer[][] {
        {sColoredFront, sColoredBack, sColoredSide},
        {sColoredFrontActiveR, sColoredBackActiveR, sColoredSideActiveR},
        {sColoredFrontActiveL, sColoredBackActiveL, sColoredSideActiveL},
    }, sOverlays = new IIconContainer[][] {
        {sOverlayFront, sOverlayBack, sOverlaySide},
        {sOverlayFrontActiveR, sOverlayBackActiveR, sOverlaySideActiveR},
        {sOverlayFrontActiveL, sOverlayBackActiveL, sOverlaySideActiveL},
    };
    
    @Override public boolean onTickCheck(long aTimer) {
        return oActive != mActive || mCounterClockwise != oCounterClockwise || mReversed != oReversed || super.onTickCheck(aTimer);
    }
    @Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        super.onTickResetChecks(aTimer, aIsServerSide);
        oActive = mActive;
        oCounterClockwise = mCounterClockwise;
        oReversed = mReversed;
    }
    @Override public void setVisualData(byte aData) {
        mActive     		= ((aData & 1)  != 0);
        mCounterClockwise   = ((aData & 2)  != 0);
        mReversed           = ((aData & 4)  != 0);
    }
    @Override public byte getVisualData() {return (byte)((mActive?1:0) | (mCounterClockwise?2:0) | (mReversed?4:0));}
    
    @Override public byte getDefaultSide() {return SIDE_DOWN;}
    
    // GTCH, 统一计算标记颜色
    protected int mRGBaMark;
    @Override public int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {mRGBaMark = UT_CH.Code.getMarkRGB(mRGBa); return 1;}
    
    @Override public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        int aIndex1, aIndex2;
        aIndex1 = mActive ? (mCounterClockwise? 2 : 1) : 0;
        aIndex2 = (aSide == mFacing)? 0 : ((aSide == OPOS[mFacing])? 1 : 2);
        ITexture tTexture = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[aIndex1][aIndex2], mRGBa), BlockTextureDefault.get(sOverlays[aIndex1][aIndex2], mRGBa));
        if (aSide ==      mFacing ) tTexture = BlockTextureMulti.get(tTexture, BlockTextureDefault.get(mReversed ? Textures.BlockIcons.ARROW_OUT : Textures.BlockIcons.ARROW_IN, mRGBaMark));
        if (aSide == OPOS[mFacing]) tTexture = BlockTextureMulti.get(tTexture, BlockTextureDefault.get(mReversed ? Textures.BlockIcons.ARROW_IN : Textures.BlockIcons.ARROW_OUT, mRGBaMark));
        return tTexture;
    }
    
    @Override public String getTileEntityName() {return "gt.multitileentity.transformers.transformer_rotation";}
    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.transformers.transformer_rotation";}
}

