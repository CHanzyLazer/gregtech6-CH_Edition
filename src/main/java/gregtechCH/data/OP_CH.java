package gregtechCH.data;

import gregapi.data.TC;
import gregapi.oredict.OreDictPrefix;

import static gregapi.data.CS.*;
import static gregapi.data.TD.Prefix.*;
import static gregtechCH.data.CS_CH.RegType;
import static gregtechCH.data.TD_CH.ItemGenerator.OPTICALFIBERS;

/* 额外添加的 Prefixes 统一放在这里 */
public class OP_CH {
    // 提供指明注册 mod 的接口
    private static OreDictPrefix create(RegType aRegType, String aName, String aCategory, String aPreMaterial, String aPostMaterial) {return OreDictPrefix.createPrefix(aRegType, aName).setCategoryName(aCategory).setLocalPrefixName(aCategory).setLocalItemName(aPreMaterial, aPostMaterial);}
    private static OreDictPrefix create(RegType aRegType, String aName, String aCategory) {return OreDictPrefix.createPrefix(aRegType, aName).setCategoryName(aCategory).setLocalPrefixName(aCategory);}
    private static OreDictPrefix unused(RegType aRegType, String aName) {return OreDictPrefix.createPrefix(aRegType, aName).add(PREFIX_UNUSED);}
    private static OreDictPrefix create(String aName, String aCategory, String aPreMaterial, String aPostMaterial) {return create(RegType.GTCH, aName, aCategory, aPreMaterial, aPostMaterial);} // 默认为 GTCH
    private static OreDictPrefix create6U(String aName, String aCategory, String aPreMaterial, String aPostMaterial) {return create(RegType.GT6U, aName, aCategory, aPreMaterial, aPostMaterial);}
    private static OreDictPrefix create(String aName, String aCategory) {return create(RegType.GTCH, aName, aCategory);} // 默认为 GTCH
    private static OreDictPrefix create6U(String aName, String aCategory) {return create(RegType.GT6U, aName, aCategory);}
    private static OreDictPrefix unused(String aName) {return unused(RegType.GTCH, aName);} // 默认为 GTCH
    private static OreDictPrefix unused6U(String aName) {return unused(RegType.GT6U, aName);}
    
    /* GT6U stuffs */
    public static final OreDictPrefix
    wireLPGt16                  = create6U("wireLPGt16"                     , "16x Wires"                       , "16x "                            , " Wire"                           ).setMaterialStats(U * 8)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE, SCANNABLE, EXTRUDER_FODDER).setMinStacksize( 2).aspects(TC.ELECTRUM, 1),
    wireLPGt12                  = create6U("wireLPGt12"                     , "12x Wires"                       , "12x "                            , " Wire"                           ).setMaterialStats(U * 6)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE, SCANNABLE, EXTRUDER_FODDER).setMinStacksize( 2).aspects(TC.ELECTRUM, 1),
    wireLPGt08                  = create6U("wireLPGt08"                     , "8x Wires"                        , "8x "                             , " Wire"                           ).setMaterialStats(U * 4)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE, SCANNABLE, EXTRUDER_FODDER).setMinStacksize( 2).aspects(TC.ELECTRUM, 1),
    wireLPGt04                  = create6U("wireLPGt04"                     , "4x Wires"                        , "4x "                             , " Wire"                           ).setMaterialStats(U * 2)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE, SCANNABLE, EXTRUDER_FODDER).setMinStacksize( 4).aspects(TC.ELECTRUM, 1),
    wireLPGt02                  = create6U("wireLPGt02"                     , "2x Wires"                        , "2x "                             , " Wire"                           ).setMaterialStats(U    )     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE, SCANNABLE, EXTRUDER_FODDER).setMinStacksize( 8).aspects(TC.ELECTRUM, 1),
    wireLPGt01                  = create6U("wireLPGt01"                     , "1x Wires"                        , "1x "                             , " Wire"                           ).setMaterialStats(U2   )     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE, SCANNABLE, EXTRUDER_FODDER).setMinStacksize(16).aspects(TC.ELECTRUM, 1),
    
    cableLPGt16                 = create6U("cableLPGt16"                    , "16x Optical Fiber"               , "16x "                            , "  Optical Fiber"                 ).setMaterialStats(U * 8)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE).setMinStacksize( 1).aspects(TC.ELECTRUM, 1),
    cableLPGt12                 = create6U("cableLPGt12"                    , "12x Optical Fiber"               , "12x "                            , "  Optical Fiber"                 ).setMaterialStats(U * 6)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE).setMinStacksize( 2).aspects(TC.ELECTRUM, 1),
    cableLPGt08                 = create6U("cableLPGt08"                    , "8x Optical Fiber"                , "8x "                             , "  Optical Fiber"                 ).setMaterialStats(U * 4)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE).setMinStacksize( 2).aspects(TC.ELECTRUM, 1),
    cableLPGt04                 = create6U("cableLPGt04"                    , "4x Optical Fiber"                , "4x "                             , "  Optical Fiber"                 ).setMaterialStats(U * 2)     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE).setMinStacksize( 4).aspects(TC.ELECTRUM, 1),
    cableLPGt02                 = create6U("cableLPGt02"                    , "2x Optical Fiber"                , "2x "                             , "  Optical Fiber"                 ).setMaterialStats(U    )     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE).setMinStacksize( 8).aspects(TC.ELECTRUM, 1),
    cableLPGt01                 = create6U("cableLPGt01"                    , "1x Optical Fiber"                , "1x "                             , "  Optical Fiber"                 ).setMaterialStats(U2   )     .setCondition(OPTICALFIBERS)                                                                                        .add(UNIFICATABLE, BURNABLE, UNIFICATABLE_RECIPES, RECYCLABLE, SIMPLIFIABLE).setMinStacksize(16).aspects(TC.ELECTRUM, 1);
    
    public static final OreDictPrefix wireLPGt[] = {wireLPGt01, wireLPGt02, wireLPGt04, wireLPGt08, wireLPGt12, wireLPGt16};
}
