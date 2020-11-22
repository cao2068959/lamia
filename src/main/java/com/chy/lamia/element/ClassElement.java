package com.chy.lamia.element;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;

public class ClassElement {

    JavacElements elementUtils;
    JavacTrees trees;
    String classPath;

    public ClassElement(JavacElements elementUtils, JavacTrees trees, String classPath) {
        this.elementUtils = elementUtils;
        this.trees = trees;
        this.classPath = classPath;
        JCTree tree = elementUtils.getTree(elementUtils.getTypeElement(classPath));
        if(tree != null){
            new TreeClassConstruction(elementUtils,trees,tree);
        }
    }
}
