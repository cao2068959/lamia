package com.chy.lamia.element.assemble.map;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

import java.util.Optional;

public class MaterialCache {

    ParameterType parameterType;
    Optional<ParameterType> topParentParameterType;
    JCTree.JCExpression expression;
    Integer priority;


    public MaterialCache(ParameterType parameterType, Optional<ParameterType> topParentParameterType, JCTree.JCExpression expression, Integer priority) {
        this.parameterType = parameterType;
        this.topParentParameterType = topParentParameterType;
        this.expression = expression;
        this.priority = priority;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public Optional<ParameterType> getTopParentParameterType() {
        return topParentParameterType;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }

    public Integer getPriority() {
        return priority;
    }
}
