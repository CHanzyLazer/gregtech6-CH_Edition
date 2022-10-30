package gregtechCH.data;


import gregapi.data.RM;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.recipes.maps.RecipeMapFormingPress;
import gregtechCH.recipes.maps.RecipeMapFuelCleaner;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.RegType;

/**
 * 额外添加的 Recipe Maps 统一放在这里
 * 目前修改只是添加一个同名的 RM，当一切完善后注释掉原本的声明
 **/
public class RM_CH extends RM {
    /* GT6U stuff */
    public static final RecipeMap
      HeatMixer         = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.heatmixer"                    , "Heat Mixer"                      , null, 0, 1, RES_PATH_GUI+"machines/HeatMixer"         ,/*IN-OUT-MIN-ITEM=*/ 6, 6, 0,/*IN-OUT-MIN-FLUID=*/ 6, 6, 0,/*MIN*/ 2,/*AMP=*/ 1, ""                    ,   1, ""       , T, T, T, T, F, T, T)
    , Fermenter         = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.fermenter"                    , "Fermenter"                       , null, 0, 1, RES_PATH_GUI+"machines/Fermenter"         ,/*IN-OUT-MIN-ITEM=*/ 2, 1, 1,/*IN-OUT-MIN-FLUID=*/ 2, 1, 0,/*MIN*/ 1,/*AMP=*/ 1, ""                    ,   1, ""       , T, T, T, T, F, T, T)
    , Press             = new RecipeMapFormingPress (RegType.GT6U, null, "gt6u.recipe.press"                        , "Press"                           , null, 0, 1, RES_PATH_GUI+"machines/Press"             ,/*IN-OUT-MIN-ITEM=*/ 3, 1, 2,/*IN-OUT-MIN-FLUID=*/ 1, 1, 0,/*MIN*/ 0,/*AMP=*/ 1, ""                    , 1, ""         , T, T, T, T, F, T, T)
    , OilCleaner        = new RecipeMapFuelCleaner  (RegType.GT6U, null, "gt6u.recipe.oilcleaner"                   , "Oil Cleaner"                     , null, 0, 1, RES_PATH_GUI+"machines/Mixer"             ,/*IN-OUT-MIN-ITEM=*/ 6, 1, 0,/*IN-OUT-MIN-FLUID=*/ 6, 2, 0,/*MIN*/ 2,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , Fluidsolidifier   = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.fluidsolidifier"              , "Fluid Solidifier"                , null, 0, 1, RES_PATH_GUI+"machines/Generifier"        ,/*IN-OUT-MIN-ITEM=*/ 1, 1, 0,/*IN-OUT-MIN-FLUID=*/ 1, 1, 0,/*MIN*/ 1,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , CrackingTower     = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.crackingtower"                , "Cracking Tower"                  , null, 0, 1, RES_PATH_GUI+"machines/SteamCracking"     ,/*IN-OUT-MIN-ITEM=*/ 1, 3, 0,/*IN-OUT-MIN-FLUID=*/ 2, 9, 1,/*MIN*/ 2,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , ParticleCollider  = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.particlecollider"             , "Particle Collider"               , null, 0, 1, RES_PATH_GUI+"machines/Fusion"            ,/*IN-OUT-MIN-ITEM=*/ 2, 6, 1,/*IN-OUT-MIN-FLUID=*/ 2, 6, 0,/*MIN*/ 2,/*AMP=*/ 1, "Start: "             ,    1, " EU"   , T, T, T, T, F, F, F)
    , Ionizer           = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.ionizer"                      , "Ionizer"                         , null, 0, 1, RES_PATH_GUI+"machines/Ionizer"           ,/*IN-OUT-MIN-ITEM=*/ 1, 1, 0,/*IN-OUT-MIN-FLUID=*/ 1, 1, 0,/*MIN*/ 1,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , RPP               = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.radiationprocessing"          , "radiationprocessing"             , null, 0, 1, RES_PATH_GUI+"machines/Lightning"         ,/*IN-OUT-MIN-ITEM=*/ 6, 6, 0,/*IN-OUT-MIN-FLUID=*/ 6, 6, 0,/*MIN*/ 2,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , MaskAligner       = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.maskaligner"                  , "Mask Aligner"                    , null, 0, 1, RES_PATH_GUI+"machines/MaskAligner"       ,/*IN-OUT-MIN-ITEM=*/ 9, 1, 2,/*IN-OUT-MIN-FLUID=*/ 1, 0, 0,/*MIN*/ 2,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , LaserCutter       = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.lasercutter"                  , "Laser Cutter"                    , null, 0, 1, RES_PATH_GUI+"machines/LaserCutter"       ,/*IN-OUT-MIN-ITEM=*/ 1, 3, 1,/*IN-OUT-MIN-FLUID=*/ 0, 0, 0,/*MIN*/ 0,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , Incubator         = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.incubator"                    , "Incubator"                       , null, 0, 1, RES_PATH_GUI+"machines/Incubator"         ,/*IN-OUT-MIN-ITEM=*/ 3, 3, 1,/*IN-OUT-MIN-FLUID=*/ 3, 3, 0,/*MIN*/ 1,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , Well              = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.well"                         , "Well"                            , null, 0, 1, RES_PATH_GUI+"machines/Generifier"        ,/*IN-OUT-MIN-ITEM=*/ 1, 1, 1,/*IN-OUT-MIN-FLUID=*/ 1, 1, 0,/*MIN*/ 1,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, F, F)
    , BioLab            = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.biolab"                       , "Biochemical Research Lab"        , null, 0, 1, RES_PATH_GUI+"machines/BioLab"            ,/*IN-OUT-MIN-ITEM=*/ 6, 6, 0,/*IN-OUT-MIN-FLUID=*/ 6, 6, 0,/*MIN*/ 1,/*AMP=*/ 1, "Disinfection: "      ,    1, " LU"   , T, T, T, T, F, F, F)
    , AquaticFarm       = new RecipeMap             (RegType.GT6U, null, "gt6u.recipe.aquaticfarm"                  , "Aquatic Farm"                    , null, 0, 1, RES_PATH_GUI+"machines/Sluice"            ,/*IN-OUT-MIN-ITEM=*/ 1, 9, 1,/*IN-OUT-MIN-FLUID=*/ 1, 1, 1,/*MIN*/ 2,/*AMP=*/ 1, ""                    ,    1, ""      , T, T, T, T, F, T, T)
    ;
    // MARK: biomass 的修改应该只是旧版 greg 修改没有来得及更新
}
