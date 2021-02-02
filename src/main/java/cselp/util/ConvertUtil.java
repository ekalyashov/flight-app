package cselp.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConvertUtil {
    private static Log log = LogFactory.getLog(ConvertUtil.class);

    public static <K,T> void addToMapEntry(Map<K, List<T>> map, K key, T value) {
        List<T> list = map.get(key);
        if (list == null) {
            list = new ArrayList<T>();
            map.put(key, list);
        }
        list.add(value);
    }

    @SuppressWarnings("unchecked")
    public static <K,T> void addToMapEntry(Map<K, ? extends Collection<T>> map, K key, T value,
                                           Class<? extends Collection> collectionClass) {
        Collection<T> coll = map.get(key);
        if (coll == null) {
            try {
                coll = collectionClass.newInstance();
            } catch (Exception e) {
                log.error("invalid collectionClass " + collectionClass, e);
                return;
            }
            //cast to generic map, else map.put generates error
            Map m = map;
            m.put(key, coll);
        }
        coll.add(value);
    }

}
