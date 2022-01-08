package gregtechCH.data;

import gregapi.data.LH;

public class LH_CH {
    public static final String getToolTipEfficiencySimple(long aEfficiency) {aEfficiency = Math.abs(aEfficiency); return LH.percent(aEfficiency) + "%";}
}
