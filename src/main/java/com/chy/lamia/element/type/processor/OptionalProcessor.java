package com.chy.lamia.element.type.processor;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.UnpackResult;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OptionalProcessor implements ITypeProcessor {

    @Override
    public String[] indexs() {
        return new String[]{Optional.class.getName()};
    }

    @Override
    public UnpackResult unpack(ParameterType parameterType, JCTree.JCExpression expression) {
        JCUtils jcUtils = JCUtils.instance;
        List<JCTree.JCExpression> list = new ArrayList();
        list.add(jcUtils.getNullExpression());

        //生成 语句 optional.orElse(null);
        JCTree.JCExpression newExpression = jcUtils.execMethod(expression, "orElse", list).getExpression();

        //获取 optional 里的泛型
        ParameterType genericByOption = getGenericByOption(parameterType);

        ParameterType newParameterType = new ParameterType(parameterType.getName(), genericByOption);

        UnpackResult result = new UnpackResult(newExpression, newParameterType);
        return result;
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
