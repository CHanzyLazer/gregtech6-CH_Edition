package gregtechCH.config.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import gregapi.oredict.OreDictMaterial;

import java.lang.reflect.Type;

public class MaterialDeserializer_CH implements ObjectDeserializer {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return (T) OreDictMaterial.get((String) parser.parse());
    }

    @Override
    public int getFastMatchToken() {return 0;}
}
