package gregtechCH.config;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

import gregapi.util.OM;
import gregtechCH.config.data.DataItemMaterial;
import gregtechCH.config.data.DataMultiTileEntity;

import java.io.File;

/**
 * @author CHanzy
 *
 * Manage the config data in json style.
 */
public class ConfigJson {
    public static DataMultiTileEntity DATA_MULTITILEENTITY = new DataMultiTileEntity();
    public static DataItemMaterial DATA_ITEM_MATERIAL = new DataItemMaterial();
    
    public static void initJsonFile() {
        DirectoriesGTCH.JSON_GTCH = new File(DirectoriesGT.CONFIG, "GregTechCH");
        if (!DirectoriesGTCH.JSON_GTCH.exists()) DirectoriesGTCH.CONFIG_GTCH = new File(DirectoriesGT.CONFIG, "gregtechCH");
    }
    
    public static void readJsonFile() {
        DATA_MULTITILEENTITY.initJsonFile("multiTileEntity.json", DataMultiTileEntity.class);
        DATA_ITEM_MATERIAL.initJsonFile("materials.json", DataItemMaterial.class);
        for (DataItemMaterial.ItemMaterial IM : DATA_ITEM_MATERIAL.IM_Init) {
            OM.data(IM.item, IM.data);
        }
    }
    
    public static void readJsonFilePost() {
        for (DataItemMaterial.ItemMaterial IM : ConfigJson.DATA_ITEM_MATERIAL.IM_PostInit) {
            OM.data(IM.item, IM.data);
        }
    }

}
