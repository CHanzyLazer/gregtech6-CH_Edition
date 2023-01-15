package gregtechCH.loaders.a;

import gregapi.data.*;
import gregapi.item.multiitem.food.FoodStatDrink;
import gregapi.oredict.OreDictMaterial;
import gregtech.loaders.a.Loader_Fluids;
import gregtechCH.data.TD_CH;
import net.minecraft.item.EnumAction;
import net.minecraftforge.fluids.Fluid;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigForge.DATA_GTCH;
import static gregtechCH.data.CS_CH.RegType;

/**
 * @author CHanzy
 * Extension of Loader_Fluids
 */
public class Loader_Fluids_CH extends Loader_Fluids {
    /* 采用和 MTE 类似的方法实现不直接修改文件的添加流体 */
    @Override public final void run() {
        if (!DATA_GTCH.enableChangeLoader_Fluids) {super.run(); return;}
        
        /// 修改前标记修改开始
        FL.MODIFYING_CREATE_START();
        
        fluidBeforeLoad();
        fluidBeforeLoadGT6U();
        super.run();
        fluidFinishLoadGT6U();
        fluidFinishLoad();
        
        /// 最后标记修改结束，并进行错误检测
        FL.MODIFYING_CREATE_END();
    }
    
    protected void fluidBeforeLoad() {}
    protected void fluidFinishLoad() {}
    
    
    protected void fluidBeforeLoadGT6U() {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 修改项
//        FL.replaceCreate(                                                "HeliumPlasma"             , "Helium Plasma"       , MT.He                 , STATE_PLASMA, 1000, 4000);
//        FL.replaceCreate(                                                "NitrogenPlasma"           , "Nitrogen Plasma"     , MT.N                  , STATE_PLASMA, 1000, 4000);
        
        /// 添加项
        // plasmas
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "OxygenPlasma"            , "Oxygen Plasma"       , MT.O                  , STATE_PLASMA, 1000, 10000); // 氧等离子体 等离子体在添加时会自动设置亮度到 15，由于设置不太方便这里略去
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "IronPlasma"              , "Iron Plasma"         , MT.Fe                 , STATE_PLASMA, 1000, 10000); // 铁等离子体 等离子体在添加时会自动设置亮度到 15，由于设置不太方便这里略去
        
        // Subatomic particle plasmas
//        FL.appendAddAfter("NitrogenPlasma", RegType.GT6U, "NeutronPlasma"               , "Neutron Plasma"      , MT.n                  , STATE_PLASMA, 1000, 10000); // 中子等离子体
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "ProtonPlasma"            , "Proton Plasma"       , MT.p                  , STATE_PLASMA, 1000, 10000); // 质子等离子体
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "ElectronPlasma"          , "Electron Plasma"     , MT.e                  , STATE_PLASMA, 1000, 10000); // 电子等离子体
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "PositronPlasma"          , "Positron Plasma"     , AM.e                  , STATE_PLASMA, 1000, 10000); // 正电子等离子体
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "AntiProtonPlasma"        , "Anti Proton Plasma"  , AM.p                  , STATE_PLASMA, 1000, 10000); // 反质子等离子体
//        FL.appendAddAfter("NitrogenPlasma", RegType.GT6U, "NeutrinoPlasma"              , "Neutrino Plasma"     , MT.v                  , STATE_PLASMA, 1000, 10000); // 中微子等离子体
//        FL.appendAddAfter("NitrogenPlasma", RegType.GT6U, "AntiNeutrinoPlasma"          , "Anti Neutrino Plasma", AM.v                  , STATE_PLASMA, 1000, 10000); // 反中微子等离子体
        
        // Ionized Plasmas
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "IonizedHydrogen"         , "Ionized Hydrogen"    , MT_CH.H_Ion           , STATE_PLASMA, 1000, 273); // 电离氢
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "IonizedHelium"           , "Ionized Helium"      , MT_CH.He_Ion          , STATE_PLASMA, 1000, 273); // 电离氦
        
        // Anti Elements
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Nitrogen"           , "Anti-Nitrogen"       , AM.N                  , STATE_GASEOUS, 1000, 273); // 反氮
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Hydrogen"           , "Anti-Hydrogen"       , AM.H                  , STATE_GASEOUS, 1000, 273); // 反氢
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Deuterium"          , "Anti-Deuterium"      , AM.D                  , STATE_GASEOUS, 1000, 273); // 反氘
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Tritium"            , "Anti-Tritium"        , AM.T                  , STATE_GASEOUS, 1000, 273); // 反氚
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Helium"             , "Anti-Helium"         , AM.He                 , STATE_GASEOUS, 1000, 273); // 反氦
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Helium_3"           , "Anti-Helium_3"       , AM.He_3               , STATE_GASEOUS, 1000, 273); // 反氦-3
        FL.appendCreateAfter("NitrogenPlasma", RegType.GT6U, "Anti-Carbon_13"          , "Anti-Carbon_13"      , AM.C_13               , STATE_GASEOUS, 1000, 2000); // 反碳-13
        
        // Petrol processing
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "sfuel"                  , "Fuel Oil With Sulfur", null                  , STATE_LIQUID); // 含硫燃油
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "sdiesel"                , "Diesel With Sulfur"  , null                  , STATE_LIQUID); // 含硫柴油
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "spetrol"                , "Petrol With Sulfur"  , null                  , STATE_LIQUID); // 含硫汽油
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "skerosine"              , "Kerosine With Sulfur", null                  , STATE_LIQUID); // 含硫煤油
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "snaphtha"               , "Naphtha With Sulfur" , null                  , STATE_LIQUID); // 含硫石脑油
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "sgasoil"                , "Gas Oil With Sulfur" , null                  , STATE_LIQUID); // 含硫瓦斯油
        
        // Petrol final products (organic chemistry)
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "NitroFuel"              , "Nitro Fuel"          , MT.NitroFuel          , STATE_LIQUID); // 硝基柴油
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "lubricant"              , "Lubricant"           , MT.Lubricant          , STATE_LIQUID); // 润滑油
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "oil"                    , "Oil"                 , MT.Oil                , STATE_LIQUID); // 油
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "fuel"                   , "Fuel Oil"            , MT.Fuel               , STATE_LIQUID); // 燃油
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "kerosine"               , "Kerosine"            , MT.Kerosine           , STATE_LIQUID); // 煤油
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "diesel"                 , "Diesel"              , MT.Diesel             , STATE_LIQUID); // 柴油
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "petrol"                 , "Petrol"              , MT.Petrol             , STATE_LIQUID); // 汽油
        FL.appendCreateAfterLiquid("gas_natural_gas", MT_CH.Naphtha); // 石脑油 // stupid gt6u did not give the texture of Naphtha!!!
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "gasoil"                 , "Gas Oil"             , null                  , STATE_LIQUID); // 瓦斯油
        
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "Propane"                , "Propane"             , MT.Propane            , STATE_GASEOUS); // 丙烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "Butane"                 , "Butane"              , MT.Butane             , STATE_GASEOUS); // 丁烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "Ethylene"               , "Ethylene"            , MT.Ethylene           , STATE_GASEOUS); // 乙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GREG, "Propylene"              , "Propylene"           , MT.Propylene          , STATE_GASEOUS); // 丙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "benzene"                , "Benzene"             , null                  , STATE_LIQUID); // 苯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "lpg"                    , "Liquefied Petroleum Gas",null                , STATE_LIQUID); // 液化石油气
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "phenol"                 , "Phenol"              , null                  , STATE_LIQUID); // 苯酚
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "acetone"                , "Acetone"             , null                  , STATE_LIQUID); // 丙酮
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "Bisphenol"              , "Bisphenol"           , null                  , STATE_LIQUID); // 双酚
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "Epichlorohydrin"        , "Epichlorohydrin"     , null                  , STATE_LIQUID); // 环氧氯丙烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "isoprene"               , "Isoprene"            , null                  , STATE_LIQUID); // 异戊二烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "dichloroisopropanol"    , "Dichloroisopropanol" , null                  , STATE_LIQUID); // 二氯异丙醇
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "cumene"                 , "Cumene"              , null                  , STATE_LIQUID); // 异丙苯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "silane"                 , "Silane"              , null                  , STATE_LIQUID); // 硅烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "allylchloride"          , "Allyl Chloride"      , null                  , STATE_LIQUID); // 氯丙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "acrylicacid"            , "Acrylic Acid"        , null                  , STATE_LIQUID); // 丙烯酸
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "ethylacrylate"          , "Ethyl Acrylate"      , null                  , STATE_LIQUID); // 丙烯酸乙酯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "styrene"                , "Styrene"             , null                  , STATE_LIQUID); // 苯乙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "vinylchloride"          , "Vinyl Chloride"      , null                  , STATE_LIQUID); // 氯乙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "ethyne"                 , "Ethyne"              , null                  , STATE_GASEOUS); // 乙炔
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "photoresist"            , "Photoresist"         , null                  , STATE_LIQUID); // 光刻胶
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "monochlorodifluoromethane","Monochlorodifluoromethane",null             , STATE_GASEOUS); // 二氟一氯甲烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "trichloromethane"       , "Trichloromethane"    , null                  , STATE_GASEOUS); // 氯仿
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "tetrafluoroethylene"    , "Tetrafluoroethylene" , null                  , STATE_GASEOUS); // 四氟乙烯
        
        // Platinum Family Metal processing related fluids 铂族金属加工相关流体
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "solutionrichptpa"       , "Rich Platinum Palladium Solution", null      , STATE_LIQUID); // 富铂钯溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "solutionrichpa"         , "Rich Palladium Solution", null               , STATE_LIQUID); // 富钯溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "moltensodiumbisulfate"  , "Molten Sodium Bisulfate", MT.NaHSO4          , STATE_LIQUID); // 熔融硫酸氢钠
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "rhodiumsulfidesolution" , "Rhodium Sulfide Solution", null              , STATE_LIQUID); // 硫化铑溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "pregnantsolutionosru"   , "Rich Osmium Ruthenium Pregnant Solution",null, STATE_LIQUID); // 富锇钌孕液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "absorptionliquidosru"   , "Rich Osmium Ruthenium Absorption Liquid",null, STATE_LIQUID); // 富锇钌吸收液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "absorptionliquidru"     , "Rich Ruthenium Absorption Liquid", null      , STATE_LIQUID); // 富钌吸收液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "rutheniumtetroxide"     , "Ruthenium Tetroxide" , null                  , STATE_GASEOUS); // 四氧化钌
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "osmiumtetroxide"        , "Osmium Tetroxide"    , null                  , STATE_GASEOUS); // 四氧化锇
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "chlororhodiumacid"      , "Chlororhodium Acid"  , null                  , STATE_LIQUID); // 氯铑酸
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "evaporatedliquidos"     , "Rich Osmium Evaporated Liquid", null         , STATE_LIQUID); // 富锇蒸发液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "sodiumosmiumhexachloride","Ammonium Hexachloroosmium Solution", null    , STATE_LIQUID); // 六氯锇铵溶液
        
        // fluids about bacteria 关于细菌的液体
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "biowaste"               , "Bio Waste"           , null                  , STATE_LIQUID); // 生物废弃物
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "agar"           	    , "Agar"                , null                  , STATE_LIQUID); // 琼脂
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "biomass_flora"          , "Biomass Flora"       , null                  , STATE_LIQUID); // 生物质菌群
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "biomass_diluted"        , "Diluted Biomass"     , null                  , STATE_LIQUID); // 稀释生物质
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "bacteria_medium"        , "Bacteria Culture Medium", null               , STATE_LIQUID); // 细菌培养基
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "yeast"                  , "Yeast"               , null                  , STATE_LIQUID); // 酵母
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "methanogens"            , "Methanogens"         , null                  , STATE_LIQUID); // 产甲烷菌
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "bacillus"               , "Bacillus"            , null                  , STATE_LIQUID); // 芽孢杆菌
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "clostridia"             , "Clostridia"          , null                  , STATE_LIQUID); // 梭状芽胞杆菌
        
        // fluids about bio circuit 关于生物电路的液体
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "mscbasalmedium"         , "MSC Basal Medium"    , null                  , STATE_LIQUID); // MSC 基础培养基
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "mscsupplement"          , "MSC Supplement"      , null                  , STATE_LIQUID); // MSC 补充
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "msc"                    , "MSC(Mesenchymal Stem Cell)", null            , STATE_LIQUID); // MSC(间充质干细胞)
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "bonemarrow"             , "Bone Marrow"         , null                  , STATE_LIQUID); // 骨髓
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "spss"                   , "Stroke-physiological saline solution", null  , STATE_LIQUID); // 中风-生理盐水溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "amylumsolution"         , "Amylum Solution"     , null                  , STATE_LIQUID); // 淀粉溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "vitaminsolution"        , "Vitamin Solution"    , null                  , STATE_LIQUID); // 维生素溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "hormonesolution"        , "Hormone Solution"    , null                  , STATE_LIQUID); // 激素溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "differentiationpromotingproteinneuron", "Differentiation Promoting Protein Neuron", null, STATE_LIQUID); // 促分化蛋白神经元
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "methanol"               , "Methanol"            , null                  , STATE_LIQUID); // 甲醇?
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "coconutoil"             , "Coconut Oil"         , null                  , STATE_LIQUID); // 椰子油
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "methyllaurate"          , "Methyl Laurate"      , null                  , STATE_LIQUID); // 月桂酸甲酯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "methylhydrolaurate"     , "Methyl Hydrolaurate" , null                  , STATE_LIQUID); // 水杨酸甲酯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "tris_hcl"               , "Tris-HCl Buffer"     , null                  , STATE_LIQUID); // Tris-HCl 缓冲液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "aceticacid"             , "Acetic Acid"         , null                  , STATE_LIQUID); // 醋酸
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "edta"                   , "Ethylenediaminetetraacetic acid", null       , STATE_LIQUID); // 乙二胺四乙酸
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "animaltissuehomogenate" , "Animal Tissue Homogenate", null              , STATE_LIQUID); // 动物组织匀浆
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "dnaextract"             , "DNA Extract"         , null                  , STATE_LIQUID); // DNA 提取物
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "dnasolution"            , "DNA Solution"        , null                  , STATE_LIQUID); // DNA 溶液
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "bacteria_differentiationpromotingproteinneuron", "Bacteria(Differentiation Promoting Protein Neuron)", null, STATE_LIQUID); // 细菌（促分化蛋白神经元）
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "protein"                , "Protein"             , null                  , STATE_LIQUID); // 蛋白质
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "dodecane"               , "Dodecane"            , null                  , STATE_LIQUID); // 十二烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "dodecanechloride"       , "Dodecane Chloride"   , null                  , STATE_LIQUID); // 十二烷氯化物
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "monochlorodecane"       , "Monochlorodecane"    , null                  , STATE_LIQUID); // 一氯十烷
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "deoxyribonucleotide"    , "Deoxyribonucleotide" , null                  , STATE_LIQUID); // 脱氧核糖核苷酸
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "primer"                 , "Primer"              , null                  , STATE_LIQUID); // 底漆?
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "taqdnapolymerase"       , "taq DNA polymerase"  , null                  , STATE_LIQUID); // taq DNA聚合酶
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "escherichiacoli"        , "Escherichia coli"    , null                  , STATE_LIQUID); // 大肠杆菌
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "restrictionendonuclease", "Restriction endonuclease", null              , STATE_LIQUID); // 限制性核酸内切酶
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "differentiationpromotingprotein_nervecell_gene", "Differentiation promoting protein (nerve cell) gene", null, STATE_LIQUID); // 促分化蛋白（神经细胞）基因
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "laurylalcohol"          , "Lauryl alcohol"      , null                  , STATE_LIQUID); // 月桂醇
        
        
        // Polymers 聚合物
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "polyvinylchloride"      , "Molten Polyvinylchloride", MT_CH.PVC         , STATE_LIQUID, 1000, 423); //聚氯乙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "polytetrafluoroethylene", "Molten Polytetrafluoroethylene", MT_CH.PTFE  , STATE_LIQUID, 1000, 423); //聚四氟乙烯
        FL.appendCreateAfter("gas_natural_gas", RegType.GT6U, "epoxid"                 , "Molten Epoxid"       , MT_CH.Epoxid          , STATE_LIQUID, 1000, 423); //环氧树脂
        
        // GT6U - others
        FL.appendCreateAfter("liquidoxygen", RegType.GT6U, "liquidhydrogen"           , "Liquid Hydrogen"      , MT.H                  , STATE_LIQUID, 1000, 20); // 液态氢
//        FL.appendAddAfter("liquidoxygen", RegType.GT6U, "electro_etching_solution" , "Electro-Etching Solution", MT_CH.ElectroEtchingSolution, STATE_LIQUID, 1000, 273); // 电蚀刻溶液（已通过 MT 创建）
//        FL.appendAddAfter("liquidoxygen", RegType.GT6U, "peroxydisulfuricacid"     , "Peroxydisulfuric Acid", MT_CH.H2S2O8          , STATE_LIQUID, 1000, 273); // 过氧化二硫酸（已通过 MT 创建）
        FL.appendCreateAfter("liquidoxygen", RegType.GT6U, "mixedester"               , "Ester Mixture"        , null                  , STATE_LIQUID, 1000, 273); // 酯类混合物
        FL.appendCreateAfter("liquidoxygen", RegType.GT6U, "aquaticwaste"             , "Aquatic Waste Water"  , null                  , STATE_LIQUID, 1000, 273); // 水生废水
        FL.appendCreateAfter("liquidoxygen", RegType.GT6U, "aquaticnutrition"         , "Aquatic Nutrition Water", null                , STATE_LIQUID, 1000, 273); // 水生营养水
        
        
        /// 移除项
        // 用来改变这些流体的注册顺序，这里主要是移除冗余注册
        FL.removeCreate("NitroFuel");
        FL.removeCreate("lubricant");
        FL.removeCreate("oil");
        FL.removeCreate("fuel");
        FL.removeCreate("kerosine");
        FL.removeCreate("diesel");
        FL.removeCreate("petrol");
        FL.removeCreate("Propane");
        FL.removeCreate("Butane");
        FL.removeCreate("Ethylene");
        FL.removeCreate("Propylene");
    }
    
    protected void fluidFinishLoadGT6U() {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 不考虑顺序的最后添加
        for (OreDictMaterial tMaterial : MT.ALL_MATERIALS_REGISTERED_HERE) {
            if (tMaterial.contains(TD_CH.ItemGenerator.LIQUID_CH)) FL.createLiquid(RegType.GTCH, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.MOLTEN_CH)) FL.createMolten(RegType.GTCH, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.GASES_CH )) FL.createGas   (RegType.GTCH, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.VAPORS_CH)) FL.createVapour(RegType.GTCH, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.PLASMA_CH)) FL.createPlasma(RegType.GTCH, tMaterial);
            
            if (tMaterial.contains(TD_CH.ItemGenerator.LIQUID_6U)) FL.createLiquid(RegType.GT6U, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.MOLTEN_6U)) FL.createMolten(RegType.GT6U, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.GASES_6U )) FL.createGas   (RegType.GT6U, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.VAPORS_6U)) FL.createVapour(RegType.GT6U, tMaterial);
            if (tMaterial.contains(TD_CH.ItemGenerator.PLASMA_6U)) FL.createPlasma(RegType.GT6U, tMaterial);
        }
        
        /// 附加属性
        Fluid
        tFluid = FL.fluid("propane");   if (tFluid != null) tFluid.setDensity(-1000);
        tFluid = FL.fluid("butane");    if (tFluid != null) tFluid.setDensity(-1000);
        tFluid = FL.fluid("lubricant"); if (tFluid != null) new FoodStatDrink(tFluid, "Industrial Use ONLY!",  0, 0.0F  ,   0, C+37,  0.00F,  0,  0,  0,  0,  0, EnumAction.drink, T, F, F, PotionsGT.ID_SLIPPERY, 300, 1,  90);
    }
}
