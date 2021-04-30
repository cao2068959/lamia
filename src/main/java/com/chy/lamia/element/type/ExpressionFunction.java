package com.chy.lamia.element.type;


import com.sun.tools.javac.tree.JCTree;

public interface ExpressionFunction {

    JCTree.JCExpression getExpression(JCTree.JCExpression expression);

}
