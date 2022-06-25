package gregtechCH.tileentity.multiblocks;

import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityUnloadable;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.tileentity.machines.ITileEntitySwitchableOnOff;
import gregapi.tileentity.multiblocks.*;
import gregapi.util.UT;
import gregtechCH.config.ConfigForge_CH.*;
import gregtechCH.data.LH_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart.ENERGY_EMITTER_RU;
import static gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart.FLUID_EMITTER;
import static gregtechCH.data.CS_CH.*;
import static gregtechCH.data.CS_CH.IconType.*;
import static gregtechCH.data.CS_CH.IconType.OVERLAY_ACTIVE_R;

/**
 * @author CHanzy
 * base class of Large Motor, using method similar to the base Motor
 */
public abstract class MultiTileEntityLargeMotor_CH extends TileEntityBase10MultiBlockBase implements ITileEntityEnergy, ITileEntityRunningActively, IMultiBlockEnergy, ITileEntitySwitchableOnOff {
    public short mTurbineWalls = 18022;

    protected short mEfficiency = 10000;
    protected long mEnergy = 0, mRate = 4096, mInRate = 4096;
    protected long mPEnergy = 4096000;
    protected long mPCost = 64, mCRate = 4096;
    protected long mInPCost = 64;
    protected long mOutput = 0;

    protected boolean mStopped = F;
    protected boolean mEmitsEnergy = F;
    protected boolean mActive = F, oActive = F;
    protected boolean mPreheat = F, oPreheat = F;
    protected boolean mCooldown = F, oCooldown = F;;
    protected boolean mCounterClockwise = F, oCounterClockwise = F;
    protected boolean mSelfOut = F, oSelfOut = F;

    protected TagData mEnergyTypeEmitted = TD.Energy.RU;

    protected int mLength = 0, mPLength = 0, mMidLength = 1, mMinLength = 1, mMaxLength = 5;
    protected short[] mEfficiencyArray = new short[]{0};
    protected long[] mRateArray = new long[]{0}, mPEnergyArray = new long[]{0}, mPCostArray = new long[]{0}, mCRateArray = new long[]{0};

    // NBT读写
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_ENERGY)) mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
        if (aNBT.hasKey(NBT_REVERSED)) mCounterClockwise = aNBT.getBoolean(NBT_REVERSED);
        if (aNBT.hasKey(NBT_OUTPUT_SELF)) mSelfOut = aNBT.getBoolean(NBT_OUTPUT_SELF);
        if (aNBT.hasKey(NBT_LENGTH)) mLength = aNBT.getInteger(NBT_LENGTH);
        if (aNBT.hasKey(NBT_LENGTH_PRE)) mPLength = aNBT.getInteger(NBT_LENGTH_PRE);

        if (aNBT.hasKey(NBT_ENERGY_EMITTED)) mEnergyTypeEmitted = TagData.createTagData(aNBT.getString(NBT_ENERGY_EMITTED));

        if (aNBT.hasKey(NBT_LENGTH_MIN)) mMinLength = (int) UT.Code.bind_(1, 128, aNBT.getInteger(NBT_LENGTH_MIN));
        if (aNBT.hasKey(NBT_LENGTH_MAX)) mMaxLength = (int) UT.Code.bind_(1, 128, aNBT.getInteger(NBT_LENGTH_MAX));
        if (aNBT.hasKey(NBT_LENGTH_MID)) mMidLength = (int) UT.Code.bind(mMinLength, mMaxLength, aNBT.getInteger(NBT_LENGTH_MID));
        setStructureParameter(mFacing);

        setEnergyArray(aNBT, mMaxLength - mMinLength + 1);
        setEnergyByLength();

        if (aNBT.hasKey(NBT_DESIGN)) mTurbineWalls = aNBT.getShort(NBT_DESIGN);
    }
    protected void setEnergyArray(NBTTagCompound aNBT, int aArrayLen) {
        mEfficiencyArray = new short[aArrayLen];
        mRateArray = new long[aArrayLen];
        mPEnergyArray = new long[aArrayLen];
        mPCostArray = new long[aArrayLen];
        mCRateArray = new long[aArrayLen];
        for (int i = 0; i < aArrayLen; ++i) {
            if (aNBT.hasKey(NBT_EFFICIENCY+"."+i)) mEfficiencyArray[i] = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY+"."+i));
            if (aNBT.hasKey(NBT_OUTPUT+"."+i)) mRateArray[i] = aNBT.getLong(NBT_OUTPUT+"."+i);
            if (aNBT.hasKey(NBT_PREHEAT_ENERGY+"."+i)) mPEnergyArray[i] = aNBT.getLong(NBT_PREHEAT_ENERGY+"."+i);
            if (aNBT.hasKey(NBT_PREHEAT_COST+"."+i)) mPCostArray[i] = aNBT.getLong(NBT_PREHEAT_COST+"."+i);
            if (aNBT.hasKey(NBT_COOLDOWN_RATE+"."+i)) mCRateArray[i] = aNBT.getLong(NBT_COOLDOWN_RATE+"."+i);
        }
    }
    protected void setEnergyByLength() {
        if (mLength >= mMinLength) setEnergyByLength2(mLength - mMinLength);
    }
    protected void setEnergyByLength2(int aI) {
        setOutRateByLength(aI);
        setInRateByLength(aI);
    }
    protected void setOutRateByLength(int aI) {
        mEfficiency = mEfficiencyArray[aI];
        mRate = mRateArray[aI];
        mPEnergy = mPEnergyArray[aI];
        mPCost = mPCostArray[aI];
        mCRate = mCRateArray[aI];
    }
    protected void setInRateByLength(int aI) {
        mInRate  = UT.Code.units(mRate, mEfficiency, 10000, T);
        mInPCost = UT.Code.units(mPCost, mEfficiency, 10000, T);
    }
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
        UT.NBT.setBoolean(aNBT, NBT_REVERSED, mCounterClockwise);
        UT.NBT.setBoolean(aNBT, NBT_OUTPUT_SELF, mSelfOut);
        UT.NBT.setNumber(aNBT, NBT_LENGTH, mLength);
        UT.NBT.setNumber(aNBT, NBT_LENGTH_PRE, mPLength);

        UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mActive); // for OmniOcular usage
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE_ENERGY, mEmitsEnergy); // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_OUTPUT_NOW, mOutput); // for OmniOcular usage
        UT.NBT.setBoolean(aNBT, NBT_PREHEAT, mPreheat); // for OmniOcular usage
        UT.NBT.setBoolean(aNBT, NBT_COOLDOWN_CH, mCooldown); // for OmniOcular usage

        UT.NBT.setNumber(aNBT, NBT_PREHEAT_ENERGY, mPEnergy);
    }

    // 多方快结构
    protected static final int[] mStartIJK = {-1, -1, 0};
    protected int[] mEndIJK = {mStartIJK[0] + 2, mStartIJK[1] + 2, mStartIJK[2]};
    protected int[] mOrder = new int[3];
    protected boolean[] mDirection = new boolean[3];
    protected void setStructureParameter(byte aFacing) {
        mEndIJK[2] = mStartIJK[2]+mPLength-1;
        setOrder(aFacing);
        setDirection(aFacing);
    }
    protected void setOrder(byte aFacing) {
        switch (aFacing) {
            //为XYZ对应的IJK下标
            case SIDE_X_POS:
            case SIDE_X_NEG:
            {mOrder[0] = 2; mOrder[1] = 0; mOrder[2] = 1; return;}
            case SIDE_Y_POS:
            case SIDE_Y_NEG:
            {mOrder[0] = 1; mOrder[1] = 2; mOrder[2] = 0; return;}
            case SIDE_Z_POS:
            case SIDE_Z_NEG:
            default:
            {mOrder[0] = 0; mOrder[1] = 1; mOrder[2] = 2;}
        }
    }
    protected void setDirection(byte aFacing) {
        switch (aFacing) {
            //为XYZ对应的方向正负
            case SIDE_X_POS:
            {mDirection[0] = F; mDirection[1] = T; mDirection[2] = T; return;}
            case SIDE_Y_POS:
            {mDirection[0] = T; mDirection[1] = F; mDirection[2] = T; return;}
            case SIDE_Z_POS:
            {mDirection[0] = T; mDirection[1] = T; mDirection[2] = F; return;}
            case SIDE_X_NEG:
            case SIDE_Y_NEG:
            case SIDE_Z_NEG:
            default:
            {mDirection[0] = T; mDirection[1] = T; mDirection[2] = T;}
        }
    }
    protected void setCoorByOrder(int[] rXYZ, int[] aIJK) {
        rXYZ[0] = xCoord + (mDirection[0]? aIJK[mOrder[0]] : (-aIJK[mOrder[0]]));
        rXYZ[1] = yCoord + (mDirection[1]? aIJK[mOrder[1]] : (-aIJK[mOrder[1]]));
        rXYZ[2] = zCoord + (mDirection[2]? aIJK[mOrder[2]] : (-aIJK[mOrder[2]]));
    }
    protected int getStructureLength(int[] aStartIJK, int[] aEndIJK) {
        int[] tIJK = new int[3];
        int[] tEndIJK = aEndIJK.clone();
        int[] tXYZ = new int[3];
        int[] tEndXYZ = new int[3];
        for (tIJK[2] = aStartIJK[2]; tIJK[2] < aStartIJK[2] + mMaxLength; ++tIJK[2]) {
            tEndIJK[2] = tIJK[2];
            setCoorByOrder(tEndXYZ, tEndIJK);
            if (worldObj.blockExists(tEndXYZ[0], tEndXYZ[1], tEndXYZ[2])) {
                for (tIJK[0] = aStartIJK[0]; tIJK[0] <= aEndIJK[0]; ++tIJK[0]) for (tIJK[1] = aStartIJK[1]; tIJK[1] <= aEndIJK[1]; ++tIJK[1]) {
                    setCoorByOrder(tXYZ, tIJK);
                    if (!ITileEntityMultiBlockController.Util.checkStructurePart(this, tXYZ[0], tXYZ[1], tXYZ[2], mTurbineWalls, getMultiTileEntityRegistryID())) {
                        return tIJK[2];
                    }
                }
            } else {
                return tIJK[2];
            }
        }
        return mMaxLength;
    }
    public ITileEntityUnloadable mEnergyEmitter = null;
    public ITileEntityEnergy getEnergyEmitter() {
        if (mEnergyEmitter.isDead()) mEnergyEmitter = null;
        return (ITileEntityEnergy) mEnergyEmitter;
    }
    public ITileEntityUnloadable mFluidEmitter = null;
    public MultiTileEntityMultiBlockPart getFluidEmitter() {
        if (mFluidEmitter.isDead()) mFluidEmitter = null;
        return (MultiTileEntityMultiBlockPart) mFluidEmitter;
    }
    public byte getEmittingSide() {return OPOS[mFacing];}

    protected void setStructurePart(int[] aStartIJK, int[] aEndIJK) {
        int[] tOutEnergyIJK = {0, 0, mLength-1};
        boolean tHasFluidOut = F;
        int tMinY = yCoord-(SIDE_Y_NEG==mFacing?0:SIDE_Y_POS==mFacing?(mLength-1):1); //用于检测得到机器底层，目前只想到这个方法
        int tBits;
        int[] tIJK = new int[3];
        int[] tXYZ = new int[3];
        for (tIJK[2] = aStartIJK[2]; tIJK[2] <= aEndIJK[2]; ++tIJK[2]) for (tIJK[0] = aStartIJK[0]; tIJK[0] <= aEndIJK[0]; ++tIJK[0]) for (tIJK[1] = aStartIJK[1]; tIJK[1] <= aEndIJK[1]; ++tIJK[1]) {
            setCoorByOrder(tXYZ, tIJK);
            if (Arrays.equals(tIJK, tOutEnergyIJK)) {
                tBits = MultiTileEntityMultiBlockPart.ONLY_ENERGY_OUT;
                ITileEntityMultiBlockController.Util.setTarget(this, tXYZ[0], tXYZ[1], tXYZ[2], ENERGY_EMITTER_RU, tBits);
                TileEntity tTileEntity = getTileEntity(tXYZ[0], tXYZ[1], tXYZ[2]);
                mEnergyEmitter = (tTileEntity instanceof ITileEntityUnloadable) ? (ITileEntityUnloadable)tTileEntity : this;
            } else
            if (tIJK[2] == mLength-1 && (tIJK[0]==0 || tIJK[1]==0) && tXYZ[1] == tMinY && !tHasFluidOut) { // 自动输出孔，可以没有，最多有一个
                tBits = (tIJK[2] == 0 ? MultiTileEntityMultiBlockPart.ONLY_FLUID : MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT);
                ITileEntityMultiBlockController.Util.setTarget(this, tXYZ[0], tXYZ[1], tXYZ[2], FLUID_EMITTER, tBits);
                TileEntity tTileEntity = getTileEntity(tXYZ[0], tXYZ[1], tXYZ[2]);
                if (tTileEntity instanceof ITileEntityUnloadable) {
                    mFluidEmitter = (ITileEntityUnloadable) tTileEntity;
                    tHasFluidOut = T;
                }
            } else {
                if (tIJK[2] == 0) {
                    tBits = (tXYZ[1] == tMinY ? MultiTileEntityMultiBlockPart.ONLY_FLUID     : MultiTileEntityMultiBlockPart.ONLY_FLUID_IN);
                } else {
                    tBits = (tXYZ[1] == tMinY ? MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT : MultiTileEntityMultiBlockPart.NOTHING);
                }
                ITileEntityMultiBlockController.Util.setTarget(this, tXYZ[0], tXYZ[1], tXYZ[2], 0, tBits);
            }
        }
    }
    protected void resetStructurePart(int[] aStartIJK, int[] aEndIJK) {
        int[] tIJK = new int[3];
        int[] tXYZ = new int[3];
        for (tIJK[2] = aStartIJK[2]; tIJK[2] <= aEndIJK[2]; ++tIJK[2]) for (tIJK[0] = aStartIJK[0]; tIJK[0] <= aEndIJK[0]; ++tIJK[0]) for (tIJK[1] = aStartIJK[1]; tIJK[1] <= aEndIJK[1]; ++tIJK[1]) {
            setCoorByOrder(tXYZ, tIJK);
            ITileEntityMultiBlockController.Util.checkAndResetTarget(this, tXYZ[0], tXYZ[1], tXYZ[2], mTurbineWalls, getMultiTileEntityRegistryID());
        }
    }
    @Override
    public final boolean checkStructure2() {
        setStructureParameter(mFacing); // 每次检测都进行更新以免出现意外的错误
        int[] tXYZ = new int[3];
        int[] tEndXYZ = new int[3];

        mLength = 0;
        mSelfOut = F;
        setCoorByOrder(tXYZ, mStartIJK.clone());
        setCoorByOrder(tEndXYZ, mEndIJK);
        if (worldObj.blockExists(tXYZ[0], tXYZ[1], tXYZ[2]) && (mPLength==0 || (worldObj.blockExists(tEndXYZ[0], tEndXYZ[1], tEndXYZ[2])))) {
            mLength = getStructureLength(mStartIJK, mEndIJK);
            boolean tSuccess;
            if (mLength >= mMinLength) {
                tSuccess = T;
                mSelfOut = (mLength == 1);
                mEndIJK[2] = mStartIJK[2] + mLength - 1;
                setStructurePart(mStartIJK, mEndIJK);
            } else {
                tSuccess = F;
                mLength = 0;
            }
            if (mLength != mPLength) {
                mEndIJK[2] = mStartIJK[2] + mPLength - 1;
                resetStructurePart(new int[]{mStartIJK[0], mStartIJK[1], mStartIJK[2]+mLength}, mEndIJK);
                doLengthChange();
                mPLength = mLength;
                mEndIJK[2] = mStartIJK[2] + mPLength - 1; // 将 mEndIJK 改为原本的值
            }
            return tSuccess;
        }
        return isStructureOkay();
    }

    protected void doLengthChange() {
        if (checkExplodeByLength()) {
            doExplodeByLength();
        }
        stopByLength();
        setEnergyByLength();
    }
    protected boolean checkExplodeByLength() {
        return (DATA_MACHINES.motorExplodeByLength && (UT.Code.units(mEnergy, mPEnergy, 16, F) > 4));
    }
    protected void doExplodeByLength() {
        explode(F);
    }
    @Override
    public void explode(boolean aInstant) {
        explode(2+Math.max(1, Math.sqrt(mEnergy) / 1000.0));
    }
    protected void stopByLength() {stop();}

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return
                aX >= xCoord-(SIDE_X_NEG==mFacing?0:SIDE_X_POS==mFacing?(mLength-1):1) &&
                aY >= yCoord-(SIDE_Y_NEG==mFacing?0:SIDE_Y_POS==mFacing?(mLength-1):1) &&
                aZ >= zCoord-(SIDE_Z_NEG==mFacing?0:SIDE_Z_POS==mFacing?(mLength-1):1) &&
                aX <= xCoord+(SIDE_X_POS==mFacing?0:SIDE_X_NEG==mFacing?(mLength-1):1) &&
                aY <= yCoord+(SIDE_Y_POS==mFacing?0:SIDE_Y_NEG==mFacing?(mLength-1):1) &&
                aZ <= zCoord+(SIDE_Z_POS==mFacing?0:SIDE_Z_NEG==mFacing?(mLength-1):1);
    }
    @Override
    public int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {
        return isStructureOkay() ? 2 : 1;
    }
    @Override
    public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
        if (aRenderPass == 1) switch(mFacing) {
            case SIDE_X_NEG: case SIDE_X_POS: return box(aBlock, -0.001, -0.999, -0.999,  1.001,  1.999,  1.999);
            case SIDE_Y_NEG: case SIDE_Y_POS: return box(aBlock, -0.999, -0.001, -0.999,  1.999,  1.001,  1.999);
            case SIDE_Z_NEG: case SIDE_Z_POS: return box(aBlock, -0.999, -0.999, -0.001,  1.999,  1.999,  1.001);
        }
        return F;
    }

    // tooltips
    @Override
    public final void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        toolTipsMultiblock(aList);
        toolTipsEnergy(aList);
        toolTipsUseful(aList);
        toolTipsImportant(aList);
        toolTipsHazard(aList);
        toolTipsOther(aList, aStack, aF3_H);
    }
    protected void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
    }
    protected void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH_CH.getNumber(LH_CH.ENERGY_LENGTH, mMidLength));
        aList.add(LH.getToolTipEfficiency(mEfficiencyArray[mMidLength-mMinLength]));
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE + mRateArray[mMidLength-mMinLength] + " " + mEnergyTypeEmitted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
    }
    protected void toolTipsUseful(List<String> aList) {
        aList.add(LH.Chat.GREEN + LH_CH.get(LH_CH.TOOLTIP_PREHEAT));
        aList.add(LH.Chat.GREEN + LH_CH.get(LH_CH.TOOLTIP_LENGTH));
    }
    protected void toolTipsImportant(List<String> aList) {}
    protected void toolTipsHazard(List<String> aList) {
        aList.add(LH.Chat.DRED     + LH_CH.get(LH_CH.HAZARD_EXPLOSION_LENGTH));
    }
    protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_SET_DIRECTION_MONKEY_WRENCH));
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
        return 0;
    }
    @Override
    public void onMagnifyingGlass2(List<String> aChatReturn) {
        aChatReturn.add(mCounterClockwise ? "Counterclockwise" : "Clockwise");
        onMagnifyingGlassEnergy(aChatReturn);
    }
    public void onMagnifyingGlassEnergy(List<String> aChatReturn) {
        if (mPreheat) {
            aChatReturn.add("Preheating: " + LH.percent(UT.Code.units(Math.min(mEnergy, mPEnergy), mPEnergy, 10000, F)) + "%");
        }
        if (mActive) {
            aChatReturn.add("Active:");
            aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(mEfficiency) + "%");
            aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + mOutput + " " + mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t");
        }
        if (mEnergy == 0) {
            aChatReturn.add("Length: " + mLength);
            aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(mEfficiency) + "%");
            aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + getEnergySizeOutputRecommended(mEnergyTypeEmitted, SIDE_ANY) + " " + mEnergyTypeEmitted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
        }
    }

    // 每 tick 转换
    @Override
    public final void onTick2(long aTimer, boolean aIsServerSide) {
        super.onTick2(aTimer, aIsServerSide);
        checkStructure(F);
        if (aIsServerSide && isStructureOkay()) {
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
            // 多方快不会淋雨损坏
        }
        if (!isStructureOkay()) {
            // 结构破坏，停止机器
            stop();
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
        energyReduce();
    }
    protected abstract long getOutput();
    protected void energyReduce() {
        mEnergy -= mOutput;
    }
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
            mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, -mOutput, 1, getEnergyEmitter()) > 0;
        } else {
            mEmitsEnergy = ITileEntityEnergy.Util.emitEnergyToNetwork(mEnergyTypeEmitted, mOutput, 1, getEnergyEmitter()) > 0;
        }
    }

    // 一些接口
    @Override
    public boolean breakBlock() {
        if (isServerSide()) {
            setStructureParameter(mFacing);
            mLength = 0;
            if (mLength != mPLength) {
                resetStructurePart(mStartIJK, mEndIJK);
                doLengthChange();
                mPLength = mLength;
            }
        }
        return super.breakBlock();
    }
    @Override
    public void onFacingChange(byte aPreviousFacing) {
        super.onFacingChange(aPreviousFacing);
        setStructureParameter(mFacing);
    }

    @Override
    public boolean allowCovers(byte aSide) {
        return aSide != mFacing;
    }

    public boolean isInput (byte aSide) {return aSide == mFacing;}
    public boolean isOutput(byte aSide) {return aSide == OPOS[mFacing];}

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
    public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
    public boolean getStateOnOff() {return !mStopped;}

    // Icons，图像动画
    public abstract IIconContainer getIIconContainer(IconType aIconType);

    @Override
    public boolean onTickCheck(long aTimer) {
        return oActive != mActive || oPreheat != mPreheat || oCooldown != mCooldown || mCounterClockwise != oCounterClockwise || mSelfOut != oSelfOut || super.onTickCheck(aTimer);
    }
    @Override
    public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        super.onTickResetChecks(aTimer, aIsServerSide);
        oActive = mActive;
        oPreheat = mPreheat;
        oCooldown = mCooldown;
        oCounterClockwise = mCounterClockwise;
        oSelfOut = mSelfOut;
    }
    @Override public void setVisualData(byte aData) {
        mActive     		= ((aData & 1)  != 0);
        mPreheat    		= ((aData & 2)  != 0);
        mCooldown   		= ((aData & 4)  != 0);
        mCounterClockwise   = ((aData & 8)  != 0);
        mSelfOut            = ((aData & 16) != 0);
    }
    @Override public byte getVisualData() {return (byte)((mActive?1:0) | (mPreheat?2:0) | (mCooldown?4:0) | (mCounterClockwise?8:0) | (mSelfOut?16:0));}

    @Override public byte getDefaultSide() {return SIDE_FRONT;}
    @Override public boolean[] getValidSides() {return SIDES_VALID;}

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (aRenderPass == 0) {
            if (!aShouldSideBeRendered[aSide]) return null;
            if (isOutput(aSide) && mSelfOut) return BlockTextureMulti.get(super.getTexture2(aBlock, aRenderPass, aSide, aShouldSideBeRendered), BlockTextureDefault.get(getIIconContainer(OVERLAY_ENERGY_RU)));
            return super.getTexture2(aBlock, aRenderPass, aSide, aShouldSideBeRendered);
        }
        // 超过了原本的方块，所以无论何时都要渲染，并且需要关闭环境光遮蔽来防止一些显示错误
        if (isInput(aSide)) {
            if (mPreheat || mCooldown) {
                if (mCounterClockwise) return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_PREHEAT_L));
                return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_PREHEAT_R));
            }
            if (mActive) {
                if (mCounterClockwise) return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_ACTIVE_L));
                return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_ACTIVE_R));
            }
            return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY));
        }
        return null;
    }
}

