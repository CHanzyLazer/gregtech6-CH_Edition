package gregtechCH.config.machine.multiblock;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import gregtechCH.config.machine.AttributesMachine_CH;
import gregtechCH.config.machine.MaterialDeserializer_CH;
import gregtechCH.config.machine.MaterialSerializer_CH;
import gregtechCH.util.UT_CH;

import static gregapi.data.CS.*;

public abstract class AttributesLargeMotor_CH extends AttributesMachine_CH {
    @JSONField(serializeUsing = MaterialSerializer_CH.class, deserializeUsing = MaterialDeserializer_CH.class)
    public OreDictMaterial rotorMaterial;

    public int nbtLengthMin;
    public int nbtLengthMax;
    public int nbtLengthMid;


    public int[] nbtEfficiency;
    public long[] nbtOutput;
    public long[] nbtPreheatEnergy;
    public long[] nbtPreheatCost;
    public long[] nbtCooldownRate;

    //使用此算法使得结果在 aX == 0 时为 aMinMul/aMinDiv 效率，aX == aLm 时为 aMaxMul/aMaxDiv 效率，aX == aL 时为 1 效率，
    public static long getLengthCurveOriginal(long aMinMul, long aMinDiv, long aMaxMul, long aMaxDiv, long aL, long aLm, long aX) {
        return (aL*aLm*aMaxDiv*aMinDiv - aL*aLm*aMaxMul*aMinDiv - aL*aMaxDiv*aMinMul*aX + aL*aMaxMul*aMinDiv*aX - aLm*aMaxDiv*aMinDiv*aX + aLm*aMaxDiv*aMinMul*aX);
    }
    public static long getLengthCurveTarget(long aMinMul, long aMinDiv, long aMaxMul, long aMaxDiv, long aL, long aLm, long aX) {
        return (aL*aLm*aMaxDiv*aMinMul - aL*aLm*aMaxMul*aMinMul - aL*aMaxDiv*aMinMul*aX + aL*aMaxMul*aMinDiv*aX - aLm*aMaxMul*aMinDiv*aX + aLm*aMaxMul*aMinMul*aX);
    }

    protected void setEnergy(int aLengthMin, int aLengthMax, int aLengthMid, long aOutputMid, long aEfficiencyMid) {
        nbtLengthMin = aLengthMin;
        nbtLengthMax = aLengthMax;
        nbtLengthMid = aLengthMid;
        int tLm = aLengthMax - aLengthMin + 1;
        int tL = aLengthMid - aLengthMin + 1;
        nbtEfficiency = new int[tLm];
        nbtOutput = new long[tLm];
        nbtPreheatEnergy = new long[tLm];
        nbtPreheatCost = new long[tLm];
        nbtCooldownRate = new long[tLm];

        for (int i = 0; i < tLm; ++i) {
            nbtOutput[i] = UT.Code.units(aOutputMid, getLengthCurveOriginal(1, 3, 3, 2, tL-1, tLm-1, i), getLengthCurveTarget(1, 3, 3, 2, tL-1, tLm-1, i), F);
            nbtEfficiency[i] = (int) UT.Code.units(aEfficiencyMid, getLengthCurveOriginal(1, 2, 6, 5, tL-1, tLm-1, i), getLengthCurveTarget(1, 2, 6, 5, tL-1, tLm-1, i), F);
            nbtEfficiency[i] = (int) UT_CH.Code.effNormalizeRound(nbtEfficiency[i]);
            nbtPreheatEnergy[i] = UT.Code.divup(aOutputMid * 8000 * (i + 1), tL);
            nbtPreheatCost[i] = UT.Code.divup(aOutputMid * (i + 1), 128L * tL);
            nbtCooldownRate[i] = UT.Code.divup(aOutputMid * (i + 1), tL);
        }
    }

}
