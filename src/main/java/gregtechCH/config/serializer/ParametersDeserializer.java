package gregtechCH.config.serializer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import gregapi.util.UT;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParametersDeserializer implements ObjectDeserializer {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONObject parametersJson = parser.parseObject();
        return (T) get(parametersJson);
    }
    
    @Override
    public int getFastMatchToken() {return 0;}
    
    public NBTTagCompound get(JSONObject aParametersJson) {
        if (aParametersJson == null) return null;
        List<Object> tTagList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : aParametersJson.entrySet()) {
            tTagList.add(entry.getKey());
            tTagList.add(entry.getValue());
        }
        return UT.NBT.make(null, tTagList.toArray());
    }
}
