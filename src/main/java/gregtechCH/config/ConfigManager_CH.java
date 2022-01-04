package gregtechCH.config;

import gregapi.config.Config;

import java.io.File;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

/**
 * @author CHanzy
 *
 * Loads some static ConfigData from config.
 */
public class ConfigManager_CH {
    public static void initFile() {
        DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.CONFIG_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
    }

    public static void initConfig() {
        ConfigsGTCH.MACHINES    = new Config(DirectoriesGTCH.CONFIG_GTCH, "Machines.cfg");
        ConfigsGTCH.REACTORS    = new Config(DirectoriesGTCH.CONFIG_GTCH, "Reactors.cfg");
        ConfigsGTCH.GTCH        = new Config(DirectoriesGTCH.CONFIG_GTCH, "GTCH.cfg");
    }
}
