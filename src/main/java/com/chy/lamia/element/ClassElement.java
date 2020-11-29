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
        }
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
