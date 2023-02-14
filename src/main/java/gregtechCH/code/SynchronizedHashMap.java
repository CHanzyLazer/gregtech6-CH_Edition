package gregtechCH.code;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CHanzy
 * 线程安全的 HashMap，可以避免死循环
 * 仅在写操作上加上同步锁，因此读取的数据可能是不正确的
 * 提供专门的加锁的读取接口，为一定需要正确结果的地方使用
 * 没有列出的方法是线程不安全的
 */
public class SynchronizedHashMap<K, V> extends HashMap<K, V> {
    @Override public synchronized V put(K key, V value) {return super.put(key, value);}
    @Override public synchronized V remove(Object key) {return super.remove(key);}
    @Override public synchronized void putAll(Map<? extends K, ? extends V> m) {super.putAll(m);}
    @Override public synchronized void clear() {super.clear();}
    public synchronized V syGet(Object key) {return super.get(key);}
}
