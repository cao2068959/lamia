package com.chy.lamia.entity;


import com.chy.lamia.element.type.ExpressionFunction;
import com.sun.tools.javac.tree.JCTree;

public class TypeProcessorResult {


    ParameterType parameterType;
    ParameterType nextParameterType;
    ExpressionFunction unboxingFun;
    ExpressionFunction autoboxingFun;

    public TypeProcessorResult(ParameterType parameterType, ParameterType nextParameterType, ExpressionFunction unboxingFun, ExpressionFunction autoboxingFun) {
        this.parameterType = parameterType;
        this.nextParameterType = nextParameterType;
        this.unboxingFun = unboxingFun;
        this.autoboxingFun = autoboxingFun;
    }

    public ExpressionFunction getUnboxingFun() {
        return unboxingFun;
    }

    public ExpressionFunction getAutoboxingFun() {
        return autoboxingFun;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public ParameterType getNextParameterType() {
        return nextParameterType;
    }
}
