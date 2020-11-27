package com.chy.lamia.entity;


import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChosenClass {

    public Map<String, SimpleClass> data = new HashMap<>();

    public void put(String key, Symbol.MethodSymbol methodSymbol) {
        SimpleClass simpleClass = data.get(key);
        if (simpleClass == null) {
            simpleClass = new SimpleClass();
            data.put(key, simpleClass);
        }
        simpleClass.add(methodSymbol);
    }

    public void forEach(BiConsumer<String, SimpleClass> action) {
        data.forEach((k, v) -> {
            action.accept(k, v);
        });
    }


    public Map<String, SimpleClass> getData() {
        return data;
    }

    public class SimpleClass {
        private List<Symbol.MethodSymbol> lists = new ArrayList<>();

        public void add(Symbol.MethodSymbol methodSymbol) {
            lists.add(methodSymbol);
        }

        public List<Symbol.MethodSymbol> getLists() {
            return lists;
        }
    }

}


