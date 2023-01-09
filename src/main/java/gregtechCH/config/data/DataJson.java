package gregtechCH.config.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gregtechCH.data.CS_CH;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static gregapi.data.CS.OUT;
import static gregtechCH.GTCH_Main.getModNameForLog;

public abstract class DataJson {
    public abstract void initDefault();
    protected abstract <Type extends DataJson> void setMember(Type aData);
    
    // 用于子类重写，实现自定义序列化和反序列化等功能
    public GsonBuilder getGsonBuilder() {return new GsonBuilder().setPrettyPrinting();}

    public <Type extends DataJson> void initJsonFile(String aFileName, Class<Type> clazz) {
        Gson gson = getGsonBuilder().create();
        try {
            // read
            String jsonInput = ReadFromFile(CS_CH.DirectoriesGTCH.JSON_GTCH, aFileName);
            OUT.println(getModNameForLog() + ": read config \"" + aFileName + "\" successful");
            setMember(gson.fromJson(jsonInput, clazz));
            
            OUT.println(getModNameForLog() + ": deserialize config \"" + aFileName + "\" successful");
        } catch (Exception e) {
            // write
            OUT.println(getModNameForLog() + ": Fail to load json config \"" + aFileName + "\", the config now become to default.");
            this.initDefault();
            String jsonOutput = gson.toJson(this);
            writeToFile(jsonOutput, CS_CH.DirectoriesGTCH.JSON_GTCH, aFileName);
        }
    }
    
    public <Type extends DataJson> void initJsonFileNoLog(String aFileName, Class<Type> clazz) {
        Gson gson = getGsonBuilder().create();
        try {
            // read
            String jsonInput = ReadFromFile(CS_CH.DirectoriesGTCH.JSON_GTCH, aFileName);
            setMember(gson.fromJson(jsonInput, clazz));
        } catch (Exception e) {
            // write
            this.initDefault();
            String jsonOutput = gson.toJson(this);
            writeToFile(jsonOutput, CS_CH.DirectoriesGTCH.JSON_GTCH, aFileName);
        }
    }
    
    private static void writeToFile(String aStr, File aDir, String aFileName) {
        try {
            Path confPath = Paths.get(aDir.getPath(), aFileName);
            if(!Files.exists(confPath)) {
                Files.createFile(confPath);
            } else {
                Path bakPath = Paths.get(aDir.getPath(), aFileName+".bak");
                Files.deleteIfExists(bakPath);
                Files.move(confPath, bakPath);
            }
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
