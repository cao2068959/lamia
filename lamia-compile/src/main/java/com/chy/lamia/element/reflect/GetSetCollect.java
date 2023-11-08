package com.chy.lamia.element.reflect;


import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用反射去收集getter以及setter方法
 */
public class GetSetCollect {

    Map<String, Getter> instantGetters = new HashMap<>();
    Map<String, Setter> instantSetters = new HashMap<>();


    public void visit(Method method) {
        String name = method.getName();
        //不是 get/set  开头直接弃坑
        if (name.length() < 4) {
            return;
        }

        if (name.startsWith("get")) {
            getterHandle(name, method);
        } else if (name.startsWith("set")) {
            setterHandle(name, method);
        }
    }

    private void setterHandle(String name, Method method) {

        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length != 1) {
            return;
        }
        String parameterTypeName = parameters[0].getType().getName();
        String varName = varNameHandle(name.substring(3));
        Setter setter = new Setter();
        setter.setType(new TypeDefinition(parameterTypeName));
        setter.setVarName(name);
        instantSetters.put(varName, setter);
    }

    private void getterHandle(String name, Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length > 0) {
            return;
        }
        String returnTypeName = method.getReturnType().getName();
        if ("void".equals(returnTypeName.toLowerCase())) {
            return;
        }
        String varName = varNameHandle(name.substring(3));
        if (varName == null) {
            return;
        }
        Getter getter = new Getter();
        getter.setVarName(name);
        //getter.setTypePath(returnTypeName);
        instantGetters.put(varName, getter);
    }

    /**
     * 处理一下 var的名字
     * 把开头字母给小写
     *
     * @param data
     * @return
     */
    private String varNameHandle(String data) {
        if (data == null || data.length() < 1) {
            return null;
        }

        char[] chars = data.toCharArray();
        chars[0] = toLow(chars[0]);
        return new String(chars);
    }

    private char toLow(char c) {
        if (c >= 'A' && c <= 'Z') {
            c += 32;
        }
        return c;
    }

    public Map<String, Getter> getInstantGetters() {
        return instantGetters;
    }

    public Map<String, Setter> getInstantSetters() {
        return instantSetters;
    }
}
