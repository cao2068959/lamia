package com.chy.lamia.element.assemble;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

public interface IAssembleFactory {

    /**
     * 添加原料
     *
     * @param parameterType
     * @param expression
     * @param priority
     */
    void addMaterial(ParameterType parameterType, JCTree.JCExpression expression, Integer priority);
}
