package gregtechCH.tileentity.cores.motors;

import gregapi.data.LH;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregtechCH.data.LH_CH;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import static gregapi.data.CS.*;


public abstract class MTEC_LargeMotor extends MTEC_Motor {
    protected MTEC_MultiBlockMotorBase mDL; // Data Large Motor
    protected MTEC_LargeMotor(TileEntityBase10MultiBlockBase aTE) {super(aTE);}
    protected TileEntityBase10MultiBlockBase te() {return (TileEntityBase10MultiBlockBase)mTE;}

    /* stuff to override */
    protected MTEC_MultiBlockMotorBase getNewCoreMultiBlock() {return new MTEC_MultiBlockMotorBase(this);}
    @Override protected ITileEntityEnergy getEnergyEmitter() {return mDL.getEnergyEmitter();}
    @Override protected TileEntityBase01Root getFluidEmitter() {return mDL.getFluidEmitter();}

    /* main code */

    // init of core
    @Override public void preInit() {super.preInit(); mDL = getNewCoreMultiBlock();}
    @Override public void init(NBTTagCompound aNBT) {super.init(aNBT); mDL.init(aNBT);}
    @Override protected void postInitNBT(NBTTagCompound aNBT) {super.postInitNBT(aNBT); mDL.postInitNBT(aNBT);}
    @Override protected void postInitRate(NBTTagCompound aNBT) {mDL.postInitRate(aNBT);} // 直接覆盖掉父类的 rate 设定

    // NBT读写
    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        mDL.writeToNBT(aNBT);
    }

    // 每 tick 转换
    @Override public void onTickExplodeCheck(long aTimer) {} // 多方快不会淋雨损坏
    public void stop() {mD.stop();} // 开放这个接口用于外部调用

    // 多方快结构
    public final boolean checkStructure2() {return mDL.checkStructure2();}
    public boolean isInsideStructure(int aX, int aY, int aZ) {return mDL.isInsideStructure(aX, aY, aZ);}

    // tooltips
    @Override public final void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        toolTipsMultiblock2(aList);
    }
    protected void toolTipsMultiblock2(List<String> aList) {/**/}
    @Override public final void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH_CH.getNumber(LH_CH.ENERGY_LENGTH, mDL.mMidLength));
        aList.add(LH.getToolTipEfficiency(mDL.mEfficiencyArray[mDL.mMidLength-mDL.mMinLength]));
        toolTipsEnergy2(aList);
    }
    protected void toolTipsEnergy2(List<String> aList) {/**/}
    @Override public void toolTipsUseful(List<String> aList)  {
        super.toolTipsUseful(aList);
        aList.add(LH.Chat.GREEN + LH_CH.get(LH_CH.TOOLTIP_LENGTH));
    }
    @Override public void toolTipsHazard(List<String> aList) {
        aList.add(LH.Chat.DRED     + LH_CH.get(LH_CH.HAZARD_EXPLOSION_LENGTH));
    }
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_SET_DIRECTION_MONKEY_WRENCH));
    }

    // 工具右键
    @Override protected boolean toolCheckMagnifyingGlass(String aTool) {return F;} // 大型机器已经在父类的实体中检测了因此不需要在这里检测放大镜
    @Override public final void onMagnifyingGlass2(List<String> aChatReturn) {
        super.onMagnifyingGlass2(aChatReturn);
        if (mD.mEnergy == 0) {
            aChatReturn.add("Length: " + mDL.mLength);
            aChatReturn.add(LH.get(LH.EFFICIENCY) + ": " + LH.percent(mD.mEfficiency) + "%");
            onMagnifyingGlass3(aChatReturn);
        }
    }
    protected void onMagnifyingGlass3(List<String> aChatReturn) {/**/}

    // data sync
    @Override public boolean onTickCheck(long aTimer) {return super.onTickCheck(aTimer) || mDL.mSelfOut != mDL.oSelfOut;}
    @Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {super.onTickResetChecks(aTimer, aIsServerSide); mDL.oSelfOut = mDL.mSelfOut;}
    @Override public void setVisualData(byte aData) {super.setVisualData(aData); mDL.mSelfOut = ((aData & 16) != 0);}
    @Override public byte getVisualData() {return (byte)(super.getVisualData() | (mDL.mSelfOut?16:0));}

    // 一些接口
    public void explode(boolean aInstant) {mDL.explode(aInstant);}
    @Override public void onBreakBlock() {mDL.onBreakBlock();}
    @Override public void onFacingChange(byte aPreviousFacing) {mDL.onFacingChange(aPreviousFacing);}

    @Override public boolean allowCovers(byte aSide) {return aSide != mTE.mFacing;}
    @Override protected boolean isInput (byte aSide) {return aSide == mTE.mFacing;}
    @Override protected boolean isOutput(byte aSide) {return aSide == OPOS[mTE.mFacing];}
}
