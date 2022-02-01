package gregtechCH.util;

import gregapi.util.UT;

public class UT_CH {
    public static class Code {
        public static long effNormalize(int aEff) {
            int tEff40 = (aEff / 250) * 10000 / 40;
            int tEff30 = (aEff / 333) * 10000 / 30;
            return UT.Code.bind_(0, 10000, (aEff - tEff40 < aEff - tEff30) ? tEff40 : tEff30);
        }

        public static long effNormalizeRound(int aEff) {
            int[] tEffArray = {
                    (aEff / 250) * 10000 / 40,
                    (aEff / 333) * 10000 / 30,
                    (int) UT.Code.divup(aEff, 250) * 10000 / 40,
                    (int) UT.Code.divup(aEff, 333) * 10000 / 30};
            int tMinEff = tEffArray[0];
            for (int i = 1; i < 4; ++i) {
                if (Math.abs(tEffArray[i] - aEff) < Math.abs(tMinEff - aEff)) tMinEff = tEffArray[i];
            }
            return UT.Code.bind_(0, 10000, tMinEff);
        }
    }

}
