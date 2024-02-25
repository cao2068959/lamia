package com.chy.lamia.convert.core.components;

import com.chy.lamia.convert.core.ConvertFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author bignosecat
 */
public class ComponentFactory {

    static Map<Class<?>, Object> allComponent = new HashMap<>();

    static Map<Object, Map<Class<?>, Object>> allInstanceComponent = new HashMap<>();


    static Map<Class<?>, Supplier<?>> entityCreateCache = new HashMap<>();

    public static <T> T getComponent(Class<T> type) {
        Object o = allComponent.get(type);
        if (o == null) {
            throw new RuntimeException("缺乏组件[" + type.getName() + "]");
        }
        return (T) o;
    }

    public static <T> T getInstanceComponent(ConvertFactory convertFactory, Class<T> type) {

        Map<Class<?>, Object> classObjectMap = allInstanceComponent.get(convertFactory);
        if (classObjectMap == null) {
            throw new RuntimeException("实例{" + convertFactory + "}缺少实例组件组件[" + type.getName() + "]");
        }
        Object o = classObjectMap.get(type);
        if (o == null) {
            throw new RuntimeException("实例{" + convertFactory + "} 找到实例容器，但是缺少实例组件组件[" + type.getName() + "]");
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


    public static <T> void registerComponents(Class<T> type, T instances) {
        allComponent.put(type, instances);
    }

    public static <T> void registerInstanceComponents(ConvertFactory instances, Class<T> type, T componentInstances) {
        allInstanceComponent.computeIfAbsent(instances, k -> new HashMap<>()).put(type, componentInstances);
    }

    public static <T> void registerEntityStructure(Class<T> type, Supplier<T> instances) {
        entityCreateCache.put(type, instances);

    }


}
