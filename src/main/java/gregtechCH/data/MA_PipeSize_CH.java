package gregtechCH.data;

import java.util.HashMap;

import static gregapi.data.CS.PX_P;
import static gregtechCH.data.CS_CH.*;
import static gregapi.data.CS.V;

public class MA_PipeSize_CH extends HashMap<Size, MA_PipeSize_CH.GeneralAttributes> {
    public static class GeneralAttributes {
        public int mID = 0;
        public int mStackSize = 64;
        public float mNbtDiameter = PX_P[ 6];
        public long mNbtPipeSize = 8;
        public long mNbtPipeBandwidth = 1;

        public GeneralAttributes() {}
        public GeneralAttributes(int aID, int aStackSize, float aNbtDiameter, long aNbtPipeSize, long aNbtPipeBandwidth) {
            mID = aID;
            mStackSize = aStackSize;
            mNbtDiameter = aNbtDiameter;
            mNbtPipeSize = aNbtPipeSize; mNbtPipeBandwidth = aNbtPipeBandwidth;
        }
    }
    public MA_PipeSize_CH() {
        put(Size.SMALL,        new MA_PipeSize_CH.GeneralAttributes(0, 64, PX_P[ 6], V[0], 1));
        put(Size.MEDIUM,       new MA_PipeSize_CH.GeneralAttributes(1, 32, PX_P[ 9], V[1], 4));
        put(Size.LARGE,        new MA_PipeSize_CH.GeneralAttributes(2, 16, PX_P[12], V[2], 16));
        put(Size.HUGE,         new MA_PipeSize_CH.GeneralAttributes(3, 8,  PX_P[16], V[3], 64));
    }
}
