package com.chy.lamia.element.type.processor;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OptionalProcessor implements ITypeProcessor {


    JCUtils jcUtils;

    public OptionalProcessor() {
        this.jcUtils = JCUtils.instance;
    }

    @Override
    public String[] indexs() {
        return new String[]{Optional.class.getName()};
    }

    @Override
    public ParameterType unboxingType(ParameterType parameterType) {
        //获取 optional 里的泛型
        ParameterType genericByOption = getGenericByOption(parameterType);
        return new ParameterType(parameterType.getName(), genericByOption);
    }

    @Override
    public JCTree.JCExpression unboxingExpression(JCTree.JCExpression expression) {
        List<JCTree.JCExpression> list = new ArrayList();
        list.add(jcUtils.getNullExpression());
        //生成 语句 optional.orElse(null);
        return jcUtils.execMethod(expression, "orElse", list).getExpression();
    }


    @Override
    public JCTree.JCExpression autoboxingExpression(JCTree.JCExpression expression) {
        List<JCTree.JCExpression> params = List.of(expression);
        return jcUtils.execMethod("java.util.Optional", "ofNullable", params).getExpression();
    }

    /**
     * 从 Optional 中获取泛型
     *
     * @param data
     * @return
     */
    private ParameterType getGenericByOption(ParameterType data) {
        List<ParameterType> generic = data.getGeneric();
        if (generic == null || generic.size() != 1) {
            return null;
        }
        return generic.get(0);
    }

}
