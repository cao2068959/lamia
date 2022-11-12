package com.chy.lamia.element.resolver;

import com.chy.lamia.element.IClassDefine;
import com.chy.lamia.element.tree.ConstructorCollect;
import com.chy.lamia.element.tree.GetSetCollect;
import com.chy.lamia.element.tree.VarCollect;
import com.chy.lamia.entity.*;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.visitor.SimpleMethodCollect;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;
import java.util.Map;

/**
 * 通过 直接解析的 java 文件来获取的 class信息
 * 里面的 属性都是 懒加载的形式获取，注意的是 是非线程安全
 */
public class TreeClassDefine implements IClassDefine {

    JCUtils jcUtils;
    JCTree jcTree;

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

    private List<SimpleMethod> allMethod;

    public TreeClassDefine(JCUtils jcUtils, JCTree jcTree) {
        this.jcUtils = jcUtils;
        this.jcTree = jcTree;
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
            initGetterAndSetter();
        }

        return instantGetters;
    }

    @Override
    public Map<String, Setter> getInstantSetters() {
        if (instantSetters == null) {
            initGetterAndSetter();
        }
        return instantSetters;
    }

    private void initGetterAndSetter() {
        GetSetCollect getterCollect = new GetSetCollect();
        jcTree.accept(getterCollect);

        TypeDefinition parentType = getterCollect.getParentType();
        this.instantGetters = getterCollect.getGetterData();
        this.instantSetters = getterCollect.getSetterData();

        //看是否有父类,如果有那么还要对父类解析
        if (parentType != null) {
            TypeResolver parentClassElement = TypeResolver.getTypeResolver(parentType);
            instantGetters.putAll(parentClassElement.getInstantGetters());
            instantSetters.putAll(parentClassElement.getInstantSetters());
        }
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

    @Override
    public List<SimpleMethod> getAllMethod() {
        if (allMethod == null) {
            SimpleMethodCollect simpleMethodCollect = new SimpleMethodCollect();
            jcTree.accept(simpleMethodCollect);
            allMethod = simpleMethodCollect.getData();
        }
        return allMethod;
    }
}
