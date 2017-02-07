package org.crud.core.util;


import lombok.NonNull;

import java.util.*;

@SuppressWarnings("unchecked")
public abstract class MapUtils {

    /**
     * Create set out of given elements
     *
     */
    public static <E> Set<E> set(E... elements) {
        HashSet<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * Create a map out of given key/value pairs
     *
     * @param params key/values pairs
     * @return the created map
     */
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
     * Variant of the map method that allows "deep" string properties to be used, i.e. ("a.b.c",5) creates a map containing
     * the key "a", pointing to a map containing the key "b" pointing to a map containing the value 5.
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
     * @param map
     * @param path Path to the property using the dot notation f.e.: "parent.type.name"
     * @return the value of the nested property using its path.
     */
    public static <T> T getValue(Map map, @NonNull String path) {
        return getValue(map, path.split("\\."));
    }

    /***
     * @param map
     * @param keys path to the property as a successive list of keys
     * @return the value of the nested property using its path.
     */
    public static <T> T getValue(Map map, @NonNull String... keys) {
        if (keys.length == 0) {
            throw new IllegalArgumentException("keys parameter is empty");
        }
        return getValue(map, keys, 0);
    }

    public static <T> void putDeepValue(Map<String, T> map, String keyPath, T v) {
        Map subMap = map;
        String[] propertyPath = keyPath.split("\\.");

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

    private static <T> T getValue(Object target, String[] keys, int index) {
        Object tmp = null;
        if (target instanceof Map) {
            tmp = ((Map) target).get(keys[index]);
        } else if (target instanceof List) {
            tmp = ((List) target).get(Integer.parseInt(keys[index]));
        }
        if (tmp != null) {
            if (index < keys.length - 1)
                return getValue(tmp, keys, index + 1);
            else
                return (T) tmp;
        }
        return null;
    }
}
