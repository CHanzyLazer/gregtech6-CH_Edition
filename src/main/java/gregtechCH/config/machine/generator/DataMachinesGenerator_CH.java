package gregtechCH.config.machine.generator;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.DataJson_CH;
import gregtechCH.config.machine.steam.DataMachinesSteam_CH;

import java.util.ArrayList;
import java.util.List;

public class DataMachinesGenerator_CH extends DataJson_CH {
    // Burning Boxes
    @JSONField(ordinal = 0)
    public List<AttributesBurningBoxBrick_CH> BurningBoxBrick = new ArrayList<>();
    @JSONField(ordinal = 1)
    public List<AttributesBurningBoxSolid_CH> BurningBoxSolid = new ArrayList<>();
    @JSONField(ordinal = 2)
    public List<AttributesDenseBurningBoxSolid_CH> DenseBurningBoxSolid = new ArrayList<>();
    // Burning Boxes Liquid
    @JSONField(ordinal = 3)
    public List<AttributesBurningBoxLiquid_CH> BurningBoxLiquid = new ArrayList<>();
    @JSONField(ordinal = 4)
    public List<AttributesDenseBurningBoxLiquid_CH> DenseBurningBoxLiquid = new ArrayList<>();
    // Burning Boxes Gas
    @JSONField(ordinal = 5)
    public List<AttributesBurningBoxGas_CH> BurningBoxGas = new ArrayList<>();
    @JSONField(ordinal = 6)
    public List<AttributesDenseBurningBoxGas_CH> DenseBurningBoxGas = new ArrayList<>();
    // Burning Boxes Fluidized Bed
    @JSONField(ordinal = 7)
    public List<AttributesBurningBoxFluidizedBed_CH> BurningBoxFluidizedBed = new ArrayList<>();
    @JSONField(ordinal = 8)
    public List<AttributesDenseBurningBoxFluidizedBed_CH> DenseBurningBoxFluidizedBed = new ArrayList<>();
    // Heat Exchangers
    @JSONField(ordinal = 9)
    public List<AttributesHeatExchanger_CH> HeatExchanger = new ArrayList<>();
    @JSONField(ordinal = 10)
    public List<AttributesDenseHeatExchanger_CH> DenseHeatExchanger = new ArrayList<>();
    // Diesel Engines
    @JSONField(ordinal = 16)
    public List<AttributesDieselEngine_CH> DieselEngine = new ArrayList<>();


    public DataMachinesGenerator_CH() {}

    @Override
    public void initDefault() {
        // Burning Boxes
        OreDictMaterial[] availableMaterials = {MT.Brick};
        BurningBoxBrick.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            BurningBoxBrick.add(new AttributesBurningBoxBrick_CH(tMat));
        }
        availableMaterials = new OreDictMaterial[]{
                MT.Pb,      MT.Bi,  MT.Bronze,      MT.Invar, ANY.Steel,
                MT.Cr,      MT.Ti,  MT.Netherite,   ANY.W,    MT.TungstenSteel,
                MT.Ta4HfC5};
        BurningBoxSolid.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            BurningBoxSolid.add(new AttributesBurningBoxSolid_CH(tMat));
        }
        DenseBurningBoxSolid.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            DenseBurningBoxSolid.add(new AttributesDenseBurningBoxSolid_CH(tMat));
        }
        // Burning Boxes Liquid
        availableMaterials = new OreDictMaterial[]{
                MT.Bronze,        MT.Invar,     ANY.Steel,        MT.Cr,        MT.Ti,
                MT.Netherite,     ANY.W,        MT.TungstenSteel, MT.Ta4HfC5};
        BurningBoxLiquid.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            BurningBoxLiquid.add(new AttributesBurningBoxLiquid_CH(tMat));
        }
        DenseBurningBoxLiquid.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            DenseBurningBoxLiquid.add(new AttributesDenseBurningBoxLiquid_CH(tMat));
        }
        // Burning Boxes Gas
        BurningBoxGas.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            BurningBoxGas.add(new AttributesBurningBoxGas_CH(tMat));
        }
        DenseBurningBoxGas.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            DenseBurningBoxGas.add(new AttributesDenseBurningBoxGas_CH(tMat));
        }
        // Burning Boxes Fluidized
        availableMaterials = new OreDictMaterial[]{
                MT.Pb,        MT.Bi,      MT.Bronze,      MT.Invar,         ANY.Steel,
                MT.Cr,        MT.Ti,      MT.Netherite,   ANY.W,            MT.TungstenSteel,
                MT.Ta4HfC5};
        BurningBoxFluidizedBed.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            BurningBoxFluidizedBed.add(new AttributesBurningBoxFluidizedBed_CH(tMat));
        }
        DenseBurningBoxFluidizedBed.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            DenseBurningBoxFluidizedBed.add(new AttributesDenseBurningBoxFluidizedBed_CH(tMat));
        }
        // Heat Exchanger
        availableMaterials = new OreDictMaterial[]{
                MT.Invar,     ANY.W,        MT.TungstenSteel,     MT.Ta4HfC5};
        HeatExchanger.clear();
        for (OreDictMaterial tMat : availableMaterials){
            HeatExchanger.add(new AttributesHeatExchanger_CH(tMat));
        }
        for (OreDictMaterial tMat : availableMaterials){
            DenseHeatExchanger.add(new AttributesDenseHeatExchanger_CH(tMat));
        }
        // Diesel Engines
        availableMaterials = new OreDictMaterial[]{
                MT.Bronze,    ANY.Steel,    MT.Invar,     MT.Ti,    MT.TungstenSteel,     MT.Ir};
        DieselEngine.clear();
        for (OreDictMaterial tMat : availableMaterials){
            DieselEngine.add(new AttributesDieselEngine_CH(tMat));
        }
    }

    protected void setMember(DataMachinesGenerator_CH aData) {
        this.BurningBoxBrick = aData.BurningBoxBrick;
        this.BurningBoxSolid = aData.BurningBoxSolid;
        this.DenseBurningBoxSolid = aData.DenseBurningBoxSolid;
        this.BurningBoxLiquid = aData.BurningBoxLiquid;
        this.DenseBurningBoxLiquid = aData.DenseBurningBoxLiquid;
        this.BurningBoxGas = aData.BurningBoxGas;
        this.DenseBurningBoxGas = aData.DenseBurningBoxGas;
        this.BurningBoxFluidizedBed = aData.BurningBoxFluidizedBed;
        this.DenseBurningBoxFluidizedBed = aData.DenseBurningBoxFluidizedBed;
        this.HeatExchanger = aData.HeatExchanger;
        this.DenseHeatExchanger = aData.DenseHeatExchanger;
        this.DieselEngine = aData.DieselEngine;
    }
    @Override
    protected <Type extends DataJson_CH> void setMember(Type aData) {
        setMember((DataMachinesGenerator_CH)aData);
    }

}
