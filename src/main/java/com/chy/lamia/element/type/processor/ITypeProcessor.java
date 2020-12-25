package com.chy.lamia.element.type.processor;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.UnpackResult;
import com.sun.tools.javac.tree.JCTree;

public interface ITypeProcessor {

    String[] indexs();

    UnpackResult unpack(ParameterType parameterType, JCTree.JCExpression expression);
}
