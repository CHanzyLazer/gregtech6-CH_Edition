package gregtechCH.lang;

import cpw.mods.fml.common.registry.LanguageRegistry;
import gregapi.lang.LanguageHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.HashMap;
import java.util.Map;

import static gregapi.data.CS.F;
import static gregapi.data.CS.T;
import static gregtechCH.data.CS_CH.RegType;

/* 由于都是静态方法，这里继承必须手动重写一些 */
public class LanguageHandler_CH extends LanguageHandler {
    public static Configuration sLangFile;

    private static final HashMap<String, String> TEMPMAP = new HashMap<>();
    private static final HashMap<String, String> BUFFERMAP = new HashMap<>();
    private static boolean mWritingEnabled = F, mUseFile = F;

    public static void save() {
        if (sLangFile != null) {
            mWritingEnabled = T;
            sLangFile.save();
        }
    }
    
    public static synchronized void add(String aKey, String aEnglish) {add(RegType.GTCH, aKey, aEnglish);}
    public static synchronized void add(RegType aRegType, String aKey, String aEnglish) {
        if (aKey == null) return;
        assert aRegType != RegType.GREG;
        aKey = aKey.trim();
        if (aKey.length() <= 0) return;
        BACKUPMAP.put(aKey, aEnglish);
        if (sLangFile == null) {
            BUFFERMAP.put(aKey, aEnglish);
        } else {
            String tCategory;
            switch (aRegType) {
                case GT6U:
                    tCategory = "LanguageFile_GT6U"; break;
                case OTHER:
                    tCategory = "LanguageFile_other"; break;
                case GTCH: default:
                    tCategory = "LanguageFile"; break;
            }
            mUseFile = sLangFile.get("EnableLangFile", "UseThisFileAsLanguageFile", F).getBoolean(F);
            if (!BUFFERMAP.isEmpty()) {
                for (Map.Entry<String, String> tEntry : BUFFERMAP.entrySet()) {
                    Property tProperty = sLangFile.get(tCategory, tEntry.getKey(), tEntry.getValue());
                    TEMPMAP.put(tEntry.getKey(), mUseFile?tProperty.getString():tEntry.getValue());
                    LanguageRegistry.instance().injectLanguage("en_US", TEMPMAP);
                    TEMPMAP.clear();
                }
                if (mWritingEnabled) sLangFile.save();
                BUFFERMAP.clear();
            }
            Property tProperty = sLangFile.get(tCategory, aKey, aEnglish);
            if (!tProperty.wasRead() && mWritingEnabled) sLangFile.save();
            TEMPMAP.put(aKey, mUseFile?tProperty.getString():aEnglish);
            LanguageRegistry.instance().injectLanguage("en_US", TEMPMAP);
            TEMPMAP.clear();
        }
    }

}
