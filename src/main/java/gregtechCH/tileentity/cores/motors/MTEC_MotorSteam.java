package gregtechCH.tileentity.cores.motors;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

import static gregapi.data.CS.*;

public class MTEC_MotorSteam extends MTEC_Motor {
    public MTEC_MotorSteam(TileEntityBase09FacingSingle aTE) {super(aTE);}
    public MTEC_MotorMainSteam data() {return (MTEC_MotorMainSteam)mD;}
    
    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainSteam(this);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_MotorIconSteam(this);}
    
    /* main code */
    protected boolean mFast = F, oFast = F; // 只有小的蒸汽涡轮才有这个属性
    
    // init of core
    @Override public void init(NBTTagCompound aNBT) {
        super.init(aNBT);
        if (aNBT.hasKey(NBT_VISUAL)) mFast = aNBT.getBoolean(NBT_VISUAL);
    }
    
    // NBT读写
    @Override public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        UT.NBT.setBoolean(aNBT, NBT_VISUAL, mFast);
    }
    public boolean isFast() {return mFast;}
    
    // ticking
    @Override public void onTickDoActive2() {
        super.onTickDoActive2();
        mFast = data().mOutput > data().mRate;
    }
    @Override
    protected void convertToTanks(FluidStack... aFluids) {
        // 小蒸汽涡轮不进行存储，直接向四周输出
        FluidStack tDistilledWater = aFluids[0];
        for (byte tDir : FACING_SIDES[mTE.mFacing]) {
            tDistilledWater.amount -= FL.fill(mTE.getAdjacentTank(tDir), tDistilledWater.copy(), T);
            if (tDistilledWater.amount <= 0) break;
        }
        GarbageGT.trash(tDistilledWater);
    }
    
    // tooltips
    protected String getLocalisedInputSide () {return LH.get(LH.FACE_BACK);}
    protected String getLocalisedOutputSide() {return LH.get(LH.FACE_FRONT);}
    @Override public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.getToolTipEfficiency(data().mEfficiency));
        LH.addEnergyToolTips((ITileEntityEnergy)mTE, aList, data().mEnergyTypeAccepted, data().mEnergyTypeEmitted, getLocalisedInputSide(), getLocalisedOutputSide());
    }
    @Override public void toolTipsUseful(List<String> aList) {
        data().toolTipsUseful_overclock(aList);
        super.toolTipsUseful(aList);
    }
    @Override public void toolTipsImportant(List<String> aList) {
        aList.add(LH.Chat.ORANGE + LH.get(LH.EMITS_USED_STEAM) + " ("+LH.get(LH.FACE_SIDES)+", " + LH_CH.getToolTipEfficiencySimple(data().mEfficiencyWater) + ")");
        super.toolTipsImportant(aList);
    }
    
    // 工具右键
    
    // data sync
    @Override public boolean onTickCheck(long aTimer) {return mFast != oFast || super.onTickCheck(aTimer);}
    @Override public void onTickResetChecks(long aTimer, boolean aIsServerSide) {super.onTickResetChecks(aTimer, aIsServerSide); oFast = mFast;}
    @Override public void setVisualData(byte aData) {super.setVisualData(aData); mFast = ((aData & 16) != 0);}
    @Override public byte getVisualData() {return (byte)(super.getVisualData() | (mFast?16:0));}
    
    @Override protected float entityRotationSpeed() {return mFast?2.0F:1.0F;}
    
    // 一些接口
    @Override public float getSurfaceSizeAttachable (byte aSide) {return ALONG_AXIS[aSide][mTE.mFacing]?0.5F:0.25F;}
    
    @Override public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {return isInput(aSide) ? super.getFluidTankFillable(aSide, aFluidToFill) : null;}
}
