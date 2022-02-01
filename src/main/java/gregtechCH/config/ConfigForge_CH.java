package gregtechCH.config;

import gregapi.config.Config;
import static gregapi.data.CS.*;
import gregtechCH.data.CS_CH.*;

import java.io.File;

/**
 * @author CHanzy
 *
 *  Manage the config data in forge style.
 */
public class ConfigForge_CH {
    public static class DATA_GTCH {
        public static boolean enableChangeMachineUnsorted1;
        public static boolean enableChangeMachineMultiblocks;

        public static boolean disableAllStoragePowerconducting;
    }
    public static class DATA_MACHINES {
        public static boolean enableChangeMotor;
        public static boolean motorExplodeCheck;

        public static boolean motorExplodeByLength;
    }

    public static class DATA_REACTORS {
        public static int adjustCoolantOtherDiv;
        public static int adjustCoolantOtherMul;
    }

    public static void initFile() {
        DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.CONFIG_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
    }
    public static void initConfig() {
        ConfigsGTCH.REACTORS    = new Config(DirectoriesGTCH.CONFIG_GTCH, "Reactors.cfg");
        ConfigsGTCH.MACHINES    = new Config(DirectoriesGTCH.CONFIG_GTCH, "Machines.cfg");
        ConfigsGTCH.GTCH        = new Config(DirectoriesGTCH.CONFIG_GTCH, "GTCH.cfg");

        DATA_GTCH.enableChangeMachineUnsorted1 = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_unsorted1_(gt6_false)",  T);
        DATA_GTCH.enableChangeMachineMultiblocks = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_Multiblocks_(gt6_false)", T);

        DATA_GTCH.disableAllStoragePowerconducting = ConfigsGTCH.GTCH.get(ConfigCategories_CH.fluid_CH, "disable_all_storage_powerconducting_(gt6_false)", T);

        DATA_MACHINES.enableChangeMotor = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "enableChange_motor_(gt6_false)", T);
        DATA_MACHINES.motorExplodeCheck = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_check_(gt6_?)", T);
        DATA_MACHINES.motorExplodeByLength = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_by_length_(gt6_false)", T);

        DATA_REACTORS.adjustCoolantOtherDiv = ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_div_(gt6_1)", 8);
        DATA_REACTORS.adjustCoolantOtherMul = ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_mul_(gt6_1)", 3);
    }
}
