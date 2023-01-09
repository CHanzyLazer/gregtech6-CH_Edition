package gregtechCH.config;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

import gregapi.util.OM;
import gregtechCH.config.data.DataItemMaterial_CH;
import gregtechCH.config.data.DataMultiTileEntity_CH;

import java.io.File;

/**
 * @author CHanzy
 *
 * Manage the config data in json style.
 */
public class ConfigJson_CH {
    public static DataMultiTileEntity_CH DATA_MULTITILEENTITY = new DataMultiTileEntity_CH();
    public static DataItemMaterial_CH DATA_ITEM_MATERIAL = new DataItemMaterial_CH();
    
    public static void initJsonFile() {
        DirectoriesGTCH.JSON_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.JSON_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
    }
    
    public static void readJsonFile() {
        DATA_MULTITILEENTITY.initJsonFile("multiTiletEntity.json", DataMultiTileEntity_CH.class);
        DATA_ITEM_MATERIAL.initJsonFile("materials.json", DataItemMaterial_CH.class);
        for (DataItemMaterial_CH.ItemMaterial IM : DATA_ITEM_MATERIAL.IM_Init) {
            OM.data(IM.item, IM.data);
        }
    }
    
    public static void readJsonFilePost() {
        for (DataItemMaterial_CH.ItemMaterial IM : ConfigJson_CH.DATA_ITEM_MATERIAL.IM_PostInit) {
            OM.data(IM.item, IM.data);
        }
    }

}
