package com.chy.lamia.utils;

import lombok.Setter;

import java.util.HashMap;

/**
 * 有默认值的 hashmap
 */
public class DefaultHashMap<K, P> extends HashMap<K, P> {

    @Setter
    P defaultValue;

    @Override
    public P get(Object key) {
        P value = super.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public boolean contains(Object key) {
        if (defaultValue != null) {
            return true;
        }
        return super.containsKey(key);
    }
}
