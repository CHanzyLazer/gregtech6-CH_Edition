package gregtechCH.config.machine;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.ANY;
import gregapi.data.MT;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataMachines_CH {
    // Burning Boxes
    @JSONField(ordinal = 0)
    public Map<String, AttributesBurningBoxBrick_CH> BurningBoxBrick = new LinkedHashMap<>();
    @JSONField(ordinal = 1)
    public Map<String, AttributesBurningBoxSolid_CH> BurningBoxSolid = new LinkedHashMap<>();
    @JSONField(ordinal = 2)
    public Map<String, AttributesDenseBurningBoxSolid_CH> DenseBurningBoxSolid = new LinkedHashMap<>();
    // Steam Boilers
    @JSONField(ordinal = 9)
    public Map<String, AttributesSteamBoilerTank_CH> SteamBoilerTank = new LinkedHashMap<>();
    @JSONField(ordinal = 10)
    public Map<String, AttributesStrongSteamBoilerTank_CH> StrongSteamBoilerTank = new LinkedHashMap<>();
    // Steam Engines
    @JSONField(ordinal = 11)
    public Map<String, AttributesSteamEngine_CH> SteamEngine = new LinkedHashMap<>();
    @JSONField(ordinal = 12)
    public Map<String, AttributesStrongSteamEngine_CH> StrongSteamEngine = new LinkedHashMap<>();
    // Burning Boxes Liquid
    @JSONField(ordinal = 3)
    public Map<String, AttributesBurningBoxLiquid_CH> BurningBoxLiquid = new LinkedHashMap<>();
    @JSONField(ordinal = 4)
    public Map<String, AttributesDenseBurningBoxLiquid_CH> DenseBurningBoxLiquid = new LinkedHashMap<>();
    // Burning Boxes Gas
    @JSONField(ordinal = 5)
    public Map<String, AttributesBurningBoxGas_CH> BurningBoxGas = new LinkedHashMap<>();
    @JSONField(ordinal = 6)
    public Map<String, AttributesDenseBurningBoxGas_CH> DenseBurningBoxGas = new LinkedHashMap<>();
    @JSONField(ordinal = 7)
    public Map<String, AttributesBurningBoxFluidizedBed_CH> BurningBoxFluidizedBed = new LinkedHashMap<>();
    @JSONField(ordinal = 8)
    public Map<String, AttributesDenseBurningBoxFluidizedBed_CH> DenseBurningBoxFluidizedBed = new LinkedHashMap<>();
    //Heat Exchangers
    @JSONField(ordinal = 13)
    public Map<String, AttributesHeatExchanger_CH> HeatExchanger = new LinkedHashMap<>();
    @JSONField(ordinal = 14)
    public Map<String, AttributesDenseHeatExchanger_CH> DenseHeatExchanger = new LinkedHashMap<>();


    public DataMachines_CH() {}
    public void initDefault() {
        // Burning Boxes
        String[] availableMaterials = {MT.Brick.mNameInternal};
        BurningBoxBrick.clear();
        for (String mtName : availableMaterials) {
            BurningBoxBrick.put(mtName, new AttributesBurningBoxBrick_CH(mtName));
        }
        availableMaterials = new String[]{
                MT.Pb.mNameInternal, MT.Bi.mNameInternal, MT.Bronze.mNameInternal,      MT.Invar.mNameInternal, ANY.Steel.mNameInternal,
                MT.Cr.mNameInternal, MT.Ti.mNameInternal, MT.Netherite.mNameInternal,   ANY.W.mNameInternal,    MT.TungstenSteel.mNameInternal,
                MT.Ta4HfC5.mNameInternal};
        BurningBoxSolid.clear();
        for (String mtName : availableMaterials) {
            BurningBoxSolid.put(mtName, new AttributesBurningBoxSolid_CH(mtName));
        }
        DenseBurningBoxSolid.clear();
        for (String mtName : availableMaterials) {
            DenseBurningBoxSolid.put(mtName, new AttributesDenseBurningBoxSolid_CH(mtName));
        }
        // Steam Boilers
        availableMaterials = new String[]{
                MT.Pb.mNameInternal, MT.Bi.mNameInternal, MT.Bronze.mNameInternal,      MT.Invar.mNameInternal, ANY.Steel.mNameInternal,
                MT.Cr.mNameInternal, MT.Ti.mNameInternal, MT.Netherite.mNameInternal,   ANY.W.mNameInternal,    MT.TungstenSteel.mNameInternal};
        SteamBoilerTank.clear();
        for (String mtName : availableMaterials) {
            SteamBoilerTank.put(mtName, new AttributesSteamBoilerTank_CH(mtName));
        }
        StrongSteamBoilerTank.clear();
        for (String mtName : availableMaterials) {
            StrongSteamBoilerTank.put(mtName, new AttributesStrongSteamBoilerTank_CH(mtName));
        }
        // Steam Engines
        availableMaterials = new String[]{
                MT.Pb.mNameInternal,        MT.TinAlloy.mNameInternal,      MT.Bronze.mNameInternal,        MT.Brass.mNameInternal, MT.Invar.mNameInternal,
                MT.IronWood.mNameInternal,  ANY.Steel.mNameInternal,        MT.FierySteel.mNameInternal,    MT.Cr.mNameInternal,    MT.Ti.mNameInternal,
                ANY.W.mNameInternal,        MT.TungstenSteel.mNameInternal};
        SteamEngine.clear();
        for (String mtName : availableMaterials) {
            SteamEngine.put(mtName, new AttributesSteamEngine_CH(mtName));
        }
        StrongSteamEngine.clear();
        for (String mtName : availableMaterials) {
            StrongSteamEngine.put(mtName, new AttributesStrongSteamEngine_CH(mtName));
        }
        // Burning Boxes Liquid
        availableMaterials = new String[]{
                MT.Bronze.mNameInternal,        MT.Invar.mNameInternal,     ANY.Steel.mNameInternal,        MT.Cr.mNameInternal,        MT.Ti.mNameInternal,
                MT.Netherite.mNameInternal,     ANY.W.mNameInternal,        MT.TungstenSteel.mNameInternal, MT.Ta4HfC5.mNameInternal};
        BurningBoxLiquid.clear();
        for (String mtName : availableMaterials) {
            BurningBoxLiquid.put(mtName, new AttributesBurningBoxLiquid_CH(mtName));
        }
        DenseBurningBoxLiquid.clear();
        for (String mtName : availableMaterials) {
            DenseBurningBoxLiquid.put(mtName, new AttributesDenseBurningBoxLiquid_CH(mtName));
        }
        // Burning Boxes Gas
        BurningBoxGas.clear();
        for (String mtName : availableMaterials) {
            BurningBoxGas.put(mtName, new AttributesBurningBoxGas_CH(mtName));
        }
        DenseBurningBoxGas.clear();
        for (String mtName : availableMaterials) {
            DenseBurningBoxGas.put(mtName, new AttributesDenseBurningBoxGas_CH(mtName));
        }
        // Burning Boxes Fluidized
        availableMaterials = new String[]{
                MT.Pb.mNameInternal,        MT.Bi.mNameInternal,      MT.Bronze.mNameInternal,      MT.Invar.mNameInternal,         ANY.Steel.mNameInternal,
                MT.Cr.mNameInternal,        MT.Ti.mNameInternal,      MT.Netherite.mNameInternal,   ANY.W.mNameInternal,            MT.TungstenSteel.mNameInternal,
                MT.Ta4HfC5.mNameInternal};
        BurningBoxFluidizedBed.clear();
        for (String mtName : availableMaterials){
            BurningBoxFluidizedBed.put(mtName, new AttributesBurningBoxFluidizedBed_CH(mtName));
        }
        DenseBurningBoxFluidizedBed.clear();
        for (String mtName : availableMaterials){
            DenseBurningBoxFluidizedBed.put(mtName, new AttributesDenseBurningBoxFluidizedBed_CH(mtName));
        }
        // Heat Exchanger
        availableMaterials = new String[]{
                MT.Invar.mNameInternal,     ANY.W.mNameInternal,        MT.TungstenSteel.mNameInternal,     MT.Ta4HfC5.mNameInternal};
        HeatExchanger.clear();
        for (String mtName : availableMaterials){
            HeatExchanger.put(mtName, new AttributesHeatExchanger_CH(mtName));
        }
        for (String mtName : availableMaterials){
            DenseHeatExchanger.put(mtName, new AttributesDenseHeatExchanger_CH(mtName));
        }
    }
}
