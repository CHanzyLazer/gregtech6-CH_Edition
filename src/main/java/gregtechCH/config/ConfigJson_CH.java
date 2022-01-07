package gregtechCH.config;

import com.alibaba.fastjson.*;
import static gregapi.data.CS.*;

import gregtechCH.config.machine.DataMachines_CH;
import gregtechCH.data.CS_CH.*;
import gregtechCH.data.MA_Machine_CH;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author CHanzy
 *
 * Manage the config data in json style.
 */
public class ConfigJson_CH {

    public static MA_Machine_CH MATERIALS_ATTRIBUTES = new MA_Machine_CH();
    public static DataMachines_CH DATA_MACHINES = new DataMachines_CH();

    public static void initFile() {
        DirectoriesGTCH.JSON_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.JSON_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
    }
    public static void initConfigMachine() {
        try {
            // read
            String jsonInput = ReadFromFile(DirectoriesGTCH.JSON_GTCH, "machines.json");
            DATA_MACHINES = JSON.parseObject(jsonInput, DataMachines_CH.class);
        } catch (Exception e) {
            // write
            System.out.println("===========GTCH===========");
            System.out.println("Fail to load json config " + "\"machines.json\"" + ", the config now become to default.");
            e.printStackTrace();
            DATA_MACHINES.initDefault();
            String jsonOutput = JSON.toJSONString(DATA_MACHINES, T);
            writeToFile(jsonOutput, DirectoriesGTCH.JSON_GTCH, "machines.json");
        }
        // write
//        System.out.println("===========GTCH===========");
//        System.out.println("Fail to load json config " + "\"machines.json\"" + ", the config now become to default.");
//        DATA_MACHINES.initDefault();
//        String jsonOutput = JSON.toJSONString(DATA_MACHINES, T);
//        writeToFile(jsonOutput, DirectoriesGTCH.JSON_GTCH, "machines.json");
    }

    private static void writeToFile(String aStr, File aDir, String aFileName) {
        try {
            Path confPath = Paths.get(aDir.getPath(), aFileName);
            if(!Files.exists(confPath)) Files.createFile(confPath);
            Files.write(confPath, aStr.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String ReadFromFile(File aDir, String aFileName) throws IOException {
        Path confPath = Paths.get(aDir.getPath(), aFileName);
        return new String(Files.readAllBytes(confPath));
    }
}
