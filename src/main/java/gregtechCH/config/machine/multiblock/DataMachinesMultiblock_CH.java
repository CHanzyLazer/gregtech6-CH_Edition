package gregtechCH.config.machine.multiblock;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.MT;
import gregapi.data.MT_CH;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.DataJson_CH;

import java.util.ArrayList;
import java.util.List;

public class DataMachinesMultiblock_CH extends DataJson_CH {
    // Boilers
    @JSONField(ordinal = 3)
    public List<AttributesLargeBoilerTank_CH> LargeBoilerTank = new ArrayList<>();
    // Turbines
    @JSONField(ordinal = 4)
    public List<AttributesLargeSteamTurbine_CH> LargeSteamTurbine = new ArrayList<>();
    @JSONField(ordinal = 5)
    public List<AttributesLargeGasTurbine_CH> LargeGasTurbine = new ArrayList<>();

    public DataMachinesMultiblock_CH() {}

    @Override
    public void initDefault() {
        OreDictMaterial[] availableMaterials;
        // Boilers
        availableMaterials = new OreDictMaterial[] {
                MT.StainlessSteel, MT.Invar, MT.Ti, MT.TungstenSteel, MT_CH.Nb2Ti3C5, MT.Ad};
        LargeBoilerTank.clear();
        for (OreDictMaterial aMat : availableMaterials){
            LargeBoilerTank.add(new AttributesLargeBoilerTank_CH(aMat));
        }
        // Turbines
        availableMaterials = new OreDictMaterial[] {
                MT.StainlessSteel, MT.Ti, MT.TungstenSteel, MT_CH.Nb2Ti3C5, MT.Ad};
        OreDictMaterial[][] availableRotorMaterials = {
                {MT.Magnalium},
                {MT.Trinitanium},
                {MT.Graphene},
                {MT.Graphene},
                {MT.Vibramantium}};
        LargeSteamTurbine.clear();
        LargeGasTurbine.clear();
        int i = 0;
        for (OreDictMaterial aMat : availableMaterials){
            for (OreDictMaterial tRotMat : availableRotorMaterials[i]) {
                LargeSteamTurbine.add(new AttributesLargeSteamTurbine_CH(aMat, tRotMat));
                LargeGasTurbine.add(new AttributesLargeGasTurbine_CH(aMat, tRotMat));
            }
            ++i;
        }
    }

    protected void setMember(DataMachinesMultiblock_CH aData) {
        this.LargeBoilerTank = aData.LargeBoilerTank;
        this.LargeSteamTurbine = aData.LargeSteamTurbine;
        this.LargeGasTurbine = aData.LargeGasTurbine;
    }
    @Override
    protected <Type extends DataJson_CH> void setMember(Type aData) {
        setMember((DataMachinesMultiblock_CH)aData);
    }
}
