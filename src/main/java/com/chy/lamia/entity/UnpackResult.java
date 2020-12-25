package com.chy.lamia.entity;


import com.sun.tools.javac.tree.JCTree;

public class UnpackResult {

    JCTree.JCExpression expression;
    ParameterType parameterType;


    public UnpackResult(JCTree.JCExpression expression, ParameterType parameterType) {
        this.expression = expression;
        this.parameterType = parameterType;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }
}
