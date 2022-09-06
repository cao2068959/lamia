package com.chy.lamia.element.boxing.processor;


import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Optional 的 装/拆箱 处理器
 *
 * @author bignosecat
 */
public class OptionalBoxingProcessor implements ITypeBoxingProcessor {


    JCUtils jcUtils;

    public OptionalBoxingProcessor() {
        this.jcUtils = JCUtils.instance;
    }


    @Override
    public String handleClassName() {
        return Optional.class.getTypeName();
    }

    @Override
    public TypeBoxingDefinition unboxing(TypeDefinition typeDefinition) {
        //获取 optional 里泛型的类型
        TypeDefinition unBoxingType = getGenericByOption(typeDefinition);
        if (unBoxingType == null) {
            return null;
        }
        // 把父包装类型 转成 TypeBoxingDefinition 以便可以方便的描述整个包装关系
        TypeBoxingDefinition parent = toTypeBoxingDefinition(typeDefinition);
        TypeBoxingDefinition children = toTypeBoxingDefinition(unBoxingType);
        parent.addChildrenBoxType(children);

        parent.unboxingExpression = this::unboxingExpression;
        children.boxingExpression = this::autoboxingExpression;

        return children;
    }

    public TypeBoxingDefinition toTypeBoxingDefinition(TypeDefinition typeDefinition) {
        if (typeDefinition instanceof TypeBoxingDefinition) {
            return (TypeBoxingDefinition) typeDefinition;
        }
        return new TypeBoxingDefinition(typeDefinition);
    }

    /**
     * 拆箱的表达式 这里使用 optional.orElse(null) 进行拆箱
     *
     * @param expression 哪一个变量进行拆箱
     * @return
     */
    private JCTree.JCExpression unboxingExpression(JCTree.JCExpression expression) {
        List<JCTree.JCExpression> list = new ArrayList();
        list.add(jcUtils.getNullExpression());
        //生成 语句 optional.orElse(null);
        return jcUtils.execMethod(expression, "orElse", list).getExpression();
    }


    private JCTree.JCExpression autoboxingExpression(JCTree.JCExpression expression) {
        List<JCTree.JCExpression> params = Lists.of(expression);
        return jcUtils.execMethod("java.util.Optional", "ofNullable", params).getExpression();
    }

    /**
     * 从 Optional 中获取泛型
     *
     * @param data
     * @return
     */
    private TypeDefinition getGenericByOption(TypeDefinition data) {
        List<TypeDefinition> generic = data.getGeneric();
        if (generic == null || generic.size() != 1) {
            return null;
        }
        return generic.get(0);
    }

}
