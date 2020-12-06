package com.chy.lamia.entity;

import com.sun.tools.javac.tree.JCTree;

public class Expression {

    JCTree.JCExpression expression;


    public Expression(JCTree.JCExpression expression) {
        this.expression = expression;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }
}
