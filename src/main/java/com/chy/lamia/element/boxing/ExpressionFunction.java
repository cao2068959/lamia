package com.chy.lamia.element.boxing;


import com.chy.lamia.entity.ExpressionWrapper;
import com.sun.tools.javac.tree.JCTree;

public interface ExpressionFunction {

    ExpressionWrapper getExpression(JCTree.JCExpression expression);

}
