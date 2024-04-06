package com.chy.lamia.processor.marked;

import com.sun.tools.javac.code.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 *  每一个 MarkedMethods 对象存放的都是 在同一个 class中被标注的所有 方法
 *
 */
public class MarkedMethods {
    /**
     * key:方法的名称
     * value:方法的表达式
     */
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
