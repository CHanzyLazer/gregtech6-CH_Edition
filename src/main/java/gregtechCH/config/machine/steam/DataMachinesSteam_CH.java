package gregtechCH.config.machine.steam;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.DataJson_CH;

import java.util.ArrayList;
import java.util.List;

public class DataMachinesSteam_CH extends DataJson_CH {
    // Steam Boilers
    @JSONField(ordinal = 11)
    public List<AttributesSteamBoilerTank_CH> SteamBoilerTank = new ArrayList<>();
    @JSONField(ordinal = 12)
    public List<AttributesStrongSteamBoilerTank_CH> StrongSteamBoilerTank = new ArrayList<>();
    // Steam Engines
    @JSONField(ordinal = 14)
    public List<AttributesSteamEngine_CH> SteamEngine = new ArrayList<>();
    @JSONField(ordinal = 15)
    public List<AttributesStrongSteamEngine_CH> StrongSteamEngine = new ArrayList<>();
    // Steam Turbines
    @JSONField(ordinal = 13)
    public List<AttributesSteamTurbine_CH> SteamTurbine = new ArrayList<>();


    public DataMachinesSteam_CH() {}

    @Override
    public void initDefault() {
        OreDictMaterial[] availableMaterials;
        // Steam Boilers
        availableMaterials = new OreDictMaterial[]{
                MT.Pb,      MT.Bi,  MT.Bronze,      MT.Invar, ANY.Steel,
                MT.Cr,      MT.Ti,  MT.Netherite,   ANY.W,    MT.TungstenSteel};
        SteamBoilerTank.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            SteamBoilerTank.add(new AttributesSteamBoilerTank_CH(tMat));
        }
        StrongSteamBoilerTank.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            StrongSteamBoilerTank.add(new AttributesStrongSteamBoilerTank_CH(tMat));
        }
        // Steam Engines
        availableMaterials = new OreDictMaterial[]{
                MT.Pb,        MT.TinAlloy,      MT.Bronze,        MT.Brass, MT.Invar,
                MT.IronWood,  ANY.Steel,        MT.FierySteel,    MT.Cr,    MT.Ti,
                ANY.W,        MT.TungstenSteel};
        SteamEngine.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            SteamEngine.add(new AttributesSteamEngine_CH(tMat));
        }
        StrongSteamEngine.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            StrongSteamEngine.add(new AttributesStrongSteamEngine_CH(tMat));
        }
        // Steam Turbines
        OreDictMaterial[][] availableRotorMaterials = {
                {MT.Bronze,     MT.Brass,       MT.Invar},
                {ANY.Steel,     MT.Cr,          MT.IronWood,    MT.Steeleaf,    MT.Thaumium},
                {MT.Ti,         MT.FierySteel,  MT.Al,          MT.Magnalium},
                {MT.VoidMetal,  MT.Trinitanium, MT.Graphene}};
        SteamTurbine.clear();
        for (int i = 0; i < 4; ++i){
            for (OreDictMaterial tRotMat : availableRotorMaterials[i]) {
                SteamTurbine.add(new AttributesSteamTurbine_CH(MT.DATA.Kinetic_T[i+1], tRotMat));
            }
        }
    }

    protected void setMember(DataMachinesSteam_CH aData) {
        this.SteamEngine = aData.SteamEngine;
        this.SteamBoilerTank = aData.SteamBoilerTank;
        this.SteamTurbine = aData.SteamTurbine;
        this.StrongSteamBoilerTank = aData.StrongSteamBoilerTank;
        this.StrongSteamEngine = aData.StrongSteamEngine;
    }
    @Override
    protected <Type extends DataJson_CH> void setMember(Type aData) {
        setMember((DataMachinesSteam_CH)aData);
    }
}
