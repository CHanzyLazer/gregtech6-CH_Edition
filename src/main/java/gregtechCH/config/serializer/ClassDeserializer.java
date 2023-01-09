package gregtechCH.config.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

import static gregapi.data.CS.OUT;

public class ClassDeserializer implements ObjectDeserializer {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return (T) get((String) parser.parse());
    }
    
    @Override
    public int getFastMatchToken() {return 0;}
    
    public static Class<?> get(String aClassName) {
        if (aClassName == null) return null;
        try {
            return Class.forName(aClassName);
        } catch (ClassNotFoundException e) {
            OUT.println("Class Not Found: " + aClassName);
            return null;
        }
    }
}
