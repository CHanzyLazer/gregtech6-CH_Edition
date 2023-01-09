package gregtechCH.config.serializer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static gregapi.data.CS.U;

public class ItemDataDeserializer_CH implements ObjectDeserializer {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        List<String> materialList = parser.parseArray(String.class);
        if (materialList == null) return null;
        // ["Lead:5", "Wood:1.2"]
        return (T) get(materialList.toArray(new String[0]));
    }

    @Override
    public int getFastMatchToken() {return 0;}

    public static OreDictItemData get(String[] aDataName) {
        if (aDataName == null) return null;
        if (aDataName.length >= 1) {
            OreDictMaterialStack materialStackF = null;
            OreDictMaterialStack[] materialStacks = new OreDictMaterialStack[aDataName.length - 1];
            String[] materialPair;
            int i = 0;
            for (String subMaterial : aDataName) {
                if (!subMaterial.isEmpty()) {
                    materialPair = subMaterial.split(":", 2);
                    if (materialPair.length >= 2) {
                        if (i == 0) materialStackF = OM.stack(OreDictMaterial.get(materialPair[0]), Math.round(Double.parseDouble(materialPair[1]) * U));
                        else materialStacks[i-1] = OM.stack(OreDictMaterial.get(materialPair[0]), Math.round(Double.parseDouble(materialPair[1]) * U));
                    } else {
                        if (i == 0) materialStackF = null;
                        else materialStacks[i-1] = null;
                    }
                } else {
                    if (i == 0) materialStackF = null;
                    else materialStacks[i-1] = null;
                }
                ++i;
            }
            return new OreDictItemData(materialStackF, materialStacks);
        } else {
            return null;
        }
    }
}
