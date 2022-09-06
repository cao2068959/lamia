package com.chy.lamia.element.boxing;


import com.sun.tools.javac.tree.JCTree;

public interface ExpressionFunction {

    JCTree.JCExpression getExpression(JCTree.JCExpression expression);

}
