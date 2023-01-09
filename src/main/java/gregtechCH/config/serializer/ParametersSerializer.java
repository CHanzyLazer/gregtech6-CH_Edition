package gregtechCH.config.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import net.minecraft.nbt.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;

import static gregapi.data.CS.T;

public class ParametersSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        
        if (object instanceof NBTTagCompound) {
            out.write(JSON.toJSONString(NBT2Json((NBTTagCompound)object), T));
        } else {
            out.writeNull();
        }
    }
    
    // 自用，NBT 转 fastjson
    private JSONObject NBT2Json(NBTTagCompound aNBT){
        Set<?> keys = aNBT.func_150296_c();
        JSONObject jsonRoot = new JSONObject();
        for(Object obj : keys) {
            String key = (String)obj;
            NBTBase tag = aNBT.getTag(key);
            
            switch (NBTBase.NBTTypes[tag.getId()]) {
                case "END":   {break;}
                case "BYTE":  {jsonRoot.put(key, ((NBTBase.NBTPrimitive) tag).func_150290_f()); break;}
                case "SHORT": {jsonRoot.put(key, ((NBTBase.NBTPrimitive)tag).func_150289_e()); break;}
                case "INT":   {jsonRoot.put(key, ((NBTBase.NBTPrimitive)tag).func_150287_d()); break;}
                case "LONG":  {jsonRoot.put(key, ((NBTBase.NBTPrimitive)tag).func_150291_c()); break;}
                case "FLOAT": {jsonRoot.put(key, ((NBTBase.NBTPrimitive)tag).func_150288_h()); break;}
                case "DOUBLE": {jsonRoot.put(key, ((NBTBase.NBTPrimitive)tag).func_150286_g()); break;}
                case "BYTE[]": {jsonRoot.put(key, ((NBTTagByteArray)tag).func_150292_c()); break;}
                case "STRING": {jsonRoot.put(key, ((NBTTagString)tag).func_150285_a_()); break;}
                case "LIST": {
                    JSONArray tArray = new JSONArray();
                    for(int i = 0; i < ((NBTTagList)tag).tagCount(); ++i) {
                        tArray.add(NBT2Json(((NBTTagList)tag).getCompoundTagAt(i)));
                    }
                    jsonRoot.put(key, tArray);
                    break;
                }
                case "COMPOUND": {jsonRoot.put(key, NBT2Json((NBTTagCompound)tag)); break;}
                case "INT[]": {jsonRoot.put(key, ((NBTTagIntArray)tag).func_150302_c()); break;}
                default: break;
            }
        }
        return jsonRoot;
    }
}
