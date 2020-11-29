package com.chy.lamia.entity;


import com.sun.tools.javac.code.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChosenClass {

    public Map<String, SimpleMethodCollect> data = new HashMap<>();

    public void put(String key, Symbol.MethodSymbol methodSymbol) {
        SimpleMethodCollect simpleClass = data.get(key);
        if (simpleClass == null) {
            simpleClass = new SimpleMethodCollect();
            data.put(key, simpleClass);
        }
        simpleClass.add(methodSymbol);
    }

    public void forEach(BiConsumer<String, SimpleMethodCollect> action) {
        data.forEach((k, v) -> {
            action.accept(k, v);
        });
    }


    public Map<String, SimpleMethodCollect> getData() {
        return data;
    }

    public static class SimpleMethodCollect {
        private Map<String, Symbol.MethodSymbol> map = new HashMap<>();

        public boolean contains(String methodName) {
            return map.containsKey(methodName);
        }

        public void add(Symbol.MethodSymbol methodSymbol) {
            map.put(methodSymbol.toString(), methodSymbol);
        }

        public Map<String, Symbol.MethodSymbol> getDatas() {
            return map;
        }
    }

}


