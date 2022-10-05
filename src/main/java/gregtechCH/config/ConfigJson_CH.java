package gregtechCH.config;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

import gregapi.util.OM;
import gregtechCH.config.additional.DataSetMaterial_CH;
import gregtechCH.config.machine.generator.DataMachinesGenerator_CH;
import gregtechCH.config.machine.kinetic.DataMachineKinetic_CH;
import gregtechCH.config.machine.multiblock.DataMachinesMultiblock_CH;
import gregtechCH.config.machine.steam.DataMachinesSteam_CH;
import gregtechCH.data.*;

import java.io.File;

/**
 * @author CHanzy
 *
 * Manage the config data in json style.
 */
public class ConfigJson_CH {

    public static MA_MachineGenerator MA_MACHINE_GENERATOR = new MA_MachineGenerator();
    public static MA_MachineKinetic MA_MACHINE_KINETIC = new MA_MachineKinetic();
    public static MA_Rotor MA_ROTOR = new MA_Rotor();
    public static MA_PipeSize MA_PIPE_SIZE = new MA_PipeSize();
    public static MA_MachineMultiblock MA_MACHINE_MULTIBLOCK = new MA_MachineMultiblock();

    public static DataMachinesSteam_CH DATA_MACHINES_STEAM = new DataMachinesSteam_CH();
    public static DataMachinesGenerator_CH DATA_MACHINES_GENERATOR = new DataMachinesGenerator_CH();
    public static DataMachinesMultiblock_CH DATA_MACHINES_MULTIBLOCK = new DataMachinesMultiblock_CH();
    public static DataMachineKinetic_CH DATA_MACHINES_KINETIC = new DataMachineKinetic_CH();

    public static DataSetMaterial_CH DATA_ADD_MATERIAL = new DataSetMaterial_CH();

    public static void initJsonFile() {
        DirectoriesGTCH.JSON_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.JSON_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
    }

    public static void readJsonFile() {
        DATA_MACHINES_STEAM.initJsonFile("machines_steam.json", DataMachinesSteam_CH.class);
        DATA_MACHINES_GENERATOR.initJsonFile("machines_generator.json", DataMachinesGenerator_CH.class);
        DATA_MACHINES_MULTIBLOCK.initJsonFile("machines_multi-block.json", DataMachinesMultiblock_CH.class);
        DATA_MACHINES_KINETIC.initJsonFile("machines_kinetic.json", DataMachineKinetic_CH.class);

        DATA_ADD_MATERIAL.initJsonFile("additional_material.json", DataSetMaterial_CH.class);
        for (DataSetMaterial_CH.ItemMaterial IM : DATA_ADD_MATERIAL.IM_Init) {
            OM.data(IM.item, IM.data);
        }
    }

    public static void readJsonFilePost() {
        for (DataSetMaterial_CH.ItemMaterial IM : ConfigJson_CH.DATA_ADD_MATERIAL.IM_PostInit) {
            OM.data(IM.item, IM.data);
        }
    }

}
