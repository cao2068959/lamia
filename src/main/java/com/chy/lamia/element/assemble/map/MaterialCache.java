package com.chy.lamia.element.assemble.map;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

public class MaterialCache {

    ParameterType parameterType;
    JCTree.JCExpression expression;
    Integer priority;

    public MaterialCache(ParameterType parameterType, JCTree.JCExpression expression, Integer priority) {
        this.parameterType = parameterType;
        this.expression = expression;
        this.priority = priority;
    }


    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }

    public void setExpression(JCTree.JCExpression expression) {
        this.expression = expression;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
