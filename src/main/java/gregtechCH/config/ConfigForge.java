package gregtechCH.config;

import gregapi.config.Config;
import static gregapi.data.CS.*;
import static gregtech.interfaces.asm.LO_CH.isEnableAsmBlockGtLightOpacity;

import gregapi.util.UT;
import gregtechCH.data.CS_CH.*;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author CHanzy
 *  Manage the config data in forge style.
 */
public class ConfigForge {
    public static class DATA_GTCH {
        public static boolean debugging;
        
        public static boolean enableGT6U;
        
        public static boolean enableChangeLoader_Fluids;
        public static boolean enableChangeLoader_MultiTileEntities;
        
        public static boolean sneakingMountCover;
        public static boolean itemNBTSensor;
        
        public static boolean disableAllStoragePowerconducting;
        
        public static float markRatio;
        public static float mixBaseRatio;
        public static float mixPaintRatio;
        
        public static boolean disableGTBlockLightOpacity;
        
        public static boolean disableGTRerender;
        public static boolean rerenderAll;
        public static int rerenderMainTick;
        public static int rerenderAroundTick;
        public static int rerenderSleepTick;
        public static RerenderTick[] rerenderTickList;
        public static int rerenderChunkPerTick;
        public static int rerenderMainMaxChunk;
        public static int rerenderMainLength;
        public static int rerenderAroundMaxChunk;
        public static int rerenderAroundLength;
    }
    public static class DATA_MACHINES {
        public static boolean motorExplodeCheck;
        public static boolean motorExplodeByLength;
        public static boolean motorExplodeByPlunger;
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
        
        DATA_GTCH.debugging = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general, "debugging_(gt6_false)", F);
        
        DATA_GTCH.enableGT6U = ConfigsGTCH.GTCH.get(ConfigCategories_CH.gameplay, "enableGT6U_(gt6_false)", F);
        
        DATA_GTCH.enableChangeLoader_Fluids = ConfigsGTCH.GTCH.get(ConfigCategories_CH.loader, "enableChangeLoader_fluids_(gt6_false)", T);
        DATA_GTCH.enableChangeLoader_MultiTileEntities = ConfigsGTCH.GTCH.get(ConfigCategories_CH.loader, "enableChangeLoader_MultiTileEntities_(gt6_false)",  T);
        
        DATA_GTCH.sneakingMountCover = ConfigsGTCH.GTCH.get(ConfigCategories_CH.general, "sneaking_mount_cover_(gt6_false)", T);
        DATA_GTCH.itemNBTSensor = ConfigsGTCH.GTCH.get(ConfigCategories_CH.nbt, "item_nbt_sensor_(gt6_true)", F);
        
        DATA_GTCH.disableAllStoragePowerconducting = ConfigsGTCH.GTCH.get(ConfigCategories_CH.fluid, "disable_all_storage_powerconducting_(gt6_false)", T);
        
        DATA_GTCH.markRatio = Math.min(1.0F, Math.max((float)ConfigsGTCH.GTCH.get(ConfigCategories_CH.colour, "mark_ratio_(gt6_?)", 0.125), 0.0F));
        DATA_GTCH.mixBaseRatio = Math.min(1.0F, Math.max((float)ConfigsGTCH.GTCH.get(ConfigCategories_CH.colour, "colour_mix_base_ratio_(gt6_0.0)", 0.5), 0.0F));
        DATA_GTCH.mixPaintRatio = Math.min(1.0F, Math.max((float)ConfigsGTCH.GTCH.get(ConfigCategories_CH.colour, "colour_mix_paint_ratio_(gt6_1.0)", 0.15), 0.0F));
        
        DATA_GTCH.disableGTBlockLightOpacity = isEnableAsmBlockGtLightOpacity() ? ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "disable_GT_block_lightopacity_(gt6_?)",  F) : T; // 对于禁用了 ASM 的情况直接禁用掉 GT 方块的不透光度
        
        DATA_GTCH.disableGTRerender         = ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "disable_GT_rerender_(gt6_false)",  F);
        DATA_GTCH.rerenderAll               = ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_all_(gt6_true)",  F);
        DATA_GTCH.rerenderMainTick          = UT.Code.bind7(ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_main_tick_(gt6_?)",  2));
        DATA_GTCH.rerenderAroundTick        = UT.Code.bind7(ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_around_tick_(gt6_?)",  1));
        DATA_GTCH.rerenderSleepTick         = UT.Code.bind7(ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_sleep_tick_(gt6_?)",  0));
        DATA_GTCH.rerenderChunkPerTick      = UT.Code.bind7(ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_chunk_per_tick_(gt6_?)",  1));
        DATA_GTCH.rerenderMainLength        = UT.Code.bind7(ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_main_length_(gt6_?)",  4));
        DATA_GTCH.rerenderAroundLength      = UT.Code.bind7(ConfigsGTCH.GTCH.get(ConfigCategories_CH.optimize, "rerender_around_length_(gt6_?)",  1));
        DATA_GTCH.rerenderMainMaxChunk = DATA_GTCH.rerenderMainTick * DATA_GTCH.rerenderChunkPerTick;
        DATA_GTCH.rerenderAroundMaxChunk = DATA_GTCH.rerenderAroundTick * DATA_GTCH.rerenderChunkPerTick;
        LinkedList<RerenderTick> tList = new LinkedList<>();
        for (int i=0; i<DATA_GTCH.rerenderMainTick; ++i)    tList.add(RerenderTick.MAIN);
        for (int i=0; i<DATA_GTCH.rerenderAroundTick; ++i)  tList.add(RerenderTick.AROUND);
        for (int i=0; i<DATA_GTCH.rerenderSleepTick; ++i)   tList.add(RerenderTick.SLEEP);
        Collections.shuffle(tList); // 使用内部方法来随机打乱队列
        tList.addFirst(RerenderTick.INIT); // 保证第一个是 INIT
        DATA_GTCH.rerenderTickList = tList.toArray(new RerenderTick[0]);
        
        
        DATA_MACHINES.motorExplodeCheck = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_check_(gt6_?)", T);
        DATA_MACHINES.motorExplodeByLength = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_by_length_(gt6_false)", T);
        DATA_MACHINES.motorExplodeByPlunger = ConfigsGTCH.MACHINES.get(ConfigCategories_CH.Machines.generatorMotor, "motor_explode_by_plunger_(gt6_false)", T);
        
        DATA_REACTORS.adjustCoolantOtherDiv = ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_div_(gt6_1)", 8);
        DATA_REACTORS.adjustCoolantOtherMul = ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_mul_(gt6_1)", 3);
    }
}
