package gregtechCH.config.machine.kinetic;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.ANY;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregtechCH.config.DataJson_CH;

import java.util.ArrayList;
import java.util.List;

import static gregtechCH.data.CS_CH.Size;

public class DataMachineKinetic_CH extends DataJson_CH {
    // Axles
    @JSONField(ordinal = 1)
    public List<AttributesAxleWood_CH> AxleWood = new ArrayList<>();
    @JSONField(ordinal = 2)
    public List<AttributesAxle_CH> Axle = new ArrayList<>();
    // Engine rotations
    @JSONField(ordinal = 3)
    public List<AttributesEngineRotationWood_CH> EngineRotationWood = new ArrayList<>();
    @JSONField(ordinal = 4)
    public List<AttributesEngineRotation_CH> EngineRotation = new ArrayList<>();
    // Transformer rotations
    @JSONField(ordinal = 5)
    public List<AttributesTransformerRotationWood_CH> TransformerRotationWood = new ArrayList<>();
    @JSONField(ordinal = 6)
    public List<AttributesTransformerRotation_CH> TransformerRotation = new ArrayList<>();
    // Gear boxes
    @JSONField(ordinal = 7)
    public List<AttributesGearBoxWood_CH> GearBoxWood = new ArrayList<>();
    @JSONField(ordinal = 8)
    public List<AttributesGearBox_CH> GearBox = new ArrayList<>();


    public DataMachineKinetic_CH() {}

    @Override
    public void initDefault() {
        OreDictMaterial[] availableMaterials = {
            MT.Bronze,      ANY.Steel,      MT.Ti,              MT.TungstenSteel, MT.Ir,
            MT.Iritanium,   MT.Trinitanium, MT.Trinaquadalloy,  MT.Ad};
        // Axles
        AxleWood.clear();
        for (Size tSize : Size.values()) {
            AxleWood.add(new AttributesAxleWood_CH(MT.WoodTreated, tSize));
        }
        Axle.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            for (Size tSize : Size.values()) {
                Axle.add(new AttributesAxle_CH(tMat, tSize));
            }
        }
        // Engine rotations
        EngineRotationWood.clear();
        EngineRotationWood.add(new AttributesEngineRotationWood_CH(MT.WoodTreated));
        EngineRotation.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            EngineRotation.add(new AttributesEngineRotation_CH(tMat));
        }
        // Transformer rotations
        TransformerRotationWood.clear();
        TransformerRotationWood.add(new AttributesTransformerRotationWood_CH(MT.WoodTreated));
        TransformerRotation.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            TransformerRotation.add(new AttributesTransformerRotation_CH(tMat));
        }
        // Gear boxes
        GearBoxWood.clear();
        GearBoxWood.add(new AttributesGearBoxWood_CH(MT.WoodTreated));
        GearBox.clear();
        for (OreDictMaterial tMat : availableMaterials) {
            GearBox.add(new AttributesGearBox_CH(tMat));
        }
    }

    protected void setMember(DataMachineKinetic_CH aData) {
        this.AxleWood = aData.AxleWood;
        this.Axle = aData.Axle;
        this.EngineRotationWood = aData.EngineRotationWood;
        this.EngineRotation = aData.EngineRotation;
        this.TransformerRotationWood = aData.TransformerRotationWood;
        this.TransformerRotation = aData.TransformerRotation;
        this.GearBoxWood = aData.GearBoxWood;
        this.GearBox = aData.GearBox;
    }
    @Override
    protected <Type extends DataJson_CH> void setMember(Type aData) {
        setMember((DataMachineKinetic_CH)aData);
    }
}
