package gregtechCH.code;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author CHanzy
 * 实现限制大小的 hashmap
 * LinkedHashMap 会自动根据调用来将键值对放到最前，超过容量后移除最末尾的键值对，因此只需要设置最大容量即可
 */
public class LimitedHashMap<Key, Value> extends LinkedHashMap<Key, Value> {
    private static final long serialVersionUID = -6147161439550295706L;

    private int mMaxSize = 1024;
    public LimitedHashMap<Key, Value> setMaxSize(int aMaxSize) {mMaxSize = aMaxSize; return this;}
    @Override protected boolean removeEldestEntry(Map.Entry<Key, Value> aEldest) {return size() > mMaxSize;}
}
