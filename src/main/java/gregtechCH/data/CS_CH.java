package gregtechCH.data;

import gregapi.config.Config;

import java.io.File;

public class CS_CH {
    public static final String
              NBT_EFFICIENCY_CH             = "gtch.eff"                    // Short from 0 to 10000 describing the global Efficiency. CHanzy
            , NBT_EFFICIENCY_WATER          = "gtch.eff.water"              // Short from 0 to 10000 describing the water Efficiency. CHanzy

            , NBT_ENERGY_SU_PRE             = "gtch.energy.su.pre"          // Long, The previous steam of turbines. CHanzy

            , NBT_INPUT_REC                 = "gtch.input.rec"              // Long, Recommend input energy, for OmniOcular usage. CHanzy
            , NBT_INPUT_NOW                 = "gtch.input.now"              // Long, Current input energy, for OmniOcular usage. CHanzy
            , NBT_OUTPUT_REC                = "gtch.output.rec"             // Long, Recommend output energy, for OmniOcular usage. CHanzy
            , NBT_OUTPUT_NOW                = "gtch.output.now"             // Long, Current output energy, for OmniOcular usage. CHanzy
            , NBT_OUTPUT_BUFFER             = "gtch.output.buffer"          // Long, Buffer to let output smooth. CHanzy
            , NBT_OUTPUT_SELF               = "gtch.output.self"            // Boolean, Is the main multiblock output energy self. CHanzy

            , NBT_PREHEAT                   = "gtch.preheat"                // Boolean, Is machine preheating, for OmniOcular usage. CHanzy
            , NBT_PREHEAT_ENERGY            = "gtch.preheat.energy"         // Long, Energy required for preheating. CHanzy
            , NBT_PREHEAT_RATE              = "gtch.preheat.rate"           // Long, Rate in preheating. CHanzy
            , NBT_PREHEAT_COST              = "gtch.preheat.cost"           // Long, Energy cost Rate in preheating. CHanzy

            , NBT_COOLDOWN_CH               = "gtch.cooldown"               // Boolean, Is machine cooldown, for OmniOcular usage. CHanzy
            , NBT_COOLDOWN_RATE             = "gtch.cooldown.rate"          // Long, Rate of cooldown. CHanzy
            , NBT_COOLDOWN_COUNTER          = "gtch.cooldown.counter"       // Byte, counter of cooldown. CHanzy

            , NBT_BURNING                   = "gtch.burning"                // Boolean, Is machine burning, for mechine need burning and preheat. CHanzy

            , NBT_LENGTH_MIN                = "gtch.length.min"             // Integer, The minimum length of turbines. CHanzy
            , NBT_LENGTH_MAX                = "gtch.length.max"             // Integer, The maximum length of turbines. CHanzy
            , NBT_LENGTH_MID                = "gtch.length.mid"             // Integer, The middle length of turbines. CHanzy
            , NBT_LENGTH                    = "gtch.length"                 // Integer, The current length of turbines. CHanzy
            , NBT_LENGTH_PRE                = "gtch.length.pre"             // Integer, The previous length of turbines. CHanzy
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
}
