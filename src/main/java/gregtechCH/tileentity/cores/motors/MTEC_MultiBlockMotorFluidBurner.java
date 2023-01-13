package gregtechCH.tileentity.cores.motors;

import gregtechCH.util.UT_CH;
import net.minecraft.nbt.NBTTagCompound;

import static gregapi.data.CS.ZL_LONG;
import static gregtechCH.data.CS_CH.NBT_PREHEAT_RATE;

/**
 * @author CHanzy
 */
public class MTEC_MultiBlockMotorFluidBurner extends MTEC_MultiBlockMotorBase {
    protected MTEC_MultiBlockMotorFluidBurner(MTEC_LargeMotor aCore) {super(aCore);}
    public MTEC_MotorMainFluidBurner data() {return (MTEC_MotorMainFluidBurner)mCore.mD;}

    /* main code */
    // 大燃气涡轮多出一个预存的预热数组
    protected long[] mPRateArray = ZL_LONG;

    @Override public void init(NBTTagCompound aNBT) {UT_CH.Debug.assertWhenDebug(mCore.mD instanceof MTEC_MotorMainFluidBurner); super.init(aNBT);}
    @Override protected void setOutRateFromIdx(int aIdx) {super.setOutRateFromIdx(aIdx); data().mPRate = mPRateArray[aIdx];}
    @Override protected void setEnergyArray(NBTTagCompound aNBT, int aArrayLen) {
        super.setEnergyArray(aNBT, aArrayLen);
        mPRateArray = new long[aArrayLen];
        for (int i = 0; i < aArrayLen; ++i) {
            mPRateArray[i] = aNBT.getLong(NBT_PREHEAT_RATE+"."+i);
        }
    }
}
