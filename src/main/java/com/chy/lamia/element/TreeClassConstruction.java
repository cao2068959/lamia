package com.chy.lamia.element;

import com.chy.lamia.element.tree.GetterCollect;
import com.chy.lamia.element.tree.VarCollect;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Var;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;

import java.util.Map;

public class TreeClassConstruction implements IClassConstruction {


    private final JavacElements elementUtils;
    private final JavacTrees trees;
    private final JCTree jcTree;

    /**
     * 实例属性的容器
     */
    private final Map<String, Var> instantVars;

    /**
     * 实例中所有的 getter
     * key getter方法对应的 字段的名称
     */
    private final Map<String, Getter> instantGetters;




    public TreeClassConstruction(JavacElements elementUtils, JavacTrees trees, JCTree jcTree) {
        this.elementUtils = elementUtils;
        this.trees = trees;
        this.jcTree = jcTree;

        VarCollect varCollect = new VarCollect();
        jcTree.accept(varCollect);
        this.instantVars = varCollect.getData();

        GetterCollect getterCollect = new GetterCollect();
        jcTree.accept(getterCollect);
        this.instantGetters = getterCollect.getData();

    }




}
