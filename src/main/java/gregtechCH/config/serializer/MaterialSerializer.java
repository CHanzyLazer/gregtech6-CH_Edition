package gregtechCH.config.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import gregapi.oredict.OreDictMaterial;

import java.io.IOException;
import java.lang.reflect.Type;


public class MaterialSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object instanceof OreDictMaterial) {
            out.writeString(((OreDictMaterial)object).mNameInternal);
        } else {
            out.writeNull();
        }
    }
}
