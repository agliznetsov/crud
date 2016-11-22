package org.crud.core.util;

import java.util.*;

@SuppressWarnings("unchecked")
public class MapUtils {
    public static <K, V> Map<K, V> map(Object... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("params.length");
        }
        HashMap map = new HashMap();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[i + 1]);
        }
        return map;
    }

    /**
     * Variant of the map method that allows "deep" string properties to be used, i.e. ("a.b.c",5) creates a hashmaps containing
     * the key "a", pointing to an hashmap containing the key "b" pointing to an hashmap containing the value 5.
     *
     * @param params list of property / value pairs
     * @return the created map
     */
    public static Map<String, Object> deepMap(Object... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("params.length");
        }
        final HashMap map = new HashMap();
        for (int i = 0; i < params.length; i += 2) {
            putDeepValue(map, params[i].toString(), params[i + 1]);
        }
        return map;
    }

    /***
     * Returns the value of the nested property.
     *
     * @param map
     * @param path Path to the property using the dot notation f.e.: "parent.type.name"
     */
    public static <T> T getPath(Map map, String path) {
        return getValue(map, Arrays.asList(path.split("\\.")), 0);
    }

    public static <T> T getValue(Map map, String... keys) {
        return getValue(map, Arrays.asList(keys), 0);
    }

    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    public static <T> void putDeepValue(Map<String, T> map, String keyPath, T v) {
        HashMap subMap = (HashMap) map;

        String[] propertyPath = keyPath.toString().split("\\.");

        int j = 0;
        for (; j < propertyPath.length - 1; j++) {
            String property = propertyPath[j];
            Object value = subMap.get(property);
            if ((value == null) || !(value instanceof Map)) {
                value = new HashMap<>();
                subMap.put(property, value);
            }
            subMap = (HashMap) value;
        }
        subMap.put(propertyPath[j], v);
    }

    private static <T> T getValue(Object target, List<String> keys, int index) {
        Object tmp = null;
        if (target instanceof Map) {
            tmp = ((Map) target).get(keys.get(index));
        } else if (target instanceof List) {
            tmp = ((List) target).get(Integer.parseInt(keys.get(index)));
        }
        if (tmp != null) {
            if (index < keys.size() - 1)
                return getValue(tmp, keys, index + 1);
            else
                return (T) tmp;
        }
        return null;
    }
}
