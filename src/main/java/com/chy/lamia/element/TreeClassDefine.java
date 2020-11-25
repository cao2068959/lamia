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

/**
 * 通过 直接解析的 java 文件来获取的 class信息
 * 里面的 属性都是 懒加载的形式获取，注意的是 是非线程安全
 */
public class TreeClassDefine implements IClassDefine {


    private final JavacElements elementUtils;
    private final JavacTrees trees;
    private final JCTree jcTree;

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


    public TreeClassDefine(JavacElements elementUtils, JavacTrees trees, JCTree jcTree) {
        this.elementUtils = elementUtils;
        this.trees = trees;
        this.jcTree = jcTree;
    }

    @Override
    public AssembleFactory getAssembleFactory() {
        return new AssembleFactory(getConstructors(), getInstantSetters());
    }

    @Override
    public Map<String, Var> getInstantVars() {
        if (instantVars == null) {
            VarCollect varCollect = new VarCollect();
            jcTree.accept(varCollect);
            this.instantVars = varCollect.getData();
        }
        return instantVars;
    }

    @Override
    public Map<String, Getter> getInstantGetters() {

        if (instantGetters == null) {
            GetSetCollect getterCollect = new GetSetCollect();
            jcTree.accept(getterCollect);
            this.instantGetters = getterCollect.getGetterData();
            this.instantSetters = getterCollect.getSetterData();
        }

        return instantGetters;
    }

    @Override
    public Map<String, Setter> getInstantSetters() {
        if (instantSetters == null) {
            GetSetCollect getterCollect = new GetSetCollect();
            jcTree.accept(getterCollect);
            this.instantGetters = getterCollect.getGetterData();
            this.instantSetters = getterCollect.getSetterData();
        }
        return instantSetters;
    }

    @Override
    public List<Constructor> getConstructors() {
        if (constructors == null) {
            ConstructorCollect constructorCollect = new ConstructorCollect();
            jcTree.accept(constructorCollect);
            constructors = constructorCollect.getData();
        }
        return constructors;
    }
}
