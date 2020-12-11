package com.chy.lamia.element;

import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Var;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashMap;
import java.util.Map;

public class ClassElement {

    String classPath;
    IClassDefine classDefine;
    private static Map<String, ClassElement> classElementCache = new HashMap<>();


    public ClassElement(JCUtils jcUtils, String classPath) {
        this.classPath = classPath;
        JCTree tree = jcUtils.getTree(classPath);
        if (tree != null) {
            classDefine = new TreeClassDefine(jcUtils, tree);
            return;
        }

        //没有对应的源码，说明对应的java文件已经生成了 class文件 那么使用 ASM  解析
        Class<?> classForReflect = getClassForReflect(classPath);
        if (classForReflect != null) {
            classDefine = new ReflectClassDefine(jcUtils, classForReflect);
            return;
        }

        throw new RuntimeException("无法解析类： " + classPath);

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

    public AssembleFactory getAssembleFactory() {
        return classDefine.getAssembleFactory();
    }

    public Map<String, Getter> getInstantGetters() {
        return classDefine.getInstantGetters();
    }


    public static ClassElement getClassElement(String classPath, JCUtils jcUtils) {

        ClassElement result = classElementCache.get(classPath);
        if (result != null) {
            return result;
        }
        result = new ClassElement(jcUtils, classPath);
        classElementCache.put(classPath, result);
        return result;
    }


}
