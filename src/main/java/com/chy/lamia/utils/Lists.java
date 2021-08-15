package com.chy.lamia.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lists {

    public static <T> List<T> of(T... t) {
        if (t == null || t.length == 0) {
            return new LinkedList<>();
        }
        List<T> result = new ArrayList<>(t.length);
        for (T item : t) {
            result.add(item);
        }
        return result;
    }


}
