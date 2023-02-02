package com.chy.lamia.utils;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {

    public static <T> T getFile(Object obj, String name, Class<T> type) {
        Field declaredField = null;
        try {
            declaredField = obj.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("类 : [" + obj.getClass().getName() + "] 无法找到名称为[" + name + "]的字段", e);
        }
        declaredField.setAccessible(true);
        Object data = null;
        try {
            data = declaredField.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("类 : [" + obj.getClass().getName() + "] 获取字段[" + name + "]失败", e);
        }
        if (type.isInstance(data)) {
            return (T) data;
        }
        throw new RuntimeException("类 : [" + obj.getClass().getName() + "] 中字段[" + name + "]类型和期望不一致, 期望[" + type.getTypeName() + "] 当前 [" + data.getClass().getTypeName() + "]");

    }

    public static Method getMethod(Class<?> c, String mName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method m = null;
        Class<?> oc = c;
        while (c != null) {
            try {
                m = c.getDeclaredMethod(mName, parameterTypes);
                break;
            } catch (NoSuchMethodException e) {
            }
            c = c.getSuperclass();
        }

        if (m == null) {
            throw new NoSuchMethodException(oc.getName() + " :: " + mName + "(args)");
        }
        return setAccessible(m);
    }

    public static <T extends AccessibleObject> T setAccessible(T accessor) {
        accessor.setAccessible(true);
        return accessor;
    }

}
