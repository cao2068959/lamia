package com.chy.lamia.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lists {

    public static List empty = new ArrayList();

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


    public static <T> com.sun.tools.javac.util.List<T> toSunList(List<T> list) {
        if (list == null || list.size() == 0) {
            return com.sun.tools.javac.util.List.nil();
        }
        return com.sun.tools.javac.util.List.from(list);
    }

    public static String toString(List<String> data) {
        if (data == null) {
            return "null";
        }
        return String.join(",", data);
    }
}
