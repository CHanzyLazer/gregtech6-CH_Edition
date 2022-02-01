package gregtechCH.data;

import gregtechCH.lang.LanguageHandler_CH;

public class LH_CH {
    public static final String
              TOOLTIP_PREHEAT                           = "gtch.lang.preheat"
            , TOOLTIP_LENGTH                            = "gtch.lang.motor.length"
            , ENERGY_LENGTH                             = "gtch.lang.energy.motor.length"
            , ENERGY_TO                                 = "gtch.lang.energy.to"
            , ENERGY_UPTO                               = "gtch.lang.energy.upto"
            , FACE_HEAT_TRANS                           = "gtch.lang.face.heattransmitter"
            , FACE_PIPE_HOLE                            = "gtch.lang.face.pipehole"
            , TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK      = "gtch.lang.use.sneak.magnifyingglass.to.detail"
            , HAZARD_EXPLOSION_LENGTH                   = "gtch.lang.hazard.explosion.motor.length"
            ;

    public static final String add(String aKey, String aEnglish) {LanguageHandler_CH.add(aKey, aEnglish); return aKey;}
    public static final String get(String aKey) {return LanguageHandler_CH.translate(aKey);}
    public static final String get(String aKey, String aDefault) {return LanguageHandler_CH.translate(aKey, aDefault);}

    public static final String percentSimple(long aNumber) {return String.valueOf(aNumber/100);}
    public static final String getToolTipEfficiencySimple(long aEfficiency) {aEfficiency = Math.abs(aEfficiency); return percentSimple(aEfficiency) + "%";}

    public static void init() {
        add(TOOLTIP_PREHEAT,                            "This machine needs preheating");
        add(TOOLTIP_LENGTH,                             "Improve power and efficiency by increasing length");
        add(ENERGY_LENGTH,                              "At length");
        add(ENERGY_TO,                                  "to");
        add(ENERGY_UPTO,                                "up to");
        add(FACE_HEAT_TRANS,                            "Heat Transmitters");
        add(FACE_PIPE_HOLE,                             "Pipe Holes");
        add(TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK,       "Use Magnifying Glass in sneaking to see more Details");
        add(HAZARD_EXPLOSION_LENGTH,                    "Explodes when Change Structure while machine is running!");
    }
}
