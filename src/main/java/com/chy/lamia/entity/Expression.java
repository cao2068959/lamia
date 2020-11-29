package com.chy.lamia.entity;

import com.sun.tools.javac.tree.JCTree;

public class Expression {

    JCTree.JCExpression expression;
    String typePath;


    public Expression(JCTree.JCExpression expression, String typePath) {
        this.expression = expression;
        this.typePath = typePath;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }

    public String getTypePath() {
        return typePath;
    }
}
