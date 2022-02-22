package gregtechCH.config;

import static gregapi.data.CS.*;

import gregapi.util.OM;
import gregtechCH.config.additional.DataSetMaterial_CH;
import gregtechCH.config.machine.generator.DataMachinesGenerator_CH;
import gregtechCH.config.machine.kinetic.DataMachineKinetic_CH;
import gregtechCH.config.machine.multiblock.DataMachinesMultiblock_CH;
import gregtechCH.config.machine.steam.DataMachinesSteam_CH;
import gregtechCH.data.*;
import gregtechCH.data.CS_CH.*;

import java.io.File;

/**
 * @author CHanzy
 *
 * Manage the config data in json style.
 */
public class ConfigJson_CH {

    public static MA_MachineGenerator_CH MA_MACHINE_GENERATOR = new MA_MachineGenerator_CH();
    public static MA_MachineKinetic_CH MA_MACHINE_KINETIC = new MA_MachineKinetic_CH();
    public static MA_Rotor_CH MA_ROTOR = new MA_Rotor_CH();
    public static MA_PipeSize_CH MA_PIPE_SIZE = new MA_PipeSize_CH();
    public static MA_MachineMultiblock_CH MA_MACHINE_MULTIBLOCK = new MA_MachineMultiblock_CH();

    public static DataMachinesSteam_CH DATA_MACHINES_STEAM = new DataMachinesSteam_CH();
    public static DataMachinesGenerator_CH DATA_MACHINES_GENERATOR = new DataMachinesGenerator_CH();
    public static DataMachinesMultiblock_CH DATA_MACHINES_MULTIBLOCK = new DataMachinesMultiblock_CH();
    public static DataMachineKinetic_CH DATA_MACHINES_KINETIC = new DataMachineKinetic_CH();

    public static DataSetMaterial_CH DATA_ADD_MATERIAL = new DataSetMaterial_CH();

    public static void preInit() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": PreInit-Phase started!");

        DirectoriesGTCH.JSON_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.JSON_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
        CS_CH.initCS_CH();

        OUT.println(getModNameForLog() + ": PreInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }
    public static void init() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": Init-Phase started!");

        DATA_MACHINES_STEAM.initJsonFile("machines_steam.json", DataMachinesSteam_CH.class);
        DATA_MACHINES_GENERATOR.initJsonFile("machines_generator.json", DataMachinesGenerator_CH.class);
        DATA_MACHINES_MULTIBLOCK.initJsonFile("machines_multi-block.json", DataMachinesMultiblock_CH.class);
        DATA_MACHINES_KINETIC.initJsonFile("machines_kinetic.json", DataMachineKinetic_CH.class);

        DATA_ADD_MATERIAL.initJsonFile("additional_material.json", DataSetMaterial_CH.class);

        for (DataSetMaterial_CH.ItemMaterial IM : DATA_ADD_MATERIAL.IM_Init) {
            OM.data(IM.item, IM.data);
        }

        OUT.println(getModNameForLog() + ": Init-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }
    public static void postInit() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": PostInit-Phase started!");

        for (DataSetMaterial_CH.ItemMaterial IM : DATA_ADD_MATERIAL.IM_PostInit) {
            OM.data(IM.item, IM.data);
        }

        OUT.println(getModNameForLog() + ": PostInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }

    public static String getModNameForLog(){
        return "GTCH";
    }
}
