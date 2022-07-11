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
        public static boolean enableChangeMachineUnsorted2;
        public static boolean enableChangeMachineMultiblocks;
        public static boolean enableChangeMachineMachines1;
        public static boolean enableChangeMachineMachines2;
        public static boolean enableChangeMachineMachines3;
        public static boolean enableChangeMachineMachines4;
        public static boolean enableChangeMachineKinetic;
        public static boolean enableChangeMachineSensors;

        public static boolean sneakingMountCover;

        public static boolean disableAllStoragePowerconducting;

        public static float markRatio;
        public static float mixRatio;

        public static boolean disableGTBlockLightOpacity;
    }
    public static class DATA_MACHINES {
        public static boolean enableChangeBoiler;
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
        DATA_GTCH.enableChangeMachineUnsorted2 = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_unsorted2_(gt6_false)",  T);
        DATA_GTCH.enableChangeMachineMultiblocks = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_multiblocks_(gt6_false)", T);
        DATA_GTCH.enableChangeMachineMachines1 = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_machines1_(gt6_false)", T);
        DATA_GTCH.enableChangeMachineMachines2 = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_machines2_(gt6_false)", T);
        DATA_GTCH.enableChangeMachineMachines3 = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_machines3_(gt6_false)", T);
        DATA_GTCH.enableChangeMachineMachines4 = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_machines4_(gt6_false)", T);
        DATA_GTCH.enableChangeMachineKinetic = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_kinetic_(gt6_false)", T);
        DATA_GTCH.enableChangeMachineSensors = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enableChange_machine_sensors_(gt6_false)", T);

        DATA_GTCH.sneakingMountCover = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "sneaking_mount_cover_(gt6_false)", T);

        DATA_GTCH.disableAllStoragePowerconducting = ConfigsGTCH.GTCH.get(ConfigCategories_CH.fluid_CH, "disable_all_storage_powerconducting_(gt6_false)", T);

        DATA_GTCH.markRatio = Math.min(1.0F, Math.max((float)ConfigsGTCH.GTCH.get(ConfigCategories_CH.colour_CH, "mark_ratio_(gt6_?)", 0.125), 0.0F));
        DATA_GTCH.mixRatio = Math.min(1.0F, Math.max((float)ConfigsGTCH.GTCH.get(ConfigCategories_CH.colour_CH, "colour_mix_ratio_(gt6_1.0)", 0.4), 0.0F));

        DATA_GTCH.disableGTBlockLightOpacity = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "disable_GT_block_LightOpacity_(gt6_?)",  F);

        DATA_MACHINES.enableChangeBoiler = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.boiler, "enableChange_boiler_(gt6_false)", T);
        DATA_MACHINES.enableChangeMotor = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "enableChange_motor_(gt6_false)", T);
        DATA_MACHINES.motorExplodeCheck = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_check_(gt6_?)", T);
        DATA_MACHINES.motorExplodeByLength = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_by_length_(gt6_false)", T);

        DATA_REACTORS.adjustCoolantOtherDiv = ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_div_(gt6_1)", 8);
        DATA_REACTORS.adjustCoolantOtherMul = ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_mul_(gt6_1)", 3);
    }
}
