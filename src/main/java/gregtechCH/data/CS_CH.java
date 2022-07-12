package gregtechCH.data;

import com.google.common.collect.Sets;
import gregapi.config.Config;
import gregapi.data.MT;
import gregapi.oredict.OreDictPrefix;
import gregapi.util.UT;
import gregtechCH.util.UT_CH;

import java.io.File;
import java.util.Set;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;

public class CS_CH {

    // 补充一些常量
    public static final byte SIDE_NUMBER = (byte)ALL_SIDES_VALID.length;
    public static final byte[][] ZL_BI_BYTE = new byte[0][0];
    public static final long[][] ZL_BI_LONG = new long[0][0];

    // 只有 RGB 值的转为 RGBa 加上此值可以比较保险
    public static final int ALPHA_COLOR = 0xff000000;
    // 事先计算的一些方块的颜色，可以避免调用时频繁计算颜色值
    public static final int[] DYES_INT_Asphalt  = new int[DYES_INT.length];
    public static final int[] DYES_INT_CFoam    = new int[DYES_INT.length];
    public static final int[] DYES_INT_Concrete = new int[DYES_INT.length];
    public static final int[] DYES_INT_Glass    = new int[DYES_INT.length];

    public static void initCS_CH() {
        for (int i = 0; i<DYES_INT.length; ++i) DYES_INT_Asphalt[i]     = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.Asphalt.fRGBaSolid),          DYES_INT[i]);
        for (int i = 0; i<DYES_INT.length; ++i) DYES_INT_CFoam[i]       = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.ConstructionFoam.fRGBaSolid), DYES_INT[i]);
        for (int i = 0; i<DYES_INT.length; ++i) DYES_INT_Concrete[i]    = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.Concrete.fRGBaSolid),         DYES_INT[i]);
        for (int i = 0; i<DYES_INT.length; ++i) DYES_INT_Glass[i]       = UT_CH.Code.getPaintRGB(UT.Code.getRGBInt(MT.Glass.fRGBaSolid),            DYES_INT[i]);
    }

    // 用于统计哪些 prefix 是可以做覆盖板的
    public static final Set<OreDictPrefix> ALL_COVER_PREFIX = Sets.newHashSet(plate, plateDouble, plateTriple, plateQuadruple, plateQuintuple, plateDense, plateCurved, plateGem, sheetGt, foil);
    // 用于统计哪些 prefix 是属于管道或者线缆或者是连接器
    public static final Set<OreDictPrefix> ALL_PIPE_PREFIX  = Sets.newHashSet(pipeTiny, pipeSmall, pipeMedium, pipeLarge, pipeHuge, pipeQuadruple, pipeNonuple, pipeRestrictiveTiny, pipeRestrictiveSmall, pipeRestrictiveMedium, pipeRestrictiveLarge, pipeRestrictiveHuge, pipe);
    public static final Set<OreDictPrefix> ALL_WIRE_PREFIX  = Sets.newHashSet(wireGt01, wireGt02, wireGt03, wireGt04, wireGt05, wireGt06, wireGt07, wireGt08, wireGt09, wireGt10, wireGt11, wireGt12, wireGt13, wireGt14, wireGt15, wireGt16);
    public static final Set<OreDictPrefix> ALL_CABLE_PREFIX = Sets.newHashSet(cableGt01, cableGt02, cableGt04, cableGt08, cableGt12);

    public static final String
              NBT_CANFILL_STEAM             = "gtch.canfill.steam"          // Boolean, Is this machine can fill steam. CHanzy

            , NBT_EFFICIENCY_CH             = "gtch.eff"                    // Short from 0 to 10000 describing the global Efficiency, or just for OmniOcular usage. CHanzy
            , NBT_EFFICIENCY_WATER          = "gtch.eff.water"              // Short from 0 to 10000 describing the water Efficiency. CHanzy
            , NBT_EFFICIENCY_OC             = "gtch.eff.oc"                 // Short from 0 to 10000 describing the overclocking Efficiency. CHanzy

            , NBT_ENERGY_EFF                = "gtch.energy.eff"             // Long, The effective energy of boiler. CHanzy
            , NBT_ENERGY_PRE                = "gtch.energy.pre"             // Long, The previous energy of boiler. CHanzy
            , NBT_ENERGY_SU_PRE             = "gtch.energy.su.pre"          // Long, The previous steam of turbines. CHanzy

            , NBT_INPUT_REC                 = "gtch.input.rec"              // Long, Recommend input energy, for OmniOcular usage. CHanzy
            , NBT_INPUT_NOW                 = "gtch.input.now"              // Long, Current input energy, for OmniOcular usage. CHanzy
            , NBT_INPUT_BUFFER              = "gtch.input.buffer"           // Long, Input Buffer to let output smooth. CHanzy
            , NBT_OUTPUT_REC                = "gtch.output.rec"             // Long, Recommend output energy, for OmniOcular usage. CHanzy
            , NBT_OUTPUT_NOW                = "gtch.output.now"             // Long, Current output energy, for OmniOcular usage. CHanzy
            , NBT_OUTPUT_SELF               = "gtch.output.self"            // Boolean, Is the main multiblock output energy self. CHanzy

            , NBT_PREHEAT                   = "gtch.preheat"                // Boolean, Is machine preheating, for OmniOcular usage. CHanzy
            , NBT_PREHEAT_ENERGY            = "gtch.preheat.energy"         // Long, Energy required for preheating. CHanzy
            , NBT_PREHEAT_RATE              = "gtch.preheat.rate"           // Long, Rate in preheating. CHanzy
            , NBT_PREHEAT_COST              = "gtch.preheat.cost"           // Long, Energy cost Rate in preheating. CHanzy

            , NBT_PROGRESS_RATE             = "gtch.progress.rate"          // Long, Rate of progress of machine. CHanzy

            , NBT_COOLDOWN_CH               = "gtch.cooldown"               // Boolean, Is machine cooldown, for OmniOcular usage. CHanzy
            , NBT_COOLDOWN_RATE             = "gtch.cooldown.rate"          // Long, Rate of cooldown. CHanzy
            , NBT_COOLDOWN_COUNTER          = "gtch.cooldown.counter"       // Byte, counter of cooldown. CHanzy

            , NBT_BURNING                   = "gtch.burning"                // Boolean, Is machine burning, for mechine need burning and preheat. CHanzy

            , NBT_LENGTH_MIN                = "gtch.length.min"             // Integer, The minimum length of turbines. CHanzy
            , NBT_LENGTH_MAX                = "gtch.length.max"             // Integer, The maximum length of turbines. CHanzy
            , NBT_LENGTH_MID                = "gtch.length.mid"             // Integer, The middle length of turbines. CHanzy
            , NBT_LENGTH                    = "gtch.length"                 // Integer, The current length of turbines. CHanzy
            , NBT_LENGTH_PRE                = "gtch.length.pre"             // Integer, The previous length of turbines. CHanzy

            , NBT_ADD_BYTE                  = "gtch.add.byte"               // Byte, The additional byte information that have random name, CHanzy
            , NBT_ADD_BOOL                  = "gtch.add.bool"               // Boolean, The additional boolean information that have random name, CHanzy

            , NBT_LIGHT_VALUE               = "gtch.light.value"            // Byte, The light value of block. CHanzy
            , NBT_LIGHT_OPACITY             = "gtch.light.opacity"          // Short, The light opacity of block. CHanzy

            , NBT_BEGIN                     = "gtch.begin"                  // Byte
            , NBT_END                       = "gtch.end"                    // Byte

            , NBT_TICK                      = "gtch.tick"                   // Byte

            , NBT_COLOR_BOTTOM              = "gtch.color.bottom"           // Integer, The bottom (or base) color of paint. CHanzy
            , NBT_COLOR_ORIGIN              = "gtch.color.origin"           // Integer, The origin color of painted block. CHanzy
            ;

    public static class DirectoriesGTCH {
        public static File
                CONFIG_GTCH,
                JSON_GTCH;
    }

    /** Configs CH */
    public static class ConfigsGTCH {
        public static Config
                GTCH,
                MACHINES,
                REACTORS;
    }

    public enum IconType {
        COLORED,
        OVERLAY,
        OVERLAY_ACTIVE,
        OVERLAY_ACTIVE_L,
        OVERLAY_ACTIVE_R,
        OVERLAY_ACTIVE_LS,
        OVERLAY_ACTIVE_LF,
        OVERLAY_ACTIVE_RS,
        OVERLAY_ACTIVE_RF,
        OVERLAY_PREHEAT,
        OVERLAY_PREHEAT_L,
        OVERLAY_PREHEAT_R,
        OVERLAY_ENERGY_RU,
        OVERLAY_FLUID;
    }

    // 管道尺寸
    public enum Size {
        SMALL,
        MEDIUM,
        LARGE,
        HUGE
    }

    // 管道模式
    public enum Mode {
        DEFAULT,
        LIMIT,
        PRIORITY,
        DIVIDE // 保留模式
    }

    // 整数类型
    public enum NumberType {
        BYTE,
        SHORT,
        INT,
        LONG
    }
}
