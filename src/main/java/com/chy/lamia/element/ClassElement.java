package com.chy.lamia.element;

import com.chy.lamia.entity.Var;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;

import java.util.Map;

public class ClassElement {

    JavacElements elementUtils;
    JavacTrees trees;
    String classPath;
    IClassDefine classConstruction;


    public ClassElement(JavacElements elementUtils, JavacTrees trees, String classPath) {
        this.elementUtils = elementUtils;
        this.trees = trees;
        this.classPath = classPath;
        JCTree tree = elementUtils.getTree(elementUtils.getTypeElement(classPath));
        if(tree != null){
            classConstruction = new TreeClassDefine(elementUtils,trees,tree);
        }
    }


    public Map<String, Var> getInstantVarName(){
        return classConstruction.getInstantVars();
    }


}
