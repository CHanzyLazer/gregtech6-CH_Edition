package gregtechCH.data;

import gregapi.config.Config;

import java.io.File;

public class CS_CH {
    public static final String
              NBT_EFFICIENCY_CH             = "gtch.eff"                    // Short from 0 to 10000 describing the global Efficiency. CHanzy
            , NBT_EFFICIENCY_WATER          = "gtch.eff.water"              // Short from 0 to 10000 describing the water Efficiency. CHanzy

            , NBT_INPUT_REC                 = "gtch.input.rec"              // Recommend input energy, for OmniOcular usage. CHanzy
            , NBT_OUTPUT_REC                = "gtch.output.rec"              // Recommend input energy, for OmniOcular usage. CHanzy
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
}
