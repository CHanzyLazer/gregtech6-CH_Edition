package gregtechCH.data;

import gregapi.data.LH;

public class LH_CH {
    public static final String
            TOOLTIP_PREHEAT = "gtch.lang.preheat" // WIP
            ;

    public static final String percentSimple(long aNumber) {return String.valueOf(aNumber/100);}
    public static final String getToolTipEfficiencySimple(long aEfficiency) {aEfficiency = Math.abs(aEfficiency); return percentSimple(aEfficiency) + "%";}
}
