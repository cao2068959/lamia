package com.chy.lamia.entity;


import com.sun.tools.javac.tree.JCTree;

public class PriorityExpression {
    JCTree.JCExpression expression;
    Integer priority = -1;

    public PriorityExpression(JCTree.JCExpression expression, Integer priority) {
        this.expression = expression;
        this.priority = priority;
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
