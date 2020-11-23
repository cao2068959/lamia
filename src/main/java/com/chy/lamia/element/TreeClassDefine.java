package com.chy.lamia.element;

import com.chy.lamia.element.tree.ConstructorCollect;
import com.chy.lamia.element.tree.GetSetCollect;
import com.chy.lamia.element.tree.VarCollect;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.Var;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;
import java.util.Map;

public class TreeClassDefine implements IClassDefine {


    private final JavacElements elementUtils;
    private final JavacTrees trees;
    private final JCTree jcTree;

    /**
     * 实例中的所有属性
     */
    private final Map<String, Var> instantVars;

    /**
     * 实例中所有的 getter
     * key getter方法对应的 字段的名称
     */
    private final Map<String, Getter> instantGetters;
    private final Map<String, Setter> instantSetters;


    /**
     * 实例中所有的构造器
     */
    private final List<Constructor> constructors;


    public TreeClassDefine(JavacElements elementUtils, JavacTrees trees, JCTree jcTree) {
        this.elementUtils = elementUtils;
        this.trees = trees;
        this.jcTree = jcTree;

        VarCollect varCollect = new VarCollect();
        jcTree.accept(varCollect);
        this.instantVars = varCollect.getData();

        GetSetCollect getterCollect = new GetSetCollect();
        jcTree.accept(getterCollect);
        this.instantGetters = getterCollect.getGetterData();
        this.instantSetters = getterCollect.getSetterData();

        ConstructorCollect constructorCollect = new ConstructorCollect();
        jcTree.accept(constructorCollect);
        constructors = constructorCollect.getData();

    }


    public AssembleFactory getAssembleFactory() {
        return new AssembleFactory(constructors, instantSetters);
    }

    @Override
    public Map<String, Var> getInstantVars() {
        return instantVars;
    }


}
