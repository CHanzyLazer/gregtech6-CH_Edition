package gregtechCH.config.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import gregapi.code.ModData;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;

public class ItemDeserializer implements ObjectDeserializer {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return (T) get((String) parser.parse());
    }

    @Override
    public int getFastMatchToken() {return 0;}

    public static ItemStack get(String aItemName) {
        if (aItemName == null) return null;
        String[] itemNamePair = aItemName.split(":", 3);
        return ST.make(new ModData(itemNamePair[0], ""), itemNamePair[1], 1,
                itemNamePair.length >= 3 ? Integer.parseInt(itemNamePair[2]) : 0);
    }
}
