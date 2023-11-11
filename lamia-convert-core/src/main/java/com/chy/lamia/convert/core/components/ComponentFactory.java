package com.chy.lamia.convert.core.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author bignosecat
 */
public class ComponentFactory {

    static Map<Class<?>, Object> allComponent = new HashMap<>();

    static Map<Class<?>, Supplier<?>> entityCreateCache = new HashMap<>();

    public static <T> T getComponent(Class<T> type) {
        Object o = allComponent.get(type);
        if (o == null) {
            throw new RuntimeException("缺乏组件[" + type.getName() + "]");
        }
        return (T) o;
    }

    public static <T> T createEntity(Class<T> type) {
        Supplier<?> supplier = entityCreateCache.get(type);
        if (supplier == null) {
            throw new RuntimeException("缺乏组件实体[" + type.getName() + "] 的构建方法");
        }
        return (T) supplier.get();
    }


}
