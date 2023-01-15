package gregtechCH.tileentity.cores.motors;

import gregapi.tileentity.ITileEntityUnloadable;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.UT;
import gregtechCH.config.ConfigForge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Arrays;

import static gregapi.data.CS.*;
import static gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart.ENERGY_EMITTER_RU;
import static gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart.FLUID_EMITTER;
import static gregtechCH.data.CS_CH.*;

/**
 * @author CHanzy
 * 对大涡轮的主要逻辑（多方快处理）进行分离
 */
public class MTEC_MultiBlockMotorBase {
    // reference of MTEC_LargeMotor for some call use
    protected final MTEC_LargeMotor mCore;
    protected MTEC_MultiBlockMotorBase(MTEC_LargeMotor aCore) {mCore = aCore;}
    
    /* main code */
    protected short mTurbineWalls = 18022;
    
    protected boolean mSelfOut = F, oSelfOut = F;
    
    protected int mLength = 0, mPLength = 0, mMidLength = 1, mMinLength = 1, mMaxLength = 5;
    protected short[] mEfficiencyArray = ZL_SHORT;
    protected long[] mRateArray = ZL_LONG, mPEnergyArray = ZL_LONG, mPCostArray = ZL_LONG, mCRateArray = ZL_LONG;
    
    // NBT读写
    public void writeToNBT(NBTTagCompound aNBT) {
        UT.NBT.setBoolean(aNBT, NBT_OUTPUT_SELF, mSelfOut);
        UT.NBT.setNumber(aNBT, NBT_LENGTH, mLength);
        UT.NBT.setNumber(aNBT, NBT_LENGTH_PRE, mPLength);
    }
    
    // init of core
    public void init(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_OUTPUT_SELF)) mSelfOut = aNBT.getBoolean(NBT_OUTPUT_SELF);
        if (aNBT.hasKey(NBT_LENGTH)) mLength = aNBT.getInteger(NBT_LENGTH);
        if (aNBT.hasKey(NBT_LENGTH_PRE)) mPLength = aNBT.getInteger(NBT_LENGTH_PRE);

        if (aNBT.hasKey(NBT_LENGTH_MIN)) mMinLength = (int) UT.Code.bind_(1, 128, aNBT.getInteger(NBT_LENGTH_MIN));
        if (aNBT.hasKey(NBT_LENGTH_MAX)) mMaxLength = (int) UT.Code.bind_(1, 128, aNBT.getInteger(NBT_LENGTH_MAX));

        if (aNBT.hasKey(NBT_DESIGN)) mTurbineWalls = aNBT.getShort(NBT_DESIGN);
    }
    public void postInitNBT(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_LENGTH_MID)) mMidLength = (int) UT.Code.bind(mMinLength, mMaxLength, aNBT.getInteger(NBT_LENGTH_MID));

        setStructureParameter(mCore.mTE.mFacing);
        setEnergyArray(aNBT, mMaxLength - mMinLength + 1);
    }
    public final void postInitRate(NBTTagCompound aNBT) {setOutRateFromLength(); setInRateFromLength();}
    
    protected void setEnergyArray(NBTTagCompound aNBT, int aArrayLen) {
        mEfficiencyArray = new short[aArrayLen];
        mRateArray = new long[aArrayLen];
        mPEnergyArray = new long[aArrayLen];
        mPCostArray = new long[aArrayLen];
        mCRateArray = new long[aArrayLen];
        for (int i = 0; i < aArrayLen; ++i) {
            mEfficiencyArray[i] = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY+"."+i));
            mRateArray[i] = aNBT.getLong(NBT_OUTPUT+"."+i);
            mPEnergyArray[i] = aNBT.getLong(NBT_PREHEAT_ENERGY+"."+i);
            mPCostArray[i] = aNBT.getLong(NBT_PREHEAT_COST+"."+i);
            mCRateArray[i] = aNBT.getLong(NBT_COOLDOWN_RATE+"."+i);
        }
    }
    protected final void setOutRateFromLength() {int tIdx = mLength - mMinLength; if (tIdx >= 0) setOutRateFromIdx(tIdx);}
    protected void setOutRateFromIdx(int aIdx) {
        mCore.mD.mEfficiency = mEfficiencyArray[aIdx];
        mCore.mD.mRate = mRateArray[aIdx];
        mCore.mD.mPEnergy = mPEnergyArray[aIdx];
        mCore.mD.mPCost = mPCostArray[aIdx];
        mCore.mD.mCRate = mCRateArray[aIdx];
    }
    protected final void setInRateFromLength() {int tIdx = mLength - mMinLength; if (tIdx >= 0) setInRateFromIdx(tIdx);}
    protected void setInRateFromIdx(int aIdx) {mCore.mD.setInRate2();} // 直接使用 Motor 的设置即可
    
    // 多方快结构
    protected static final int[] START_IJK = {-1, -1, 0};
    protected int[] mEndIJK = {START_IJK[0] + 2, START_IJK[1] + 2, START_IJK[2]};
    protected int[] mOrder = new int[3]; // mOrder[{index of xyz}] = {index of ijk}
    protected boolean[] mDirection = new boolean[3]; // mDirection[{index of xyz}] = {direction of xyz is positive}
    protected void setStructureParameter(byte aFacing) {
        mEndIJK[2] = START_IJK[2]+mPLength-1;
        setOrder(aFacing);
        setDirection(aFacing);
    }
    protected void setOrder(byte aFacing) {
        switch (aFacing) {
            //为XYZ对应的IJK下标
            case SIDE_X_POS: case SIDE_X_NEG:
            {mOrder[0] = 2; mOrder[1] = 0; mOrder[2] = 1; return;}
            case SIDE_Y_POS: case SIDE_Y_NEG:
            {mOrder[0] = 1; mOrder[1] = 2; mOrder[2] = 0; return;}
            case SIDE_Z_POS: case SIDE_Z_NEG: default:
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
            case SIDE_X_NEG: case SIDE_Y_NEG: case SIDE_Z_NEG: default:
            {mDirection[0] = T; mDirection[1] = T; mDirection[2] = T;}
        }
    }
    protected void setCoorByOrder(int[] rXYZ, int[] aIJK) {
        rXYZ[0] = mCore.mTE.xCoord + (mDirection[0]? aIJK[mOrder[0]] : (-aIJK[mOrder[0]]));
        rXYZ[1] = mCore.mTE.yCoord + (mDirection[1]? aIJK[mOrder[1]] : (-aIJK[mOrder[1]]));
        rXYZ[2] = mCore.mTE.zCoord + (mDirection[2]? aIJK[mOrder[2]] : (-aIJK[mOrder[2]]));
    }
    protected int getStructureLength(int[] aStartIJK, int[] aEndIJK) {
        int[] tIJK = new int[3];
        int[] tEndIJK = aEndIJK.clone();
        int[] tXYZ = new int[3];
        int[] tEndXYZ = new int[3];
        for (tIJK[2] = aStartIJK[2]; tIJK[2] < aStartIJK[2] + mMaxLength; ++tIJK[2]) {
            tEndIJK[2] = tIJK[2];
            setCoorByOrder(tEndXYZ, tEndIJK);
            if (mCore.mTE.getWorldObj().blockExists(tEndXYZ[0], tEndXYZ[1], tEndXYZ[2])) {
                for (tIJK[0] = aStartIJK[0]; tIJK[0] <= aEndIJK[0]; ++tIJK[0]) for (tIJK[1] = aStartIJK[1]; tIJK[1] <= aEndIJK[1]; ++tIJK[1]) {
                    setCoorByOrder(tXYZ, tIJK);
                    if (!ITileEntityMultiBlockController.Util.checkStructurePart((ITileEntityMultiBlockController)mCore.mTE, tXYZ[0], tXYZ[1], tXYZ[2], mTurbineWalls, mCore.mTE.getMultiTileEntityRegistryID())) {
                        return tIJK[2];
                    }
                }
            } else {
                return tIJK[2];
            }
        }
        return mMaxLength;
    }
    protected ITileEntityUnloadable mEnergyEmitter = null;
    protected ITileEntityEnergy getEnergyEmitter() {
        if (mEnergyEmitter.isDead()) mEnergyEmitter = null;
        return (ITileEntityEnergy) mEnergyEmitter;
    }
    protected ITileEntityUnloadable mFluidEmitter = null;
    protected TileEntityBase01Root getFluidEmitter() {
        if (mFluidEmitter.isDead()) mFluidEmitter = null;
        return (TileEntityBase01Root) mFluidEmitter;
    }
    
    protected void setStructurePart(int[] aStartIJK, int[] aEndIJK) {
        int[] tOutEnergyIJK = {0, 0, mLength-1};
        boolean tHasFluidOut = F;
        int tMinY = mCore.mTE.yCoord-(SIDE_Y_NEG==mCore.mTE.mFacing?0:SIDE_Y_POS==mCore.mTE.mFacing?(mLength-1):1); //用于检测得到机器底层，目前只想到这个方法
        int tBits;
        int[] tIJK = new int[3];
        int[] tXYZ = new int[3];
        for (tIJK[2] = aStartIJK[2]; tIJK[2] <= aEndIJK[2]; ++tIJK[2]) for (tIJK[0] = aStartIJK[0]; tIJK[0] <= aEndIJK[0]; ++tIJK[0]) for (tIJK[1] = aStartIJK[1]; tIJK[1] <= aEndIJK[1]; ++tIJK[1]) {
            setCoorByOrder(tXYZ, tIJK);
            if (Arrays.equals(tIJK, tOutEnergyIJK)) {
                tBits = MultiTileEntityMultiBlockPart.ONLY_ENERGY_OUT;
                ITileEntityMultiBlockController.Util.setTarget(mCore.te(), tXYZ[0], tXYZ[1], tXYZ[2], ENERGY_EMITTER_RU, tBits);
                TileEntity tTileEntity = mCore.mTE.getTileEntity(tXYZ[0], tXYZ[1], tXYZ[2]);
                mEnergyEmitter = (tTileEntity instanceof ITileEntityUnloadable) ? (ITileEntityUnloadable)tTileEntity : mCore.mTE;
            } else
            if (tIJK[2] == mLength-1 && (tIJK[0]==0 || tIJK[1]==0) && tXYZ[1] == tMinY && !tHasFluidOut) { // 自动输出孔，可以没有，最多有一个
                tBits = (tIJK[2] == 0 ? MultiTileEntityMultiBlockPart.ONLY_FLUID : MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT);
                ITileEntityMultiBlockController.Util.setTarget(mCore.te(), tXYZ[0], tXYZ[1], tXYZ[2], FLUID_EMITTER, tBits);
                TileEntity tTileEntity = mCore.mTE.getTileEntity(tXYZ[0], tXYZ[1], tXYZ[2]);
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
                ITileEntityMultiBlockController.Util.setTarget(mCore.te(), tXYZ[0], tXYZ[1], tXYZ[2], 0, tBits);
            }
        }
    }
    protected void resetStructurePart(int[] aStartIJK, int[] aEndIJK) {
        int[] tIJK = new int[3];
        int[] tXYZ = new int[3];
        for (tIJK[2] = aStartIJK[2]; tIJK[2] <= aEndIJK[2]; ++tIJK[2]) for (tIJK[0] = aStartIJK[0]; tIJK[0] <= aEndIJK[0]; ++tIJK[0]) for (tIJK[1] = aStartIJK[1]; tIJK[1] <= aEndIJK[1]; ++tIJK[1]) {
            setCoorByOrder(tXYZ, tIJK);
            ITileEntityMultiBlockController.Util.checkAndResetTarget(mCore.te(), tXYZ[0], tXYZ[1], tXYZ[2], mTurbineWalls, mCore.mTE.getMultiTileEntityRegistryID());
        }
    }
    
    public final boolean checkStructure2() {
        setStructureParameter(mCore.mTE.mFacing); // 每次检测都进行更新以免出现意外的错误
        int[] tXYZ = new int[3];
        int[] tEndXYZ = new int[3];

        mLength = 0;
        mSelfOut = F;
        setCoorByOrder(tXYZ, START_IJK.clone());
        setCoorByOrder(tEndXYZ, mEndIJK);
        if (mCore.mTE.getWorldObj().blockExists(tXYZ[0], tXYZ[1], tXYZ[2]) && (mPLength==0 || (mCore.mTE.getWorldObj().blockExists(tEndXYZ[0], tEndXYZ[1], tEndXYZ[2])))) {
            mLength = getStructureLength(START_IJK, mEndIJK);
            // 如果在运行，则不会增加长度
            if (mCore.mD.mEnergy > 0) mLength = Math.min(mLength, mPLength);
            boolean tSuccess;
            if (mLength >= mMinLength) {
                tSuccess = T;
                mSelfOut = (mLength == 1);
                mEndIJK[2] = START_IJK[2] + mLength - 1;
                setStructurePart(START_IJK, mEndIJK);
            } else {
                tSuccess = F;
                mLength = 0;
            }
            if (mLength != mPLength) {
                mEndIJK[2] = START_IJK[2] + mPLength - 1;
                resetStructurePart(new int[]{START_IJK[0], START_IJK[1], START_IJK[2]+mLength}, mEndIJK);
                doLengthChange();
                mPLength = mLength;
                mEndIJK[2] = START_IJK[2] + mPLength - 1; // 将 mEndIJK 改为原本的值
            }
            return tSuccess;
        }
        return mCore.te().isStructureOkay();
    }
    protected void doLengthChange() {
        if (ConfigForge.DATA_MACHINES.motorExplodeByLength && mCore.canExplode()) {
            mCore.explode(F);
        }
        mCore.mD.stop();
        setOutRateFromLength();
        setInRateFromLength();
        mCore.postInitTank(); // 需要重置容器的容量
    }
    
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return
        aX >= mCore.mTE.xCoord-(SIDE_X_NEG==mCore.mTE.mFacing?0:SIDE_X_POS==mCore.mTE.mFacing?(mLength-1):1) &&
        aY >= mCore.mTE.yCoord-(SIDE_Y_NEG==mCore.mTE.mFacing?0:SIDE_Y_POS==mCore.mTE.mFacing?(mLength-1):1) &&
        aZ >= mCore.mTE.zCoord-(SIDE_Z_NEG==mCore.mTE.mFacing?0:SIDE_Z_POS==mCore.mTE.mFacing?(mLength-1):1) &&
        aX <= mCore.mTE.xCoord+(SIDE_X_POS==mCore.mTE.mFacing?0:SIDE_X_NEG==mCore.mTE.mFacing?(mLength-1):1) &&
        aY <= mCore.mTE.yCoord+(SIDE_Y_POS==mCore.mTE.mFacing?0:SIDE_Y_NEG==mCore.mTE.mFacing?(mLength-1):1) &&
        aZ <= mCore.mTE.zCoord+(SIDE_Z_POS==mCore.mTE.mFacing?0:SIDE_Z_NEG==mCore.mTE.mFacing?(mLength-1):1);
    }
    
    public void onBreakBlock() {
        if (mCore.mTE.isServerSide()) {
            setStructureParameter(mCore.mTE.mFacing);
            mLength = 0;
            if (mLength != mPLength) {
                resetStructurePart(START_IJK, mEndIJK);
                doLengthChange();
                mPLength = mLength;
            }
        }
    }
    public void onFacingChange(byte aPreviousFacing) {
        setStructureParameter(mCore.mTE.mFacing);
    }
    
    protected void explode(boolean aInstant) {mCore.mTE.explode(aInstant, 2+Math.max(1, Math.sqrt(mCore.mD.mEnergy) / 1000.0));}
}
