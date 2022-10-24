package gregtechCH.data;


import gregapi.code.TagData;
import gregapi.data.LH;

import static gregapi.data.TD.Energy.ALL;
import static gregapi.data.TD.Energy.ALL_NEGATIVE_ALLOWED;
import static gregtechCH.data.CS_CH.RegType.*;

// TODO 查看这些 TD 的使用
/* 额外添加的 Tag Data 统一放在这里 */
public class TD_CH {
    public static class Connectors {
        /* GT6U stuff */
        public static final TagData WIRE_OpticalFiber   = TagData.createTagData(GT6U, "CONNECTORS.WIRE_OPTICSLFIBER", "Optical Fiber");
    }
    
    public static class Energy {
        /* GT6U stuff */
        /**
         * Energy Tag for light Energy(From the sun). (Light Power)
         * Size = Nobody knows
         * Amount = Nobody knows
         */
        public static final TagData LP                  = TagData.createTagData(GT6U, "ENERGY.LP", "LP", "Light Energy", LH.Chat.YELLOW);
        static {
            ALL.add(LP);
            ALL_NEGATIVE_ALLOWED.add(LP);
        }
    }
    
    public static class ItemGenerator {
        /* GT6U stuff */
        public static final TagData
        // 仅用于为原版材料添加 tag 时，使用相应的 mod 注册
        LIQUID_CH                                       = TagData.createTagData("ITEMGENERATOR.LIQUID_CH"),
        MOLTEN_CH                                       = TagData.createTagData("ITEMGENERATOR.MOLTEN_CH"),
        GASES_CH                                        = TagData.createTagData("ITEMGENERATOR.GASES_CH"),
        VAPORS_CH                                       = TagData.createTagData("ITEMGENERATOR.VAPORS_CH"),
        PLASMA_CH                                       = TagData.createTagData("ITEMGENERATOR.PLASMA_CH"),
        LIQUID_6U                                       = TagData.createTagData("ITEMGENERATOR.LIQUID_6U"),
        MOLTEN_6U                                       = TagData.createTagData("ITEMGENERATOR.MOLTEN_6U"),
        GASES_6U                                        = TagData.createTagData("ITEMGENERATOR.GASES_6U"),
        VAPORS_6U                                       = TagData.createTagData("ITEMGENERATOR.VAPORS_6U"),
        PLASMA_6U                                       = TagData.createTagData("ITEMGENERATOR.PLASMA_6U"),
        
        OPTICALFIBERS                                   = TagData.createTagData("ITEMGENERATOR.OPTICALFIBERS");
    }
    
    // Making sure subclasses is statically loaded.
    public static void init() {
        Connectors.WIRE_OpticalFiber.getClass();
        Energy.LP.getClass();
        ItemGenerator.OPTICALFIBERS.getClass();
    }
}
