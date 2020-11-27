package com.chy.lamia.element;

import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Var;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;

import java.util.Map;

public class ClassElement {

    String classPath;
    IClassDefine classConstruction;


    public ClassElement(JCUtils jcUtils, String classPath) {
        this.classPath = classPath;
        JCTree tree = jcUtils.getTree(classPath);
        if (tree != null) {
            classConstruction = new TreeClassDefine(jcUtils, tree);
        }
    }


    public Map<String, Var> getInstantVarName() {
        return classConstruction.getInstantVars();
    }

    public AssembleFactory getAssembleFactory() {
        return classConstruction.getAssembleFactory();
    }

    public Map<String, Getter> getInstantGetters() {
        return classConstruction.getInstantGetters();
    }


}
