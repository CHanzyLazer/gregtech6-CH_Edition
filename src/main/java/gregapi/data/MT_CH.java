package gregapi.data;

import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.TextureSet;
import gregtechCH.data.OP_CH;

import static gregapi.data.CS.*;
import static gregapi.data.CS.T;
import static gregapi.data.TD.Atomic.*;
import static gregapi.data.TD.Compounds.APPROXIMATE;
import static gregapi.data.TD.ItemGenerator.*;
import static gregapi.data.TD.Processing.*;
import static gregapi.data.TD.Properties.*;
import static gregapi.render.TextureSet.*;
import static gregtechCH.data.TD_CH.ItemGenerator.*;

/* 额外添加的材料统一放在这里
* 使用原有的接口但是由于不希望改动访问权限因此只能放到同一 package 下
* 由于 MT 太大了（会编译失败），因此这些结构都放到另一个文件下
* 由于不同的来源 ID 有明确的区域，因此直接在 OreDictMaterial 中确定来源以及处理语言文件 */
@SuppressWarnings("ConstantConditions")
public class MT_CH extends MT {
    
    /* GT6U stuff */
    // 提供特殊的元素创建方式
    static OreDictMaterial element      (int aID, String aNameOreDict, String aSymbol, long aProtons, long aElectrons, long aNeutrons, long aMeltingPoint, long aBoilingPoint, double aGramPerCubicCentimeter, TextureSet[] aSets, long aR, long aG, long aB, long aA, Object... aRandomData) {return create      (aID, aNameOreDict, aSets, aR, aG, aB, aA, aRandomData).setStatsElement(aProtons, aElectrons, aNeutrons, 0, aGramPerCubicCentimeter).heat(aMeltingPoint, aBoilingPoint).put(ELEMENT).tooltip(aSymbol);}
    static OreDictMaterial element      (int aID, String aNameOreDict, String aSymbol, long aProtons, long aElectrons, long aNeutrons, TextureSet[] aSets, long aR, long aG, long aB, long aA, Object... aRandomData) {return element(aID, aNameOreDict, aSymbol, aProtons, aElectrons, aNeutrons, 1, 1, 0, aSets, aR, aG, aB, aA, aRandomData);}
    /* 15000-15999 For GregTech6-Unofficial */
    public static final OreDictMaterial
    // 碳化铌钛 // MARK ADDED UNBURNABLE; Nb2Ti3C4 -> Nb2Ti3C5; REMOVED alloyCentrifuge()
    Nb2Ti3C5                = alloymachine  (15000, "Titanium Niobium Carbide"                      , 153, 153, 204     , MOLTEN, UNBURNABLE).qual(3, 16.0, 7680, 4).uumMcfg( 0, Nb, 2*U, Ti, 3*U, C, 5*U).aspects(TC.METALLUM, 2, TC.GELUM, 2).heat(4196),
    
    
    // 磷酸
    H3PO4                   = lqudaciddcmp  (15200, "Phosphoric Acid"                               , 255, 255,  61, 255, LIQUID).uumMcfg( 0, H, 3*U, P, 1*U, O, 4*U).heat( 200,  400), PhosphoricAcid = H3PO4,
    // 石脑油 // MARK lquddcmp -> lqudexpl
    Naphtha                 = lqudexpl      (15201, "Naphtha"                                       , 255, 255, 102, 255).heat( 100,  400),
    // 电离氢 // MARK lquddcmp -> element // TODO '+' 以后自动生成
    H_Ion                   = element       (15202, "Ionized Hydrogen", "H+"                , 1, 0, 1, SET_PLASMA, 255, 255, 102, 255), H_p = H_Ion,
    // 电离氦 // MARK lquddcmp -> element
    He_Ion                  = element       (15203, "Ionized Helium", "He++"                , 2, 0, 2, SET_PLASMA, 255, 255, 102, 255), He_pp = He_Ion, Alpha = He_pp,
    // 电蚀刻溶液
    ElectroEtchingSolution  = lqudacidcent  (15204, "Electro-Etching Solution"                      , 193, 255, 193, 255, LIQUID).uumMcfg( 0, H2O, 3*U, HCl, 1*U, H2O2, 2*U).heat( 200,  400),
    // 过氧化二硫酸
    H2S2O8                  = lqudaciddcmp  (15205, "Peroxydisulfuric Acid"                         , 255, 250, 205, 255, LIQUID).uumMcfg( 0, H  , 2*U, S  , 2*U, O   , 8*U).heat( 200,  400),
    // 五氟化磷 // MARK GASSES -> GASES （去掉过时用法）; RGB 241, 220, 207 -> 211, 241, 181
    PF5                     = gaschemelec   (15206, "Phosphorus Pentafluoride"                      , 211, 241, 181, 100, GASES).uumMcfg( 0, P  , 1*U, F  , 5*U),
    // 碳酰氯 // MARK GASSES -> GASES; RGB 241, 220, 207 -> 143, 209, 123
    COCl2                   = gaschemelec   (15207, "Phosgen"                                       , 143, 209, 123, 100, GASES).uumMcfg( 0, C  , 1*U, O  , 1*U, Cl  , 2*U),
    // 碳酸二乙酯 // MARK aA 100 -> 255
    C5H10O3                 = lqudchemelec  (15208, "Diethyl Carbonate"                             , 241, 220, 207, 255, LIQUID).uumMcfg( 0, C  , 5*U, H  , 10*U,O   , 3*U),
    // 碳酸甲酯乙酯 // MARK aA 100 -> 255
    C4H8O3                  = lqudchemelec  (15209, "Ethyl Methyl Carbonate"                        , 241, 200, 207, 255, LIQUID).uumMcfg( 0, C  , 4*U, H  , 8*U, O   , 3*U),
    // 碳酸二甲酯 // MARK aA 100 -> 255
    C3H6O3                  = lqudchemelec  (15210, "Dimethyl Carbonate"                            , 241, 180, 207, 255, LIQUID).uumMcfg( 0, C  , 3*U, H  , 6*U, O   , 3*U),
    
    // TODO 塑料的熔融态在量杯中不显示（Polytetrafluoroethylene icon 透明，opengl bug?），坩埚和实际的颜色不同啊啊啊
    // 聚四氟乙烯 // MARK aDurability 32 -> 256
    PTFE                    = create        (15400, "Polytetrafluoroethylene",SET_DULL              , 250, 250, 250, 255, G_INGOT_MACHINE, APPROXIMATE, FLAMMABLE, EXTRUDER, EXTRUDER_SIMPLE, WIRES, MORTAR, BOUNCY, BRITTLE, FURNACE).uumMcfg( 0, C, 2*U, F, 4*U)              .aspects(TC.MOTUS, 2).heat(423).setBurning(Ash, U9).setSmelting(null, 2*U3).qual(1, 3.0, 256, 1),
    // 聚乙烯 // MARK aDurability 32 -> 256
    PE                      = create        (15401, "Polyethylene"          , SET_DULL              , 200, 200, 200, 255, G_INGOT_MACHINE, APPROXIMATE, FLAMMABLE, EXTRUDER, EXTRUDER_SIMPLE, WIRES, MORTAR, BOUNCY, BRITTLE, FURNACE).uumMcfg( 0, C, 1*U, H, 2*U)              .aspects(TC.MOTUS, 2).heat(423).setBurning(Ash, U9).setSmelting(null, 2*U3).qual(1, 3.0, 256, 1),
    // 聚氯乙烯 // MARK aDurability 32 -> 256
    PVC                     = create        (15402, "Polyvinylchloride"     , SET_DULL              , 144, 238, 144, 255, G_INGOT_MACHINE, APPROXIMATE, FLAMMABLE, EXTRUDER, EXTRUDER_SIMPLE, WIRES, MORTAR, BOUNCY, BRITTLE, FURNACE).uumMcfg( 0, C, 2*U, H, 3*U, Cl   , 1*U)  .aspects(TC.MOTUS, 2).heat(600).setBurning(Ash, U9).setSmelting(null, 2*U3).qual(1, 3.0, 256, 1),
    // 环氧树脂 // MARK aDurability 32 -> 256
    Epoxid                  = create        (15403, "Epoxid"                , SET_DULL              , 192, 255,  62, 255, G_INGOT_MACHINE, APPROXIMATE, FLAMMABLE, EXTRUDER, EXTRUDER_SIMPLE, WIRES, MORTAR, BOUNCY, BRITTLE, FURNACE).uumMcfg( 0, C, 11*U,H, 12*U, O   , 3*U)  .aspects(TC.MOTUS, 2).heat(423).setBurning(Ash, U9).setSmelting(null, 2*U3).qual(1, 3.0, 256, 1),
    
    
    // 铬酸钠 // MARK ADD MELTING POINT
    Na2CrO4                 = dustdcmp      (15600, "Sodium Chromate"       , SET_FINE              , 255, 255,   0, 255, MELTING).setMcfg( 0, Na, 2*U, Cr, 1*U, O , 4*U).heat(1065),
    // 重铬酸钠 // MARK ADD MELTING POINT
    Na2Cr2O7                = dustdcmp      (15601, "Sodium Dichromate"     , SET_FINE              , 255,  69,   0, 255, MELTING).setMcfg( 0, Na, 2*U, Cr, 2*U, O , 7*U).heat(630, 673),
    // 氧化铬 // MARK ADD MELTING POINT
    Cr2O3                   = dustdcmp      (15602, "Chromium Oxide"        , SET_FINE              ,   0, 139,   0, 255, MELTING).setMcfg( 0, Cr, 2*U, O , 3*U).heat(2708, 4270),
    // 氯化铝 // MARK ADD MELTING POINT
    AlCl3                   = dustdcmp      (15603, "Aluminium Chloride"    , SET_FINE              , 255, 250, 205, 255, MELTING).setMcfg( 0, Al, 1*U, Cl, 3*U).heat(453),
    // 五氧化二磷 // MARK ADD MELTING POINT; ADD MELTING
    P2O5                    = dustdcmp      (15604, "Phosphorus Pentoxide"  , SET_DULL              , 255, 250, 205, 255, MELTING).setMcfg( 0, P , 2*U, O , 5*U).heat(613, 633),
    // 氟化锂 // MARK ADD MELTING POINT
    LiF                     = dustdcmp      (15605, "Lithium Fluoride"      , SET_CUBE              , 204, 218, 218, 255, MELTING).setMcfg( 0, Li, 1*U, F , 1*U).heat(1118, 1949),
    // 六氟磷酸锂 // MARK ADD MELTING POINT; ADD MELTING
    LiPF6                   = dustdcmp      (15606, "Lithium Hexafluorophosphate", SET_FINE         , 229, 209, 226, 255, MELTING).setMcfg( 0, Li, 1*U, P , 1*U, F, 6*U).heat(473),
    // 氧化钙 // MARK ADD MELTING POINT
    CaO                     = dustdcmp      (15607, "Calcium Oxide"         , SET_FINE              , 255, 255, 255, 255, MELTING).setMcfg( 0, Ca, 1*U, O , 1*U).heat(2886, 3120);
    
    public static final OreDictMaterial
    // 椰子食物，和原版的椰子木不同
    Coconut                 = dustfood      (15777, "Coconut" ,                                             240, 150, 140, 255, TICKS_PER_SMELT/2).aspects(TC.FAMES, 1).setOriginalMod(MD.BINNIE),
    // 希格斯玻色子 // MARK ID 6 -> 15906
    HB      , HiggsBoson                =   HB      = create(15906, "Higgs-Boson").setStatsElement( 0, 0, 0, 0, 0).heat(0,0,0).setRGBa(255, 255, 255, 255).put(PARTICLE).tooltip("HB").hide();
    
    public static class TECH {
        private static boolean INITIALIZED = false;
        static void init() {
            if (INITIALIZED) return;
            INITIALIZED = true;
            
            /* 标记来源 mod，greg 内部使用 */
            Nb2Ti3C5.put(MD.GT6U);
            H3PO4.put(MD.GT6U);
            Naphtha.put(MD.GT6U);
            H_Ion.put(MD.GT6U);
            He_Ion.put(MD.GT6U);
            ElectroEtchingSolution.put(MD.GT6U);
            H2S2O8.put(MD.GT6U);
            PF5.put(MD.GT6U);
            COCl2.put(MD.GT6U);
            C5H10O3.put(MD.GT6U);
            C4H8O3.put(MD.GT6U);
            C3H6O3.put(MD.GT6U);
            PTFE.put(MD.GT6U);
            PE.put(MD.GT6U);
            PVC.put(MD.GT6U);
            Epoxid.put(MD.GT6U);
            Na2CrO4.put(MD.GT6U);
            Na2Cr2O7.put(MD.GT6U);
            Cr2O3.put(MD.GT6U);
            AlCl3.put(MD.GT6U);
            P2O5.put(MD.GT6U);
            LiF.put(MD.GT6U);
            LiPF6.put(MD.GT6U);
            CaO.put(MD.GT6U);
            HB.put(MD.GT6U);
            
            /* 其他对原版材料的修改 */
            Proton.put(FUSION);
//            Li_6.put(MOLTEN_6U); // 不必要
//            Be_7.put(MOLTEN_6U); // 不必要
            Ru.put(MOLTEN_6U);
            Rh.put(MOLTEN_6U);
            Hf.put(MOLTEN_6U);
            Ta.put(MOLTEN_6U);
//            U_238.put(MOLTEN_6U); // 不必要
//            U_235.put(MOLTEN_6U); // 不必要
            Pu.put(MOLTEN_6U);
            Pu_241.put(MOLTEN_6U);
            Pu_243.put(MOLTEN_6U);
            Pu_239.put(MOLTEN_6U);
            Am.put(MOLTEN_6U);
            Am_241.put(MOLTEN_6U);
            // MARK Tn Dn 不再进行熔沸点调整
            // MARK Sand SoulSand SluiceSand 依旧保留为 dust
//            OREMATS.Chromite.addOreByProducts(MnO2 , Cr , Mg , OREMATS.Bromargyrite ); // MARK 这是副产物表所以不用修改
            // TODO 暂不去除 Chromite 的 .setSmelting(Cr , 2*U9)
        }
    }
    public static class DATA {
        public static final OreDictItemData[]
        FIBERCABLES_04 = {
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor),
            OP_CH.cableLPGt04.dat(Superconductor)
        },
    
        FIBERCABLES_01 = {
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor),
            OP_CH.cableLPGt01.dat(Superconductor)
        };
    }
}
