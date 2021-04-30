package com.chy.lamia.element.type.processor;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.TypeProcessorResult;
import com.sun.tools.javac.tree.JCTree;

public interface ITypeProcessor {

    String[] indexs();


    /**
     * 获取拆箱后的类型
     *
     * @param parameterType
     * @return
     */
    ParameterType unboxingType(ParameterType parameterType);


    /**
     * 对于代码来说应该如何去拆箱
     *
     * @param expression
     * @return
     */
    JCTree.JCExpression unboxingExpression(JCTree.JCExpression expression);


    /**
     * 如何去装箱
     *
     * @param expression
     * @return
     */
    JCTree.JCExpression autoboxingExpression(JCTree.JCExpression expression);
}
