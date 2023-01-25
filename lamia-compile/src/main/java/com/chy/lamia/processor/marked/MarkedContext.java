package com.chy.lamia.processor.marked;

import com.sun.tools.javac.code.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 被标注了 注解的 所有方法将存放到这个 context中
 * 存放格式为  {classPath : {方法名称 : 被标注了注解的方法}}
 */
public class MarkedContext {

    public Map<String, MarkedMethods> data = new HashMap<>();

    public void put(String key, Symbol.MethodSymbol methodSymbol) {
        MarkedMethods simpleClass = data.get(key);
        if (simpleClass == null) {
            simpleClass = new MarkedMethods();
            data.put(key, simpleClass);
        }
        simpleClass.add(methodSymbol);
    }

    public void forEach(BiConsumer<String, MarkedMethods> action) {
        data.forEach((k, v) -> {
            action.accept(k, v);
        });
    }


    public Map<String, MarkedMethods> getData() {
        return data;
    }

}
