package com.chy.lamia.element.class_define;


import com.chy.lamia.convert.core.entity.Constructor;
import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.element.reflect.GetSetCollect;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.entity.Var;
import com.chy.lamia.utils.JCUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReflectClassDefine implements IClassDefine {

    private final JCUtils jcUtils;
    private final String classPath;
    private final Class<?> leadClass;

    /**
     * 实例中的所有属性
     */
    private Map<String, Var> instantVars;

    /**
     * 实例中所有的 getter
     * key getter方法对应的 字段的名称
     */
    private Map<String, Getter> instantGetters;
    private Map<String, Setter> instantSetters;

    /**
     * 实例中所有的构造器
     */
    private List<Constructor> constructors;


    public ReflectClassDefine(JCUtils jcUtils, Class<?> leadClass) {
        this.jcUtils = jcUtils;
        this.classPath = leadClass.getName();
        this.leadClass = leadClass;
    }

    private void parseClass() {
        Method[] methods = leadClass.getMethods();
        parseMethods(methods);
        parseMethodConstructor(leadClass);
    }

    /**
     * 解析构造器
     *
     * @param leadClass
     */
    private void parseMethodConstructor(Class<?> leadClass) {
        Arrays.stream(leadClass.getConstructors()).forEach(jConstructor -> {
            Constructor constructor = new Constructor();
        });
    }

    /**
     * 解析所有的方法，收集所有的 getter和setter方法
     *
     * @param methods
     */
    private void parseMethods(Method[] methods) {
        GetSetCollect getSetCollect = new GetSetCollect();
        for (Method method : methods) {
            getSetCollect.visit(method);
        }
        instantGetters = getSetCollect.getInstantGetters();
        instantSetters = getSetCollect.getInstantSetters();
    }

    @Override
    public Map<String, Var> getInstantVars() {
        return null;
    }

    @Override
    public Map<String, Getter> getInstantGetters() {
        return null;
    }

    @Override
    public Map<String, Setter> getInstantSetters() {
        return null;
    }

    @Override
    public List<Constructor> getConstructors() {
        return null;
    }

    public List<SimpleMethod> getAllMethod() {
        return null;
    }
}
