package com.chy.lamia.element;

import com.chy.lamia.element.assemble.valobj.ValueObjectAssembleFactory;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.Var;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashMap;
import java.util.Map;

public class ClassDetails {


    IClassDefine classDefine;
    ParameterType parameterType;

    private static Map<ParameterType, ClassDetails> classElementCache = new HashMap<>();


    public ClassDetails(ParameterType parameterType) {
        this.parameterType = parameterType;
        JCUtils jcUtils = JCUtils.instance;
        JCTree tree = jcUtils.getTree(parameterType.getTypePatch());
        if (tree != null) {
            classDefine = new TreeClassDefine(jcUtils, tree);
            return;
        }

        //没有对应的源码，说明对应的java文件已经生成了 class文件 那么使用 ASM  解析
        Class<?> classForReflect = getClassForReflect(parameterType.getTypePatch());
        if (classForReflect != null) {
            classDefine = new AsmClassDefine(jcUtils, classForReflect);
            return;
        }

        throw new RuntimeException("无法解析类： " + parameterType.getTypePatch());

    }

    /**
     * 通过反射去获取对应的 class对象
     *
     * @param classPath
     * @return
     */
    private Class<?> getClassForReflect(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }


    public Map<String, Var> getInstantVarName() {
        return classDefine.getInstantVars();
    }

    public ValueObjectAssembleFactory getAssembleFactory() {
        return classDefine.getAssembleFactory();
    }

    public Map<String, Getter> getInstantGetters() {
        return classDefine.getInstantGetters();
    }


    public static ClassDetails getClassElement(ParameterType parameterType) {
        ClassDetails result = classElementCache.get(parameterType);
        if (result != null) {
            return result;
        }
        result = new ClassDetails(parameterType);
        classElementCache.put(parameterType, result);
        return result;
    }


}
