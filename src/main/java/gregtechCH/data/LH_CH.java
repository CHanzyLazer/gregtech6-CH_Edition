package gregtechCH.data;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.LH;
import gregapi.lang.LanguageHandler;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregtechCH.lang.LanguageHandler_CH;

import static gregtechCH.data.CS_CH.RegType;

/**
 * @author CHanzy
 * Extension of LH
 * MUST use LH_CH.add() to add custom lang
 */
public class LH_CH {
    public static final String
      AXLE_STATS_SPEED                          = "gtch.lang.axle.stats.speed"
    , AXLE_STATS_POWER                          = "gtch.lang.axle.stats.power"
    , DEPOSIT_LEVEL                             = "gtch.lang.deposit.level"
    , AUTO                                      = "gtch.lang.auto"
    , AUTO_OTHERWISE_ANY                        = "gtch.lang.autootherwiseany"
    , NO_AUTO                                   = "gtch.lang.noauto"
    , TOOLTIP_PREHEAT                           = "gtch.lang.preheat"
    , TOOLTIP_LENGTH                            = "gtch.lang.motor.length"
    , ENERGY_LENGTH                             = "gtch.lang.energy.motor.length"
    , ENERGY_TO                                 = "gtch.lang.energy.to"
    , ENERGY_UPTO                               = "gtch.lang.energy.upto"
    , ENERGY_PARALLEL                           = "gtch.lang.energy.parallel"
    , OVERCLOCK_GENERATOR                       = "gtch.lang.overclock.generator"
    , OVERCLOCK_CHEAP                           = "gtch.lang.overclock.cheap"
    , OVERCLOCK_EXPENSIVE                       = "gtch.lang.overclock.expensive"
    , OVERCLOCK_SQRT                            = "gtch.lang.overclock.sqrt"
    , OVERCLOCK_PARALLEL                        = "gtch.lang.overclock.parallel"
    , OVERCLOCK_PARALLEL_TU                     = "gtch.lang.overclock.parallel.tu"
    , FACE_HEAT_TRANS                           = "gtch.lang.face.heattransmitter"
    , FACE_PIPE_HOLE                            = "gtch.lang.face.pipehole"
    , TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK      = "gtch.lang.use.sneak.magnifyingglass.to.detail"
    , TOOL_TO_MEASURE_TACHOMETER                = "gtch.lang.use.tachometer.to.measure"
    , TOOL_TO_MEASURE_ELECTOMETER               = "gtch.lang.use.electrometer.to.measure"
    , TOOL_TO_SET_MODE_MONKEY_WRENCH            = "gtch.lang.use.monkey.wrench.to.set.mode.side"
    , TOOL_TO_SET_IO_MONKEY_WRENCH              = "gtch.lang.use.monkey.wrench.to.set.io.side"
    , TRANSFORMER_NORMAL                        = "gtch.lang.transformer.normal"
    , TRANSFORMER_REVERSED                      = "gtch.lang.transformer.reversed"
    , HAZARD_EXPLOSION_LENGTH                   = "gtch.lang.hazard.explosion.motor.length"
    , WIRE_STATS_RESISTANCE                     = "gtch.lang.wire.stats.resistance"
    ;

    public static final String add(String aKey, String aEnglish) {LanguageHandler_CH.add(RegType.GTCH, aKey, aEnglish); return aKey;}
    public static final String add(RegType aRegType, String aKey, String aEnglish) {LanguageHandler_CH.add(aRegType, aKey, aEnglish); return aKey;}
    public static final String get(String aKey) {return LanguageHandler_CH.translate(aKey);}
    public static final String getNumber(String aKey, long aNum) {return String.format(LanguageHandler_CH.translate(aKey), aNum);}
    public static final String getNumber(String aKey, long aNum1, long aNum2) {return String.format(LanguageHandler_CH.translate(aKey), aNum1, aNum2);}
    public static final String getItemName(String aKey, int aRegistryMeta, int aRegistryID) {return String.format(LanguageHandler_CH.translate(aKey), MultiTileEntityRegistry.getRegistry(aRegistryID).getLocal(aRegistryMeta));}
    public static final String get(String aKey, String aDefault) {return LanguageHandler_CH.translate(aKey, aDefault);}

    public static final String percentSimple(long aNumber) {return String.valueOf(aNumber/100);}
    public static final String getToolTipEfficiencySimple(long aEfficiency) {aEfficiency = Math.abs(aEfficiency); return percentSimple(aEfficiency) + "%";}
    
    
    // 用于避免重复代码，添加 prefix + material 的物品的语言文件
    public static void addOredict(OreDictPrefix aPrefix, OreDictMaterial aMaterial) {
        if (aMaterial.mRegType==RegType.GREG && aPrefix.mRegType==RegType.GREG) LH.add("oredict." + aPrefix.dat(aMaterial).toString(), LanguageHandler.getLocalName(aPrefix, aMaterial));
        else {
            // 以 aPrefix 优先语言文件的位置
            if (aPrefix.mRegType==RegType.GREG) LH_CH.add(aMaterial.mRegType, "oredict." + aPrefix.dat(aMaterial).toString(), LanguageHandler.getLocalName(aPrefix, aMaterial));
            else LH_CH.add(aPrefix.mRegType, "oredict." + aPrefix.dat(aMaterial).toString(), LanguageHandler.getLocalName(aPrefix, aMaterial));
        }
    }

    public static void init() {
        add(AXLE_STATS_SPEED,                           "Speed limit:");
        add(AXLE_STATS_POWER,                           "Power limit:");
        add(DEPOSIT_LEVEL,                              "Level:");
        add(AUTO,                                       "auto");
        add(AUTO_OTHERWISE_ANY,                         "auto, otherwise any");
        add(NO_AUTO,                                    "no auto");
        add(TOOLTIP_PREHEAT,                            "This machine needs preheating");
        add(TOOLTIP_LENGTH,                             "Improve power and efficiency by increasing length");
        add(ENERGY_LENGTH,                              "At length %d:");
        add(ENERGY_TO,                                  "%d to %d");
        add(ENERGY_UPTO,                                "up to %d");
        add(ENERGY_PARALLEL,                            "up to %dx processed per run");
        add(OVERCLOCK_GENERATOR,                        "Input beyond recommended power will reduce efficiency");
        add(OVERCLOCK_CHEAP,                            "Additional power can accelerate processing Without Loss");
        add(OVERCLOCK_EXPENSIVE,                        "Additional power can Partially accelerate processing");
        add(OVERCLOCK_SQRT,                             "square root");
        add(OVERCLOCK_PARALLEL,                         "Parallel processing can split power to improve efficiency");
        add(OVERCLOCK_PARALLEL_TU,                      "Processes can be accelerated by Parallelism");
        add(FACE_HEAT_TRANS,                            "Heat Transmitters");
        add(FACE_PIPE_HOLE,                             "Pipe Holes");
        add(TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK,       "Use Magnifying Glass in sneaking to see more Details");
        add(TOOL_TO_MEASURE_TACHOMETER,                 "Use Tachometer to Measure");
        add(TOOL_TO_MEASURE_ELECTOMETER,                "Use Electrometer to Measure");
        add(TOOL_TO_SET_MODE_MONKEY_WRENCH,             "Use Monkeywrench to Change Mode");
        add(TOOL_TO_SET_IO_MONKEY_WRENCH,               "Use Monkeywrench to set Input and Output Side");
        add(TRANSFORMER_NORMAL,                         "Normal Mode:");
        add(TRANSFORMER_REVERSED,                       "Reversed Mode:");
        add(HAZARD_EXPLOSION_LENGTH,                    "Explodes when Change Structure while machine is running!");
        add(WIRE_STATS_RESISTANCE,                      "Electrical Resistance:");
    }
}
