package com.chy.lamia.element;

import com.chy.lamia.element.tree.VarCollect;
import com.chy.lamia.entity.Var;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;

import java.util.Map;

public class TreeClassConstruction implements IClassConstruction {


    private final JavacElements elementUtils;
    private final JavacTrees trees;
    private final JCTree jcTree;

    //实例属性的容器
    private final Map<String, Var> instantVar;




    public TreeClassConstruction(JavacElements elementUtils, JavacTrees trees, JCTree jcTree) {
        this.elementUtils = elementUtils;
        this.trees = trees;
        this.jcTree = jcTree;

        VarCollect varCollect = new VarCollect();
        jcTree.accept(varCollect);
        this.instantVar = varCollect.getData();

    }




}
